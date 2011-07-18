package com.lasalletech.umdf.decoder;

import org.openfast.GroupValue;

import quickfix.FieldNotFound;
import quickfix.IncorrectTagValue;
import quickfix.UnsupportedMessageType;

public interface FastProcessor {
	public void process(GroupValue msg) throws UnsupportedMessageType, FieldNotFound, IncorrectTagValue;
}
