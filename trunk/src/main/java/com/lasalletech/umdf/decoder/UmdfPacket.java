package com.lasalletech.umdf.decoder;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.DatagramPacket;

public class UmdfPacket {
    private final long msgSeqNum;
    private final int noChunks;
    private final int curChunk;
    private final int msgLength;
    //private final ByteArrayInputStream byteIn;
    private final byte[] msgData;

    public UmdfPacket(DatagramPacket packet) throws IOException {
        ByteArrayInputStream byteIn = new ByteArrayInputStream(packet.getData(), 0, packet.getLength());
        DataInputStream dataIn = new DataInputStream(byteIn);
        this.msgSeqNum = dataIn.readInt() & 0xffffffff;
        this.noChunks = dataIn.readShort() & 0xffff;
        this.curChunk = dataIn.readShort() & 0xffff;
        this.msgLength = dataIn.readShort() & 0xffff;
        //this.byteIn = byteIn;
        msgData=new byte[msgLength];
        byteIn.read(this.msgData,0,msgLength);
    }
    
    public UmdfPacket(byte[] inArray,int offset,int length) throws IOException {
    	ByteArrayInputStream byteIn=new ByteArrayInputStream(inArray,offset,length);
    	DataInputStream dataIn=new DataInputStream(byteIn);
    	this.msgSeqNum = dataIn.readInt() & 0xffffffff;
        this.noChunks = dataIn.readShort() & 0xffff;
        this.curChunk = dataIn.readShort() & 0xffff;
        this.msgLength = dataIn.readShort() & 0xffff;
        msgData=new byte[msgLength];
        byteIn.read(this.msgData,0,msgLength);
    }

    public int getCurChunk() {
        return curChunk;
    }

    public int getMsgLength() {
        return msgLength;
    }

    public long getMsgSeqNum() {
        return msgSeqNum;
    }

    public int getNoChunks() {
        return noChunks;
    }

    public int readData(byte[] data, int offset) {
        //return byteIn.read(data, offset, msgLength);
    	ByteArrayInputStream b=new ByteArrayInputStream(msgData,0,msgLength);
    	return b.read(data,offset,msgLength);
    }

    @Override
    public String toString() {
        return "#" + msgSeqNum + " [" + curChunk + "/" + noChunks + "] len=" + msgLength;
    }
}
