package com.lasalletech.market_data;

import java.util.Collection;

public interface InstrumentManager {
	public Instrument getInstrument(String id,String src);
	public Instrument getInstrumentBySymbol(String sym);
	
	public Collection<Instrument> getAllInstruments();
	
	public int getNumInstruments();
}
