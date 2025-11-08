<script setup>
</script>

<template>
  <div id="app">
    <main class="main-content">
      <router-view />
    </main>
  </div>
</template>

<script>
export default { name: 'App' }
</script>

<style>
* { margin: 0; padding: 0; box-sizing: border-box; }
body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; line-height: 1.6; color: #333; }

#app { min-height: 100vh; display: flex; flex-direction: column; }

/* 涓昏鍐呭鍖哄煙 */
.main-content { flex: 1; }

/* 鍝嶅簲寮忓湪鍚勭粍浠跺唴閮ㄥ鐞?*/

/* 骞虫粦婊氬姩 */
html { scroll-behavior: smooth; }

/* 鍏ㄥ眬鍔ㄧ敾 */
* { transition: color 0.3s ease, background-color 0.3s ease; }
</style>

