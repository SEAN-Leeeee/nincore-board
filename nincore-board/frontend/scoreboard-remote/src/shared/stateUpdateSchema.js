export const STATE_UPDATE_FIELDS = [
  "quarter",
  "gameTime",
  "shotClock",
  "isGameRunning",
  "isShotRunning",
  "homeScore",
  "homeFoul",
  "homeAssists",
  "homeRebounds",
  "homeSteals",
  "awayScore",
  "awayFoul",
  "awayAssists",
  "awayRebounds",
  "awaySteals",
  "players",
  "rosterPlayers",
  "homeName",
  "awayName",
  "gameLog",
  "everActivePlayerIds",
];

export function normalizeStateUpdatePayload(raw) {
  if (!raw || typeof raw !== "object") return {};
  const out = {};
  STATE_UPDATE_FIELDS.forEach((key) => {
    if (Object.prototype.hasOwnProperty.call(raw, key)) out[key] = raw[key];
  });
  return out;
}

export function createStateUpdatePayloadFromLocal(local) {
  return {
    quarter: local.quarter,
    gameTime: local.gameTime,
    shotClock: local.shotClock,
    isGameRunning: local.isGameRunning,
    isShotRunning: local.isShotRunning,
    homeScore: local.homeScore,
    homeFoul: local.homeFoul,
    homeAssists: local.homeAssists,
    homeRebounds: local.homeRebounds,
    homeSteals: local.homeSteals,
    awayScore: local.awayScore,
    awayFoul: local.awayFoul,
    awayAssists: local.awayAssists,
    awayRebounds: local.awayRebounds,
    awaySteals: local.awaySteals,
    players: local.players,
    rosterPlayers: local.rosterPlayers,
    homeName: local.homeName,
    awayName: local.awayName,
    gameLog: local.gameLog,
    everActivePlayerIds: local.everActivePlayerIds,
  };
}
