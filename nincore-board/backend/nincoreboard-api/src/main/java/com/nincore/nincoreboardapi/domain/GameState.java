package com.nincore.nincoreboardapi.domain;


import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class GameState {
    private int quarter;
    private String totalTime;
    private int shotClock;
    private String homeName;
    private String awayName;
    private int homeScore;
    private int awayScore;
    private int teamFoul;

}
