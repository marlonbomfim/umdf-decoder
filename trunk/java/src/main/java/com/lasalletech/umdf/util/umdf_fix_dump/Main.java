package com.lasalletech.umdf.util.umdf_fix_dump;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.PrintStream;

import org.openfast.Context;
import org.openfast.GroupValue;
import org.openfast.Message;
import org.openfast.SequenceValue;
import org.openfast.codec.FastDecoder;
import org.openfast.template.ComposedScalar;
import org.openfast.template.Field;
import org.openfast.template.Group;
import org.openfast.template.MessageTemplate;
import org.openfast.template.Scalar;
import org.openfast.template.Sequence;
import org.openfast.template.loader.MessageTemplateLoader;
import org.openfast.template.loader.XMLMessageTemplateLoader;

import com.lasalletech.umdf.decoder.MulticastPacketSource;
import com.lasalletech.umdf.decoder.UmdfMessage;
import com.lasalletech.umdf.decoder.UmdfUdpQueue;

public class Main {
	public static void main(String[] args) throws Exception {
		if(args.length!=3 &&args.length!=4) {
			System.out.println("Usage: umdf-fix-dump host port FAST_template_path [output_file_path]");
			return;
		}
		
		String host=args[0];
		int port=Integer.valueOf(args[1]);
		String templateFile=args[2];
		
		String outFile=host+":"+port+"-"+System.currentTimeMillis()+".fix";
		if(args.length==4) {
			outFile=args[3];
		}
		
		File outputFileObj=new File(outFile);
		//System.out.println(outputFileObj.getCanonicalPath());
		PrintStream output=new PrintStream(outputFileObj);
		
		FileInputStream templateSource=new FileInputStream(new File(templateFile));
		MessageTemplateLoader templateLoader = new XMLMessageTemplateLoader();
		MessageTemplate[] templates = templateLoader.load(templateSource);
		
		Context ctx=new Context();
		for(MessageTemplate t:templates) {
			ctx.registerTemplate(Integer.valueOf(t.getId()), t);
		}
		
		UmdfUdpQueue queue=new UmdfUdpQueue("umdf_fix_dump");
		queue.listen(new MulticastPacketSource(host,port));
		
		while(true) {
			UmdfMessage umsg=queue.read();
			FastDecoder decoder=new FastDecoder(ctx,new ByteArrayInputStream(umsg.getData()));
			Message msg=decoder.readMessage();
			
			// preamble
			output.print("8=FIX.4.4"+SEPARATOR+"9=");

			// message body
			String outStr=writeGroup(msg);
			
			// message length
			output.print(String.valueOf(outStr.length())+SEPARATOR);
			
			// actual body and checksum
			output.print(outStr+SEPARATOR+"10=000"+SEPARATOR);
			
			output.println();
		}
	}
	
	private static String writeGroup(GroupValue value) {
		Group group=value.getGroup();
		int start=value instanceof Message ? 1 : 0;
		
		boolean first=true;
		
		String out=new String();
		
		for(int i=start;i<group.getFieldCount();++i) {
			if(value.isDefined(i)) {
				if(!first) out=out.concat(SEPARATOR);
				//if(!first) System.out.print(SEPARATOR);
				first=false;
				
				Field field=group.getField(i);
				if(field instanceof Scalar || field instanceof ComposedScalar) {
					out=out.concat(writeScalar(field,value.getString(i)));
				} else if(field instanceof Sequence) {
					out=out.concat(writeSequence(value.getSequence(i)));
				} else if(field instanceof Group) {
					out=out.concat(writeGroup(value.getGroup(i)));
				}
			}
		}
		
		return out;
	}
	
	private static String writeScalar(Field field,String value) {
		//System.out.print(field.getId()+"="+value);
		return field.getId()+"="+value;
	}
	
	private static String writeSequence(SequenceValue sequence) {
		String out=writeScalar(sequence.getSequence().getLength(),String.valueOf(sequence.getLength()));
		
		for(int i=0;i<sequence.getLength();++i) {
			//System.out.print(SEPARATOR);
			out=out.concat(SEPARATOR+writeGroup(sequence.get(i)));
		}
		
		return out;
	}
	
	//private static final char SEPARATOR=0x01;
	private static final String SEPARATOR=String.valueOf((char)0x01);
}
