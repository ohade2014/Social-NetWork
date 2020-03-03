package bgu.spl.net.api.Messages;

import bgu.spl.net.api.bidi.Connections;
import bgu.spl.net.srv.DataBase;

import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class RegisterMessage extends Message {
    private String username;
    private String password;
    private List<Byte> _bytes = new LinkedList<Byte>();
    private boolean decodingFinished = false;
    private int numOfZeroBytes = 0;

    public String getDestination(){
        return null;
    }

    /**
     * empty constructor
     */
    public RegisterMessage(){
        super();
    }

    /**
     * decode the message to string before send to process in the protocol.
     * @param bytes
     */
    public void translate(byte [] bytes){
        boolean found0 = false;
        int end_username = -1;
        for (int i = 0 ; i < bytes.length ; i++){
            if (bytes[i] == '\0' & !found0){
                try {
                    username = new String(bytes , 0 , i , "utf-8");
                }
                catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                found0 = true;
                end_username = i;
            }
            else if (bytes[i] == '\0' & found0){
                try {
                    password = new String(bytes , end_username+1 , i-end_username-1 , "utf-8");
                }
                catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                break;
            }
        }
    }

    /**
     * implements just for abstarct function, used in clients side
     * @return
     */
    public byte [] encode(){
        return null;
    }

    /**
     * process the register message, check if the username is valid, if it is add the user to the system. else send back an error.
     * @param db
     * @param ConnectionID
     * @param connections
     * @return
     */
    public Message process(DataBase db , int ConnectionID , Connections connections){
        if (!db.RegisterClient(username,password)){
            ErrorMessage err = new ErrorMessage((short)1);
            return err;
        }
        else{
            String optionaldata=username+"\0"+password;
            AckMessage ack = new AckMessage((short)1,optionaldata);
            return ack;
        }
    }

    /**
     * decode decode the bytes message and fill the fields with the match values
     * @param nextByte
     * @return
     */
    public boolean decodeNextByte(byte nextByte){
        if(nextByte != '\0') {
            _bytes.add(nextByte);
        }
        else if (numOfZeroBytes == 0){
            numOfZeroBytes ++;
            _bytes.add(nextByte);
        }
        else{
            numOfZeroBytes++;
            _bytes.add(nextByte);
            translate(ToArray(_bytes));
            decodingFinished = true;
        }
        return decodingFinished;
    }

}
