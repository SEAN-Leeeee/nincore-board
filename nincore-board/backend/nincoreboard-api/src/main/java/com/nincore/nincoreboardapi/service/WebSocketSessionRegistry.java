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

    public Integer getBoardSessionId(String webSocketSessionId) {
        String boardSessionIdString = sessionMap.get(webSocketSessionId);

        if (boardSessionIdString == null) {
            return null;
        }

        try {
            return Integer.parseInt(boardSessionIdString);
        } catch (NumberFormatException e) {
            return null;
        }

    }


    public void unregisterSession(String webSocketSessionId) {
        sessionMap.remove(webSocketSessionId);
    }
}
