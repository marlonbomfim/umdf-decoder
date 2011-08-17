# :mode=cmake:indentSize=2:noTabs=true:tabSize=2:
# @brief umdf decoder tests
# make script for decoder tests: dump

project(OpenUMDF_CC_Decoder_DumpTest)

add_executable(openumdf-decoder-dump "dump.cc")
target_link_libraries(openumdf-decoder-dump ${Boost_LIBRARIES})
target_link_libraries(openumdf-decoder-dump openumdf-decoder)
target_link_libraries(openumdf-decoder-dump pthread)

