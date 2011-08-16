/** :mode=c++:indentSize=2:noTabs=true:tabSize=2:
  @brief Packet implementation

  longdesc
*/

#include "packet.h"

#include <algorithm>
using std::copy;
//#include <cstdint>
//using std::uint16_t;
//using std::uint32_t;
#include <stdint.h>
#include <string>
using std::string;
#include <iostream>
#include <iomanip>
using std::cout;
using std::endl;
using std::hex;

#include "platform.h"
#if PLATFORM_LINUX
#include <sys/types.h>
#include <netinet/in.h>
#elif PLATFORM_WIN
#include <winsock2.h>
#endif

#include "common.h"

using boost::asio::const_buffer;
using boost::asio::buffer;
using boost::asio::buffer_size;
using boost::asio::buffer_cast;

Packet::Packet(const char* in, int size) : msg_data(0) {
  read_encoded_data(in,size);
}

Packet::Packet(const_buffer in) : msg_data(0) {
  read_encoded_data(buffer_cast<const void*>(in),buffer_size(in));
}

Packet::Packet(const Packet& in) {
  msg_len=in.msg_len;
  hdr_cur_chunk=in.hdr_cur_chunk;
  hdr_seq_num=in.hdr_seq_num;
  hdr_num_chunks=in.hdr_num_chunks;

  msg_data.reset(new char[msg_len]);
  copy(in.msg_data.get(),in.msg_data.get()+msg_len,msg_data.get());
}

Packet& Packet::operator=(const Packet& in) {
  msg_len=in.msg_len;
  hdr_cur_chunk=in.hdr_cur_chunk;
  hdr_seq_num=in.hdr_seq_num;
  hdr_num_chunks=in.hdr_num_chunks;

  msg_data.reset(new char[msg_len]);
  copy(in.msg_data.get(),in.msg_data.get()+msg_len,msg_data.get());

  return *this;
}

int Packet::read(void* dest,int offset,int len) const {
  if(offset>len) return 0;
  char* p=static_cast<char*>(dest);
  int n=msg_len-offset<len?msg_len-offset:len;
  copy(msg_data.get()+offset,msg_data.get()+offset+n,p);
  return n;
}

void Packet::read_encoded_data(const void* in_buf, int size) {
  if(size<HEADER_SIZE) throw 1; // TODO: error
  const char* p=static_cast<const char*>(in_buf);

  hdr_seq_num=ntohl(*reinterpret_cast<const uint32_t*>(p+SEQNUM_OFFSET));
  hdr_num_chunks=ntohs(*reinterpret_cast<const uint16_t*>(p+NUMCHUNKS_OFFSET));
  hdr_cur_chunk=ntohs(*reinterpret_cast<const uint16_t*>(p+CURCHUNK_OFFSET));
  msg_len=ntohs(*reinterpret_cast<const uint16_t*>(p+LENGTH_OFFSET));

  int amt_to_copy=size<msg_len?size:msg_len;

  msg_data.reset(new char[msg_len]);
  copy(p+DATA_OFFSET,p+DATA_OFFSET+amt_to_copy,msg_data.get());
}

