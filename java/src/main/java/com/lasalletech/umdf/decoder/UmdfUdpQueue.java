package com.lasalletech.umdf.decoder;

import java.io.IOException;
import java.net.DatagramPacket;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

/**
 * Assembles UDP fragments into full UMDF messages
 *
 */
public class UmdfUdpQueue {
	public UmdfUdpQueue(String myName) {
		debugName=myName;
	}

	public void listen(final PacketSource src) {
		if(running) stop();
		
		thread=new Thread() {
			@Override
			public void run() {
				try {
					byte[] buf=new byte[MAX_UDPPACKET_SIZE];
					DatagramPacket p=new DatagramPacket(buf,buf.length);
					
					running=true;
					
					while(!Thread.interrupted()) {
						if(src.receivePacket(p)) {
							processPacket(p);
						}
						
						processQueue();
					}
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
	
	public void stop() {
		if(running) thread.interrupt();
	}
	
	public UmdfMessage read() throws InterruptedException {
		outgoingSemaphore.acquire();
		synchronized(outgoing) {
			return outgoing.remove();
		}
	}
	
	public UmdfMessage read(int timeout,TimeUnit unit) throws InterruptedException {
		if(outgoingSemaphore.tryAcquire(timeout,unit)) {
			synchronized(outgoing) {
				return outgoing.remove();
			}
		}

		return null;
	}
	
	public UmdfMessage poll() {
		if(outgoingSemaphore.tryAcquire()) {
			synchronized(outgoing) {
				return outgoing.remove();
			}
		}
		
		return null;
	}
	
	private void processPacket(DatagramPacket p) throws IOException {
		UmdfPacket packet=new UmdfPacket(p);
		long seqnum=packet.getMsgSeqNum();
		//System.out.println(debugName+": got packet with seqnum="+seqnum);
		
		if(incoming.containsKey(seqnum)) {
			UmdfMessage msg=incoming.get(seqnum);
			if(msg instanceof ChunkedUmdfMessage && !msg.isComplete()) {
				//System.out.println("Appended "+seqnum);
				((ChunkedUmdfMessage)(msg)).add(packet);
			} else {
				// the packet lies!
				throw new IOException();
			}
		} else {
			//System.out.println("Added "+seqnum);
			incoming.put(seqnum,UmdfMessages.umdfMessage(packet));
		}
	}
	
	private void processQueue() {
		Set<Long> keys=incoming.keySet();
		Iterator<Long> i=keys.iterator();
		while(i.hasNext()) {
			Long cur=i.next();
			UmdfMessage msg=incoming.get(cur);
			//System.out.println(msg.getMsgSeqNum());
			if(msg.isComplete()) {
				//System.out.println("Completed "+new String(msg.getData()));
				synchronized(outgoing) {
					outgoing.add(msg);
				}
				outgoingSemaphore.release();
				i.remove();
			}
		}
	}
	
	private Thread thread;
	private boolean running=false;
	
	private HashMap<Long,UmdfMessage> incoming=new HashMap<Long,UmdfMessage>();
	
	private Queue<UmdfMessage> outgoing=new LinkedList<UmdfMessage>();
	Semaphore outgoingSemaphore=new Semaphore(0);
	
	private static final int MAX_UDPPACKET_SIZE=1310;
	
	private String debugName;
}
