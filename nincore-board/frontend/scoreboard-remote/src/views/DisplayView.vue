<template>
  <ScoreboardDisplay />
</template>

<script>
import ScoreboardDisplay from "@/components/display/ScoreBoardDisplay.vue";
import { connectWS, disconnectWS } from "@/shared/wsClient";
import { publishState } from "@/shared/stateChannel";

export default {
  name: "DisplayView",
  components: {
    ScoreboardDisplay,
  },
  mounted() {
    const router = this.$router;
    const sessionId = String(this.$route.params.sessionId || "");

    const handleStateUpdate = (state) => {
      // DisplayView는 상태를 받으면 stateChannel에 발행만 합니다.
      // 실제 UI 업데이트는 ScoreBoardDisplay 컴포넌트가 담당합니다.
      publishState({ ...(state || {}), sessionId });
    };

    const handleSessionEnd = () => {
      alert("세션이 종료되었습니다. 로그인 화면으로 돌아갑니다.");
      router.push({ name: "Login" });
    };

    if (!sessionId) {
      alert("세션 ID가 없습니다. 로그인 화면으로 돌아갑니다.");
      router.push({ name: "Login" });
      return;
    }
    connectWS(sessionId, handleStateUpdate, handleSessionEnd);
  },
  beforeDestroy() {
    disconnectWS();
  },
};
</script>
