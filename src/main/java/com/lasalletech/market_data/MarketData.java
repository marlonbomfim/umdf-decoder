package com.lasalletech.market_data;

import java.util.Collection;

public interface MarketData {
	public Instrument getInstrument(String id, String src);
	public Instrument getInstrumentBySymbol(String sym);
	
	public Collection<Instrument> getAllInstruments();
	public int getNumInstruments();
	
	//public OrderBook getBook(String id,String src);
	//public OrderBook getBook(Instrument inst);
	//public OrderBook getBookBySymbol(String sym);
}
