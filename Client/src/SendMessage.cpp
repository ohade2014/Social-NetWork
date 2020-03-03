//
// Created by yuvaleis@wincs.cs.bgu.ac.il on 12/28/18.
//
#include "SendMessage.h"
#include <mutex>
#include <boost/algorithm/string.hpp>
#include <vector>
#include <string>
using boost::asio::ip::tcp;
using namespace std;

/**
 * Constructor of "Runnable" object will be responsible of sending messages to the server
 * @param ch
 * @param isLogin
 */
SendMessage::SendMessage(ConnectionHandler *ch, bool& isLogin) :CH(ch) , IsLogIn(&isLogin) {
}

/**
 * Run operation of sending messages to server
 */
void  SendMessage::operator()(){
    while (true) {
        //Create Buffer and wait until user enters command using keyboard
        const short bufsize=1024;
        char buf[bufsize];
        std::cin.getline(buf,bufsize);
        std::string line(buf);
        vector<string> strs;
        boost::split(strs,line,boost::is_any_of(" ")); //split command by ' '
        if (strs[0] == "REGISTER"){ //Register command entered
            char op [2];
            shortToBytes((short) 1,op);
            CH->sendBytes(op,2);
            CH->sendFrameAscii(strs[1],'\0');
            CH->sendFrameAscii(strs[2],'\0');
        }
        else if (strs[0] == "LOGIN"){ //Login command entered
            shortToBytes((short) 2,buf);
            CH->sendBytes(buf,2);
            CH->sendFrameAscii(strs[1],'\0');
            CH->sendFrameAscii(strs[2],'\0');
        }
        else if (strs[0] == "LOGOUT"){ //Logout command entered
            shortToBytes(3,buf);
            CH->sendBytes(buf,2);
            if (*IsLogIn) {
                break;
            }
        }
        else if (strs[0] == "FOLLOW"){ //Follow command entered
            char op[2];
            shortToBytes(4,op);
            CH->sendBytes(op,2);
            char ch[1];
            //Send Follow / UnFollow
            if (strs[1] == "1"){
                ch[0] = '1';
                CH->sendBytes(ch,1);
            }
            else{
                ch[0] = '0';
                CH->sendBytes(ch,1);
            }
            //Send Number Of users
            int NumOfUsers = stoi(strs[2]);
            short NumOfUsersShort = (short) NumOfUsers;
            char num_of_users_bytes[2];
            shortToBytes(NumOfUsersShort,num_of_users_bytes);
            CH->sendBytes(num_of_users_bytes,2);
            //Send Users Names
            for (unsigned int i = 3 ; i < strs.size() ; i++){
                CH->sendFrameAscii(strs[i],'\0');
            }
        }
        else if (strs[0] == "POST"){ //Post command entered
            shortToBytes(5,buf);
            CH->sendBytes(buf,2);
            for (unsigned int j = 1 ; j < strs.size()-1 ; j++){
                CH->sendFrameAscii(strs[j],' ');
            }
            CH->sendFrameAscii(strs[strs.size()-1],'\0');
        }
        else if (strs[0] == "PM"){ //PM command entered
            shortToBytes(6,buf);
            CH->sendBytes(buf,2);
            CH->sendFrameAscii(strs[1],'\0');
            for (unsigned int j = 2 ; j < strs.size()-1 ; j++){
                CH->sendFrameAscii(strs[j],' ');
            }
            CH->sendFrameAscii(strs[strs.size()-1],'\0');
        }
        else if (strs[0] == "USERLIST"){ //Userlist command entered
            shortToBytes(7,buf);
            CH->sendBytes(buf,2);
        }
        else if (strs[0] == "STAT"){ //Stat command entered
            shortToBytes(8,buf);
            CH->sendBytes(buf,2);
            CH->sendFrameAscii(strs[1],'\0');
        }
    }
}

/**
 * Casting short to form of 2 bytes
 * @param num
 * @param bytesArr
 */
void SendMessage::shortToBytes(short num, char* bytesArr)
{
    bytesArr[0] = ((num >> 8) & 0xFF);
    bytesArr[1] = (num & 0xFF);
}
