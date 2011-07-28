# :mode=cmake:indentSize=2:noTabs=true:tabSize=2:
# @brief base cmake file
# longdesc

cmake_minimum_required(VERSION 2.6)

if(WIN32)
  set(DEF_IS_WIN 1)
  set(DEF_IS_LINUX 0)
else(LINUX)
  set(DEF_IS_LINUX 1)
  set(DEF_IS_WIN 0)
endif()

configure_file(
  "${PROJECT_SOURCE_DIR}/src/platform.h.in"
  "${PROJECT_BINARY_DIR}/pre/platform.h")

include_directories("${RPOJECT_BINARY_DIR}/pre")
