/** :mode=c++:indentSize=2:noTabs=true:tabSize=2:
  @brief UMDF message aggregator implementation

  longdesc
*/

#include "aggregator.h"

using boost::thread;
using boost::microsec_clock::local_time;

void Aggregator::start(UdpQueue& q) {
  read_thread=new thread([=]() {
      try {
        running=true;
        this->process(q);
      } catch(boost::exception e) {
      } catch(std::exception e) {
      }

      running=false;
  });
}

void Aggregator::stop() {
  if(running) {
    done_reading=true;
    read_thread->join();
  }
}

void Aggregator::reset(int new_seqnum) {
  curr_seqnum=new_seqnum;
}

void Aggregator::process(UdpQueue& q) {
  last_recv_time=local_time();

  Message msg;

  while(!done_reading) {
    std::unordered_map<int,Message>::iterator iter;
    while((iter=backlog.find(curr_seqnum))!=backlog.end()) {
      process_msg(*iter);
      last_recv_time=local_time();
    }

    msg=q.read();

    //if(q.read(msg,MAX_RECV_TIMEOUT)) {
      if(curr_seqnum<0) curr_seqnum=msg.seqnum();

      if(msg.seqnum()==curr_seqnum) {
        process_msg(msg);
        last_recv_time=local_time();
      } else if(msg.seqnum()>curr_seqnum) {
        backlog.insert(make_pair(msg.seqnum(),msg));
      }
    //}

    if(local_time()-last_recv_time>MAX_RECV_TIMEOUT) {
      //TODO: replay stream request here
    }
  }
}

void Aggregator::process_msg(const Message& m) {
  curr_seqnum++;

  for_each(hooks.begin(),hooks.end(),[=](callback_type f) { f(m); });
}

