<template>
  <div class="game-report-container">
    <div class="report-header">
      <h1>경기 기록지</h1>
      <button @click="downloadPdf" class="print-button">PDF 다운로드</button>
    </div>

    <section class="game-basic-info">
      <div><strong>날짜:</strong> {{ dateText }}</div>
      <div><strong>매치업:</strong> {{ homeTeamName }} : {{ awayTeamName }}</div>
    </section>

    <section class="scoreboard-section">
      <h2>쿼터별 스코어보드</h2>
      <table class="report-table">
        <thead>
          <tr>
            <th>Team</th>
            <th v-for="q in allQuarters" :key="`score-h-${q}`">{{ quarterLabel(q) }}</th>
            <th>Total</th>
            <th>Foul</th>
          </tr>
        </thead>
        <tbody>
          <tr>
            <td>{{ homeTeamName }}</td>
            <td v-for="q in allQuarters" :key="`score-home-${q}`">{{ teamQuarterScore('Home', q) }}</td>
            <td>{{ teamTotalScore('Home') }}</td>
            <td>{{ teamTotalFoul('Home') }}</td>
          </tr>
          <tr>
            <td>{{ awayTeamName }}</td>
            <td v-for="q in allQuarters" :key="`score-away-${q}`">{{ teamQuarterScore('Away', q) }}</td>
            <td>{{ teamTotalScore('Away') }}</td>
            <td>{{ teamTotalFoul('Away') }}</td>
          </tr>
        </tbody>
      </table>
    </section>

    <section class="players-section">
      <h2>개인별 기록</h2>

      <div class="team-block" v-for="teamKey in ['Home', 'Away']" :key="`team-${teamKey}`">
        <h3>{{ teamDisplayName(teamKey) }}</h3>

        <div class="quarter-block" v-for="q in allQuarters" :key="`team-${teamKey}-q-${q}`">
          <h4>{{ quarterLabel(q) }}</h4>
          <table class="report-table">
            <thead>
              <tr>
                <th>No</th>
                <th>이름</th>
                <th>득점</th>
                <th>파울</th>
                <th>어시</th>
                <th>스틸</th>
                <th>리바</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="player in rosterByTeam[teamKey]" :key="`${teamKey}-${q}-${player.id || player.no || player.name}`">
                <td>{{ displayNo(player.no) }}</td>
                <td>{{ player.name || '-' }}</td>
                <td>{{ playerStat(teamKey, q, player.id, 'points') }}</td>
                <td>{{ playerStat(teamKey, q, player.id, 'fouls') }}</td>
                <td>{{ playerStat(teamKey, q, player.id, 'assists') }}</td>
                <td>{{ playerStat(teamKey, q, player.id, 'steals') }}</td>
                <td>{{ playerStat(teamKey, q, player.id, 'rebounds') }}</td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
    </section>
  </div>
</template>

<script>
import html2canvas from 'html2canvas';
import jsPDF from 'jspdf';
import { normalizeGameLog } from '@/shared/gameLogEvents';

function toNoOrMax(value) {
  const n = Number.parseInt(String(value ?? '').trim(), 10);
  return Number.isNaN(n) ? Number.MAX_SAFE_INTEGER : n;
}

function comparePlayerNoAsc(a, b) {
  const diff = toNoOrMax(a && a.no) - toNoOrMax(b && b.no);
  if (diff !== 0) return diff;
  const nameA = String((a && a.name) || '');
  const nameB = String((b && b.name) || '');
  return nameA.localeCompare(nameB, 'ko');
}

function makeTeamQuarterAccumulator() {
  return {
    score: 0,
    foul: 0,
  };
}

function makePlayerQuarterAccumulator() {
  return {
    points: 0,
    fouls: 0,
    assists: 0,
    steals: 0,
    rebounds: 0,
  };
}

export default {
  name: 'GameReport',
  props: {
    gameState: {
      type: Object,
      required: true,
      default: () => ({
        home: { name: 'Home Team', score: 0, foul: 0 },
        away: { name: 'Away Team', score: 0, foul: 0 },
        quarter: 1,
        gameLog: [],
        rosterPlayers: { Home: [], Away: [] },
      }),
    },
  },
  computed: {
    dateText() {
      return new Date().toLocaleDateString();
    },
    homeTeamName() {
      return String((this.gameState.home && this.gameState.home.name) || 'Home').trim() || 'Home';
    },
    awayTeamName() {
      return String((this.gameState.away && this.gameState.away.name) || 'Away').trim() || 'Away';
    },
    normalizedGameLog() {
      return normalizeGameLog(this.gameState.gameLog);
    },
    allQuarters() {
      const quartersInLog = new Set(
        this.normalizedGameLog
          .map((e) => Number(e.quarter) || 1)
          .filter((q) => q > 0)
      );

      const maxQuarter = Math.max(4, Number(this.gameState.quarter) || 1, ...quartersInLog);
      const out = [];
      for (let i = 1; i <= maxQuarter; i += 1) out.push(i);
      return out;
    },
    rosterByTeam() {
      const homeRoster = Array.isArray(this.gameState.rosterPlayers && this.gameState.rosterPlayers.Home)
        ? this.gameState.rosterPlayers.Home
        : [];
      const awayRoster = Array.isArray(this.gameState.rosterPlayers && this.gameState.rosterPlayers.Away)
        ? this.gameState.rosterPlayers.Away
        : [];

      return {
        Home: homeRoster.slice().sort(comparePlayerNoAsc),
        Away: awayRoster.slice().sort(comparePlayerNoAsc),
      };
    },
    teamQuarterStats() {
      const stats = {
        Home: {},
        Away: {},
      };

      this.allQuarters.forEach((q) => {
        stats.Home[q] = makeTeamQuarterAccumulator();
        stats.Away[q] = makeTeamQuarterAccumulator();
      });

      this.normalizedGameLog.forEach((event) => {
        const q = Number(event.quarter) || 1;
        const teamKey = event.teamKey === 'Away' ? 'Away' : 'Home';
        if (!stats[teamKey][q]) stats[teamKey][q] = makeTeamQuarterAccumulator();

        const payload = event.payload || {};
        if (event.kind === 'SCORE' || event.kind === 'SCORE_UNDO') {
          stats[teamKey][q].score += Number(payload.delta) || 0;
        } else if (event.kind === 'FOUL') {
          stats[teamKey][q].foul += Number(payload.delta) || 0;
        } else if (event.kind === 'STAT_UNDO' && payload.statKind === 'FOUL') {
          stats[teamKey][q].foul += Number(payload.delta) || 0;
        }
      });

      return stats;
    },
    playerQuarterStats() {
      const stats = {
        Home: {},
        Away: {},
      };

      this.allQuarters.forEach((q) => {
        stats.Home[q] = {};
        stats.Away[q] = {};
      });

      const ensurePlayer = (teamKey, quarter, playerId) => {
        if (!stats[teamKey][quarter]) stats[teamKey][quarter] = {};
        if (!stats[teamKey][quarter][playerId]) {
          stats[teamKey][quarter][playerId] = makePlayerQuarterAccumulator();
        }
        return stats[teamKey][quarter][playerId];
      };

      this.normalizedGameLog.forEach((event) => {
        const q = Number(event.quarter) || 1;
        const teamKey = event.teamKey === 'Away' ? 'Away' : 'Home';
        const playerId = event.playerId;
        if (!playerId) return;

        const bucket = ensurePlayer(teamKey, q, playerId);
        const payload = event.payload || {};
        const delta = Number(payload.delta) || 0;

        if (event.kind === 'SCORE' || event.kind === 'SCORE_UNDO') {
          bucket.points += delta;
        } else if (event.kind === 'FOUL') {
          bucket.fouls += delta;
        } else if (event.kind === 'ASSIST') {
          bucket.assists += delta;
        } else if (event.kind === 'STEAL') {
          bucket.steals += delta;
        } else if (event.kind === 'REBOUND') {
          bucket.rebounds += delta;
        } else if (event.kind === 'STAT_UNDO') {
          if (payload.statKind === 'FOUL') bucket.fouls += delta;
          if (payload.statKind === 'ASSIST') bucket.assists += delta;
          if (payload.statKind === 'STEAL') bucket.steals += delta;
          if (payload.statKind === 'REBOUND') bucket.rebounds += delta;
        }
      });

      return stats;
    },
  },
  methods: {
    quarterLabel(q) {
      return q <= 4 ? `${q}Q` : `${q}Q`;
    },
    teamDisplayName(teamKey) {
      return teamKey === 'Home' ? this.homeTeamName : this.awayTeamName;
    },
    displayNo(no) {
      const n = Number.parseInt(String(no ?? '').trim(), 10);
      return Number.isNaN(n) ? '-' : n;
    },
    teamQuarterScore(teamKey, q) {
      const value = this.teamQuarterStats[teamKey] && this.teamQuarterStats[teamKey][q]
        ? this.teamQuarterStats[teamKey][q].score
        : 0;
      return Number(value) || 0;
    },
    teamTotalScore(teamKey) {
      const sumFromLog = this.allQuarters.reduce((acc, q) => acc + this.teamQuarterScore(teamKey, q), 0);
      if (sumFromLog !== 0) return sumFromLog;
      return teamKey === 'Home'
        ? Number((this.gameState.home && this.gameState.home.score) || 0)
        : Number((this.gameState.away && this.gameState.away.score) || 0);
    },
    teamTotalFoul(teamKey) {
      const sumFromLog = this.allQuarters.reduce((acc, q) => {
        const foul = this.teamQuarterStats[teamKey] && this.teamQuarterStats[teamKey][q]
          ? this.teamQuarterStats[teamKey][q].foul
          : 0;
        return acc + (Number(foul) || 0);
      }, 0);
      if (sumFromLog !== 0) return sumFromLog;
      return teamKey === 'Home'
        ? Number((this.gameState.home && this.gameState.home.foul) || 0)
        : Number((this.gameState.away && this.gameState.away.foul) || 0);
    },
    playerStat(teamKey, quarter, playerId, field) {
      const team = this.playerQuarterStats[teamKey] || {};
      const q = team[quarter] || {};
      const stat = q[playerId] || makePlayerQuarterAccumulator();
      return Number(stat[field]) || 0;
    },
    async downloadPdf() {
      const reportElement = this.$el;
      const printButton = reportElement.querySelector('.print-button');
      if (printButton) printButton.style.display = 'none';

      try {
        const targetCaptureWidthPx = 780;

        const canvas = await html2canvas(reportElement, {
          scale: 2,
          useCORS: true,
          logging: true,
          width: targetCaptureWidthPx,
          windowWidth: targetCaptureWidthPx,
        });

        const imgData = canvas.toDataURL('image/png');
        const pdf = new jsPDF('p', 'mm', 'a4');

        const pdfWidth = pdf.internal.pageSize.getWidth();
        const pdfHeight = pdf.internal.pageSize.getHeight();

        const imgCanvasWidth = canvas.width;
        const imgCanvasHeight = canvas.height;
        const aspectRatio = imgCanvasWidth / imgCanvasHeight;

        const targetPdfContentWidth = 190;
        const marginX = (pdfWidth - targetPdfContentWidth) / 2;
        const marginY = 10;

        let imgDisplayWidth = targetPdfContentWidth;
        let imgDisplayHeight = imgDisplayWidth / aspectRatio;

        let heightRendered = 0;
        let pageNumber = 0;

        while (heightRendered < imgDisplayHeight) {
          if (pageNumber > 0) {
            pdf.addPage();
          }

          const yPositionOnPage = -heightRendered + marginY;

          pdf.addImage(
            imgData,
            'PNG',
            marginX,
            yPositionOnPage,
            imgDisplayWidth,
            imgDisplayHeight
          );

          heightRendered += (pdfHeight - (2 * marginY));
          pageNumber += 1;
        }

        pdf.save(`game-report-${new Date().toISOString().slice(0, 10)}.pdf`);
      } catch (error) {
        console.error('Error generating PDF:', error);
        alert('PDF 생성 중 오류가 발생했습니다.');
      } finally {
        if (printButton) printButton.style.display = '';
      }
    },
  },
};
</script>

<style scoped src="./game-report.css"></style>
