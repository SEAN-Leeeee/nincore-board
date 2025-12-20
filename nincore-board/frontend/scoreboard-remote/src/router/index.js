import Vue from "vue";
import Router from "vue-router";

import RemoteView from "@/views/RemoteView.vue";
import DisplayView from "@/views/DisplayView.vue";

Vue.use(Router);

export default new Router({
  mode: "history",
  routes: [
    { path: "/", redirect: "/remote" },
    { path: "/remote", component: RemoteView },
    { path: "/display", component: DisplayView }
  ]
});
