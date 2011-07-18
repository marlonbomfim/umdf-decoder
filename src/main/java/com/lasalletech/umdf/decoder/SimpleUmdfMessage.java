package com.lasalletech.umdf.decoder;

public class SimpleUmdfMessage implements UmdfMessage {
    private final byte[] data;

    public SimpleUmdfMessage(byte[] data) {
        this.data = data;
    }

    @Override
    public byte[] getData() {
        return data;
    }

    @Override
    public boolean isComplete() {
        return true;
    }

	@Override
	public long getMsgSeqNum() {
		return -1;
	}
}
