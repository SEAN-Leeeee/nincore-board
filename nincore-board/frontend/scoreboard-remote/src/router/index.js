import Vue from "vue";
import Router from "vue-router";

import RemoteView from "@/views/RemoteView.vue";
import DisplayView from "@/views/DisplayView.vue";
import LoginView from "@/views/LoginView.vue";

Vue.use(Router);

const router = new Router({
  mode: "history",
  routes: [
    { path: "/", redirect: "/login" },
    { path: "/login", name: 'Login', component: LoginView },
    { path: "/remote/:sessionId", name: 'Remote', component: RemoteView, meta: { requiresAuth: true } },
    { path: "/display/:sessionId", name: 'Display', component: DisplayView, meta: { requiresAuth: true } }
  ]
});

router.beforeEach(async (to, from, next) => {
  const loggedIn = sessionStorage.getItem('sessionId');
  const requiresAuth = to.matched.some(record => record.meta && record.meta.requiresAuth);

  if (requiresAuth && !loggedIn) {
    // 1. 인증이 필요한 페이지에 접속하는데, 세션 ID가 없는 경우 -> 로그인 페이지로
    next('/login');
  } else if (requiresAuth && loggedIn) {
    // 2. 인증이 필요한 페이지에 접속하고, 세션 ID도 있는 경우 -> 서버에 유효성 확인
    try {
      const response = await fetch(`/api/session/${loggedIn}/status`);
      if (response.ok) {
        // 2a. 세션이 유효함 -> 페이지로 정상 이동
        next();
      } else {
        // 2b. 세션이 유효하지 않음 (만료, 삭제 등) -> 세션 정보 지우고 로그인 페이지로
        sessionStorage.removeItem('sessionId');
        next('/login');
      }
    } catch (error) {
      // API 호출 중 네트워크 에러 등 발생 -> 로그인 페이지로
      console.error("Session check failed:", error);
      sessionStorage.removeItem('sessionId');
      next('/login');
    }
  }
  else {
    // 3. 인증이 필요 없는 페이지 (e.g. /login) -> 정상 진행
    next();
  }
});

export default router;
