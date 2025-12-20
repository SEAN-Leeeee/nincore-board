<template>
  <div class="rm-overlay" @click.self="$emit('close')">
    <div class="rm-panel">
      <div class="rm-head">
        <div class="rm-title">{{ teamName }} 선수 변경</div>
        <button class="rm-close" @click="$emit('close')">닫기</button>
      </div>

      <div class="rm-desc">체크박스로 최대 5명 선택. 선택된 5명만 메인에 표시됩니다.</div>

      <div class="rm-table">
        <div class="rm-thead">
          <div>선택</div>
          <div>등번호</div>
          <div>이름</div>
          <div></div>
        </div>

        <div class="rm-row" v-for="p in localPlayers" :key="p.id">
          <div class="rm-cell rm-cell--center">
            <input
                type="checkbox"
                :checked="p.selected"
                :disabled="!p.selected && selectedCount >= 5"
                @change="toggleSelect(p.id)"
            />
          </div>

          <div class="rm-cell">
            <input class="rm-no" v-model="p.no" placeholder="번호" inputmode="numeric" />
          </div>

          <div class="rm-cell">
            <input class="rm-name" v-model="p.name" placeholder="이름" />
          </div>

          <div class="rm-cell rm-cell--center">
            <button class="rm-x" @click="removeRow(p.id)">×</button>
          </div>
        </div>
      </div>

      <div class="rm-footer">
        <button class="rm-btn rm-btn--ghost" @click="addRow">추가</button>
        <button class="rm-btn rm-btn--primary" @click="save">저장</button>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: "RosterModal",
  props: {
    team: { type: String, required: true },
    teamName: { type: String, required: true },
    players: { type: Array, required: true }
  },
  data() {
    return {
      localPlayers: this.players.map(p => ({ ...p })),
      nextId: this.players.reduce((m, p) => Math.max(m, p.id), 0) + 1
    };
  },
  computed: {
    selectedCount() {
      return this.localPlayers.filter(p => p.selected).length;
    }
  },
  methods: {
    toggleSelect(id) {
      const p = this.localPlayers.find(x => x.id === id);
      if (!p) return;

      if (!p.selected && this.selectedCount >= 5) return;
      p.selected = !p.selected;
    },

    addRow() {
      this.localPlayers.push({
        id: this.nextId++,
        no: "",
        name: "",
        selected: false
      });
    },

    removeRow(id) {
      this.localPlayers = this.localPlayers.filter(p => p.id !== id);
    },

    save() {
      // ✅ 검증: 선택된 선수들 번호는 반드시 입력 + 중복 금지
      const selected = this.localPlayers.filter(p => p.selected);

      for (const p of selected) {
        if (!String(p.no || "").trim()) {
          alert("선택된 선수의 등번호를 입력해주세요.");
          return;
        }
      }

      const nos = selected.map(p => String(p.no).trim());
      const set = new Set(nos);
      if (set.size !== nos.length) {
        alert("등번호가 중복되었습니다. 중복 없이 입력해주세요.");
        return;
      }

      this.$emit("save", { team: this.team, players: this.localPlayers });
    }
  }
};
</script>

<style scoped>
.rm-overlay {
  position: fixed;
  inset: 0;
  z-index: 99999;
  background: rgba(0, 0, 0, 0.72);
  display: grid;
  place-items: center;
}

.rm-panel {
  width: min(92vw, 560px);
  max-height: 86vh;
  overflow: hidden;
  background: #0e1420;
  color: #eef3ff;
  border: 1px solid rgba(255, 255, 255, 0.12);
  border-radius: 16px;
  display: grid;
  grid-template-rows: auto auto 1fr auto;
}

.rm-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12px 12px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.rm-title {
  font-weight: 900;
  font-size: 14px;
}

.rm-close {
  border: 1px solid rgba(255, 255, 255, 0.14);
  background: transparent;
  color: #eef3ff;
  border-radius: 10px;
  padding: 8px 10px;
  cursor: pointer;
  font-weight: 900;
}

.rm-desc {
  padding: 10px 12px;
  font-size: 12px;
  color: rgba(238, 243, 255, 0.65);
}

.rm-table {
  padding: 0 12px 12px;
  overflow: auto;
}

.rm-thead {
  display: grid;
  grid-template-columns: 52px 90px 1fr 44px;
  gap: 8px;
  padding: 10px 0;
  font-size: 11px;
  font-weight: 900;
  color: rgba(238, 243, 255, 0.65);
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.rm-row {
  display: grid;
  grid-template-columns: 52px 90px 1fr 44px;
  gap: 8px;
  padding: 10px 0;
  border-bottom: 1px solid rgba(255, 255, 255, 0.06);
  align-items: center;
}

.rm-cell--center {
  display: flex;
  justify-content: center;
}

.rm-no,
.rm-name {
  width: 100%;
  border: 1px solid rgba(255, 255, 255, 0.12);
  background: rgba(255, 255, 255, 0.04);
  color: #eef3ff;
  border-radius: 10px;
  padding: 10px 10px;
  outline: none;
  font-weight: 800;
  font-size: 12px;
}

.rm-x {
  width: 32px;
  height: 32px;
  border-radius: 10px;
  border: 1px solid rgba(255, 255, 255, 0.14);
  background: transparent;
  color: #eef3ff;
  font-size: 18px;
  font-weight: 900;
  cursor: pointer;
}

.rm-footer {
  display: flex;
  justify-content: space-between;
  padding: 12px;
  border-top: 1px solid rgba(255, 255, 255, 0.1);
}

.rm-btn {
  border: 1px solid rgba(255, 255, 255, 0.14);
  background: rgba(255, 255, 255, 0.06);
  color: #eef3ff;
  border-radius: 12px;
  padding: 10px 12px;
  font-weight: 900;
  cursor: pointer;
  font-size: 12px;
}

.rm-btn--ghost {
  background: transparent;
}

.rm-btn--primary {
  background: rgba(79, 124, 255, 0.18);
  border-color: rgba(79, 124, 255, 0.3);
}
</style>
