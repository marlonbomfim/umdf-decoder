package com.lasalletech.umdf.decoder;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.Socket;
import java.net.SocketAddress;

/*
 * Packet source that gets real data from a socket
 */
public class SocketPacketSource implements PacketSource {
	
	public SocketPacketSource(int inPort) throws Exception {
		socket=new DatagramSocket(inPort);
	}

	@Override
	public boolean receivePacket(DatagramPacket in) throws IOException {
		socket.receive(in);
		return true;
	}

	private DatagramSocket socket;
}
