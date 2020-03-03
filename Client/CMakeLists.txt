cmake_minimum_required(VERSION 3.12)
project(Client)

set(CMAKE_CXX_STANDARD 14)

include_directories(.)
include_directories(bin)
include_directories(include)
include_directories(src)
include_directories(${Boost_INCLUDE_DIR})
link_directories(${Boost_LIBRARY_DIR})
find_package(Boost COMPONENTS system filesystem REQUIRED)


set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS}-std=c++11 -pthread -lboost_thread")


add_executable(Client
        include/connectionHandler.h
        include/ReciveMessage.h
        include/SendMessage.h
        src/connectionHandler.cpp
        src/BGSclient.cpp
        src/ReciveMessage.cpp
        src/SendMessage.cpp
        makefile include/SendMessage.h include/ReciveMessage.h src/SendMessage.cpp src/ReciveMessage.cpp)

target_link_libraries(Client ${Boost_LIBRARIES})
