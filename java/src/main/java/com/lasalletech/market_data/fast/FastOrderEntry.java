package com.lasalletech.market_data.fast;

import java.util.Calendar;

import org.openfast.GroupValue;

import com.lasalletech.market_data.OrderBook;
import com.lasalletech.market_data.OrderEntry;
import com.lasalletech.market_data.fast.error.FieldNotFound;


public class FastOrderEntry implements Comparable<FastOrderEntry>, OrderEntry {
	public FastOrderEntry(GroupValue info,FastOrderBook inBook) throws FieldNotFound {
		process(info);
		book=inBook;
	}
	
	public String getID() {
		return id;
	}
	
	public int getPos() {
		return pos;
	}
	
	public double getPrice() {
		return price;
	}
	public double getQty() {
		return qty;
	}
	
	public Calendar getDate() { return timestamp; }
	
	public void process(GroupValue grp) throws FieldNotFound {
		price=FastUtil.getDouble(grp, Fields.MDENTRYPX);
		qty=FastUtil.getDouble(grp, Fields.MDENTRYSIZE);
		
		timestamp=DateUtil.bvmfToCal(FastUtil.getLong(grp, Fields.MDENTRYDATE),FastUtil.getLong(grp, Fields.MDENTRYTIME));
		
		id=FastUtil.getString(grp, Fields.ORDERID);
		
		pos=FastUtil.getInt(grp, Fields.MDENTRYPOSITIONNO);
		
		if(grp.isDefined(Fields.MDENTRYBUYER)) {
			broker=grp.getString(Fields.MDENTRYBUYER);
		} else if(grp.isDefined(Fields.MDENTRYSELLER)) {
			broker=grp.getString(Fields.MDENTRYSELLER);
		} else {
			broker="(N/A)";
		}
	}
	
	//////////////////////////////////////////////////////////////////////////////
	
	@Override
	public int compareTo(FastOrderEntry o) {
		if(pos>o.pos) return 1;
		else if(pos<o.pos) return -1;
		else return 0;
	}
	
	private double qty;
	
	private double price;
	
	private String id;
	
	private Calendar timestamp;
	
	private String broker;
	
	private int pos;
		
	private FastOrderBook book;

	@Override
	public OrderBook getBook() {
		return book;
	}

	@Override
	public int getBookPos() {
		return pos;
	}

	@Override
	public String getBroker() {
		return broker;
	}
}
