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
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  padding: 5px 10px;
  border-radius: 10px;
}

.logo:hover {
  transform: scale(1.05);
}

.logo:hover .logo-icon {
  animation: bounce 0.5s ease;
}

.logo-icon {
  font-size: 1.8rem;
  margin-right: 10px;
  transition: transform 0.3s ease;
}

.logo-text {
  background: linear-gradient(135deg, #3498db, #2ecc71);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  position: relative;
}

.logo-text::after {
  content: '';
  position: absolute;
  bottom: -2px;
  left: 0;
  width: 0;
  height: 2px;
  background: linear-gradient(135deg, #3498db, #2ecc71);
  transition: width 0.3s ease;
}

.logo:hover .logo-text::after {
  width: 100%;
}

@keyframes bounce {
  0%, 100% {
    transform: translateY(0);
  }
  50% {
    transform: translateY(-5px);
  }
}

.nav {
  display: flex;
  gap: 30px;
}

.nav-link {
  font-size: 1rem;
  color: #555;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  position: relative;
  padding: 8px 16px;
  border-radius: 8px;
  overflow: hidden;
}

.nav-link::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: linear-gradient(135deg, rgba(52, 152, 219, 0.1), rgba(46, 204, 113, 0.1));
  border-radius: 8px;
  opacity: 0;
  transition: opacity 0.3s ease;
  z-index: -1;
}

.nav-link::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 50%;
  width: 0;
  height: 2px;
  background: linear-gradient(135deg, #3498db, #2ecc71);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  transform: translateX(-50%);
}

.nav-link:hover {
  color: #3498db;
  transform: translateY(-2px);
}

.nav-link:hover::before {
  opacity: 1;
}

.nav-link:hover::after {
  width: 80%;
}

.nav-link.active {
  color: #3498db;
  font-weight: 600;
}

.nav-link.active::after {
  width: 100%;
  left: 0;
  transform: none;
  bottom: -5px;
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
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  position: relative;
  overflow: hidden;
}

.mobile-nav-link::before {
  content: '';
  position: absolute;
  left: 0;
  top: 0;
  width: 3px;
  height: 100%;
  background: linear-gradient(135deg, #3498db, #2ecc71);
  transform: scaleY(0);
  transition: transform 0.3s ease;
}

.mobile-nav-link:hover {
  background-color: #f5f7fa;
  color: #3498db;
  padding-left: 30px;
}

.mobile-nav-link:hover::before {
  transform: scaleY(1);
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
