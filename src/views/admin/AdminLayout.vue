<template>
  <div class="admin-layout">
    <aside class="admin-sidebar" :class="{ collapsed: isSidebarCollapsed }">
      <div class="sidebar-header">
        <div class="logo">
          <span class="logo-icon">⚙️</span>
          <span class="logo-text" v-if="!isSidebarCollapsed">管理后台</span>
        </div>
        <button class="toggle-btn" @click="toggleSidebar">
          <span v-if="!isSidebarCollapsed">◀</span>
          <span v-else>▶</span>
        </button>
      </div>
      
      <nav class="sidebar-nav">
        <router-link 
          to="/admin/carousel" 
          class="nav-item" 
          :class="{ active: $route.path === '/admin/carousel' }"
        >
          <span class="nav-icon">🖼️</span>
          <span class="nav-text" v-if="!isSidebarCollapsed">轮播图管理</span>
        </router-link>
        <router-link 
          to="/admin/intro" 
          class="nav-item" 
          :class="{ active: $route.path === '/admin/intro' }"
        >
          <span class="nav-icon">📋</span>
          <span class="nav-text" v-if="!isSidebarCollapsed">平台简介管理</span>
        </router-link>
      </nav>
      
      <div class="sidebar-footer">
        <div class="user-info" v-if="!isSidebarCollapsed">
          <span class="user-icon">👤</span>
          <div class="user-details">
            <span class="user-name">{{ username }}</span>
            <span class="user-role">管理员</span>
          </div>
        </div>
        <button class="logout-btn" @click="handleLogout">
          <span>🚪</span>
          <span v-if="!isSidebarCollapsed">退出登录</span>
        </button>
      </div>
    </aside>
    
    <main class="admin-main">
      <header class="admin-header">
        <div class="header-left">
          <h1 class="page-title">{{ pageTitle }}</h1>
        </div>
        <div class="header-right">
          <span class="welcome-text">欢迎，{{ username }}</span>
          <button class="back-to-front" @click="goToFrontend">
            🏠 返回前台
          </button>
        </div>
      </header>
      
      <div class="admin-content">
        <router-view />
      </div>
    </main>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { removeToken } from '../../utils/request'

const route = useRoute()
const router = useRouter()

const isSidebarCollapsed = ref(false)
const username = ref(localStorage.getItem('username') || '管理员')

const pageTitle = computed(() => {
  const path = route.path
  if (path === '/admin/carousel') return '轮播图管理'
  if (path === '/admin/intro') return '平台简介管理'
  if (path === '/admin/home') return '管理后台首页'
  return '管理后台'
})

const toggleSidebar = () => {
  isSidebarCollapsed.value = !isSidebarCollapsed.value
}

const handleLogout = () => {
  localStorage.removeItem('isLoggedIn')
  localStorage.removeItem('username')
  localStorage.removeItem('nickname')
  localStorage.removeItem('userType')
  removeToken()
  router.push('/login')
}

const goToFrontend = () => {
  router.push('/')
}
</script>

<style scoped>
.admin-layout {
  display: flex;
  min-height: 100vh;
  background: #f5f7fa;
}

.admin-sidebar {
  width: 260px;
  background: linear-gradient(180deg, #2c3e50 0%, #1a252f 100%);
  color: white;
  display: flex;
  flex-direction: column;
  transition: width 0.3s ease;
  position: fixed;
  top: 0;
  left: 0;
  height: 100vh;
  z-index: 100;
}

.admin-sidebar.collapsed {
  width: 80px;
}

.sidebar-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.logo {
  display: flex;
  align-items: center;
  gap: 12px;
}

.logo-icon {
  font-size: 1.8rem;
}

.logo-text {
  font-size: 1.2rem;
  font-weight: 600;
}

.toggle-btn {
  background: rgba(255, 255, 255, 0.1);
  border: none;
  color: white;
  width: 32px;
  height: 32px;
  border-radius: 8px;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s ease;
}

.toggle-btn:hover {
  background: rgba(255, 255, 255, 0.2);
}

.sidebar-nav {
  flex: 1;
  padding: 20px 10px;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.nav-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 14px 16px;
  color: rgba(255, 255, 255, 0.7);
  text-decoration: none;
  border-radius: 10px;
  transition: all 0.3s ease;
}

.nav-item:hover {
  background: rgba(255, 255, 255, 0.1);
  color: white;
}

.nav-item.active {
  background: linear-gradient(135deg, #6B8E23, #8FBC8F);
  color: white;
}

.nav-icon {
  font-size: 1.3rem;
  width: 24px;
  text-align: center;
}

.nav-text {
  font-size: 0.95rem;
  font-weight: 500;
}

.sidebar-footer {
  padding: 20px;
  border-top: 1px solid rgba(255, 255, 255, 0.1);
}

.user-info {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 16px;
  padding: 12px;
  background: rgba(255, 255, 255, 0.05);
  border-radius: 10px;
}

.user-icon {
  font-size: 1.5rem;
}

.user-details {
  display: flex;
  flex-direction: column;
}

.user-name {
  font-size: 0.95rem;
  font-weight: 600;
}

.user-role {
  font-size: 0.8rem;
  color: rgba(255, 255, 255, 0.6);
}

.logout-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  width: 100%;
  padding: 12px;
  background: rgba(255, 255, 255, 0.1);
  border: none;
  color: white;
  border-radius: 10px;
  cursor: pointer;
  font-size: 0.9rem;
  transition: all 0.3s ease;
}

.logout-btn:hover {
  background: rgba(196, 91, 26, 0.8);
}

.admin-main {
  flex: 1;
  margin-left: 260px;
  display: flex;
  flex-direction: column;
  transition: margin-left 0.3s ease;
}

.admin-sidebar.collapsed + .admin-main {
  margin-left: 80px;
}

.admin-header {
  background: white;
  padding: 20px 30px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.05);
  position: sticky;
  top: 0;
  z-index: 50;
}

.page-title {
  font-size: 1.5rem;
  color: #2c3e50;
  margin: 0;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 20px;
}

.welcome-text {
  color: #7f8c8d;
  font-size: 0.95rem;
}

.back-to-front {
  padding: 10px 20px;
  background: linear-gradient(135deg, #6B8E23, #8FBC8F);
  color: white;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  font-size: 0.9rem;
  font-weight: 500;
  transition: all 0.3s ease;
}

.back-to-front:hover {
  transform: translateY(-2px);
  box-shadow: 0 5px 15px rgba(107, 142, 35, 0.3);
}

.admin-content {
  flex: 1;
  padding: 30px;
}

@media (max-width: 768px) {
  .admin-sidebar {
    width: 80px;
  }
  
  .admin-sidebar.collapsed {
    width: 0;
    transform: translateX(-100%);
  }
  
  .admin-main {
    margin-left: 80px;
  }
  
  .admin-sidebar.collapsed + .admin-main {
    margin-left: 0;
  }
  
  .logo-text,
  .nav-text,
  .user-info {
    display: none;
  }
  
  .admin-header {
    padding: 15px 20px;
    flex-direction: column;
    gap: 10px;
    align-items: flex-start;
  }
  
  .header-right {
    width: 100%;
    justify-content: space-between;
  }
  
  .admin-content {
    padding: 20px;
  }
}
</style>
