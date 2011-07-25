/** :mode=c++:indentSize=2:noTabs=true:tabSize=2:
  @brief udp queue implementation

  longdesc
*/

#include "udp_queue.h"

#include <utility>

#include <boost/exception.hpp>

using std::string;
using std::make_pair;
using std::bind;

using boost::system::error_code;

using namespace boost::asio;

UdpQueue::UdpQueue(string ip, int port) : io(),buf(),
    socket(io,ip::udp::endpoint(port)),data_ready=0 {
  socket.set_option(ip::multicast::join_group(ip));
  start_read();
}

void UdpQueue::start_read() {
  async_read(socket,buf,transfer_at_least(buffer_size_helper(buf)),
    bind(&on_read,this,_1));
}

void UdpQueue::on_read(error_code e) {
  if(e) {
    stop();
    return;
  }

  try {

    UmdfPacket packet(buf);

    auto iter=incoming.find(packet.seqnum());
    if(iter==incoming.end()) {
      iter=incoming.insert(
        make_pair(packet.seqnum(),UmdfMessage(packet))).first;
    } else {
      iter->add(packet);
    }

    if(iter->complete()) {
      {
        lock_guard<mutex> lock(outgoing_mutex);
        outgoing.push(*iter);
      }

      incoming.remove(iter);

      data_ready++;
      has_data.notify_one();
    }

  } catch(boost::exception e) {
  } catch(std::exception e) {
  }

  start_read();
}

void UmdfMessage read() {
  unique_lock<mutex> lock(outgiong_mutex);
  while(data_ready<=0) wait_cond.wait(lock);

  data_ready--;
  return outgoing.pop();
}

bool read(UmdfMessage& out,system_time timeout) {
  unique_lock<mutex> lock(outgiong_mutex);
  while(data_ready<=0) {
    if(!wait_cond.timed_wait(lock,timeout)) return false;
  }

  data_ready--;
  out=outgoing.pop();

  return true;
}

