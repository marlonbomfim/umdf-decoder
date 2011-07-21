package com.lasalletech.market_data.fast;

import org.openfast.GroupValue;

import com.lasalletech.market_data.Instrument;
import com.lasalletech.market_data.OrderBook;
import com.lasalletech.market_data.fast.error.FieldNotFound;
import com.lasalletech.market_data.fast.error.InvalidFieldValue;
import com.lasalletech.market_data.fast.error.UnsupportedMessageType;

public class FastInstrument implements Instrument {

	public FastInstrument(GroupValue grp,FastMarketDataProcessor mgr)
			throws UnsupportedMessageType, FieldNotFound, InvalidFieldValue {
		book=new FastOrderBook(this);
		id=FastUtil.getString(grp, Fields.SECURITYID);
		source=FastUtil.getString(grp, Fields.SECURITYIDSOURCE);
		exchange=FastUtil.getString(grp,Fields.SECURITYEXCHANGE, "(none)");
		symbol=FastUtil.getString(grp, Fields.SYMBOL,"(none)");
		
		processUpdate(grp);
	}

	@Override
	public OrderBook getBook() {
		return book;
	}
	public FastOrderBook getUmdfBook() {
		return book;
	}

	@Override
	public String getExchange() {
		return exchange;
	}

	@Override
	public String getID() {
		return id;
	}

	@Override
	public String getSource() {
		return source;
	}

	@Override
	public String getSymbol() {
		return symbol;
	}
	
	public void processUpdate(GroupValue grp) throws UnsupportedMessageType,FieldNotFound,InvalidFieldValue {
		//TODO: 
	}

	public boolean process(GroupValue grp) throws UnsupportedMessageType, FieldNotFound, InvalidFieldValue {
		String type=FastUtil.getString(grp, Fields.MSGTYPE);
		if(type.equals(Messages.SECURITYSTATUS)) {
			//TODO: implement
		} else if(type.equals(Messages.MARKETDATASNAPSHOTFULLREFRESH)) {
			book.processSnapshot(grp);
		} else {
			throw new UnsupportedMessageType(type);
		}
		
		return true; // we end up processing everything we read in here
	}
	
	public boolean processIncrementals(GroupValue grp) throws UnsupportedMessageType, FieldNotFound, InvalidFieldValue {
		book.processIncremental(grp);
		return true; // we always either process or discard incrementals
	}

	private String id;
	private String source;
	private String exchange;
	private String symbol;
	
	private FastOrderBook book;
}
