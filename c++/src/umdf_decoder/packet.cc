/** :mode=c++:indentSize=2:noTabs=true:tabSize=2:
  @brief Packet implementation

  longdesc
*/

#include "packet.h"

#include <cstdlib>
using std::memcpy;
#include <cstdint>
using std::uint16_t;
using std::uint32_t;

using boost::asio::buffer;
using boost::asio::buffer_size;
using boost::asio::buffer_cast;

Packet::Packet(const char* in, int size) {
  read_encoded_data(in,size);
}

Packet::Packet(const_buffer in) {
  read_encoded_data(buffer_cast<const void*>(in),buffer_size(in));
}

Packet::Packet(const Packet& in) {
  msg_len=in.msg_len;
  hdr_cur_chunk=in.hdr_cur_chunk;
  hdr_seq_num=in.hdr_seq_num;
  hdr_num_chunks=in.hdr_num_chunks;

  msg_data=new char[msg_len];
  memcpy(msg_data,in.msg_data,msg_len);
}

Packet& Packet::operator=(const Packet& in) {
  msg_len=in.msg_len;
  hdr_cur_chunk=in.hdr_cur_chunk;
  hdr_seq_num=in.hdr_seq_num;
  hdr_num_chunks=in.hdr_num_chunks;

  msg_data=new char[msg_len];
  memcpy(msg_data,in.msg_data,msg_len);
}

int Packet::read(void* dest,int offset,int len) const {
  if(offset>len) return 0;
  int n=msg_len-offset<len?msg_len-offset:len;
  memcpy(dest,msg_data+offset,n);
  return n;
}

void Packet::read_encoded_data(const void* p, int size) {
  if(size<HEADER_SIZE) throw 1; // TODO: error

  hdr_seq_num=0xffffffff&*static_cast<const uint32_t*>(p+SEQNUM_OFFSET);
  hdr_num_chunks=0xffff&*static_cast<const uint16_t*>(p+NUMCHUNKS_OFFSET);
  hdr_cur_chunk=0xffff&*static_cast<const uint16_t*>(p+CURCHUNK_OFFSET);
  hdr_length=0xffff&*static_cast<const uint16_t*>(p+LENGTH_OFFSET);

  msg_data=new char[hdr_length];
  memcpy(msg_data,p+DATA_OFFSET,hdr_length);
}

