/** :mode=c++:indentSize=2:noTabs=true:tabSize=2:
  @brief

  longdesc
*/

#include <iostream>
using namespace std;

#include "umdf_decoder/aggregator.h"
#include "umdf_decoder/udp_queue.h"
#include "umdf_decoder/message.h"

int main(int argc,char** argv) {
  Aggregator aggregator;
  aggregator.add_listener([](Message m,Aggregator a) {
    cout << m.seqnum() << endl;
  });

  UdpQueue q("233.111.180.112",10050);

  aggregator.start(q);

  string tmp;
  cin>>tmp;

  aggregator.stop();

  return 0;
}
