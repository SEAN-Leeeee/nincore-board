package com.nincore.nincoreboardapi.controller;

import com.nincore.nincoreboardapi.domain.Action;
import com.nincore.nincoreboardapi.domain.GameState;
import com.nincore.nincoreboardapi.service.StateService;
import com.nincore.nincoreboardapi.service.WebSocketSessionRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;

import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class StateWsController {

    private final StateService stateService;
    private final WebSocketSessionRegistry webSocketRegistry;

    @MessageMapping("/command")
    public void onCommand(Action cmd) {
        stateService.apply(cmd);
    }

    @MessageMapping("/state")
    @SendTo("/subscribe/state")
    public GameState getState(SimpMessageHeaderAccessor headerAccessor) {
        String simpSessionId = headerAccessor.getSessionId();
        if (simpSessionId == null) {
            return null;
        }

        Integer boardSessionId = webSocketRegistry.getBoardSessionId(simpSessionId);
        if (boardSessionId == null) {
            return null;
        }
        return stateService.get(boardSessionId);
    }
}
