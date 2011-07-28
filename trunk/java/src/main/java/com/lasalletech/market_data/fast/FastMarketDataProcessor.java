package com.lasalletech.market_data.fast;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.openfast.GroupValue;

import com.lasalletech.market_data.Instrument;
import com.lasalletech.market_data.InstrumentManager;
import com.lasalletech.market_data.fast.error.FieldNotFound;
import com.lasalletech.market_data.fast.error.InvalidFieldValue;
import com.lasalletech.market_data.fast.error.UnsupportedMessageType;

public class FastMarketDataProcessor implements InstrumentManager {

	@Override
	public Collection<Instrument> getAllInstruments() {
		
		LinkedList<Instrument> out=new LinkedList<Instrument>();
		
		for(FastInstrument cur:instruments.values()) {
			out.add(cur);
		}

		return out;
	}
	
	@Override
	public int getNumInstruments() {
		return instruments.size();
	}

	@Override
	public Instrument getInstrument(String id, String src) {
		return instruments.get(makeHash(id,src));
	}
	
	private Map<String,FastInstrument> instruments=new ConcurrentHashMap<String,FastInstrument>();

	public void onMessage(GroupValue msg) throws UnsupportedMessageType, FieldNotFound, InvalidFieldValue {
		String type=FastUtil.getString(msg, Fields.MSGTYPE);
		//TODO: add callbacks
		if(type.equals(Messages.SECURITYLIST)) {
			synchronized(updates) {
				updates.add(msg);
			}
		} else if(type.equals(Messages.SECURITYSTATUS)) {
			synchronized(updates) {
				updates.add(msg);
			}
		} else if(type.equals(Messages.MARKETDATASNAPSHOTFULLREFRESH)) {
			synchronized(updates) {
				updates.add(msg);
			}
		} else if(type.equals(Messages.MARKETDATAINCREMENTALREFRESH)) {
			synchronized(updates) {
				updates.add(msg);
			}
		} else if(type.equals(Messages.NEWS)) {
			synchronized(updates) {
				updates.add(msg);
			}
		} else {
			throw new UnsupportedMessageType(type);
		}
	}
	
	private Thread thread;
	boolean running=false;
	public void start() {
		thread=new Thread() {
			@Override
			public void run() {
				try {
					running=true;
					processQueue();
				//} catch(InterruptedException e) {
				} catch(Exception e) {
					e.printStackTrace();
				} finally {
					running=false;
				}
			}
		};
		thread.setDaemon(true);
		thread.start();
	}
	public void stop() {
		if(running) {
			thread.interrupt();
		}
	}
	
	private List<GroupValue> updates=new LinkedList<GroupValue>();
	
	private void processQueue() throws FieldNotFound,InvalidFieldValue, UnsupportedMessageType {
		while(!Thread.interrupted()) {
			synchronized(updates) {
				Iterator<GroupValue> iter=updates.iterator();
				while(iter.hasNext()) {
					GroupValue msg=iter.next();
					String type=FastUtil.getString(msg, Fields.MSGTYPE);
					if(type.equals(Messages.SECURITYLIST)) {
						if(processInstrumentUpdates(msg)) iter.remove();
					} else if(type.equals(Messages.NEWS)) {
						//TODO: implement
						iter.remove();
					} else if(type.equals(Messages.MARKETDATAINCREMENTALREFRESH)) {
						if(processIncrementals(msg)) iter.remove();
					} else {
						FastInstrument inst=instruments.get(makeHash(msg));
						if(inst!=null) {
							if(inst.process(msg)) iter.remove();
						}
					}
				}
			}
		}
	}
	
	private boolean processIncrementals(GroupValue msg) throws FieldNotFound,InvalidFieldValue, UnsupportedMessageType {
		boolean processed=true;
		for(GroupValue grp:FastUtil.getSequence(msg, Fields.MDENTRIES)) {
			FastInstrument inst=instruments.get(makeHash(grp));
			if(inst==null || !inst.processIncrementals(grp)) {
				processed=false;
			}
		}
		
		return processed;
	}
	
	private boolean processInstrumentUpdates(GroupValue msg) throws FieldNotFound,InvalidFieldValue, UnsupportedMessageType {
		for(GroupValue grp:FastUtil.getSequence(msg,Fields.RELATEDSYM)) {
			String code=makeHash(grp);
			if(grp.isDefined(Fields.SECURITYUPDATEACTION)) {
				char op=(char)grp.getByte(Fields.SECURITYUPDATEACTION);
				if(op=='A') {
					instruments.put(code, new FastInstrument(grp,this));
				} else if(op=='M') {
					FastInstrument inst=instruments.get(code);
					if(inst!=null) inst.processUpdate(grp);
					else {
						// this means we got a modification when there was no instrument to modify
						throw new InvalidFieldValue(Fields.SECURITYID,grp.getString(Fields.SECURITYID));
					}
				} else if(op=='D') {
					instruments.remove(makeHash(grp));
				} else {
					throw new InvalidFieldValue(Fields.SECURITYUPDATEACTION,String.valueOf(op));
				}
			} else {
				// no action specified.  either create it, or modify it if it exists
				FastInstrument inst=instruments.get(code);
				if(inst==null) {
					instruments.put(code, new FastInstrument(grp,this));
				} else {
					inst.processUpdate(grp);
				}
			}
		}
		
		return true; // we always end up processing this message
	}
	
	private String makeHash(String id,String src) {
		return id+":"+src;
	}
	
	private String makeHash(GroupValue grp) throws FieldNotFound {
		return makeHash(FastUtil.getString(grp, Fields.SECURITYID),FastUtil.getString(grp,Fields.SECURITYIDSOURCE));
	}

	@Override
	public Instrument getInstrumentBySymbol(String sym) {
		for(FastInstrument cur:instruments.values()) {
			if(cur.getSymbol().equals(sym)) return cur;
		}
		return null;
	}
}
