/** :mode=c++:indentSize=2:noTabs=true:tabSize=2:
  @brief order book interface

  longdesc
*/

#ifndef MARKET_DATA_BOOK_H_
#define MARKET_DATA_BOOK_H_ 1

#include <boost/weak_ptr.hpp>

#include "order_entry.h"

struct Book {
  virtual std::list<boost::weak_ptr<OrderEntry> > bids() const=0;
  virtual std::list<boost::weak_ptr<OrderEntry> > offers() const=0;

  virtual boost::weak_ptr<OrderEntry> top_bid() const=0;
  virtual boost::weak_ptr<OrderEntry> top_offer() const=0;

  virtual std::size_t bids_count() const=0;
  virtual std::size_t offers_count() const=0;
};

#endif // MARKET_DATA_BOOK_H_

