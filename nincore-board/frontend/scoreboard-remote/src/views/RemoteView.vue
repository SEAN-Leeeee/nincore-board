<template>
  <RemoteControl ref="remoteControl" />
</template>

<script>
import RemoteControl from "@/components/remote/RemoteControl.vue";
import { connectWS, disconnectWS } from "@/shared/wsClient";
import { loadState, publishState } from "@/shared/stateChannel";

export default {
  name: "RemoteView",
  components: { RemoteControl },
  mounted() {
    const router = this.$router;
    const remoteControlInstance = this.$refs.remoteControl;
    const sessionId = String(this.$route.params.sessionId || "");

    const handleStateUpdate = (state) => {
      const scopedState = { ...(state || {}), sessionId };
      // 1. 상태를 저장/병합해 약한 패킷으로 기존 데이터가 지워지지 않게 보호
      publishState(scopedState);

      // 2. 현재 탭 UI는 저장 후의 최신 스냅샷을 적용
      const latest = loadState(sessionId) || scopedState;
      if (remoteControlInstance) {
        remoteControlInstance.applyStateToView(latest);
      }
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
    connectWS(sessionId, handleStateUpdate.bind(this), handleSessionEnd);
  },
  beforeDestroy() {
    disconnectWS();
  },
};
</script>
