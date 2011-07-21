package com.lasalletech.market_data;

import java.util.List;

public interface OrderBook {
	public List<OrderEntry> getBids();
	public List<OrderEntry> getOffers();
	
	public OrderEntry topBid();
	public OrderEntry topOffer();
	
	public int bidCount();
	public int offerCount();
	
	public Instrument getInstrument();
}
