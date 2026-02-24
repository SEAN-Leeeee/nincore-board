<template>
  <div class="login-view">
    <div class="login-container">
      <div class="login-box">
        <h2 class="title">즐코어보드 접속하기</h2>
        <form @submit.prevent="login">
          <div class="input-group">
            <label for="ip-address">ID (IP는 변경 가능합니다.)</label>
            <input type="text" id="ip-address" v-model="ip" />
          </div>
          <div class="input-group">
            <label for="password">Password</label>
            <input type="password" id="password" v-model="password" required placeholder="Enter your password" />
          </div>
          <button type="submit" class="login-button" :disabled="loading">
            {{ loading ? 'Connecting...' : 'Login' }}
          </button>
          <p v-if="error" class="error-message">{{ error }}</p>
        </form>
      </div>
    </div>
  </div>
</template>

<script>
import '@/assets/css/login.css';
import { publishState, clearPersistedState, loadState } from '@/shared/stateChannel';

export default {
  name: 'LoginComponent',
  components: {
    // No background component
  },
  data() {
    return {
      ip: '',
      password: '',
      loading: false,
      error: null,
    };
  },
  async created() {
    this.loading = true;
    try {
      const response = await fetch('https://api.ipify.org?format=json');
      const data = await response.json();
      this.ip = data.ip.slice(0, 6);
    } catch (error) {
      this.error = 'Failed to get IP address. Please check your network connection.';
      console.error('Error fetching IP address:', error);
    } finally {
      this.loading = false;
    }
  },
  methods: {
    async login() {
      if (!this.ip || !this.password) {
        this.error = 'IP and password are required.';
        return;
      }
      this.loading = true;
      this.error = null;

      try {
        const response = await fetch('/api/login', {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
          },
          body: JSON.stringify({
            ip: this.ip,
            password: this.password,
          }),
        });


        console.log(response)
        const result = await response.json();

        if (!result || !result.sessionId) {
          this.error = "로그인 실패: 서버로부터 유효하지 않은 세션 ID를 받았습니다.";
          console.error("Invalid login response:", result);
          return;
        }

        const stored = loadState();
        const storedSessionId = String((stored && stored.sessionId) || "");
        const incomingSessionId = String(result.sessionId);

        // Session changed: prevent previous game's local data (gameLog/roster) from leaking.
        if ((stored && !storedSessionId) || (storedSessionId && storedSessionId !== incomingSessionId)) {
          clearPersistedState();
        }

        sessionStorage.setItem('sessionId', result.sessionId);

        if (result.isNewSession) {
          clearPersistedState();
        }

        // Save IP, password, and sessionId to state for RemoteControl to pick up
        publishState({
          ip: this.ip,
          password: this.password,
          sessionId: result.sessionId,
        });

        if (result.isNewSession) {
          this.$router.push({ name: 'Remote', params: { sessionId: result.sessionId } });
        } else {
          this.$router.push({ name: 'Display', params: { sessionId: result.sessionId } });
        }
      } catch (error) {
        this.error = 'Login error: ' + error.message;
        console.error('Login error:', error);
      } finally {
        this.loading = false;
      }
    },
  },
};
</script>

<style>
/* 등장 애니메이션을 login-container에 직접 적용 */
.login-container {
  animation: fade-in-up 1s ease-out forwards;
  animation-delay: 0.5s;
  opacity: 0;
}

@keyframes fade-in-up {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}
</style>
