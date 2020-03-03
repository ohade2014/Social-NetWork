package bgu.spl.net.api.Messages;

import bgu.spl.net.api.bidi.Connections;
import bgu.spl.net.srv.DataBase;

import java.io.UnsupportedEncodingException;
import java.util.LinkedList;
import java.util.List;

public class PMMessage extends Message {

    private String username;
    private String content;
    private List<Byte> _bytes = new LinkedList<Byte>();
    private boolean decodingFinished = false;
    private int numOfZeroBytes = 0;

    public PMMessage(){
        super();

    }

    /**
     * decode the PM message before used by the server
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
                    content = new String(bytes , end_username+1 , i-end_username-1 , "utf-8");
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
     * process the private message, if succeeded send notification to the target user, else send back an error to the client.
     * @param db
     * @param ConnectionID
     * @param connections
     * @return
     */
    public Message process(DataBase db , int ConnectionID , Connections connections) {
        String user= db.ConnectionIdToUserName(ConnectionID);
        if(db.getClientsInfo(user)==null || !db.getClientsInfo(user).isLoggedIn()||(db.getClientsInfo(username)==null)){//check if the client user is log in and check if the target uset is register.
            ErrorMessage err=new ErrorMessage((short) 6);
            return err;
        }
        else{
            NotificationMessage notificationMessage = new NotificationMessage(user , content , true, username);
            if (db.getClientsInfo(username).isLoggedIn()) {
                connections.send((Integer) db.getUsersList().get(username), notificationMessage);
            }
            else{
                db.AddFutureMessage(username,notificationMessage);
            }
            db.getClientsInfo(user).addPM(this);
            AckMessage ack = new AckMessage((short) 6);
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
