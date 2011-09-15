/** :mode=c++:indentSize=2:noTabs=true:tabSize=2:
  @brief fast main marketdata interface

  longdesc
*/

#ifndef MANAGER_H_
#define MANAGER_H_ 1

#include <map>
#include <string>
#include <queue>
#include <utility>

#include <boost/shared_ptr.hpp>
#include <boost/weak_ptr.hpp>
#include <boost/thread.hpp>

#include <quickfast/Messages/Message.h>
#include <quickfast/Messages/MessageAccessor.h>

#include "market_data/market_data.h"
#include "maret_data/fast/instrument.h"

class FastMarketDataManager : public MarketData {
public:
  virtual std::list<boost::weak_ptr<Instrument> > instruments() const;

  virtual boost::weak_ptr<Instrument> find(std::string id, std::string src) const;
  virtual boost::weak_ptr<Instrument> find_by_symbol(std::string sym) const;

  virtual std::size_t count_instruments() const;

  void on_message(boost::shared_ptr<QuickFAST::Messages::Message> msg);

  void start();
  void stop();

private:
  typedef std::map<std::string,boost::shared_ptr<FastInstrument> > InstrumentMap;
  InstrumentMap instruments;

  bool running;
  bool done_reading;

  volatile int data_ready;
  boost::condition_variable wait_cond;
  boost::mutex incoming_mutex;
  std::queue<boost::shared_ptr<QuickFAST::Messages::Message> > incoming;

  boost::shared_ptr<boost::thread> update_thread;
  void update_thread_proc();

  std::string make_hash(std::string id,std::string src);
  std::string make_hash(const QuickFAST::Messages::MessageAccessor& msg);

  void process(const QuickFAST::Messages::MessageAccessor& msg);

  void process_securitylist_entry(const QuickFAST::Messages::MessageAccessor& msg);
  void process_incrementals_entry(const QuickFAST::Messages::MessageAccessor& msg);

  FastInstrument& new_instrument(const QuickFAST::Messages::MessageAccessor& msg);
};

#endif // MANAGER_H_
