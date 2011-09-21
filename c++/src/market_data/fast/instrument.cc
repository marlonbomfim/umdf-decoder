/** :mode=c++:indentSize=2:noTabs=true:tabSize=2:
  @brief briefdesc

  longdesc
*/

#include "instrument.h"

#include "fast_errors.h"
#include "fast_util.h"
#include "fast_protocol.h"

using std::string;

using boost::shared_ptr;

using QuickFAST::Messages::MessageAccessor;
using QuickFAST::Messages::Message;

FastInstrument::FastInstrument(const MessageAccessor& info) : order_book(*this) {
  sec_id=get_string(info,fields::kSecurityID);
  sec_source=get_string(info,fields::kSecuritySource);
  sec_exchange=get_string(info,fields::kSecurityExchg);
}

void FastInstrument::process_update(const MessageAccessor& msg) {
  str_symbol=get_string(msg,fields::kSymbol);

  //TODO: implement
}

void FastInstrument::process(const MessageAccessor& msg) {
  string type=get_string(msg,fields::kMsgType);
  if(type==messages::kSecurityStatus) {
    //TODO: implement
  } else if(type==messages::kSnapshot) {
    //if(order_book.last_seqnum()==-1)
    order_book.process_snapshot(msg);
  } else {
    BOOST_THROW_EXCEPTION(UnknownFastMessageType()<<FastMessageType(type));
  }
}

void FastInstrument::process_incremental(
    shared_ptr<Message> msg_ptr,
    const MessageAccessor& grp) {
  order_book.process_incremental(msg_ptr,grp);
}

