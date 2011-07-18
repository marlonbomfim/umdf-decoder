package com.lasalletech.umdf.decoder;

import java.io.IOException;
import java.net.DatagramPacket;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Vector;

public class ChunkPacketSource implements PacketSource {
	private Queue<byte[]> packets=new LinkedList<byte[]>();
	public ChunkPacketSource(Vector<String> inMsgs) throws IOException {
		for(byte[] cur:TestUtil.generateUmdfChunks(inMsgs, 2)) {
			packets.add(cur);
		}
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
