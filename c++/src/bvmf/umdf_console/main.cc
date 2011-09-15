/** :mode=c++:indentSize=2:noTabs=true:tabSize=2:
  @brief main function

  longdesc
*/

#include <string>

#include "session.h"

using std::string;

int main(int argc,char** argv) {

  ifstream fast_template_file(argv[1]);

  XMLTemplateParser parser;
  TemplateRegistryPtr templates=parser.parse(fast_template_file);

  BvmfSession session(market_data,templates);

  return 0;
}

