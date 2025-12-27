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
                  <button class="rc-btn rc-btn--mini" @click="changeQuarter(1)">+1</button>
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

              <input
                class="rc-input"
                :value="teams.Home.homeName"
                placeholder="Home"
                @change="changeName('Home', $event.target.value)"
                maxlength="4"
              />
              <div class="rc-team__scoretext">{{ teams.Home.homeScore }}</div>
            </div>

            <div class="rc-team__onebox">
              <div class="rc-team__meta-row">
                <div class="rc-meta-label">팀 파울</div>
                <div class="rc-meta-value">{{ teams.Home.homeFoul }}</div>
              </div>

              <div class="rc-team__controls-row">
                <div>
                  <div class="rc-mini-title">점수</div>
                  <div class="rc-score-2rows">
                    <div class="rc-score-2rows__row">
                      <button class="rc-btn rc-btn--pill" @click="addTeamScore('Home', 1)">+1</button>
                      <button class="rc-btn rc-btn--pill" @click="addTeamScore('Home', 2)">+2</button>
                    </div>
                    <div class="rc-score-2rows__row">
                      <button class="rc-btn rc-btn--pill" @click="addTeamScore('Home', 3)">+3</button>
                      <button
                          class="rc-btn rc-btn--pill rc-btn--ghost"
                          @click="addTeamScore('Home', -1)"
                          :disabled="teams.Home.homeScore <= 0"
                      >
                        -1
                      </button>
                    </div>
                  </div>
                </div>

                <div>
                  <div class="rc-mini-title">파울</div>
                  <div class="rc-btn-row">
                    <button class="rc-btn rc-btn--pill" @click="addTeamFoul('Home', 1)">+1</button>
                    <button
                        class="rc-btn rc-btn--pill rc-btn--ghost"
                        @click="addTeamFoul('Home', -1)"
                        :disabled="teams.Home.homeFoul <= 0"
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
              <button class="rc-btn rc-btn--mini2" @click="openRoster('Home')">선수 변경</button>
              <div class="rc-stathead">
                <div>득점</div>
                <div>어시</div>
                <div>리바</div>
                <div>스틸</div>
                <div>파울</div>
              </div>
            </div>

            <div class="rc-statgrid" v-if="activePlayers.Home.length">
              <div class="rc-statrow2" v-for="p in activePlayers.Home" :key="p.id">
                <div class="rc-no">{{ p.no }}</div>

                <div class="rc-statcell">
                  <div class="rc-num">{{ p.points }}</div>
                  <button class="rc-plus" @click="addPlayerStat('Home', p.id, 'points', 1)">+1</button>
                </div>

                <div class="rc-statcell">
                  <div class="rc-num">{{ p.assists }}</div>
                  <button class="rc-plus" @click="addPlayerStat('Home', p.id, 'assists', 1)">+1</button>
                </div>

                <div class="rc-statcell">
                  <div class="rc-num">{{ p.rebounds }}</div>
                  <button class="rc-plus" @click="addPlayerStat('Home', p.id, 'rebounds', 1)">+1</button>
                </div>

                <div class="rc-statcell">
                  <div class="rc-num">{{ p.steals }}</div>
                  <button class="rc-plus" @click="addPlayerStat('Home', p.id, 'steals', 1)">+1</button>
                </div>

                <div class="rc-statcell">
                  <div class="rc-num">{{ p.fouls }}</div>
                  <button class="rc-plus" @click="addPlayerStat('Home', p.id, 'fouls', 1)">+1</button>
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
              <input
                class="rc-input"
                :value="teams.Away.awayName"
                placeholder="Away"
                @change="changeName('Away', $event.target.value)"
                maxlength="4"
              />
              <div class="rc-team__scoretext">{{ teams.Away.awayScore }}</div>
            </div>

            <div class="rc-team__onebox">
              <div class="rc-team__meta-row">
                <div class="rc-meta-label">팀 파울</div>
                <div class="rc-meta-value">{{ teams.Away.awayFoul }}</div>
              </div>

              <div class="rc-team__controls-row">
                <div>
                  <div class="rc-mini-title">점수</div>
                  <div class="rc-score-2rows">
                    <div class="rc-score-2rows__row">
                      <button class="rc-btn rc-btn--pill" @click="addTeamScore('Away', 1)">+1</button>
                      <button class="rc-btn rc-btn--pill" @click="addTeamScore('Away', 2)">+2</button>
                    </div>
                    <div class="rc-score-2rows__row">
                      <button class="rc-btn rc-btn--pill" @click="addTeamScore('Away', 3)">+3</button>
                      <button
                          class="rc-btn rc-btn--pill rc-btn--ghost"
                          @click="addTeamScore('Away', -1)"
                          :disabled="teams.Away.awayScore <= 0"
                      >
                        -1
                      </button>
                    </div>
                  </div>
                </div>

                <div>
                  <div class="rc-mini-title">파울</div>
                  <div class="rc-btn-row">
                    <button class="rc-btn rc-btn--pill" @click="addTeamFoul('Away', 1)">+1</button>
                    <button
                        class="rc-btn rc-btn--pill rc-btn--ghost"
                        @click="addTeamFoul('Away', -1)"
                        :disabled="teams.Away.awayFoul <= 0"
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
              <button class="rc-btn rc-btn--mini2" @click="openRoster('Away')">선수 변경</button>
              <div class="rc-stathead">
                <div>득점</div>
                <div>어시</div>
                <div>리바</div>
                <div>스틸</div>
                <div>파울</div>
              </div>
            </div>

            <div class="rc-statgrid" v-if="activePlayers.Away.length">
              <div class="rc-statrow2" v-for="p in activePlayers.Away" :key="p.id">
                <div class="rc-no">{{ p.no }}</div>

                <div class="rc-statcell">
                  <div class="rc-num">{{ p.points }}</div>
                  <button class="rc-plus" @click="addPlayerStat('Away', p.id, 'points', 1)">+1</button>
                </div>

                <div class="rc-statcell">
                  <div class="rc-num">{{ p.assists }}</div>
                  <button class="rc-plus" @click="addPlayerStat('Away', p.id, 'assists', 1)">+1</button>
                </div>

                <div class="rc-statcell">
                  <div class="rc-num">{{ p.rebounds }}</div>
                  <button class="rc-plus" @click="addPlayerStat('Away', p.id, 'rebounds', 1)">+1</button>
                </div>

                <div class="rc-statcell">
                  <div class="rc-num">{{ p.steals }}</div>
                  <button class="rc-plus" @click="addPlayerStat('Away', p.id, 'steals', 1)">+1</button>
                </div>

                <div class="rc-statcell">
                  <div class="rc-num">{{ p.fouls }}</div>
                  <button class="rc-plus" @click="addPlayerStat('Away', p.id, 'fouls', 1)">+1</button>
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
import {connectWS, sendCommand} from "@/shared/wsClient";
import {ActionType} from "@/shared/actionTypes";

export default {
  name: "RemoteControl",
  components: { RosterModal },
  data() {
    return {
      quarter: 1,
      hydratedFromServer: false,
      teams: {
        Home: { homeName: "Home", homeScore: 0, homeFoul: 0 },
        Away: { awayName: "Away", awayScore: 0, awayFoul: 0 },
      },

      gameClockSec: 7 * 60,
      shotClockSec: 24,
      // gameTimer: null, // 제거
      // shotTimer: null, // 제거
      isGameRunning: false,
      isShotRunning: false,

      rosterPlayers: {
        Home: [
          { id: 1, no: "", name: "", selected: false },
          { id: 2, no: "", name: "", selected: false },
          { id: 3, no: "", name: "", selected: false },
          { id: 4, no: "", name: "", selected: false },
          { id: 5, no: "", name: "", selected: false },
        ],
        Away: [
          { id: 1, no: "", name: "", selected: false },
          { id: 2, no: "", name: "", selected: false },
          { id: 3, no: "", name: "", selected: false },
          { id: 4, no: "", name: "", selected: false },
          { id: 5, no: "", name: "", selected: false },
        ]
      },

      players: {
        Home: [],
        Away: []
      },

      rosterModal: {
        open: false,
        team: "Home"
      },

      timeModal: {
        open: false,
        mm: "10",
        ss: "00",
      },
    };
  },
  computed: {
    activePlayers() {
      return {
        Home: this.players.Home,
        Away: this.players.Away,
      };
    }
  },
  mounted() {
    connectWS((s) => {
      if (!s) return;

      // Always update state from server
      if (s.quarter !== undefined) this.quarter = s.quarter;
      if (s.gameClockSec !== undefined) this.gameClockSec = s.gameClockSec;
      if (s.shotClockSec !== undefined) this.shotClockSec = s.shotClockSec;
      if (s.isGameRunning !== undefined) this.isGameRunning = s.isGameRunning;
      if (s.isShotRunning !== undefined) this.isShotRunning = s.isShotRunning;

      if (s.homeName !== undefined) this.teams.Home.homeName = s.homeName;
      if (s.homeScore !== undefined) this.teams.Home.homeScore = s.homeScore;
      if (s.homeFoul !== undefined) this.teams.Home.homeFoul = s.homeFoul;

      if (s.awayName !== undefined) this.teams.Away.awayName = s.awayName;
      if (s.awayScore !== undefined) this.teams.Away.awayScore = s.awayScore;
      if (s.awayFoul !== undefined) this.teams.Away.awayFoul = s.awayFoul;

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
    });
  },
  beforeDestroy() {
    // 타이머 정리 로직 제거
  },
  methods: {
    pushState(action, payload) {
      sendCommand(action, payload);
    },

    resetAll() {
      this.resetGameClock();
      this.resetShotClock();

      this.pushState(ActionType.QUARTER, { isReset: true, quarter: 1 });

      this.pushState(ActionType.HOME_SETTING, {
        isReset: true,
        homeName: "Home",
        homeScore: 0,
        homeFoul: 0,
      });
      this.pushState(ActionType.AWAY_SETTING, {
        isReset: true,
        awayName: "Away",
        awayScore: 0,
        awayFoul: 0,
      });
      // TODO: 선수 정보 리셋 로직 추가 필요
    },

    changeQuarter(delta) {
      const nextQuarter = this.quarter + delta;
      if (nextQuarter < 1) return;
      this.pushState(ActionType.QUARTER, { quarter: nextQuarter });
    },

    changeName(teamKey, nextName) {
      const fallback = teamKey === "Home" ? "Home" : "Away";
      let name = String(nextName ?? "").trim() || fallback;
      name = name.slice(0, 4);

      const payload = {};
      if (teamKey === "Home") {
        payload.homeName = name;
      } else {
        payload.awayName = name;
      }

      const action = teamKey === "Home" ? ActionType.HOME_SETTING : ActionType.AWAY_SETTING;
      this.pushState(action, payload);
    },

    addTeamScore(teamKey, delta) {
      const payload = { score: delta };
      const action = teamKey === "Home" ? ActionType.HOME_SCORE : ActionType.AWAY_SCORE;
      this.pushState(action, payload);
    },
    addTeamFoul(teamKey, delta) {
      const payload = { foul: delta };
      const action = teamKey === "Home" ? ActionType.HOME_FOUL : ActionType.AWAY_FOUL;
      this.pushState(action, payload);
    },

    addPlayerStat(teamKey, playerId, field, delta) {
      // TODO: 서버 전송 로직으로 변경 필요
      const list = this.players[teamKey];
      const p = list.find(x => x.id === playerId);
      if (!p) return;
      p[field] = Math.max(0, (p[field] || 0) + delta);
    },

    openRoster(teamKey) {
      if (teamKey !== "Home" && teamKey !== "Away") return;
      this.rosterModal.team = teamKey;
      this.rosterModal.open = true;
    },
    closeRoster() {
      this.rosterModal.open = false;
    },
    saveRoster({ team, players }) {
      // TODO: 서버 전송 로직으로 변경 필요
      this.rosterPlayers[team] = players;

      const selected = players.filter(p => p.selected).slice(0, 5);

      this.players[team] = selected.map(p => ({
        id: p.id,
        no: p.no,
        name: p.name,
        points: 0,
        assists: 0,
        rebounds: 0,
        steals: 0,
        fouls: 0,
      }));

      this.closeRoster();
    },

    // === 시간 관련 로직 (서버 전송 방식으로 변경) ===
    toggleGameClock() {
      const payload = { isRunning: !this.isGameRunning };
      this.pushState(ActionType.GAME_TIME, payload);
    },
    toggleShotClock() {
      const payload = { isRunning: !this.isShotRunning };
      this.pushState(ActionType.SHOT_CLOCK, payload);
    },

    resetGameClock() {
      const payload = {
        isRunning: false,
        isReset: true,
        resetTime: 7 * 60
      };
      this.pushState(ActionType.GAME_TIME, payload);
    },
    adjustGameClock(delta) {
      const payload = { adjust: delta };
      this.pushState(ActionType.GAME_TIME, payload);
    },

    resetShotClock() {
      const payload = {
        isRunning: false,
        isReset: true,
        resetTime: 24
      };
      this.pushState(ActionType.SHOT_CLOCK, payload);
    },
    setShotClock(value) {
      const payload = { isReset: true, resetTime: value };
      this.pushState(ActionType.SHOT_CLOCK, payload);
    },
    adjustShotClock(delta) {
      const payload = { adjust: delta };
      this.pushState(ActionType.SHOT_CLOCK, payload);
    },

    openTimeModal() {
      this.timeModal.open = true;
      const mm = Math.floor(this.gameClockSec / 60);
      const ss = this.gameClockSec % 60;
      this.timeModal.mm = String(mm);
      this.timeModal.ss = String(ss).padStart(2, "0");
    },
    closeTimeModal() {
      this.timeModal.open = false;
    },
    applyTimeModal() {
      const mm = parseInt(this.timeModal.mm, 10);
      const ss = parseInt(this.timeModal.ss, 10);
      if (Number.isNaN(mm) || Number.isNaN(ss)) return;

      const safeSs = Math.min(59, Math.max(0, ss));
      const total = Math.max(0, mm * 60 + safeSs);

      const payload = {
        isReset: true,
        resetTime: total
      };
      this.pushState(ActionType.GAME_TIME, payload);
      this.closeTimeModal();
    },

    formatMMSS(sec) {
      const m = Math.floor(sec / 60);
      const s = sec % 60;
      return `${String(m).padStart(2, "0")}:${String(s).padStart(2, "0")}`;
    }
  }
};
</script>
