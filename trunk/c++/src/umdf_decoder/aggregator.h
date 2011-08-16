/** :mode=c++:indentSize=2:noTabs=true:tabSize=2:
  @brief UMDF message aggregator

  longdesc
*/

#ifndef AGGREGATOR_H_
#define AGGREGATOR_H_ 1

#include <vector>
//#include <functional>
#include <map>

#include <boost/thread.hpp>
#include <boost/date_time/posix_time/posix_time_types.hpp>
#include <boost/bind.hpp>
#include <boost/function.hpp>
#include <boost/signal.hpp>

#include "message.h"
#include "udp_queue.h"

class Aggregator {
public:
  //typedef std::function<void(Message,Aggregator&> callback_type;
  typedef boost::function<void(Message,Aggregator&)> callback_type;
  int add_listener(callback_type f) { hooks.push_back(f); return hooks.size(); }
  void remove_listener(int n) { hooks.erase(hooks.begin()+n); }

  void start(UdpQueue& q);
  void stop();

  void reset(int new_seqnum);

private:
  //std::unordered_map<int,Message> backlog;
  std::map<int,Message> backlog;
  std::vector<callback_type> hooks;

  int curr_seqnum;

  enum { MAX_RECV_TIMEOUT = 1000 };
  boost::posix_time::ptime last_recv_time;

  boost::shared_ptr<boost::thread> read_thread;
  bool running;

  void process(UdpQueue& q);
  void process_msg(const Message& m);

  bool done_reading;

  static void thread_helper(Aggregator*,UdpQueue*);
};

#endif // AGGREGATOR_H_
