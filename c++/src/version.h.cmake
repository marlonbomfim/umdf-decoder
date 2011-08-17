/** :mode=c++:indentSize=2:noTabs=true:tabSize=2:
  @brief version information

  longdesc
*/

#ifndef VERSION_H_
#define VERSION_H_ 1

#define VERSION_NAME  "@DEF_VER_NAME@"

#define VERSION_MAJOR @DEF_VER_MAJOR@
#define VERSION_MINOR @DEF_VER_MINOR@
#define VERSION_PATCH @DEF_VER_PATCH@

#define VERSION_STR   "@DEF_VER_STR@"

#define VERSION       (VERSION_MAJOR*100000+VERSION_MINOR*1000+VERSION_PATCH)

#endif // VERSION_H_

