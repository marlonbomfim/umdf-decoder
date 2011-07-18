package com.lasalletech.umdf.console;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.openfast.Context;
import org.openfast.Message;
import org.openfast.MessageInputStream;
import org.openfast.codec.FastDecoder;
import org.openfast.template.MessageTemplate;
import org.openfast.template.loader.MessageTemplateLoader;
import org.openfast.template.loader.XMLMessageTemplateLoader;

import com.lasalletech.umdf.book.Instrument;
import com.lasalletech.umdf.book.OrderBook;
import com.lasalletech.umdf.book.OrderEntry;
import com.lasalletech.umdf.book.UmdfInstrumentListManager;
import com.lasalletech.umdf.decoder.EmptyReplayStream;
import com.lasalletech.umdf.decoder.MulticastPacketSource;
import com.lasalletech.umdf.decoder.UmdfFastMessageAggregator;
import com.lasalletech.umdf.decoder.UmdfUdpQueue;


import quickfix.SessionSettings;

public class Main {
	public static void main(String[] args) throws Exception {
		// replay stream session configuration
		//FileInputStream settingsFile=new FileInputStream(new File("/home/wes/projects/umdf-decoder/settings.cfg"));
		// market data template
		FileInputStream templateSource = new FileInputStream("/home/wes/projects/umdf-decoder/templates-UMDF.xml");
		MessageTemplateLoader templateLoader = new XMLMessageTemplateLoader();
		MessageTemplate[] templates = templateLoader.load(templateSource);
		
		Context ctx=new Context();
		for(MessageTemplate t:templates) {
			ctx.registerTemplate(Integer.valueOf(t.getId()), t);
		}
		
		UmdfInstrumentListManager instruments=new UmdfInstrumentListManager();
		
		// instrument feed
		UmdfFastMessageAggregator instrumentFeedAggregator=new UmdfFastMessageAggregator();
		UmdfUdpQueue instrumentFeedUdp=new UmdfUdpQueue();
		instrumentFeedUdp.listen(new MulticastPacketSource("233.111.180.112",10050));
		//instrumentFeedAggregator.start(instrumentFeedUdp, new TestEmptyReplayStream(), ctx, instruments);
		instrumentFeedAggregator.start(instrumentFeedUdp, new EmptyReplayStream(), ctx, instruments);
		
		// market snapshot feed
		UmdfFastMessageAggregator snapshotAggregator=new UmdfFastMessageAggregator();
		UmdfUdpQueue snapshotUdp=new UmdfUdpQueue();
		snapshotUdp.listen(new MulticastPacketSource("233.111.180.112",30050));
		snapshotAggregator.start(snapshotUdp, new EmptyReplayStream(), ctx, instruments);
		
		Console con=new Console();
		con.run(instruments);
	}
}
