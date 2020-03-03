package bgu.spl.net.api.Messages;

import bgu.spl.net.api.bidi.Connections;
import bgu.spl.net.srv.DataBase;

import java.io.UnsupportedEncodingException;
import java.util.LinkedList;
import java.util.List;

public class LoginMessage extends Message {
    private String username;
    private String password;
    private List<Byte> _bytes = new LinkedList<Byte>();
    private boolean decodingFinished = false;
    private int numOfZeroBytes = 0;

    /**
     * constructor
     */
    public LoginMessage(){
        super();

    }

    /**
     * decode decode the bytes message and fill the fields with the match values
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
     * implements just for abstract function, used in clients side
     * @return
     */
    public byte [] encode(){
        return null;
    }

    /**
     * process the login message, if succeeded add the username to the match data structure and update the fields
     * @param db
     * @param ConnectionID
     * @param connections
     * @return
     */
    public Message process(DataBase db , int ConnectionID , Connections connections) {
        if(db.getClientsInfo(username)==null){
            ErrorMessage err = new ErrorMessage((short) 2);
            return err;
        }
        synchronized (db.getClientsInfo(username)) {
            if (db.getClientsInfo(username) == null || (!db.getClientsInfo(username).getPassword().equals(password) | db.getClientsInfo(username).isLoggedIn() |
                    (db.getClientsInfo(db.ConnectionIdToUserName(ConnectionID)) != null && db.getClientsInfo(db.ConnectionIdToUserName(ConnectionID)).isLoggedIn()))) {
                ErrorMessage err = new ErrorMessage((short) 2);
                return err;
            } else {
                db.LogInClient(username, ConnectionID, connections);
                AckMessage ack = new AckMessage((short) 2);
                return ack;
            }
        }
    }

    /**
     * decode the bytes and return true if the message is ready
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
