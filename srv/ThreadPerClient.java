package bgu.spl.net.srv;
import bgu.spl.net.api.MessageEncoderDecoder;
import bgu.spl.net.api.Messages.Message;
import bgu.spl.net.api.bidi.BidiMessagingProtocolImpl;
import java.util.function.Supplier;


public class ThreadPerClient<T> extends BaseServer {
    public ThreadPerClient(int port,
                           Supplier<BidiMessagingProtocolImpl> protocolFactory,
                           Supplier<MessageEncoderDecoder<T>> encdecFactory){
        super( port, protocolFactory,encdecFactory);
    }
    protected void execute(BlockingConnectionHandler handler){
        new Thread(handler).start();
    }

}
