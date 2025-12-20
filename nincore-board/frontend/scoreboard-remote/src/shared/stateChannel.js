const CHANNEL = "nincore-scoreboard";
const STORAGE_KEY = "nincore_scoreboard_state_v1";

let bc = null;
try {
    bc = new BroadcastChannel(CHANNEL);
} catch (e) {
    bc = null;
}

export function loadState() {
    try {
        const raw = localStorage.getItem(STORAGE_KEY);
        return raw ? JSON.parse(raw) : null;
    } catch (e) {
        return null;
    }
}

export function publishState(state) {
    try {
        localStorage.setItem(STORAGE_KEY, JSON.stringify(state));
    } catch (e) {}

    if (bc) bc.postMessage({ type: "STATE", payload: state });
}

export function subscribeState(handler) {
    if (!bc) return () => {};
    const onMsg = (ev) => {
        if (!ev || !ev.data) return;
        if (ev.data.type !== "STATE") return;
        handler(ev.data.payload);
    };
    bc.addEventListener("message", onMsg);
    return () => bc.removeEventListener("message", onMsg);
}
