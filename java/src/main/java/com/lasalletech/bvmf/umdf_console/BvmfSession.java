package com.lasalletech.bvmf.umdf_console;

import java.io.ByteArrayInputStream;

import org.openfast.Context;
import org.openfast.Message;
import org.openfast.codec.FastDecoder;

import com.lasalletech.market_data.fast.FastUtil;
import com.lasalletech.market_data.fast.Fields;
import com.lasalletech.market_data.fast.Messages;
import com.lasalletech.market_data.fast.FastInstrumentManager;
import com.lasalletech.umdf.decoder.UmdfMessageAggregator;
import com.lasalletech.umdf.decoder.UmdfMessageListener;

public class BvmfSession implements UmdfMessageListener {
	private FastInstrumentManager link;
	private Context context;
	public BvmfSession(FastInstrumentManager mgr, Context ctx) {
		link=mgr;
		context=ctx;
	}

	@Override
	public void onMessage(byte[] message, UmdfMessageAggregator source) {
		Context ctx = new Context();
		ctx.setTemplateRegistry(context.getTemplateRegistry());
		FastDecoder decoder=new FastDecoder(ctx,new ByteArrayInputStream(message));
		Message msg=decoder.readMessage();
		
		try {
		
			// deal with session-layer messages
			String type=FastUtil.getString(msg, Fields.MSGTYPE);
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
