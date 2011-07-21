package com.lasalletech.market_data.fast.error;

public class UnsupportedMessageType extends Exception {
	private static final long serialVersionUID = 1L;
	public UnsupportedMessageType(String type) {
		super(type);
	}

}
