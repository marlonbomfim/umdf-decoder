/** :mode=c++:indentSize=2:noTabs=true:tabSize=2:
  @brief briefdesc

  longdesc
*/

#include "session.h"

#include <iostream>

#include <quickfast/Codecs/Decoder.h>
#include <quickfast/Codecs/DataSourceBuffer.h>

using std::string;
using std::cout;
using std::endl;

using QuickFAST::Codecs::Decoder;
using QuickFAST::Codecs::Context;
using QuickFAST::Messages::MessageBuilder;
using QuickFAST::Messages::FieldIdentityCPtr;
using QuickFAST::Messages::FieldCPtr;

void BvmfSession::on_recv_message(Message msg,Aggregator& source) {
  void* tmp_buf=new char[msg.total_size()];
  msg.read(tmp_buf,0,msg.total_size());

  Decoder decoder;
  GenericMessageBuilder builder(*this);
  decoder.decodeMessage(DataSourceBuffer(tmp_buf,out_msg.total_size()),builder);

  delete[] tmp_buf;
}

bool BvmfSession::consumeMessage(QuickFAST::Messages::Message& msg) {
  out_msg.reset(new QuickFAST::Messages::Message(0));
  out_msg->swap(msg);

  target.on_message(out_msg);

  return true;
}

bool BvmfSession::logMessage(unsigned short,const string& msg) {
  cout<<msg<<endl;
  return true;
}

bool BvmfSession::reportDecodingError(const string& msg) {
  cout<<msg<<endl;
  return true;
}

bool BvmfSession::reportCommunicationError(const string& msg) {
  cout<<msg<<endl;
  return true;
}

