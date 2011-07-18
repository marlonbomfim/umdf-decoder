package com.lasalletech.umdf.decoder;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Vector;

public class TestUtil {
	public static List<byte[]> generateUmdf(List<String> inMsgs) throws IOException {
		LinkedList<byte[]> out=new LinkedList<byte[]>();
		
		ByteArrayOutputStream tmp=new ByteArrayOutputStream();
		DataOutputStream s=new DataOutputStream(tmp);
		
		int i=0;
		for(String cur:inMsgs) {
			s.writeInt(i); // sequence number
			s.writeShort(1); // noChunks
			s.writeShort(1); // curChunk
			byte[] msgData=cur.getBytes();
			s.writeShort(msgData.length); // msg length
			s.write(msgData); // msg bytes
			
			out.add(tmp.toByteArray());
			
			tmp.reset();
			i++;
		}
		
		return out;
	}
	
	public static LinkedList<byte[]> generateUmdfChunks(Vector<String> inMsgs,
														int chunkSize)
			throws IOException {
		LinkedList<byte[]> out=new LinkedList<byte[]>();
		
		ByteArrayOutputStream tmp=new ByteArrayOutputStream();
		DataOutputStream s=new DataOutputStream(tmp);
		
		int seqnum=0;
		for(byte[] inData:generateUmdf(inMsgs)) {
			// integer division, always rounding up
			int chunks=(inData.length/chunkSize)+((inData.length%chunkSize)>0?1:0);
			for(int i=0;i<inData.length;i+=chunkSize) {
				s.writeInt(seqnum); // sequence number
				s.writeShort(chunks); // noChunks
				s.writeShort((short)(i+1)); // curChunk
				
				int len=inData.length-i<chunkSize?(inData.length-i):chunkSize;
				s.writeShort(len); // msg length
				s.write(inData, i, len); // msg data
				
				out.add(tmp.toByteArray());
				
				tmp.reset();
			}
			
			seqnum++;
		}
		
		return out;
	}
		
	public static Vector<String> generateMessages(int n) {
		Vector<String> out=new Vector<String>();
		for(int i=0;i<n;++i) {
			out.add(i+"th message");
		}
		return out;
	}
}
