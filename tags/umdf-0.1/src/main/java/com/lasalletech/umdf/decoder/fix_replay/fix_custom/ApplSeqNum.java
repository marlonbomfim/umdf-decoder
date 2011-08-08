package com.lasalletech.umdf.decoder.fix_replay.fix_custom;

import quickfix.StringField;

public class ApplSeqNum extends StringField {
	private static final long serialVersionUID = 1L;
	public static final int FIELD=1181;
	public ApplSeqNum() { super(FIELD); }
	public ApplSeqNum(String in) { super(FIELD,in); }
}

