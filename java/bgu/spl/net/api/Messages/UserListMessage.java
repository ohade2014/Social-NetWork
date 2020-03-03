package bgu.spl.net.api.Messages;

import bgu.spl.net.api.bidi.Connections;
import bgu.spl.net.srv.DataBase;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentHashMap;

public class UserListMessage extends Message {
    /**
     * empty constructor
     */
    public UserListMessage(){
        super();
    }

    /**
     * implements just for abstarct function, create in the messageEncoderDecoderImpl
     * @param bytes
     */
    public void translate (byte [] bytes){}

    /**
     * implements just for abstarct function, used in clients side
     * @return
     */
    public byte [] encode(){return null;}

    /**
     * process the UesrListMessage, create string for sending back to the client with all the register user
     * @param db
     * @param ConnectionID
     * @param connections
     * @return
     */
    public Message process(DataBase db , int ConnectionID , Connections connections) {
        String outPut="";
        String username= db.ConnectionIdToUserName(ConnectionID);
        if(db.getClientsInfo(username)==null || !db.getClientsInfo(username).isLoggedIn()){
            ErrorMessage err=new ErrorMessage((short) 7);
            return err;
        }
        else
        {
            Iterator <String> iter=db.getRegisterList().iterator();
            while (iter.hasNext()){
                outPut=outPut + iter.next()+" ";
            }
        }
            AckMessage ack=new AckMessage((short) 7, outPut,db.getNumOfRegisterUsers());
            return ack;
    }

    /**
     * implements just for abstarct function,create in the messageEncoderDecoderImpl
     * @param nextByte
     * @return
     */
    public boolean decodeNextByte(byte nextByte){
        return true;
    }

}
