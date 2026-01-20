<template>
  <div class="game-report-container">
    <div class="report-header">
      <h1>즐농 기록지</h1>
      <button @click="downloadPdf" class="print-button">PDF 다운로드</button>
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
        <h2>{{ gameState.home.name }}</h2>
        <table class="player-stats-table">
          <thead>
            <tr>
              <th>No.</th>
              <th>Player</th>
              <th>PTS</th>
              <th>AST</th>
              <th>REB</th>
              <th>STL</th>
              <th>FLS</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="player in homePlayers" :key="player.id">
              <td>{{ player.no }}</td>
              <td>{{ player.name }}</td>
              <td>{{ player.points }}</td>
              <td>{{ player.assists }}</td>
              <td>{{ player.rebounds }}</td>
              <td>{{ player.steals }}</td>
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
        <h2>{{ gameState.away.name }}</h2>
        <table class="player-stats-table">
          <thead>
            <tr>
              <th>No.</th>
              <th>Player</th>
              <th>PTS</th>
              <th>AST</th>
              <th>REB</th>
              <th>STL</th>
              <th>FLS</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="player in awayPlayers" :key="player.id">
              <td>{{ player.no }}</td>
              <td>{{ player.name }}</td>
              <td>{{ player.points }}</td>
              <td>{{ player.assists }}</td>
              <td>{{ player.rebounds }}</td>
              <td>{{ player.steals }}</td>
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

    <div class="quarterly-summary">
        <h2>쿼터별 요약</h2>
        <table class="player-stats-table">
            <thead>
                <tr>
                    <th>Team</th>
                    <th v-for="q in allQuarters" :key="q">{{ q > 4 ? `OT${q - 4}` : `${q}Q` }}</th>
                </tr>
            </thead>
            <tbody>
                <tr>
                    <td>{{ gameState.home.name }} Score</td>
                    <td v-for="q in allQuarters" :key="`home-score-${q}`">{{ quarterlyStats[q]?.home.score || 0 }}</td>
                </tr>
                <tr>
                    <td>{{ gameState.home.name }} Fouls</td>
                    <td v-for="q in allQuarters" :key="`home-foul-${q}`">{{ quarterlyStats[q]?.home.foul || 0 }}</td>
                </tr>
                <tr>
                    <td>{{ gameState.away.name }} Score</td>
                    <td v-for="q in allQuarters" :key="`away-score-${q}`">{{ quarterlyStats[q]?.away.score || 0 }}</td>
                </tr>
                <tr>
                    <td>{{ gameState.away.name }} Fouls</td>
                    <td v-for="q in allQuarters" :key="`away-foul-${q}`">{{ quarterlyStats[q]?.away.foul || 0 }}</td>
                </tr>
            </tbody>
        </table>
    </div>
    <div class="footer">
      <p>Nincore Board - Official Game Record</p>
    </div>
  </div>
</template>

<script>
import html2canvas from 'html2canvas';
import jsPDF from 'jspdf';

export default {
  name: "GameReport",
  props: {
    gameState: {
      type: Object,
      required: true,
      default: () => ({
        home: { name: 'Home Team', foul: 0, timeout: 0, players: [] },
        away: { name: 'Away Team', foul: 0, timeout: 0, players: [] },
        gameLog: [],
      })
    }
  },
  computed: {
    homePlayers() {
      return (this.gameState.home.players || []).slice().sort((a, b) => (a.no || 0) - (b.no || 0));
    },
    awayPlayers() {
      return (this.gameState.away.players || []).slice().sort((a, b) => (a.no || 0) - (b.no || 0));
    },
    totalScore() {
      const homeScore = this.homePlayers.reduce((acc, player) => acc + player.points, 0);
      const awayScore = this.awayPlayers.reduce((acc, player) => acc + player.points, 0);
      return { home: homeScore, away: awayScore };
    },
    allQuarters() {
      const quartersInLog = new Set(this.gameState.gameLog.map(e => e.quarter));
      const maxQuarter = Math.max(1, this.gameState.quarter, ...quartersInLog); // Ensure at least 1, and consider current quarter
      
      const allQuarters = new Set();
      for (let i = 1; i <= maxQuarter; i++) {
        allQuarters.add(i);
      }
      quartersInLog.forEach(q => allQuarters.add(q));

      return Array.from(allQuarters).sort((a, b) => a - b);
    },
    quarterlyStats() {
      const stats = {};

      if (!this.gameState.gameLog) {
        return stats;
      }

      // Initialize stats for all quarters
      this.allQuarters.forEach(q => {
        stats[q] = {
          home: { score: 0, foul: 0 },
          away: { score: 0, foul: 0 },
        };
      });

      this.gameState.gameLog.forEach(event => {
        const { quarter, type, team, points } = event;
        if (!stats[quarter]) return; // Should not happen due to initialization

        const teamKey = team.toLowerCase(); // 'Home' -> 'home', 'Away' -> 'away'

        if (type === 'SCORE') {
          stats[quarter][teamKey].score += points;
        } else if (type === 'FOUL') {
          stats[quarter][teamKey].foul += 1;
        }
      });
      return stats;
    }
  },
  methods: {
    async downloadPdf() {
      const reportElement = this.$el; // The root element of the component
      
      // Temporarily hide the print button for the screenshot
      const printButton = reportElement.querySelector('.print-button');
      if (printButton) printButton.style.display = 'none';

      try {
        const targetCaptureWidthPx = 780; // Example target pixel width for capture, adjust as needed.
        
        const canvas = await html2canvas(reportElement, {
            scale: 2, // Higher scale for better resolution
            useCORS: true, // Important for images, fonts
            logging: true, // Enable logging for debugging html2canvas
            width: targetCaptureWidthPx, // Force capture width
            windowWidth: targetCaptureWidthPx // Ensure window context matches capture width
        });

        const imgData = canvas.toDataURL('image/png');
        const pdf = new jsPDF('p', 'mm', 'a4');

        const pdfWidth = pdf.internal.pageSize.getWidth(); // A4 width in mm (210mm)
        const pdfHeight = pdf.internal.pageSize.getHeight(); // A4 height in mm (297mm)

        const imgCanvasWidth = canvas.width; // Actual width of the generated canvas (targetCaptureWidthPx * scale)
        const imgCanvasHeight = canvas.height; // Actual height of the generated canvas

        const aspectRatio = imgCanvasWidth / imgCanvasHeight;

        // Desired content width in PDF, leaving margins
        const targetPdfContentWidth = 190; // mm, e.g., 210mm - 2*10mm margin
        const marginX = (pdfWidth - targetPdfContentWidth) / 2; // Center horizontally
        const marginY = 10; // Top/bottom margin in mm

        let imgDisplayWidth = targetPdfContentWidth;
        let imgDisplayHeight = imgDisplayWidth / aspectRatio; // Height scaled to targetPdfContentWidth

        let heightRendered = 0; // Total height of the image that has been rendered in mm
        let pageNumber = 0;

        while (heightRendered < imgDisplayHeight) {
            if (pageNumber > 0) {
                pdf.addPage();
            }

            // Calculate the Y position to draw the image on the current page.
            // It's a negative offset of what's already rendered, plus the top margin for each new page.
            const yPositionOnPage = -heightRendered + marginY; 

            pdf.addImage(
                imgData,
                'PNG',
                marginX,
                yPositionOnPage,
                imgDisplayWidth,
                imgDisplayHeight
            );
            
            // Advance the heightRendered by the usable height of a PDF page
            heightRendered += (pdfHeight - (2 * marginY)); // Usable page height for content
            pageNumber++;
        }
        
        // --- Blob-based download workaround ---
        const pdfBlob = pdf.output('blob');
        const pdfUrl = URL.createObjectURL(pdfBlob);

        const a = document.createElement('a');
        a.href = pdfUrl;
        a.download = 'game_report.pdf';
        document.body.appendChild(a);
        a.click();
        document.body.removeChild(a);
        URL.revokeObjectURL(pdfUrl); // Clean up the object URL
        // --- End workaround ---

      } catch (error) {
        console.error('Error generating PDF:', error);
        alert('PDF 생성 중 오류가 발생했습니다. 자세한 내용은 콘솔을 확인해주세요.');
      } finally {
        // Restore the print button visibility
        if (printButton) printButton.style.display = '';
      }
    }
  }
};
</script>

<style scoped src="./game-report.css"></style>
