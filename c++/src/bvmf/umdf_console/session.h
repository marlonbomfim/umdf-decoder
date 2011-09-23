/** :mode=c++:indentSize=2:noTabs=true:tabSize=2:
  @brief briefdesc

  longdesc
*/

#ifndef SESSION_H_
#define SESSION_H_ 1

#include <boost/shared_ptr.hpp>

#include "quickfast.h"

#include "market_data/fast/manager.h"
#include "market_data/fast/quickfast.h"

#include "umdf_decoder/message.h"
#include "umdf_decoder/aggregator.h"

class BvmfSession : public QuickFAST::Codecs::MessageConsumer {
public:
  BvmfSession(
      FastMarketDataManager& out,
      QuickFAST::Codecs::TemplateRegistryPtr in_registry) :
    target(out),registry(in_registry) {}

  virtual bool consumeMessage(QuickFAST::Messages::Message& msg);

  virtual void decodingStarted() {}
  virtual void decodingStopped() {}

  void on_recv_message(Message msg,Aggregator& source);

private:
  boost::shared_ptr<QuickFAST::Messages::Message> out_msg;
  FastMarketDataManager& target;
  QuickFAST::Codecs::TemplateRegistryPtr registry;
};

#endif // SESSION_H_

