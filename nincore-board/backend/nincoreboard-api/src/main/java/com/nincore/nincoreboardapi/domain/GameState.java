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

    public void minusGameTime(int i) {
        this.gameTime = i;
    }
    public void minusShotClock(int i) {
        this.shotClock = i;
    }

    public void setGameTime(JsonNode payload) {
        this.isGameRunning = payload.get("isRunning").asBoolean();
        if (payload.get("isReset").asBoolean()) {
            this.gameTime = payload.get("resetTime").asInt();
        } else if (payload.get("adjust").asInt() != 0) {
            this.gameTime = payload.get("adjust").asInt();
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
}
