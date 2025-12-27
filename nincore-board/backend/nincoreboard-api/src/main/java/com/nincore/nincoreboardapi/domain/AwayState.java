package com.nincore.nincoreboardapi.domain;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class AwayState {

    private  String awayName;
    private int awayScore;
    private int awayFoul;

}
