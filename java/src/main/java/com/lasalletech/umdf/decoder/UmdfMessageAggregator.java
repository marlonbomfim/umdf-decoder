package com.lasalletech.umdf.decoder;

import java.io.IOException;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.LinkedList;

public class UmdfMessageAggregator {
	public UmdfMessageAggregator(long startSeqnum,String myName) {
		currentSeqnum=startSeqnum;
		debugName=myName;
	}
	public UmdfMessageAggregator(String myName) {
		currentSeqnum=-1;
		debugName=myName;
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
	
	public void start(UmdfUdpQueue inQ, ReplayStream replay, int replayTimeout) {
		stop();
		udpQ=inQ;
		replayStream=replay;
		replayRequestTimeout=replayTimeout;
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
	
	public void start(UmdfUdpQueue inQ,ReplayStream replay) {
		start(inQ,replay,DEFAULT_REPLAY_TIMEOUT);
	}
	
	private void processQueue() throws InterruptedException, IOException {
		long lastRecvTime=System.currentTimeMillis();
		UmdfMessage raw=null;
		
		while(!Thread.interrupted()) {
			
			// try to process any backed-up messages
			while(backlog.containsKey(currentSeqnum)) {
				process(backlog.remove(currentSeqnum));
				lastRecvTime=System.currentTimeMillis();
			}
			
			// deal with a new message
			raw=udpQ.read();
			
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
			if(timeDelta>replayRequestTimeout) {
				if(replayStream==null) {
					// packet has been dropped, just skip it
					System.out.println("[UmdfMessageAggregator.processQueue]: Queue "+debugName+": Recv timeout on message "+currentSeqnum+"; skipping");
					currentSeqnum++;
					lastRecvTime=System.currentTimeMillis();
				} else if((raw=replayStream.request(currentSeqnum))==null) {
					// the packet has been dropped and the replay request failed somehow
					System.out.println("[UmdfMessageAggregator.processQueue]: Queue "+debugName+": Recv timeout on message "+currentSeqnum+"; failed");
					throw new IOException();
				} else {
					process(raw);
					lastRecvTime=System.currentTimeMillis();
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
	
	private static final int DEFAULT_REPLAY_TIMEOUT=1000;
	private int replayRequestTimeout=0;
	private ReplayStream replayStream=null;
	
	private HashMap<Long,UmdfMessage> backlog=new HashMap<Long,UmdfMessage>();
	
	private Thread thread;
	
	private String debugName;
}
