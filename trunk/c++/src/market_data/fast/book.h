/** :mode=c++:indentSize=2:noTabs=true:tabSize=2:
  @brief briefdesc

  longdesc
*/

#ifndef BOOK_H_
#define BOOK_H_ 1

#include <vector>
#include <string>
#include <queue>

#include <boost/shared_ptr.hpp>
#include <boost/weak_ptr.hpp>

#include <quickfast/Messages/MessageAccessor.h>
#include <quickfast/Messages/FieldSet.h>

#include "market_data/book.h"
#include "order_entry.h"

class FastInstrument;

class FastBook : public Book {
public:
  virtual std::list<boost::weak_ptr<OrderEntry> > bids() const;
  virtual std::list<boost::weak_ptr<OrderEntry> > offers() const;

  virtual boost::weak_ptr<OrderEntry> top_bid() const;
  virtual boost::weak_ptr<OrderEntry> top_offer() const;

  virtual std::size_t bid_count() const;
  virtual std::size_t offer_count() const;

  FastBook(
    const QuickFAST::Messages::MessageAccessor& info,
    FastInstrument& in_instrument);

  void process_incremental(const QuickFAST::Messages::MessageAccessor& grp);
  void process_snapshot(const QuickFAST::Messages::MessageAccessor& grp);

private:
  typedef std::vector<boost::shared_ptr<FastOrderEntry> > EntryQueue;
  EntryQueue bids_queue,offers_queue;

  std::queue<QuickFAST::Messages::FieldSet> backlog;

  int last_seqnum;

  FastInstrument& instrument;

  void process_entry(
    const QuickFAST::Messages::MessageAccessor& grp,
    const char* op);

  void process_bid(
    const QuickFAST::Messages::MessageAccessor& grp,
    const char* op);
  void process_offer(
    const QuickFAST::Messages::MessageAccessor& grp,
    const char* op);

  void add_order(
    EntryQueue& q,
    const QuickFAST::Messages::MessageAccessor& grp);
  void update_order(
    EntryQueue& q,
    const QuickFAST::Messages::MessageAccessor& grp);
  void delete_order(
    EntryQueue& q,
    const QuickFAST::Messages::MessageAccessor& grp);
  void delete_orders_thru(
    EntryQueue& q,
    const QuickFAST::Messages::MessageAccessor& grp);
  void delete_orders_from(
    EntryQueue& q,
    const QuickFAST::Messages::MessageAccessor& grp);

  static const char* kBid="0";
  static const char* kOffer="1";

  static const char* kNew="0";
  static const char* kChange="1";
  static const char* kDelete="2";
  static const char* kDeleteThru="3";
  static const char* kDeleteFrom="4";
  static const char* kOverlay="5";
};

#endif // BOOK_H_
