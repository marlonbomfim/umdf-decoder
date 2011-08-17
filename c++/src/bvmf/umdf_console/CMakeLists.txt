# :mode=cmake:indentSize=2:noTabs=true:tabSize=2:
# @brief BVMF console
# simple console interface to BVMF data feeds

project(OpenUMDF_CC_BVMFConsole)

set(OpenUMDF_CC_BVMFConsole_SOURCES
  "main.cc"
  "console.cc")

add_executable(bvmf-console ${OpenUMDF_CC_BVMFConsole_SOURCES})

