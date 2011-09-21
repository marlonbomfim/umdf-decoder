/** :mode=c++:indentSize=2:noTabs=true:tabSize=2:
  @brief instrument information

  longdesc
*/

#ifndef MARKET_DATA_INSTRUMENT_H_
#define MARKET_DATA_INSTRUMENT_H_ 1

#include <string>

struct Book;

struct Instrument {
  virtual std::string id() const=0;
  virtual std::string source() const=0;

  virtual std::string symbol() const=0;

  virtual std::string exchange() const=0;

  virtual const Book& book() const=0;
};

#endif // MARKET_DATA_INSTRUMENT_H_

