/** :mode=c++:indentSize=2:noTabs=true:tabSize=2:
  @brief briefdesc

  longdesc
*/

#ifndef MARKET_DATA_FAST_ORDER_ENTRY_H_
#define MARKET_DATA_FAST_ORDER_ENTRY_H_ 1

#include "market_data/order_entry.h"

#include "quickfast.h"

class FastBook;

class FastOrderEntry : public OrderEntry {
public:
  virtual std::string id() const;

  virtual int pos() const;

  virtual double price() const;
  virtual double qty() const;

  virtual std::string broker() const;

  virtual boost::posix_time::ptime date() const;

  FastOrderEntry(const QuickFAST::Messages::MessageAccessor&,FastBook&);

  void update(const QuickFAST::Messages::MessageAccessor&);

private:
  std::string order_id;
  int order_pos;

  double order_price;
  double order_quantity;

  std::string buy_broker,sell_broker;

  boost::posix_time::ptime order_date;

  FastBook& book;

  void update_first(const QuickFAST::Messages::MessageAccessor&);
};

#endif // MARKET_DATA_FAST_ORDER_ENTRY_H_

