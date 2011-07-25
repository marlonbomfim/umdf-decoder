/** :mode=c++:indentSize=2:noTabs=true:tabSize=2:
  @brief udp queue interface

  longdesc
*/

#ifndef UDP_QUEUE_H_
#define UDP_QUEUE_H_ 1

#include <string>

#include <boost/asio/asio.hpp>
#include <boost/system/error_code.hpp>

class UdpQueue {
public:
  UdpQueue(std::string ip, int port);

  UmdfMessage read();
  bool read(UmdfMessage& out, boost::system_time timeout);

private:
  boost::asio::io_service io;
  enum { MAX_UDPPACKET_SIZE = 1310 };
  boost::asio::buffer buf;
  ip::udp::socket socket;
  void start_read();
  void on_read(boost::system::error_code);

  std::unordered_map<int,UmdfMessage> incoming;

  std::queue<UmdfMessage> outgoing;
  boost::mutex outgoing_mutex;

  boost::condition_variable wait_cond;
  int data_ready;
};

#endif // UDP_QUEUE_H_
