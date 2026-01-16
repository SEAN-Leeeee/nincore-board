package com.nincore.nincoreboardapi.controller;

import com.nincore.nincoreboardapi.dto.WebSocketRegisterRequest;
import com.nincore.nincoreboardapi.service.WebSocketSessionRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;

@Slf4j
@Controller
@RequiredArgsConstructor
public class WebSocketController {

    private final WebSocketSessionRegistry webSocketRegistry;

    @MessageMapping("/session/register")
    public void registerSession(WebSocketRegisterRequest request, SimpMessageHeaderAccessor headerAccessor) {
        String webSocketSessionId = headerAccessor.getSessionId();
        String boardSessionId = request.getSessionId();

        log.debug("webSocketConnected! = {} ", boardSessionId);
        if (webSocketSessionId != null && boardSessionId != null) {
            log.info("Registering WebSocket session {} for BoardSession {}", webSocketSessionId, boardSessionId);
            webSocketRegistry.registrySession(webSocketSessionId, boardSessionId);
        } else {
            log.warn("Failed to register WEbSocket session. WebSocketSessionId: {}, BoardSessionId: {}", webSocketSessionId, boardSessionId);
        }
    }
}
