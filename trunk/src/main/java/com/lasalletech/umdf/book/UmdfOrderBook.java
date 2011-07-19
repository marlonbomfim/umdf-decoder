package com.lasalletech.umdf.book;

import java.util.Calendar;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.openfast.GroupValue;

import quickfix.FieldNotFound;
import quickfix.IncorrectTagValue;
import quickfix.UnsupportedMessageType;

import com.lasalletech.umdf.decoder.FastProcessor;
import com.lasalletech.umdf.decoder.Messages;

import java.util.Collections;

public class UmdfOrderBook implements FastProcessor, OrderBook {
	
	private UmdfInstrument instrument;
	
	public UmdfOrderBook(UmdfInstrument inParent) {
		instrument=inParent;
		lastSnapshotSeq=-1;
	}

	@Override
	public synchronized void process(GroupValue msg)
			throws UnsupportedMessageType, FieldNotFound, IncorrectTagValue {
		String msgtype=msg.getString(Fields.MSGTYPE);
		if(msgtype.equals(Messages.MARKETDATASNAPSHOTFULLREFRESH)) {
			bids.clear();
			offers.clear();
			
			for(GroupValue grp:msg.getSequence(Fields.MDENTRIES).getValues()) {
				if(grp.isDefined(Fields.RPTSEQ)) {
					long seq=grp.getLong(Fields.RPTSEQ);
					if(lastSnapshotSeq>=seq) continue;
					
					lastSnapshotSeq=seq;
				}
				
				addNew(grp);
			}
			
			Collections.sort(bids);
			Collections.sort(offers);
		} else {
			throw new UnsupportedMessageType();
		}
	}
	
	public synchronized void processIncremental(GroupValue info) 
			throws UnsupportedMessageType,FieldNotFound,IncorrectTagValue {
		if(info.isDefined(Fields.RPTSEQ) && lastSnapshotSeq<info.getInt(Fields.RPTSEQ)) {
			lastSnapshotSeq=info.getInt(Fields.RPTSEQ);
			
			String op=info.getString(Fields.MDUPDATEACTION);
			if(op.equals("0")) { // new entry
				addNew(info);
			} else if(op.equals("1")) { // change
				update(info);
			} else if(op.equals("2")) { // delete
				delete(info);
			} else if(op.equals("3")) { // delete all thru
				deleteThru(info);
			} else if(op.equals("4")) { // delete all after
				deleteAfter(info);
			} else if(op.equals("5")) { // overlay
				//TODO: what does this do?
			}
		}
	}
	
	private void deleteThru(GroupValue info) {
		String type=info.getString(Fields.MDENTRYTYPE);
		String id=info.getString(Fields.ORDERID);
		
		if(type.equals("0")) {
			Iterator<UmdfOrderEntry> iter=bids.iterator();
			while(iter.hasNext()) {
				UmdfOrderEntry cur=iter.next();
				iter.remove();
				if(cur.getID().equals(id)) break;
			}
		} else if(type.equals("1")) {
			Iterator<UmdfOrderEntry> iter=offers.iterator();
			while(iter.hasNext()) {
				UmdfOrderEntry cur=iter.next();
				iter.remove();
				if(cur.getID().equals(id)) break;
			}
		}
	}
	
	private void deleteAfter(GroupValue info) {
		String type=info.getString(Fields.MDENTRYTYPE);
		String id=info.getString(Fields.ORDERID);
		
		if(type.equals("0")) {
			Iterator<UmdfOrderEntry> iter=bids.iterator();
			boolean found=false;
			while(iter.hasNext()) {
				UmdfOrderEntry cur=iter.next();
				if(found) iter.remove();
				else if(cur.getID().equals(id)) found=true;
			}
		} else if(type.equals("1")) {
			Iterator<UmdfOrderEntry> iter=offers.iterator();
			boolean found=false;
			while(iter.hasNext()) {
				UmdfOrderEntry cur=iter.next();
				if(found) iter.remove();
				else if(cur.getID().equals(id)) found=true;
			}
		}
	}
	
	private void delete(GroupValue info) {
		String type=info.getString(Fields.MDENTRYTYPE);
		String id=info.getString(Fields.ORDERID);

		if(type.equals("0")) {
			Iterator<UmdfOrderEntry> iter=bids.iterator();
			while(iter.hasNext()) {
				if(iter.next().getID().equals(id)) {
					iter.remove();
					return;
				}
			}
		} else if(type.equals("1")) {
			Iterator<UmdfOrderEntry> iter=offers.iterator();
			while(iter.hasNext()) {
				if(iter.next().getID().equals(id)) {
					iter.remove();
					return;
				}
			}
		}
		
		//TODO: if we get here, we couldn't find the correct OrderID
	}
	
	private void addNew(GroupValue info) {
		String type=info.getString(Fields.MDENTRYTYPE);
		if(type.equals("0")) { // bid
			bids.add(new UmdfOrderEntry(info,this));
			Collections.sort(bids);
		} else if(type.equals("1")) { // offer
			offers.add(new UmdfOrderEntry(info,this));
			Collections.sort(offers);
		}
	}
	
	private void update(GroupValue info) {
		String type=info.getString(Fields.MDENTRYTYPE);
		if(type.equals("0")) {
			for(UmdfOrderEntry cur:bids) {
				if(cur.getID().equals(info.getString(Fields.ORDERID))) {
					cur.updateFromRefresh(info);
					return;
				}
			}
		} else if(type.equals("1")) { 
			for(UmdfOrderEntry cur:offers) {
				if(cur.getID().equals(info.getString(Fields.ORDERID))) {
					cur.updateFromRefresh(info);
					return;
				}
			}
		}
		
		//TODO: if we get here, we could not find the right order
	}

	@Override
	public synchronized List<OrderEntry> getBids() {
		LinkedList<OrderEntry> out=new LinkedList<OrderEntry>();
		for(UmdfOrderEntry cur:bids) {
			out.addFirst(cur);
		}
		return out;
	}

	@Override
	public synchronized List<OrderEntry> getOffers() {
		LinkedList<OrderEntry> out=new LinkedList<OrderEntry>();
		for(UmdfOrderEntry cur:offers) {
			out.addFirst(cur);
		}
		return out;
	}

	@Override
	public UmdfOrderEntry topBid() {
		return bids.peekFirst();
	}

	@Override
	public UmdfOrderEntry topOffer() {
		return offers.peekFirst();
	}

	private LinkedList<UmdfOrderEntry> bids=new LinkedList<UmdfOrderEntry>();
	private LinkedList<UmdfOrderEntry> offers=new LinkedList<UmdfOrderEntry>();
	
	private long lastSnapshotSeq;

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
}
