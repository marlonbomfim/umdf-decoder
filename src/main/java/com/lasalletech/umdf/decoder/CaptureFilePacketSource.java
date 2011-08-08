package com.lasalletech.umdf.decoder;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.DatagramPacket;

public class CaptureFilePacketSource implements PacketSource {
	private DataInputStream stream;
	public CaptureFilePacketSource(File file) throws FileNotFoundException {
		stream=new DataInputStream(new FileInputStream(file));
	}

	@Override
	public boolean receivePacket(DatagramPacket out) throws Exception {
		ByteArrayOutputStream outBuf=new ByteArrayOutputStream();
		DataOutputStream tmp=new DataOutputStream(outBuf);
		
		tmp.writeInt(stream.readInt());
		tmp.writeShort(stream.readShort());
		tmp.writeShort(stream.readShort());
		
		short len=stream.readShort();
		tmp.writeShort(len);
		
		byte[] msgBuf=new byte[len];
		stream.read(msgBuf, 0, len);
		tmp.write(msgBuf);
		
		out.setData(outBuf.toByteArray());
		
		return true;
	}

}
