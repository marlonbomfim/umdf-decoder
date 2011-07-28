/** :mode=c++:indentSize=2:noTabs=true:tabSize=2:
  @brief UMDF packet

  longdesc
*/

#ifndef PACKET_H_
#define PACKET_H_ 1

#include <utility>

#include <boost/asio/buffer.hpp>

class Packet {
public:
  Packet(const char* in, int size);
  Packet(boost::asio::const_buffer in);

  Packet(const Packet& in);
  Packet& operator=(const Packet& in);

  int cur_chunk() const { return hdr_cur_chunk; }
  std::size_t length() const { return msg_len; }
  int seqnum() const { return hdr_seq_num; }
  int num_chunks() const { return hdr_num_chunks; }

  int read(void* dest,int offset,int len) const;

private:
  void read_encoded_data(const void* p, int size);
  std::unique_ptr<char[]> msg_data;
  int msg_len;

  int hdr_cur_chunk;
  int hdr_seq_num;
  int hdr_num_chunks;
};

#endif // PACKET_H_

