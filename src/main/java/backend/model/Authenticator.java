package backend.model;

import java.util.Arrays;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class Authenticator <T> {

    private final long expireInSeconds;
    private final ConcurrentMap<String, Session> valToSession = new ConcurrentHashMap<>();
    private final ConcurrentMap<T, Session> uidToSession = new ConcurrentHashMap<>();

    public Authenticator(long expireInSeconds) {
        this.expireInSeconds = expireInSeconds;
    }

    public String login(T userId) {
        Session currentSession = uidToSession.computeIfAbsent(userId,  k -> new Session(userId, expireInSeconds));
        synchronized (uidToSession) {
            if (currentSession.isExpired()) {
                Session session = new Session(userId, expireInSeconds);
                uidToSession.replace(userId, currentSession, session);
                currentSession = session;
            }
        }
        valToSession.put(currentSession.getValue(), currentSession);
        return currentSession.getValue();
    }

    public boolean isLoggedIn(String sessionValue) {
        Session session = valToSession.get(sessionValue);
        return session != null && !session.isExpired();
    }

    public <T>Optional getUserId(String sessionKey) {
        return Optional.ofNullable(valToSession.get(sessionKey))
                .map(Session::getUserId);
    }

    public void removeExpiredSessions() {
        Arrays.asList(valToSession, uidToSession).forEach(
                sessionMap -> sessionMap.forEach( (sessId, session) -> {
                    if (session.isExpired()) {
                        sessionMap.remove(sessId, session);
                    }
                })
        );
    }

}
