package com.lasalletech.umdf.book;

import java.util.Collection;

public interface InstrumentManager {
	public Instrument getInstrument(String id,String src);
	
	public Collection<Instrument> getAllInstruments();
	
	public int getNumInstruments();
}
