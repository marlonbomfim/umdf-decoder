package com.lasalletech.bvmf.umdf_console;

import java.io.ByteArrayInputStream;

import org.openfast.Context;
import org.openfast.Message;
import org.openfast.codec.FastDecoder;

import com.lasalletech.market_data.fast.FastMarketDataProcessor;
import com.lasalletech.market_data.fast.FastUtil;
import com.lasalletech.market_data.fast.Fields;
import com.lasalletech.market_data.fast.Messages;
import com.lasalletech.umdf.decoder.UmdfMessage;
import com.lasalletech.umdf.decoder.UmdfMessageAggregator;
import com.lasalletech.umdf.decoder.UmdfMessageListener;

public class BvmfSession implements UmdfMessageListener {
	private FastMarketDataProcessor link;
	Context context;
	public BvmfSession(FastMarketDataProcessor mgr, Context ctx) {
		link=mgr;
		context=ctx;
	}

	@Override
	public void onMessage(UmdfMessage message, UmdfMessageAggregator source) {
		FastDecoder decoder=new FastDecoder(context,new ByteArrayInputStream(message.getData()));
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
