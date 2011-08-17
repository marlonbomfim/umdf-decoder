/** :mode=c++:indentSize=2:noTabs=true:tabSize=2:
  @brief UMDF message aggregator implementation

  longdesc
*/

#include "aggregator.h"

#include <utility>
#include <iostream>

#include "common.h"

using std::cout;
using std::endl;
using boost::thread;
using boost::posix_time::microsec_clock;
using boost::posix_time::milliseconds;
//using std::bind;
using boost::bind;
using std::make_pair;
using boost::diagnostic_information;

Aggregator::Aggregator() :
    curr_seqnum(-1), running(false), done_reading(false) {
}

void Aggregator::thread_helper(Aggregator* a,UdpQueue* q) {
  try {
    a->running=true;
    a->process(*q);
  } catch(boost::exception& e) {
    cout<<"[Aggregator::thread_helper]: caught boost::exception: "
      <<diagnostic_information(e)<<endl;
  } catch(std::exception& e) {
    cout<<"[Aggregator::thread_helper]: caught std::exception: "
      <<diagnostic_information(e)<<endl;
  }

  a->running=false;
}

void Aggregator::start(UdpQueue& q) {
  stop();
  /*read_thread=new thread([=]() {
      try {
        running=true;
        this->process(q);
      } catch(boost::exception e) {
      } catch(std::exception e) {
      }

      running=false;
  });*/
  read_thread.reset(new thread(bind(&thread_helper,this,&q)));
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
  last_recv_time=microsec_clock::local_time();

  Message msg;

  while(!done_reading) {
    std::map<int,Message>::iterator iter;
    while((iter=backlog.find(curr_seqnum))!=backlog.end()) {
      process_msg(iter->second);
      last_recv_time=microsec_clock::local_time();
    }

    msg=q.read();

    //if(q.read(msg,MAX_RECV_TIMEOUT)) {
      if(curr_seqnum<0) curr_seqnum=msg.seqnum();

      TRACE(msg.seqnum());

      if(msg.seqnum()==curr_seqnum) {
        process_msg(msg);
        last_recv_time=microsec_clock::local_time();
      } else if(msg.seqnum()>curr_seqnum) {
        backlog.insert(make_pair(msg.seqnum(),msg));
      }
    //}

    if(microsec_clock::local_time()-last_recv_time>milliseconds(MAX_RECV_TIMEOUT)) {
      //TODO: replay stream request here
    }
  }
}

void Aggregator::process_msg(const Message& m) {
  curr_seqnum++;

  //for_each(hooks.begin(),hooks.end(),[=](callback_type f) { f(m); });
  for(std::vector<callback_type>::iterator i=hooks.begin();i!=hooks.end();i++) {
    (*i)(m,*this);
  }
}

