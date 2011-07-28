/** :mode=c++:indentSize=2:noTabs=true:tabSize=2:
  @brief UMDF message aggregator

  longdesc
*/

#ifndef AGGREGATOR_H_
#define AGGREGATOR_H_ 1

#include <list>

#include <boost/thread.hpp>
#include <boost/date_time/posix_time/posix_time_types.hpp>

struct MessageListener {
  virtual void on_message(Message,Aggregator&)=0;
};

class Aggregator {
public:
  void add_listener(MessageListener& obj) { hooks.push_back(&obj); }
  void remove_listener(MessageListener& obj) { hooks.remove(&obj); }

  void start(UdpQueue& q);
  void stop();

  void reset(int new_seqnum);

private:
  std::unordered_map<int,Message> backlog;
  std::list<MessageListener*> hooks;

  int curr_seqnum;

  enum { MAX_RECV_TIMEOUT = 1000 };
  boost::posix_time::ptime last_recv_time;

  boost::thread* read_thread;

  void process(UdpQueue& q);
  void process_msg(const Message& m);

  bool done_reading;
};

#endif // AGGREGATOR_H_
