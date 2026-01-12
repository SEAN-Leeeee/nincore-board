package com.nincore.nincoreboardapi.service;

import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class WebSocketSessionRegistry {

    private final ConcurrentHashMap<String, String> sessionMap = new ConcurrentHashMap<>();

    public void registrySession(String webSocketSessionId, String boardSessionId) {
        sessionMap.put(webSocketSessionId, boardSessionId);
    }

    public Optional<String> getBoardSessionId(String webSocketSessionId) {
        return Optional.ofNullable(sessionMap.get(webSocketSessionId));
    }

    public void unregisterSession(String webSocketSessionId) {
        sessionMap.remove(webSocketSessionId);
    }
}
