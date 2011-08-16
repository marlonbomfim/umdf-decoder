/** :mode=c++:indentSize=2:noTabs=true:tabSize=2:
  @brief common definitions

  longdesc
*/

#ifndef COMMON_H_
#define COMMON_H_ 1

#include <iostream>

#define TRACE0() (::std::cout)<<"["<<__FILE__<<":"<<__LINE__<<"]"<< ::std::endl
#define TRACE(x) (::std::cout)<<"["<<__FILE__<<":"<<__LINE__<<"] "<<x<< ::std::endl

#endif // COMMON_H_
