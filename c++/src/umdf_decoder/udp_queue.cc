/** :mode=c++:indentSize=2:noTabs=true:tabSize=2:
  @brief udp queue implementation

  longdesc
*/

#include "udp_queue.h"

#include <utility>
#include <iostream>

#include <boost/exception/all.hpp>

#include "common.h"

using std::string;
using std::make_pair;
using std::cout;
using std::endl;
//using std::bind;
using boost::bind;
//using namespace std::placeholders;
using boost::lock_guard;
using boost::mutex;
using boost::system_time;
using boost::unique_lock;
using boost::diagnostic_information;
using boost::thread;
using boost::system::error_code;

using namespace boost::asio;

UdpQueue::UdpQueue(string ip, int port) : io(),socket(io),data_ready(0) {
  socket.open(ip::udp::v4());
  socket.set_option(socket_base::reuse_address(true));
  socket.set_option(
    ip::multicast::join_group(
      ip::address::from_string(ip)));
  socket.bind(ip::udp::endpoint(ip::address_v4::any(),port));

  io_thread.reset(new thread(bind(&io_service::run,&io)));

  start_read();
}

void UdpQueue::start_read() {
  socket.async_receive(buffer(buf),bind(&UdpQueue::on_read,this,_1,_2));
}

void UdpQueue::on_read(error_code err,std::size_t bytes_read) {
  if(err) {
    cout<<"[UdpQueue::on_read]: error: "<<err<<endl;
    io.stop();
    return;
  }

  Packet packet(buffer(buf,bytes_read));

  std::map<int,Message>::iterator iter=incoming.find(packet.seqnum());
  if(iter==incoming.end()) {
    iter=incoming.insert(
      make_pair(packet.seqnum(),Message(packet))).first;
  } else {
    iter->second.add(packet);
  }

  if(iter->second.complete()) {
    {
      lock_guard<mutex> lock(outgoing_mutex);
      outgoing.push(iter->second);
    }

    incoming.erase(iter);

    data_ready++;
    wait_cond.notify_one();
  }

  start_read();
}

Message UdpQueue::read() {
  unique_lock<mutex> lock(outgoing_mutex);
  while(data_ready<=0) wait_cond.wait(lock);

  data_ready--;
  Message msg=outgoing.front();
  outgoing.pop();
  return msg;
}

bool UdpQueue::read(Message& out,system_time timeout) {
  unique_lock<mutex> lock(outgoing_mutex);
  while(data_ready<=0) {
    if(!wait_cond.timed_wait(lock,timeout)) return false;
  }

  data_ready--;
  out=outgoing.front();
  outgoing.pop();

  return true;
}

