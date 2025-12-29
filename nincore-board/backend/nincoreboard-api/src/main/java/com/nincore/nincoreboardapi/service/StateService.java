package com.nincore.nincoreboardapi.service;

import com.nincore.nincoreboardapi.domain.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicReference;

@Service
@RequiredArgsConstructor
@Slf4j
public class StateService {

    private final SimpMessagingTemplate messagingTemplate;

    private final AtomicReference<GameState> state = new AtomicReference<>(
            GameState.builder()
                    .homeName("HOME")
                    .awayName("AWAY")
                    .quarter(1)
                    .homeScore(0)
                    .awayScore(0)
                    .homeFoul(0)
                    .awayFoul(0)
                    .awayScore(0)
                    .gameTime(7*60)
                    .shotClock(24)
                    .build()
    );

    public GameState get() {
        return state.get();
    }

    public GameState apply(Action cmd) {
        GameState s = state.get();

        state.set(s);

        if("QUARTER".equals((cmd.getType()))) {
            s.setQuarter(cmd.getPayload());
            messagingTemplate.convertAndSend("/subscribe/state", s);
            return s;
        }
        log.debug("CMD = " + cmd.getType() + ", payload=" + cmd.getPayload());


        switch (cmd.getType()) {

            case "HOME_NAME" -> s.setHomeName(cmd.getPayload());
            case "HOME_SCORE" -> s.setHomeScore(cmd.getPayload());
            case "HOME_FOUL" -> s.setHomeFoul(cmd.getPayload());

            case "AWAY_NAME" -> s.setAwayName(cmd.getPayload());
            case "AWAY_SCORE" -> s.setAwayScore(cmd.getPayload());
            case "AWAY_FOUL" -> s.setAwayFoul(cmd.getPayload());

            case "TOGGLE_GAME_TIME" -> s.toggleGameTime(cmd.getPayload());
            case "TOGGLE_SHOT_CLOCK" -> s.toggleShotClock(cmd.getPayload());


            case "GAME_TIME_SETTING" -> s.setGameTime(cmd.getPayload());
            case "SHOT_CLOCK_SETTING" -> s.setShotClock(cmd.getPayload());
            default -> log.warn("처리되지 않은 ActionType 입니다: {} ", cmd.getType());
        }

        messagingTemplate.convertAndSend("/subscribe/state", s);
        return s;
    }

}
