package com.lasalletech.umdf.decoder.fix_replay.fix_custom;

import quickfix.IntField;

public class ApplEndSeqNum extends IntField {
	private static final long serialVersionUID = 1L;
	public static final int FIELD=1183;
	public ApplEndSeqNum() { super(FIELD); }
	public ApplEndSeqNum(int in) { super(FIELD,in); }
}
