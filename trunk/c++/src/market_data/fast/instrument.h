/** :mode=c++:indentSize=2:noTabs=true:tabSize=2:
  @brief briefdesc

  longdesc
*/

#ifndef INSTRUMENT_H_
#define INSTRUMENT_H_ 1

#include <string>

#include <quickfast/Messages/MessageAccessor.h>

#include "market_data/instrument.h"

class FastInstrument : public Instrument {
public:
  virtual std::string id() const { return sec_id; }
  virtual std::string source() const { return sec_source; }

  virtual std::string symbol() const { return str_symbol; }

  virtual std::string exchange() const { return sec_exchange; }

  virtual Book& book() const { return order_book; }

  FastInstrument(QuickFAST::Messages::MessageAccessor& info);

  void process(const QuickFAST::Messages::MessageAccessor& msg);
  void process_incremental(const QuickFAST::Messages::MessageAccessor& msg);
  void process_update(const QuickFAST::Messages::MessageAccessor& msg);

private:

  FastBook order_book;

  std::string sec_id,sec_source,sec_exchange,str_symbol;
};

#endif // INSTRUMENT_H_

