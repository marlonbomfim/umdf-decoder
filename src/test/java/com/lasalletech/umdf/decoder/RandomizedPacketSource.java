package com.lasalletech.umdf.decoder;

import java.io.EOFException;
import java.io.IOException;
import java.net.DatagramPacket;
import java.util.LinkedList;
import java.util.Vector;

import edu.emory.mathcs.backport.java.util.Collections;

public class RandomizedPacketSource implements PacketSource {
	private LinkedList<DatagramPacket> packets=new LinkedList<DatagramPacket>();
	public RandomizedPacketSource(Vector<String> inMsgs) throws IOException {
		for(byte[] cur:TestUtil.generateUmdfChunks(inMsgs, 2)) {
			packets.add(new DatagramPacket(cur,cur.length));
		}
		Collections.shuffle(packets);
	}

	@Override
	public boolean receivePacket(DatagramPacket out) throws Exception {
		if(!packets.isEmpty()) {
			out=packets.remove();
			return true;
		}
		
		throw new EOFException();
	}
}
