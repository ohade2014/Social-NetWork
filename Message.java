package bgu.spl.net.api.Messages;

import bgu.spl.net.api.bidi.Connections;
import bgu.spl.net.srv.DataBase;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public abstract class Message {
    public Message(){

    }

    /**
     * implements in the right instance class
     * @param bytes
     */
    public abstract void translate (byte [] bytes);

    /**
     * implements in the right instance class
     * @param db
     * @param ConnectionID
     * @param connections
     * @return
     */
    public abstract Message process(DataBase db , int ConnectionID, Connections connections);

    /**
     * implements in the right instance class
     * @param nextByte
     * @return
     */
    public abstract boolean decodeNextByte(byte nextByte);

    /**
     * implements in the right instance class
      * @return
     */
    public abstract byte[] encode ();

    /**
     * ToArray is a function that reform the linkedlist to array because the buffer reads from byets array.
     * @param bytes
     * @return
     */
    protected byte [] ToArray (List<Byte> bytes){
        byte [] result = new byte [bytes.size()];
        int i = 0;
        Iterator<Byte> iter = bytes.iterator();
        while (iter.hasNext()){
            result[i] = iter.next();
            i++;
        }
        return result;
    }

    /**
     * short that match to the input byte array
     * @param num
     * @return
     */
    protected byte[] shortToBytes (short num){
        byte[] bytesArr = new byte [2];
        bytesArr[0] = (byte) ((num >> 8) & 0xFF);
        bytesArr[1] = (byte) (num & 0xFF);
        return bytesArr;
    }
}

