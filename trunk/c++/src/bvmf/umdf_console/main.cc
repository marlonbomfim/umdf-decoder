/** :mode=c++:indentSize=2:noTabs=true:tabSize=2:
  @brief main function

  longdesc
*/

#include <string>
#include <vector>
#include <iostream>
using namespace std;

#include <boost/algorithm/string.hpp>

#include "platform.h"
#include "console.h"
#include "umdf_decoder/udpqueue.h"
#include "umdf_decoder/aggregator.h"
#include "umdf_decoder/message.h"

int main(int argc,char** argv) {
  // default FAST templates path
  /*string path="templates-UMDF.xml";
  for(int i=1;i<argc;++i) {
    if(strcmp(argv[i],"-t")==0) {
      if(!(i<argc-1)) {
        cerr<<"Missing argument for -t";
        return 1;
      }
      path=argv[i+1];
  }*/

  //TODO: load templates

  // parse feeds
  /*for(int i=1;i<argc;++i) {
    if(strcmp(argv[i],"-t")) {
      ++i; continue;
    }

    vector<string> parts;
    boost::split(parts,argv[i],boost::is_any_of(":"));
    if(parts.size()!=2) {
      cerr<<"Invalid argument "<<argv[i]<<"; should be ip:port";
      return 1;
    }

    string ip=parts[0];
    int port=boost::lexical_cast<int>(parts[1]);
    if(port>65535||port<1) {
      cerr<<"Invalid argument "<<parts[1]<<" is not in the valid port range";
      return 1;
    }

    //
  }*/

  return 0;
}

