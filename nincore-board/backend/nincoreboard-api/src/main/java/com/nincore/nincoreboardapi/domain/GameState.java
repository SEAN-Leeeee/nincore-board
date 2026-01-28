package com.nincore.nincoreboardapi.domain;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Slf4j
public class GameState {
    private int sessionId;
    private String ip;
    private String password;
    private int quarter;
    private int gameTime;
    private boolean isGameRunning;
    private int shotClock;
    private boolean isShotClockRunning;
    private  String homeName;
    private int homeScore;
    private int homeFoul;
    private String awayName;
    private int awayScore;
    private int awayFoul;
    private JsonNode players;
    private JsonNode rosterPlayers;

    public void setPlayers(JsonNode players) {
        if (players != null) this.players = players;
    }

    public void setRosterPlayers(JsonNode rosterPlayers) {
        if (rosterPlayers != null) this.rosterPlayers = rosterPlayers;
    }

    public void setQuarter(int quarter) { this.quarter = quarter; }
    public void setGameTime(int gameTime) { this.gameTime = gameTime; }
    public void setIsGameRunning(boolean isGameRunning) { this.isGameRunning = isGameRunning; }
    public void setShotClock(int shotClock) { this.shotClock = shotClock; }
    public void setIsShotClockRunning(boolean isShotClockRunning) { this.isShotClockRunning = isShotClockRunning; }
    public void setHomeName(String homeName) { this.homeName = homeName; }
    public void setHomeScore(int homeScore) { this.homeScore = homeScore; }
    public void setHomeFoul(int homeFoul) { this.homeFoul = homeFoul; }
    public void setAwayName(String awayName) { this.awayName = awayName; }
    public void setAwayScore(int awayScore) { this.awayScore = awayScore; }
    public void setAwayFoul(int awayFoul) { this.awayFoul = awayFoul; }

    public void minusGameTime(int i) {
        this.gameTime = i;
    }
    public void minusShotClock(int i) {
        this.shotClock = i;
    }

    public void setSessionId(int sessionId) {
        this.sessionId = sessionId;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setGameTime(JsonNode payload) {
        this.isGameRunning = payload.get("isRunning").asBoolean();
        if (payload.get("isReset").asBoolean()) {
            this.gameTime = payload.get("resetTime").asInt();
        } else if (payload.get("adjust").asInt() != 0) {
            this.gameTime += payload.get("adjust").asInt();
        }
    }

    public void setShotClock(JsonNode payload) {
        this.isShotClockRunning = payload.get("isRunning").asBoolean();

        if (payload.get("isReset").asBoolean()) {
            this.shotClock = 24;
        } else if (payload.get("isSetHalf").asBoolean()) {
            this.shotClock = 14;
        } else if (payload.get("adjust").asInt() != 0) {
            this.shotClock += payload.get("adjust").asInt();
        }
    }

    public void setQuarter(JsonNode payload) {
        this.quarter = payload.get("quarter").asInt();
    }

    public void setHomeName(JsonNode payload) {
        this.homeName = payload.get("homeName").asText();
    }
    public void setHomeScore(JsonNode payload) {
        this.homeScore  += payload.get("score").asInt();
    }
    public void setHomeFoul(JsonNode payload) {
        this.homeFoul += payload.get("foul").asInt();
    }
    public void setAwayName(JsonNode payload) {
        this.awayName = payload.get("awayName").asText();
    }
    public void setAwayScore(JsonNode payload) {
        this.awayScore += payload.get("score").asInt();
    }
    public void setAwayFoul(JsonNode payload) {
        this.awayFoul += payload.get("foul").asInt();
    }
    public void stopGameRunning(boolean state) {
        this.isGameRunning = state;
    }

    public void stopShotClock(boolean state) {
        this.isShotClockRunning = state;
    }

    public void toggleGameTime(JsonNode payload) {
        this.isGameRunning = payload.get("isRunning").asBoolean();
    }

    public void toggleShotClock(JsonNode payload) {
        this.isShotClockRunning = payload.get("isRunning").asBoolean();
    }

    public void resetHome(JsonNode payload) {
        this.homeName = payload.get("homeName").asText();
        this.homeScore =  payload.get("homeScore").asInt();
        this.homeFoul =  payload.get("homeFoul").asInt();
    }

    public void resetAway(JsonNode payload) {
        this.awayName =  payload.get("awayName").asText();
        this.awayScore = payload.get("awayScore").asInt();
        this.awayFoul = payload.get("AwayFoul").asInt();
    }
}
