package com.lasalletech.umdf.decoder;

public class EmptyReplayStream implements ReplayStream {

	@Override
	public void request(long seqnum) throws Exception {
	}

}
