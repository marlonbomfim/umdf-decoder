package com.lasalletech.market_data.fast.error;

public class FieldNotFound extends Exception {
	private static final long serialVersionUID = 1L;
	public FieldNotFound(String fieldName) {
		super(fieldName);
	}
	public FieldNotFound(int fieldId) {
		super(String.valueOf(fieldId));
	}
}
