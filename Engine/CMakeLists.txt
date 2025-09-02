cmake_minimum_required(VERSION 3.10)
project(Remake LANGUAGES CXX)

set(CMAKE_CXX_STANDARD 20)

add_library(Remake SHARED
        main.cpp
        jni_exports.h
)

target_include_directories(Remake
        PRIVATE
        ${CMAKE_SOURCE_DIR}/include/jni
        ${CMAKE_SOURCE_DIR}/include/jni
)
