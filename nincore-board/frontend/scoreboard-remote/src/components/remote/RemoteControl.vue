<template>
  <div class="rc-page">
    <div class="rc-3row">
      <section class="rc-row rc-row--time">
        <div class="rc-card rc-time-card">
          <div class="rc-time-header">
            <div class="rc-time-header__main">리모컨</div>
            <div class="rc-time-header__main">IP: {{ connectedIp }}</div>
            <div class="rc-time-header__main">비밀번호: {{ sessionPassword }}</div>
            <div>
              <button class="rc-btn rc-btn--ghost" @click="resetAll">리셋</button>
              <button class="rc-btn rc-btn--info" @click="openReportModal">기록</button>
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
                <button class="rc-time-click" @click="openTimeModal" :disabled="isGameRunning">
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
                  <button class="rc-btn rc-btn--mini" @click="changeQuarter(-1)" :disabled="quarter <= 1 || isGameRunning">-1</button>
                  <button class="rc-btn rc-btn--mini" @click="changeQuarter(1)" :disabled="isGameRunning">+1</button>
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
                  placeholder="Home"
                  maxlength="5"
                  v-model="homeName"
              />
              <div class="rc-team__scoretext">{{ teams.Home.homeScore }}</div>
            </div>

              <div class="rc-team__controls-row">
                <div>
                  <div class="rc-mini-title">득점</div>
                  <div class="rc-score-2rows">
                    <div class="rc-score-2rows__row">
                      <button class="rc-btn rc-btn--score-tiny" @click="startPlayerSelection('Home', 1)" :disabled="activePlayers.Home.length === 0">+1</button>
                      <button class="rc-btn rc-btn--score-tiny" @click="startPlayerSelection('Home', 2)" :disabled="activePlayers.Home.length === 0">+2</button>
                    </div>
                    <div class="rc-score-2rows__row">
                      <button class="rc-btn rc-btn--score-tiny" @click="startPlayerSelection('Home', 3)" :disabled="activePlayers.Home.length === 0">+3</button>
                      <button
                          class="rc-btn rc-btn--score-tiny rc-btn--ghost"
                          @click="startScoreUndoSelection('Home')"
                          :disabled="teams.Home.homeScore <= 0 || activePlayers.Home.length === 0"
                      >
                        -1
                      </button>
                    </div>
                  </div>
                </div>

                <div>

                  <div class="rc-team__meta-row">
                    <div class="rc-meta-label">팀 파울</div>
                    <div class="rc-meta-value">{{ teams.Home.homeFoul }}</div>
                  </div>


                  <div class="rc-btn-row rc-btn-row--stat-adjust">
                    <div class="rc-btn-row">
                      <button class="rc-btn rc-btn--mini"
                              @click="startStatUndoSelection('Home', 'fouls')"
                              :disabled="teams.Home.homeFoul <= 0">파울-1</button>
                      <button class="rc-btn rc-btn--mini"
                              @click="startStatUndoSelection('Home', 'assists')"
                              :disabled="teams.Home.homeAssists <= 0">어시-1</button>
                    </div>
                    <div class="rc-btn-row">
                      <button class="rc-btn rc-btn--mini"
                              @click="startStatUndoSelection('Home', 'rebounds')"
                              :disabled="teams.Home.homeRebounds <= 0">리바-1</button>
                      <button class="rc-btn rc-btn--mini"
                              @click="startStatUndoSelection('Home', 'steals')"
                              :disabled="teams.Home.homeSteals <= 0">스틸-1</button>
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
                  <button
                      v-if="(isPlayerSelectMode && scoreTargetTeam === 'Home') || (isScoreUndoSelectMode && scoreUndoTargetTeam === 'Home')"
                      class="rc-plus"
                      :class="{ 'blinking-effect': (isPlayerSelectMode && scoreTargetTeam === 'Home') || (isScoreUndoSelectMode && scoreUndoTargetTeam === 'Home') }"
                      @click="onPlayerScoreButtonClick('Home', p.id)"
                      :disabled="!((isPlayerSelectMode && scoreTargetTeam === 'Home') || (isScoreUndoSelectMode && scoreUndoTargetTeam === 'Home' && p.points > 0))"
                  >
                    {{ (isScoreUndoSelectMode && scoreUndoTargetTeam === 'Home') ? '-1' : '득점' }}
                  </button>
                </div>

                <div class="rc-statcell">
                  <div class="rc-num">{{ p.assists }}</div>
                  <button
                      class="rc-plus"
                      :class="{ 'blinking-effect': isStatUndoSelectMode && statUndoTargetTeam === 'Home' && statUndoField === 'assists' }"
                      @click="onPlayerStatButtonClick('Home', p.id, 'assists')"
                      :disabled="isStatUndoSelectMode && statUndoTargetTeam === 'Home' && statUndoField === 'assists' && p.assists <= 0"
                  >
                    {{ (isStatUndoSelectMode && statUndoTargetTeam === 'Home' && statUndoField === 'assists') ? '-' : '+' }}
                  </button>
                </div>

                <div class="rc-statcell">
                  <div class="rc-num">{{ p.rebounds }}</div>
                  <button
                      class="rc-plus"
                      :class="{ 'blinking-effect': isStatUndoSelectMode && statUndoTargetTeam === 'Home' && statUndoField === 'rebounds' }"
                      @click="onPlayerStatButtonClick('Home', p.id, 'rebounds')"
                      :disabled="isStatUndoSelectMode && statUndoTargetTeam === 'Home' && statUndoField === 'rebounds' && p.rebounds <= 0"
                  >
                    {{ (isStatUndoSelectMode && statUndoTargetTeam === 'Home' && statUndoField === 'rebounds') ? '-' : '+' }}
                  </button>
                </div>

                <div class="rc-statcell">
                  <div class="rc-num">{{ p.steals }}</div>
                  <button
                      class="rc-plus"
                      :class="{ 'blinking-effect': isStatUndoSelectMode && statUndoTargetTeam === 'Home' && statUndoField === 'steals' }"
                      @click="onPlayerStatButtonClick('Home', p.id, 'steals')"
                      :disabled="isStatUndoSelectMode && statUndoTargetTeam === 'Home' && statUndoField === 'steals' && p.steals <= 0"
                  >
                    {{ (isStatUndoSelectMode && statUndoTargetTeam === 'Home' && statUndoField === 'steals') ? '-' : '+' }}
                  </button>
                </div>

                <div class="rc-statcell">
                  <div class="rc-num">{{ p.fouls }}</div>
                  <button
                      class="rc-plus"
                      :class="{ 'blinking-effect': isStatUndoSelectMode && statUndoTargetTeam === 'Home' && statUndoField === 'fouls' }"
                      @click="onPlayerStatButtonClick('Home', p.id, 'fouls')"
                      :disabled="isStatUndoSelectMode && statUndoTargetTeam === 'Home' && statUndoField === 'fouls' && p.fouls <= 0"
                  >
                    {{ (isStatUndoSelectMode && statUndoTargetTeam === 'Home' && statUndoField === 'fouls') ? '-' : '+' }}
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
                  placeholder="Away"
                  maxlength="5"
                  v-model="awayName"
              />
              <div class="rc-team__scoretext">{{ teams.Away.awayScore }}</div>
            </div>

              <div class="rc-team__controls-row">
                <div>
                  <div class="rc-mini-title">득점</div>
                  <div class="rc-score-2rows">
                    <div class="rc-score-2rows__row">
                      <button class="rc-btn rc-btn--score-tiny" @click="startPlayerSelection('Away', 1)" :disabled="activePlayers.Away.length === 0">+1</button>
                      <button class="rc-btn rc-btn--score-tiny" @click="startPlayerSelection('Away', 2)" :disabled="activePlayers.Away.length === 0">+2</button>
                    </div>
                    <div class="rc-score-2rows__row">
                      <button class="rc-btn rc-btn--score-tiny" @click="startPlayerSelection('Away', 3)" :disabled="activePlayers.Away.length === 0">+3</button>
                      <button
                          class="rc-btn rc-btn--score-tiny rc-btn--ghost"
                          @click="startScoreUndoSelection('Away')"
                          :disabled="teams.Away.awayScore <= 0 || activePlayers.Away.length === 0"
                      >
                        -1
                      </button>
                    </div>
                  </div>
                </div>

                <div>
                  <div class="rc-team__meta-row">
                    <div class="rc-meta-label">팀 파울</div>
                    <div class="rc-meta-value">{{ teams.Away.awayFoul }}</div>
                  </div>


                  <div class="rc-btn-row rc-btn-row--stat-adjust">
                    <div class="rc-btn-row">
                      <button class="rc-btn rc-btn--mini"
                              @click="startStatUndoSelection('Away', 'fouls')"
                              :disabled="teams.Away.awayFoul <= 0">파울-1</button>
                      <button class="rc-btn rc-btn--mini"
                              @click="startStatUndoSelection('Away', 'assists')"
                              :disabled="teams.Away.awayAssists <= 0">어시-1</button>
                    </div>
                    <div class="rc-btn-row">
                      <button class="rc-btn rc-btn--mini"
                              @click="startStatUndoSelection('Away', 'rebounds')"
                              :disabled="teams.Away.awayRebounds <= 0">리바-1</button>
                      <button class="rc-btn rc-btn--mini"
                              @click="startStatUndoSelection('Away', 'steals')"
                              :disabled="teams.Away.awaySteals <= 0">스틸-1</button>
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
                  <button
                      v-if="(isPlayerSelectMode && scoreTargetTeam === 'Away') || (isScoreUndoSelectMode && scoreUndoTargetTeam === 'Away')"
                      class="rc-plus"
                      :class="{ 'blinking-effect': (isPlayerSelectMode && scoreTargetTeam === 'Away') || (isScoreUndoSelectMode && scoreUndoTargetTeam === 'Away') }"
                      @click="onPlayerScoreButtonClick('Away', p.id)"
                      :disabled="!((isPlayerSelectMode && scoreTargetTeam === 'Away') || (isScoreUndoSelectMode && scoreUndoTargetTeam === 'Away' && p.points > 0))"
                  >
                    {{ (isScoreUndoSelectMode && scoreUndoTargetTeam === 'Away') ? '-1' : '득점' }}
                  </button>
                </div>

                <div class="rc-statcell">
                  <div class="rc-num">{{ p.assists }}</div>
                  <button
                      class="rc-plus"
                      :class="{ 'blinking-effect': isStatUndoSelectMode && statUndoTargetTeam === 'Away' && statUndoField === 'assists' }"
                      @click="onPlayerStatButtonClick('Away', p.id, 'assists')"
                      :disabled="isStatUndoSelectMode && statUndoTargetTeam === 'Away' && statUndoField === 'assists' && p.assists <= 0"
                  >
                    {{ (isStatUndoSelectMode && statUndoTargetTeam === 'Away' && statUndoField === 'assists') ? '-' : '+' }}
                  </button>
                </div>

                <div class="rc-statcell">
                  <div class="rc-num">{{ p.rebounds }}</div>
                  <button
                      class="rc-plus"
                      :class="{ 'blinking-effect': isStatUndoSelectMode && statUndoTargetTeam === 'Away' && statUndoField === 'rebounds' }"
                      @click="onPlayerStatButtonClick('Away', p.id, 'rebounds')"
                      :disabled="isStatUndoSelectMode && statUndoTargetTeam === 'Away' && statUndoField === 'rebounds' && p.rebounds <= 0"
                  >
                    {{ (isStatUndoSelectMode && statUndoTargetTeam === 'Away' && statUndoField === 'rebounds') ? '-' : '+' }}
                  </button>
                </div>

                <div class="rc-statcell">
                  <div class="rc-num">{{ p.steals }}</div>
                  <button
                      class="rc-plus"
                      :class="{ 'blinking-effect': isStatUndoSelectMode && statUndoTargetTeam === 'Away' && statUndoField === 'steals' }"
                      @click="onPlayerStatButtonClick('Away', p.id, 'steals')"
                      :disabled="isStatUndoSelectMode && statUndoTargetTeam === 'Away' && statUndoField === 'steals' && p.steals <= 0"
                  >
                    {{ (isStatUndoSelectMode && statUndoTargetTeam === 'Away' && statUndoField === 'steals') ? '-' : '+' }}
                  </button>
                </div>

                <div class="rc-statcell">
                  <div class="rc-num">{{ p.fouls }}</div>
                  <button
                      class="rc-plus"
                      :class="{ 'blinking-effect': isStatUndoSelectMode && statUndoTargetTeam === 'Away' && statUndoField === 'fouls' }"
                      @click="onPlayerStatButtonClick('Away', p.id, 'fouls')"
                      :disabled="isStatUndoSelectMode && statUndoTargetTeam === 'Away' && statUndoField === 'fouls' && p.fouls <= 0"
                  >
                    {{ (isStatUndoSelectMode && statUndoTargetTeam === 'Away' && statUndoField === 'fouls') ? '-' : '+' }}
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

    <ReportModal
        v-if="isReportModalVisible"
        :gameState="currentGameState"
        @close="closeReportModal"
    />
  </div>
</template>

<script>
import { disconnectWS, sendCommand } from "@/shared/wsClient";
import "./remote-control.css";
import { ActionType } from "@/shared/actionTypes";
import { subscribeState, loadState, publishState, clearPersistedState } from "@/shared/stateChannel";
import {
  createScoreEvent,
  createScoreUndoEvent,
  createStatEvent,
  createStatUndoEvent,
  normalizeGameLog,
} from "@/shared/gameLogEvents";
import {
  createStateUpdatePayloadFromLocal,
  normalizeStateUpdatePayload,
} from "@/shared/stateUpdateSchema";
import RosterModal from "@/components/remote/RosterModal.vue";
import ReportModal from "@/components/report/ReportModal.vue";

const NINCORE_BOARD_STATE = "nincore-board-state";
const CHANNEL = "nincore-scoreboard";

// Simple debounce function
function debounce(func, delay) {
  let timeout;
  return function(...args) {
    const context = this;
    clearTimeout(timeout);
    timeout = setTimeout(() => func.apply(context, args), delay);
  };
}

function toNoOrMax(value) {
  const n = Number.parseInt(String(value ?? "").trim(), 10);
  return Number.isNaN(n) ? Number.MAX_SAFE_INTEGER : n;
}

function comparePlayerNoAsc(a, b) {
  const diff = toNoOrMax(a && a.no) - toNoOrMax(b && b.no);
  if (diff !== 0) return diff;
  const nameA = String((a && a.name) || "");
  const nameB = String((b && b.name) || "");
  return nameA.localeCompare(nameB, "ko");
}

function normalizeJerseyNo(value) {
  const digits = String(value ?? "").replace(/[^0-9]/g, "").slice(0, 2);
  if (!digits) return "";
  return String(Number.parseInt(digits, 10));
}

function normalizePlayerList(list) {
  if (!Array.isArray(list)) return [];
  return list.map((p) => ({ ...p, no: normalizeJerseyNo(p && p.no) }));
}

function normalizeTeamPlayersMap(map) {
  if (!map || typeof map !== "object") return { Home: [], Away: [] };
  return {
    Home: normalizePlayerList(map.Home),
    Away: normalizePlayerList(map.Away),
  };
}

function hasTeamPlayers(map) {
  if (!map || typeof map !== "object") return false;
  const home = Array.isArray(map.Home) ? map.Home.length : 0;
  const away = Array.isArray(map.Away) ? map.Away.length : 0;
  return home > 0 || away > 0;
}

const hardcodedPlayers = [
  { no: 1, name: "윤선민" },
  { no: 2, name: "이유지" },
  { no: 3, name: "임지수" },
  { no: 4, name: "이시연" },
  { no: 5, name: "김민정" },
  { no: 33, name: "한유정" },
  { no: 7, name: "한지행" },
  { no: 8, name: "이슬기" },
  { no: 11, name: "조세핀" },
  { no: 14, name: "최찬영" },
  { no: 15, name: "박민영" },
  { no: 66, name: "이담비" },
  { no: 91, name: "박구원" },
  { no: 0, name: "아코" },
  { no: 0o0, name: "홍신애" },
  { no: 99, name: "나선일" },
  { no: 9, name: "황선오" },
].map((p, index) => ({
  id: `player_${p.no}_${p.name}_${Math.random().toString(36).substr(2, 9)}`, // Generate a unique ID with random suffix
  no: p.no,
  name: p.name,
  selected: false, // No players selected by default
  points: 0,
  assists: 0,
  rebounds: 0,
  steals: 0,
  fouls: 0,
}));

export default {
  name: "RemoteControl",
  components: { RosterModal, ReportModal },
      data() {
      return {
        quarter: 1,
        teams: {
          Home: { homeName: "", homeScore: 0, homeFoul: 0, homeAssists: 0, homeRebounds: 0, homeSteals: 0 },
          Away: { awayName: "", awayScore: 0, awayFoul: 0, awayAssists: 0, awayRebounds: 0, awaySteals: 0 },
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
        scoreTargetTeam: null,
        isScoreUndoSelectMode: false,
        scoreUndoTargetTeam: null,
        isReportModalVisible: false,
        gameLog: [],
        everActivePlayerIds: { Home: new Set(), Away: new Set() },
        connectedIp: '',
        sessionPassword: '',
        debouncedSyncState: null, // Added debouncedSyncState
        debouncedChangeName: null, // Added debouncedChangeName
        isStatUndoSelectMode: false,
        statUndoTargetTeam: null,
        statUndoField: null,
        _bc: null,
        _onBCMessage: null,
        _onBeforeUnload: null,
      };
    },
    computed: {
      activePlayers() {      return {
        Home: (this.players.Home || []).slice().sort(comparePlayerNoAsc),
        Away: (this.players.Away || []).slice().sort(comparePlayerNoAsc),
      };
    },
    homeName: {
      get() {
        return this.teams.Home.homeName;
      },
      set(newValue) {
        this.teams.Home.homeName = newValue;
        this.debouncedChangeName('Home', newValue);
      }
    },
    awayName: {
      get() {
        return this.teams.Away.awayName;
      },
      set(newValue) {
        this.teams.Away.awayName = newValue;
        this.debouncedChangeName('Away', newValue);
      }
    },
    currentGameState() {
      const homePlayers = (this.players.Home || []).map(p => ({ ...p }));
      const awayPlayers = (this.players.Away || []).map(p => ({ ...p }));
      const log = this.gameLog.map(e => ({ ...e }));

      return {
        home: {
          name: this.teams.Home.homeName,
          score: this.teams.Home.homeScore,
          foul: this.teams.Home.homeFoul,
          players: homePlayers,
        },
        away: {
          name: this.teams.Away.awayName,
          score: this.teams.Away.awayScore,
          foul: this.teams.Away.awayFoul,
          players: awayPlayers,
        },
        quarter: this.quarter,
        gameClock: this.gameClockSec,
        shotClock: this.shotClockSec,
        gameLog: log,
        everActivePlayerIds: {
          Home: Array.from(this.everActivePlayerIds.Home),
          Away: Array.from(this.everActivePlayerIds.Away),
        },
        rosterPlayers: {
          Home: this.rosterPlayers.Home.map(p => ({ ...p })),
          Away: this.rosterPlayers.Away.map(p => ({ ...p })),
        }
      };
    },
  },
  created() {
    this.debouncedSyncState = debounce(this.syncState, 300); // 300ms debounce
    this.debouncedChangeName = debounce(this.changeName, 500); // Debounce team name changes for 500ms
  },
  mounted() {
    console.log(sessionStorage);
    this.unsubscribe = subscribeState(this.applyStateToView);
    const initialState = loadState(this.$route?.params?.sessionId);
    if (initialState) {
      this.applyStateToView(initialState);
    }
    this.connectedIp = sessionStorage.getItem("loginIp") || ((initialState && initialState.ip) || "");
    this.sessionPassword = sessionStorage.getItem("loginPassword") || ((initialState && initialState.password) || "");

    // Hardcode players if connectedIp is "번희수"
    if (this.connectedIp === '번희수') {
      this.$set(this.rosterPlayers, 'Home', hardcodedPlayers.map(p => ({ ...p })));
      this.$set(this.rosterPlayers, 'Away', hardcodedPlayers.map(p => ({ ...p })));
      this.$set(this.players, 'Home', hardcodedPlayers.filter(p => p.selected).map(p => ({ ...p })));
      this.$set(this.players, 'Away', hardcodedPlayers.filter(p => p.selected).map(p => ({ ...p })));
      this.syncState();
    }

    try {
      this._bc = new BroadcastChannel(CHANNEL);
      this._onBCMessage = (ev) => {
        if (!ev || !ev.data) return;
        if (ev.data.type !== "REQUEST_STATE") return;
        this.publishSnapshotForDisplay();
      };
      this._bc.addEventListener("message", this._onBCMessage);
    } catch (e) {}

    this._onBeforeUnload = () => {
      publishState(this.buildFullStatePayload());
    };
    window.addEventListener("beforeunload", this._onBeforeUnload);
  },
  beforeDestroy() {
    if (this.unsubscribe) {
      this.unsubscribe();
    }
    if (this._bc && this._onBCMessage) {
      this._bc.removeEventListener("message", this._onBCMessage);
      this._bc.close();
    }
    if (this._onBeforeUnload) {
      window.removeEventListener("beforeunload", this._onBeforeUnload);
    }
  },
  methods: {
    buildFullStatePayload() {
      return createStateUpdatePayloadFromLocal({
        quarter: this.quarter,
        gameTime: this.gameClockSec,
        shotClock: this.shotClockSec,
        isGameRunning: this.isGameRunning,
        isShotRunning: this.isShotRunning,
        homeScore: this.teams.Home.homeScore,
        homeFoul: this.teams.Home.homeFoul,
        homeAssists: this.teams.Home.homeAssists,
        homeRebounds: this.teams.Home.homeRebounds,
        homeSteals: this.teams.Home.homeSteals,
        awayScore: this.teams.Away.awayScore,
        awayFoul: this.teams.Away.awayFoul,
        awayAssists: this.teams.Away.awayAssists,
        awayRebounds: this.teams.Away.awayRebounds,
        awaySteals: this.teams.Away.awaySteals,
        players: this.players,
        rosterPlayers: this.rosterPlayers,
        homeName: this.teams.Home.homeName,
        awayName: this.teams.Away.awayName,
        gameLog: this.gameLog,
        everActivePlayerIds: {
          Home: Array.from(this.everActivePlayerIds.Home),
          Away: Array.from(this.everActivePlayerIds.Away),
        },
      });
    },
    publishSnapshotForDisplay() {
      const current = this.buildFullStatePayload();
      const hasCurrentData =
          (current.homeName && String(current.homeName).trim().length > 0) ||
          (current.awayName && String(current.awayName).trim().length > 0) ||
          (Array.isArray(current.players && current.players.Home) && current.players.Home.length > 0) ||
          (Array.isArray(current.players && current.players.Away) && current.players.Away.length > 0) ||
          (Array.isArray(current.rosterPlayers && current.rosterPlayers.Home) && current.rosterPlayers.Home.length > 0) ||
          (Array.isArray(current.rosterPlayers && current.rosterPlayers.Away) && current.rosterPlayers.Away.length > 0) ||
          Number(current.homeScore || 0) > 0 ||
          Number(current.awayScore || 0) > 0;

      if (hasCurrentData) {
        publishState(current);
        return;
      }

      const stored = loadState();
      if (stored) publishState(stored);
    },
    openReportModal() {
      this.isReportModalVisible = true;
    },
    closeReportModal() {
      this.isReportModalVisible = false;
    },
    applyStateToView(s) {
      if (!s) return;
      const currentSessionId = String(this.$route?.params?.sessionId || sessionStorage.getItem("sessionId") || "").trim();
      const incomingSessionId = String(s.sessionId || "").trim();
      if (currentSessionId && incomingSessionId && currentSessionId !== incomingSessionId) {
        return;
      }
      const state = normalizeStateUpdatePayload(s);
      if (typeof s.ip === "string" && s.ip.trim()) {
        this.connectedIp = s.ip.trim();
        sessionStorage.setItem("loginIp", this.connectedIp);
      }
      if (typeof s.password === "string" && s.password.trim()) {
        this.sessionPassword = s.password.trim();
        sessionStorage.setItem("loginPassword", this.sessionPassword);
      }

      if (typeof state.quarter === "number") this.quarter = state.quarter;
      if (typeof state.gameTime === "number") this.gameClockSec = state.gameTime;
      if (typeof state.shotClock === "number") this.shotClockSec = state.shotClock;

      // Prefer live server aliases first; persisted local fields can be stale.
      if (typeof s.gameRunning === "boolean") this.isGameRunning = s.gameRunning;
      else if (typeof s.isGameRunning === "boolean") this.isGameRunning = s.isGameRunning;
      else if (typeof s.gameIsRunning === "boolean") this.isGameRunning = s.gameIsRunning;
      else if (typeof s.isRunningGame === "boolean") this.isGameRunning = s.isRunningGame;
      else if (typeof state.isGameRunning === "boolean") this.isGameRunning = state.isGameRunning;

      if (typeof s.shotClockRunning === "boolean") this.isShotRunning = s.shotClockRunning;
      else if (typeof s.isShotClockRunning === "boolean") this.isShotRunning = s.isShotClockRunning;
      else if (typeof s.shotRunning === "boolean") this.isShotRunning = s.shotRunning;
      else if (typeof s.shotIsRunning === "boolean") this.isShotRunning = s.shotIsRunning;
      else if (typeof s.isRunningShot === "boolean") this.isShotRunning = s.isRunningShot;
      else if (typeof state.isShotRunning === "boolean") this.isShotRunning = state.isShotRunning;

      if (Array.isArray(state.gameLog)) this.gameLog = normalizeGameLog(state.gameLog);
      if (state.everActivePlayerIds) {
        const homeIds = Array.isArray(state.everActivePlayerIds.Home) ? state.everActivePlayerIds.Home : [];
        const awayIds = Array.isArray(state.everActivePlayerIds.Away) ? state.everActivePlayerIds.Away : [];
        this.everActivePlayerIds = {
          Home: new Set(homeIds),
          Away: new Set(awayIds),
        };
      }

      const inResetGuard = Date.now() < (this.resetGuardUntil || 0);
      if (!inResetGuard) {
        if (state.homeScore !== undefined) this.teams.Home.homeScore = state.homeScore;
        if (state.homeFoul !== undefined) this.teams.Home.homeFoul = state.homeFoul;
        if (state.awayScore !== undefined) this.teams.Away.awayScore = state.awayScore;
        if (state.awayFoul !== undefined) this.teams.Away.awayFoul = state.awayFoul;
      } else {
        if (state.homeName !== undefined) this.teams.Home.homeName = state.homeName;
        if (state.awayName !== undefined) this.teams.Away.awayName = state.awayName;
      }

      if (state.homeName !== undefined) this.teams.Home.homeName = state.homeName;
      if (state.awayName !== undefined) this.teams.Away.awayName = state.awayName;
      if (state.players) {
        if (state.players.Home || state.players.Away) {
          const incomingPlayers = normalizeTeamPlayersMap(state.players);
          if (hasTeamPlayers(incomingPlayers) || !hasTeamPlayers(this.players)) {
            this.players = incomingPlayers;
          }
        }
      }
      if (state.rosterPlayers) {
        if (state.rosterPlayers.Home || state.rosterPlayers.Away) {
          const incomingRoster = normalizeTeamPlayersMap(state.rosterPlayers);
          if (hasTeamPlayers(incomingRoster) || !hasTeamPlayers(this.rosterPlayers)) {
            this.rosterPlayers = incomingRoster;
          }
        }
      }
      if (state.homeAssists !== undefined) this.teams.Home.homeAssists = Number(state.homeAssists) || 0;
      if (state.homeRebounds !== undefined) this.teams.Home.homeRebounds = Number(state.homeRebounds) || 0;
      if (state.homeSteals !== undefined) this.teams.Home.homeSteals = Number(state.homeSteals) || 0;
      if (state.awayAssists !== undefined) this.teams.Away.awayAssists = Number(state.awayAssists) || 0;
      if (state.awayRebounds !== undefined) this.teams.Away.awayRebounds = Number(state.awayRebounds) || 0;
      if (state.awaySteals !== undefined) this.teams.Away.awaySteals = Number(state.awaySteals) || 0;

      this.recalculateTeamStats("Home");
      this.recalculateTeamStats("Away");
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
      this.pointsToAdd = 0;
      this.lastScoringPlayer = null;
      this.isScoreUndoSelectMode = false;
      this.scoreUndoTargetTeam = null;
      this.gameLog = [];
      
      this.isStatUndoSelectMode = false;
      this.statUndoTargetTeam = null;
      this.statUndoField = null;

      this.everActivePlayerIds = { Home: new Set(), Away: new Set() };

      this.gameClockSec = this.strictGameTime;
      this.shotClockSec = 24;

      this.teams.Home.homeScore = 0;
      this.teams.Home.homeFoul = 0;
      this.teams.Away.awayScore = 0;
      this.teams.Away.awayFoul = 0;

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

      if (ActionType.RESET_ALL) {
        this.pushState(ActionType.RESET_ALL, {});
      }

      if (ActionType.RESET_HOME) this.pushState(ActionType.RESET_HOME, {});
      if (ActionType.RESET_AWAY) this.pushState(ActionType.RESET_AWAY, {});

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
      if (ActionType.STATE_UPDATE) {
        this.pushState(
            ActionType.STATE_UPDATE,
            createStateUpdatePayloadFromLocal({
          quarter: 1,
          gameTime: this.strictGameTime,
          shotClock: 24,
          isGameRunning: false,
          isShotRunning: false,
          homeScore: 0,
          homeFoul: 0,
          homeAssists: 0,
          homeRebounds: 0,
          homeSteals: 0,
          awayScore: 0,
          awayFoul: 0,
          awayAssists: 0,
          awayRebounds: 0,
          awaySteals: 0,
          players: this.players,
          rosterPlayers: this.rosterPlayers,
          homeName: this.teams.Home.homeName,
          awayName: this.teams.Away.awayName,
          gameLog: this.gameLog,
          everActivePlayerIds: {
            Home: Array.from(this.everActivePlayerIds.Home),
            Away: Array.from(this.everActivePlayerIds.Away),
          },
            })
        );
      }
    },
    changeQuarter(delta) {
      if (this.isGameRunning) return;
      const nextQuarter = this.quarter + delta;
      if (nextQuarter < 1) return;
      this.pushState(ActionType.QUARTER, { quarter: nextQuarter });
    },
    changeName(teamKey, nextName) {
      let name = String(nextName ?? "").trim();
      const payload = {};
      if (teamKey === "Home") {
        payload.homeName = name;
      } else {
        payload.awayName = name;
      }
      const action = "STATE_UPDATE";
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
    addTeamStat(teamKey, field, delta) {
      const teamStatKey = teamKey.toLowerCase() + field; // e.g., 'homeAssists'
      this.teams[teamKey][teamStatKey] = Math.max(0, (this.teams[teamKey][teamStatKey] || 0) + delta);

      const payload = {};
      payload[teamStatKey] = this.teams[teamKey][teamStatKey]; // Send the new team total
      const action = "STATE_UPDATE"; // Or a more specific action if available for team stats

      this.pushState(action, payload);
    },
    startPlayerSelection(teamKey, points) {
      this.isScoreUndoSelectMode = false;
      this.scoreUndoTargetTeam = null;
      this.isStatUndoSelectMode = false;
      this.statUndoTargetTeam = null;
      this.statUndoField = null;

      this.isPlayerSelectMode = true;
      this.pointsToAdd = points;
      this.scoreTargetTeam = teamKey;
    },
    onPlayerFoulClick(teamKey, playerId) {
      this.addPlayerStat(teamKey, playerId, "fouls", 1);
    },
    markPlayerEverActive(teamKey, playerId) {
      if (!playerId) return;
      if (!this.everActivePlayerIds[teamKey]) {
        this.$set(this.everActivePlayerIds, teamKey, new Set());
      }
      this.everActivePlayerIds[teamKey].add(playerId);
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

        const lastScoreIndex = this.gameLog
            .slice()
            .reverse()
            .findIndex((e) => e.kind === "SCORE" && e.teamKey === teamKey);
        if (lastScoreIndex !== -1) {
          this.gameLog.splice(this.gameLog.length - 1 - lastScoreIndex, 1);
        }
      }
    },
    onPlayerScoreButtonClick(teamKey, playerId) {
      if (this.isPlayerSelectMode && this.scoreTargetTeam === teamKey) {
        this.confirmPlayerScore(teamKey, playerId);
      } else if (this.isScoreUndoSelectMode && this.scoreUndoTargetTeam === teamKey) {
        this.confirmScoreUndo(teamKey, playerId);
      }
    },
    confirmScoreUndo(teamKey, playerId) {
      const list = this.players[teamKey] || [];
      const p = list.find((x) => x.id === playerId);
      if (!p || p.points <= 0) return;

      p.points = (p.points || 0) - 1;

      if (teamKey === 'Home') {
        this.teams.Home.homeScore = Math.max(0, this.teams.Home.homeScore - 1);
      } else {
        this.teams.Away.awayScore = Math.max(0, this.teams.Away.awayScore - 1);
      }

      this.markPlayerEverActive(teamKey, playerId);
      this.gameLog.push(
          createScoreUndoEvent({
            quarter: this.quarter,
            teamKey,
            playerId,
            points: 1,
          })
      );

      this.isScoreUndoSelectMode = false;
      this.scoreUndoTargetTeam = null;
      this.debouncedSyncState();
    },
    startScoreUndoSelection(teamKey) {
      this.isPlayerSelectMode = false;
      this.pointsToAdd = 0;
      this.scoreTargetTeam = null;
      this.isStatUndoSelectMode = false;
      this.statUndoTargetTeam = null;
      this.statUndoField = null;

      this.isScoreUndoSelectMode = true;
      this.scoreUndoTargetTeam = teamKey;
    },
    confirmPlayerStatDecrement(teamKey, playerId, field) {
      // Ensure the stat is not already zero before decrementing
      const list = this.players[teamKey];
      const player = list.find(p => p.id === playerId);
      if (player && player[field] > 0) {
        this.addPlayerStat(teamKey, playerId, field, -1);
      }

      // Clear the stat decrement mode
      this.isStatUndoSelectMode = false;
      this.statUndoTargetTeam = null;
      this.statUndoField = null;
    },
    confirmPlayerScore(teamKey, playerId) {
      if (!this.isPlayerSelectMode || this.scoreTargetTeam !== teamKey) return;

      const list = this.players[teamKey];
      const p = list.find((x) => x.id === playerId);
      if (!p) return;

      const points_to_add = this.pointsToAdd;
      this.isPlayerSelectMode = false;
      this.pointsToAdd = 0;
      this.scoreTargetTeam = null;

      this.markPlayerEverActive(teamKey, playerId);
      this.gameLog.push(
          createScoreEvent({
            quarter: this.quarter,
            teamKey,
            playerId,
            points: points_to_add,
          })
      );

      p.points = Math.max(0, (p.points || 0) + points_to_add);

      if (teamKey === 'Home') {
        this.teams.Home.homeScore += points_to_add;
      } else {
        this.teams.Away.awayScore += points_to_add;
      }

      this.lastScoringPlayer = { teamKey, playerId, points: points_to_add };
      this.debouncedSyncState();
    },
    addPlayerStat(teamKey, playerId, field, delta) {
      const list = this.players[teamKey];
      const p = list.find((x) => x.id === playerId);
      if (!p) return;

      const oldPlayerStat = p[field] || 0;
      const newPlayerStat = Math.max(0, oldPlayerStat + delta);
      p[field] = newPlayerStat;
      this.markPlayerEverActive(teamKey, playerId);

      const actualDelta = newPlayerStat - oldPlayerStat;

      if (field === 'fouls') {
        this.recalculateTeamFouls(teamKey);
      } else if (['assists', 'rebounds', 'steals'].includes(field)) {
        const capitalizedField = field.charAt(0).toUpperCase() + field.slice(1);
        const teamStatKey = teamKey.toLowerCase() + capitalizedField;
        this.teams[teamKey][teamStatKey] = Math.max(0, (this.teams[teamKey][teamStatKey] || 0) + actualDelta);
      }
      
      const kindMap = {
        fouls: "FOUL",
        assists: "ASSIST",
        rebounds: "REBOUND",
        steals: "STEAL",
      };
      const statKind = kindMap[field];
      if (actualDelta !== 0 && statKind) {
        if (actualDelta > 0) {
          this.gameLog.push(
              createStatEvent({
                quarter: this.quarter,
                teamKey,
                playerId,
                kind: statKind,
                delta: actualDelta,
              })
          );
        } else {
          this.gameLog.push(
              createStatUndoEvent({
                quarter: this.quarter,
                teamKey,
                playerId,
                statKind,
                delta: Math.abs(actualDelta),
              })
          );
        }
      }

      this.debouncedSyncState();
    },
    startStatUndoSelection(teamKey, field) {
      this.isPlayerSelectMode = false;
      this.pointsToAdd = 0;
      this.scoreTargetTeam = null;
      this.isScoreUndoSelectMode = false;
      this.scoreUndoTargetTeam = null;
      
      this.isStatUndoSelectMode = true;
      this.statUndoTargetTeam = teamKey;
      this.statUndoField = field;
    },
    onPlayerStatButtonClick(teamKey, playerId, field) {
      if (this.isStatUndoSelectMode && this.statUndoTargetTeam === teamKey && this.statUndoField === field) {
        this.confirmStatUndo(teamKey, playerId, field);
      } else {
        this.addPlayerStat(teamKey, playerId, field, 1);
      }
    },
    confirmStatUndo(teamKey, playerId, field) {
      this.addPlayerStat(teamKey, playerId, field, -1);
      
      this.isStatUndoSelectMode = false;
      this.statUndoTargetTeam = null;
      this.statUndoField = null;
    },
    openRoster(teamKey) {
      if (teamKey !== "Home" && teamKey !== "Away") return;
      this.rosterModal.team = teamKey;
      this.rosterModal.open = true;
    },
    closeRoster() {
      this.rosterModal.open = false;
    },
    saveRoster({ team, players: newRosterInfoFromModal }) {
      const currentActivePlayers = this.players[team] || [];

      const statsMap = new Map();
      currentActivePlayers.forEach(p => {
        statsMap.set(p.id, {
          points: p.points,
          assists: p.assists,
          rebounds: p.rebounds,
          steals: p.steals,
          fouls: p.fouls,
        });
      });

      const newMasterRoster = newRosterInfoFromModal.map(playerFromModal => {
        const oldPlayerFromMaster = (this.rosterPlayers[team] || []).find(p => p.id === playerFromModal.id);
        const latestStats = statsMap.get(playerFromModal.id);

        const defaultStats = {
          points: 0,
          assists: 0,
          rebounds: 0,
          steals: 0,
          fouls: 0
        };

        // Ensure critical player properties from modal are always present
        const safePlayerFromModal = {
            id: playerFromModal.id,
            no: normalizeJerseyNo(playerFromModal.no),
            name: playerFromModal.name || '', // Default to empty string
            selected: playerFromModal.selected || false, // Default to false
        };

        return {
          ...defaultStats,
          ...(oldPlayerFromMaster || {}),
          ...safePlayerFromModal,
          ...(latestStats || {}),
        };
      });

      const sortedMasterRoster = newMasterRoster.slice().sort(comparePlayerNoAsc);
      this.$set(this.rosterPlayers, team, sortedMasterRoster);
      this.$set(this.players, team, sortedMasterRoster.filter(p => p.selected).slice(0, 5).sort(comparePlayerNoAsc));

      // Add newly active players to the set of all-time active players for the report
      this.players[team].forEach(p => this.everActivePlayerIds[team].add(p.id));
      this.recalculateTeamStats(team);

      this.syncState();
      this.closeRoster();
    },
    syncState() {
      const fullState = this.buildFullStatePayload();
      publishState(fullState);
      const serverSyncPayload = {
        homeScore: fullState.homeScore,
        homeFoul: fullState.homeFoul,
        homeAssists: fullState.homeAssists,
        homeRebounds: fullState.homeRebounds,
        homeSteals: fullState.homeSteals,
        awayScore: fullState.awayScore,
        awayFoul: fullState.awayFoul,
        awayAssists: fullState.awayAssists,
        awayRebounds: fullState.awayRebounds,
        awaySteals: fullState.awaySteals,
        players: fullState.players,
        rosterPlayers: fullState.rosterPlayers,
        homeName: fullState.homeName,
        awayName: fullState.awayName,
        gameLog: fullState.gameLog,
        everActivePlayerIds: fullState.everActivePlayerIds,
      };
      this.pushState(ActionType.STATE_UPDATE, serverSyncPayload);
    },
    recalculateTeamFouls(teamKey) {
      this.recalculateTeamStats(teamKey);
    },
    recalculateTeamStats(teamKey) {
      const teamPlayers = this.players[teamKey] || [];
      const prefix = teamKey.toLowerCase();

      const sum = (field) => teamPlayers.reduce((acc, p) => acc + (Number(p[field]) || 0), 0);
      this.teams[teamKey][`${prefix}Foul`] = sum("fouls");
      this.teams[teamKey][`${prefix}Assists`] = sum("assists");
      this.teams[teamKey][`${prefix}Rebounds`] = sum("rebounds");
      this.teams[teamKey][`${prefix}Steals`] = sum("steals");
    },
    toggleGameClock() {
      const next = !this.isGameRunning;
      this.isGameRunning = next;
      this.pushState(ActionType.TOGGLE_GAME_TIME, { isRunning: next });
    },
    toggleShotClock() {
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
      this.gameClockSec = this.strictGameTime;
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
    // ─── 세션 종료 ─────────────────────────────────────────────────
    shutdownSession() {
      if (confirm("게임을 종료하시겠습니까?")) {
        const sessionId = sessionStorage.getItem("sessionId");
        if (!sessionId) return;

        fetch("/api/shutdown", {
          method: "POST",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify({ sessionId: sessionId })
        })
            .then(response => {
              // 서버 응답과 무관하게 종료 신호를 보내고 이동
              this.broadcastShutdown();
              disconnectWS();
              sessionStorage.clear();
              clearPersistedState();
              window.location.href = "/";
            })
            .catch(error => {
              console.error("Error during session shutdown:", error);
              // 서버 통신 실패해도 클라이언트 종료 진행
              this.broadcastShutdown();
              disconnectWS();
              sessionStorage.clear();
              clearPersistedState();
              window.location.href = "/";
            });
      }
    },
    // Display에 종료 신호를 전달
    broadcastShutdown() {
      try {
        const bc = new BroadcastChannel("nincore-scoreboard");
        bc.postMessage({ type: "SHUTDOWN" });
        bc.close();
      } catch (e) {
        console.warn("BroadcastChannel shutdown signal failed:", e);
      }
    },
  },
};
</script>

<style scoped>
.blinking-effect {
  animation: blink 1s ease-in-out infinite;
  box-shadow: 0 0 8px 2px rgba(255, 215, 0, 0.8);
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
