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
import { loadState } from "@/shared/stateChannel";

const CHANNEL = "nincore-scoreboard";
const STORAGE_KEY = "nincore_scoreboard_state_v1";

export default {
  name: "ScoreBoardDisplay",
  data() {
    return {
      state: null,
      baseW: 1200,
      baseH: 600,
      homeTeamName: "",
      awayTeamName: "",
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
      fullScreenMode: false, // New data property
      _bc: null,
      _onMsg: null,
    };
  },
  computed: {
    skinStyle() {
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
          localStorage.removeItem("nincore_scoreboard_state_v1");
          window.location.href = "/";
          return;
        }

        if (ev.data.type !== "STATE") return;
        this.applyStateToView(ev.data.payload);
      };
      this._bc.addEventListener("message", this._onMsg);
    } catch (e) {
      console.error("BroadcastChannel 생성 실패:", e);
    }

    try {
      const raw = localStorage.getItem(STORAGE_KEY);
      if (raw) {
        this.applyStateToView(JSON.parse(raw));
      }
    } catch (e) {}
  },
  beforeDestroy() {
    window.removeEventListener("resize", this.updateScale);
    if (this._bc && this._onMsg) {
      this._bc.removeEventListener("message", this._onMsg);
      this._bc.close();
    }
  },
  methods: {
    applyStateToView(s) {
      const sessionId = sessionStorage.getItem("sessionId");
      if (!s || s.sessionId != sessionId) return;

      if (typeof s.quarter === "number") this.quarter = s.quarter;
      if (typeof s.gameTime === "number") this.gameClockSec = s.gameTime;
      if (typeof s.shotClock === "number") this.shotClockSec = s.shotClock;

      this.homeTeamName = s.homeName ?? this.homeTeamName;
      this.homeScore = Number(s.homeScore ?? this.homeScore);
      this.homeTeamFouls = Number(s.homeFoul ?? this.homeTeamFouls);

      this.awayTeamName = s.awayName ?? this.awayTeamName;
      this.awayScore = Number(s.awayScore ?? this.awayScore);
      this.awayTeamFouls = Number(s.awayFoul ?? this.awayTeamFouls);

      const players = s.players || {};
      const homePlayers = players.Home || players.A || [];
      const awayPlayers = players.Away || players.B || [];

      const toRow = (p) => ({
        no: p.no ?? "",
        name: p.name ?? "",
        f: typeof p.fouls === "number" ? p.fouls : (typeof p.f === "number" ? p.f : 0),
        p: typeof p.points === "number" ? p.points : (typeof p.p === "number" ? p.p : 0)
      });

      this.homePlayers = (homePlayers || []).slice(0, 5).map(toRow).sort((a, b) => a.no - b.no);
      this.awayPlayers = (awayPlayers || []).slice(0, 5).map(toRow).sort((a, b) => a.no - b.no);
    },
    updateScale() {
      const vw = window.innerWidth;
      const vh = window.innerHeight;

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
c
