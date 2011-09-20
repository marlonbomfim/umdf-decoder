# :mode=cmake:indentSize=2:noTabs=true:tabSize=2:
# @brief FAST market data implementation
# FIX over FAST version of MarketData api

project(OpenUMDF_CC_MarketData_FAST)

#option(QuickFAST_PATH "Path to QuickFAST root" "~/projects/quickfast/src")
set(QuickFAST_PATH "~/projects/quickfast/src/")

set(OpenUMDF_CC_MarketData_FAST_SOURCES
  "book.cc"
  "fast_util.cc"
  "instrument.cc"
  "manager.cc"
  "order_entry.cc"
  )

include_directories(SYSTEM ${QuickFAST_PATH})

add_library(openumdf-marketdata-fast STATIC
  ${OpenUMDF_CC_MarketData_FAST_SOURCES})

# tests
#add_subdirectory("test")

