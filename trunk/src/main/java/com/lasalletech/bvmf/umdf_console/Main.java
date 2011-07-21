package com.lasalletech.bvmf.umdf_console;

import java.io.FileInputStream;

import org.openfast.Context;
import org.openfast.template.MessageTemplate;
import org.openfast.template.loader.MessageTemplateLoader;
import org.openfast.template.loader.XMLMessageTemplateLoader;

import com.lasalletech.market_data.fast.FastMarketDataProcessor;
import com.lasalletech.umdf.decoder.MulticastPacketSource;
import com.lasalletech.umdf.decoder.UmdfMessageAggregator;
import com.lasalletech.umdf.decoder.UmdfUdpQueue;

public class Main {
	public static void main(String[] args) throws Exception {
		
		String path="templates-UMDF.xml";
		
		// find FAST template path if given
		for(int i=0;i<args.length;++i) {
			if(args[i].equals("-t")) {
				if(!(i<args.length-1)) {
					System.out.println("Missing argument for -t");
					return;
				}
				
				path=args[i+1];
			}
		}
		
		// replay stream session configuration
		//FileInputStream settingsFile=new FileInputStream(new File("/home/wes/projects/umdf-decoder/settings.cfg"));
		// market data template
		//FileInputStream templateSource = new FileInputStream("/home/wes/projects/umdf-decoder/templates-UMDF.xml");
		FileInputStream templateSource=new FileInputStream(path);
		MessageTemplateLoader templateLoader = new XMLMessageTemplateLoader();
		MessageTemplate[] templates = templateLoader.load(templateSource);
		
		FastMarketDataProcessor instruments=new FastMarketDataProcessor();
		
		// parse feed parameters
		for(int i=0;i<args.length;++i) {
			if(args[i].equals("-t")) {
				++i; continue;
			}
			
			String[] feed=args[i].split(":");
			if(feed.length!=2) {
				System.out.println("Invalid argument "+args[i]+"; should be ip:port");
				return;
			}
			
			String ip=feed[0];
			int port=0;
			try {
				port=Integer.valueOf(feed[1]);
			} catch(NumberFormatException e) {
				System.out.println("Invalid argument: "+feed[1]+" is not a port");
				return;
			}
			if(port>65535 || port<1) {
				System.out.println("Invalid argument: "+feed[1]+" is not in the valid port range");
				return;
			}
			
			UmdfMessageAggregator aggregator=new UmdfMessageAggregator();
			Context ctx=new Context();
			for(MessageTemplate t:templates) {
				ctx.registerTemplate(Integer.valueOf(t.getId()), t);
			}
			aggregator.addListener(new BvmfSession(instruments,ctx));
			UmdfUdpQueue q=new UmdfUdpQueue();
			q.listen(new MulticastPacketSource(ip,port));
			aggregator.start(q);
		}
		
		instruments.start();
		
		// instrument feed
		/*UmdfFastMessageAggregator instrumentFeedAggregator=new UmdfFastMessageAggregator();
		UmdfUdpQueue instrumentFeedUdp=new UmdfUdpQueue();
		instrumentFeedUdp.listen(new MulticastPacketSource("233.111.180.112",10050));
		//instrumentFeedAggregator.start(instrumentFeedUdp, new TestEmptyReplayStream(), ctx, instruments);
		instrumentFeedAggregator.start(instrumentFeedUdp, new EmptyReplayStream(), ctx, instruments);
		
		// market snapshot feed
		UmdfFastMessageAggregator snapshotAggregator=new UmdfFastMessageAggregator();
		UmdfUdpQueue snapshotUdp=new UmdfUdpQueue();
		snapshotUdp.listen(new MulticastPacketSource("233.111.180.112",30050));
		snapshotAggregator.start(snapshotUdp, new EmptyReplayStream(), ctx, instruments);
		
		// market incremental feed
		UmdfFastMessageAggregator incrementalAggregator=new UmdfFastMessageAggregator();
		UmdfUdpQueue incrUdp=new UmdfUdpQueue();
		incrUdp.listen(new MulticastPacketSource("233.111.180.113",20050));
		incrementalAggregator.start(incrUdp, new EmptyReplayStream(), ctx, instruments);*/
		
		Console con=new Console();
		con.run(instruments);
	}
}
