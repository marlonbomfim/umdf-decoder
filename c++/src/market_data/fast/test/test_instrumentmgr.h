/** :mode=c++:indentSize=2:noTabs=true:tabSize=2:
  @brief test FAST market data

  longdesc
*/

#ifndef TEST_INSTRUMENTMGR_H_
#define TEST_INSTRUMENTMGR_H_ 1

#include <string>
#include <map>
#include <list>

#include "market_data/market_data.h"

#include "test_instrument.h"

class TestInstrumentManager : public MarketData {
public:
  virtual std::list<Instrument> instruments() const;

  virtual Instrument* find(std::string id,std::string src) const;
  virtual Instrument* find_by_symbol(std::string sym) const;

  virtual int count_instruments() const;

private:
  std::map<std::string,Instrument*> instruments;
};

#endif // TEST_INSTRUMENTMGR_H_
