/** :mode=c++:indentSize=2:noTabs=true:tabSize=2:
  @brief book order entry

  longdesc
*/

#ifndef MARKET_DATA_ORDER_ENTRY_H_
#define MARKET_DATA_ORDER_ENTRY_H_ 1

#include <string>

#include <boost/date_time/posix_time/posix_time_types.hpp>

struct OrderEntry {
  virtual std::string id() const=0;

  virtual int pos() const=0;

  virtual double price() const=0;
  virtual double qty() const=0;

  virtual std::string buyer() const=0;
  virtual std::string seller() const=0;

  virtual boost::posix_time::ptime date() const=0;
};

#endif // MARKET_DATA_ORDER_ENTRY_H_

