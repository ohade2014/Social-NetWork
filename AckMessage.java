package bgu.spl.net.api.Messages;

import bgu.spl.net.api.bidi.Connections;
import bgu.spl.net.srv.DataBase;

import java.io.UnsupportedEncodingException;
import java.util.LinkedList;
import java.util.List;

public class AckMessage extends Message {
    private short OpCode;
    private String OptionalData;
    private int OptionalNumber;
    private int Followers;
    private int Following;

    /**
     *  empty constructor
     */
    public AckMessage() {
        super();
    }

    /**
     * constructor that get the op code of the source message
     * @param op
     */
    public AckMessage(short op) {
        super();
        OpCode = op;
    }

    /**
     * constructor that get the op code of the source message and get an optional data.
     * @param op
     * @param opt
     */
    public AckMessage(short op, String opt) {
        super();
        OpCode = op;
        OptionalData = opt;
    }

    /**
     * constructor that get the op code of the source message, get an optional data and optional number
     * @param op
     * @param opt
     * @param num
     */
    public AckMessage(short op, String opt, int num) {
        super();
        OpCode = op;
        OptionalData = opt;
        OptionalNumber = num;

    }

    /**
     * constructor for StatsMessage ACK
     * @param op
     * @param numPosts
     * @param numFollowers
     * @param numFollowing
     */
    public AckMessage(short op, int numPosts, int numFollowers, int numFollowing) {
        super();
        OpCode = op;
        OptionalNumber = numPosts;
        Followers = numFollowers;
        Following = numFollowing;
    }

    /**
     * implements empty just for abstract function, used in clients only
     * @param bytes
     */
    public void translate(byte[] bytes) {
    }

    /**
     * used to encode ACK message before send back an ACK to the client
     * @return
     */
    @Override
    public byte[] encode() {
        List<Byte> ret = new LinkedList<>();
        byte[] opCode = shortToBytes((short) 10);
        byte[] MessageOpBytes = shortToBytes(OpCode);
        ret.add(opCode[0]);
        ret.add(opCode[1]);
        ret.add(MessageOpBytes[0]);
        ret.add(MessageOpBytes[1]);
        if(OpCode==(short)4|OpCode==(short)7|OpCode==(short)8){ // each message used diffrents field, so we fill the "ret" with the matching ones.
            byte[]num_users = shortToBytes((short) OptionalNumber);
            ret.add(num_users[0]);
            ret.add(num_users[1]);
        }
        if (OpCode == (short)4 | OpCode==(short)7){ // add the followers list (op=4) or the userlist that register (op=7) to the return bytes array
            String [] names_list = OptionalData.split(" ");
            byte [] bytesToAdd = null;
            for (int i = 0 ; i < names_list.length ; i++){
                try {
                    bytesToAdd = names_list[i].getBytes("utf-8");
                }
                catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                for (int j = 0 ; j < bytesToAdd.length ; j++){
                    ret.add(bytesToAdd[j]);
                }
                ret.add((byte) '\0');
            }
        }
        else if(OpCode==(short)8){
            byte[]num_followers = shortToBytes((short) Followers);
            ret.add(num_followers[0]);
            ret.add(num_followers[1]);
            byte[]num_following = shortToBytes((short) Following);
            ret.add(num_following[0]);
            ret.add(num_following[1]);
        }
        return ToArray(ret); // ToArray is a function that reform the linkedlist to array because the buffer reads from byets array.
    }

    /**
     * implements empty just for abstract function, used in client side.
     * @param db
     * @param ConnectionID
     * @param connections
     * @return
     */
    public Message process(DataBase db, int ConnectionID , Connections connections) {
        return null;
    }

    /**
     * implements empty just for abstract function, used in client side.
     * @param nextByte
     * @return
     */
    public boolean decodeNextByte(byte nextByte){
        return true;
    }

    /**
     *
     * @return op code
     */
    public int getOpCode(){
        return OpCode;
    }
}