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

        <div class="rm-row" v-for="(p, index) in localPlayers" :key="p.id">
          <div class="rm-cell rm-cell--center">
            <input
                type="checkbox"
                :checked="p.selected"
                :disabled="!p.selected && selectedCount >= 5"
                @change="toggleSelect(p.id)"
            />
          </div>

          <div class="rm-cell">
            <input class="rm-no" :value="p.no" @input="handleNoInput($event, index)" placeholder="번호" inputmode="numeric" />
          </div>

          <div class="rm-cell">
            <input class="rm-name" v-model="p.name" placeholder="이름" maxlength="4" />
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
    let initialPlayers = this.players.map(p => ({ ...p }));
    let startingNextId = this.players.reduce((m, p) => Math.max(m, p.id), 0) + 1;

    // If no players are provided, add 5 empty slots by default
    if (initialPlayers.length === 0) {
      for (let i = 0; i < 5; i++) {
        initialPlayers.push({
          id: startingNextId++,
          no: "",
          name: "",
          selected: false
        });
      }
    }

    return {
      localPlayers: initialPlayers,
      nextId: startingNextId
    };
  },
  computed: {
    selectedCount() {
      return this.localPlayers.filter(p => p.selected).length;
    },
    canSelectAll() {
      return this.localPlayers.length <= 5;
    },
    isAllSelected() {
      return this.localPlayers.length > 0 && this.selectedCount === this.localPlayers.length;
    }
  },
  methods: {
    handleNoInput(event, index) {
      const value = event.target.value;
      // 숫자가 아닌 문자를 제거하고, 길이를 최대 2자로 제한합니다.
      const filteredValue = value.replace(/[^0-9]/g, "").slice(0, 2);
      const player = this.localPlayers[index];

      // Vue의 반응성을 보장하기 위해 this.$set을 사용하여 객체를 교체합니다.
      this.$set(this.localPlayers, index, { ...player, no: filteredValue });
    },
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
        // 1. 이름 빈칸 검사
        if (!String(p.name || "").trim()) {
          alert("선택된 선수의 이름을 입력해주세요.");
          return;
        }

        // 2. 등번호 빈칸 검사 (0도 유효한 등번호로 인식)
        const noStr = String(p.no ?? "").trim();
        if (noStr === "") {
          alert("선택된 선수의 등번호를 입력해주세요.");
          return;
        }

        // 3. 등번호 00-99 숫자 검사
        const num = parseInt(noStr, 10);
        if (isNaN(num) || num < 0 || num > 99) {
          alert("등번호는 00부터 99까지의 숫자만 입력할 수 있습니다.");
          return;
        }
      }

      // 4. 등번호 중복 검사
      const nos = selected.map(p => String(p.no).trim());
      const set = new Set(nos);
      if (set.size !== nos.length) {
        alert("중복된 등번호가 있습니다.");
        return;
      }

      this.$emit("save", { team: this.team, players: this.localPlayers });
    }
  }
};
</script>

<style scoped src="./roster-modal.css"></style>
