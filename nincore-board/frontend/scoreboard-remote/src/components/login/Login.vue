<template>
  <div class="login-container">
    <div class="login-box">
      <h2 class="title">Scoreboard Access</h2>
      <form @submit.prevent="login">
        <div class="input-group">
          <label for="ip-address">ID (IP Address)</label>
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
</template>

<script>
import '@/assets/css/login.css';

export default {
  name: 'LoginComponent',
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
      this.ip = data.ip;
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
        // 백엔드로부터 { sessionId, isNewSession } 형태의 응답을 받음
        const result = await response.json();

        // [수정] 서버로부터 받은 sessionId가 유효한지 확인합니다.
        if (!result || !result.sessionId) {
          this.error = "로그인 실패: 서버로부터 유효하지 않은 세션 ID를 받았습니다.";
          console.error("Invalid login response:", result);
          return; // 페이지 이동을 막습니다.
        }

        // 이제 비밀번호 대신 고유 ID인 sessionId를 저장합니다.
        sessionStorage.setItem('sessionId', result.sessionId);

        if (result.isNewSession) {
          // [수정] 새 세션인 경우, localStorage의 이전 상태를 삭제합니다.
          localStorage.removeItem("nincore-board-state");
          // 새 세션이면 remote로 접속
          this.$router.push({ name: 'Remote', params: { sessionId: result.sessionId } });
        } else {
          // 기존 세션이 있으면 display로 접속
          this.$router.push({ name: 'Display', params: { sessionId: result.sessionId } });
        }
      } catch (error) {
        this.error = error.message;
        console.error('Login error:', error);
      } finally {
        this.loading = false;
      }
    },
  },
};
</script>
