export const ActionType = Object.freeze({
    // Clock
    TOGGLE_GAME_TIME: "TOGGLE_GAME_TIME",
    TOGGLE_SHOT_CLOCK: "TOGGLE_SHOT_CLOCK",
    SETTING_GAME_TIME: "SETTING_GAME_TIME",
    SETTING_SHOT_CLOCK: "SETTING_SHOT_CLOCK",
    // Game
    QUARTER: "QUARTER",

    // Home Team
    RESET_HOME: "RESET_HOME",
    HOME_NAME: "HOME_NAME",
    HOME_SCORE: "HOME_SCORE",
    HOME_FOUL: "HOME_FOUL",

    // Away Team
    RESET_AWAY: "RESET_AWAY",
    AWAY_NAME: "AWAY_NAME",
    AWAY_SCORE: "AWAY_SCORE",
    AWAY_FOUL: "AWAY_FOUL",

    // Full state override (for things like roster changes for now)
    STATE_UPDATE: "STATE_UPDATE",
});
