# :mode=cmake:indentSize=2:noTabs=true:tabSize=2:
# @brief base cmake file
# root of project with common CMake definitions

cmake_minimum_required(VERSION 2.8)
project(OpenUMDF_CC)

# C++ debug warnings and optimization
if(UNIX)
set(CMAKE_CXX_FLAGS_DEBUG "-ansi -g3 -O0 -Wall -Wextra -pedantic -Weffc++")
set(CMAKE_CXX_FLAGS_RELEASE "-DNDEBUG -O3")
endif()

# platform-specific definitions
if(UNIX)
set(PLATFORM_UNIX ON)
endif()
if(WIN32)
set(PLATFORM_WIN32 ON)
endif()
configure_file(
  "${PROJECT_SOURCE_DIR}/src/platform.h.cmake"
  "${PROJECT_BINARY_DIR}/pre/platform.h")

# version definition
set(DEF_VER_NAME  "OpenUMDF-C++")
set(DEF_VER_MAJOR 0)
set(DEF_VER_MINOR 1)
set(DEF_VER_PATCH 0)
set(DEF_VER_STR "${DEF_VER_NAME}-${DEF_VER_MAJOR}.${DEF_VER_MINOR}.${DEF_VER_PATCH}")
configure_file(
  "${PROJECT_SOURCE_DIR}/src/version.h.cmake"
  "${PROJECT_BINARY_DIR}/pre/version.h")

# common include folders
include_directories(
  "${PROJECT_BINARY_DIR}/pre"
  "src"
)

# boost
set(Boost_USE_MULTITHREADED ON)
find_package(Boost 1.4.7 COMPONENTS thread system filesystem)
include_directories(${Boost_INCLUDE_DIRS})

# subprojects
add_subdirectory("src/umdf_decoder")
add_subdirectory("src/market_data")
add_subdirectory("src/bvmf/umdf_console")

