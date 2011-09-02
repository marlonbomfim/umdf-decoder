package com.lasalletech.umdf.decoder.fix_replay;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import org.quickfixj.CharsetSupport;

import quickfix.FieldNotFound;
import quickfix.Group;
import quickfix.IncorrectDataFormat;
import quickfix.IncorrectTagValue;
import quickfix.Message;
import quickfix.SessionID;
import quickfix.UnsupportedMessageType;
import quickfix.field.MsgType;
import quickfix.field.RawData;
import quickfix.field.RawDataLength;

import com.lasalletech.umdf.decoder.ReplayStream;
import com.lasalletech.umdf.decoder.fix_replay.fix_custom.ApplBeginSeqNum;
import com.lasalletech.umdf.decoder.fix_replay.fix_custom.RefApplID;
import com.lasalletech.umdf.decoder.fix_replay.fix_custom.ApplEndSeqNum;
import com.lasalletech.umdf.decoder.fix_replay.fix_custom.ApplReqID;
import com.lasalletech.umdf.decoder.fix_replay.fix_custom.ApplReqType;
import com.lasalletech.umdf.decoder.fix_replay.fix_custom.ApplSeqNum;
import com.lasalletech.umdf.decoder.fix_replay.fix_custom.Messages;
import com.lasalletech.umdf.decoder.fix_replay.fix_custom.NoApplIDs;
import com.lasalletech.umdf.decoder.fix_replay.fix_custom.NoApplSeqNums;
import com.lasalletech.umdf.decoder.fix_replay.fix_custom.RawDataOffset;

public class FixReplayStream implements ReplayStream {
	public FixReplayStream(FixReplaySession mySession,String channel,String targetID,String myName) {
		session=mySession;
		channelID=channel;
		targetCompID=targetID;
		debugName=myName;
		mySession.addStream(targetID, this);
	}
	
	private Map<Long,byte[]> responses=new HashMap<Long,byte[]>();
	private Map<Long,Semaphore> waitQ=new HashMap<Long,Semaphore>();

	@Override
	public byte[] request(long seqnum) throws IOException {
		//System.out.println("[FixReplayStream.request]: ("+debugName+") Sending request for "+seqnum+" from channel "+channelID);
		
		try {
			// check to see if we already have this message
			synchronized(responses) {
				if(responses.containsKey(seqnum)) {
					return responses.remove(seqnum);
				}
			}
		
			Message msg=new Message();
			msg.getHeader().setField(new MsgType(Messages.APPLMESSAGEREQUEST));
			msg.setField(new ApplReqID(String.valueOf(seqnum)));
			msg.setField(new ApplReqType(ApplReqType.RETRANSMISSION));
			
			NoApplIDs grp=new NoApplIDs();
			grp.setField(new RefApplID(channelID));
			grp.setField(new ApplBeginSeqNum((int)seqnum));
			grp.setField(new ApplEndSeqNum((int)seqnum));
			msg.addGroup(grp);
			
			//Session.sendToTarget(msg, senderCompID,targetCompID);
			session.send(msg, targetCompID);
			
			Semaphore sem=null;
			synchronized(waitQ) {
				if(waitQ.containsKey(seqnum)) {
					sem=waitQ.get(seqnum);
				} else {
					sem=new Semaphore(0);
					waitQ.put(seqnum,sem);
				}
			}
			
			
			
			if(!sem.tryAcquire(10, TimeUnit.SECONDS)) {
				System.out.println("[FixReplayStream.request]: ("+debugName+") Failed to retrieve "+seqnum+" from channel "+channelID);
				return null;
			}
			
			synchronized(responses) {
				return responses.remove(seqnum);
			}

		} catch(Exception e) {
			e.printStackTrace();
			throw new IOException(e);
		}
	}
	
	public void onMessage(Message message,SessionID sessionId) 
			throws FieldNotFound, IncorrectDataFormat, IncorrectTagValue,
			UnsupportedMessageType {
		try {
		MsgType type=new MsgType();
		message.getHeader().getField(type);
		
		//System.out.println("[FixReplayStream.onMessage]: ("+debugName+") got message "+type.getValue());
		
		if(type.getValue().equals(Messages.APPLMESSAGEREQUESTACK)) {
		} else if(type.getValue().equals(Messages.APPLRAWDATAREPORTING)) {
			// We have to specify the currenct character set here for the bytes to be decoded properly
			byte[] rawBytes=message.getField(new RawData()).getValue().getBytes(CharsetSupport.getCharset());
			
			for(Group grp:message.getGroups(NoApplSeqNums.FIELD)) {
				long seqnum=Long.parseLong(grp.getField(new ApplSeqNum()).getValue());
				
				int offset=grp.getField(new RawDataOffset()).getValue();
				
				int length=grp.getField(new RawDataLength()).getValue();
				
				byte[] data=new byte[length];
				System.arraycopy(rawBytes, offset,
									data, 0, length);
				
				//System.out.println("[FixReplayStream.onMessage]: ("+debugName+") received data for message "+seqnum);
				
				synchronized(responses) {
					responses.put(seqnum,data);
				}
				
				// notify anyone that was waiting
				Semaphore waitObj=waitQ.get(seqnum);
				if(waitObj!=null) {
					waitObj.release();
				}
			}
			
		} else if(type.getValue().equals(Messages.APPLMESSAGEREPORT)) {
		} else {
			System.out.println("[FixReplayStream.fromApp]: Unknown message type "+type);
			throw new UnsupportedMessageType();
		}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	private String channelID;
	
	private String targetCompID;
	
	private String debugName;
	
	private FixReplaySession session;
}
