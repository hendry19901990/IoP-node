package org.iop.version_1.structure.context;

import javax.websocket.Session;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by mati on 11/08/16.
 */
public class SessionManager {


    private static SessionManager instance = new SessionManager();;
    /**
     * Holds all client sessions
     * SessionId+Client
     */
    private final Map<String , Session> clientSessionsById;

    /**
     * Constructor
     */
    private SessionManager() {
        clientSessionsById = new ConcurrentHashMap<>();
    }

    /**
     * Return the singleton instance
     *
     * @return ClientsSessionMemoryCache
     */
    public static SessionManager getInstance(){
        return instance;
    }

    /**
     * Get the session client
     *
     * @param sessionId the client identity
     * @return the session of the client
     */
    public Session get(String sessionId){

        /*
         * Return the session of this client
         */
        return getInstance().clientSessionsById.get(sessionId);
    }

    /**
     * Add a new session to the memory cache
     *
     * @param session the client session
     */
    public void add(final Session session){

        /*
         * Add to the cache
         */
        getInstance().clientSessionsById.put(session.getId(), session);
    }

    /**
     * Remove the session client
     *
     * @param session the session of the connection
     * @return the id of the session
     */
    public String remove(Session session){

        /*
         * remove the session of this client
         */
        try {
            if (getInstance().clientSessionsById.containsKey(session.getId())) {
                System.out.println("removing session");
                getInstance().clientSessionsById.remove(session.getId());
                return null;
            }

        }catch (Exception e){
            e.printStackTrace();
        }

        return "";
    }

    /**
     * Verify is exist a session for a session id
     *
     * @param sessionId the session id
     *
     * @return (TRUE or FALSE)
     */
    public boolean exist(String sessionId){

        return getInstance().clientSessionsById.containsKey(sessionId);
    }

}
