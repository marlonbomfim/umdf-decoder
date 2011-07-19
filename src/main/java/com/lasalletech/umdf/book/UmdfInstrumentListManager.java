package com.lasalletech.umdf.book;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

import org.openfast.GroupValue;
import org.openfast.SequenceValue;

import quickfix.FieldNotFound;
import quickfix.IncorrectTagValue;
import quickfix.UnsupportedMessageType;

import com.lasalletech.umdf.decoder.FastProcessor;
import com.lasalletech.umdf.decoder.Messages;

public class UmdfInstrumentListManager implements InstrumentManager,FastProcessor {

	@Override
	public synchronized Collection<Instrument> getAllInstruments() {
		LinkedList<Instrument> out=new LinkedList<Instrument>();
		for(UmdfInstrument cur:instruments) {
			out.add(cur);
		}
		return out;
	}

	@Override
	public synchronized Instrument getInstrument(String id, String src) {
		for(UmdfInstrument cur:instruments) {
			if(cur.getID().equals(id) && cur.getSource().equals(src)) return cur;
		}
		return null;
	}
	
	private LinkedList<UmdfInstrument> instruments=new LinkedList<UmdfInstrument>();



	@Override
	public synchronized void process(GroupValue msg) throws UnsupportedMessageType,
			FieldNotFound, IncorrectTagValue {
		String type=msg.getString(Fields.MSGTYPE);
		if(type.equals(Messages.SECURITYLIST)) {
			for(GroupValue grp:msg.getSequence(Fields.RELATEDSYM).getValues()) {
				if(grp.isDefined(Fields.SECURITYUPDATEACTION)) {
					char op=(char)grp.getByte(Fields.SECURITYUPDATEACTION);
					if(op=='A') {
						instruments.add(new UmdfInstrument(grp,this));
					} else if(op=='M') {
						find(grp).process(grp);
					} else {
						delete(grp);
					}
				} else {
					// create it if it doesn't exist, update it otherwise
					UmdfInstrument inst=find(grp);
					if(inst==null) instruments.add(new UmdfInstrument(grp,this));
					else inst.process(grp);
				}
			}
		} else if(type.equals(Messages.SECURITYSTATUS))	{
			//
		} else if(type.equals(Messages.MARKETDATASNAPSHOTFULLREFRESH)) {
			// find instrument
			UmdfInstrument inst=find(msg);
			if(inst==null) {
				// skip this update, wait for when we know what the instrument is
				return;
			}
			
			inst.getUmdfBook().process(msg);
		} else if(type.equals(Messages.MARKETDATAINCREMENTALREFRESH)) {
			for(GroupValue grp:msg.getSequence(Fields.MDENTRIES).getValues()) {
				UmdfInstrument inst=find(grp);
				if(inst==null) {
					// skip this update, wait for when we know what the instrument is
					return;
				}
				
				inst.getUmdfBook().processIncremental(grp);
			}
		} else {
			throw new UnsupportedMessageType();
		}
	}
	
	private UmdfInstrument find(GroupValue info) throws FieldNotFound {
		String id=info.getString(Fields.SECURITYID);
		String src=info.getString(Fields.SECURITYIDSOURCE);
		for(UmdfInstrument cur:instruments)	{
			if(cur.getID().equals(id) && cur.getSource().equals(src)) return cur;
		}
		return null;
	}
	
	private UmdfInstrument delete(GroupValue info) throws FieldNotFound	 {
		String id=info.getString(Fields.SECURITYID);
		String src=info.getString(Fields.SECURITYIDSOURCE);
		
		Iterator<UmdfInstrument> i=instruments.iterator();
		while(i.hasNext()) {
			UmdfInstrument cur=i.next();
			if(cur.getID().equals(id) && cur.getSource().equals(src)) {
				i.remove();
				return cur;
			}
		}

		return null;
	}
}
