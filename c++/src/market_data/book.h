/** :mode=c++:indentSize=2:noTabs=true:tabSize=2:
  @brief order book interface

  longdesc
*/

#ifndef BOOK_H_
#define BOOK_H_ 1

#include <boost/weak_ptr.hpp>

struct Book {
  virtual std::list<boost::weak_ptr<OrderEntry> > bids() const=0;
  virtual std::list<boost::weak_ptr<OrderEntry> > offers() const=0;

  virtual boost::weak_ptr<OrderEntry> top_bid() const=0;
  virtual boost::weak_ptr<OrderEntry> top_offer() const=0;

  virtual std::size_t bid_count() const=0;
  virtual std::size_t offer_count() const=0;
};

#endif // BOOK_H_

