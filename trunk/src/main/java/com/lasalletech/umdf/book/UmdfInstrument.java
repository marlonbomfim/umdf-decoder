package com.lasalletech.umdf.book;

import java.util.LinkedList;

import org.openfast.GroupValue;

import quickfix.FieldNotFound;
import quickfix.IncorrectTagValue;
import quickfix.UnsupportedMessageType;

import com.lasalletech.umdf.decoder.FastProcessor;
import com.lasalletech.umdf.decoder.Messages;

public class UmdfInstrument implements Instrument, FastProcessor {
	
	private UmdfInstrumentListManager parent;

	public UmdfInstrument(GroupValue grp,UmdfInstrumentListManager mgr)
			throws UnsupportedMessageType, FieldNotFound, IncorrectTagValue {
		parent=mgr;
		book=new UmdfOrderBook(this);
		id=grp.getString(Fields.SECURITYID);
		source=grp.getString(Fields.SECURITYIDSOURCE);
		if(grp.isDefined(Fields.SECURITYEXCHANGE)) {
			exchange=grp.getString(Fields.SECURITYEXCHANGE);
		}
		if(grp.isDefined(Fields.SYMBOL)) {
			symbol=grp.getString(Fields.SYMBOL);
		}
		
		process(grp);
	}

	@Override
	public OrderBook getBook() {
		return book;
	}
	public UmdfOrderBook getUmdfBook() {
		return book;
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

	@Override
	public void process(GroupValue msg) throws UnsupportedMessageType,
			FieldNotFound, IncorrectTagValue {
	}

	private String id;
	private String source;
	private String exchange;
	private String symbol;
	
	private LinkedList<UmdfInstrument> alternates=new LinkedList<UmdfInstrument>();
	private LinkedList<UmdfInstrument> underlyings=new LinkedList<UmdfInstrument>();
	private LinkedList<UmdfInstrument> legs=new LinkedList<UmdfInstrument>();
	
	private UmdfOrderBook book;
}
