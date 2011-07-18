package com.lasalletech.umdf.book;

import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

import org.openfast.GroupValue;

import quickfix.FieldNotFound;
import quickfix.IncorrectTagValue;
import quickfix.UnsupportedMessageType;

import com.lasalletech.umdf.decoder.FastProcessor;
import com.lasalletech.umdf.decoder.Messages;

import edu.emory.mathcs.backport.java.util.Collections;

public class UmdfOrderBook implements FastProcessor, OrderBook {
	
	private UmdfInstrument instrument;
	
	public UmdfOrderBook(UmdfInstrument inParent) {
		instrument=inParent;
	}

	@Override
	public synchronized void process(GroupValue msg)
			throws UnsupportedMessageType, FieldNotFound, IncorrectTagValue {
		String msgtype=msg.getString(Fields.MSGTYPE);
		if(msgtype.equals(Messages.MARKETDATASNAPSHOTFULLREFRESH)) {
			bids.clear();
			offers.clear();
			
			for(GroupValue grp:msg.getSequence(Fields.MDENTRIES).getValues()) {
				if(grp.isDefined(Fields.RPTSEQ))
					lastSnapshotSeq=grp.getLong(Fields.RPTSEQ);
				
				String type=grp.getString(Fields.MDENTRYTYPE);
				if(type.equals("0")) {
					bids.add(new UmdfOrderEntry(grp,this));
				} else if(type.equals("1")) {
					offers.add(new UmdfOrderEntry(grp,this));
				}
			}
			
			Collections.sort(bids);
			Collections.sort(offers);
		}
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
