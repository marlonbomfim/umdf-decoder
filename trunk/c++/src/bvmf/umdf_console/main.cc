/** :mode=c++:indentSize=2:noTabs=true:tabSize=2:
  @brief main function

  longdesc
*/

#include <string>
#include <fstream>
#include <cstdlib>

#include <boost/bind.hpp>

#include "market_data/fast/manager.h"

#include "session.h"
#include "quickfast.h"

using std::string;
using std::ifstream;
using std::atoi;

using boost::bind;

using QuickFAST::Codecs::TemplateRegistryPtr;
using QuickFAST::Codecs::XMLTemplateParser;

int main(int argc,char** argv) {

  ifstream fast_template_file(argv[1]);

  XMLTemplateParser parser;
  TemplateRegistryPtr templates=parser.parse(fast_template_file);

  FastMarketDataManager market_data;

  BvmfSession session(market_data,templates);

  string ip=argv[2];
  int port=atoi(argv[3]);

  Aggregator aggregator;
  aggregator.add_listener(bind(&BvmfSession::on_recv_message,session));
  UdpQueue q(ip,port);

  aggregator.start(q);

  return 0;
}

