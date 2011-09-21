/** :mode=c++:indentSize=2:noTabs=true:tabSize=2:
  @brief briefdesc

  longdesc
*/

#include "book.h"

#include <algorithm>

#include <boost/bind.hpp>

#include "fast_util.h"
#include "fast_protocol.h"

using std::string;
using std::vector;
using std::find_if;
using std::sort;
using std::list;
using std::make_pair;

using boost::bind;
using boost::shared_ptr;
using boost::weak_ptr;

using QuickFAST::Messages::MessageAccessor;
using QuickFAST::Messages::Message;

const char* FastBook::kBid="0";
const char* FastBook::kOffer="1";
const char* FastBook::kNew="0";
const char* FastBook::kChange="1";
const char* FastBook::kDelete="2";
const char* FastBook::kDeleteThru="3";
const char* FastBook::kDeleteFrom="4";
const char* FastBook::kOverlay="5";

FastBook::FastBook(FastInstrument& in_instrument) :
    instrument(in_instrument) {
}

void FastBook::process_incremental(
    shared_ptr<Message> msg,
    const MessageAccessor& grp) {
  if(last_seqnum==-1) {
    backlog.push(make_pair(msg,&grp));
    return;
  }

  while(!backlog.empty()) {
    process_entry(*(backlog.front().second),
      get_string(*(backlog.front().second),
        fields::kIncrementalUpdateAction).c_str());
    backlog.pop();
  }

  int seq=get_int(grp,fields::kIncrementalSeqnum);
  if(seq<last_seqnum) return;
  last_seqnum=seq;

  process_entry(grp,get_string(grp,fields::kIncrementalUpdateAction).c_str());
}

void FastBook::process_snapshot(const MessageAccessor& grp) {
  int seq=get_int(grp,fields::kSnapshotSeqnum);
  if(seq<last_seqnum) return;
  last_seqnum=seq;

  bids_queue.clear(); offers_queue.clear();
  for_each_in_sequence(grp,fields::kEntriesGroup,
    bind(&FastBook::process_entry,this,_1,kNew));
}

void FastBook::process_entry(const MessageAccessor& grp,const char* op) {
  string type=get_string(grp,fields::kEntryType);

  if(type==kBid) {
    process_bid(grp,op);
  } else if(type==kOffer) {
    process_offer(grp,op);
  }

  //TODO: process more things!
}

void FastBook::process_bid(const MessageAccessor& grp,const char* op) {
  if(op==kNew) {
    add_order(bids_queue,grp);
  } else if(op==kChange) {
    update_order(bids_queue,grp);
  } else if(op==kDelete) {
    delete_order(bids_queue,grp);
  } else if(op==kDeleteThru) {
    delete_orders_thru(bids_queue,grp);
  } else if(op==kDeleteFrom) {
    delete_orders_from(bids_queue,grp);
  } else if(op==kOverlay) {
    //TODO: implement
  } else {
    BOOST_THROW_EXCEPTION(BadFastTagData()
      <<FastTagName(fields::kIncrementalUpdateAction));
  }
}

void FastBook::process_offer(const MessageAccessor& grp,const char* op) {
  if(op==kNew) {
    add_order(offers_queue,grp);
  } else if(op==kChange) {
    update_order(offers_queue,grp);
  } else if(op==kDelete) {
    delete_order(offers_queue,grp);
  } else if(op==kDeleteThru) {
    delete_orders_thru(offers_queue,grp);
  } else if(op==kDeleteFrom) {
    delete_orders_from(offers_queue,grp);
  } else if(op==kOverlay) {
    //TODO: implement
  } else {
    BOOST_THROW_EXCEPTION(BadFastTagData()
      <<FastTagName(fields::kIncrementalUpdateAction));
  }
}

void FastBook::add_order(EntryQueue& q,const MessageAccessor& grp) {
  q.push_back(shared_ptr<FastOrderEntry>(new FastOrderEntry(grp,*this)));
  sort(q.begin(),q.end());
}

void FastBook::delete_order(EntryQueue& q,const MessageAccessor& grp) {
  string id=get_string(grp,fields::kOrderID);
  for(EntryQueue::iterator i=q.begin();i!=q.end();++i) {
    if((*i)->id()==id) {
      q.erase(i);
      return;
    }
  }
  BOOST_THROW_EXCEPTION(BadFastTagData()
    <<FastTagName(fields::kOrderID));
}

void FastBook::update_order(EntryQueue& q,const MessageAccessor& grp) {
  string id=get_string(grp,fields::kOrderID);
  for(EntryQueue::iterator i=q.begin();i!=q.end();++i) {
    if((*i)->id()==id) {
      (*i)->update(grp);
      sort(q.begin(),q.end());
      return;
    }
  }
  BOOST_THROW_EXCEPTION(BadFastTagData()
    <<FastTagName(fields::kOrderID));
}

void FastBook::delete_orders_thru(EntryQueue& q,const MessageAccessor& grp) {
  string id=get_string(grp,fields::kOrderID);
  for(EntryQueue::iterator i=q.begin();i!=q.end();++i) {
    i=q.erase(i);
    if((*i)->id()==id) {
      return;
    }
  }
  BOOST_THROW_EXCEPTION(BadFastTagData()
    <<FastTagName(fields::kOrderID));
}

void FastBook::delete_orders_from(EntryQueue& q,const MessageAccessor& grp) {
  string id=get_string(grp,fields::kOrderID);
  bool found=false;
  for(EntryQueue::iterator i=q.begin();i!=q.end();++i) {
    if((*i)->id()==id) {
      found=true;
    }
    if(found) i=q.erase(i);
  }
  if(!found) {
    BOOST_THROW_EXCEPTION(BadFastTagData()
    <<FastTagName(fields::kOrderID));
  }
}

list<weak_ptr<OrderEntry> > FastBook::bids() const {
  list<weak_ptr<OrderEntry> > out;
  for_each(
    bids_queue.begin(),bids_queue.end(),
    bind(&list<weak_ptr<OrderEntry> >::push_back,out,_1));
  return out;
}

list<weak_ptr<OrderEntry> > FastBook::offers() const {
  list<weak_ptr<OrderEntry> > out;
  for_each(
    offers_queue.begin(),offers_queue.end(),
    bind(&list<weak_ptr<OrderEntry> >::push_back,out,_1));
  return out;
}

weak_ptr<OrderEntry> FastBook::top_bid() const {
  return bids_queue.empty()?weak_ptr<OrderEntry>():weak_ptr<OrderEntry>(bids_queue.front());
}

weak_ptr<OrderEntry> FastBook::top_offer() const {
  return offers_queue.empty()?weak_ptr<OrderEntry>():weak_ptr<OrderEntry>(offers_queue.front());
}

size_t FastBook::bids_count() const {
  return bids_queue.size();
}

size_t FastBook::offers_count() const {
  return offers_queue.size();
}

