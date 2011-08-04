package com.lasalletech.umdf.decoder;

import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.LinkedList;

public class UmdfMessageAggregator {
	public UmdfMessageAggregator(long startSeqnum) {
		currentSeqnum=startSeqnum;
	}
	public UmdfMessageAggregator() {
		currentSeqnum=-1;
	}
	
	private UmdfUdpQueue udpQ;
	private boolean running=false;
	
	private LinkedList<UmdfMessageListener> hooks=new LinkedList<UmdfMessageListener>();
	
	public void addListener(UmdfMessageListener hook) {
		if(running) throw new ConcurrentModificationException();
		hooks.add(hook);
	}
	public void removeListener(UmdfMessageListener hook) {
		if(running) throw new ConcurrentModificationException();
		hooks.remove(hook);
	}
	
	//WARNING: this is /not/ thread safe! should only be called from inside UmdfMessageListener.onMessage()
	public void reset(long newSeqnum) {
		currentSeqnum=newSeqnum;
	}
	
	public void stop() {
		if(running) {
			thread.interrupt();
		}
	}
	
	public void start(UmdfUdpQueue inQ) {
		stop();
		udpQ=inQ;
		thread=new Thread() {
			@Override
			public void run() {
				try {
					running=true;
					processQueue();
				} catch(InterruptedException e) {
				} catch(Exception e) {
					e.printStackTrace();
				} finally {
					running=false;
				}
			}
		};
		thread.setDaemon(true);
		thread.start();
	}
	
	private void processQueue() throws InterruptedException {
		long lastRecvTime=System.currentTimeMillis();
		
		while(!Thread.interrupted()) {
			
			// try to process any backed-up messages
			while(backlog.containsKey(currentSeqnum)) {
				process(backlog.remove(currentSeqnum));
				lastRecvTime=System.currentTimeMillis();
			}
			
			// deal with a new message
			UmdfMessage raw=udpQ.read();
			
			// set our seqnum if we haven't already
			if(currentSeqnum<0) currentSeqnum=raw.getMsgSeqNum();
			
			if(raw.getMsgSeqNum()==currentSeqnum) {
				process(raw);
				lastRecvTime=System.currentTimeMillis();
			} else if(raw.getMsgSeqNum()>currentSeqnum) {
				// out-of-order packet, deal with it when we get there
				backlog.put(raw.getMsgSeqNum(), raw);
			} else {
				// dropping the packet because it is too old
			}
			
			long timeDelta=System.currentTimeMillis()-lastRecvTime;
			if(timeDelta>RECV_TIMEOUT) {
				//TODO: timeout, get from replay stream
				System.out.println("[UmdfMessageAggregator.processQueue]: Recv timeout: "+(((float)timeDelta)/1000.0));
				if(timeDelta>(RECV_TIMEOUT*10)) {
					System.exit(1);
				}
			}
		}
	}
	
	private void process(UmdfMessage raw) {
		
		// it is important to do this before any processing so that a processor function
		// can call reset() and expect it to do the right thing
		currentSeqnum++;
		
		for(UmdfMessageListener cur:hooks) {
			cur.onMessage(raw, this);
		}
	}
	
	private long currentSeqnum;
	
	private static final int RECV_TIMEOUT=1000;
	
	private HashMap<Long,UmdfMessage> backlog=new HashMap<Long,UmdfMessage>();
	
	private Thread thread;
}
