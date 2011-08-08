package com.lasalletech.umdf.decoder;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class MulticastPacketSource implements PacketSource {
	public MulticastPacketSource(String inGrp, int port) throws Exception {
		socket=new MulticastSocket(port);
		group=InetAddress.getByName(inGrp);
		socket.joinGroup(group);
	}

	@Override
	public boolean receivePacket(DatagramPacket out) throws Exception {
		socket.receive(out);
		return true;
	}
	
	public void stop() throws IOException {
		socket.leaveGroup(group);
	}
	
	private MulticastSocket socket;
	private InetAddress group;
}
