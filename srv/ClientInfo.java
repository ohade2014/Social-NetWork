package bgu.spl.net.srv;
import bgu.spl.net.api.Messages.PMMessage;
import bgu.spl.net.api.Messages.PostMessage;
import java.util.LinkedList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class ClientInfo {
    private String UserName;
    private String Password;
    private BlockingQueue<PostMessage> posts;
    private BlockingQueue<PMMessage> PMs;
    private ConcurrentHashMap <String, String> FollowList;
    private AtomicInteger numOfIFollow = new AtomicInteger();
    private AtomicInteger NumOfFollowingMe = new AtomicInteger();
    private AtomicInteger NumOfPosts = new AtomicInteger();
    private boolean isLoggedIn;

    /**
     * Constructor
     * @param UserName
     * @param Password
     */
    public ClientInfo(String UserName , String Password){
        this.UserName = UserName;
        this.Password = Password;
        posts = new LinkedBlockingQueue<>();
        PMs = new LinkedBlockingQueue<>();
        FollowList = new ConcurrentHashMap<>();
        isLoggedIn = false;
        NumOfPosts.set(0);
        numOfIFollow.set(0);
        NumOfFollowingMe.set(0);
    }

    /**
     * Return username of this client
     * @return
     */
    public String getUserName() {
        return UserName;
    }

    /**
     * @return Password of this client
     */
    public String getPassword(){
        return Password;
    }

    /**
     * @return posts that this client has made
     */
    public BlockingQueue<PostMessage> getPosts(){
        return posts;
    }

    /**
     * @return PMs that this client has sent
     */
    public BlockingQueue<PMMessage> getPMs(){
        return PMs;
    }

    /**
     * @return number of posts this client made
     */
    public AtomicInteger getNumberOfPosts(){
        return NumOfPosts;
    }

    /**
     * @return number of useres this user is following
     */
    public AtomicInteger getNumberOfIFollow(){
        return numOfIFollow;
    }

    /**
     * @return number of follower of this client
     */
    public AtomicInteger getNumberOfFollowingMe(){
        return NumOfFollowingMe;
    }

    /**
     * @return true if this client is logged in. false otherwise
     */
    public boolean isLoggedIn(){ return isLoggedIn; }

    /**
     * changes the status of loggedIn of this client
     * @param log
     */
    public void setLoggedIn(boolean log){ isLoggedIn = log; }

    /**
     * @param follower
     * @return true if this client follows follower. false otherwise
     */
    public boolean isIFollow(String follower){
        return FollowList.get(follower)!=null && (FollowList.get(follower).equals( "I Follow") | FollowList.get(follower).equals( "Both"));
    }

    /**
     * Add user to the list of my followers
     * @param user
     */
    public void AddFollowingMe(String user) {
        synchronized (FollowList) {
            if (FollowList.get(user) != null) {
                FollowList.put(user, "Both");
            } else {
                FollowList.put(user, "FollowingMe");
            }
            NumOfFollowingMe.addAndGet(1);
        }
    }

    /**
     * Add user to the list of my followings
     * @param user
     */
    public void AddIFollow(String user) {
        synchronized (FollowList) {
            if (FollowList.get(user) != null) {
                FollowList.put(user, "Both");
            } else {
                FollowList.put(user, "I Follow");
            }
            numOfIFollow.addAndGet(1);
        }
    }

    /**
     * Remove user from my followers list
     * @param user
     */
    public void removeFollowingMe(String user) {
        synchronized (FollowList) {
            if (FollowList.get(user).equals( "FollowingMe"))
                FollowList.remove(user);
            else if (FollowList.get(user).equals( "Both"))
                FollowList.put(user, "I Follow");
            NumOfFollowingMe.addAndGet(-1);
        }
    }

    /**
     * Remove user from my following list
     * @param user
     */
    public void removeIFollow(String user) {
        synchronized (FollowList) {
            if (FollowList.get(user).equals("I Follow"))
                FollowList.remove(user);
            else if (FollowList.get(user).equals("Both"))
                FollowList.put(user, "FollowingMe");
            numOfIFollow.addAndGet(-1);
        }
    }

    /**
     * @param follower
     * @return true if follower is following me. false otherwise
     */
    public boolean isFollowingMe(String follower){
        return FollowList.get(follower)!=null && (FollowList.get(follower).equals("FollowingMe") | FollowList.get(follower).equals("Both"));
    }

    /**
     * @return List of my followers
     */
    public LinkedList<String> getListOfFollowingMe(){
        LinkedList <String> FollowingMe=new LinkedList<>();
        for (String key:FollowList.keySet()) {
            if(!FollowList.get(key).equals("I Follow"))
                FollowingMe.add(key);
        }
        return FollowingMe;
    }

    /**
     * Adds post to this client list posts
     * @param post
     */
    public void addPost(PostMessage post){
        posts.add(post);
        NumOfPosts.addAndGet(1);
    }

    /**
     * adds PM to this client PMs list
     * @param post
     */
    public void addPM(PMMessage post){
        PMs.add(post);
    }

}
