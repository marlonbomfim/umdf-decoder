package com.lasalletech.market_data.fast;

import org.openfast.GroupValue;

import com.lasalletech.market_data.Instrument;
import com.lasalletech.market_data.OrderBook;
import com.lasalletech.market_data.fast.error.FieldNotFound;
import com.lasalletech.market_data.fast.error.InvalidFieldValue;
import com.lasalletech.market_data.fast.error.UnsupportedMessageType;

public class FastInstrument implements Instrument {
	
	private String debugName;

	public FastInstrument(GroupValue grp,FastInstrumentManager mgr)
			throws UnsupportedMessageType, FieldNotFound, InvalidFieldValue {
		id=FastUtil.getString(grp, Fields.SECURITYID);
		source=FastUtil.getString(grp, Fields.SECURITYIDSOURCE);
		exchange=FastUtil.getString(grp,Fields.SECURITYEXCHANGE, "(none)");
		debugName=id+":"+source;
		snapshotBook=new FastOrderBook(this,debugName+" Snapshot Book");
		incrementalBook=new FastOrderBook(this,debugName+" Incremental Book");
	}

	@Override
	public OrderBook getBook() {
		return incrementalBook;
	}
	public FastOrderBook getUmdfBook() {
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
			if(testLastSeqnum==grp.getLong(Fields.LASTMSGSEQNUMPROCESSED)) {
				System.out.println("[FastInstrument.process]: ("+debugName+") Duplicate snapshot received");
				//String thisMsg=FastUtil.fastMsgToFixString(grp, "FIXT.1.1", "000");
				
				//duplicateFixMsgs.println(testLastMsg);
				//duplicateFixMsgs.println(FastUtil.fastMsgToFixString(grp, "FIXT.1.1", "000"));
				//duplicateFixMsgs.flush();
				
				//System.out.println(testLastMsg+"\n"+thisMsg);
			} else {
				testLastSeqnum=grp.getLong(Fields.LASTMSGSEQNUMPROCESSED);
				testLastMsg=FastUtil.fastMsgToFixString(grp, "FIXT.1.1", "000");
			}
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
	
	private long testLastSeqnum=-1;
	private String testLastMsg=new String();

	private String id;
	private String source;
	private String exchange;
	private String symbol="";
	
	private FastOrderBook snapshotBook;
	private FastOrderBook incrementalBook;
}
