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

    public void setHomeSetting(String payload) {
        // 여기서 이제 json 파싱해서 데이터 저장하기
    }

    public void setAwaySetting(String payload) {
    }
}
