package com.lasalletech.umdf.book;

import java.util.Calendar;

import org.openfast.GroupValue;


public class UmdfOrderEntry implements Comparable<UmdfOrderEntry>, OrderEntry {
	public UmdfOrderEntry(GroupValue info,UmdfOrderBook inBook) {
		updateFromRefresh(info);
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
	
	public void updateFromRefresh(GroupValue info) {
		price=info.getDouble(Fields.MDENTRYPX);
		qty=info.getDouble(Fields.MDENTRYSIZE);
		
		timestamp=DateUtil.bvmfToCal(info.getLong(Fields.MDENTRYDATE),
			info.getLong(Fields.MDENTRYTIME));
		
		id=info.getString(Fields.ORDERID);
		
		if(info.isDefined(Fields.MDENTRYBUYER)) {
			broker=info.getString(Fields.MDENTRYBUYER);
		} else if(info.isDefined(Fields.MDENTRYSELLER)) {
			broker=info.getString(Fields.MDENTRYSELLER);
		} else {
			broker="";
		}
		
		pos=info.getInt(Fields.MDENTRYPOSITIONNO);
	}
	
	//////////////////////////////////////////////////////////////////////////////
	
	@Override
	public int compareTo(UmdfOrderEntry o) {
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
		
	private UmdfOrderBook book;

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
