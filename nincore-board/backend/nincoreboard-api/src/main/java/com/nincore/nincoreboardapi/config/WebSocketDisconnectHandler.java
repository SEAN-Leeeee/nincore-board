package com.nincore.nincoreboardapi.config;

import com.nincore.nincoreboardapi.dto.LogoutRequest;
import com.nincore.nincoreboardapi.service.SessionService;
import com.nincore.nincoreboardapi.service.WebSocketSessionRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Slf4j
@Component
@RequiredArgsConstructor
public class WebSocketDisconnectHandler implements ApplicationListener<SessionDisconnectEvent> {

    private final WebSocketSessionRegistry webSocketSessionRegistry;
    private final SessionService sessionService;
    private final SimpMessagingTemplate messagingTemplate;

    @Override
    public void onApplicationEvent(SessionDisconnectEvent event) {
        String webSocketSessionId = event.getSessionId();
        if (webSocketSessionId == null) {
            return;
        }

        log.info("WebSocket session disconnected: {}", webSocketSessionId);

        webSocketSessionRegistry.getBoardSessionId(webSocketSessionId).ifPresent(boardSessionId -> {
            log.info("Found BoardSession {} associated with the disconnected WebSocket session. Cleaning up.\", boardSessionId)");

            sessionService.logout(new LogoutRequest(boardSessionId));
            log.info("BoardSession {} has been deleted from the database.", boardSessionId);

            String destination = "/subcribe/session/" + boardSessionId;
            messagingTemplate.convertAndSend(destination, "{\"status\":\"TERMINATED\"}");
            log.info("Sent TERMINATED signal to destination: {}", destination);

            webSocketSessionRegistry.unregisterSession(webSocketSessionId);
            log.info("Unregistered WebSocket session {}.", webSocketSessionId);

        });
    }
}
