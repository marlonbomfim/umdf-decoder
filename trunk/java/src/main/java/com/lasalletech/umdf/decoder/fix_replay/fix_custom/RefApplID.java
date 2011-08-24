package com.lasalletech.umdf.decoder.fix_replay.fix_custom;

import quickfix.StringField;

public class RefApplID extends StringField {
	private static final long serialVersionUID = 1L;
	public static final int FIELD=1355;
	public RefApplID() { super(FIELD); }
	public RefApplID(String in) { super(FIELD,in); }
}
