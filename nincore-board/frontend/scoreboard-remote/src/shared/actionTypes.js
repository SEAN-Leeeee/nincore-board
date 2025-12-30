export const ActionType = Object.freeze({
    // Clock
    TOGGLE_GAME_TIME: "TOGGLE_GAME_TIME",
    TOGGLE_SHOT_CLOCK: "TOGGLE_SHOT_CLOCK",

    // Game
    QUARTER: "QUARTER",

    // Home Team
    HOME_SETTING: "HOME_SETTING",
    HOME_SCORE: "HOME_SCORE",
    HOME_FOUL: "HOME_FOUL",

    // Away Team
    AWAY_SETTING: "AWAY_SETTING",
    AWAY_SCORE: "AWAY_SCORE",
    AWAY_FOUL: "AWAY_FOUL",

    // Full state override (for things like roster changes for now)
    STATE_UPDATE: "STATE_UPDATE",
});
