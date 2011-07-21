package com.lasalletech.market_data.fast.error;

public class InvalidFieldValue extends Exception {
	private static final long serialVersionUID = 1L;
	public InvalidFieldValue(String field,String value) {
		super(field+" '"+value+"'");
	}
	public InvalidFieldValue(int field,String value) {
		super(String.valueOf(field)+" '"+value+"'");
	}
}
