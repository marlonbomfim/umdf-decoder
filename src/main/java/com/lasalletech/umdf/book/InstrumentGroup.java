package com.lasalletech.umdf.book;

import java.util.Collection;
import java.util.LinkedList;

public class InstrumentGroup {
	
	public String getName() { return id; }
	
	public int getStatus() { return status; }
	public String getStatusStr() {
		switch(status) {
		case 2: return "Trading halt (Pause)";
		case 4: return "No-open (Close)";
		case 17: return "Ready to trade (Open)";
		case 18: return "Not available (Pre-close)";
		case 21: return "Pre-open";
		default: return "?";
		}
	}
	
	public void setStatus(int in) { status=in; }
	
	public static InstrumentGroup find(String name) {
		for(InstrumentGroup cur:groups) {
			if(cur.id.equals(name)) return cur;
		}
		
		InstrumentGroup grp=new InstrumentGroup(name);
		groups.add(grp);
		return grp;
	}
	
	private InstrumentGroup(String inName) {
		id=inName;
	}
	
	private String id;
	private int status=-1;
	
	private static Collection<InstrumentGroup> groups=new LinkedList<InstrumentGroup>();
}
