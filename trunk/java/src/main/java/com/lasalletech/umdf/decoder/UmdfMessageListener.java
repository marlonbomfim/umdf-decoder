package com.lasalletech.umdf.decoder;

public interface UmdfMessageListener {
	public void onMessage(byte[] message, UmdfMessageAggregator source);
}
