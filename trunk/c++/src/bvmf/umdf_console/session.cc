/** :mode=c++:indentSize=2:noTabs=true:tabSize=2:
  @brief briefdesc

  longdesc
*/

#include "session.h"

#include <iostream>

#include <boost/scoped_array.hpp>

using std::string;
using std::cout;
using std::endl;

using boost::scoped_array;

using QuickFAST::Codecs::Decoder;
using QuickFAST::Codecs::Context;
using QuickFAST::Messages::MessageBuilder;
using QuickFAST::Messages::FieldIdentityCPtr;
using QuickFAST::Messages::FieldCPtr;
using QuickFAST::Codecs::GenericMessageBuilder;
using QuickFAST::Codecs::DataSourceBuffer;

void BvmfSession::on_recv_message(Message msg,Aggregator& source) {
  scoped_array<unsigned char> tmp_buf(new unsigned char[msg.size()]);
  msg.read(tmp_buf.get(),0,msg.size());

  Decoder decoder(registry);
  GenericMessageBuilder builder(*this);
  DataSourceBuffer tmp_buf2(tmp_buf.get(),msg.size());
  decoder.decodeMessage(tmp_buf2,builder);
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

