//
// Created by yuvaleis@wincs.cs.bgu.ac.il on 12/28/18.
//

#ifndef BOOST_ECHO_CLIENT_RECIVEMESSAGE_H
#define BOOST_ECHO_CLIENT_RECIVEMESSAGE_H
#include <string>
#include <iostream>
#include <boost/asio.hpp>
#include "connectionHandler.h"
#include <mutex>
#include <thread>
class ReciveMessage{
private:
    ConnectionHandler* CH;
    bool* IsLogIn;
    bool TerminateProgram;
    //int _bufSize;
public:
    ReciveMessage(ConnectionHandler* ch, bool& toTerminate);
    //virtual ~ReciveMessage();
    void operator()();
    short bytesToShort(char* bytesArr);

};
#endif //BOOST_ECHO_CLIENT_RECIVEMESSAGE_H
