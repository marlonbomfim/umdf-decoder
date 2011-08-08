package com.lasalletech.umdf.decoder;

import java.io.IOException;

public interface ReplayStream {
	public UmdfMessage request(long seqnum) throws IOException;
}
