<template>
  <RemoteControl ref="remoteControl" />
</template>

<script>
import RemoteControl from "@/components/remote/RemoteControl.vue";
import { connectWS, disconnectWS } from "@/shared/wsClient";
import { publishState } from "@/shared/stateChannel";

export default {
  name: "RemoteView",
  components: { RemoteControl },
  mounted() {
    const router = this.$router;
    const remoteControlInstance = this.$refs.remoteControl;

    const handleStateUpdate = (state) => {
      // 1. 현재 탭의 UI를 직접 업데이트하기 위해 자식 컴포넌트의 메서드를 호출합니다.
      if (remoteControlInstance) {
        remoteControlInstance.applyStateToView(state);
      }
      // 2. 다른 탭(디스플레이)에 상태를 전파합니다.
      publishState(state);
    };

    const handleSessionEnd = () => {
      alert("세션이 종료되었습니다. 로그인 화면으로 돌아갑니다.");
      router.push({ name: "Login" });
    };

    const sessionId = this.$route.params.sessionId;
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
