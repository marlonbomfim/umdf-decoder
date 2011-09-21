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
using boost::shared_ptr;
using boost::mutex;

using QuickFAST::Messages::MessageAccessor;
using QuickFAST::Messages::Message;

list<weak_ptr<Instrument> > FastMarketDataManager::instruments() const {
  list<weak_ptr<Instrument> > out;
  for(InstrumentMap::const_iterator i=all_instruments.begin();i!=all_instruments.end();++i) {
    out.push_back((i->second));
  }
  return out;
}

weak_ptr<Instrument> FastMarketDataManager::find(string id,string src) const {
  InstrumentMap::const_iterator cur=all_instruments.find(make_hash(id,src));
  return cur!=all_instruments.end()?weak_ptr<Instrument>(cur->second):weak_ptr<Instrument>();
}

weak_ptr<Instrument> FastMarketDataManager::find_by_symbol(string sym) const {
  for(InstrumentMap::const_iterator i=all_instruments.begin();i!=all_instruments.end();++i) {
    if(i->second->symbol()==sym) return weak_ptr<Instrument>(i->second);
  }

  return weak_ptr<Instrument>();
}

size_t FastMarketDataManager::count_instruments() const {
  return all_instruments.size();
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

void FastMarketDataManager::process(shared_ptr<Message> msg_ptr) {
  std::string type=get_string(*msg_ptr,fields::kMsgType);

  if(type==messages::kSecurityList) {
    for_each_in_sequence(*msg_ptr,fields::kSecurityGroup,
      bind(&FastMarketDataManager::process_securitylist_entry,this,_1));
  } else if(type==messages::kNews) {
    //TODO: implement
  } else if(type==messages::kIncrementals) {
    for_each_in_sequence(*msg_ptr,fields::kEntriesGroup,
      bind(&FastMarketDataManager::process_incrementals_entry,this,msg_ptr,_1));
  } else {
    InstrumentMap::iterator cur=all_instruments.find(make_hash(*msg_ptr));
    if(cur!=all_instruments.end()) {
      cur->second->process(*msg_ptr);
    } else {
      new_instrument(*msg_ptr).process(*msg_ptr);
    }
  }
}

void FastMarketDataManager::process_securitylist_entry(const MessageAccessor& grp) {
  string code=get_string(grp,fields::kUpdateAction);
  if(code=="A") {
    new_instrument(grp).process_update(grp);
  } else if(code=="M") {
    InstrumentMap::iterator cur=all_instruments.find(make_hash(grp));
    if(cur!=all_instruments.end()) {
      cur->second->process_update(grp);
    } else {
      BOOST_THROW_EXCEPTION(BadFastTagData()<<FastTagName(fields::kSecurityID));
    }
  } else if(code=="D") {
    all_instruments.erase(make_hash(grp));
  } else {
    BOOST_THROW_EXCEPTION(BadFastTagData()
      <<FastTagName(fields::kUpdateAction));
  }
}

void FastMarketDataManager::process_incrementals_entry(
  shared_ptr<Message> msg_ptr,const MessageAccessor& grp) {
  InstrumentMap::iterator cur=all_instruments.find(make_hash(grp));
  if(cur!=all_instruments.end()) {
    cur->second->process_incremental(msg_ptr,grp);
  } else {
    new_instrument(grp).process_incremental(msg_ptr,grp);
  }
}

FastInstrument& FastMarketDataManager::new_instrument(
    const MessageAccessor& msg) {
  if(!all_instruments.insert(
      make_pair(
        make_hash(msg),shared_ptr<FastInstrument>(
          new FastInstrument(msg)))
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

