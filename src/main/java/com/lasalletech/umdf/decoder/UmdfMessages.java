package com.lasalletech.umdf.decoder;

import java.io.IOException;

public class UmdfMessages {
    public static UmdfMessage umdfMessage(UmdfPacket packet) throws IOException {
        if (packet.getNoChunks() == 1) {
            return new SinglePacketUmdfMessage(packet);
        }
        return new ChunkedUmdfMessage(packet);
    }

    public static UmdfMessage replayMessage(byte[] data) {
        return new SimpleUmdfMessage(data);
    }
}
