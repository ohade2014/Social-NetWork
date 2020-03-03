#include <stdlib.h>
#include "connectionHandler.h"
#include <thread>
#include "ReciveMessage.h"
#include "SendMessage.h"
#include <mutex>
#include <boost/asio.hpp>


/**
* This code assumes that the server replies the exact text the client sent it (as opposed to the practical session example)
*/
int main (int argc, char *argv[]) {
    if (argc < 3) {
        std::cerr << "Usage: " << argv[0] << " host port" << std::endl << std::endl;
        return -1;
    }
    std:: string host=argv[1];
    short port=atoi(argv[2]);
    //Try To connect to the server
    std::string s;
    ConnectionHandler connectionHandler(host, port);
    if (!connectionHandler.connect()) {
        std::cerr << "Cannot connect to " << host << ":" << port << std::endl;
        return 1;
    }

    //Start 2 threads - one handling Messages gets from server, and the other gets commands from the keboard and sends it to the server
    bool isLogin = false;
    SendMessage Send = SendMessage(&connectionHandler, isLogin);
    ReciveMessage Recieve = ReciveMessage(&connectionHandler , isLogin);
    std::thread SendThread(std::ref(Send));
    std::thread RecieveThread(std::ref(Recieve));
    SendThread.join();
    RecieveThread.join();

    return 0;
}
