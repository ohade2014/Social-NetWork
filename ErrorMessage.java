package bgu.spl.net.api.Messages;

import bgu.spl.net.api.bidi.Connections;
import bgu.spl.net.srv.DataBase;

import java.util.LinkedList;
import java.util.List;

public class ErrorMessage extends Message {

    short MessageOP;

    /**
     * empty constructor
     */
    public ErrorMessage() {
        super();
    }

    /**
     * constructor that get the source message op code.
     * @param op
     */
    public ErrorMessage(short op){
        super();
        MessageOP = op;
    }

    /**
     * decode the op code bytes to the matching code
     * @param bytes
     */
    public void translate(byte[] bytes) {
        MessageOP = bytesToShort(bytes, 2);
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
     * encode the error message to bytes before sending back to the client.
     * @return
     */
    @Override
    public byte[] encode() {
        List<Byte> ret = new LinkedList<>();
        byte [] opCode = shortToBytes((short) 11);
        byte [] MessageOpBytes = shortToBytes((short) MessageOP);
        ret.add(opCode[0]);
        ret.add(opCode[1]);
        ret.add(MessageOpBytes[0]);
        ret.add(MessageOpBytes[1]);
        return ToArray(ret);
    }

    /**
     *  implements just for abstract function, used only in client side
     * @param db
     * @param ConnectionID
     * @param connections
     * @return
     */
    public Message process(DataBase db , int ConnectionID , Connections connections) {
        return null;
    }

    /**
     * implements just for abstract function, used only in client side
     * @param nextByte
     * @return
     */
    public boolean decodeNextByte(byte nextByte){
        return true;
    }
}
