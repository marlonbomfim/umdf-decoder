package com.lasalletech.umdf.decoder;

import java.io.ByteArrayInputStream;
import java.util.HashMap;

import org.openfast.Context;
import org.openfast.Message;
import org.openfast.codec.FastDecoder;

import com.lasalletech.umdf.book.Fields;

import quickfix.FieldNotFound;
import quickfix.IncorrectTagValue;
import quickfix.UnsupportedMessageType;


/**
 * Takes async input messages from other sources via push(), and returns the packets in order with no
 * duplications via pop() or poll()
 *
 */
public class UmdfFastMessageAggregator {
	public UmdfFastMessageAggregator(long startSeqnum) {
		currentSeqnum=startSeqnum;
	}
	public UmdfFastMessageAggregator() {
		currentSeqnum=-1;
	}
	
	private UmdfUdpQueue udpQ;
	private ReplayStream replay;
	private Context ctx;
	private FastProcessor proc;
	private boolean running=false;
	
	public void stop() {
		if(running) {
			thread.interrupt();
		}
	}
	
	public void start(UmdfUdpQueue inQ,ReplayStream inReplay,Context inCtx,FastProcessor inProc) {
		stop();
		udpQ=inQ;
		replay=inReplay;
		ctx=inCtx;
		proc=inProc;
		final UmdfFastMessageAggregator me=this;
		thread=new Thread() {
			@Override
			public void run() {
				try {
					running=true;
					me.run();
				} catch(InterruptedException e) {
				} catch(Exception e) {
					e.printStackTrace();
				}
				running=false;
			}
		};
		thread.setDaemon(true);
		thread.start();
	}
	
	private void run() throws InterruptedException, UnsupportedMessageType, FieldNotFound, IncorrectTagValue {
		long lastRecvTime=System.currentTimeMillis();
		
		while(!Thread.interrupted()) {
			
			// try to process any backed-up messages
			while(backlog.containsKey(currentSeqnum)) {
				process(backlog.remove(currentSeqnum),ctx,proc);
				lastRecvTime=System.currentTimeMillis();
			}
			
			// deal with a new message
			UmdfMessage raw=udpQ.read();
			
			// set our seqnum if we haven't already
			if(currentSeqnum<0) currentSeqnum=raw.getMsgSeqNum();
			
			if(raw.getMsgSeqNum()==currentSeqnum) {
				process(raw,ctx,proc);
				lastRecvTime=System.currentTimeMillis();
			} else if(raw.getMsgSeqNum()>currentSeqnum) {
				// out-of-order packet, deal with it when we get there
				backlog.put(raw.getMsgSeqNum(), raw);
			} else if(raw.getMsgSeqNum()<currentSeqnum) {
				// dropping the packet because it is too old
			}
			
			if(System.currentTimeMillis()-lastRecvTime>RECV_TIMEOUT) {
				//TODO: timeout, get from replay stream
			}
		}
	}
	
	private void process(UmdfMessage raw,Context ctx,FastProcessor proc) throws UnsupportedMessageType, FieldNotFound, IncorrectTagValue {
		FastDecoder decoder=new FastDecoder(ctx, new ByteArrayInputStream(raw.getData()));
		Message msg=decoder.readMessage();
		
		String type=msg.getString(Fields.MSGTYPE);
		if(type.equals(Messages.SEQUENCERESET)) {
			currentSeqnum=msg.getLong(Fields.NEWSEQNO);
		} else if(type.equals(Messages.HEARTBEAT)) {
			currentSeqnum++;
		} else {
			currentSeqnum++;
			proc.process(msg);
		}
		
		//System.out.println(msg);
	}
	
	private long currentSeqnum;
	
	private static final int RECV_TIMEOUT=100;
	
	private HashMap<Long,UmdfMessage> backlog=new HashMap<Long,UmdfMessage>();
	
	private Thread thread;
}
