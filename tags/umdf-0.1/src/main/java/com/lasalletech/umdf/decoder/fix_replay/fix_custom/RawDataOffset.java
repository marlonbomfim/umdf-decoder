package com.lasalletech.umdf.decoder.fix_replay.fix_custom;

import quickfix.IntField;

public class RawDataOffset extends IntField {
	private static final long serialVersionUID = 1L;
	public static final int FIELD=10055;
	public RawDataOffset() { super(FIELD); }
	public RawDataOffset(int in) { super(FIELD,in); }
}
