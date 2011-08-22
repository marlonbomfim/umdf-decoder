package com.lasalletech.market_data.fast;

import org.openfast.GroupValue;

import com.lasalletech.market_data.Instrument;
import com.lasalletech.market_data.OrderBook;
import com.lasalletech.market_data.fast.error.FieldNotFound;
import com.lasalletech.market_data.fast.error.InvalidFieldValue;
import com.lasalletech.market_data.fast.error.UnsupportedMessageType;

public class TestFastInstrument implements Instrument {
	
	public TestFastInstrument(GroupValue grp,TestFastInstrumentManager mgr)
			throws UnsupportedMessageType, FieldNotFound, InvalidFieldValue {
		snapshotBook=new TestFastOrderBook(this);
		incrementalBook=new TestFastOrderBook(this);
		id=FastUtil.getString(grp, Fields.SECURITYID);
		source=FastUtil.getString(grp, Fields.SECURITYIDSOURCE);
		exchange=FastUtil.getString(grp,Fields.SECURITYEXCHANGE, "(none)");
	}

	@Override
	public OrderBook getBook() {
		return incrementalBook;
	}
	public TestFastOrderBook getUmdfBook() {
		return incrementalBook;
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
		symbol=FastUtil.getString(grp, Fields.SYMBOL,"(none)");
		
		//TODO: implement
	}

	public void process(GroupValue grp) throws UnsupportedMessageType, FieldNotFound, InvalidFieldValue {
		String type=FastUtil.getString(grp, Fields.MSGTYPE);
		if(type.equals(Messages.SECURITYSTATUS)) {
			//TODO: implement
		} else if(type.equals(Messages.MARKETDATASNAPSHOTFULLREFRESH)) {
			snapshotBook.processSnapshot(grp);
			if(incrementalBook.getSeqnum()==-1) {
				incrementalBook.processSnapshot(grp);
			}
		} else {
			throw new UnsupportedMessageType(type);
		}
		
		correctnessTest();
	}
	
	public void processIncremental(GroupValue grp) throws UnsupportedMessageType, FieldNotFound, InvalidFieldValue {
		incrementalBook.processIncremental(grp);
		correctnessTest();
	}
	
	private void correctnessTest() {
		if(snapshotBook.getSeqnum()==incrementalBook.getSeqnum()) {
			if(!snapshotBook.correctnessTest(incrementalBook)) {
				System.out.println("==== Snapshot log ====");
				for(String cur:snapshotBook.getLog()) {
					System.out.println(cur);
				}
				System.out.println();
				System.out.println("==== Incremental log ====");
				for(String cur:incrementalBook.getLog()) {
					System.out.println(cur);
				}
				
				System.exit(1);
			}
		}
	}

	private String id;
	private String source;
	private String exchange;
	private String symbol="";
	
	private TestFastOrderBook snapshotBook;
	private TestFastOrderBook incrementalBook;

}
