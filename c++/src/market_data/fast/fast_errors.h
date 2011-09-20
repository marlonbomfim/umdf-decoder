/** :mode=c++:indentSize=2:noTabs=true:tabSize=2:
  @brief briefdesc

  longdesc
*/

#ifndef MARKET_DATA_FAST_FAST_ERRORS_H_
#define MARKET_DATA_FAST_FAST_ERRORS_H_ 1

#include <boost/exception/all.hpp>

class BaseFastException :
    virtual public std::exception, virtual public boost::exception {};

class UnknownFastMessageType : virtual public BaseFastException {};
class UnknownFastTag : virtual public BaseFastException {};
class BadFastTagData : virtual public BaseFastException {};
class UnknownFastSequenceEntry : virtual public BaseFastException {};

typedef boost::error_info<struct tag_errinfo_messagetype,std::string>
  FastMessageType;

typedef boost::error_info<struct tag_errinfo_tagname,std::string>
  FastTagName;

#endif // MARKET_DATA_FAST_FAST_ERRORS_H_

