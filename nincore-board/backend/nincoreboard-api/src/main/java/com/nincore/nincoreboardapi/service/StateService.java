package com.nincore.nincoreboardapi.service;

import com.nincore.nincoreboardapi.domain.Command;
import com.nincore.nincoreboardapi.domain.GameState;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicReference;

@Service
@RequiredArgsConstructor
public class StateService {

    private final SimpMessagingTemplate messagingTemplate;

    private final AtomicReference<GameState> state = new AtomicReference<>(
            GameState.builder()
                    .homeName("HOME")
                    .awayName("AWAY")
                    .homeScore(0)
                    .awayScore(0)
                    .totalTime("7:00")
                    .shotClock(24)
                    .build()
    );

    public GameState get() {
        return state.get();
    }

    public GameState apply(Command cmd) {
        GameState s = state.get();

        switch (cmd.getType()) {
            case "HOME_SCORE_PLUS_1"  -> s.setHomeScore(s.getHomeScore() + 1);
            case "AWAY_SCORE_PLUS_1"  -> s.setAwayScore(s.getAwayScore() + 1);
            case "SET TOTAL_TIME" -> s.setTotalTime(cmd.getPayload());
            case "SHOT_RESET_24" -> s.setShotClock(24);
            case "SHOT_RESET_14" -> s.setShotClock(14);
        }

        state.set(s);
        messagingTemplate.convertAndSend("/topic/state", s);
        return s;
    }

}
