/** :mode=c++:indentSize=2:noTabs=true:tabSize=2:
  @brief UMDF message implementation

  longdesc
*/

#include "message.h"

Message::Message() : hdr_seq_num(-1), hdr_num_chunks(-1), total_size(0) {
}

Message::Message(const Packet& p) :
    hdr_seq_num(p.seqnum()), hdr_num_chunks(p.num_chunks()),total_size(0) {
  add(p);
}

static bool sort_helper(const Packet& first, const Packet& second) {
  return first.seqnum()<second.seqnum();
}

void Message::add(const Packet& p) {
  if(packets.size()==0) {
    hdr_seq_num=p.seqnum();
    hdr_num_chunks=p.num_chunks();
  }

  if(complete()) throw 1; // TODO: error
  if(p.seqnum()!=hdr_seq_num) throw 1; //TODO: error
  packets.push_back(p);
  total_size+=p.size();
  /*packets.sort([](const Packet& first,const Packet& second) {
      return first.seqnum()<second.seqnum();
  });*/
  sort(packets.begin(),packets.end(),&sort_helper);
}

int Message::read(void* dest,int offset,int len) const {
  if(!complete()) return 0;
  if(offset>=total_size) return 0;

  unsigned int total_to_read=total_size-offset<len?total_size-offset:len;

  // first find starting packet and starting offset
  unsigned int start_packet=0;
  unsigned int start_offset=0;
  for(int accum=0;accum<offset;
      accum+=packets[start_packet].size(),start_packet++) {
    if(accum+packets[start_packet].size()
        >offset-packets[start_packet].size()) {
      start_offset=offset-accum; break;
    }
  }

  // read until end
  unsigned int bytes_read=0;
  while(bytes_read<total_to_read && start_packet<packets.size()) {
    int read_amt=total_to_read-bytes_read<packets[start_packet].size()?
      total_to_read-bytes_read:packets[start_packet].size();
    bytes_read+=packets[start_packet++].read(
      dest+bytes_read,start_offset,read_amt);
    start_offset=0;
  }

  return bytes_read;
}

