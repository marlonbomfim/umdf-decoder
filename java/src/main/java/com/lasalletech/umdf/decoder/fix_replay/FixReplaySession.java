package com.lasalletech.umdf.decoder.fix_replay;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
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
import quickfix.SessionID;
import quickfix.SessionSettings;
import quickfix.ThreadedSocketInitiator;
import quickfix.UnsupportedMessageType;
import quickfix.field.TargetCompID;

public class FixReplaySession extends ApplicationAdapter {
	public FixReplaySession(File sessionSettings) throws ConfigError,FileNotFoundException {
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
		FixReplayStream stream=out.get(message.getHeader().getField(new TargetCompID()).getValue());
		// if we actually get a response before any streams are registered, we can safely ignore it
		if(stream!=null) out.get(message.getHeader().getField(new TargetCompID()).getValue()).onMessage(message);
	}
	
	public void addStream(String targetCompID,FixReplayStream stream) {
		out.put(targetCompID, stream);
	}
	
	private ConcurrentHashMap<String,FixReplayStream> out=new ConcurrentHashMap<String,FixReplayStream>();
}
