package com.lasalletech.umdf.decoder.fix_replay.fix_custom;

import quickfix.StringField;

public class ApplReqID extends StringField {
	private static final long serialVersionUID = 1L;
	public static final int FIELD=1346;
	public ApplReqID() { super(FIELD); }
	public ApplReqID(String in) { super(FIELD,in); }
}
