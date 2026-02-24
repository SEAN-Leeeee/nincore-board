<template>
  <div class="game-report-container">
    <div class="report-header">
      <h1>Nincore Basketball Game Report</h1>
      <button @click="printReport" class="print-button">Print Report</button>
    </div>

    <div class="game-details">
      <div class="team-info">
        <span class="team-name home">{{ gameState.home.name }}</span>
        <span class="final-score">{{ totalScore.home }} - {{ totalScore.away }}</span>
        <span class="team-name away">{{ gameState.away.name }}</span>
      </div>
      <div class="game-meta">
        <span><strong>Date:</strong> {{ new Date().toLocaleDateString() }}</span>
      </div>
    </div>

    <div class="team-stats-container">
      <!-- Home Team Stats -->
      <div class="team-sheet">
        <h2>{{ gameState.home.name }} (HOME)</h2>
        <table class="player-stats-table">
          <thead>
            <tr>
              <th>#</th>
              <th>Player Name</th>
              <th>PTS</th>
              <th>FLS</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="player in homePlayers" :key="player.id">
              <td>{{ player.jerseyNumber }}</td>
              <td>{{ player.name }}</td>
              <td>{{ player.points }}</td>
              <td>{{ player.fouls }}</td>
            </tr>
          </tbody>
        </table>
        <div class="team-summary">
          <p><strong>Team Fouls:</strong> {{ gameState.home.foul }}</p>
          <p><strong>Timeouts Left:</strong> {{ gameState.home.timeout }}</p>
        </div>
      </div>

      <!-- Away Team Stats -->
      <div class="team-sheet">
        <h2>{{ gameState.away.name }} (AWAY)</h2>
        <table class="player-stats-table">
          <thead>
            <tr>
              <th>#</th>
              <th>Player Name</th>
              <th>PTS</th>
              <th>FLS</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="player in awayPlayers" :key="player.id">
              <td>{{ player.jerseyNumber }}</td>
              <td>{{ player.name }}</td>
              <td>{{ player.points }}</td>
              <td>{{ player.fouls }}</td>
            </tr>
          </tbody>
        </table>
        <div class="team-summary">
          <p><strong>Team Fouls:</strong> {{ gameState.away.foul }}</p>
          <p><strong>Timeouts Left:</strong> {{ gameState.away.timeout }}</p>
        </div>
      </div>
    </div>

    <div class="footer">
      <p>Nincore Board - Official Game Record</p>
    </div>
  </div>
</template>

<script>
export default {
  name: "GameReport",
  props: {
    gameState: {
      type: Object,
      required: true,
      default: () => ({
        home: { name: 'Home Team', foul: 0, timeout: 0, players: [] },
        away: { name: 'Away Team', foul: 0, timeout: 0, players: [] },
      })
    }
  },
  computed: {
    homePlayers() {
      // Provides a stable list for rendering, even if the prop is initially empty
      return this.gameState.home.players || [];
    },
    awayPlayers() {
      return this.gameState.away.players || [];
    },
    totalScore() {
      const homeScore = this.homePlayers.reduce((acc, player) => acc + player.points, 0);
      const awayScore = this.awayPlayers.reduce((acc, player) => acc + player.points, 0);
      return { home: homeScore, away: awayScore };
    }
  },
  methods: {
    printReport() {
      window.print();
    }
  }
};
</script>

<style scoped src="./game-report.css"></style>
