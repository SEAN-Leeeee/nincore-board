package com.nincore.nincoreboardapi.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class GameTime {
    int gameTime;
    boolean isRunning;
    boolean isReset;
    int resetTime;
    int adjust;
}
