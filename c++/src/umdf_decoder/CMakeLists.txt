# :mode=cmake:indentSize=2:noTabs=true:tabSize=2:
# @brief cmake config for UMDF decoder
# longdesc

cmake_minimum_required(VERSION 2.6)

project(umdf-decoder)
set(DEF_VER_NAME "UMDF_DECODER")
set(DEF_VER_MAJOR 0)
set(DEF_VER_MINOR 1)
configure_file(
  "${PROJECT_SOURCE_DIR}/../version.h.in"
  "${PROJECT_BINARY_DIR}/pre/umdf-decoder/version.h")

add_library()

