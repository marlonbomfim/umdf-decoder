/** :mode=c++:indentSize=2:noTabs=true:tabSize=2:
  @brief briefdesc

  longdesc
*/

#ifndef MARKET_DATA_FAST_FAST_PROTOCOL_H_
#define MARKET_DATA_FAST_FAST_PROTOCOL_H_ 1

namespace messages {

extern const char* kSequenceReset;
extern const char* kHeartbeat;

extern const char* kSecurityList;

extern const char* kIncrementals;
extern const char* kSnapshot;
extern const char* kSecurityStatus;

extern const char* kNews;

} // end namespace messages

namespace fields {

// header
extern const char* kMsgType;
extern const char* kSeqnum;

// instrument identification block
extern const char* kSecurityID;
extern const char* kSecuritySource;
extern const char* kSecurityExchg;

// sequence reset
extern const char* kNewSeqnum;

// security list
extern const char* kSecurityGroup;
extern const char* kNumSecurities;
extern const char* kSymbol;
extern const char* kUpdateAction;

// incrementals
extern const char* kIncrementalSeqnum;
extern const char* kIncrementalUpdateAction;

// snapshot
extern const char* kSnapshotSeqnum;

// snapshot and incrementals
extern const char* kEntryType;
extern const char* kTradeDate;
extern const char* kEntriesGroup;
extern const char* kPrice;
extern const char* kQuantity;
extern const char* kDate;
extern const char* kTime;
extern const char* kOrderID;
extern const char* kBuyer;
extern const char* kSeller;
extern const char* kPos;

} // end namespace fields

#endif // MARKET_DATA_FAST_FAST_PROTOCOL_H_

