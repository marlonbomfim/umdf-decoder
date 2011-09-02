package com.lasalletech.umdf.decoder;

import java.net.DatagramPacket;

public class LossyPacketSource implements PacketSource {
	public LossyPacketSource(PacketSource inSrc,double lossRate) {
		src=inSrc;
		bound=lossRate;
	}

	@Override
	public boolean receivePacket(DatagramPacket out) throws Exception {
		/*if(first) {
			first=false;
			return src.receivePacket(out);
		} else {
			first=true;
			return false;
		}*/
		boolean keep=Math.random()>bound;
		//System.out.println("[LossyPacketSource.receivePacket]: kept packet: "+keep);
		return src.receivePacket(out) && keep;
	}
	private PacketSource src;
	private double bound;
}
