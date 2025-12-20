<template>
  <div class="sb-root">
    <div class="sb-skin" :style="skinStyle">
      <div class="sb-bg"></div>

      <!-- LEFT PANEL (A팀) -->
      <section class="sb-panel sb-panel--left">
        <div class="sb-head">
          <span class="h-no">NO</span>
          <span class="h-name">NAME</span>
          <span class="h-f">F</span>
          <span class="h-p">P</span>
        </div>

        <div class="sb-list">
          <div v-for="(p, idx) in leftPlayers" :key="'L' + idx" class="sb-row">
            <span class="c-no">{{ p.no }}</span>
            <span class="c-name">{{ p.name }}</span>
            <span class="c-f">{{ p.f }}</span>
            <span class="c-p">{{ p.p }}</span>
          </div>
        </div>
      </section>

      <!-- CENTER PANEL -->
      <section class="sb-center">
        <!-- 팀명(상단) -->
        <div class="sb-center-teamnames">
          <span class="sb-teamname sb-teamname--left">{{ leftTeamName }}</span>
          <span class="sb-teamname sb-teamname--sep">:</span>
          <span class="sb-teamname sb-teamname--right">{{ rightTeamName }}</span>
        </div>

        <!-- 전체 시간 -->
        <div class="sb-center-clock">{{ gameClockText }}</div>

        <!-- 점수 + 쿼터(점수 사이) -->
        <div class="sb-center-scores-under-team">
          <div class="sb-score-under sb-score-under--left">{{ leftScore }}</div>
          <div class="sb-quarterbox" aria-label="quarter">{{ quarter }}</div>
          <div class="sb-score-under sb-score-under--right">{{ rightScore }}</div>
        </div>

        <!-- 샷클락 + T-FOUL(좌/우) -->
        <div class="sb-shotrow">
          <div class="sb-tfoul sb-tfoul--left">T-FOUL {{ leftTeamFouls }}</div>

          <div class="sb-shot">
            <div class="sb-shot-label">샷클락</div>
            <div class="sb-shot-value">{{ shotClockText }}</div>
          </div>

          <div class="sb-tfoul sb-tfoul--right">T-FOUL {{ rightTeamFouls }}</div>
        </div>
      </section>

      <!-- RIGHT PANEL (B팀) -->
      <section class="sb-panel sb-panel--right">
        <div class="sb-head">
          <span class="h-no">NO</span>
          <span class="h-name">NAME</span>
          <span class="h-f">F</span>
          <span class="h-p">P</span>
        </div>

        <div class="sb-list">
          <div v-for="(p, idx) in rightPlayers" :key="'R' + idx" class="sb-row">
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

export default {
  name: "ScoreBoardDisplay",
  data() {
    return {
      baseW: 1200,
      baseH: 600,

      leftTeamName: "창원",
      rightTeamName: "안양",

      quarter: 4,

      gameClockSec: 87, // 1:27
      shotClockSec: 24,

      leftScore: 77,
      rightScore: 71,

      leftTeamFouls: 4,
      rightTeamFouls: 14,

      leftPlayers: [
        { no: 1, name: "이관희", f: 1, p: 11 },
        { no: 4, name: "이재도", f: 2, p: 17 },
        { no: 9, name: "정희재", f: 4, p: 3 },
        { no: 10, name: "윤원상", f: 1, p: 6 },
        { no: 50, name: "마레이", f: 4, p: 16 }
      ],
      rightPlayers: [
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
  },
  beforeDestroy() {
    window.removeEventListener("resize", this.updateScale);
  },
  methods: {
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
