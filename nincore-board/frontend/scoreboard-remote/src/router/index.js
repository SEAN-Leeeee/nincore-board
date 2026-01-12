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

router.beforeEach((to, from, next) => {
  const loggedIn = sessionStorage.getItem('sessionId');
  // record.meta가 존재하는지 먼저 확인하여 버그를 수정합니다.
  const requiresAuth = to.matched.some(record => record.meta && record.meta.requiresAuth);

  // --- 디버깅 코드 시작 ---
  console.log(
    `[Nav Guard] ` +
    `From: ${from.path}, ` +
    `To: ${to.path}, ` +
    `SessionID: ${loggedIn}, ` +
    `RequiresAuth: ${requiresAuth}`
  );
  // --- 디버깅 코드 끝 ---

  if (requiresAuth && !loggedIn) {
    console.log('[Nav Guard] 인증 필요, 로그인 안됨 -> /login으로 리다이렉트');
    next('/login');
  } else {
    console.log('[Nav Guard] 정상 진행 -> next() 호출');
    next();
  }
});

export default router;
