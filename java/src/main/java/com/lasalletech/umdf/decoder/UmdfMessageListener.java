package com.lasalletech.umdf.decoder;

public interface UmdfMessageListener {
	public void onMessage(UmdfMessage message, UmdfMessageAggregator source);
}
