/** :mode=c++:indentSize=2:noTabs=true:tabSize=2:
  @brief briefdesc

  longdesc
*/

#ifndef SESSION_H_
#define SESSION_H_ 1

#include <boost/shared_ptr.hpp>

#include "market_data/fast/quickfast.h"

#include "umdf_decoder/message.h"
#include "umdf_decoder/aggregator.h"

class BvmfSession : public MessageBuilder {
public:
  BvmfSession::BvmfSession(
      FastMarketDataManager& out,
      TemplateRegistryPtr in_registry) :
    target(out),registry(in_registry) {}

  virtual void addField(
    const QuickFast::Messages::FieldIdentityCPtr& identity,
    const QuickFast::Messages::FieldCPtr& value) {
    out_msg->addField(identity,value);
  }

  void on_recv_message(Message msg,Aggregator& source);

private:
  boost::shared_ptr<QuickFAST::Messages::Message> out_msg;
  FastMarketDataManager& target;
  TemplateRegistryPtr registry;
};

#endif // SESSION_H_

