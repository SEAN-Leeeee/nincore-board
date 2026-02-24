const CHANNEL = "nincore-scoreboard";
const STORAGE_KEY = "nincore_scoreboard_state_v1";
const BACKUP_KEY = "nincore_scoreboard_state_backup_v1";
const STORAGE_KEY_PREFIX = `${STORAGE_KEY}:`;
const BACKUP_KEY_PREFIX = `${BACKUP_KEY}:`;

function isObject(value) {
    return value && typeof value === "object" && !Array.isArray(value);
}

function parseJSON(raw) {
    if (!raw) return null;
    try {
        return JSON.parse(raw);
    } catch (e) {
        return null;
    }
}

function hasEntries(value) {
    return isObject(value) && Object.keys(value).length > 0;
}

function hasPlayerData(players) {
    if (!hasEntries(players)) return false;
    const home = Array.isArray(players.Home) ? players.Home : [];
    const away = Array.isArray(players.Away) ? players.Away : [];
    return home.length > 0 || away.length > 0;
}

function hasRosterData(rosterPlayers) {
    if (!hasEntries(rosterPlayers)) return false;
    const home = Array.isArray(rosterPlayers.Home) ? rosterPlayers.Home : [];
    const away = Array.isArray(rosterPlayers.Away) ? rosterPlayers.Away : [];
    return home.length > 0 || away.length > 0;
}

function isDefaultTeamName(name) {
    const text = String(name || "").trim().toLowerCase();
    return text === "" || text === "home" || text === "away";
}

function hasNonDefaultTeamName(state) {
    if (!isObject(state)) return false;
    return !isDefaultTeamName(state.homeName) || !isDefaultTeamName(state.awayName);
}

function numericStateScore(state) {
    if (!isObject(state)) return 0;
    return (
        Number(state.homeScore || 0) +
        Number(state.awayScore || 0) +
        Number(state.homeFoul || 0) +
        Number(state.awayFoul || 0)
    );
}

function isWeakIncomingState(state) {
    if (!isObject(state)) return true;
    const hasAnyPlayers = hasPlayerData(state.players);
    const hasAnyRoster = hasRosterData(state.rosterPlayers);
    const hasAnyLog = Array.isArray(state.gameLog) && state.gameLog.length > 0;
    const hasAnyNumbers = numericStateScore(state) > 0;
    const hasAnyNames = hasNonDefaultTeamName(state);
    return !(hasAnyPlayers || hasAnyRoster || hasAnyLog || hasAnyNumbers || hasAnyNames);
}

function mergeState(prev, next) {
    const left = isObject(prev) ? prev : {};
    const right = isObject(next) ? next : {};
    const merged = { ...left, ...right };

    // Prevent weak payloads (often first WS sync) from wiping roster/player state.
    if (!hasPlayerData(right.players) && hasPlayerData(left.players)) {
        merged.players = left.players;
    }
    if (!hasRosterData(right.rosterPlayers) && hasRosterData(left.rosterPlayers)) {
        merged.rosterPlayers = left.rosterPlayers;
    }

    // Keep meaningful team names when incoming payload contains defaults only.
    if (isDefaultTeamName(right.homeName) && !isDefaultTeamName(left.homeName)) {
        merged.homeName = left.homeName;
    }
    if (isDefaultTeamName(right.awayName) && !isDefaultTeamName(left.awayName)) {
        merged.awayName = left.awayName;
    }

    // Hard guard: if incoming is effectively empty/default, keep previous full state.
    if (isWeakIncomingState(right) && hasMeaningfulState(left)) {
        return { ...left, ...right, 
            players: left.players,
            rosterPlayers: left.rosterPlayers,
            gameLog: left.gameLog,
            homeName: left.homeName,
            awayName: left.awayName,
            homeScore: left.homeScore,
            awayScore: left.awayScore,
            homeFoul: left.homeFoul,
            awayFoul: left.awayFoul,
            homeAssists: left.homeAssists,
            homeRebounds: left.homeRebounds,
            homeSteals: left.homeSteals,
            awayAssists: left.awayAssists,
            awayRebounds: left.awayRebounds,
            awaySteals: left.awaySteals,
        };
    }

    return merged;
}

function hasMeaningfulState(state) {
    if (!isObject(state)) return false;

    const homeScore = Number(state.homeScore || 0);
    const awayScore = Number(state.awayScore || 0);
    const homeFoul = Number(state.homeFoul || 0);
    const awayFoul = Number(state.awayFoul || 0);
    const gameLogCount = Array.isArray(state.gameLog) ? state.gameLog.length : 0;
    const homePlayers = Array.isArray(state.players && state.players.Home) ? state.players.Home.length : 0;
    const awayPlayers = Array.isArray(state.players && state.players.Away) ? state.players.Away.length : 0;
    const homeRoster = Array.isArray(state.rosterPlayers && state.rosterPlayers.Home) ? state.rosterPlayers.Home.length : 0;
    const awayRoster = Array.isArray(state.rosterPlayers && state.rosterPlayers.Away) ? state.rosterPlayers.Away.length : 0;
    const hasTeamNames = hasNonDefaultTeamName(state);

    return (
        homeScore > 0 ||
        awayScore > 0 ||
        homeFoul > 0 ||
        awayFoul > 0 ||
        gameLogCount > 0 ||
        homePlayers > 0 ||
        awayPlayers > 0 ||
        homeRoster > 0 ||
        awayRoster > 0 ||
        hasTeamNames
    );
}

function getBC() {
    if (!window.__nincore_bc__) {
        try {
            window.__nincore_bc__ = new BroadcastChannel(CHANNEL);
        } catch (e) {
            window.__nincore_bc__ = null;
        }
    }
    return window.__nincore_bc__;
}

function getSessionIdFromPath() {
    try {
        const path = String(window.location && window.location.pathname ? window.location.pathname : "");
        const match = path.match(/\/(?:remote|display)\/([^/?#]+)/i);
        return match && match[1] ? String(match[1]) : "";
    } catch (e) {
        return "";
    }
}

function getCurrentSessionId(state) {
    if (state && state.sessionId !== undefined && state.sessionId !== null && String(state.sessionId).trim() !== "") {
        return String(state.sessionId).trim();
    }
    try {
        const fromSession = sessionStorage.getItem("sessionId");
        if (fromSession && String(fromSession).trim() !== "") return String(fromSession).trim();
    } catch (e) {}
    return getSessionIdFromPath();
}

function buildStorageKey(prefix, sessionId) {
    if (!sessionId) return `${prefix}global`;
    return `${prefix}${sessionId}`;
}

export function loadState(targetSessionId) {
    const sessionId = (targetSessionId !== undefined && targetSessionId !== null && String(targetSessionId).trim() !== "")
        ? String(targetSessionId).trim()
        : getCurrentSessionId();
    const primaryKey = buildStorageKey(STORAGE_KEY_PREFIX, sessionId);
    const backupKey = buildStorageKey(BACKUP_KEY_PREFIX, sessionId);

    const primary = parseJSON(localStorage.getItem(primaryKey));
    const backupPayload = parseJSON(localStorage.getItem(backupKey));
    const backup = isObject(backupPayload) ? backupPayload.state : null;

    if (isObject(primary) && sessionId && (!primary.sessionId || String(primary.sessionId) !== sessionId)) {
        primary.sessionId = sessionId;
    }
    if (isObject(backup) && sessionId && (!backup.sessionId || String(backup.sessionId) !== sessionId)) {
        backup.sessionId = sessionId;
    }

    if (hasMeaningfulState(primary)) return primary;
    if (hasMeaningfulState(backup)) return mergeState(backup, primary);
    return primary || backup || null;
}

export function publishState(state) {
    const sessionId = getCurrentSessionId(state);
    const incoming = isObject(state) ? { ...state } : {};
    if (sessionId && (!incoming.sessionId || String(incoming.sessionId).trim() === "")) {
        incoming.sessionId = sessionId;
    }

    const previous = loadState(sessionId);
    const merged = mergeState(previous, incoming);
    const primaryKey = buildStorageKey(STORAGE_KEY_PREFIX, sessionId);
    const backupKey = buildStorageKey(BACKUP_KEY_PREFIX, sessionId);

    try {
        localStorage.setItem(primaryKey, JSON.stringify(merged));
        if (hasMeaningfulState(merged)) {
            localStorage.setItem(backupKey, JSON.stringify({ updatedAt: Date.now(), state: merged }));
        }
    } catch (e) {}

    const bc = getBC();
    if (bc) bc.postMessage({ type: "STATE", payload: merged });
}

export function subscribeState(handler) {
    const bc = getBC();
    if (!bc) return () => {};

    const onMsg = (ev) => {
        if (!ev || !ev.data) return;
        if (ev.data.type !== "STATE") return;
        handler(ev.data.payload);
    };

    bc.addEventListener("message", onMsg);
    return () => bc.removeEventListener("message", onMsg);
}

export function clearPersistedState() {
    try {
        localStorage.removeItem(STORAGE_KEY);
        localStorage.removeItem(BACKUP_KEY);

        const keysToRemove = [];
        for (let i = 0; i < localStorage.length; i += 1) {
            const key = localStorage.key(i);
            if (!key) continue;
            if (key.startsWith(STORAGE_KEY_PREFIX) || key.startsWith(BACKUP_KEY_PREFIX)) {
                keysToRemove.push(key);
            }
        }
        keysToRemove.forEach((key) => localStorage.removeItem(key));
    } catch (e) {}
}
