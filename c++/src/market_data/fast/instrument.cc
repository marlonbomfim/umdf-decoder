/** :mode=c++:indentSize=2:noTabs=true:tabSize=2:
  @brief briefdesc

  longdesc
*/

#include "instrument.h"

#include "fast_errors.h"
#include "fast_util.h"
#include "fast_protocol.h"

using std::string;

using QuickFAST::Messages::MessageAccessor;

FastInstrument::FastInstrument(MessageAccessor& info) : order_book(*this) {
  sec_id=get_string(info,fields::kSecurityID);
  sec_source=get_string(info,fields::kSecuritySource);
  sec_exchange=get_string(info,fields::kSecurityExchg);
}

void FastInstrument::process_update(MessageAccessor& msg) {
  str_symbol=get_string(msg,fields::kSymbol);

  //TODO: implement
}

void FastInstrument::process(MessageAccessor& msg) {
  string type=get_string(msg,fields::kMsgType);
  if(type==fields::kSecurityStatus) {
    //TODO: implement
  } else if(type==fields::kSnapshot) {
    if(order_book.last_seqnum()==-1) order_book.process_snapshot(msg);
  } else {
    BOOST_THROW_EXCEPTION(UnknownFastMessageType()<<FastMessageType(type));
  }
}

void FastInstrument::process_incremental(MessageAccessor& msg) {
  order_book.process_incremental(msg);
}

