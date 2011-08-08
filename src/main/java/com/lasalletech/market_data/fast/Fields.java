package com.lasalletech.market_data.fast;

public class Fields {
	// header
	public static final String MSGTYPE="MsgType";
	public static final String MSGSEQNUM="MsgSeqNum";
	
	// instrument identification block
	public static final String SECURITYID="SecurityID";
	public static final String SECURITYIDSOURCE="SecurityIDSource";
	public static final String SECURITYEXCHANGE="SecurityExchange";

	// sequence reset
	public static final String NEWSEQNO="NewSeqNo";

	// security list
	public static final String RELATEDSYM="RelatedSym";
	public static final String NORELATEDSYM="NoRelatedSym";
	public static final String SYMBOL="Symbol";
	public static final String SECURITYUPDATEACTION="SecurityUpdateAction";
	
	// incrementals
	public static final String RPTSEQ="RptSeq";
	public static final String MDUPDATEACTION="MDUpdateAction";
	
	// snapshots
	public static final String LASTMSGSEQNUMPROCESSED="LastMsgSeqNumProcessed";
	
	// snapshots and incrementals
	public static final String MDENTRYTYPE="MDEntryType";
	public static final String TRADEDATE="TradeDate";
	public static final String MDENTRIES="MDEntries";
	public static final String MDENTRYPX="MDEntryPx";
	public static final String MDENTRYSIZE="MDEntrySize";
	public static final String MDENTRYDATE="MDEntryDate";
	public static final String MDENTRYTIME="MDEntryTime";
	public static final String ORDERID="OrderID";
	public static final String MDENTRYBUYER="MDEntryBuyer";
	public static final String MDENTRYSELLER="MDEntrySeller";
	public static final String MDENTRYPOSITIONNO="MDEntryPositionNo";
}
