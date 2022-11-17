/***
 * Manage session, include session generation, killing, validation checking
 * @author Yingli Duan
 * @version 1.1
 */
package utils;

import Models.session;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class sessionManager {
    private static final ConcurrentHashMap<UUID, session> __session__ = new ConcurrentHashMap<>();

    /**
     * only one thread can use this function
     */
    public static synchronized UUID generateSession(String uid){
        session se = new session(uid);
        UUID token = UUID.randomUUID();
        se.isSessionExpired = false;
        se.loginStartTime = System.currentTimeMillis();
        __session__.put(token,se);
        return token;
    }

    /**
     * token not exist->false, session out of data->false
     */
    public static boolean isSessionValid(UUID token){
        session se = __session__.get(token);
        if(se==null) return false;
        //System.out.println(System.currentTimeMillis()+" "+(System.currentTimeMillis()-se.loginStartTime));
        se.isSessionExpired = ((System.currentTimeMillis()-se.loginStartTime)>se.expirationTime);
        if (se.isSessionExpired)
            killSession(token);
        return !se.isSessionExpired;
    }
    public static void killSession(UUID token){
        __session__.remove(token);
    }
   
}
