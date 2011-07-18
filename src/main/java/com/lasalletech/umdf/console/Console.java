package com.lasalletech.umdf.console;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import com.lasalletech.umdf.book.Instrument;
import com.lasalletech.umdf.book.InstrumentManager;
import com.lasalletech.umdf.book.OrderBook;
import com.lasalletech.umdf.book.OrderEntry;

public class Console {
	private static final int SCREEN_MAX_LINES=15;
	public void run(InstrumentManager mgr) throws IOException {
		while(true) {
			System.out.print(": ");
			String cmd=con.readLine();
			if(cmd==null) break; // end-of-file
			
			if(cmd.equals("quit") || cmd.equals("exit")) {
				break;
			} else if(cmd.equals("listall")) {
				printList(mgr.getAllInstruments());
			} else if(cmd.equals("listtraded")) {
				Collection<Instrument> all=mgr.getAllInstruments();
				Iterator<Instrument> iter=all.iterator();
				while(iter.hasNext()) {
					Instrument cur=iter.next();
					if(cur.getBook().bidCount()==0 && cur.getBook().offerCount()==0) {
						iter.remove();
					}
				}
				printList(all);
			} else if(cmd.equals("book")) {
				System.out.print("Instrument id: ");
				String id=con.readLine();
				System.out.print("\nSource: ");
				String src=con.readLine();
				
				Instrument inst=mgr.getInstrument(id, src);
				if(inst==null) {
					System.out.println("\nUnknown instrument");
				} else {
					printBook(inst.getBook());
				}
			}
		}
	}
	
	private void printList(Collection<Instrument> instruments) throws IOException {
		Iterator<Instrument> iter=instruments.iterator();
		
		System.out.printf("ID\tSymbol\tSource\tBids\tOffers\n");
		
		boolean first=true;
		while(iter.hasNext()) {
			if(first) {
				first=false;
			} else {
				System.out.print("c to continue, b to go back: ");
				String cmd=con.readLine();
				if(cmd.equals("b")) return;
				System.out.println();
			}
			
			for(int i=0;i<SCREEN_MAX_LINES && iter.hasNext();++i) {
				Instrument cur=iter.next();
				System.out.printf("%s\t%s\t%s\t%d\t%d\n",
						cur.getID(),cur.getSymbol(),cur.getSource(),
						cur.getBook().bidCount(),cur.getBook().offerCount());
			}
		}
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
					System.out.printf("%f\t%f\t\t%f\t%f\n",bid.getPrice(),bid.getQty(),offer.getPrice(),offer.getQty());
				} else if(!offers.isEmpty()) {
					OrderEntry offer=offers.remove(0);
					System.out.printf("\t\t\t%f\t%f\n",offer.getPrice(),offer.getQty());
				} else {
					OrderEntry bid=bids.remove(0);
					System.out.printf("%f\t%f\n",bid.getPrice(),bid.getQty());
				}
			}
		}
	}
	
	private BufferedReader con=new BufferedReader(new InputStreamReader(System.in));
}
