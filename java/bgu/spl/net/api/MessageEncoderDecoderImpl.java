package bgu.spl.net.api;

import bgu.spl.net.api.Messages.*;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class MessageEncoderDecoderImpl implements MessageEncoderDecoder <Message> {

    private List<Byte> _bytes = new LinkedList<Byte>();
    private Message curr_message;

    @Override
    /**
     * add the next byte to the decoding process
     *
     * @param nextByte the next byte to consider for the currently decoded
     * message
     * @return a message if this byte completes one or null if it doesnt.
     */
    public Message decodeNextByte(byte nextByte) {
        if (_bytes.size()>=2){
            if(curr_message.decodeNextByte(nextByte)){
                _bytes.clear();
                return curr_message;
            }
            else
                return null;
        }
        else{
            _bytes.add(nextByte);
            if (_bytes.size() == 2){
                byte [] Opcode = ToArray(_bytes);
                CreateMessage(bytesToShort(Opcode,0));
                if (curr_message instanceof LogoutMessage){
                    _bytes.clear();
                    return curr_message;
                }
                else if(curr_message instanceof UserListMessage){
                    _bytes.clear();
                    return curr_message;
                }
            }
            return null;
        }
    }

    @Override
    /**
     * encodes the given message to bytes array
     *
     * @param message the message to encode
     * @return the encoded bytes
     */
    public byte[] encode(Message message) {

        return message.encode();
    }

    /**
     * Reforms Linked List to Array
     * @param bytes
     * @return
     */
    private byte [] ToArray (List <Byte> bytes){
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
     * Create message with the matching opCode
     * @param Opcode
     */
    private void CreateMessage (short Opcode){
        switch (Opcode){
            case 1:
                curr_message = new RegisterMessage();
                break;
            case 2:
                curr_message = new LoginMessage();
                break;
            case 3:
                curr_message = new LogoutMessage();
                break;
            case 4:
                curr_message = new FollowMessage();
                break;
            case 5:
                curr_message = new PostMessage();
                break;
            case 6:
                curr_message = new PMMessage();
                break;
            case 7:
                curr_message = new UserListMessage();
                break;
            case 8:
                curr_message = new StatsMessage();
                break;
            case 9:
                curr_message = new NotificationMessage();
                break;
            case 10:
                curr_message = new AckMessage();
                break;
            case 11:
                curr_message = new ErrorMessage();
                break;
        }
    }

    /**
     * Casting bytes to short
     * @param byteArr
     * @param i
     * @return short value of inserted bytes
     */
    private short bytesToShort(byte [] byteArr,int i){
        short result = (short) ((byteArr[i] & 0xff) << 8);
        result += (short) (byteArr[i+1] & 0xff);
        return result;
    }
}
