package com.nincore.nincoreboardapi.controller;

import com.nincore.nincoreboardapi.domain.Action;
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
    public void onCommand(Action cmd) {
        System.out.println("@@@@");
        System.out.println(cmd.toString());
        System.out.println("@@@@");
        stateService.apply(cmd);
    }

    @MessageMapping("/state")
    @SendTo("/subscribe/state")
    public GameState getState() {
        return stateService.get();
    }
}
