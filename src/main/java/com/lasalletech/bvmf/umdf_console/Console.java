package com.lasalletech.bvmf.umdf_console;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.lasalletech.market_data.Instrument;
import com.lasalletech.market_data.InstrumentManager;
import com.lasalletech.market_data.OrderBook;
import com.lasalletech.market_data.OrderEntry;

public class Console {
	private static final int SCREEN_MAX_LINES=15;
	public void run(InstrumentManager mgr) throws IOException {
		while(true) {
			System.out.print(": ");
			String cmd=con.readLine();
			if(cmd==null) break; // end-of-file
			
			if(cmd.equals("quit") || cmd.equals("exit")) {
				break;
			} else if(cmd.equals("count")) {
				System.out.println("There are "+mgr.getNumInstruments()+" instruments in total");
			} else if(cmd.equals("all")) {
				printList(mgr.getAllInstruments());
			} else if(cmd.equals("traded")) {
				Collection<Instrument> all=mgr.getAllInstruments();
				Iterator<Instrument> iter=all.iterator();
				while(iter.hasNext()) {
					Instrument cur=iter.next();
					if(cur.getBook().bidCount()==0 && cur.getBook().offerCount()==0) {
						iter.remove();
					}
				}
				printList(all);
			} else if(cmd.startsWith("booksym")) {
				String[] args=cmd.split("\\s");
				if(args.length==2) {
					String sym=args[1];
					Instrument inst=mgr.getInstrumentBySymbol(sym);
					if(inst==null) {
						System.out.println("Unknown symbol "+sym);
					} else {
						printBook(inst.getBook());
					}
				} else {
					System.out.println("Bad command: should be booksym SYM");
				}
			} else if(cmd.startsWith("book")) {
				// get instrument id and source
				String[] args=cmd.split("\\s");
				if(args.length==3) {
					String id=args[1];
					String src=args[2];
					
					Instrument inst=mgr.getInstrument(id, src);
					if(inst==null) {
						System.out.println("Unknown instrument "+id+" or source "+src);
					} else {
						printBook(inst.getBook());
					}
				} else {
					System.out.println("Bad command: should be book ID SRC");
				}
			} else if(cmd.equals("help")) {
				System.out.print("Commands:\n"+
						"quit - exit the application\n"+"" +
						"count - report number of instruments\n"+
						"all - list all instruments\n"+
						"traded - list all instruments with active trades\n"+
						"book ID SRC - show order book for instrument ID from source SRC\n"+
						"booksym SYMBOL - show order book for instrument with symbol SYM\n");
			} else {
				System.out.println("Unknown command");
			}
		}
	}
	
	private void printList(Collection<Instrument> instruments) throws IOException {
		LinkedList<String> out=new LinkedList<String>();
		for(Instrument cur:instruments) {
			String sym="(none)";
			if(cur.getSymbol()!=null) sym=cur.getSymbol();
			
			String exchg="(unknown)";
			if(cur.getExchange()!=null) exchg=cur.getExchange();
			
			out.add(cur.getID()+"\t"+sym+"\t"+cur.getSource()+"\t"+exchg+"\t"+cur.getBook().bidCount()+"\t"+cur.getBook().offerCount());
		}
		
		printLines(out,SCREEN_MAX_LINES,"ID\tSym\tSource\tExchg\tBids\tOffers");
	}
	
	private void printBook(OrderBook book) throws IOException {
		System.out.printf("\tBids\t\tOffers\n\nPX\tQty\t\tPX\tQty\n");
		
		List<OrderEntry> bids=book.getBids();
		List<OrderEntry> offers=book.getOffers();
		
		boolean first=true;
		while(!bids.isEmpty() || !offers.isEmpty()) {
			
			if(first) {
				first=false;
			} else {
				System.out.print("c to continue, b to go back: ");
				String cmd=con.readLine();
				if(cmd.equals("b")) return;
				System.out.println();
			}
			
			for(int i=0;i<SCREEN_MAX_LINES && (!bids.isEmpty() || !offers.isEmpty());++i) {
				if(!offers.isEmpty() && !bids.isEmpty()) {
					OrderEntry bid=bids.remove(0);
					OrderEntry offer=offers.remove(0);
					System.out.printf("%.3f\t%.3f\t\t%.3f\t%.3f\n",bid.getPrice(),bid.getQty(),offer.getPrice(),offer.getQty());
				} else if(!offers.isEmpty()) {
					OrderEntry offer=offers.remove(0);
					System.out.printf("\t\t\t%.3f\t%.3f\n",offer.getPrice(),offer.getQty());
				} else {
					OrderEntry bid=bids.remove(0);
					System.out.printf("%.3f\t%.3f\n",bid.getPrice(),bid.getQty());
				}
			}
		}
	}
	
	private void printLines(List<String> lines, int maxScreenLines, String header) throws IOException {
		System.out.println("Total: "+lines.size());
		Iterator<String> iter=lines.iterator();
		
		while(iter.hasNext()) {
			System.out.println(header);
			
			for(int i=0;i<maxScreenLines && iter.hasNext();++i) {
				System.out.println(iter.next());
			}
			
			if(iter.hasNext()) {
				System.out.print("c to continue, b to go back: ");
				String cmd=con.readLine();
				System.out.println();
				
				if(cmd.equals("b")) return;
			}
		}
	}
	
	private BufferedReader con=new BufferedReader(new InputStreamReader(System.in));
}
