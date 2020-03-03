package bgu.spl.net.impl.BGSServer;

import bgu.spl.net.api.MessageEncoderDecoderImpl;
import bgu.spl.net.api.bidi.BidiMessagingProtocolImpl;
import bgu.spl.net.srv.DataBase;
import bgu.spl.net.srv.Server;

public class ReactorMain {

    public static void main(String[] args) {
        DataBase db =DataBase.getInstance(); //one shared object

        Server.reactor(
                Integer.parseInt(args[1]), //number of threads
                Integer.parseInt(args[0]), //port
                () ->  new BidiMessagingProtocolImpl(){}, //protocol factory
                MessageEncoderDecoderImpl::new //message encoder decoder factory
        ).serve();
    }
}
