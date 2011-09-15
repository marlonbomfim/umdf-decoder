/** :mode=c++:indentSize=2:noTabs=true:tabSize=2:
  @brief briefdesc

  longdesc
*/

#ifndef FAST_PROTOCOL_H_
#define FAST_PROTOCOL_H_ 1

namespace messages {

const char* kSequenceReset="4";
const char* kHeartbeat="0";

const char* kSecurityList="y";

const char* kIncrementals="X";
const char* kSnapshot="W";
const char* kSecurityStatus="f";

const char* kNews="B";

} // end namespace messages

namespace fields {

// header
const char* kMsgType="MsgType";
const char* kSeqnum="MsgSeqNum";

// instrument identification block
const char* kSecurityID="SecurityID";
const char* kSecuritySource="SecurityIDSource";
const char* kSecurityExchg="SecurityExchange";

// sequence reset
const char* kNewSeqnum="NewSeqNo";

// security list
const char* kSecurityGroup="RelatedSym";
const char* kNumSecurities="NoRelatedSym";
const char* kSymbol="Symbol";
const char* kUpdateAction="SecurityUpdateAction";

// incrementals
const char* kIncrementalSeqnum="RptSeq";
const char* kIncrementalUpdateAction="MDUpdateAction";

// snapshot
const char* kSnapshotSeqnum="LastMsgSeqNumProcessed";

// snapshot and incrementals
const char* kEntryType="MDEntryType";
const char* kTradeDate="TradeDate";
const char* kEntriesGroup="MDEntries";
const char* kPrice="MDEntryPx";
const char* kQuantity="MDEntrySize";
const char* kDate="MDEntryDate";
const char* kTime="MDEntryTime";
const char* kOrderID="OrderID";
const char* kBuyer="MDEntryBuyer";
const char* kSeller="MDEntrySeller";
const char* kPos="MDEntryPositionNo";

} // end namespace fields

#endif // FAST_PROTOCOL_H_
