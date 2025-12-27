package com.nincore.nincoreboardapi.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nincore.nincoreboardapi.domain.*;
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

    private String normalizeJson(String payload) {
        if (payload == null) return null;
        String p = payload.trim();
        ObjectMapper objectMapper = new ObjectMapper();

        if (p.startsWith("\"") && p.endsWith("\"")) {
            try {
                return objectMapper.readValue(p, String.class);
            } catch (Exception e) {
                throw new RuntimeException("payload unwrap failed: " + p, e);
            }
        }
        return p;
    }


    private <T> T parseToGameState (String payload, Class<T> classT) {
        ObjectMapper objectMapMapper = new ObjectMapper();

        try {
            String json = normalizeJson(payload);
            return objectMapMapper.readValue(json, classT);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public GameState apply(Action cmd) {
        System.out.println("CMD = " + cmd.getType() + ", payload=" + cmd.getPayload());
        GameState s = state.get();

        state.set(s);

        if("QUARTER".equals((cmd.getType()))) {
            s.setQuarter(Integer.parseInt(cmd.getPayload()));
            messagingTemplate.convertAndSend("/subscribe/state", s);
            return s;
        }


        switch (cmd.getType()) {
            case "HOME_SETTING" -> {
                HomeState hs = parseToGameState(cmd.getPayload(), HomeState.class);
                s.setHome(hs);
            }
            case "AWAY_SETTING" -> {
                AwayState as = parseToGameState(cmd.getPayload(), AwayState.class);
                s.setAway(as);
            }
            case "GAME_TIME_SETTING" -> {
                GameTime gt = parseToGameState(cmd.getPayload(), GameTime.class);
                s.setGameTime(gt);
            }
            case "SHOT_CLOCK_SETTING" -> {
                ShotClock sct = parseToGameState(cmd.getPayload(), ShotClock.class);
                s.setShotClock(sct);
            }

        }


        messagingTemplate.convertAndSend("/subscribe/state", s);
        return s;
    }

}
