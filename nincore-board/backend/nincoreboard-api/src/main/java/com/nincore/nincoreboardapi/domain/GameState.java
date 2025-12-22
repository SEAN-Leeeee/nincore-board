package com.nincore.nincoreboardapi.domain;


import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class GameState {
    private String homeName;
    private String awayName;
    private int homeScore;
    private int awayScore;

    private String totalTime;
    private int shotClock;

    private int teamFoul;
}
