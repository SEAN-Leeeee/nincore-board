<template>
  <div class="rc-page">
    <div class="rc-3row">
      <section class="rc-row rc-row--time">
        <div class="rc-card rc-time-card">
          <div class="rc-time-header">
            <div class="rc-time-header__main">경기 리모컨</div>
            <div>
              <button class="rc-btn rc-btn--ghost" @click="resetAll">리셋</button>
              <button class="rc-btn rc-btn--danger" @click="shutdownSession">종료</button>
            </div>
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
                      <button class="rc-btn rc-btn--pill" @click="startPlayerSelection('Home', 1)" :disabled="activePlayers.Home.length === 0">+1</button>
                      <button class="rc-btn rc-btn--pill" @click="startPlayerSelection('Home', 2)" :disabled="activePlayers.Home.length === 0">+2</button>
                    </div>
                    <div class="rc-score-2rows__row">
                      <button class="rc-btn rc-btn--pill" @click="startPlayerSelection('Home', 3)" :disabled="activePlayers.Home.length === 0">+3</button>
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
                    <button class="rc-btn rc-btn--pill"
                            @click="startFoulSelection('Home', 1)"
                            :disabled="activePlayers.Home.length === 0">+1</button>
                    <button
                        class="rc-btn rc-btn--pill rc-btn--ghost"
                        @click="startFoulSelection('Home', -1)"
                        :disabled="teams.Home.homeFoul <= 0 || activePlayers.Home.length === 0"
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
                  <button class="rc-plus" :class="{ 'blinking-effect': isPlayerSelectMode && scoreTargetTeam === 'Home' }" @click="confirmPlayerScore('Home', p.id)" :disabled="!isPlayerSelectMode || scoreTargetTeam !== 'Home'">득점</button>
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
                  <button
                      class="rc-plus"
                      :class="{ 'blinking-effect': isFoulSelectMode && foulTargetTeam === 'Home' }"
                      @click="onPlayerFoulClick('Home', p.id)"
                      :disabled="!isFoulSelectMode || (isFoulSelectMode && foulTargetTeam !== 'Home')"
                  >
                    {{ (isFoulSelectMode && foulTargetTeam === 'Home') ? '파울' : '+1' }}
                  </button>
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
                      <button class="rc-btn rc-btn--pill" @click="startPlayerSelection('Away', 1)" :disabled="activePlayers.Away.length === 0">+1</button>
                      <button class="rc-btn rc-btn--pill" @click="startPlayerSelection('Away', 2)" :disabled="activePlayers.Away.length === 0">+2</button>
                    </div>
                    <div class="rc-score-2rows__row">
                      <button class="rc-btn rc-btn--pill" @click="startPlayerSelection('Away', 3)" :disabled="activePlayers.Away.length === 0">+3</button>
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
                    <button class="rc-btn rc-btn--pill"
                            @click="startFoulSelection('Away', 1)"
                            :disabled="activePlayers.Away.length === 0">+1</button>

                    <button
                        class="rc-btn rc-btn--pill rc-btn--ghost"
                        @click="startFoulSelection('Away', -1)"
                        :disabled="teams.Away.awayFoul <= 0 || activePlayers.Away.length === 0"
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
                  <button class="rc-plus" :class="{ 'blinking-effect': isPlayerSelectMode && scoreTargetTeam === 'Away' }" @click="confirmPlayerScore('Away', p.id)" :disabled="!isPlayerSelectMode || scoreTargetTeam !== 'Away'">득점</button>
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
                  <button
                      class="rc-plus"
                      :class="{ 'blinking-effect': isFoulSelectMode && foulTargetTeam === 'Away' }"
                      @click="onPlayerFoulClick('Away', p.id)"
                      :disabled="!isFoulSelectMode || (isFoulSelectMode && foulTargetTeam !== 'Away')"
                  >
                    {{ (isFoulSelectMode && foulTargetTeam === 'Away') ? '파울' : '+1' }}
                  </button>
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
        :teamName="rosterModal.team === 'Home' ? teams.Home.homeName : teams.Away.awayName"
        :players="rosterPlayers[rosterModal.team]"
        @close="closeRoster"
        @save="saveRoster"
    />
  </div>
</template>

<script>
import { disconnectWS, sendCommand } from "@/shared/wsClient";
import "./remote-control.css";
import { ActionType } from "@/shared/actionTypes";
import { subscribeState, loadState, publishState } from "@/shared/stateChannel";
import RosterModal from "@/components/remote/RosterModal.vue";

const NINCORE_BOARD_STATE = "nincore-board-state";

export default {
  name: "RemoteControl",
  components: { RosterModal },
  data() {
    return {
      quarter: 1,
      teams: {
        Home: { homeName: "Home", homeScore: 0, homeFoul: 0 },
        Away: { awayName: "Away", awayScore: 0, awayFoul: 0 },
      },
      gameClockSec: 7 * 60,
      shotClockSec: 24,
      isGameRunning: false,
      isShotRunning: false,
      isPlayerSelectMode: false,
      pointsToAdd: 0,
      lastScoringPlayer: null,
      strictGameTime: 7 * 60,
      rosterPlayers: { Home: [], Away: [] },
      players: { Home: [], Away: [] },
      rosterModal: { open: false, team: "Home", name: "" },
      timeModal: { open: false, mm: "10", ss: "00" },
      unsubscribe: null,
      resetGuardUntil: 0,
      isFoulSelectMode: false,
      foulTargetTeam: null,
      foulDeltaToAdd: 0,
      scoreTargetTeam: null,
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
    this.unsubscribe = subscribeState(this.applyStateToView);
    const initialState = loadState();
    if (initialState) {
      this.applyStateToView(initialState);
    }
    window.addEventListener("beforeunload", this.handleBeforeUnload);
  },
  beforeDestroy() {
    window.removeEventListener("beforeunload", this.handleBeforeUnload);
    if (this.unsubscribe) {
      this.unsubscribe();
    }
  },
  methods: {
    applyStateToView(s) {
      if (!s) return;
      if (typeof s.quarter === "number") this.quarter = s.quarter;
      if (typeof s.gameTime === "number") this.gameClockSec = s.gameTime;
      if (typeof s.shotClock === "number") this.shotClockSec = s.shotClock;
      // ✅ 서버/WS 필드명 변형 호환
      if (typeof s.isGameRunning === "boolean") this.isGameRunning = s.isGameRunning;
      else if (typeof s.gameIsRunning === "boolean") this.isGameRunning = s.gameIsRunning;
      else if (typeof s.isRunningGame === "boolean") this.isGameRunning = s.isRunningGame;
      else if (typeof s.gameRunning === "boolean") this.isGameRunning = s.gameRunning;

      if (typeof s.isShotRunning === "boolean") this.isShotRunning = s.isShotRunning;
      else if (typeof s.shotIsRunning === "boolean") this.isShotRunning = s.shotIsRunning;
      else if (typeof s.isRunningShot === "boolean") this.isShotRunning = s.isRunningShot;
      else if (typeof s.shotRunning === "boolean") this.isShotRunning = s.shotRunning;

      // ✅ reset 직후 서버에서 이전 점수/파울이 다시 내려오는 경우 잠깐 무시(화면 원복 방지)
      const inResetGuard = Date.now() < (this.resetGuardUntil || 0);
      if (!inResetGuard) {
        if (s.homeScore !== undefined) this.teams.Home.homeScore = s.homeScore;
        if (s.homeFoul !== undefined) this.teams.Home.homeFoul = s.homeFoul;
        if (s.awayScore !== undefined) this.teams.Away.awayScore = s.awayScore;
        if (s.awayFoul !== undefined) this.teams.Away.awayFoul = s.awayFoul;
      } else {
        // guard 중에는 0으로 유지
        if (s.homeName !== undefined) this.teams.Home.homeName = s.homeName;
        if (s.awayName !== undefined) this.teams.Away.awayName = s.awayName;
      }

      if (s.homeName !== undefined) this.teams.Home.homeName = s.homeName;
      if (s.awayName !== undefined) this.teams.Away.awayName = s.awayName;
      if (s.players) {
        if (s.players.Home || s.players.Away) this.players = s.players;
      }
      if (s.rosterPlayers) {
        if (s.rosterPlayers.Home || s.rosterPlayers.Away) this.rosterPlayers = s.rosterPlayers;
      }
    },
    handleBeforeUnload() {
      const sessionId = sessionStorage.getItem("sessionId");
      if (sessionId) {
        const payload = JSON.stringify({ sessionId: sessionId });
        const blob = new Blob([payload], { type: "application/json" });
        navigator.sendBeacon("/api/logout", blob);
      }
    },
    pushState(action, payload) {
      const session = sessionStorage.getItem("sessionId");
      sendCommand(action, payload, session);
    },
    resetAll() {
      this.resetGuardUntil = Date.now() + 2500;

      const homeScoreNow = Number(this.teams.Home.homeScore ?? 0);
      const awayScoreNow = Number(this.teams.Away.awayScore ?? 0);
      const homeFoulNow = Number(this.teams.Home.homeFoul ?? 0);
      const awayFoulNow = Number(this.teams.Away.awayFoul ?? 0);

      if (ActionType.HOME_SCORE && homeScoreNow !== 0) {
        const d = -homeScoreNow;
        this.pushState(ActionType.HOME_SCORE, { score: d, delta: d, points: d });
      }
      if (ActionType.AWAY_SCORE && awayScoreNow !== 0) {
        const d = -awayScoreNow;
        this.pushState(ActionType.AWAY_SCORE, { score: d, delta: d, points: d });
      }
      if (ActionType.HOME_FOUL && homeFoulNow !== 0) {
        const d = -homeFoulNow;
        this.pushState(ActionType.HOME_FOUL, { foul: d, delta: d });
      }
      if (ActionType.AWAY_FOUL && awayFoulNow !== 0) {
        const d = -awayFoulNow;
        this.pushState(ActionType.AWAY_FOUL, { foul: d, delta: d });
      }

      this.quarter = 1;
      this.isGameRunning = false;
      this.isShotRunning = false;
      this.isPlayerSelectMode = false;
      this.isFoulSelectMode = false;
      this.foulTargetTeam = null;
      this.foulDeltaToAdd = 0;
      this.pointsToAdd = 0;
      this.lastScoringPlayer = null;

      this.gameClockSec = this.strictGameTime;
      this.shotClockSec = 24;

      this.teams.Home.homeScore = 0;
      this.teams.Home.homeFoul = 0;
      this.teams.Away.awayScore = 0;
      this.teams.Away.awayFoul = 0;

      console.log("homeeee score : " + this.teams.Home.homeScore)
      const resetStats = (list) =>
        (list || []).map((p) => ({
          ...p,
          points: 0,
          assists: 0,
          rebounds: 0,
          steals: 0,
          fouls: 0,
        }));
      this.players = {
        Home: resetStats(this.players.Home),
        Away: resetStats(this.players.Away),
      };

      // 2) ✅ 서버 동기화(호환)
      // 2-1) 혹시 서버가 RESET_ALL을 지원하면 한 방에 끝
      if (ActionType.RESET_ALL) {
        this.pushState(ActionType.RESET_ALL, {});
      }

      // 2-2) 서버가 RESET_ALL을 무시해도 최소한의 리셋이 되도록 개별 액션 전송
      // 팀 리셋
      if (ActionType.RESET_HOME) this.pushState(ActionType.RESET_HOME, {});
      if (ActionType.RESET_AWAY) this.pushState(ActionType.RESET_AWAY, {});

      // 쿼터/시간/샷클락 리셋
      if (ActionType.QUARTER) this.pushState(ActionType.QUARTER, { quarter: 1 });
      if (ActionType.SETTING_GAME_TIME) {
        this.pushState(ActionType.SETTING_GAME_TIME, {
          isRunning: false,
          isReset: true,
          resetTime: this.strictGameTime,
        });
      }
      if (ActionType.SETTING_SHOT_CLOCK) {
        this.pushState(ActionType.SETTING_SHOT_CLOCK, {
          isRunning: false,
          isReset: true,
          isSetHalf: false,
        });
      }
      // ✅ 서버가 '전체 상태 덮어쓰기'를 지원하면 한 번 더 강제(디스플레이 동기화 목적)
      if (ActionType.STATE_UPDATE) {
        this.pushState(ActionType.STATE_UPDATE, {
          quarter: 1,
          gameTime: this.strictGameTime,
          shotClock: 24,
          isGameRunning: false,
          isShotRunning: false,
          homeScore: 0,
          homeFoul: 0,
          awayScore: 0,
          awayFoul: 0,
          players: this.players,
          rosterPlayers: this.rosterPlayers,
          homeName: this.teams.Home.homeName,
          awayName: this.teams.Away.awayName,
        });
      }
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
      const action = teamKey === "Home" ? ActionType.HOME_NAME : ActionType.AWAY_NAME;
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
        startPlayerSelection(teamKey, points) {
          this.isFoulSelectMode = false;
          this.foulTargetTeam = null;
          this.foulDeltaToAdd = 0;

          this.isPlayerSelectMode = true;
          this.pointsToAdd = points;
          this.scoreTargetTeam = teamKey; // Set the target team for scoring
        },
        startFoulSelection(teamKey, delta) {
          if (teamKey !== "Home" && teamKey !== "Away") return;
          if (delta !== 1 && delta !== -1) return;
          this.isPlayerSelectMode = false;
          this.pointsToAdd = 0;
          this.scoreTargetTeam = null; // Clear score target when starting foul selection

          const currentTeamFoul = teamKey === "Home"
              ? Number(this.teams.Home.homeFoul || 0)
              : Number(this.teams.Away.awayFoul || 0);

          if (delta === -1 && currentTeamFoul <= 0) return;

          this.isFoulSelectMode = true;
          this.foulTargetTeam = teamKey;
          this.foulDeltaToAdd = delta;
        },

            confirmPlayerFoul(teamKey, playerId) {

              if (!this.isFoulSelectMode) return;

              if (this.foulTargetTeam !== teamKey) return;



              const delta = this.foulDeltaToAdd;

              if (delta !== 1 && delta !== -1) return;



              const list = this.players[teamKey] || [];

              const p = list.find((x) => x.id === playerId);

              if (!p) return;



                        // 개인 파울 증감(0 미만 방지)



                        p.fouls = Math.max(0, (p.fouls || 0) + delta);







                        // 팀 파울 재계산 (개인 파울 합산 및 5개 상한 적용)



                        this.recalculateTeamFouls(teamKey);







              // 선택 모드 종료

              this.isFoulSelectMode = false;

              this.foulTargetTeam = null;

              this.foulDeltaToAdd = 0;

              this.syncState();

            },



            onPlayerFoulClick(teamKey, playerId) {

              // 파울 선택 모드면 '선수 선택 확정'

              if (this.isFoulSelectMode && this.foulTargetTeam === teamKey) {

                this.confirmPlayerFoul(teamKey, playerId);

                return;

              }

              // 기본 모드: 기존처럼 개인 파울 +1

              this.addPlayerStat(teamKey, playerId, "fouls", 1);

            },

            undoLastScore() {

              if (!this.lastScoringPlayer) return;

              const { teamKey, playerId } = this.lastScoringPlayer;

              const list = this.players[teamKey];

              const p = list.find((x) => x.id === playerId);

              if (p) {

                p.points = Math.max(0, (p.points || 0) - 1);

                this.addTeamScore(teamKey, -1);

                this.lastScoringPlayer = null;

              }

            },

            confirmPlayerScore(teamKey, playerId) {
              if (!this.isPlayerSelectMode || this.scoreTargetTeam !== teamKey) return; // Added check for scoreTargetTeam

              const list = this.players[teamKey];
              const p = list.find((x) => x.id === playerId);
              if (!p) return;

              const points_to_add = this.pointsToAdd;
              this.isPlayerSelectMode = false;
              this.pointsToAdd = 0;
              this.scoreTargetTeam = null; // Reset score target after scoring

              // 1. Update local player points
              p.points = Math.max(0, (p.points || 0) + points_to_add);

              // 2. Update local team score
              if (teamKey === 'Home') {
                this.teams.Home.homeScore += points_to_add;
              } else {
                this.teams.Away.awayScore += points_to_add;
              }

              this.lastScoringPlayer = { teamKey, playerId, points: points_to_add };

              // 3. Sync everything
              this.syncState();
            },

            addPlayerStat(teamKey, playerId, field, delta) {

              const list = this.players[teamKey];

              const p = list.find((x) => x.id === playerId);

              if (!p) return;

                        p[field] = Math.max(0, (p[field] || 0) + delta);

                        if (field === 'fouls') {

                          this.recalculateTeamFouls(teamKey);

                        }

                        this.syncState();

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

              const selected = players.filter((p) => p.selected).slice(0, 5);

              this.players[team] = selected.map((p) => ({

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

              this.syncState();

            },

                        syncState() {



                          const fullState = {



                            quarter: this.quarter,



                            gameTime: this.gameClockSec,



                            shotClock: this.shotClockSec,



                            isGameRunning: this.isGameRunning,



                            isShotRunning: this.isShotRunning,



                            homeScore: this.teams.Home.homeScore,



                            homeFoul: this.teams.Home.homeFoul,



                            awayScore: this.teams.Away.awayScore,



                            awayFoul: this.teams.Away.awayFoul,



                            players: this.players,



                            rosterPlayers: this.rosterPlayers,



                            homeName: this.teams.Home.homeName,



                            awayName: this.teams.Away.awayName,



                          };



                          // 1. Persist locally and inform other tabs

                          publishState(fullState);



                          // 2. Inform the backend

                          this.pushState(ActionType.STATE_UPDATE, fullState);

                        },

                        recalculateTeamFouls(teamKey) {

                          const teamPlayers = this.players[teamKey] || [];

                          let totalFouls = 0;

                          for (const p of teamPlayers) {

                            totalFouls += (p.fouls || 0);

                          }

                          // Cap team fouls at 5

                          const cappedFouls = Math.min(5, totalFouls);



                          if (teamKey === 'Home') {

                            this.teams.Home.homeFoul = cappedFouls;

                          } else {

                            this.teams.Away.awayFoul = cappedFouls;

                          }

                        },

            toggleGameClock() {
      // ✅ UI 즉시 반응(정지/시작 토글) + 서버/WS는 동기화 용도
      const next = !this.isGameRunning;
      this.isGameRunning = next;
      this.pushState(ActionType.TOGGLE_GAME_TIME, { isRunning: next });
    },
    toggleShotClock() {
      // ✅ UI 즉시 반응(정지/시작 토글) + 서버/WS는 동기화 용도
      const next = !this.isShotRunning;
      this.isShotRunning = next;
      this.pushState(ActionType.TOGGLE_SHOT_CLOCK, { isRunning: next });
    },
    resetGameClock() {
      const payload = { isRunning: false, isReset: true, resetTime: this.strictGameTime };
      this.pushState(ActionType.SETTING_GAME_TIME, payload);
    },
    adjustGameClock(delta) {
      const payload = { isRunning: false, isReset: false, adjust: delta };
      this.pushState(ActionType.SETTING_GAME_TIME, payload);
    },
    resetShotClock() {
      const payload = { isRunning: false, isReset: true, isSetHalf: false };
      this.pushState(ActionType.SETTING_SHOT_CLOCK, payload);
    },
    setShotClock14() {
      const payload = { isRunning: false, isReset: false, isSetHalf: true };
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
      const payload = { isReset: true, resetTime: total };
      this.pushState(ActionType.SETTING_GAME_TIME, payload);
      this.closeTimeModal();
    },
    formatMMSS(sec) {
      const safe = Number(sec ?? 0);
      const m = Math.floor(safe / 60);
      const s = safe % 60;
      return `${String(m).padStart(2, "0")}:${String(s).padStart(2, "0")}`;
    },
    shutdownSession() {
      if (confirm("세션을 완전히 종료하시겠습니까? 디스플레이 화면도 꺼집니다.")) {
        disconnectWS();
      }
    },
  },
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
