package com.lasalletech.umdf.decoder;

public interface UmdfMessage {
    public boolean isComplete();
    public byte[] getData();
    public long getMsgSeqNum();
}
