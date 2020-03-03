package bgu.spl.net.api.Messages;

import bgu.spl.net.api.bidi.Connections;
import bgu.spl.net.srv.DataBase;

import java.io.UnsupportedEncodingException;
import java.util.LinkedList;
import java.util.List;

public class NotificationMessage extends Message {
    private boolean isPrivate;
    private String PostingUser;
    private String content;
    private String Destination;

    /**
     * empty constructor
     */
    public NotificationMessage(){
        super();
    }

    /**
     * constructor that for post/ pm message
     * @param posting_user
     * @param cont
     * @param isPM
     * @param Dest
     */
    public NotificationMessage(String posting_user , String cont , boolean isPM , String Dest){
        super();
        PostingUser = posting_user;
        content = cont;
        isPrivate = isPM;
        Destination = Dest;
    }

    /**
     * decode the function message
     * @param bytes
     */
    public void translate (byte [] bytes) {
    }

    /**
     * encode the function message to bytes before send back to the client
     * @return
     */
    public byte [] encode(){
        List<Byte> ret = new LinkedList<>();
        byte [] opCode = shortToBytes((short) 9);
        byte [] postUserBytes = null;
        byte [] contentBytes = null;
        ret.add(opCode[0]);
        ret.add(opCode[1]);
        if (isPrivate){
            ret.add((byte)'0');
        }
        else{
            ret.add((byte)'1');
        }
        try {
            postUserBytes =  PostingUser.getBytes("utf-8");
            contentBytes = content.getBytes("utf-8");
        }
        catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        for (int i=0 ; i < postUserBytes.length ; i++)
            ret.add(postUserBytes[i]);
        ret.add((byte)'\0');

        for (int i=0 ; i < contentBytes.length ; i++)
            ret.add(contentBytes[i]);
        ret.add((byte)'\0');

        return ToArray(ret);
    }

    /**
     * implements just for abstarct function, used in clients side
     * @param db
     * @param ConnectionID
     * @param connections
     * @return
     */
    public Message process(DataBase db , int ConnectionID , Connections connections) {
        return null;
    }

    public boolean decodeNextByte(byte nextByte){
        return true;
    }

}
