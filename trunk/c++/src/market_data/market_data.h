/** :mode=c++:indentSize=2:noTabs=true:tabSize=2:
  @brief market data interface

  longdesc
*/

#ifndef MARKET_DATA_H_
#define MARKET_DATA_H_ 1

#include <list>
#include <string>

#include "instrument.h"

struct MarketData {
  virtual std::list<Instruments> instruments() const=0;

  virtual Instrument* find(std::string id, std::string src) const=0;
  virtual Instrument* find_by_symbol(std::string sym) const=0;

  virtual int count_instruments() const=0;
};

#endif // MARKET_DATA_H_

