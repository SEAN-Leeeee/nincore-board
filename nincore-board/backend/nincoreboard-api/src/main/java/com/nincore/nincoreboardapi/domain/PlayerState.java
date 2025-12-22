package com.nincore.nincoreboardapi.domain;


import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PlayerState {

    private int backNumber;
    private String name;
    private int points;
    private int foul;

}
