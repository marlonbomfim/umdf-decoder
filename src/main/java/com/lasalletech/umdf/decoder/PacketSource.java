package com.lasalletech.umdf.decoder;

import java.net.DatagramPacket;

/*
 * Provides UDP packets
 * The idea is that you can either point this to a socket
 * connected to an actual data stream or have it point
 * to a set of fake data for testing purposes
 */
public interface PacketSource {
	/*
	 * Returns a single udp packet
	 * NOTE: this call may block until a packet arrives
	 * in - datagram packet to be filled
	 * returns - false if the receive did not generate data on this call
	 * 				This may still mean there is more data to get
	 */
	public boolean receivePacket(DatagramPacket out) throws Exception;
}
