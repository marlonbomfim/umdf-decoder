package com.lasalletech.market_data.fast;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Semaphore;

import org.openfast.GroupValue;

import com.lasalletech.market_data.Instrument;
import com.lasalletech.market_data.MarketData;
import com.lasalletech.market_data.fast.error.FieldNotFound;
import com.lasalletech.market_data.fast.error.InvalidFieldValue;
import com.lasalletech.market_data.fast.error.UnsupportedMessageType;

public class FastInstrumentManager implements MarketData {

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
		synchronized(updates) {
			updates.add(msg);
		}
		updateSem.release();
	}
	
	private Thread thread;
	boolean running=false;
	public void start() {
		thread=new Thread() {
			@Override
			public void run() {
				try {
					running=true;
					
					while(!Thread.interrupted()) {
						updateSem.acquire();
						synchronized(updates) {
							processMsg(updates.remove());
						}
					}
					
					//processQueue();
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
	
	private Queue<GroupValue> updates=new LinkedList<GroupValue>();
	private Semaphore updateSem=new Semaphore(0);
	
	private void processMsg(GroupValue msg) throws FieldNotFound, InvalidFieldValue, UnsupportedMessageType {
		String type=FastUtil.getString(msg, Fields.MSGTYPE);
		
		if(type.equals(Messages.SECURITYLIST)) {
			processInstrumentUpdates(msg);
		} else if(type.equals(Messages.NEWS)) {
			//TODO: implement
		} else if(type.equals(Messages.MARKETDATAINCREMENTALREFRESH)) {
			processIncrementals(msg);
		} else {
			FastInstrument inst=instruments.get(makeHash(msg));
			if(inst!=null) inst.process(msg);
			else {
				newInstrument(makeHash(msg),msg).process(msg);
			}
		}
	}
	
	private void processIncrementals(GroupValue msg) throws FieldNotFound,InvalidFieldValue, UnsupportedMessageType {
		for(GroupValue grp:FastUtil.getSequence(msg, Fields.MDENTRIES)) {
			FastInstrument inst=instruments.get(makeHash(grp));
			if(inst!=null) inst.processIncremental(grp);
			else {
				newInstrument(makeHash(msg),msg).processIncremental(grp);
			}
		}
	}
	
	private void processInstrumentUpdates(GroupValue msg) throws FieldNotFound,InvalidFieldValue, UnsupportedMessageType {
		for(GroupValue grp:FastUtil.getSequence(msg,Fields.RELATEDSYM)) {
			String code=makeHash(grp);
			if(grp.isDefined(Fields.SECURITYUPDATEACTION)) {
				char op=(char)grp.getByte(Fields.SECURITYUPDATEACTION);
				if(op=='A') {
					newInstrument(code,grp).processUpdate(grp);
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
					newInstrument(code,grp).processUpdate(grp);
				} else {
					inst.processUpdate(grp);
				}
			}
		}
	}
	
	private FastInstrument newInstrument(String code,GroupValue grp) throws UnsupportedMessageType, FieldNotFound, InvalidFieldValue {
		FastInstrument newInst=new FastInstrument(grp,this);
		if(instruments.put(code,newInst)!=null) {
			System.out.println("[FastInstrumentManager.newInstrument]: Duplicate instrument code "+code);
			throw new InvalidFieldValue(Fields.SECURITYID,grp.getString(Fields.SECURITYID));
		}
		return newInst;
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
