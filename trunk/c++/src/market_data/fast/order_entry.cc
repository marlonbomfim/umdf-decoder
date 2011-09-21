/** :mode=c++:indentSize=2:noTabs=true:tabSize=2:
  @brief briefdesc

  longdesc
*/

#include "order_entry.h"

#include "fast_util.h"
#include "fast_protocol.h"

using std::string;

using boost::posix_time::ptime;

using QuickFAST::Messages::MessageAccessor;

FastOrderEntry::FastOrderEntry(const MessageAccessor& grp,FastBook& in_book) :
    book(in_book) {
  update_first(grp);
  update(grp);
}

void FastOrderEntry::update_first(const MessageAccessor& grp) {
  order_id=get_string(grp,fields::kOrderID);
}

void FastOrderEntry::update(const MessageAccessor& grp) {
  order_price=get_double(grp,fields::kPrice);
  order_quantity=get_double(grp,fields::kQuantity);

  order_date=bvmf_date_to_posix(get_int(grp,fields::kDate),get_int(grp,fields::kTime));

  order_pos=static_cast<int>(get_int(grp,fields::kPos));

  buy_broker=get_string(grp,fields::kBuyer);
  sell_broker=get_string(grp,fields::kSeller);
}

string FastOrderEntry::id() const {
  return order_id;
}

int FastOrderEntry::pos() const {
  return order_pos;
}

double FastOrderEntry::price() const {
  return order_price;
}

double FastOrderEntry::qty() const {
  return order_quantity;
}

ptime FastOrderEntry::date() const {
  return order_date;
}

