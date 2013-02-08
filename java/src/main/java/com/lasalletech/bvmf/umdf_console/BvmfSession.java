package com.lasalletech.bvmf.umdf_console;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.openfast.Context;
import org.openfast.Message;
import org.openfast.codec.FastDecoder;

import com.lasalletech.market_data.fast.FastInstrumentManager;
import com.lasalletech.market_data.fast.FastUtil;
import com.lasalletech.market_data.fast.Fields;
import com.lasalletech.market_data.fast.Messages;
import com.lasalletech.umdf.decoder.UmdfMessageAggregator;
import com.lasalletech.umdf.decoder.UmdfMessageListener;

public class BvmfSession implements UmdfMessageListener {
	private final FastInstrumentManager link;
	private final Context context;
	private final String debugName;
	private final PrintWriter log;
	public BvmfSession(FastInstrumentManager mgr, Context ctx,String myName) throws FileNotFoundException {
		link=mgr;
		context=ctx;
		debugName=myName;
		log=new PrintWriter(debugName+"-"+System.currentTimeMillis()+".messages.log");
	}

	@Override
	public void onMessage(byte[] message, UmdfMessageAggregator source) {
		Context ctx = new Context();
		ctx.setTemplateRegistry(context.getTemplateRegistry());
		FastDecoder decoder=new FastDecoder(ctx,new ByteArrayInputStream(message));
		Message msg=decoder.readMessage();
		
		SimpleDateFormat fmt=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
		
		log.println(fmt.format(Calendar.getInstance().getTime())+" "
					+FastUtil.fastMsgToFixString(msg, "FIXT.1.1", "000")
					+" ["+new String(message)+"]");
		
		try {
		
			// deal with session-layer messages
			String type=FastUtil.getStringById(msg, Fields.MSGTYPE);
			if(type.equals(Messages.SEQUENCERESET)) {
				source.reset(FastUtil.getLong(msg, Fields.NEWSEQNO));
			} else if(type.equals(Messages.HEARTBEAT)) {
				//TODO: currently heartbeats are pointless to us
			} else {
				link.onMessage(msg);
			}
			
		} catch(Exception e) {
			e.printStackTrace();
			source.stop();
		}
	}
}
