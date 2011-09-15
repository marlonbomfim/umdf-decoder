/** :mode=c++:indentSize=2:noTabs=true:tabSize=2:
  @brief fast marketdata manager implementation

  longdesc
*/

#include "manager.h"

#include <algorithm>
#include <iostream>

#include <boost/bind.hpp>

#include "fast_errors.h"
#include "fast_util.h"
#include "fast_protocol.h"

using std::string;
using std::list;
using std::size_t;
using std::make_pair;
using std::cerr;

using boost::lock_guard;
using boost::bind;
using boost::thread;
using boost::unique_lock;
using boost::diagnostic_information;
using boost::weak_ptr;

using QuickFAST::Messages::MessageAccessor;

list<weak_ptr<Instrument> > FastMarketDataManager::instruments() const {
  list<weak_ptr<Instrument> > out;
  for_each(
    instruments.begin(),instruments.end(),
    bind(list<weak_ptr<Instrument> >::push_back,out,_1));
  return out;
}

weak_ptr<Instrument> FastMarketDataManager::find(string id,string src) const {
  InstrumentMap::iterator cur=instruments.find(make_hash(id,src));
  return cur!=instruments.end()?weak_ptr<Instrument>(cur->second):weak_ptr<Instrument>();
}

weak_ptr<Instrument> FastMarketDataManager::find_by_symbol(string sym) const {
  for(InstrumentMap::iterator i=instruments.begin();i!=instruments.end();++i) {
    if(cur->second.symbol()==sym) return weak_ptr<Instrument>(cur->second);
  }

  return weak_ptr();
}

size_t FastMarketDataManager::count_instruments() const {
  return instruments.size();
}

void FastMarketDataManager::on_message(boost::shared_ptr<Message> msg) {
  {
    lock_guard<mutex> lock(incoming_mutex);
    incoming.push(msg);
  }

  data_ready++;
  wait_cond.notify_one();
}

void FastMarketDataManager::start() {
  stop();
  update_thread.reset(new thread(
    bind(&FastMarketDataManager::update_thread_proc,this)));
}

void FastMarketDataManager::stop() {
  if(running) {
    done_reading=true;
    update_thread->join();
  }
}

void FastMarketDataManager::update_thread_proc() {
  try {
    running=true;
    unique_lock<mutex> lock(incoming_mutex);
    while(!done_reading) {
      while(data_ready<=0) wait_cond.wait(lock);

      data_ready--;
      process(incoming.front());
      incoming.pop();
    }
  } catch(boost::exception& e) {
    cerr<<diagnostic_information(e);
  } catch(std::exception& e) {
    cerr<<diagnostic_information(e);
  }

  running=false;
}

void FastMarketDataManager::process(const MessageAccessor& msg) {
  std::string type=get_string(msg,fields::kMsgType);

  if(type==messages::kSecurityList) {
    for_each_in_sequence(msg,fields::kSecurityGroup,
      bind(&FastMarketDataManager::process_securitylist_entry,this,_1));
  } else if(type==messages::kNews) {
    //TODO: implement
  } else if(type==messages::kIncrementals) {
    for_each_in_sequence(msg,fields::kEntriesGroup,
      bind(&FastMarketDataManager::process_incrementals_entry,this,_1));
  } else {
    InstrumentMap::iterator cur=instruments.find(make_hash(msg));
    if(cur!=instruments.end()) {
      cur->second->process(msg);
    } else {
      new_instrument(msg).process(msg);
    }
  }
}

void FastMarketDataManager::process_securitylist_entry(const MessageAccessor& msg) {
  string code=get_string(grp,fields::kUpdateAction);
  if(code=="A") {
    new_instrument(grp).process_update(grp);
  } else if(code=="M") {
    InstrumentMap::iterator cur=instruments.find(make_hash(grp));
    if(cur!=instruments.end()) {
      cur->second->process_update(grp);
    } else {
      BOOST_THROW_EXCEPTION(BadFastTagData()<<FastTagName(fields::kSecurityID));
    }
  } else if(code=="D") {
    instruments.erase(make_hash(grp));
  } else {
    BOOST_THROW_EXCEPTION(BadFastTagData()
      <<FastTagName(fields::kUpdateAction)
      <<code);
  }
}

void FastMarketDataManager::process_incrementals_entry(const MessageAccessor& grp) {
  InstrumentMap::iterator cur=instruments.find(make_hash(grp));
  if(cur!=instruments.end()) {
    cur->second->process_incremental(grp);
  } else {
    new_instrument(grp).process_incremental(grp);
  }
}

FastInstrument& FastMarketDataManager::new_instrument(
    const MessageAccessor& msg) {
  if(!instruments.insert(
      make_pair(
        make_hash(msg),shared_ptr<FastInstrument>(
          new FastInstrument(msg,*this)))
      ).second) {
    BOOST_THROW_EXCEPTION(BadFastTagData()<<FastTagName(fields::kSecurityID));
  }
}

string FastMarketDataManager::make_hash(string id,string src) {
  return id+":"+src;
}

string FastMarketDataManager::make_hash(const MessageAccessor& msg) {
  return make_hash(
    get_string(msg,fields::kSecurityID),
    get_string(msg,fields::kSecuritySource));
}

