import Vue from "vue";
import App from "./App.vue";
import router from "./router";
import * as Sentry from "@sentry/vue";

Sentry.init({
  Vue: Vue,
  dsn: "https://8076c888b80173f7492580b09870a497@o4510786515173376.ingest.us.sentry.io/4510787062661120",
  // Setting this option to true will send default PII data to Sentry.
  // For example, automatic IP address collection on events
  sendDefaultPii: true,
  // Tracing configuration
  integrations: [
    Sentry.browserTracingIntegration({ router }), // Ensure router is passed for performance monitoring
  ],
  tracesSampleRate: 1.0, // Capture all transactions
  replaysSessionSampleRate: 0.1, // Session replay sampling rate
  replaysOnErrorSampleRate: 1.0, // 100% replay capture for error sessions
});

Vue.config.productionTip = false;

new Vue({
  router,
  render: (h) => h(App)
}).$mount("#app");


