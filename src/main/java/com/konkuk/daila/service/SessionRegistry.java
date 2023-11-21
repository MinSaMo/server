package com.konkuk.daila.service;

import com.konkuk.daila.service.enums.SessionType;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class SessionRegistry {
    private final Map<SessionType, WebSocketSession> sessionMap;

    public SessionRegistry() {
        sessionMap = new ConcurrentHashMap<>();
    }

    public Optional<WebSocketSession> getSession(SessionType type) {
        return Optional.ofNullable(sessionMap.get(type));
    }

    public boolean exist(SessionType type) {
        return sessionMap.containsKey(type);
    }

    public void register(WebSocketSession session, SessionType type) throws IllegalArgumentException {
        if (exist(type)) {
            throw new IllegalArgumentException("Already Exist Type");
        }

        sessionMap.put(type, session);
    }

    public void remove(WebSocketSession session, SessionType type) {
        if (!exist(type)) {
            throw new IllegalArgumentException("Session Not Found");
        }

        WebSocketSession sess = sessionMap.get(type);

        if (!sess.getId().equals(session.getId())) {
            throw new IllegalStateException("Invalid Session");
        }

        sessionMap.remove(type);
    }

}
