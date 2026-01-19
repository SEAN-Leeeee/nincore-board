package com.nincore.nincoreboardapi.config;

import com.nincore.nincoreboardapi.dto.LogoutRequest;
import com.nincore.nincoreboardapi.service.SessionService;
import com.nincore.nincoreboardapi.service.StateService;
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
    private final StateService stateService;

    @Override
    public void onApplicationEvent(SessionDisconnectEvent event) {
        String webSocketSessionId = event.getSessionId();
        if (webSocketSessionId == null) {
            return;
        }

        log.info("WebSocket session disconnected: {}", webSocketSessionId);

        Integer boardSessionId = webSocketSessionRegistry.getBoardSessionId(webSocketSessionId);

        if (boardSessionId != null) {
            log.info("Found BoardSession {} associated with the disconnected WebSocket session. Cleaning up.", boardSessionId);

            stateService.scheduleSessionCleanup(boardSessionId);
            log.info("Scheduled cleanup for BoardSession ID: {} on WebSocket disconnect.", boardSessionId);

            String destination = "/subscribe/session/" + boardSessionId;
            messagingTemplate.convertAndSend(destination, "{\"status\":\"TERMINATED\"}");
            log.info("Sent TERMINATED signal to destination: {}", destination);

            webSocketSessionRegistry.unregisterSession(webSocketSessionId);
            log.info("Unregistered WebSocket session {}.", webSocketSessionId);
        } else {
            log.warn("Disconnected WebSocket session {} was not associated with any BoardSession. No cleanup performed.", webSocketSessionId);
        }

    }
}
