# :mode=cmake:indentSize=2:noTabs=true:tabSize=2:
# @brief cmake config for UMDF decoder
# the UMDF decoder library

project(OpenUMDF_CC_Decoder)

set(OpenUMDF_CC_Decoder_SOURCES
  "aggregator.cc"
  "message.cc"
  "packet.cc"
  "udp_queue.cc")

add_library(openumdf-decoder STATIC ${OpenUMDF_CC_Decoder_SOURCES})

# tests
add_subdirectory("test")

