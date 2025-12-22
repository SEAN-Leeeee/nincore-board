<template>
  <div class="rc-page">
    <div class="rc-3row">
      <section class="rc-row rc-row--time">
        <div class="rc-card rc-time-card">
          <div class="rc-time-header">
            <div class="rc-time-header__main">경기 리모컨</div>
            <button class="rc-btn rc-btn--ghost" @click="resetAll">리셋</button>
          </div>

          <div class="rc-time-merged">
            <div class="rc-time-grid">
              <div class="rc-time-cell rc-time-cell--left">
                <span class="rc-time-label">현재쿼터</span>
                <span class="rc-time-qnum">{{ quarter }}</span>
              </div>

              <div class="rc-time-cell rc-time-cell--mid rc-time-cell--center">
                <span class="rc-time-label">전체 시간</span>
                <button class="rc-time-click" @click="openTimeModal">
                  <span class="rc-time-value rc-time-value--accent">{{ formatMMSS(gameClockSec) }}</span>
                </button>
              </div>

              <div class="rc-time-cell rc-time-cell--right rc-time-cell--center">
                <span class="rc-time-label">공격 시간</span>
                <span class="rc-time-value">{{ shotClockSec }}</span>
              </div>
            </div>

            <div class="rc-time-grid rc-time-grid--row2">
              <div class="rc-time-cell rc-time-cell--left rc-time-cell--center">
                <div class="rc-time-rowbtns">
                  <button class="rc-btn rc-btn--mini" @click="changeQuarter(-1)" :disabled="quarter <= 1">-1</button>
                  <button class="rc-btn rc-btn--mini" @click="changeQuarter(1)" :disabled="quarter >= 4">+1</button>
                </div>
              </div>

              <div class="rc-time-cell rc-time-cell--mid rc-time-cell--center">
                <div class="rc-time-rowbtns">
                  <button class="rc-btn rc-btn--primary" @click="toggleGameClock">
                    {{ isGameRunning ? "정지" : "시작" }}
                  </button>
                  <button class="rc-btn" @click="resetGameClock">리셋</button>
                </div>
              </div>

              <div class="rc-time-cell rc-time-cell--right rc-time-cell--center">
                <div class="rc-time-rowbtns">
                  <button class="rc-btn rc-btn--primary" @click="toggleShotClock">
                    {{ isShotRunning ? "정지" : "시작" }}
                  </button>
                  <button class="rc-btn" @click="resetShotClock">리셋</button>
                </div>
              </div>
            </div>

            <div class="rc-time-grid rc-time-grid--row3">
              <div class="rc-time-cell rc-time-cell--left"></div>

              <div class="rc-time-cell rc-time-cell--mid rc-time-cell--center">
                <div class="rc-time-rowbtns">
                  <button class="rc-btn rc-btn--compact" @click="adjustGameClock(1)">+1</button>
                  <button class="rc-btn rc-btn--compact" @click="adjustGameClock(-1)" :disabled="gameClockSec <= 0">
                    -1
                  </button>
                </div>
              </div>

              <div class="rc-time-cell rc-time-cell--right rc-time-cell--center">
                <div class="rc-time-rowbtns">
                  <button class="rc-btn rc-btn--compact" @click="setShotClock(14)">14</button>
                  <button class="rc-btn rc-btn--compact" @click="adjustShotClock(1)">+1</button>
                  <button class="rc-btn rc-btn--compact" @click="adjustShotClock(-1)" :disabled="shotClockSec <= 0">
                    -1
                  </button>
                </div>
              </div>
            </div>
          </div>
        </div>
      </section>

      <section class="rc-row rc-row--team">
        <div class="rc-card rc-card--fill">
          <div class="rc-team">
            <div class="rc-team__header">
              <input class="rc-input" v-model="teams.A.name" placeholder="Home" />
              <div class="rc-team__scoretext">{{ teams.A.score }}</div>
            </div>

            <div class="rc-team__onebox">
              <div class="rc-team__meta-row">
                <div class="rc-meta-label">팀 파울</div>
                <div class="rc-meta-value">{{ teams.A.fouls }}</div>
              </div>

              <div class="rc-team__controls-row">
                <div>
                  <div class="rc-mini-title">점수</div>
                  <div class="rc-score-2rows">
                    <div class="rc-score-2rows__row">
                      <button class="rc-btn rc-btn--pill" @click="addTeamScore('A', 1)">+1</button>
                      <button class="rc-btn rc-btn--pill" @click="addTeamScore('A', 2)">+2</button>
                    </div>
                    <div class="rc-score-2rows__row">
                      <button class="rc-btn rc-btn--pill" @click="addTeamScore('A', 3)">+3</button>
                      <button
                          class="rc-btn rc-btn--pill rc-btn--ghost"
                          @click="addTeamScore('A', -1)"
                          :disabled="teams.A.score <= 0"
                      >
                        -1
                      </button>
                    </div>
                  </div>
                </div>

                <div>
                  <div class="rc-mini-title">파울</div>
                  <div class="rc-btn-row">
                    <button class="rc-btn rc-btn--pill" @click="addTeamFoul('A', 1)">+1</button>
                    <button
                        class="rc-btn rc-btn--pill rc-btn--ghost"
                        @click="addTeamFoul('A', -1)"
                        :disabled="teams.A.fouls <= 0"
                    >
                      -1
                    </button>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>

        <div class="rc-card rc-card--fill">
          <div class="rc-players">
            <div class="rc-players__head">
              <button class="rc-btn rc-btn--mini2" @click="openRoster('A')">선수 변경</button>
              <div class="rc-stathead">
                <div>득점</div>
                <div>어시</div>
                <div>리바</div>
                <div>파울</div>
              </div>
            </div>

            <div class="rc-statgrid" v-if="activePlayers.A.length">
              <div class="rc-statrow2" v-for="p in activePlayers.A" :key="p.id">
                <div class="rc-no">{{ p.no }}</div>

                <div class="rc-statcell">
                  <div class="rc-num">{{ p.points }}</div>
                  <button class="rc-plus" @click="addPlayerStat('A', p.id, 'points', 1)">+1</button>
                </div>

                <div class="rc-statcell">
                  <div class="rc-num">{{ p.assists }}</div>
                  <button class="rc-plus" @click="addPlayerStat('A', p.id, 'assists', 1)">+1</button>
                </div>

                <div class="rc-statcell">
                  <div class="rc-num">{{ p.rebounds }}</div>
                  <button class="rc-plus" @click="addPlayerStat('A', p.id, 'rebounds', 1)">+1</button>
                </div>

                <div class="rc-statcell">
                  <div class="rc-num">{{ p.fouls }}</div>
                  <button class="rc-plus" @click="addPlayerStat('A', p.id, 'fouls', 1)">+1</button>
                </div>
              </div>
            </div>
          </div>
        </div>
      </section>

      <section class="rc-row rc-row--team">
        <div class="rc-card rc-card--fill">
          <div class="rc-team">
            <div class="rc-team__header">
              <input class="rc-input" v-model="teams.B.name" placeholder="Away" />
              <div class="rc-team__scoretext">{{ teams.B.score }}</div>
            </div>

            <div class="rc-team__onebox">
              <div class="rc-team__meta-row">
                <div class="rc-meta-label">팀 파울</div>
                <div class="rc-meta-value">{{ teams.B.fouls }}</div>
              </div>

              <div class="rc-team__controls-row">
                <div>
                  <div class="rc-mini-title">점수</div>
                  <div class="rc-score-2rows">
                    <div class="rc-score-2rows__row">
                      <button class="rc-btn rc-btn--pill" @click="addTeamScore('B', 1)">+1</button>
                      <button class="rc-btn rc-btn--pill" @click="addTeamScore('B', 2)">+2</button>
                    </div>
                    <div class="rc-score-2rows__row">
                      <button class="rc-btn rc-btn--pill" @click="addTeamScore('B', 3)">+3</button>
                      <button
                          class="rc-btn rc-btn--pill rc-btn--ghost"
                          @click="addTeamScore('B', -1)"
                          :disabled="teams.B.score <= 0"
                      >
                        -1
                      </button>
                    </div>
                  </div>
                </div>

                <div>
                  <div class="rc-mini-title">파울</div>
                  <div class="rc-btn-row">
                    <button class="rc-btn rc-btn--pill" @click="addTeamFoul('B', 1)">+1</button>
                    <button
                        class="rc-btn rc-btn--pill rc-btn--ghost"
                        @click="addTeamFoul('B', -1)"
                        :disabled="teams.B.fouls <= 0"
                    >
                      -1
                    </button>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>

        <div class="rc-card rc-card--fill">
          <div class="rc-players">
            <div class="rc-players__head">
              <button class="rc-btn rc-btn--mini2" @click="openRoster('B')">선수 변경</button>
              <div class="rc-stathead">
                <div>득점</div>
                <div>어시</div>
                <div>리바</div>
                <div>파울</div>
              </div>
            </div>

            <div class="rc-statgrid" v-if="activePlayers.B.length">
              <div class="rc-statrow2" v-for="p in activePlayers.B" :key="p.id">
                <div class="rc-no">{{ p.no }}</div>

                <div class="rc-statcell">
                  <div class="rc-num">{{ p.points }}</div>
                  <button class="rc-plus" @click="addPlayerStat('B', p.id, 'points', 1)">+1</button>
                </div>

                <div class="rc-statcell">
                  <div class="rc-num">{{ p.assists }}</div>
                  <button class="rc-plus" @click="addPlayerStat('B', p.id, 'assists', 1)">+1</button>
                </div>

                <div class="rc-statcell">
                  <div class="rc-num">{{ p.rebounds }}</div>
                  <button class="rc-plus" @click="addPlayerStat('B', p.id, 'rebounds', 1)">+1</button>
                </div>

                <div class="rc-statcell">
                  <div class="rc-num">{{ p.fouls }}</div>
                  <button class="rc-plus" @click="addPlayerStat('B', p.id, 'fouls', 1)">+1</button>
                </div>
              </div>
            </div>
          </div>
        </div>
      </section>
    </div>


    <div v-if="timeModal.open" class="tm-overlay" @click.self="closeTimeModal">
      <div class="tm-panel">
        <div class="tm-head">
          <div class="tm-title">전체 시간 설정</div>
          <button class="tm-x" @click="closeTimeModal">닫기</button>
        </div>

        <div class="tm-body">
          <div class="tm-row">
            <div class="tm-label">분</div>
            <input class="tm-input" v-model="timeModal.mm" inputmode="numeric" />
          </div>
          <div class="tm-row">
            <div class="tm-label">초</div>
            <input class="tm-input" v-model="timeModal.ss" inputmode="numeric" />
          </div>

          <div class="tm-hint">예: 10:00 → 분 10, 초 00</div>

          <div class="tm-actions">
            <button class="tm-btn tm-btn--ghost" @click="closeTimeModal">취소</button>
            <button class="tm-btn tm-btn--primary" @click="applyTimeModal">적용</button>
          </div>
        </div>
      </div>
    </div>


    <RosterModal
        v-if="rosterModal.open"
        :team="rosterModal.team"
        :teamName="teams[rosterModal.team].name"
        :players="rosterPlayers[rosterModal.team]"
        @close="closeRoster"
        @save="saveRoster"
    />
  </div>
</template>

<script>
import "./remote-control.css";
import RosterModal from "./RosterModal.vue";
import { connectWS, sendCommand } from "@/shared/wsClient";

export default {
  name: "RemoteControl",
  components: { RosterModal },
  data() {
    return {
      quarter: 1,
      teams: {
        A: { name: "Home", score: 0, fouls: 0 },
        B: { name: "Away", score: 0, fouls: 0 }
      },

      gameClockSec: 10 * 60,
      shotClockSec: 24,
      gameTimer: null,
      shotTimer: null,
      isGameRunning: false,
      isShotRunning: false,

      rosterPlayers: {
        A: [
          { id: 1, no: "", name: "", selected: false },
          { id: 2, no: "", name: "", selected: false },
          { id: 3, no: "", name: "", selected: false },
          { id: 4, no: "", name: "", selected: false },
          { id: 5, no: "", name: "", selected: false }
        ],
        B: [
          { id: 1, no: "", name: "", selected: false },
          { id: 2, no: "", name: "", selected: false },
          { id: 3, no: "", name: "", selected: false },
          { id: 4, no: "", name: "", selected: false },
          { id: 5, no: "", name: "", selected: false }
        ]
      },

      players: {
        A: [],
        B: []
      },

      rosterModal: {
        open: false,
        team: "A"
      },

      timeModal: {
        open: false,
        mm: "10",
        ss: "00"
      }
    };
  },
  computed: {
    activePlayers() {
      return {
        A: this.players.A,
        B: this.players.B
      };
    }
  },
  mounted() {
    this.pushState();

    connectWS((s) => {
      if (!s) return;
      if (typeof s.quarter === "number") this.quarter = s.quarter;
      if (typeof s.gameClockSec === "number") this.gameClockSec = s.gameClockSec;
      if (typeof s.shotClockSec === "number") this.shotClockSec = s.shotClockSec;

      if (typeof s.isGameRunning === "boolean") this.isGameRunning = s.isGameRunning;
      if (typeof s.isShotRunning === "boolean") this.isShotRunning = s.isShotRunning;

      if (s.teams) {
        if (s.teams.Home || s.teams.Away) this.teams = s.teams;
        else if (s.teams.A || s.teams.B) {
          this.teams = {
            Home: s.teams.A,
            Away: s.teams.B
          };
        }
      }

      if (s.players) {
        if (s.players.Home || s.players.Away) this.players = s.players;
        else if (s.players.A || s.players.B) {
          this.players = {
            Home: s.players.A || [],
            Away: s.players.B || []
          };
        }
      }

      if (s.rosterPlayers) {
        if (s.rosterPlayers.Home || s.rosterPlayers.Away) this.rosterPlayers = s.rosterPlayers;
        else if (s.rosterPlayers.A || s.rosterPlayers.B) {
          this.rosterPlayers = {
            Home: s.rosterPlayers.A || [],
            Away: s.rosterPlayers.B || []
          };
        }
      }

      if (s.timeModal) this.timeModal = s.timeModal;
      if (s.rosterModal) this.rosterModal = s.rosterModal;
    });
  },
  beforeDestroy() {
    this.stopGameClock();
    this.stopShotClock();
  },
  methods: {
    buildState() {
      return {
        quarter: this.quarter,
        gameClockSec: this.gameClockSec,
        shotClockSec: this.shotClockSec,
        isGameRunning: this.isGameRunning,
        isShotRunning: this.isShotRunning,
        teams: this.teams,
        players: this.players,
        rosterPlayers: this.rosterPlayers
      };
    },
    pushState() {
      const next = this.buildState();
      sendCommand("STATE_SET", JSON.stringify(next));
    },

    resetAll() {
      this.quarter = 1;
      this.teams.A.score = 0;
      this.teams.B.score = 0;
      this.teams.A.fouls = 0;
      this.teams.B.fouls = 0;
      this.resetGameClock();
      this.resetShotClock();
      this.pushState();
    },

    changeQuarter(delta) {
      const next = this.quarter + delta;
      if (next < 1 || next > 4) return;
      this.quarter = next;
      this.pushState();
    },

    addTeamScore(teamKey, delta) {
      const t = this.teams[teamKey];
      t.score = Math.max(0, t.score + delta);
      this.pushState();
    },
    addTeamFoul(teamKey, delta) {
      const t = this.teams[teamKey];
      t.fouls = Math.max(0, t.fouls + delta);
      this.pushState();
    },

    addPlayerStat(teamKey, playerId, field, delta) {
      const list = this.players[teamKey];
      const p = list.find(x => x.id === playerId);
      if (!p) return;
      p[field] = Math.max(0, (p[field] || 0) + delta);
      this.pushState();
    },

    openRoster(teamKey) {
      if (teamKey !== "Home" && teamKey !== "Away") return;
      this.rosterModal.team = teamKey;
      this.rosterModal.open = true;
      this.pushState();
    },
    closeRoster() {
      this.rosterModal.open = false;
      this.pushState();
    },
    saveRoster({ team, players }) {
      this.rosterPlayers[team] = players;

      const selected = players.filter(p => p.selected).slice(0, 5);

      this.players[team] = selected.map(p => ({
        id: p.id,
        no: p.no,
        name: p.name,
        points: 0,
        assists: 0,
        rebounds: 0,
        fouls: 0
      }));

      this.closeRoster();
      this.pushState();
    },

    toggleGameClock() {
      if (this.isGameRunning) this.stopGameClock();
      else this.startGameClock();
    },
    toggleShotClock() {
      if (this.isShotRunning) this.stopShotClock();
      else this.startShotClock();
    },

    startGameClock() {
      if (this.gameTimer) return;
      this.isGameRunning = true;

      this.gameTimer = setInterval(() => {
        if (this.gameClockSec <= 0) {
          this.stopGameClock();
          return;
        }
        this.gameClockSec -= 1;
        this.pushState();
      }, 1000);

      this.pushState();
    },
    stopGameClock() {
      if (this.gameTimer) clearInterval(this.gameTimer);
      this.gameTimer = null;
      this.isGameRunning = false;
      this.pushState();
    },
    resetGameClock() {
      this.stopGameClock();
      this.gameClockSec = 10 * 60;
      this.pushState();
    },
    adjustGameClock(delta) {
      this.gameClockSec = Math.max(0, this.gameClockSec + delta);
      this.pushState();
    },

    startShotClock() {
      if (this.shotTimer) return;
      this.isShotRunning = true;

      this.shotTimer = setInterval(() => {
        if (this.shotClockSec <= 0) {
          this.stopShotClock();
          return;
        }
        this.shotClockSec -= 1;
        this.pushState();
      }, 1000);

      this.pushState();
    },
    stopShotClock() {
      if (this.shotTimer) clearInterval(this.shotTimer);
      this.shotTimer = null;
      this.isShotRunning = false;
      this.pushState();
    },
    resetShotClock() {
      this.stopShotClock();
      this.shotClockSec = 24;
      this.pushState();
    },
    setShotClock(value) {
      this.shotClockSec = value;
      this.pushState();
    },
    adjustShotClock(delta) {
      this.shotClockSec = Math.max(0, this.shotClockSec + delta);
      this.pushState();
    },

    openTimeModal() {
      this.timeModal.open = true;
      const mm = Math.floor(this.gameClockSec / 60);
      const ss = this.gameClockSec % 60;
      this.timeModal.mm = String(mm);
      this.timeModal.ss = String(ss).padStart(2, "0");
      this.pushState();
    },
    closeTimeModal() {
      this.timeModal.open = false;
      this.pushState();
    },
    applyTimeModal() {
      const mm = parseInt(this.timeModal.mm, 10);
      const ss = parseInt(this.timeModal.ss, 10);
      if (Number.isNaN(mm) || Number.isNaN(ss)) return;

      const safeSs = Math.min(59, Math.max(0, ss));
      const total = Math.max(0, mm * 60 + safeSs);

      this.gameClockSec = total;
      this.closeTimeModal();
      this.pushState();
    },

    formatMMSS(sec) {
      const m = Math.floor(sec / 60);
      const s = sec % 60;
      return `${String(m).padStart(2, "0")}:${String(s).padStart(2, "0")}`;
    }
  }
};
</script>
