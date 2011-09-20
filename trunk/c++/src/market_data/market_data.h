/** :mode=c++:indentSize=2:noTabs=true:tabSize=2:
  @brief market data interface

  longdesc
*/

#ifndef MARKET_DATA_MARKET_DATA_H_
#define MARKET_DATA_MARKET_DATA_H_ 1

#include <list>
#include <string>

#include <boost/weak_ptr.hpp>

#include "instrument.h"

struct MarketData {
  virtual std::list<boost::weak_ptr<Instrument> > instruments() const=0;

  virtual boost::weak_ptr<Instrument> find(std::string id, std::string src) const=0;
  virtual boost::weak_ptr<Instrument> find_by_symbol(std::string sym) const=0;

  virtual std::size_t count_instruments() const=0;
};

#endif // MARKET_DATA_MARKET_DATA_H_

