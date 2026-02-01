<template>
  <div class="report-modal-overlay" @click.self="close">
    <div class="report-modal-panel">
      <div class="report-modal-header">
        <button class="report-modal-close-btn" @click="close">×</button>
      </div>
      <div class="report-modal-body">
        <GameReport v-if="gameState" :gameState="gameState" />
      </div>
    </div>
  </div>
</template>

<script>
import GameReport from './GameReport.vue';

export default {
  name: 'ReportModal',
  components: {
    GameReport,
  },
  props: {
    gameState: {
      type: Object,
      required: true,
    },
  },
  mounted() {
    alert(this.gameState.home.players[0].points);

  },
  methods: {
    close() {
      this.$emit('close');
    },
  },
};
</script>

<style scoped>
.report-modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.6);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.report-modal-panel {
  width: 90%;
  max-width: 840px;
  max-height: 90vh;
  background-color: #fff;
  border-radius: 8px;
  display: flex;
  flex-direction: column;
}

.report-modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 1rem 1.5rem;
  border-bottom: 1px solid #e0e0e0;
}

.report-modal-title {
  margin: 0;
  font-size: 1.5rem;
}

.report-modal-close-btn {
  font-size: 2rem;
  font-weight: 300;
  line-height: 1;
  border: none;
  background: transparent;
  cursor: pointer;
  color: #888;
}

.report-modal-body {
  overflow-y: auto;
  padding: 1rem;
}

/* Make the inner report's background transparent */
.report-modal-body >>> .game-report-container {
  box-shadow: none;
  border: none;
  margin: 0;
  padding: 0;
  max-width: 100%;
}
</style>
