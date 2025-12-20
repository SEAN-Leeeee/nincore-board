<template>
  <div id="app">
    <router-view />
  </div>
</template>

<script>
export default { name: "App" };
</script>

<style>
* {
  -webkit-text-size-adjust: 100%;
}

html,
body,
#app {
  height: 100%;
  margin: 0;
  overflow: hidden; /* ✅ 전체 스크롤 금지 */
}
</style>
