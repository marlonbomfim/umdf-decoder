package com.lasalletech.umdf.decoder.fix_replay.fix_custom;

import quickfix.IntField;

public class ApplBeginSeqNum extends IntField {
	private static final long serialVersionUID = 1L;
	public static final int FIELD=1182;
	public ApplBeginSeqNum() { super(FIELD); }
	public ApplBeginSeqNum(int in) { super(FIELD,in); }
}
