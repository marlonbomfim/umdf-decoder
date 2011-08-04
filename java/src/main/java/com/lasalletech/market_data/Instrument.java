package com.lasalletech.market_data;

public interface Instrument {
	public String getID();
	public String getSource();
	
	public String getSymbol();
	
	public String getExchange();
	
	public OrderBook getBook();
}
