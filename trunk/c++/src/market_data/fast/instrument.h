/** :mode=c++:indentSize=2:noTabs=true:tabSize=2:
  @brief briefdesc

  longdesc
*/

#ifndef MARKET_DATA_FAST_INSTRUMENT_H_
#define MARKET_DATA_FAST_INSTRUMENT_H_ 1

#include <string>

#include <boost/shared_ptr.hpp>

#include "market_data/instrument.h"

#include "book.h"
#include "quickfast.h"

class FastInstrument : public Instrument {
public:
  virtual std::string id() const { return sec_id; }
  virtual std::string source() const { return sec_source; }

  virtual std::string symbol() const { return str_symbol; }

  virtual std::string exchange() const { return sec_exchange; }

  virtual const Book& book() const { return order_book; }

  FastInstrument(const QuickFAST::Messages::MessageAccessor& info);

  void process(const QuickFAST::Messages::MessageAccessor& msg);
  void process_incremental(
    boost::shared_ptr<QuickFAST::Messages::Message> msg_ptr,
    const QuickFAST::Messages::MessageAccessor& msg);
  void process_update(const QuickFAST::Messages::MessageAccessor& msg);

private:

  FastBook order_book;

  std::string sec_id,sec_source,sec_exchange,str_symbol;
};

#endif // MARKET_DATA_FAST_INSTRUMENT_H_

