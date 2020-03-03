package bgu.spl.net.srv;

import bgu.spl.net.api.Messages.Message;
import bgu.spl.net.api.bidi.Connections;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class DataBase {
    private ConcurrentHashMap<String , Integer> UserNameToConnectionId=new ConcurrentHashMap<>();
    private ConcurrentHashMap<Integer , String> ConnectionIdToUserName=new ConcurrentHashMap<>();
    private ConcurrentHashMap<String , ClientInfo> ClientsInformation=new ConcurrentHashMap<>();
    private ConcurrentHashMap<String , LinkedBlockingQueue<Message>> Future_Messages=new ConcurrentHashMap<>();
    private LinkedBlockingQueue<String> RegisterList=new LinkedBlockingQueue<>();
    private AtomicInteger numOfUsers = new AtomicInteger();

    /**
     * Create DateBase as singleton
     */
    private static class SingletonHolder {
        private static DataBase instance = new DataBase();
    }

    /**
     * Empty Constructor
     */
    private DataBase(){}

    /**
     *
     * @return Single instance of the data base
     */
    public static DataBase getInstance(){
        return SingletonHolder.instance;
    }

    /**
     * REturn the ClientInfo Object belongs to the username inserted
     * @param username
     * @return
     */
    public ClientInfo getClientsInfo(String username){
        if (username!=null)
            return ClientsInformation.get(username);
        return null;
    }

    /**
     * Registering Client to the db. adds its username and password to the list if its possible
     * @param username
     * @param password
     * @return true if register succeeded, false otherwise
     */
    public boolean RegisterClient(String username , String password){
        ClientInfo info = new ClientInfo(username,password);
        ClientInfo c_info = ClientsInformation.putIfAbsent(username,info);
        if (c_info==null) {
            numOfUsers.addAndGet(1);
            RegisterList.add(username);
            return true;
        }
        else{
            return false;
        }

    }

    /**
     * Logging in the inserted client by adding its details to the lists in db
     * @param username
     * @param conn_id
     * @param connections
     */
    public void LogInClient(String username , int conn_id , Connections connections){
        ClientsInformation.get(username).setLoggedIn(true);
        ConnectionIdToUserName.put(conn_id,username);
        UserNameToConnectionId.put(username,conn_id);
        LinkedBlockingQueue <Message> future_msg = Future_Messages.get(username);
        if (future_msg != null && future_msg.size()!=0){
            while (future_msg.size() > 0){
                connections.send(UserNameToConnectionId.get(username), future_msg.poll());
            }
        }
    }

    /**
     * Logging out client by changing its loggedIn value
     * @param username
     */
    public void LogOutClient(String username){
        ClientsInformation.get(username).setLoggedIn(false);
        ConnectionIdToUserName.remove(UserNameToConnectionId.get(username));
        UserNameToConnectionId.remove(username);
    }

    /**
     * Gets Connection Id and return matching User Name
     * @param connectionID
     * @return
     */
    public String ConnectionIdToUserName(int connectionID){
        return ConnectionIdToUserName.get(connectionID);
    }

    /**
     * @return number of registered users at the moment
     */
    public int getNumOfRegisterUsers(){
        return numOfUsers.intValue();
    }

    /**
     * @return the list connects the connection id to the username
     */
    public ConcurrentHashMap getConnectionList(){
        return ConnectionIdToUserName;
    }

    /**
     * @return the list connects the username to the connection id
     */
    public ConcurrentHashMap getUsersList(){
        return UserNameToConnectionId;
    }

    /**
     * Add msg to the list of messages belonges to the username that will be sent to him when he will log in
     * @param username
     * @param msg
     */
    public void AddFutureMessage(String username , Message msg){
        if (Future_Messages.get(username) == null){
            Future_Messages.put(username,new LinkedBlockingQueue<Message>());
        }
        Future_Messages.get(username).add(msg);
    }

    /**
     * @return all registered clients
     */
    public LinkedBlockingQueue<String> getRegisterList(){
        return this.RegisterList;
    }
}
