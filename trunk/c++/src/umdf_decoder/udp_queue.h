/** :mode=c++:indentSize=2:noTabs=true:tabSize=2:
  @brief udp queue interface

  longdesc
*/

#ifndef UDP_QUEUE_H_
#define UDP_QUEUE_H_ 1

#include <string>
#include <map>
#include <queue>

#include <boost/shared_ptr.hpp>
#include <boost/asio.hpp>
#include <boost/system/error_code.hpp>
#include <boost/thread.hpp>
#include <boost/date_time/posix_time/posix_time_types.hpp>

#include "message.h"

class UdpQueue {
public:
  UdpQueue(std::string ip, int port);

  Message read();
  bool read(Message& out, boost::system_time timeout);

private:
  boost::asio::io_service io;
  boost::shared_ptr<boost::thread> io_thread;

  enum { MAX_UDPPACKET_SIZE = 1310 };
  char buf[MAX_UDPPACKET_SIZE];

  boost::asio::ip::udp::socket socket;
  void start_read();
  void on_read(boost::system::error_code,std::size_t bytes_read);

  //std::unordered_map<int,Message> incoming;
  std::map<int,Message> incoming;

  std::queue<Message> outgoing;
  boost::mutex outgoing_mutex;

  boost::condition_variable wait_cond;
  volatile int data_ready;
};

#endif // UDP_QUEUE_H_
