/** :mode=c++:indentSize=2:noTabs=true:tabSize=2:
  @brief UMDF message

  longdesc
*/

#ifndef MESSAGE_H_
#define MESSAGE_H_ 1

#include "packet.h"

class Message {
public:
  Message();
  Message(const Packet& in);

  void add(const Packet& p);

  int seqnum() const { return hdr_seq_num; }
  int num_chunks() const { return hdr_num_chunks; }
  int num_curr_chunks() const { return packets.size(); }

  int size() const { return total_size; }

  bool complete() const {
    return static_cast<int>(packets.size())==hdr_num_chunks;
  }

  int read(void* dest,int offset,int len) const;

private:
  int hdr_seq_num;
  int hdr_num_chunks;
  int total_size;
  std::vector<Packet> packets;
};

#endif // MESSAGE_H_

