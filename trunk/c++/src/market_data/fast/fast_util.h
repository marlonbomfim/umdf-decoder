/** :mode=c++:indentSize=2:noTabs=true:tabSize=2:
  @brief briefdesc

  longdesc
*/

#ifndef MARKET_DATA_FAST_FAST_UTIL_H_
#define MARKET_DATA_FAST_FAST_UTIL_H_ 1

#include <string>

#include <boost/cstdint.hpp>
#include <boost/date_time/posix_time/posix_time_types.hpp>

#include "fast_errors.h"
#include "quickfast.h"

inline std::string get_string(
    const QuickFAST::Messages::MessageAccessor& msg,
    const char* name) {
  const QuickFAST::StringBuffer* ptr;
  if(!msg.getString(
      QuickFAST::Messages::FieldIdentity(name),
      QuickFAST::ValueType::ASCII,
      ptr)) {
    BOOST_THROW_EXCEPTION(UnknownFastTag()
      <<FastTagName(name));
  }

  return static_cast<std::string>(*ptr);
}

inline boost::int64_t get_int(
    const QuickFAST::Messages::MessageAccessor& msg,
    const char* name) {
  QuickFAST::int64 out;
  if(!msg.getSignedInteger(
      QuickFAST::Messages::FieldIdentity(name),
      QuickFAST::ValueType::INT64,
      out)) {
    BOOST_THROW_EXCEPTION(UnknownFastTag()
      <<FastTagName(name));
  }

  return static_cast<boost::int64_t>(out);
}

inline boost::uint64_t get_uint(
    const QuickFAST::Messages::MessageAccessor& msg,
    const char* name) {
  QuickFAST::uint64 out;
  if(!msg.getUnsignedInteger(
      QuickFAST::Messages::FieldIdentity(name),
      QuickFAST::ValueType::UINT64,
      out)) {
    BOOST_THROW_EXCEPTION(UnknownFastTag()
      <<FastTagName(name));
  }

  return static_cast<boost::uint64_t>(out);
}

inline double get_double(
    const QuickFAST::Messages::MessageAccessor& msg,
    const char* name) {
  QuickFAST::Decimal out;
  if(!msg.getDecimal(
      QuickFAST::Messages::FieldIdentity(name),
      QuickFAST::ValueType::DECIMAL,
      out)) {
    BOOST_THROW_EXCEPTION(UnknownFastTag()
      <<FastTagName(name));
  }
  return static_cast<double>(out);
}

inline std::size_t get_sequence_length(
    const QuickFAST::Messages::MessageAccessor& msg,
    const char* name) {
  std::size_t out_len=0;
  if(!msg.getSequenceLength(
      QuickFAST::Messages::FieldIdentity(name),
      out_len)) {
    BOOST_THROW_EXCEPTION(UnknownFastTag()
      <<FastTagName(name));
  }

  return out_len;
}

template<class Callable>
inline std::size_t for_each_in_sequence(
    const QuickFAST::Messages::MessageAccessor& msg,
    const char* name,
    Callable f) {
  std::size_t len=get_sequence_length(msg,name);
  const QuickFAST::Messages::MessageAccessor* cur=0;
  for(std::size_t i=0;i<len;++i) {
    if(!msg.getSequenceEntry(
        QuickFAST::Messages::FieldIdentity(name),
        i,
        cur)) {
      BOOST_THROW_EXCEPTION(UnknownFastSequenceEntry()
        <<FastTagName(name));
    }

    f(*cur);
  }

  return len;
}

boost::posix_time::ptime bvmf_date_to_posix(boost::int64_t in_date,boost::int64_t in_time);
boost::posix_time::ptime bvmf_date_to_posix(boost::int64_t full_date);
boost::int64_t posix_date_to_bvmf(boost::posix_time::ptime full_date);

#endif // MARKET_DATA_FAST_FAST_UTIL_H_

