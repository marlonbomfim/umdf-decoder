/** :mode=c++:indentSize=2:noTabs=true:tabSize=2:
  @brief

  longdesc
*/

#include <iostream>
using namespace std;

#include <boost/exception/all.hpp>

#include "umdf_decoder/aggregator.h"
#include "umdf_decoder/udp_queue.h"
#include "umdf_decoder/message.h"
#include "common.h"

void callback(Message m, Aggregator& a) {
  cout<<m.seqnum()<<endl;
}

int main(int argc,char** argv) {
  try {
    Aggregator aggregator;
    /*aggregator.add_listener([](Message m,Aggregator a) {
      cout << m.seqnum() << endl;
    });*/
    aggregator.add_listener(&callback);

    UdpQueue q("233.111.180.112",10050);

    aggregator.start(q);

    string tmp;
    cin>>tmp;

    aggregator.stop();
  } catch(boost::exception& e) {
    cout<<"[main]: caught boost::exception: "<<
      boost::diagnostic_information(e)<<endl;
  } catch(std::exception& e) {
    cout<<"[main]: caught std::exception: "<<e.what()<<endl;
  }

  return 0;
}
