package com.lasalletech.market_data;

public interface Instrument {
	public String getID();
	public String getSource();
	
	public OrderBook getBook();
	
	public String getSymbol();
	
	public String getExchange();
}
