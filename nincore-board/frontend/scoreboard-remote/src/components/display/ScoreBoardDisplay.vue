<template>
  <div class="sb-root">
    <div class="sb-skin" :style="skinStyle">
      <div class="sb-bg"></div>

      <section class="sb-panel sb-panel--left">
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
            <div class="sb-shot-label">샷클락</div>
            <div class="sb-shot-value">{{ shotClockText }}</div>
          </div>

          <div class="sb-tfoul sb-tfoul--right">T-FOUL {{ awayTeamFouls }}</div>
        </div>
      </section>

      <section class="sb-panel sb-panel--right">
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
  </div>
</template>

<script>
import "./scoreboard-display.css";
import { connectWS } from "@/shared/wsClient";

export default {
  name: "ScoreBoardDisplay",
  data() {
    return {
      state: null,

      baseW: 1200,
      baseH: 600,

      homeTeamName: "Home",
      awayTeamName: "Away",

      quarter: 4,

      gameClockSec: 87,
      shotClockSec: 24,

      homeScore: 77,
      awayScore: 71,

      homeTeamFouls: 4,
      awayTeamFouls: 14,

      homePlayers: [
        { no: 1, name: "이관희", f: 1, p: 11 },
        { no: 4, name: "이재도", f: 2, p: 17 },
        { no: 9, name: "정희재", f: 4, p: 3 },
        { no: 10, name: "윤원상", f: 1, p: 6 },
        { no: 50, name: "마레이", f: 4, p: 16 }
      ],
      awayPlayers: [
        { no: 6, name: "박지훈", f: 3, p: 8 },
        { no: 10, name: "문성곤", f: 3, p: 9 },
        { no: 11, name: "양희종", f: 0, p: 5 },
        { no: 12, name: "아반도", f: 1, p: 2 },
        { no: 14, name: "스펠맨", f: 4, p: 7 }
      ],

      scale: 1
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

    connectWS((s) => {
      this.state = s;
      this.applyStateToView(s);
    });
  },
  beforeDestroy() {
    window.removeEventListener("resize", this.updateScale);
  },
  methods: {
    applyStateToView(s) {
      if (!s) return;

      if (typeof s.quarter === "number") this.quarter = s.quarter;

      const teams = s.teams || {};
      const homeTeam = teams.Home || teams.A;
      const awayTeam = teams.Away || teams.B;

      if (homeTeam && awayTeam) {
        this.homeTeamName = homeTeam.name ?? this.homeTeamName;
        this.awayTeamName = awayTeam.name ?? this.awayTeamName;

        this.homeScore = Number(homeTeam.score ?? this.homeScore);
        this.awayScore = Number(awayTeam.score ?? this.awayScore);

        this.homeTeamFouls = Number(homeTeam.fouls ?? this.homeTeamFouls);
        this.awayTeamFouls = Number(awayTeam.fouls ?? this.awayTeamFouls);
      }

      const roster = s.rosterPlayers || {};
      const homeRoster = roster.Home || roster.A;
      const awayRoster = roster.Away || roster.B;

      if (homeRoster && awayRoster) {
        const toRow = (p) => ({
          no: p.no ?? "",
          name: p.name ?? "",
          f: typeof p.f === "number" ? p.f : 0,
          p: typeof p.p === "number" ? p.p : 0
        });

        this.homePlayers = (homeRoster || [])
          .filter((p) => p.selected)
          .slice(0, 5)
          .map(toRow);

        this.awayPlayers = (awayRoster || [])
          .filter((p) => p.selected)
          .slice(0, 5)
          .map(toRow);
      }
    },
    updateScale() {
      const vw = window.innerWidth;
      const vh = window.innerHeight;
      const s = Math.min(vw / this.baseW, vh / this.baseH);
      this.scale = Math.max(0.2, s);
    },
    mmss(sec) {
      const s = Math.max(0, Number(sec) || 0);
      const m = Math.floor(s / 60);
      const r = s % 60;
      return `${m}:${String(r).padStart(2, "0")}`;
    }
  }
};
</script>
