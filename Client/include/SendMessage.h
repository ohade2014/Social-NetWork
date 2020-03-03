//
// Created by yuvaleis@wincs.cs.bgu.ac.il on 12/28/18.
//

#ifndef BOOST_ECHO_CLIENT_SENDMESSAGE_H
#define BOOST_ECHO_CLIENT_SENDMESSAGE_H
#include <string>
#include <iostream>
#include <boost/asio.hpp>
#include "connectionHandler.h"
#include <mutex>
#include <thread>

class SendMessage{
private:
    ConnectionHandler* CH;
    bool* IsLogIn;
    //int _bufSize;
public:
    SendMessage(ConnectionHandler* ch, bool& isLogin);
    //virtual ~SendMessage();
    void operator()();
    short bytesToShort(char* bytesArr);
    void shortToBytes(short num, char* bytesArr);
};
#endif //BOOST_ECHO_CLIENT_SENDMESSAGE_H
