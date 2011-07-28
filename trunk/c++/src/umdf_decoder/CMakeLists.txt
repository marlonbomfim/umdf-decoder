# :mode=cmake:indentSize=2:noTabs=true:tabSize=2:
# @brief cmake config for UMDF decoder
# longdesc

cmake_minimum_required(VERSION 2.6)

project(umdf-decoder)

# version information
set(UMDF_DECODER_VERSION_MAJOR 0)
set(UMDF_DECODER_VERSION_MINOR 1)
configure_file(
  "${PROJECT_SOURCE_DIR}/pre/version.h.in"
  "${PROJECT_BINARY_DIR}/pre/version.h")

# add for preprocessed files
include_directories("${RPOJECT_BINARY_DIR}/pre")



