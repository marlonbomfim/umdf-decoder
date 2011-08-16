package com.lasalletech.market_data.fast;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.PriorityBlockingQueue;

import org.openfast.GroupValue;

import com.lasalletech.market_data.Instrument;
import com.lasalletech.market_data.OrderBook;
import com.lasalletech.market_data.OrderEntry;
import com.lasalletech.market_data.fast.error.FieldNotFound;
import com.lasalletech.market_data.fast.error.InvalidFieldValue;

public class TestFastOrderBook implements OrderBook {
	private static final String BID="0";
	private static final String OFFER="1";
	private static final String TRADE="2";
	private static final String INDEX_VALUE="3";
	private static final String OPENING_PRICE="4";
	private static final String CLOSING_PRICE="5";
	private static final String SETTLEMENT_PRICE="6";
	private static final String SESSION_HIGH_PRICE="7";
	private static final String SESSION_LOW_PRICE="8";
	private static final String SESSION_VWAP_PRICE="9";
	private static final String TRADE_IMBALANCE="A";
	private static final String TRADE_VOLUME="B";
	private static final String OPEN_INTEREST="C";
	private static final String TRADING_STATE="c";
	private static final String EMPTY_BOOK="J";
	private static final String PRICE_BAND="g";
	
	private static final String NEW="0";
	private static final String CHANGE="1";
	private static final String DELETE="2";
	private static final String DELETE_THRU="3";
	private static final String DELETE_FROM="4";
	private static final String OVERLAY="5";
	
	private TestFastInstrument instrument;
	
	private Queue<TestFastOrderEntry> bids=new PriorityBlockingQueue<TestFastOrderEntry>();
	private Queue<TestFastOrderEntry> offers=new PriorityBlockingQueue<TestFastOrderEntry>();
	
	private Queue<GroupValue> incrementalBacklog=new LinkedList<GroupValue>();
	
	private int lastRptSeqnum=-1;
	
	public TestFastOrderBook(TestFastInstrument inParent) {
		instrument=inParent;
	}
	
	public int getSeqnum() { return lastRptSeqnum; }
	
	public void processSnapshot(GroupValue msg) throws FieldNotFound, InvalidFieldValue {
		int seq=FastUtil.getInt(msg, Fields.LASTMSGSEQNUMPROCESSED);
		if(seq<lastRptSeqnum) return;
		lastRptSeqnum=seq;
		
		//flushLog();
		log(" -- refresh -- ");
		bids.clear(); offers.clear();
		for(GroupValue grp:msg.getSequence(Fields.MDENTRIES).getValues()) {
			processEntry(grp,NEW);
		}
		log(" -- end refresh -- ");
	}
	
	public void processIncremental(GroupValue grp) throws FieldNotFound, InvalidFieldValue {
		if(lastRptSeqnum==-1) {
			incrementalBacklog.add(grp);
			return;
		}
		
		while(!incrementalBacklog.isEmpty()) {
			processIncremental(incrementalBacklog.remove());
		}
		
		int seq=FastUtil.getInt(grp, Fields.RPTSEQ);
		if(seq<lastRptSeqnum) return;
		lastRptSeqnum=seq;
		
		processEntry(grp,FastUtil.getString(grp, Fields.MDUPDATEACTION));
	}
	
	private void processEntry(GroupValue grp,String op) throws FieldNotFound, InvalidFieldValue {
		String type=FastUtil.getString(grp, Fields.MDENTRYTYPE);

		//TODO: implement more of these
		if(type.equals(BID)) {
			processBid(grp,op);
		} else if(type.equals(OFFER)) {
			processOffer(grp,op);
		} else if(type.equals(TRADE)) {
		} else if(type.equals(INDEX_VALUE)) {
		} else if(type.equals(OPENING_PRICE)) {
		} else if(type.equals(CLOSING_PRICE)) {
		} else if(type.equals(SETTLEMENT_PRICE)) {
		} else if(type.equals(SESSION_HIGH_PRICE)) {
		} else if(type.equals(SESSION_LOW_PRICE)) {
		} else if(type.equals(SESSION_VWAP_PRICE)) {
		} else if(type.equals(TRADE_IMBALANCE)) {
		} else if(type.equals(TRADE_VOLUME)) {
		} else if(type.equals(OPEN_INTEREST)) {
		} else if(type.equals(TRADING_STATE)) {
		} else if(type.equals(EMPTY_BOOK)) {
		} else if(type.equals(PRICE_BAND)) {
		} else {
			throw new InvalidFieldValue(Fields.MDENTRYTYPE,type);
		}
	}
	
	private void processBid(GroupValue grp,String op) throws FieldNotFound, InvalidFieldValue {
		if(op.equals(NEW)) {
			log("New bid "+grp.getString(Fields.ORDERID));
			addOrder(bids,grp);
		} else if(op.equals(CHANGE)) {
			log("Updated bid "+grp.getString(Fields.ORDERID));
			updateOrder(bids,grp);
		} else if(op.equals(DELETE)) {
			deleteOrder(bids,grp);
			log("Deleted bid "+grp.getString(Fields.ORDERID));
		} else if(op.equals(DELETE_THRU)) {
			deleteOrderThru(bids,grp);
			log("Deleted bids thru "+grp.getString(Fields.ORDERID));
		} else if(op.equals(DELETE_FROM)) {
			deleteOrderFrom(bids,grp);
			log("Deleted bids from "+grp.getString(Fields.ORDERID));
		} else if(op.equals(OVERLAY)) {
			//TODO: implement
		}
	}
	
	private void processOffer(GroupValue grp,String op) throws FieldNotFound, InvalidFieldValue {
		if(op.equals(NEW)) {
			log("New offer "+grp.getString(Fields.ORDERID));
			addOrder(offers,grp);
		} else if(op.equals(CHANGE)) {
			log("Updated offer "+grp.getString(Fields.ORDERID));
			updateOrder(offers,grp);
		} else if(op.equals(DELETE)) {
			deleteOrder(offers,grp);
			log("Deleted offer "+grp.getString(Fields.ORDERID));
		} else if(op.equals(DELETE_THRU)) {
			deleteOrderThru(offers,grp);
			log("Deleted offers thru "+grp.getString(Fields.ORDERID));
		} else if(op.equals(DELETE_FROM)) {
			deleteOrderFrom(offers,grp);
			log("Deleted offers from "+grp.getString(Fields.ORDERID));
		} else if(op.equals(OVERLAY)) {
			//TODO: implement
		}
	}
	
	private void addOrder(Queue<TestFastOrderEntry> q,GroupValue grp) throws FieldNotFound {
		q.add(new TestFastOrderEntry(grp,this));
	}
	
	private void deleteOrderThru(Queue<TestFastOrderEntry> q,GroupValue grp) throws FieldNotFound {
		String id=FastUtil.getString(grp, Fields.ORDERID);
		Iterator<TestFastOrderEntry> iter=q.iterator();
		while(iter.hasNext()) {
			TestFastOrderEntry cur=iter.next();
			iter.remove();
			if(cur.getID().equals(id)) break;
		}
	}
	
	private void deleteOrderFrom(Queue<TestFastOrderEntry> q,GroupValue grp) throws FieldNotFound {
		String id=FastUtil.getString(grp, Fields.ORDERID);
		Iterator<TestFastOrderEntry> iter=q.iterator();
		boolean found=false;
		while(iter.hasNext()) {
			TestFastOrderEntry cur=iter.next();
			if(cur.getID().equals(id)) found=true;
			if(found) iter.remove();
		}
	}
	
	private void deleteOrder(Queue<TestFastOrderEntry> q,GroupValue grp) throws FieldNotFound, InvalidFieldValue {
		String id=FastUtil.getString(grp, Fields.ORDERID);
		TestFastOrderEntry found=null;
		for(TestFastOrderEntry cur:q) {
			if(cur.getID().equals(id)) {
				found=cur; break;
			}
		}
		if(found==null) throw new InvalidFieldValue(Fields.ORDERID,id);
		q.remove(found);
	}
	
	private void updateOrder(Queue<TestFastOrderEntry> q, GroupValue grp) throws FieldNotFound, InvalidFieldValue {
		String id=FastUtil.getString(grp, Fields.ORDERID);
		TestFastOrderEntry found=null;
		for(TestFastOrderEntry cur:q) {
			if(cur.getID().equals(id)) {
				found=cur; break;
			}
		}
		if(found==null) throw new InvalidFieldValue(Fields.ORDERID,id);
		q.remove(found);
		found.update(grp);
		q.add(found);
	}

	@Override
	public List<OrderEntry> getBids() {
		LinkedList<OrderEntry> out=new LinkedList<OrderEntry>();
		for(TestFastOrderEntry cur:bids) {
			out.add(cur);
		}
		return out;
	}

	@Override
	public List<OrderEntry> getOffers() {
		LinkedList<OrderEntry> out=new LinkedList<OrderEntry>();
		for(TestFastOrderEntry cur:offers) {
			out.add(cur);
		}
		return out;
	}

	@Override
	public TestFastOrderEntry topBid() {
		return bids.peek();
	}

	@Override
	public TestFastOrderEntry topOffer() {
		return offers.peek();
	}

	

	@Override
	public Instrument getInstrument() {
		return instrument;
	}

	@Override
	public int bidCount() {
		return bids.size();
	}

	@Override
	public int offerCount() {
		return offers.size();
	}
	
	public boolean correctnessTest(TestFastOrderBook other) {
		boolean equal=true;
		
		if(bidCount()!=other.bidCount()) {
			System.out.println("Bids "+bidCount()+" != "+other.bidCount());
			equal=false;
		}
		if(offerCount()!=other.offerCount()) {
			System.out.println("Offers "+offerCount()+" != "+other.offerCount());
			equal=false;
		}
		
		Iterator<TestFastOrderEntry> iter1,iter2;
		
		iter1=bids.iterator(); iter2=other.bids.iterator();
		while(iter1.hasNext()&&iter2.hasNext()) {
			if(!iter1.next().correctnessTest(iter2.next())) {
				equal=false;
			}
		}
		
		iter1=offers.iterator(); iter2=other.offers.iterator();
		while(iter1.hasNext()&&iter2.hasNext()) {
			if(!iter1.next().correctnessTest(iter2.next())) {
				equal=false;
			}
		}
		
		return equal;
	}
	
	private List<String> log=new LinkedList<String>();
	
	public void log(String msg) {
		log.add(lastRptSeqnum+" "+msg);
	}
	public List<String> getLog() {
		return log;
	}
	private void flushLog() {
		log.clear();
	}

}
