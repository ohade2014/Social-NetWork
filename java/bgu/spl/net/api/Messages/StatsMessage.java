package bgu.spl.net.api.Messages;

import bgu.spl.net.api.bidi.Connections;
import bgu.spl.net.srv.DataBase;

import java.io.UnsupportedEncodingException;
import java.util.LinkedList;
import java.util.List;

public class StatsMessage extends Message {
    private String UserName;
    private List<Byte> _bytes = new LinkedList<Byte>();
    private boolean decodingFinished = false;

    /**
     * empty constructor
     */
    public StatsMessage() {
        super();
    }

    /**
     * decode the stats message before using in the protocol
     * @param bytes
     */
    public void translate(byte[] bytes) {
        try {
            UserName = new String(bytes, 0, bytes.length - 1, "utf-8");
        } catch (
                UnsupportedEncodingException e) {
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
     * process the stats message, add the right values and create a matching ACK / Error message for sending back
     * @param db
     * @param ConnectionID
     * @param connections
     * @return
     */
    public Message process(DataBase db, int ConnectionID , Connections connections) {
        String outPut = "";
        String username = db.ConnectionIdToUserName(ConnectionID);
        if (db.getClientsInfo(username)==null || !db.getClientsInfo(username).isLoggedIn() || db.getClientsInfo(UserName) == null) {
            ErrorMessage err = new ErrorMessage((short) 8);
            return err;
        } else {
            int NumOfPosts = db.getClientsInfo(UserName).getNumberOfPosts().intValue();
            int NumOfFollowers = db.getClientsInfo(UserName).getNumberOfFollowingMe().intValue();
            int NumOfFollowing = db.getClientsInfo(UserName).getNumberOfIFollow().intValue();
            AckMessage ack = new AckMessage((short) 8, NumOfPosts, NumOfFollowers, NumOfFollowing);
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
