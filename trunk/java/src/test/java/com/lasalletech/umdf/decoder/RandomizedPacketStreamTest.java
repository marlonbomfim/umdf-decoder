package com.lasalletech.umdf.decoder;

import java.util.LinkedList;
import java.util.Vector;
import java.util.concurrent.TimeUnit;

import junit.framework.TestCase;

public class RandomizedPacketStreamTest extends TestCase {
	private static final int NUM_PACKETS=10;
	public void test() throws Exception {
		Vector<String> src=TestUtil.generateMessages(NUM_PACKETS);
		LinkedList<String> out=new LinkedList<String>();
		
		UmdfUdpQueue q=new UmdfUdpQueue("Test");
		q.listen(new RandomizedPacketSource(src));
		
		while(out.size()<NUM_PACKETS) {
			UmdfMessage msg=q.read(100, TimeUnit.MILLISECONDS);
			assertTrue(msg!=null);
			out.add(new String(msg.getData()));
		}
		
		q.stop();
		
		// the output list will be out of order
		for(String cur:src) {
			assertTrue(out.contains(cur));
		}
	}
}
