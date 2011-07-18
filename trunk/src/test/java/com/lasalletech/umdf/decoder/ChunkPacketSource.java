package com.lasalletech.umdf.decoder;

import java.io.EOFException;
import java.io.IOException;
import java.net.DatagramPacket;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Vector;

public class ChunkPacketSource implements PacketSource {
	private Queue<DatagramPacket> packets=new LinkedList<DatagramPacket>();
	public ChunkPacketSource(Vector<String> inMsgs) throws IOException {
		for(byte[] cur:TestUtil.generateUmdfChunks(inMsgs, 2)) {
			packets.add(new DatagramPacket(cur,cur.length));
		}
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
