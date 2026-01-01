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
                  <button class="rc-btn rc-btn--compact" @click="setShotClock14(14)">14</button>
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
                      <button class="rc-btn rc-btn--pill" @click="startPlayerSelection(1)" :disabled="activePlayers.Home.length === 0">+1</button>
                      <button class="rc-btn rc-btn--pill" @click="startPlayerSelection(2)" :disabled="activePlayers.Home.length === 0">+2</button>
                    </div>
                    <div class="rc-score-2rows__row">
                      <button class="rc-btn rc-btn--pill" @click="startPlayerSelection(3)" :disabled="activePlayers.Home.length === 0">+3</button>
                      <button
                          class="rc-btn rc-btn--pill rc-btn--ghost"
                          @click="undoLastScore"
                          :disabled="!lastScoringPlayer"
                      >
                        -1
                      </button>
                    </div>
                  </div>
                </div>

                <div>
                  <div class="rc-mini-title">파울</div>
                  <div class="rc-btn-row">
                    <button class="rc-btn rc-btn--pill" @click="addTeamFoul('Home', 1)" :disabled="activePlayers.Home.length === 0">+1</button>
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
                  <button class="rc-plus" :class="{ 'blinking-effect': isPlayerSelectMode }" @click="confirmPlayerScore('Home', p.id)" :disabled="!isPlayerSelectMode">득점</button>
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
            <div class="rc-players__empty-message" v-else>
              선수를 등록한 후에 점수를 입력할 수 있습니다.
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
                      <button class="rc-btn rc-btn--pill" @click="startPlayerSelection(1)" :disabled="activePlayers.Away.length === 0">+1</button>
                      <button class="rc-btn rc-btn--pill" @click="startPlayerSelection(2)" :disabled="activePlayers.Away.length === 0">+2</button>
                    </div>
                    <div class="rc-score-2rows__row">
                      <button class="rc-btn rc-btn--pill" @click="startPlayerSelection(3)" :disabled="activePlayers.Away.length === 0">+3</button>
                      <button
                          class="rc-btn rc-btn--pill rc-btn--ghost"
                          @click="undoLastScore"
                          :disabled="!lastScoringPlayer"
                      >
                        -1
                      </button>
                    </div>
                  </div>
                </div>

                <div>
                  <div class="rc-mini-title">파울</div>
                  <div class="rc-btn-row">
                    <button class="rc-btn rc-btn--pill" @click="addTeamFoul('Away', 1)" :disabled="activePlayers.Away.length === 0">+1</button>
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
                  <button class="rc-plus" :class="{ 'blinking-effect': isPlayerSelectMode }" @click="confirmPlayerScore('Away', p.id)" :disabled="!isPlayerSelectMode">득점</button>
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
            <div class="rc-players__empty-message" v-else>
              선수를 등록한 후에 점수를 입력할 수 있습니다.
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

const NINCORE_BOARD_STATE = "nincore-board-state";

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
      strictGameTime: 7* 60,
      gameClockSec: 7 * 60,
      shotClockSec: 24,
      isGameRunning: false,
      isShotRunning: false,

      isPlayerSelectMode: false,
      pointsToAdd: 0,
      lastScoringPlayer: null,

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
        team: "Home",
        name: ""
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
    },
  },
  mounted() {
    this.loadStateFromLocalStorage();
    connectWS((s) => {
      if (!s) return;
      if (typeof s.quarter === "number") this.quarter = s.quarter;
      if (typeof s.gameTime === "number") this.gameClockSec = s.gameTime;
      if (typeof s.shotClock === "number") this.shotClockSec = s.shotClock;

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
  },
  methods: {
    saveStateToLocalStorage() {
      const stateToSave = {
        quarter: this.quarter,
        teams: this.teams,
        players: this.players,
        rosterPlayers: this.rosterPlayers,
      };
      localStorage.setItem(NINCORE_BOARD_STATE, JSON.stringify(stateToSave));
    },
    loadStateFromLocalStorage() {
      const savedState = localStorage.getItem(NINCORE_BOARD_STATE);
      if (savedState) {
        try {
          const parsedState = JSON.parse(savedState);
          this.quarter = parsedState.quarter || this.quarter;
          this.teams = parsedState.teams || this.teams;
          this.players = parsedState.players || this.players;
          this.rosterPlayers = parsedState.rosterPlayers || this.rosterPlayers;
        } catch (e) {
          console.error("Error loading state from localStorage", e);
        }
      }
    },

    applyStateToView(s) {
      if (!s) return;

      // Always update state from server
      if (s.quarter !== undefined) this.quarter = s.quarter;
      if (s.gameTime !== undefined) this.gameClockSec = s.gameTime;
      if (s.shotClock !== undefined) this.shotClockSec = s.shotClock;
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
    },

    pushState(action, payload) {
      sendCommand(action, payload);
    },

    resetAll() {
      // 1. Reset local component state
      this.quarter = 1;
      this.teams.Home.homeName = "Home";
      this.teams.Home.homeScore = 0;
      this.teams.Home.homeFoul = 0;
      this.teams.Away.awayName = "Away";
      this.teams.Away.awayScore = 0;
      this.teams.Away.awayFoul = 0;
      this.lastScoringPlayer = null;

      // Clear active players
      this.players.Home = [];
      this.players.Away = [];

      // Deselect all players in the main roster
      if (this.rosterPlayers.Home) {
        this.rosterPlayers.Home.forEach(p => p.selected = false);
      }
      if (this.rosterPlayers.Away) {
        this.rosterPlayers.Away.forEach(p => p.selected = false);
      }

      // 2. Persist this fully-reset state to localStorage
      this.saveStateToLocalStorage();

      // 3. Push reset state to server
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
    },

    changeQuarter(delta) {
      const nextQuarter = this.quarter + delta;
      if (nextQuarter < 1) return;
      this.quarter = nextQuarter;
      this.pushState(ActionType.QUARTER, { quarter: nextQuarter });
      this.saveStateToLocalStorage();
    },

    changeName(teamKey, nextName) {
      const fallback = teamKey === "Home" ? "Home" : "Away";
      let name = String(nextName ?? "").trim() || fallback;
      name = name.slice(0, 4);

      const payload = {};
      if (teamKey === "Home") {
        this.teams.Home.homeName = name;
        payload.homeName = name;
      } else {
        this.teams.Away.awayName = name;
        payload.awayName = name;
      }

      const action = teamKey === "Home" ? ActionType.HOME_NAME : ActionType.AWAY_NAME;
      this.pushState(action, payload);
      this.saveStateToLocalStorage();
    },

    addTeamScore(teamKey, delta) {
      const payload = { score: delta };
      if (teamKey === 'Home') {
        this.teams.Home.homeScore = Math.max(0, this.teams.Home.homeScore + delta);
      } else {
        this.teams.Away.awayScore = Math.max(0, this.teams.Away.awayScore + delta);
      }
      const action = teamKey === "Home" ? ActionType.HOME_SCORE : ActionType.AWAY_SCORE;
      this.pushState(action, payload);
      this.saveStateToLocalStorage();
    },
    addTeamFoul(teamKey, delta) {
      if (teamKey === 'Home') {
        this.teams.Home.homeFoul = Math.max(0, this.teams.Home.homeFoul + delta);
      } else {
        this.teams.Away.awayFoul = Math.max(0, this.teams.Away.awayFoul + delta);
      }
      const payload = { foul: delta };
      const action = teamKey === "Home" ? ActionType.HOME_FOUL : ActionType.AWAY_FOUL;
      this.pushState(action, payload);
      this.saveStateToLocalStorage();
    },

    startPlayerSelection(points) {
      this.isPlayerSelectMode = true;
      this.pointsToAdd = points;
    },

    undoLastScore() {
      if (!this.lastScoringPlayer) return;

      const { teamKey, playerId } = this.lastScoringPlayer;
      const list = this.players[teamKey];
      const p = list.find(x => x.id === playerId);

      if (p) {
        // Update player stat locally
        p.points = Math.max(0, (p.points || 0) - 1);

        // Update team score (this now handles local state, saving, and pushing)
        this.addTeamScore(teamKey, -1);

        // Reset last score trackers
        this.lastScoringPlayer = null;
      }
    },

    confirmPlayerScore(teamKey, playerId) {
      if (!this.isPlayerSelectMode) return;

      const list = this.players[teamKey];
      const p = list.find(x => x.id === playerId);
      if (!p) return;

      const points_to_add = this.pointsToAdd;

      // Reset selection mode state
      this.isPlayerSelectMode = false;
      this.pointsToAdd = 0;

      // Update player stat locally
      p.points = Math.max(0, (p.points || 0) + points_to_add);

      // Update team score (this also handles local state, saving, and pushing)
      this.addTeamScore(teamKey, points_to_add);

      // Track this action for undo
      this.lastScoringPlayer = { teamKey, playerId };
    },

    addPlayerStat(teamKey, playerId, field, delta) {
      const list = this.players[teamKey];
      const p = list.find(x => x.id === playerId);
      if (!p) return;
      p[field] = Math.max(0, (p[field] || 0) + delta);
      this.saveStateToLocalStorage();
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
      this.saveStateToLocalStorage();
    },

    toggleGameClock() {
      this.isGameRunning = !this.isGameRunning;
      const payload = { isRunning: this.isGameRunning };
      this.pushState(ActionType.TOGGLE_GAME_TIME, payload);
    },
    toggleShotClock() {
      this.isShotRunning = !this.isShotRunning;
      const payload = { isRunning: this.isShotRunning };
      this.pushState(ActionType.TOGGLE_SHOT_CLOCK, payload);
    },

    resetGameClock() {
      const payload = {
        isRunning: false,
        isReset: true,
        resetTime: this.strictGameTime,
      };
      this.pushState(ActionType.SETTING_GAME_TIME, payload);
    },

    adjustGameClock(delta) {
      const payload = {
        isRunning: false,
        isReset: false,
        adjust: delta };
      this.pushState(ActionType.SETTING_GAME_TIME, payload);
    },

    resetShotClock() {
      const payload = { isRunning: false, isReset: true, isSetHalf: false};
      this.pushState(ActionType.SETTING_SHOT_CLOCK, payload);
    },
    setShotClock14() {
      const payload = { isRunning: false, isReset: false, isSetHalf: true};
      this.pushState(ActionType.SETTING_SHOT_CLOCK, payload);
    },
    adjustShotClock(delta) {
      const payload = { isRunning: false, isReset: false, isSetHalf: false, adjust: delta };
      this.pushState(ActionType.SETTING_SHOT_CLOCK, payload);
    },

    openTimeModal() {
      this.timeModal.open = true;
      const mm = Math.floor(this.strictGameTime / 60);
      const ss = this.strictGameTime % 60;
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
      this.strictGameTime = mm * 60 + ss;
      const safeSs = Math.min(59, Math.max(0, ss));
      const total = Math.max(0, mm * 60 + safeSs);

      const payload = {
        isReset: true,
        resetTime: total,
      };
      this.pushState(ActionType.SETTING_GAME_TIME, payload);
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

<style scoped>
.blinking-effect {
  animation: blink 1s ease-in-out infinite;
  box-shadow: 0 0 8px 2px rgba(255, 215, 0, 0.8); /* Gold glow */
  border-radius: 4px;
  transition: box-shadow 0.3s ease;
}

@keyframes blink {
  0%, 100% {
    transform: scale(1);
    opacity: 1;
  }
  50% {
    transform: scale(1.05);
    opacity: 0.8;
    box-shadow: 0 0 12px 4px rgba(255, 215, 0, 1);
  }
}

.rc-plus:disabled {
  background-color: transparent;
  color: #b0b0b0;
  cursor: not-allowed;
  box-shadow: none;
  animation: none;
}

.rc-players__empty-message {
  padding: 2rem 1rem;
  text-align: center;
  color: #888;
  font-size: 0.9rem;
}
</style>
