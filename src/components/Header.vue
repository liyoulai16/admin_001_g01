<template>
  <header class="header">
    <div class="container">
      <div class="header-content">
        <router-link to="/" class="logo">
          <span class="logo-icon">🏠</span>
          <span class="logo-text">社区生活服务</span>
        </router-link>
        <nav class="nav">
          <router-link to="/" class="nav-link" :class="{ active: $route.path === '/' }">首页</router-link>
          <router-link to="/services" class="nav-link" :class="{ active: $route.path === '/services' }">服务列表</router-link>
          <router-link to="/about" class="nav-link" :class="{ active: $route.path === '/about' }">关于我们</router-link>
          <router-link to="/contact" class="nav-link" :class="{ active: $route.path === '/contact' }">联系我们</router-link>
        </nav>
        <button class="mobile-menu-btn" @click="toggleMobileMenu">
          <span class="menu-icon"></span>
        </button>
      </div>
    </div>
    <div class="mobile-menu" :class="{ open: isMobileMenuOpen }">
      <nav class="mobile-nav">
        <router-link to="/" class="mobile-nav-link" @click="closeMobileMenu">首页</router-link>
        <router-link to="/services" class="mobile-nav-link" @click="closeMobileMenu">服务列表</router-link>
        <router-link to="/about" class="mobile-nav-link" @click="closeMobileMenu">关于我们</router-link>
        <router-link to="/contact" class="mobile-nav-link" @click="closeMobileMenu">联系我们</router-link>
      </nav>
    </div>
  </header>
</template>

<script setup>
import { ref } from 'vue'

const isMobileMenuOpen = ref(false)

const toggleMobileMenu = () => {
  isMobileMenuOpen.value = !isMobileMenuOpen.value
}

const closeMobileMenu = () => {
  isMobileMenuOpen.value = false
}
</script>

<style scoped>
.header {
  background-color: #fff;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  position: sticky;
  top: 0;
  z-index: 1000;
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px 0;
}

.logo {
  display: flex;
  align-items: center;
  font-size: 1.5rem;
  font-weight: bold;
  color: #2c3e50;
}

.logo-icon {
  font-size: 1.8rem;
  margin-right: 10px;
}

.logo-text {
  background: linear-gradient(135deg, #3498db, #2ecc71);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.nav {
  display: flex;
  gap: 30px;
}

.nav-link {
  font-size: 1rem;
  color: #555;
  transition: color 0.3s ease;
  position: relative;
}

.nav-link:hover,
.nav-link.active {
  color: #3498db;
}

.nav-link.active::after {
  content: '';
  position: absolute;
  bottom: -5px;
  left: 0;
  width: 100%;
  height: 2px;
  background: linear-gradient(135deg, #3498db, #2ecc71);
}

.mobile-menu-btn {
  display: none;
  flex-direction: column;
  justify-content: space-between;
  width: 25px;
  height: 20px;
  padding: 0;
}

.menu-icon {
  width: 100%;
  height: 3px;
  background-color: #333;
  border-radius: 2px;
  transition: all 0.3s ease;
}

.mobile-menu {
  display: none;
  background-color: #fff;
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.1);
  overflow: hidden;
  max-height: 0;
  transition: max-height 0.3s ease;
}

.mobile-menu.open {
  max-height: 300px;
}

.mobile-nav {
  display: flex;
  flex-direction: column;
  padding: 10px 0;
}

.mobile-nav-link {
  padding: 15px 20px;
  font-size: 1rem;
  color: #555;
  transition: background-color 0.3s ease;
}

.mobile-nav-link:hover {
  background-color: #f5f7fa;
  color: #3498db;
}

@media (max-width: 768px) {
  .nav {
    display: none;
  }

  .mobile-menu-btn {
    display: flex;
  }

  .mobile-menu {
    display: block;
  }
}
</style>
