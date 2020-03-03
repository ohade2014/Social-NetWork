package bgu.spl.net.api.bidi;

import bgu.spl.net.api.Messages.AckMessage;
import bgu.spl.net.srv.BlockingConnectionHandler;
import bgu.spl.net.srv.ConnectionHandler;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class ConnectionsImpl <T> implements Connections<T> {

    private ConcurrentHashMap <Integer , ConnectionHandler> clients_map = new ConcurrentHashMap<>();
    private ConcurrentHashMap <String , Integer> UsernameToId;
    private AtomicInteger idCounter=new AtomicInteger();

    /**
     *send message from the Connections to the correct connection handler
     * @param connectionId
     * @param msg
     * @return true to continue deliver message to the ch
     */
    public synchronized boolean send(int connectionId, T msg){
        ConnectionHandler connection_handler = clients_map.get(connectionId);
        if (connection_handler == null)
            return false;
        else{
            connection_handler.send(msg);
            if (msg instanceof AckMessage){
             int opCode=((AckMessage) msg).getOpCode();
                if(opCode==3)
                    this.disconnect(connectionId);
            }
            return true;
        }
    }

    /**
     * send message to all the connection handler that subscribe in the connections
     * @param msg
     */
    public void broadcast(T msg){
        for (Integer id : clients_map.keySet()){
            clients_map.get(id).send(msg);
        }
    }

    /**
     * remove specific connection handler from the connections
     * @param connectionId
     */
    public void disconnect(int connectionId){
        clients_map.remove(connectionId);
    }

    /**
     *
     * @param id
     * @return matching ch for specific id
     */
    public ConnectionHandler getConnectionHandler(int id){
        return clients_map.get(id);
    }

    /**
     *use to provied a uniqe id to each ch
     * @return add one to the counter "idCounter" and return the Counter.
     */
    public int getConnectionId(){
        return  idCounter.addAndGet(1);
    }

    /**
     * add ch to the map "Connections"
     * @param id
     * @param handler
     */
    public void AddConnection(int id , ConnectionHandler handler){
        clients_map.put(id,handler);
    }
}

