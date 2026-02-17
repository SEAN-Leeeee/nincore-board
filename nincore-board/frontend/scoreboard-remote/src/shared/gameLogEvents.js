let sequence = 0;

function nextEventId() {
  sequence += 1;
  return `evt_${Date.now()}_${sequence}`;
}

export function createScoreEvent({ quarter, teamKey, playerId = null, points = 0, at = Date.now() }) {
  const safePoints = Number(points) || 0;
  return {
    id: nextEventId(),
    at,
    quarter: Number(quarter) || 1,
    teamKey,
    playerId,
    kind: "SCORE",
    payload: {
      points: safePoints,
      delta: safePoints,
    },
  };
}

export function createStatEvent({
  quarter,
  teamKey,
  playerId = null,
  kind,
  delta = 1,
  at = Date.now(),
}) {
  return {
    id: nextEventId(),
    at,
    quarter: Number(quarter) || 1,
    teamKey,
    playerId,
    kind,
    payload: {
      delta: Number(delta) || 0,
    },
  };
}

function legacyTypeToKind(type) {
  if (type === "SCORE") return "SCORE";
  if (type === "FOUL" || type === "FOULS") return "FOUL";
  if (type === "ASSISTS" || type === "ASSIST") return "ASSIST";
  if (type === "REBOUNDS" || type === "REBOUND") return "REBOUND";
  if (type === "STEALS" || type === "STEAL") return "STEAL";
  return null;
}

function normalizeSingleEvent(event) {
  if (!event || typeof event !== "object") return null;

  if (event.kind && event.teamKey) {
    return {
      id: event.id || nextEventId(),
      at: Number(event.at) || Date.now(),
      quarter: Number(event.quarter) || 1,
      teamKey: event.teamKey,
      playerId: event.playerId ?? null,
      kind: event.kind,
      payload: { ...(event.payload || {}) },
    };
  }

  const kind = legacyTypeToKind(event.type);
  const teamKey = event.team || event.teamKey;
  if (!kind || !teamKey) return null;

  const payload = {};
  if (kind === "SCORE") {
    const points = Number(event.points) || 0;
    payload.points = points;
    payload.delta = points;
  } else {
    payload.delta = Number(event.delta);
    if (!Number.isFinite(payload.delta)) payload.delta = 1;
  }

  return {
    id: event.id || nextEventId(),
    at: Number(event.at) || Date.now(),
    quarter: Number(event.quarter) || 1,
    teamKey,
    playerId: event.playerId ?? null,
    kind,
    payload,
  };
}

export function normalizeGameLog(gameLog) {
  if (!Array.isArray(gameLog)) return [];
  return gameLog.map(normalizeSingleEvent).filter(Boolean);
}
