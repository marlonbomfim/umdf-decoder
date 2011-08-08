package com.lasalletech.umdf.decoder;

public class EmptyReplayStream implements ReplayStream {

	@Override
	public UmdfMessage request(long seqnum) {
		return null;
	}

}
