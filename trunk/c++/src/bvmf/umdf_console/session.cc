/** :mode=c++:indentSize=2:noTabs=true:tabSize=2:
  @brief briefdesc

  longdesc
*/

#include "session.h"

#include <quickfast/Codecs/Decoder.h>
#include <quickfast/Codecs/DataSourceBuffer.h>

using QuickFAST::Codecs::Decoder;
using QuickFAST::Codecs::Context;
using QuickFAST::Messages::MessageBuilder;
using QuickFAST::Messages::FieldIdentityCPtr;
using QuickFAST::Messages::FieldCPtr;

void BvmfSession::on_recv_message(Message msg,Aggregator& source) {
  void* tmp_buf=new char[msg.total_size()];
  msg.read(tmp_buf,0,msg.total_size());

  out_msg.reset(new QuickFAST::Messages::Message(0));

  Decoder decoder;
  decoder.decodeMessage(DataSourceBuffer(tmp_buf,out_msg.total_size()),*this);

  delete[] tmp_buf;

  target.on_message(out_msg);
}

