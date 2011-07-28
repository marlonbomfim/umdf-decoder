package com.lasalletech.umdf.decoder;

import java.io.IOException;
import java.net.DatagramPacket;
import java.util.LinkedList;
import java.util.Vector;

import java.util.Collections;

public class RandomizedPacketSource implements PacketSource {
	private LinkedList<byte[]> packets=new LinkedList<byte[]>();
	public RandomizedPacketSource(Vector<String> inMsgs) throws IOException {
		for(byte[] cur:TestUtil.generateUmdfChunks(inMsgs,2)) {
			packets.add(cur);
		}
		Collections.shuffle(packets);
	}

	@Override
	public boolean receivePacket(DatagramPacket out) throws Exception {
		if(!packets.isEmpty()) {
			out.setData(packets.remove());
			return true;
		}
		return false;
	}
}
