package com.nincore.nincoreboardapi.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ShotClock {
    int shotClock;
    boolean isRunning;
    boolean isReset;
    boolean isSetHalf;
    int adjust;
}
