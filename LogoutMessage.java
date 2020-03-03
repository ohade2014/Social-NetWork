package bgu.spl.net.api.Messages;

import bgu.spl.net.api.bidi.Connections;
import bgu.spl.net.srv.DataBase;

import java.io.UnsupportedEncodingException;
import java.util.LinkedList;
import java.util.List;

public class LogoutMessage extends Message {
    /**
     * constructor
     */
    public LogoutMessage(){
        super();
    }

    public void translate (byte [] bytes){
    }

    /**
     * implements just for abstarct function, used in clients side
     * @return
     */
    public byte [] encode(){

        //return shortToBytes((short) 3);
        return null;
    }

    /**
     * process the logout message, check if the user is login, if he is ACK will return, else Error will return
     * @param db
     * @param ConnectionID
     * @param connections
     * @return
     */
    public Message process(DataBase db , int ConnectionID , Connections connections) {
        String username = db.ConnectionIdToUserName(ConnectionID);
        if (db.getClientsInfo(username)==null || !db.getClientsInfo(username).isLoggedIn()) {
            ErrorMessage err = new ErrorMessage((short) 3);
            return err;
        } else {
            db.LogOutClient(username);
            AckMessage ack = new AckMessage((short)3);
            return ack;
        }
    }

    /**
     * implements just for abstarct function, used in clients side
     * @param nextByte
     * @return
     */
    public boolean decodeNextByte(byte nextByte){
        return true;
    }

}
