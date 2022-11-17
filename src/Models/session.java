package Models;


public class session {
    public double loginStartTime = 0;
    public boolean isSessionExpired = true;
    public int expirationTime = 3 * 60 * 1000; // the expiration time of each session is 3 mins;
    public String uid;

    public session(String uid) {
        this.uid = uid;
    }
}
