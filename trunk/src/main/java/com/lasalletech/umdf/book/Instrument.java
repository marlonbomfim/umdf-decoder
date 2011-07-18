package com.lasalletech.umdf.book;

public interface Instrument {
	public String getID();
	public String getSource();
	
	public OrderBook getBook();
	
	public String getSymbol();
	
	public String getExchange();
}
