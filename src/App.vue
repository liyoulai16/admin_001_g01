<template>
  <div id="app">
    <template v-if="!isLoginPage">
      <Header />
    </template>
    <main class="main-content">
      <router-view />
    </main>
    <template v-if="!isLoginPage">
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
  return route.path === '/login'
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
</style>
