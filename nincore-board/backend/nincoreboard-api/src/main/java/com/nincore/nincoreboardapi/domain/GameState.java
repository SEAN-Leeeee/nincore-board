package com.nincore.nincoreboardapi.domain;

import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
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
        this.gameTime -= i;
    }

    public void minusShotClock(int i) {
        this.shotClock -= i;
    }

    public void setGameTime(GameTime payload) {
        this.isGameRunning = payload.isRunning();

        if (payload.isReset()) {
            this.gameTime = payload.getResetTime();
        } else if (payload.getAdjust() != 0) {
            this.gameTime = payload.getAdjust();
        }
    }

    public void setShotClock(ShotClock payload) {
        this.isShotClockRunning = payload.isRunning();

        if (payload.isReset) {
            this.shotClock = 24;
        } else if (payload.isSetHalf()) {
            this.shotClock = 14;
        } else if (payload.getAdjust() != 0) {
            this.shotClock += payload.getAdjust();
        }
    }

    public void setQuarter(int quarter) {
        this.quarter = quarter;
    }

    public void setHome(HomeState payload) {
        this.homeName = payload.getHomeName();
        this.homeScore  = payload.getHomeScore();
        this.homeFoul = payload.getHomeFoul();
    }

    public void setAway(AwayState payload) {
        this.awayName = payload.getAwayName();
        this.awayScore = payload.getAwayScore();
        this.awayFoul = payload.getAwayFoul();
    }

    public void stopGameRunning(boolean state) {
        this.isGameRunning = state;
    }

    public void stopShotClock(boolean state) {
        this.isShotClockRunning = false;
    }
}
