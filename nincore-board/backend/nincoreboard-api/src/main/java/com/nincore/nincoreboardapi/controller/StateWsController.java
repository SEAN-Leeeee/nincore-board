package com.nincore.nincoreboardapi.controller;

import com.nincore.nincoreboardapi.domain.Command;
import com.nincore.nincoreboardapi.domain.GameState;
import com.nincore.nincoreboardapi.service.StateService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class StateWsController {

    private final StateService stateService;

    @MessageMapping("/command")
    public void onCommand(Command cmd) {
        stateService.apply(cmd);
    }

    @MessageMapping("/state.get")
    @SendTo("topic/state")
    public GameState getState() {
        return stateService.get();
    }
}
