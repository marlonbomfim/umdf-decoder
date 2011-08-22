package com.lasalletech.umdf.decoder.fix_replay;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import quickfix.FieldNotFound;
import quickfix.Group;
import quickfix.IncorrectDataFormat;
import quickfix.IncorrectTagValue;
import quickfix.Message;
import quickfix.Session;
import quickfix.UnsupportedMessageType;
import quickfix.field.BeginString;
import quickfix.field.MsgType;
import quickfix.field.RawData;
import quickfix.field.RawDataLength;

import com.lasalletech.umdf.decoder.ReplayStream;
import com.lasalletech.umdf.decoder.UmdfMessage;
import com.lasalletech.umdf.decoder.UmdfMessages;
import com.lasalletech.umdf.decoder.fix_replay.fix_custom.ApplBeginSeqNum;
import com.lasalletech.umdf.decoder.fix_replay.fix_custom.ApplChannelID;
import com.lasalletech.umdf.decoder.fix_replay.fix_custom.ApplEndSeqNum;
import com.lasalletech.umdf.decoder.fix_replay.fix_custom.ApplReqID;
import com.lasalletech.umdf.decoder.fix_replay.fix_custom.ApplReqType;
import com.lasalletech.umdf.decoder.fix_replay.fix_custom.ApplSeqNum;
import com.lasalletech.umdf.decoder.fix_replay.fix_custom.Messages;
import com.lasalletech.umdf.decoder.fix_replay.fix_custom.NoApplIDs;
import com.lasalletech.umdf.decoder.fix_replay.fix_custom.NoApplSeqNums;
import com.lasalletech.umdf.decoder.fix_replay.fix_custom.RawDataOffset;

public class FixReplayStream implements ReplayStream {
	public FixReplayStream(FixReplaySession mySession,int channel,String sendID,String targetID,String verStr,String myName) {
		channelID=channel;
		senderCompID=sendID;
		targetCompID=targetID;
		beginString=verStr;
		debugName=myName;
		mySession.addStream(targetID, this);
	}
	
	private Map<Long,UmdfMessage> responses=new HashMap<Long,UmdfMessage>();
	private Map<Long,Thread> waitQ=new HashMap<Long,Thread>();

	@Override
	public UmdfMessage request(long seqnum) throws IOException {
		System.out.println("[FixReplayStream.request]: ("+debugName+") Sending request for "+seqnum+" from channel "+channelID);
		
		try {
			// check to see if we already have this message
			synchronized(responses) {
				if(responses.containsKey(seqnum)) {
					return responses.remove(seqnum);
				}
			}
		
			Message msg=new Message();
			msg.getHeader().setField(new MsgType(Messages.APPLMESSAGEREQUEST));
			msg.getHeader().setField(new BeginString(beginString));
			msg.setField(new ApplReqID(String.valueOf(seqnum)));
			msg.setField(new ApplReqType(ApplReqType.RETRANSMISSION));
			
			NoApplIDs grp=new NoApplIDs();
			grp.setField(new ApplChannelID(Integer.toString(channelID)));
			grp.setField(new ApplBeginSeqNum((int)seqnum));
			grp.setField(new ApplEndSeqNum((int)seqnum));
			msg.addGroup(grp);
			
			Session.sendToTarget(msg, senderCompID,targetCompID);
			
			synchronized(waitQ) {
				waitQ.put(seqnum, Thread.currentThread());
			}
			Thread.currentThread().wait();
			
			synchronized(responses) {
				return responses.remove(seqnum);
			}

		} catch(Exception e) {
			e.printStackTrace();
			throw new IOException(e);
		}
	}
	
	public void onMessage(Message message) 
			throws FieldNotFound, IncorrectDataFormat, IncorrectTagValue,
			UnsupportedMessageType {
		MsgType type=new MsgType();
		message.getHeader().getField(type);
		
		if(type.valueEquals(Messages.APPLMESSAGEREQUESTACK)) {
		} else if(type.valueEquals(Messages.APPLRAWDATAREPORTING)) {
			RawData rawDataField=new RawData();
			message.setField(rawDataField);
			byte[] rawBytes=rawDataField.getValue().getBytes();
			
			for(Group grp:message.getGroups(NoApplSeqNums.FIELD)) {
				ApplSeqNum seqnumField=new ApplSeqNum();
				grp.setField(seqnumField);
				long seqnum=Long.parseLong(seqnumField.getValue());
				
				RawDataOffset offsetField=new RawDataOffset();
				grp.setField(offsetField);
				
				RawDataLength lenField=new RawDataLength();
				grp.setField(lenField);
				
				byte[] data=new byte[lenField.getValue()];
				System.arraycopy(rawBytes, offsetField.getValue(),
									data, 0, lenField.getValue());
				
				synchronized(responses) {
					responses.put(seqnum,UmdfMessages.replayMessage(data));
				}
				waitQ.get(seqnum).notify();
				waitQ.remove(seqnum);
			}
			
		} else if(type.valueEquals(Messages.APPLMESSAGEREPORT)) {
		} else {
			System.out.println("[FixReplayStream.fromApp]: Unknown message type "+type);
			throw new UnsupportedMessageType();
		}
	}
	
	private int channelID;
	
	private String targetCompID;
	private String senderCompID;
	private String beginString;
	
	private String debugName;
}
