package com.lasalletech.market_data.fast;

import java.util.Calendar;

import org.openfast.GroupValue;

import com.lasalletech.market_data.OrderBook;
import com.lasalletech.market_data.OrderEntry;
import com.lasalletech.market_data.fast.error.FieldNotFound;

public class TestFastOrderEntry implements OrderEntry,
		Comparable<TestFastOrderEntry> {
	public TestFastOrderEntry(GroupValue info,TestFastOrderBook inBook)
			throws FieldNotFound {
		book=inBook;
		processFirst(info);
		update(info);
	}

	@Override
	public OrderBook getBook() {
		return book;
	}

	@Override
	public int getBookPos() {
		return pos;
	}

	@Override
	public String getBuyer() {
		return buyer;
	}

	@Override
	public Calendar getDate() {
		return timestamp;
	}

	@Override
	public String getID() {
		return id;
	}

	@Override
	public double getPrice() {
		return price;
	}

	@Override
	public double getQty() {
		return qty;
	}

	@Override
	public String getSeller() {
		return seller;
	}

	@Override
	public int compareTo(TestFastOrderEntry o) {
		if(pos>o.pos) return 1;
		else if(pos<o.pos) return -1;
		else return 0;
	}
	
	public boolean correctnessTest(TestFastOrderEntry other) {
		boolean equal=true;
		if(Math.abs(qty-other.qty)>0.00000001) {
			System.out.print("Qty "+qty+" != "+other.qty+"\n");
			equal=false;
		}
		if(Math.abs(price-other.price)>0.00000001) {
			System.out.print("Price "+price+" != "+other.price+"\n");
			equal=false;
		}
		if(!id.equals(other.id)) {
			System.out.print("ID "+id+" != "+other.id+"\n");
			equal=false;
		}
		if(!timestamp.equals(other.timestamp)) {
			System.out.print("Timestamp "+timestamp.getTimeInMillis()+" != "+other.timestamp.getTimeInMillis()+"\n");
			equal=false;
		}
		if(!buyer.equals(other.buyer)) {
			System.out.print("Buyer "+buyer+" != "+other.buyer+"\n");
			equal=false;
		}
		if(!seller.equals(other.seller)) {
			System.out.print("Seller "+seller+" != "+other.seller+"\n");
			equal=false;
		}
		if(pos!=other.pos) {
			System.out.print("Pos "+pos+" != "+other.pos+"\n");
			equal=false;
		}
		
		return equal;
	}
	
	private void processFirst(GroupValue grp) throws FieldNotFound {
		id=FastUtil.getString(grp, Fields.ORDERID);
		buyer="(N/A)"; seller="(N/A)";
	}
	
	public void update(GroupValue grp) throws FieldNotFound {
		if(grp.isDefined(Fields.MDENTRYPX)) {
			price=grp.getDouble(Fields.MDENTRYPX);
			book.log("Price = "+price+" ("+grp.getValue(Fields.MDENTRYPX)+")");
		}
		if(grp.isDefined(Fields.MDENTRYSIZE)) {
			qty=grp.getDouble(Fields.MDENTRYSIZE);
			book.log("Qty = "+qty+" ("+grp.getValue(Fields.MDENTRYSIZE).toString()+")");
		}
		
		if(grp.isDefined(Fields.MDENTRYDATE) && grp.isDefined(Fields.MDENTRYTIME)) {
			timestamp=DateUtil.bvmfToCal(grp.getLong(Fields.MDENTRYDATE),grp.getLong(Fields.MDENTRYTIME));
			book.log("Timestamp = "+timestamp.getTimeInMillis());
		}
		
		if(grp.isDefined(Fields.MDENTRYPOSITIONNO)) {
			pos=grp.getInt(Fields.MDENTRYPOSITIONNO);
			book.log("Pos = "+pos);
		}
		
		if(grp.isDefined(Fields.MDENTRYBUYER)) {
			buyer=grp.getString(Fields.MDENTRYBUYER);
			book.log("Buyer = "+buyer);
		}
		
		if(grp.isDefined(Fields.MDENTRYSELLER)) {
			seller=grp.getString(Fields.MDENTRYSELLER);
			book.log("Seller = "+seller);
		}
	}
	
	private double qty;
	
	private double price;
	
	private String id;
	
	private Calendar timestamp;
	
	private String buyer,seller;
	
	private int pos;
		
	private TestFastOrderBook book;

}
