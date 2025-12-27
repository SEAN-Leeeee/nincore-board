package com.nincore.nincoreboardapi.service;

import com.nincore.nincoreboardapi.domain.GameState;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ClockService {
    private final StateService stateService;
    private final SimpMessagingTemplate simpMessagingTemplate;

    @Scheduled(fixedRate = 1000)
    public void tick() {
        GameState currentState = stateService.get();

        if (currentState.isGameRunning()) {
            if(currentState.getGameTime() > 0) {
                currentState.minusGameTime(currentState.getGameTime() - 1);
            } else {
                currentState.stopGameRunning(false);
            }
        }

        if (currentState.isShotClockRunning()) {
            if (currentState.getShotClock() > 0) {
                currentState.minusShotClock(currentState.getShotClock() -1);
            } else {
                currentState.stopShotClock(false);
            }
        }

        simpMessagingTemplate.convertAndSend("/subscribe/state", currentState);
    }
}
