<template>
  <div class="rm-overlay" @click.self="$emit('close')">
    <div class="rm-panel">
      <div class="rm-head">
        <div class="rm-title"> 선수 변경</div>
        <button class="rm-close" @click="$emit('close')">닫기</button>
      </div>

      <div class="rm-desc">체크박스로 최대 5명 선택. 선택된 5명만 메인에 표시됩니다.</div>

      <div class="rm-table">
        <div class="rm-thead">
          <div class="rm-th rm-th--center rm-th--select">
            <label class="rm-selectall__label" aria-label="전체선택">
              <input
                type="checkbox"
                :checked="isAllSelected"
                :disabled="!canSelectAll"
                @change="toggleSelectAll"
              />
            </label>
          </div>
          <div class="rm-th rm-th--center">등번호</div>
          <div class="rm-th rm-th--center">이름</div>
          <div class="rm-th"></div>
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
        <button class="rm-btn rm-btn--ghost" @click="addRow">인원추가 +</button>
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
    },
    canSelectAll() {
      return this.localPlayers.length === 5;
    },
    isAllSelected() {
      return this.localPlayers.length > 0 && this.selectedCount === this.localPlayers.length;
    }
  },
  methods: {
    toggleSelect(id) {
      const p = this.localPlayers.find(x => x.id === id);
      if (!p) return;

      if (!p.selected && this.selectedCount >= 5) return;
      p.selected = !p.selected;
    },

    toggleSelectAll(e) {
      if (!this.canSelectAll) return;
      const checked = !!(e && e.target && e.target.checked);
      this.localPlayers.forEach(p => {
        p.selected = checked;
      });
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

<style scoped src="./roster-modal.css"></style>
