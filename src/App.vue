<template>
  <div id="app">
    <template v-if="!isLoginPage && !isAdminPage">
      <Header />
    </template>
    <main class="main-content" :class="{ 'admin-main': isAdminPage }">
      <router-view />
    </main>
    <template v-if="!isLoginPage && !isAdminPage">
      <Footer />
    </template>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import Header from './components/Header.vue'
import Footer from './components/Footer.vue'

const route = useRoute()

const isLoginPage = computed(() => {
  return route.path === '/login' || route.path === '/register'
})

const isAdminPage = computed(() => {
  return route.path.startsWith('/admin')
})
</script>

<style scoped>
#app {
  display: flex;
  flex-direction: column;
  min-height: 100vh;
}

.main-content {
  flex: 1;
}

.admin-main {
  padding: 0;
}
</style>
