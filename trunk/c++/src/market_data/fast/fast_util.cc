/** :mode=c++:indentSize=2:noTabs=true:tabSize=2:
  @brief briefdesc

  longdesc
*/

#include "fast_util.h"

#include <boost/date_time/gregorian/gregorian_types.hpp>

using boost::gregorian::date;
using boost::posix_time::ptime;
using boost::posix_time::hours;
using boost::posix_time::minutes;
using boost::posix_time::seconds;
using boost::posix_time::milliseconds;
using boost::posix_time::time_duration;
using boost::int64_t;

ptime bvmf_date_to_posix(int64_t in_date,int64_t in_time) {
  int64_t year=in_date/10000;
  int64_t month=((in_date-(year*10000))/100)-1;
  int64_t day=in_date-(year*10000)-(month*100);

  int64_t hour=in_time/10000000;
  int64_t minute=(in_time-(hour*10000000))/100000;
  int64_t second=(in_time-(hour*10000000)-(minute*100000))/1000;
  int64_t milli=in_time-(hour*10000000)-(minute*100000)-(second*1000);

  return ptime(
    date(year,month,day),
    hours(hour)+minutes(minute)+seconds(second)+milliseconds(milli));
}

ptime bvmf_date_to_posix(int64_t full_date) {
  int64_t date_part=full_date/1000000000;
  int64_t time_part=full_date-(date_part*1000000000);
  return bvmf_date_to_posix(date_part,time_part);
}

int64_t posix_date_to_bvmf(ptime full_date) {
  date d=full_date.date();
  time_duration t=full_date.time_of_day();

  int64_t date_part=d.year()*10000;
  date_part+=(d.month()+1)*100;
  date_part+=d.day();

  int64_t time_part=d.hours()*10000000;
  time_part+=d.minutes()*100000;
  time_part+=d.seconds()*1000;
  time_part+=d.milliseconds();

  return date_part*1000000000 + time_part;
}

