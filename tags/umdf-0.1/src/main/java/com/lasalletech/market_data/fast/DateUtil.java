package com.lasalletech.market_data.fast;

import java.util.Calendar;

public class DateUtil {
	public static Calendar bvmfToCal(long datePart,long timePart) {
		int year=(int)(datePart/10000);
		int month=(int)((datePart-(year*10000))/100)-1; // months are 0-indexed in java, because why not?
		int day=(int)(datePart-(year*10000)-(month*100));
		
		int hour=(int)(timePart/10000000);
		int minute=(int)((datePart-(hour*10000000))/100000);
		int second=(int)((datePart-(hour*10000000)-(minute*100000))/1000);
		int milli=(int)(datePart-(hour*10000000)-(minute*100000)-(second*1000));

		Calendar cal=Calendar.getInstance();
		cal.set(year, month, day, hour, minute, second);
		cal.set(Calendar.MILLISECOND, milli);
		
		return cal;
	}	
	public static Calendar bvmfToCal(long fullDate) {
		long datePart=fullDate/1000000000;
		long timePart=fullDate-(datePart*1000000000);
		return bvmfToCal(datePart,timePart);
	}
	
	public static long bvmfToUnix(long datePart,long timePart) {
		return bvmfToCal(datePart,timePart).getTimeInMillis();
	}
	public static long bvmfToUnix(long fullDate) {
		return bvmfToCal(fullDate).getTimeInMillis();
	}
	
	public static long unixToBvmf(long in) {
		Calendar cal=Calendar.getInstance();
		cal.setTimeInMillis(in);
		return calToBvmf(cal);
	}
	
	public static long calToBvmf(Calendar in) {		
		long datePart=in.get(Calendar.YEAR)*10000;
		datePart+=(in.get(Calendar.MONTH)+1)*100; // again with the 0-indexing
		datePart+=in.get(Calendar.DAY_OF_MONTH);
		
		long timePart=in.get(Calendar.HOUR)*10000000;
		timePart+=in.get(Calendar.MINUTE)*100000;
		timePart+=in.get(Calendar.SECOND)*1000;
		timePart+=in.get(Calendar.MILLISECOND);
		
		return datePart*1000000000 + timePart;
	}
}
