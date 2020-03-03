package bgu.spl.net.api.Messages;

import bgu.spl.net.api.bidi.Connections;
import bgu.spl.net.srv.DataBase;

import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class FollowMessage extends Message {
    boolean Follow;
    short NumofUsers;
    LinkedList<String> UserNameList;
    private List<Byte> _bytes = new LinkedList<Byte>();
    private boolean decodingFinished = false;
    private int numOfZeroBytes = 0;

    /**
     * constructor
     */
    public FollowMessage() {
        super();
        UserNameList = new LinkedList<String>();
        NumofUsers=-1;
    }

    /**
     * decode the bytes message and fill the fields with the match values
     * @param bytes
     */
    public void translate(byte[] bytes) {
        if (bytes[0]=='0')
            Follow = true;
        else
            Follow = false;
        int last0 = 2;
        for (int i = 3; i < bytes.length; i++) {
            if (bytes[i] == '\0') {
                try {
                    UserNameList.add(new String(bytes, last0+1, i-last0-1, "utf-8"));//add the follow / unfollow list.
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                last0 = i;
            }

        }
    }

    /**
     * implements just for abstract function, used only in client side
     * @return
     */
    public byte[] encode() {
        return null;
    }

    /**
     *
     * @param byteArr
     * @param i
     * @return short that match to the input byte array
     */
    private short bytesToShort(byte[] byteArr, int i) {
        short result = (short) ((byteArr[i] & 0xff) << 8);
        result += (short) (byteArr[i + 1] & 0xff);
        return result;
    }

    /**
     * process the Follow message, update the follow / unfollow list for the specific user
     * @param db
     * @param ConnectionID
     * @param connections
     * @return
     */
    public Message process(DataBase db, int ConnectionID , Connections connections) {
        boolean succeeded = false;
        int successfull_follows = 0;
        String ack_data = "";
        String username = db.ConnectionIdToUserName(ConnectionID);
        if (db.getClientsInfo(username) == null || !db.getClientsInfo(username).isLoggedIn()) {// check if the clients is register and then check if he is log in
            ErrorMessage err = new ErrorMessage((short) 4);
            return err;
        } else {
            // Follow user
            if (Follow) {// if the user want to add followers
                Iterator<String> iter = UserNameList.iterator();
                while (iter.hasNext()) {
                    String user = iter.next();
                    if (db.getClientsInfo(user) == null || db.getClientsInfo(username).isIFollow(user)) {
                        continue;
                    }
                    else {
                        synchronized (db.getClientsInfo(username)) {//  sync the username for not loosing posts form user that the clients want to add from the followers list, and symetrics
                            db.getClientsInfo(username).AddIFollow(user);
                            db.getClientsInfo(user).AddFollowingMe(username);
                            succeeded = true;
                            ack_data = ack_data + user + " ";
                            successfull_follows++;
                        }
                    }
                }
            }

            //UnFollow user
            else { // if the user wants to remove from his followers list
                Iterator<String> iter = UserNameList.iterator();
                while (iter.hasNext()) {
                    String user = iter.next();
                    if (db.getClientsInfo(user) == null || !(db.getClientsInfo(username).isIFollow(user))) {
                        continue;
                    }
                    else {
                        synchronized (db.getClientsInfo(username)) { //sync the username to avoid recived posts form user that the clients want to remove from the followers list, and symetrics
                            db.getClientsInfo(username).removeIFollow(user);
                            db.getClientsInfo(user).removeFollowingMe(username);
                            succeeded = true;
                            ack_data = ack_data + user + " ";
                            successfull_follows++;
                        }
                    }
                }
            }

            //Result
            if (!succeeded) { // at least one user add/ remove form the client lists
                ErrorMessage err = new ErrorMessage((short) 4);
                return err;
            } else {
                AckMessage ack = new AckMessage((short) 4, ack_data , successfull_follows);
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
        if(_bytes.size()==2){
            _bytes.add(nextByte);
            NumofUsers = bytesToShort(ToArray(_bytes),1);
        }
        else if(_bytes.size() < 2 | nextByte != '\0') {
            _bytes.add(nextByte);
        }
        else if (numOfZeroBytes < NumofUsers-1){//we have a problem, we are sending zero byte between the op cose to the number of users.
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
