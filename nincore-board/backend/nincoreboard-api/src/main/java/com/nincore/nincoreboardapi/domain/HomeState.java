package com.nincore.nincoreboardapi.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class HomeState {
    private  String homeName;
    private int homeScore;
    private int homeFoul;


}
