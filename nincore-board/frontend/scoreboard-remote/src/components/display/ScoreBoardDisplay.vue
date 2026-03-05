<template>
  <div class="sb-root">
    <div class="sb-skin" :class="{ 'full-screen-mode': fullScreenMode }" :style="skinStyle">
      <div class="sb-bg"></div>

      <section v-if="!fullScreenMode" class="sb-panel sb-panel--left">
        <div class="sb-head">
          <span class="h-no">NO</span>
          <span class="h-name">NAME</span>
          <span class="h-f">F</span>
          <span class="h-p">P</span>
        </div>

        <div class="sb-list">
          <div v-for="(p, idx) in homePlayers" :key="'H' + idx" class="sb-row">
            <span class="c-no">{{ p.no }}</span>
            <span class="c-name">{{ p.name }}</span>
            <span class="c-f">{{ p.f }}</span>
            <span class="c-p">{{ p.p }}</span>
          </div>
        </div>
      </section>

      <section class="sb-center">
        <div class="sb-center-teamnames">
          <span class="sb-teamname sb-teamname--left">{{ homeTeamName }}</span>
          <span class="sb-teamname sb-teamname--sep">:</span>
          <span class="sb-teamname sb-teamname--right">{{ awayTeamName }}</span>
        </div>

        <div class="sb-center-clock">{{ gameClockText }}</div>

        <div class="sb-center-scores-under-team">
          <div class="sb-score-under sb-score-under--left">{{ homeScore }}</div>
          <div class="sb-quarterbox" aria-label="quarter">{{ quarter }}</div>
          <div class="sb-score-under sb-score-under--right">{{ awayScore }}</div>
        </div>

        <div class="sb-shotrow">
          <div class="sb-tfoul sb-tfoul--left">T-FOUL {{ homeTeamFouls }}</div>

          <div class="sb-shot">
            <div class="sb-shot-label">Shot Clock</div>
            <div class="sb-shot-value">{{ shotClockText }}</div>
          </div>

          <div class="sb-tfoul sb-tfoul--right">T-FOUL {{ awayTeamFouls }}</div>
        </div>
      </section>

      <section v-if="!fullScreenMode" class="sb-panel sb-panel--right">
        <div class="sb-head">
          <span class="h-no">NO</span>
          <span class="h-name">NAME</span>
          <span class="h-f">F</span>
          <span class="h-p">P</span>
        </div>

        <div class="sb-list">
          <div v-for="(p, idx) in awayPlayers" :key="'A' + idx" class="sb-row">
            <span class="c-no">{{ p.no }}</span>
            <span class="c-name">{{ p.name }}</span>
            <span class="c-f">{{ p.f }}</span>
            <span class="c-p">{{ p.p }}</span>
          </div>
        </div>
      </section>
    </div>

    <button @click="toggleFullScreenMode" class="full-screen-toggle-button">
      {{ fullScreenMode ? '플레이어 보기' : '점수만 보기' }}
    </button>
  </div>
</template>

<script>
import "./scoreboard-display.css";
import { loadState, clearPersistedState } from "@/shared/stateChannel";

const CHANNEL = "nincore-scoreboard";

export default {
  name: "ScoreBoardDisplay",
  data() {
    return {
      state: null,
      baseW: 1200,
      baseH: 600,
      homeTeamName: "HOME",
      awayTeamName: "AWAY",
      quarter: 1,
      gameClockSec: 7 * 60,
      shotClockSec: 24,
      homeScore: 0,
      awayScore: 0,
      homeTeamFouls: 0,
      awayTeamFouls: 0,
      homePlayers: [],
      awayPlayers: [],
      scale: 1,
      fullScreenMode: false,
      isMobile: false,
      _bc: null,
      _onMsg: null,
      _initialLoadTimer: null,
      _initialLoadAttempts: 0,
    };
  },
  computed: {
    skinStyle() {
      if (this.isMobile) {
        return {
          width: '100vw',
          height: '100vh',
          transform: 'none',
          position: 'fixed',
          top: 0,
          left: 0,
          padding: 0
        };
      }
      return {
        width: this.baseW + "px",
        height: this.baseH + "px",
        transform: `scale(${this.scale})`
      };
    },
    gameClockText() {
      return this.mmss(this.gameClockSec);
    },
    shotClockText() {
      const s = Math.max(0, Number(this.shotClockSec) || 0);
      return String(s);
    }
  },
  mounted() {
    this.updateScale();
    window.addEventListener("resize", this.updateScale, { passive: true });

    try {
      this._bc = new BroadcastChannel(CHANNEL);
      this._onMsg = (ev) => {
        if (!ev || !ev.data) return;

        if (ev.data.type === "SHUTDOWN") {
          sessionStorage.clear();
          clearPersistedState();
          window.location.href = "/";
          return;
        }

        if (ev.data.type !== "STATE") return;
        this.applyStateToView(ev.data.payload);
      };
      this._bc.addEventListener("message", this._onMsg);
      this.requestLatestState();
    } catch (e) {
      console.error("BroadcastChannel 생성 실패:", e);
    }

    this.tryInitialStateLoad();
  },
  beforeDestroy() {
    window.removeEventListener("resize", this.updateScale);
    if (this._bc && this._onMsg) {
      this._bc.removeEventListener("message", this._onMsg);
      this._bc.close();
    }
    if (this._initialLoadTimer) {
      clearTimeout(this._initialLoadTimer);
      this._initialLoadTimer = null;
    }
  },
  methods: {
    requestLatestState() {
      if (!this._bc) return;
      try {
        this._bc.postMessage({ type: "REQUEST_STATE" });
      } catch (e) {}
    },
    tryInitialStateLoad() {
      const initialState = loadState(this.$route?.params?.sessionId);
      if (initialState) {
        this.applyStateToView(initialState);
      }

      const hasNames = Boolean(String(this.homeTeamName || "").trim()) && Boolean(String(this.awayTeamName || "").trim());
      const hasPlayers = (this.homePlayers && this.homePlayers.length) || (this.awayPlayers && this.awayPlayers.length);

      if (hasNames && hasPlayers) {
        return;
      }

      if (this._initialLoadAttempts >= 15) {
        return;
      }

      this._initialLoadAttempts += 1;
      this.requestLatestState();
      this._initialLoadTimer = setTimeout(() => this.tryInitialStateLoad(), 200);
    },
    applyStateToView(s) {
      if (!s) return;
      const currentSessionId = String(this.$route?.params?.sessionId || sessionStorage.getItem("sessionId") || "").trim();
      const incomingSessionId = String(s.sessionId || "").trim();
      if (currentSessionId && incomingSessionId && currentSessionId !== incomingSessionId) {
        return;
      }

      if (typeof s.quarter === "number") this.quarter = s.quarter;
      if (typeof s.gameTime === "number") this.gameClockSec = s.gameTime;
      if (typeof s.shotClock === "number") this.shotClockSec = s.shotClock;

      const homeName = String(s.homeName ?? "").trim();
      const awayName = String(s.awayName ?? "").trim();

      this.homeTeamName = homeName || this.homeTeamName || "HOME";
      this.homeScore = Number(s.homeScore ?? this.homeScore);
      this.homeTeamFouls = Math.min(5, Number(s.homeFoul ?? this.homeTeamFouls));

      this.awayTeamName = awayName || this.awayTeamName || "AWAY";
      this.awayScore = Number(s.awayScore ?? this.awayScore);
      this.awayTeamFouls = Math.min(5, Number(s.awayFoul ?? this.awayTeamFouls));

      const players = s.players || {};
      const rosterPlayers = s.rosterPlayers || {};
      const homePlayers = players.Home || players.home || players.A || [];
      const awayPlayers = players.Away || players.away || players.B || [];
      const homeRoster = rosterPlayers.Home || rosterPlayers.home || [];
      const awayRoster = rosterPlayers.Away || rosterPlayers.away || [];

      const toRow = (p) => ({
        no: p.no ?? "",
        name: p.name ?? "",
        f: typeof p.fouls === "number" ? p.fouls : (typeof p.f === "number" ? p.f : 0),
        p: typeof p.points === "number" ? p.points : (typeof p.p === "number" ? p.p : 0)
      });

      const pickRosterPlayers = (rosterList) => {
        const selected = (rosterList || []).filter((p) => p && (p.selected === true || p.selected === "true" || p.selected === 1));
        if (selected.length) return selected.slice(0, 5);
        return (rosterList || []).slice(0, 5);
      };

      const homePlayerList = Array.isArray(homePlayers) ? homePlayers : [];
      const awayPlayerList = Array.isArray(awayPlayers) ? awayPlayers : [];
      const homeRosterList = Array.isArray(homeRoster) ? homeRoster : [];
      const awayRosterList = Array.isArray(awayRoster) ? awayRoster : [];

      const resolvedHome = homePlayerList.length ? homePlayerList : pickRosterPlayers(homeRosterList);
      const resolvedAway = awayPlayerList.length ? awayPlayerList : pickRosterPlayers(awayRosterList);

      const hasIncomingPlayerData = homePlayerList.length || awayPlayerList.length || homeRosterList.length || awayRosterList.length;
      if (!hasIncomingPlayerData) {
        return;
      }

      const sortByNo = (a, b) => Number(a.no ?? 0) - Number(b.no ?? 0);
      this.homePlayers = (resolvedHome || []).slice(0, 5).map(toRow).sort(sortByNo);
      this.awayPlayers = (resolvedAway || []).slice(0, 5).map(toRow).sort(sortByNo);
    },
    updateScale() {
      const vw = window.innerWidth;
      const vh = window.innerHeight;
      this.isMobile = vw < 950;

      // Force fullScreenMode on mobile initially
      if (this.isMobile && this._initialLoadAttempts === 0) {
        this.fullScreenMode = true;
      }

      let s = 1;
      if (this.fullScreenMode) {
        // For "cover" effect, fill the screen while maintaining aspect ratio
        s = Math.max(vw / this.baseW, vh / this.baseH);
      } else {
        // For "contain" effect, fit within the screen
        s = Math.min(vw / this.baseW, vh / this.baseH);
      }
      this.scale = Math.max(0.2, s);
    },
    mmss(sec) {
      const s = Math.max(0, Number(sec) || 0);
      const m = Math.floor(s / 60);
      const r = s % 60;
      return `${m}:${String(r).padStart(2, "0")}`;
    },
    toggleFullScreenMode() {
      this.fullScreenMode = !this.fullScreenMode;
      console.log('fullScreenMode toggled to:', this.fullScreenMode); // Debug log
      this.updateScale(); // Recalculate scale after mode change
    }
  }
};
</script>
