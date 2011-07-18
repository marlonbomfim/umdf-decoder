package com.lasalletech.umdf.decoder;

import java.io.IOException;

public class ChunkedUmdfMessage implements UmdfMessage {
    final UmdfPacket[] packets;
    int packetCount;

    public ChunkedUmdfMessage(UmdfPacket packet) throws IOException {
    	if(packet.getNoChunks()<1 || packet.getCurChunk()<1) {
    		throw new IOException();
    	}
        this.packets = new UmdfPacket[packet.getNoChunks()];
        this.packets[packet.getCurChunk()-1] = packet;
        packetCount++;
    }

    public boolean isComplete() {
        return packetCount == packets.length;
    }

    public byte[] getData() {
        int length = 0;
        for (UmdfPacket p : packets) {
            length += p.getMsgLength();
        }
        byte[] data = new byte[length];
        int offset = 0;
        for (UmdfPacket p : packets) {
            offset += p.readData(data, offset);
        }
        return data;
    }

    public void add(UmdfPacket umdfPacket) throws IOException {
    	if(umdfPacket.getCurChunk()<1) throw new IOException();
        packets[umdfPacket.getCurChunk()-1] = umdfPacket;
        packetCount++;
    }

	@Override
	public long getMsgSeqNum() {
		return packets[0].getMsgSeqNum();
	}
}
