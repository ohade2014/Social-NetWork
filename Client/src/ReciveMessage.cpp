//
// Created by yuvaleis@wincs.cs.bgu.ac.il on 12/28/18.
//
#include <stdlib.h>
#include "ReciveMessage.h"
#include <string>
#include <iostream>
#include <boost/asio.hpp>
#include "connectionHandler.h"
#include <mutex>
#include <thread>
#include <boost/algorithm/string.hpp>
using boost::asio::ip::tcp;
using namespace std;

/**
 * Constructor of "Runnable" object will be responsible of getting messages from the server
 * @param connection handler
 * @param isLogin
 */
ReciveMessage::ReciveMessage(ConnectionHandler*  ch, bool& isLogin) :CH(ch) , IsLogIn(&isLogin) , TerminateProgram(false){
}

/**
 * Run operation of receiving messages from server
 */
void  ReciveMessage::operator()(){

    while(!TerminateProgram) {
        //Get and decode op number of the message that arrived
        char op[2];
        bool opTaken = CH->getBytes(op, 2);
        string s;
        short opNumber = bytesToShort(op);
        if (opTaken) {
            if (opNumber == 9) { //Notification Message
                char typeOfMessage;
                bool TypeTaken = CH->getBytes(&typeOfMessage, 1); //Get type of post/pm notification
                short TypeIndicate;
                if (typeOfMessage == '1')
                    TypeIndicate = 1;
                else
                    TypeIndicate = 0;
                std::string MessageType;
                if (TypeTaken && TypeIndicate == 0) {
                    MessageType = "PM";
                } else {
                    MessageType = "Public";
                }
                //Get content of notification message
                std::string userName;
                bool userNameIndicate = CH->getFrameAscii(userName, '\0');
                std::string content;
                bool contentIndicate = CH->getFrameAscii(content, '\0');
                if (userNameIndicate && contentIndicate && TypeTaken) {
                    std::cout << "NOTIFICATION" << " " << MessageType << " " << userName.substr(0,userName.length()-1) << " " << content.substr(0,content.length()-1)
                              << std::endl;
                }
            }
            else if (opNumber == 10) { //Ack Message
                char MessageOp[2];
                bool MessageOpTaken = CH->getBytes(MessageOp, 2); //Get type of ack Message
                short MessageOpNumber = bytesToShort(MessageOp);
                if (MessageOpTaken) {
                    if (MessageOpNumber == 2){ //Login ack message
                        *IsLogIn = true;
                        cout<< "ACK "<< MessageOpNumber<< std:: endl;
                    }
                    else if (MessageOpNumber == 3) { //Logout ack Message
                        std::cout<<"ACK "<<MessageOpNumber<<std::endl;
                        CH->close();
                        TerminateProgram = true;
                    }
                    else if ((MessageOpNumber == 4) | (MessageOpNumber == 7)) { //Follow / UserList ack Message
                        char NumOfUsers[2];
                        bool NumOfUsersTaken = CH->getBytes(NumOfUsers, 2);
                        short NumberOfUsers = bytesToShort(NumOfUsers);
                        if(NumOfUsersTaken) {
                            string output;
                            if (MessageOpNumber == 4)
                                output = "ACK 4 " + std::to_string(NumberOfUsers);
                            else
                                output = "ACK 7 " + std::to_string(NumberOfUsers);
                            // Add users list to the printed ack message
                            for (int i = 0; i < NumberOfUsers; i++) {
                                std::string usersNames;
                                bool userNameIndicate = CH->getFrameAscii(usersNames, '\0');
                                if (userNameIndicate) {
                                    output = output + " " + usersNames.substr(0, usersNames.length() - 1);
                                }
                            }
                            std::cout << output << std::endl;
                        }
                    }
                    else if (MessageOpNumber == 8) { // Stat ack Message
                        char NumPosts[2];
                        char NumFollowers[2];
                        char NumFollowing[2];
                        bool NumPostsTaken = CH->getBytes(NumPosts, 2);
                        bool NumFollowersTaken = CH->getBytes(NumFollowers, 2);
                        bool NumFollowingTaken = CH->getBytes(NumFollowing, 2);
                        short NumPostsNumber = bytesToShort(NumPosts);
                        short NumFollowersNumber = bytesToShort(NumFollowers);
                        short NumFollowingNumber = bytesToShort(NumFollowing);
                        if (NumPostsTaken & NumFollowersTaken & NumFollowingTaken) {
                            cout << "ACK 8" << " " << NumPostsNumber << " " << NumFollowersNumber << " "
                                 << NumFollowingNumber << endl;
                        }
                    }
                    else{ // All other ack Messages
                        cout<< "ACK "<< MessageOpNumber<< std:: endl;
                    }
                }
            } else if (opNumber == 11) { //Error Message
                char MessageOp[2];
                bool MessageOpTaken = CH->getBytes(MessageOp, 2);
                short MessageOpNumber = bytesToShort(MessageOp);
                if (MessageOpTaken)
                    cout << "ERROR " << MessageOpNumber << endl;
            }
        }
    }
};

/**
 *
 * @param bytesArr
 * @return the casting number from bytes to short
 */
short ReciveMessage::bytesToShort(char* bytesArr)
{
    short result = (short)((bytesArr[0] & 0xff) << 8);
    result += (short)(bytesArr[1] & 0xff);
    return result;
}

