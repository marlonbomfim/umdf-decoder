package com.lasalletech.umdf.examples.umdf_dump;

import java.io.ByteArrayInputStream;

import org.openfast.Context;
import org.openfast.Message;
import org.openfast.codec.FastDecoder;

import com.lasalletech.market_data.fast.FastUtil;
import com.lasalletech.market_data.fast.Fields;
import com.lasalletech.market_data.fast.Messages;
import com.lasalletech.umdf.decoder.UmdfMessageAggregator;
import com.lasalletech.umdf.decoder.UmdfMessageListener;

public class OutputListener implements UmdfMessageListener {
	private Context context;
	private String debugName;
	public OutputListener(Context ctx,String myName) {
		context=ctx;
		debugName=myName;
	}

	@Override
	public void onMessage(byte[] message, UmdfMessageAggregator source) {
		FastDecoder decoder=new FastDecoder(context,new ByteArrayInputStream(message));
		Message msg=decoder.readMessage();
		
		try {
		
			// deal with session-layer messages
			String type=FastUtil.getString(msg, Fields.MSGTYPE);
			if(type.equals(Messages.SEQUENCERESET)) {
				source.reset(FastUtil.getLong(msg, Fields.NEWSEQNO));
			} else if(type.equals(Messages.HEARTBEAT)) {
				//TODO: currently heartbeats are pointless to us
			} else {
				System.out.println(debugName+": "+msg.toString());
			}
			
		} catch(Exception e) {
			e.printStackTrace();
			source.stop();
		}
	}

}
