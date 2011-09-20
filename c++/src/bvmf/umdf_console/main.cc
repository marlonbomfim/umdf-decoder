/** :mode=c++:indentSize=2:noTabs=true:tabSize=2:
  @brief main function

  longdesc
*/

#include <string>
#include <fstream>
#include <cstdlib>

#include "session.h"

using std::string;
using std::ifstream;
using std::atoi;

int main(int argc,char** argv) {

  ifstream fast_template_file(argv[1]);

  XMLTemplateParser parser;
  TemplateRegistryPtr templates=parser.parse(fast_template_file);

  BvmfSession session(market_data,templates);

  string ip=argv[2];
  int port=atoi(argv[3]);

  Aggregator aggregator;
  aggregator.add_listener(bind(&BvmfSession::on_recv_message,session));
  UdpQueue q(ip,port);

  aggregator.start(q);

  return 0;
}

