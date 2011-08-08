package com.lasalletech.umdf.decoder.fix_replay.fix_custom;

import quickfix.IntField;

public class ApplReqType extends IntField {
	private static final long serialVersionUID = 1L;
	public static final int FIELD=1347;
	public ApplReqType() { super(FIELD); }
	public ApplReqType(int in) { super(FIELD,in); }
	
	public static final int RETRANSMISSION=0;
}
