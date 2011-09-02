package com.lasalletech.bvmf.umdf_console;

import java.io.File;
import java.io.FileInputStream;
import java.util.Map.Entry;

import org.ini4j.Ini;
import org.ini4j.Profile.Section;
import org.openfast.Context;
import org.openfast.template.MessageTemplate;
import org.openfast.template.loader.MessageTemplateLoader;
import org.openfast.template.loader.XMLMessageTemplateLoader;

import com.lasalletech.market_data.fast.FastInstrumentManager;
import com.lasalletech.umdf.decoder.LossyPacketSource;
import com.lasalletech.umdf.decoder.MulticastPacketSource;
import com.lasalletech.umdf.decoder.UmdfMessageAggregator;
import com.lasalletech.umdf.decoder.UmdfUdpQueue;
import com.lasalletech.umdf.decoder.fix_replay.FixReplaySession;
import com.lasalletech.umdf.decoder.fix_replay.FixReplayStream;

public class Main {
	private static MessageTemplate[] templates=null;
	private static FastInstrumentManager instruments=new FastInstrumentManager();
	public static void main(String[] args) throws Exception {
		
		// configuration path defaults to current directory/conf)
		String configPath=System.getProperty("conf","conf/");
		Ini prefs=new Ini(new File(configPath,"settings.cfg"));

		// FAST templates
		String templateFile=prefs.get("FAST").get("TemplateFile","templates-UMDF.xml");
		FileInputStream templateSource=new FileInputStream(new File(configPath,templateFile));
		MessageTemplateLoader templateLoader = new XMLMessageTemplateLoader();
		templates = templateLoader.load(templateSource);
		
		// FIX session
		String sessionFile=prefs.get("FIX").get("ReplaySessionFile","replay_session.cfg");
		FixReplaySession session=new FixReplaySession(new File(configPath,sessionFile));
		
		for(Entry<String,Section> entry:prefs.entrySet()) {
			if(!entry.getKey().equals("FAST") && !entry.getKey().equals("FIX")) {
				Section channel=entry.getValue();
				addFeed(channel.get("InstrumentDefinitionIP"),Integer.parseInt(channel.get("InstrumentDefinitionPort")),
						channel.get("Name")+" Instrument Definition");
				addFeed(channel.get("MarketRecoveryIP"),Integer.parseInt(channel.get("MarketRecoveryPort")),
						channel.get("Name")+" Market Recovery");
				addFeed(channel.get("IncrementalsIP"),Integer.parseInt(channel.get("IncrementalsPort")),
						channel.get("ChannelID"),
						channel.get("TargetCompID"),
						session,
						channel.get("Name")+" Incrementals");
			}
		}
		
		instruments.start();
		
		Console con=new Console();
		con.run(instruments);
	}
	
	private static UmdfMessageAggregator addFeed(String ip, int port,String debugName) throws Exception {
		UmdfMessageAggregator aggregator=new UmdfMessageAggregator(debugName);
		Context ctx=new Context();
		for(MessageTemplate t:templates) {
			ctx.registerTemplate(Integer.valueOf(t.getId()), t);
		}
		aggregator.addListener(new BvmfSession(instruments,ctx));
		UmdfUdpQueue q=new UmdfUdpQueue(debugName);
		q.listen(new MulticastPacketSource(ip,port));
		aggregator.start(q,null,10000);
		
		return aggregator;
	}
	
	private static UmdfMessageAggregator addFeed(String ip, int port,String channel,String targetID,FixReplaySession session,String debugName) throws Exception {
		UmdfMessageAggregator aggregator=new UmdfMessageAggregator(debugName);
		Context ctx=new Context();
		for(MessageTemplate t:templates) {
			ctx.registerTemplate(Integer.valueOf(t.getId()), t);
		}
		aggregator.addListener(new BvmfSession(instruments,ctx));
		UmdfUdpQueue q=new UmdfUdpQueue(debugName);
		q.listen(new LossyPacketSource(new MulticastPacketSource(ip,port),0.75));
		
		aggregator.start(q,new FixReplayStream(session,channel,targetID,debugName),10000);
		
		return aggregator;
	}
}
