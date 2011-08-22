package com.lasalletech.umdf.decoder;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Vector;
import java.util.concurrent.TimeUnit;

import junit.framework.TestCase;

public class ChunkPacketStreamTest extends TestCase {
	private static final int NUM_PACKETS=10;
	public void test() throws Exception {
		Vector<String> src=TestUtil.generateMessages(NUM_PACKETS);
		Queue<String> out=new LinkedList<String>();
		
		UmdfUdpQueue q=new UmdfUdpQueue("Test");
		q.listen(new ChunkPacketSource(src));
		
		while(out.size()<NUM_PACKETS) {
			UmdfMessage msg=q.read(100, TimeUnit.MILLISECONDS);
			assertTrue(msg!=null);
			out.add(new String(msg.getData()));
		}
		
		q.stop();
		
		for(int i=0;i<NUM_PACKETS;++i) {
			String s=out.poll();
			assertTrue(s!=null);
			assertTrue(s.equals(src.get(i)));
		}
	}
}
