package com.lasalletech.umdf.decoder;

public interface ReplayStream {
	public void request(long seqnum) throws Exception;
}
