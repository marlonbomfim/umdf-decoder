package com.lasalletech.umdf.decoder;

import java.io.IOException;

public interface ReplayStream {
	public byte[] request(long seqnum) throws IOException;
}
