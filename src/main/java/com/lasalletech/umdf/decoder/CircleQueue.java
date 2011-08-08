package com.lasalletech.umdf.decoder;

public class CircleQueue {
    /*private final UmdfMessage[] queue;
    private int head = 0;
    private int tail = 0;
    private long nextSeqNum = -1;

    public CircleQueue(int capacity) {
        this.queue = new UmdfMessage[capacity];
    }

    public boolean contains(long msgSeqNum) {
        if (nextSeqNum == -1) {
            return false;
        }
        return queue[idx(msgSeqNum)] != null;
    }

    public synchronized void put(UmdfPacket umdfPacket) {
        if (nextSeqNum == -1) {
            nextSeqNum = umdfPacket.getMsgSeqNum();
        }
        if (umdfPacket.getMsgSeqNum() - nextSeqNum + tail < head) {
            System.out.println("Received old packet " + umdfPacket);
        }
        if (contains(umdfPacket.getMsgSeqNum())) {
            UmdfMessage msg = get(umdfPacket.getMsgSeqNum());
            ((ChunkedUmdfMessage)msg).add(umdfPacket);
        } else {
            UmdfMessage msg = UmdfMessages.umdfMessage(umdfPacket);
            int idx = idx(umdfPacket.getMsgSeqNum());
            queue[idx] = msg;
            if (idx > tail) {
                tail = idx;
                nextSeqNum = umdfPacket.getMsgSeqNum()+1;
            }
        }
    }

    public UmdfMessage next() throws InterruptedException {
        while (queue[head] == null) {
            Thread.sleep(15);
        }
        synchronized (this) {
            UmdfMessage m = queue[head];
            head++;
            queue[head] = null;
            if (head == tail) {
                head = 0;
                tail = 0;
            }
            return m;
        }
    }

    private UmdfMessage get(long msgSeqNum) {
        return queue[idx(msgSeqNum)];
    }

    private int idx(long msgSeqNum) {
        return (int) (msgSeqNum-nextSeqNum+tail);
    }*/
}
