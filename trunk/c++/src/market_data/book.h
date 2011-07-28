/** :mode=c++:indentSize=2:noTabs=true:tabSize=2:
  @brief order book interface

  longdesc
*/

#ifndef BOOK_H_
#define BOOK_H_ 1

struct Book {
  virtual std::list<OrderEntry> bids() const=0;
  virtual std::list<OrderEntry> offers() const=0;

  virtual OrderEntry* top_bid() const=0;
  virtual OrderEntry* top_offer() const=0;

  virtual int bid_count() const=0;
  virtual int offer_count() const=0;
};

#endif // BOOK_H_
