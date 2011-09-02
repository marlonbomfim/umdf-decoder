package com.lasalletech.umdf.decoder;

public class EmptyReplayStream implements ReplayStream {

	@Override
	public byte[] request(long seqnum) {
		return null;
	}

}
