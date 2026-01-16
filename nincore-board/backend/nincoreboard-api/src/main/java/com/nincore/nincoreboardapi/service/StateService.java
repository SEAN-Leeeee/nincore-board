package com.nincore.nincoreboardapi.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.nincore.nincoreboardapi.domain.*;
import com.nincore.nincoreboardapi.repository.BoardSessionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

@Service
@RequiredArgsConstructor
@Slf4j
public class StateService {

    private final SimpMessagingTemplate messagingTemplate;
    private final BoardSessionRepository boardSessionRepository;

    private final Map<Integer, AtomicReference<GameState>> sessionStates = new ConcurrentHashMap<>();

    private GameState createInitialGameState() {
        return GameState.builder()
                .homeName("HOME")
                .awayName("AWAY")
                .quarter(1)
                .homeScore(0)
                .awayScore(0)
                .homeFoul(0)
                .awayFoul(0)
                .awayScore(0)
                .gameTime(7 * 60)
                .shotClock(24)
                .players(JsonNodeFactory.instance.objectNode())
                .rosterPlayers(JsonNodeFactory.instance.objectNode())
                .build();
    }

    public GameState get(int sessionId) {
        return sessionStates.computeIfAbsent(sessionId, id -> new AtomicReference<>(createInitialGameState())).get();
    }

    public GameState apply(Action cmd) {
        int sessionId = cmd.getSession();
        AtomicReference<GameState> stateRef = sessionStates.computeIfAbsent(sessionId, id -> new AtomicReference<>(createInitialGameState()));

        GameState newState = stateRef.get();

        String destination = "/subscribe/state/" + sessionId;

        if ("QUARTER".equals((cmd.getType()))) {
            newState.setQuarter(cmd.getPayload());
        } else {
            log.debug("CMD = " + cmd.getType() + ", payload=" + cmd.getPayload());

            switch (cmd.getType()) {

                case "AWAY_NAME" -> newState.setAwayName(cmd.getPayload());
                case "AWAY_SCORE" -> newState.setAwayScore(cmd.getPayload());
                case "AWAY_FOUL" -> newState.setAwayFoul(cmd.getPayload());

                case "TOGGLE_GAME_TIME" -> newState.toggleGameTime(cmd.getPayload());
                case "TOGGLE_SHOT_CLOCK" -> newState.toggleShotClock(cmd.getPayload());


                case "SETTING_GAME_TIME" -> newState.setGameTime(cmd.getPayload());
                case "SETTING_SHOT_CLOCK" -> newState.setShotClock(cmd.getPayload());

                case "RESET_HOME" -> newState.resetHome(cmd.getPayload());
                case "RESET_AWAY" -> newState.resetAway(cmd.getPayload());
                case "STATE_UPDATE" -> {
                    JsonNode payload = cmd.getPayload();
                    if (payload.has("quarter")) newState.setQuarter(payload.get("quarter").asInt());
                    if (payload.has("gameTime")) newState.setGameTime(payload.get("gameTime").asInt());
                    if (payload.has("isGameRunning"))
                        newState.setIsGameRunning(payload.get("isGameRunning").asBoolean());
                    if (payload.has("shotClock")) newState.setShotClock(payload.get("shotClock").asInt());
                    if (payload.has("isShotRunning"))
                        newState.setIsShotClockRunning(payload.get("isShotRunning").asBoolean());
                    if (payload.has("homeName")) newState.setHomeName(payload.get("homeName").asText());
                    if (payload.has("homeScore")) newState.setHomeScore(payload.get("homeScore").asInt());
                    if (payload.has("homeFoul")) newState.setHomeFoul(payload.get("homeFoul").asInt());
                    if (payload.has("awayName")) newState.setAwayName(payload.get("awayName").asText());
                    if (payload.has("awayScore")) newState.setAwayScore(payload.get("awayScore").asInt());
                    if (payload.has("awayFoul")) newState.setAwayFoul(payload.get("awayFoul").asInt());
                    if (payload.has("players")) newState.setPlayers(payload.get("players"));
                    if (payload.has("rosterPlayers")) newState.setRosterPlayers(payload.get("rosterPlayers"));
                }
                default -> log.warn("처리되지 않은 ActionType 입니다: {} ", cmd.getType());
            }
        }

        stateRef.set(newState);

        messagingTemplate.convertAndSend(destination, newState);
        return newState;
    }

    public void removeSessionState(int sessionId) {
        sessionStates.remove(sessionId);
        log.info("Removed GameState for session ID: {}", sessionId);
    }

    public Map<Integer, AtomicReference<GameState>> getAllSessionStates() {
        return sessionStates;
    }


}
