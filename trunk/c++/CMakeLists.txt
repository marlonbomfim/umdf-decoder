# :mode=cmake:indentSize=2:noTabs=true:tabSize=2:
# @brief base cmake file
# longdesc

cmake_minimum_required(VERSION 2.6)
project(dump)

if(WIN32)
  set(DEF_IS_WIN 1)
  set(DEF_IS_LINUX 0)
else()
  set(DEF_IS_LINUX 1)
  set(DEF_IS_WIN 0)
endif()

configure_file(
  "${PROJECT_SOURCE_DIR}/src/platform.h.in"
  "${PROJECT_BINARY_DIR}/pre/platform.h")

include_directories(
  "${PROJECT_BINARY_DIR}/pre"
  "src"
  "src/umdf_decoder"
)

set(Boost_USE_MULTITHREADED ON)
find_package(Boost 1.4.0 COMPONENTS filesystem thread system)
include_directories(${Boost_INCLUDE_DIRS})

set(dump_test_SOURCES
  "src/umdf_decoder/test/dump.cc"
  "src/umdf_decoder/aggregator.cc"
  "src/umdf_decoder/message.cc"
  "src/umdf_decoder/packet.cc"
  "src/umdf_decoder/udp_queue.cc"
)

set(DEBUG ON)
add_definitions(-g -Wall)

add_executable(dump ${dump_test_SOURCES})
target_link_libraries(dump ${Boost_LIBRARIES})

