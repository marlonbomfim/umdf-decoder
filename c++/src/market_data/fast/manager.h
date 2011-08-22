/** :mode=c++:indentSize=2:noTabs=true:tabSize=2:
  @brief fast main marketdata interface

  longdesc
*/

#ifndef MANAGER_H_
#define MANAGER_H_ 1

#include <map>
#include <string>
#include <queue>

#include <boost/thread.hpp>

#include "market_data/market_data.h"
#include "maret_data/fast/instrument.h"

class FastMarketDataManager : public MarketData {
public:
  FastMarketDataManager();

  virtual std::list<Instrument*> instruments() const;

  virtual Instrument* find(std::string id, std::string src) const;
  virtual Instrument* find_by_symbol(std::string sym) const;

  virtual int count_instruments() const;

  void on_message();

  void start();
  void stop();

private:
  std::map<std::string,FastInstrument*> instruments;
  boost::read_write_mutex access_mutex;

  bool running;
  bool done_reading;

  volatile int data_ready;
  boost::condition_variable wait_cond;
  boost::mutex incoming_mutex;
  std::queue<> incoming;

  boost::shared_ptr<boost::thread> update_thread;
  void update_thread_helper();
};

#endif // MANAGER_H_
