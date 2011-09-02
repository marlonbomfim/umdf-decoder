package com.lasalletech.umdf.decoder.fix_replay;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.concurrent.ConcurrentHashMap;

import quickfix.ApplicationAdapter;
import quickfix.ConfigError;
import quickfix.DefaultMessageFactory;
import quickfix.FieldNotFound;
import quickfix.FileLogFactory;
import quickfix.IncorrectDataFormat;
import quickfix.IncorrectTagValue;
import quickfix.Initiator;
import quickfix.MemoryStoreFactory;
import quickfix.Message;
import quickfix.Session;
import quickfix.SessionID;
import quickfix.SessionNotFound;
import quickfix.SessionSettings;
import quickfix.ThreadedSocketInitiator;
import quickfix.UnsupportedMessageType;
import quickfix.field.SenderCompID;

public class FixReplaySession extends ApplicationAdapter {
	public FixReplaySession(File sessionSettings) throws ConfigError,FileNotFoundException, UnsupportedEncodingException {
		SessionSettings settings=new SessionSettings(new FileInputStream(sessionSettings));
		Initiator initiator=new ThreadedSocketInitiator(this,
				new MemoryStoreFactory(),settings,new FileLogFactory(settings),
				new DefaultMessageFactory());
		initiator.start();
	}
	
	@Override
	public void fromApp(Message message, SessionID sessionId)
			throws FieldNotFound, IncorrectDataFormat, IncorrectTagValue,
			UnsupportedMessageType {
		String targetID=message.getHeader().getField(new SenderCompID()).getValue();
		FixReplayStream stream=out.get(targetID);
		// if we actually get a response before any streams are registered, we can safely ignore it
		if(stream!=null) stream.onMessage(message,sessionId);
	}
	
	@Override
	public void onLogon(SessionID sessionId) {
		//System.out.println("[FixReplaySession.onLogon]: Connected to "+sessionId);
		sessions.put(sessionId.getTargetCompID(), sessionId);
	}
	
	public boolean send(Message msg,String target) throws SessionNotFound {
		return Session.sendToTarget(msg,sessions.get(target));
	}
	
	public void addStream(String targetCompID,FixReplayStream stream) {
		out.put(targetCompID, stream);
	}
	
	private ConcurrentHashMap<String,FixReplayStream> out=new ConcurrentHashMap<String,FixReplayStream>();
	private ConcurrentHashMap<String,SessionID> sessions=new ConcurrentHashMap<String,SessionID>();
}
