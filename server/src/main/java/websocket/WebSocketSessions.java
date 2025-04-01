package websocket;

import org.eclipse.jetty.websocket.api.Session;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class WebSocketSessions {

    private final ConcurrentHashMap<Integer, Set<Session>> sessionMap = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Session, Integer> reverseMap = new ConcurrentHashMap<>();

    public void addSessionToGame(Integer gameID, Session session) {
        sessionMap.compute(gameID, (k, set) -> {
            if (set == null) {
                set = ConcurrentHashMap.newKeySet();
            }
            set.add(session);
            return set;
        });
        reverseMap.put(session, gameID);
    }

    public void removeSessionFromGame(Integer gameID, Session session) {
        sessionMap.computeIfPresent(gameID, (k, set) -> {
            set.remove(session);
            return set.isEmpty() ? null : set;
        });
        reverseMap.remove(session);
    }

    public void removeSession(Session session) {
        sessionMap.forEach((gameID, set) -> {
            if (set.remove(session) && set.isEmpty()) {
                sessionMap.remove(gameID);
            }
        });
        reverseMap.remove(session);
    }

    public Set<Session> getSessionsForGame(Integer gameID) {
        return sessionMap.get(gameID);
    }

    public Integer getIDForSession(Session session) {
        return reverseMap.get(session);
    }
}
