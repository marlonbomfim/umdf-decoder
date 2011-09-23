# :mode=cmake:indentSize=2:noTabs=true:tabSize=2:
# @brief BVMF console
# simple console interface to BVMF data feeds

project(OpenUMDF_CC_BVMFConsole)

set(QuickFAST_PATH "~/projects/quickfast/src/")
include_directories(SYSTEM ${QuickFAST_PATH})

set(OpenUMDF_CC_BVMFConsole_SOURCES
  "main.cc"
  "console.cc")

link_directories("~/projects/quickfast/lib/")
add_executable(bvmf-console ${OpenUMDF_CC_BVMFConsole_SOURCES})
target_link_libraries(bvmf-console QuickFAST openumdf-marketdata-fast)

