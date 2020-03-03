package bgu.spl.net.api.bidi;

import bgu.spl.net.api.Messages.AckMessage;
import bgu.spl.net.api.Messages.LogoutMessage;
import bgu.spl.net.api.Messages.Message;
import bgu.spl.net.srv.BlockingConnectionHandler;
import bgu.spl.net.srv.ConnectionHandler;
import bgu.spl.net.srv.DataBase;

import java.util.Iterator;
import java.util.LinkedList;

public class BidiMessagingProtocolImpl implements BidiMessagingProtocol <Message> {
    private int ClinetId;
    private DataBase db=DataBase.getInstance();
    private Connections <Message> connections;
    private boolean shouldTerminate;
    /**
     * Used to initiate the current client protocol with it's personal connection ID and the connections implementation
     **/
    public void start(int connectionId, Connections <Message> connections){
        this.connections=connections;
        ClinetId=connectionId;
        shouldTerminate = false;
    }

    /**
     * processing the message and create matching response
     * @param message
     */
    public void process(Message message){
        Message response= message.process(db,ClinetId,connections);// use the specific message process function
        connections.send(ClinetId,response);
        if(response instanceof AckMessage && message instanceof LogoutMessage)
            shouldTerminate = true;
    }

    /**
     * @return true if the connection should be terminated
     */
    public boolean shouldTerminate(){
        return shouldTerminate;
    }

}

