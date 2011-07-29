/** :mode=c++:indentSize=2:noTabs=true:tabSize=2:
  @brief UMDF message aggregator

  longdesc
*/

#ifndef AGGREGATOR_H_
#define AGGREGATOR_H_ 1

#include <list>
#include <functional>

#include <boost/thread.hpp>
#include <boost/date_time/posix_time/posix_time_types.hpp>

class Aggregator {
public:
  typedef std::function<void(Message,Aggregator&> callback_type;
  void add_listener(callback_type f) { hooks.push_back(f); }
  void remove_listener(callback_type f) { hooks.remove(f); }

  void start(UdpQueue& q);
  void stop();

  void reset(int new_seqnum);

private:
  std::unordered_map<int,Message> backlog;
  std::list<callback_type> hooks;

  int curr_seqnum;

  enum { MAX_RECV_TIMEOUT = 1000 };
  boost::posix_time::ptime last_recv_time;

  boost::thread* read_thread;

  void process(UdpQueue& q);
  void process_msg(const Message& m);

  bool done_reading;
};

#endif // AGGREGATOR_H_
