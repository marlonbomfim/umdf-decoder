/** :mode=c++:indentSize=2:noTabs=true:tabSize=2:
  @brief fast marketdata manager implementation

  longdesc
*/

#include "manager.h"

#include <boost/exception/all.hpp>

using std::string;

using boost::read_write_mutex;
using boost::lock_guard;
using boost::bind;
using boost::thread;
using boost::unique_lock;
using boost::diagnostic_information;

FastMarketDataManager::FastMarketDataManager() {
}

std::list<Instrument> FastMarketDataManager::instruments() const {
  read_write_mutex::scoped_read_lock lock(access_mutex);
}

Instrument* FastMarketDataManager::find(string id,string src) const {
  read_write_mutex::scoped_read_lock lock(access_mutex);
}

Instrument* FastMarketDataManager::find_by_symbol(string sym) const {
  read_write_mutex::scoped_read_lock lock(access_mutex);
}

int FastMarketDataManager::count_instruments() const {
  read_write_mutex::scoped_read_lock lock(access_mutex);
  return instruments.size();
}

void FastMarketDataManager::on_message() {
  {
    lock_guard<mutex> lock(incoming_mutex);
    incoming.push();
  }

  data_ready++;
  wait_cond.notify_one();
}

void FastMarketDataManager::start() {
  stop();
  update_thread.reset(new thread(
    bind(&FastMarketDataManager::update_thread_helper,this)));
}

void FastMarketDataManager::stop() {
  if(running) {
    done_reading=true;
    update_thread->join();
  }
}

void FastMarketDataManager::update_thread_helper() {
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
  } catch(std::exception& e) {
  }

  running=false;
}
