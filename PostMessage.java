package bgu.spl.net.api.Messages;

import bgu.spl.net.api.bidi.Connections;
import bgu.spl.net.srv.DataBase;

import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class PostMessage extends Message {

    private String content;
    private List<Byte> _bytes = new LinkedList<Byte>();
    private boolean decodingFinished = false;

    /**
     * empty constructor
     */
    public PostMessage(){
        super();
    }

    /**
     * decode the message to string before send to process in the protocol.
     * @param bytes
     */
    public void translate (byte [] bytes){
        try {
            content = new String(bytes, 0 , bytes.length-1 , "utf-8");
        }
        catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    /**
     * implements just for abstarct function, used in clients side
     * @return
     */
    public byte [] encode (){
        return null;
    }

    /**
     * process the post, first check if the user is login, after that send notification to all the followers of the user with the post,
     * then check if there is any tags in the post if there is send to unfollowers only.
     * @param db
     * @param ConnectionID
     * @param connections
     * @return
     */
    public Message process(DataBase db , int ConnectionID , Connections connections) {
        String username = db.ConnectionIdToUserName(ConnectionID);
        if ((username==null)||(db.getClientsInfo(username) == null) || (!db.getClientsInfo(username).isLoggedIn())) {
            ErrorMessage err = new ErrorMessage((short) 5);
            return err;
        }
        else{
            LinkedList<String> Tagged=new LinkedList<>();
            LinkedList <String> sendPost = db.getClientsInfo(username).getListOfFollowingMe();
            Iterator <String> iter = sendPost.iterator();
            while(iter.hasNext()){
                String user = iter.next();
                NotificationMessage notificationMessage = new NotificationMessage(username , content , false, user);
                synchronized (db.getClientsInfo(user)) {// sync the username for not loosing posts from user that the clients want to add from the followers list, and symetrics
                    if (db.getClientsInfo(username).isFollowingMe(user)) {
                        if (db.getClientsInfo(user).isLoggedIn()) {
                            connections.send((Integer) db.getUsersList().get(user), notificationMessage);
                        } else {
                            db.AddFutureMessage(user, notificationMessage);
                        }
                    }
                }
            }
            int i = content.indexOf('@',0);
            while (i != -1){
                String tagged;
                if(content.indexOf(' ', i)!=-1) {
                     tagged = content.substring(i + 1, content.indexOf(' ', i));
                }
                else
                     tagged = content.substring(i + 1);

                NotificationMessage notificationMessage;
                if ((!db.getClientsInfo(username).isFollowingMe(tagged))&!Tagged.contains(tagged)) {
                    Tagged.add(tagged);
                    notificationMessage = new NotificationMessage(username, content, false, tagged);
                    if (db.getClientsInfo(tagged) != null) {
                        synchronized (db.getClientsInfo(tagged)) {
                            if (db.getClientsInfo(tagged).isLoggedIn()) {
                                connections.send((Integer) db.getUsersList().get(tagged), notificationMessage);
                            } else {
                                db.AddFutureMessage(tagged, notificationMessage);
                            }
                        }
                    }
                }
                i = content.indexOf('@',i+1);
            }
            db.getClientsInfo(username).addPost(this);
            AckMessage ack = new AckMessage((short) 5);
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
        else{
            _bytes.add(nextByte);
            translate(ToArray(_bytes));
            decodingFinished = true;
        }
        return decodingFinished;
    }

}
