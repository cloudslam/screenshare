cmake_minimum_required(VERSION 3.22.1)

project(nativeRpmsg)

add_library(
        nativeRpmsg
        SHARED
        native_rpmsg.cpp)

target_link_libraries(
        nativeRpmsg
        log)
