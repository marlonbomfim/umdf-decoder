package com.lasalletech.umdf.decoder;

public class SinglePacketUmdfMessage implements UmdfMessage {
    private final UmdfPacket packet;

    public SinglePacketUmdfMessage(UmdfPacket packet) {
        this.packet = packet;
    }

    public boolean isComplete() {
        return true;
    }

    public byte[] getData() {
        byte[] data = new byte[packet.getMsgLength()];
        packet.readData(data, 0);
        return data;
    }

	@Override
	public long getMsgSeqNum() {
		return packet.getMsgSeqNum();
	}
}
