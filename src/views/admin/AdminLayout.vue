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
        <div class="nav-group">
          <div 
            class="nav-item nav-group-header" 
            :class="{ active: isHomeMenuActive, expanded: isHomeMenuExpanded }"
            @click="toggleHomeMenu"
          >
            <span class="nav-icon">🏠</span>
            <span class="nav-text" v-if="!isSidebarCollapsed">首页管理</span>
            <span class="nav-expand-icon" v-if="!isSidebarCollapsed">{{ isHomeMenuExpanded ? '▼' : '▶' }}</span>
          </div>
          <transition name="menu-expand">
            <div 
              class="nav-group-items" 
              v-show="isHomeMenuExpanded && !isSidebarCollapsed"
              :style="{ maxHeight: isHomeMenuExpanded ? '200px' : '0px', opacity: isHomeMenuExpanded ? '1' : '0' }"
            >
              <router-link 
                to="/admin/carousel" 
                class="nav-item nav-sub-item" 
                :class="{ active: $route.path === '/admin/carousel' }"
              >
                <span class="nav-icon">🖼️</span>
                <span class="nav-text">轮播图管理</span>
              </router-link>
              <router-link 
                to="/admin/intro" 
                class="nav-item nav-sub-item" 
                :class="{ active: $route.path === '/admin/intro' }"
              >
                <span class="nav-icon">📋</span>
                <span class="nav-text">平台简介管理</span>
              </router-link>
              <router-link 
                to="/admin/announcement" 
                class="nav-item nav-sub-item" 
                :class="{ active: $route.path === '/admin/announcement' }"
              >
                <span class="nav-icon">📢</span>
                <span class="nav-text">公告管理</span>
              </router-link>
            </div>
          </transition>
        </div>
        
        <div class="nav-group">
          <div 
            class="nav-item nav-group-header" 
            :class="{ active: isServiceMenuActive, expanded: isServiceMenuExpanded }"
            @click="toggleServiceMenu"
          >
            <span class="nav-icon">🛠️</span>
            <span class="nav-text" v-if="!isSidebarCollapsed">服务列表管理</span>
            <span class="nav-expand-icon" v-if="!isSidebarCollapsed">{{ isServiceMenuExpanded ? '▼' : '▶' }}</span>
          </div>
          <transition name="menu-expand">
            <div 
              class="nav-group-items" 
              v-show="isServiceMenuExpanded && !isSidebarCollapsed"
              :style="{ maxHeight: isServiceMenuExpanded ? '200px' : '0px', opacity: isServiceMenuExpanded ? '1' : '0' }"
            >
              <router-link 
                to="/admin/service-category" 
                class="nav-item nav-sub-item" 
                :class="{ active: $route.path === '/admin/service-category' }"
              >
                <span class="nav-icon">📁</span>
                <span class="nav-text">服务分类管理</span>
              </router-link>
              <router-link 
                to="/admin/service" 
                class="nav-item nav-sub-item" 
                :class="{ active: $route.path === '/admin/service' }"
              >
                <span class="nav-icon">🛠️</span>
                <span class="nav-text">服务管理</span>
              </router-link>
            </div>
          </transition>
        </div>
        
        <div class="nav-group">
          <div 
            class="nav-item nav-group-header" 
            :class="{ active: isForumMenuActive, expanded: isForumMenuExpanded }"
            @click="toggleForumMenu"
          >
            <span class="nav-icon">💬</span>
            <span class="nav-text" v-if="!isSidebarCollapsed">论坛管理</span>
            <span class="nav-expand-icon" v-if="!isSidebarCollapsed">{{ isForumMenuExpanded ? '▼' : '▶' }}</span>
          </div>
          <transition name="menu-expand">
            <div 
              class="nav-group-items" 
              v-show="isForumMenuExpanded && !isSidebarCollapsed"
              :style="{ maxHeight: isForumMenuExpanded ? '200px' : '0px', opacity: isForumMenuExpanded ? '1' : '0' }"
            >
              <router-link 
                to="/admin/forum-category" 
                class="nav-item nav-sub-item" 
                :class="{ active: $route.path === '/admin/forum-category' }"
              >
                <span class="nav-icon">📁</span>
                <span class="nav-text">论坛版块管理</span>
              </router-link>
              <router-link 
                to="/admin/forum-post" 
                class="nav-item nav-sub-item" 
                :class="{ active: $route.path === '/admin/forum-post' }"
              >
                <span class="nav-icon">📝</span>
                <span class="nav-text">帖子管理</span>
              </router-link>
            </div>
          </transition>
        </div>
        
        <div class="nav-group">
          <div 
            class="nav-item nav-group-header" 
            :class="{ active: isOrderMenuActive, expanded: isOrderMenuExpanded }"
            @click="toggleOrderMenu"
          >
            <span class="nav-icon">📋</span>
            <span class="nav-text" v-if="!isSidebarCollapsed">订单管理</span>
            <span class="nav-expand-icon" v-if="!isSidebarCollapsed">{{ isOrderMenuExpanded ? '▼' : '▶' }}</span>
          </div>
          <transition name="menu-expand">
            <div 
              class="nav-group-items" 
              v-show="isOrderMenuExpanded && !isSidebarCollapsed"
              :style="{ maxHeight: isOrderMenuExpanded ? '200px' : '0px', opacity: isOrderMenuExpanded ? '1' : '0' }"
            >
              <router-link 
                to="/admin/order" 
                class="nav-item nav-sub-item" 
                :class="{ active: $route.path === '/admin/order' }"
              >
                <span class="nav-icon">📋</span>
                <span class="nav-text">订单管理</span>
              </router-link>
            </div>
          </transition>
        </div>
        
        <div class="nav-group">
          <div 
            class="nav-item nav-group-header" 
            :class="{ active: isContactMenuActive, expanded: isContactMenuExpanded }"
            @click="toggleContactMenu"
          >
            <span class="nav-icon">💬</span>
            <span class="nav-text" v-if="!isSidebarCollapsed">联系管理</span>
            <span class="nav-expand-icon" v-if="!isSidebarCollapsed">{{ isContactMenuExpanded ? '▼' : '▶' }}</span>
          </div>
          <transition name="menu-expand">
            <div 
              class="nav-group-items" 
              v-show="isContactMenuExpanded && !isSidebarCollapsed"
              :style="{ maxHeight: isContactMenuExpanded ? '400px' : '0px', opacity: isContactMenuExpanded ? '1' : '0' }"
            >
              <router-link 
                to="/admin/message" 
                class="nav-item nav-sub-item" 
                :class="{ active: $route.path === '/admin/message' }"
              >
                <span class="nav-icon">📭</span>
                <span class="nav-text">留言管理</span>
              </router-link>
              <router-link 
                to="/admin/contact-card" 
                class="nav-item nav-sub-item" 
                :class="{ active: $route.path === '/admin/contact-card' }"
              >
                <span class="nav-icon">📞</span>
                <span class="nav-text">联系方式管理</span>
              </router-link>
              <router-link 
                to="/admin/working-hour" 
                class="nav-item nav-sub-item" 
                :class="{ active: $route.path === '/admin/working-hour' }"
              >
                <span class="nav-icon">⏰</span>
                <span class="nav-text">工作时间管理</span>
              </router-link>
              <router-link 
                to="/admin/faq" 
                class="nav-item nav-sub-item" 
                :class="{ active: $route.path === '/admin/faq' }"
              >
                <span class="nav-icon">❓</span>
                <span class="nav-text">常见问题管理</span>
              </router-link>
              <router-link 
                to="/admin/feature" 
                class="nav-item nav-sub-item" 
                :class="{ active: $route.path === '/admin/feature' }"
              >
                <span class="nav-icon">⭐</span>
                <span class="nav-text">特色优势管理</span>
              </router-link>
            </div>
          </transition>
        </div>

        <div class="nav-group">
          <div 
            class="nav-item nav-group-header" 
            :class="{ active: isAboutMenuActive, expanded: isAboutMenuExpanded }"
            @click="toggleAboutMenu"
          >
            <span class="nav-icon">📖</span>
            <span class="nav-text" v-if="!isSidebarCollapsed">关于我们管理</span>
            <span class="nav-expand-icon" v-if="!isSidebarCollapsed">{{ isAboutMenuExpanded ? '▼' : '▶' }}</span>
          </div>
          <transition name="menu-expand">
            <div 
              class="nav-group-items" 
              v-show="isAboutMenuExpanded && !isSidebarCollapsed"
              :style="{ maxHeight: isAboutMenuExpanded ? '300px' : '0px', opacity: isAboutMenuExpanded ? '1' : '0' }"
            >
              <router-link 
                to="/admin/about-mission" 
                class="nav-item nav-sub-item" 
                :class="{ active: $route.path === '/admin/about-mission' }"
              >
                <span class="nav-icon">🎯</span>
                <span class="nav-text">使命管理</span>
              </router-link>
              <router-link 
                to="/admin/about-value" 
                class="nav-item nav-sub-item" 
                :class="{ active: $route.path === '/admin/about-value' }"
              >
                <span class="nav-icon">💎</span>
                <span class="nav-text">价值观管理</span>
              </router-link>
              <router-link 
                to="/admin/about-stat" 
                class="nav-item nav-sub-item" 
                :class="{ active: $route.path === '/admin/about-stat' }"
              >
                <span class="nav-icon">📊</span>
                <span class="nav-text">平台数据管理</span>
              </router-link>
              <router-link 
                to="/admin/about-team" 
                class="nav-item nav-sub-item" 
                :class="{ active: $route.path === '/admin/about-team' }"
              >
                <span class="nav-icon">👥</span>
                <span class="nav-text">核心团队管理</span>
              </router-link>
            </div>
          </transition>
        </div>
      </nav>
      
      <div class="sidebar-footer">
        <div class="user-info" v-if="!isSidebarCollapsed">
          <span class="user-icon">👤</span>
          <div class="user-details">
            <span class="user-name">{{ username }}</span>
            <span class="user-role">管理员</span>
          </div>
        </div>
        <button 
          class="logout-btn" 
          @click="handleLogout"
          :disabled="isSidebarCollapsed"
          :class="{ disabled: isSidebarCollapsed }"
        >
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
          <button class="change-password-btn" @click="openPasswordModal">
            🔐 修改密码
          </button>
        </div>
      </header>
      
      <div class="admin-content">
        <router-view />
      </div>
    </main>
    
    <!-- 修改密码模态框 -->
    <div class="modal-overlay" v-if="showPasswordModal" @click="closePasswordModal">
      <div class="modal-content" @click.stop>
        <div class="modal-header">
          <h3>修改管理员密码</h3>
          <button class="modal-close" @click="closePasswordModal">×</button>
        </div>
        <div class="modal-body">
          <div class="form-group">
            <label>原密码</label>
            <input 
              type="password" 
              v-model="passwordForm.oldPassword" 
              placeholder="请输入原密码"
              class="form-input"
            />
          </div>
          <div class="form-group">
            <label>新密码</label>
            <input 
              type="password" 
              v-model="passwordForm.newPassword" 
              placeholder="请输入新密码（6-16位）"
              class="form-input"
            />
          </div>
          <div class="form-group">
            <label>确认新密码</label>
            <input 
              type="password" 
              v-model="passwordForm.confirmPassword" 
              placeholder="请再次输入新密码"
              class="form-input"
            />
          </div>
          <p class="error-message" v-if="errorMessage">{{ errorMessage }}</p>
          <p class="success-message" v-if="successMessage">{{ successMessage }}</p>
        </div>
        <div class="modal-footer">
          <button class="btn btn-cancel" @click="closePasswordModal">取消</button>
          <button class="btn btn-confirm" @click="submitPasswordChange" :disabled="isSubmitting">
            {{ isSubmitting ? '提交中...' : '确认修改' }}
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { removeToken, request } from '../../utils/request'

const route = useRoute()
const router = useRouter()

const isSidebarCollapsed = ref(false)
const username = ref(localStorage.getItem('username') || '管理员')
const isHomeMenuExpanded = ref(true)
const isServiceMenuExpanded = ref(true)
const isForumMenuExpanded = ref(true)
const isOrderMenuExpanded = ref(true)
const isContactMenuExpanded = ref(true)
const isAboutMenuExpanded = ref(true)

const showPasswordModal = ref(false)
const isSubmitting = ref(false)
const errorMessage = ref('')
const successMessage = ref('')
const passwordForm = ref({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const isHomeMenuActive = computed(() => {
  return route.path === '/admin/carousel' || route.path === '/admin/intro' || route.path === '/admin/announcement'
})

const isServiceMenuActive = computed(() => {
  return route.path === '/admin/service-category' || route.path === '/admin/service'
})

const isForumMenuActive = computed(() => {
  return route.path === '/admin/forum-category' || route.path === '/admin/forum-post'
})

const isOrderMenuActive = computed(() => {
  return route.path === '/admin/order'
})

const isContactMenuActive = computed(() => {
  return route.path === '/admin/message' ||
    route.path === '/admin/contact-card' ||
    route.path === '/admin/working-hour' ||
    route.path === '/admin/faq' ||
    route.path === '/admin/feature'
})

const isAboutMenuActive = computed(() => {
  return route.path === '/admin/about-mission' ||
    route.path === '/admin/about-value' ||
    route.path === '/admin/about-stat' ||
    route.path === '/admin/about-team'
})

const pageTitle = computed(() => {
  const path = route.path
  if (path === '/admin/carousel') return '轮播图管理'
  if (path === '/admin/intro') return '平台简介管理'
  if (path === '/admin/announcement') return '公告管理'
  if (path === '/admin/service-category') return '服务分类管理'
  if (path === '/admin/service') return '服务管理'
  if (path === '/admin/forum-category') return '论坛版块管理'
  if (path === '/admin/forum-post') return '帖子管理'
  if (path === '/admin/order') return '订单管理'
  if (path === '/admin/message') return '留言管理'
  if (path === '/admin/contact-card') return '联系方式管理'
  if (path === '/admin/working-hour') return '工作时间管理'
  if (path === '/admin/faq') return '常见问题管理'
  if (path === '/admin/feature') return '特色优势管理'
  if (path === '/admin/about-mission') return '使命管理'
  if (path === '/admin/about-value') return '价值观管理'
  if (path === '/admin/about-stat') return '平台数据管理'
  if (path === '/admin/about-team') return '核心团队管理'
  if (path === '/admin/home') return '管理后台首页'
  return '管理后台'
})

const toggleSidebar = () => {
  isSidebarCollapsed.value = !isSidebarCollapsed.value
}

const toggleHomeMenu = () => {
  if (!isSidebarCollapsed.value) {
    isHomeMenuExpanded.value = !isHomeMenuExpanded.value
  }
}

const toggleServiceMenu = () => {
  if (!isSidebarCollapsed.value) {
    isServiceMenuExpanded.value = !isServiceMenuExpanded.value
  }
}

const toggleForumMenu = () => {
  if (!isSidebarCollapsed.value) {
    isForumMenuExpanded.value = !isForumMenuExpanded.value
  }
}

const toggleOrderMenu = () => {
  if (!isSidebarCollapsed.value) {
    isOrderMenuExpanded.value = !isOrderMenuExpanded.value
  }
}

const toggleContactMenu = () => {
  if (!isSidebarCollapsed.value) {
    isContactMenuExpanded.value = !isContactMenuExpanded.value
  }
}

const toggleAboutMenu = () => {
  if (!isSidebarCollapsed.value) {
    isAboutMenuExpanded.value = !isAboutMenuExpanded.value
  }
}

const handleLogout = () => {
  if (isSidebarCollapsed.value) return
  localStorage.removeItem('isLoggedIn')
  localStorage.removeItem('username')
  localStorage.removeItem('nickname')
  localStorage.removeItem('userType')
  removeToken()
  router.push('/login')
}

const openPasswordModal = () => {
  showPasswordModal.value = true
  errorMessage.value = ''
  successMessage.value = ''
  passwordForm.value = {
    oldPassword: '',
    newPassword: '',
    confirmPassword: ''
  }
}

const closePasswordModal = () => {
  showPasswordModal.value = false
  errorMessage.value = ''
  successMessage.value = ''
}

const validatePasswordForm = () => {
  if (!passwordForm.value.oldPassword) {
    errorMessage.value = '请输入原密码'
    return false
  }
  if (!passwordForm.value.newPassword) {
    errorMessage.value = '请输入新密码'
    return false
  }
  if (passwordForm.value.newPassword.length < 6 || passwordForm.value.newPassword.length > 16) {
    errorMessage.value = '新密码长度必须为6-16位'
    return false
  }
  if (!passwordForm.value.confirmPassword) {
    errorMessage.value = '请输入确认密码'
    return false
  }
  if (passwordForm.value.newPassword !== passwordForm.value.confirmPassword) {
    errorMessage.value = '两次输入的新密码不一致'
    return false
  }
  return true
}

const submitPasswordChange = async () => {
  errorMessage.value = ''
  successMessage.value = ''
  
  if (!validatePasswordForm()) {
    return
  }
  
  isSubmitting.value = true
  
  try {
    const response = await request('/api/auth/update-password', {
      method: 'POST',
      body: JSON.stringify({
        oldPassword: passwordForm.value.oldPassword,
        newPassword: passwordForm.value.newPassword,
        confirmPassword: passwordForm.value.confirmPassword
      })
    })
    
    const data = await response.json()
    
    if (data.code === 200 || data.success) {
      successMessage.value = '密码修改成功！'
      setTimeout(() => {
        closePasswordModal()
      }, 1500)
    } else {
      errorMessage.value = data.message || '密码修改失败'
    }
  } catch (error) {
    errorMessage.value = '网络错误，请稍后重试'
  } finally {
    isSubmitting.value = false
  }
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

.nav-group {
  display: flex;
  flex-direction: column;
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
  cursor: pointer;
}

.nav-item:hover {
  background: rgba(255, 255, 255, 0.1);
  color: white;
}

.nav-item.active {
  background: linear-gradient(135deg, #6B8E23, #8FBC8F);
  color: white;
}

.nav-group-header {
  justify-content: space-between;
}

.nav-group-header.expanded {
  background: rgba(255, 255, 255, 0.05);
}

.nav-group-items {
  display: flex;
  flex-direction: column;
  gap: 4px;
  margin-top: 4px;
  padding-left: 20px;
  overflow: hidden;
  transition: max-height 0.4s cubic-bezier(0.4, 0, 0.2, 1), 
              opacity 0.3s ease,
              margin-top 0.3s ease,
              padding-top 0.3s ease;
  max-height: 0;
  opacity: 0;
}

.nav-group-items[style*="max-height: 200px"] {
  max-height: 200px;
  opacity: 1;
  margin-top: 4px;
  padding-top: 0;
}

.menu-expand-enter-active,
.menu-expand-leave-active {
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
}

.menu-expand-enter-from,
.menu-expand-leave-to {
  opacity: 0;
  max-height: 0;
  margin-top: 0;
  padding-top: 0;
}

.menu-expand-enter-to,
.menu-expand-leave-from {
  opacity: 1;
  max-height: 200px;
  margin-top: 4px;
  padding-top: 0;
}

.nav-sub-item {
  padding: 10px 16px;
  font-size: 0.9rem;
}

.nav-icon {
  font-size: 1.3rem;
  width: 24px;
  text-align: center;
}

.nav-text {
  font-size: 0.95rem;
  font-weight: 500;
  flex: 1;
}

.nav-expand-icon {
  font-size: 0.7rem;
  transition: transform 0.4s cubic-bezier(0.4, 0, 0.2, 1);
  display: inline-block;
}

.nav-group-header.expanded .nav-expand-icon {
  transform: rotate(180deg);
}

.nav-group-header:not(.expanded) .nav-expand-icon {
  transform: rotate(0deg);
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

.logout-btn:hover:not(.disabled) {
  background: rgba(196, 91, 26, 0.8);
}

.logout-btn.disabled {
  opacity: 0.5;
  cursor: not-allowed;
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

.change-password-btn {
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

.change-password-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 5px 15px rgba(107, 142, 35, 0.3);
}

.admin-content {
  flex: 1;
  padding: 30px;
}

/* 模态框样式 */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.modal-content {
  background: white;
  border-radius: 12px;
  width: 450px;
  max-width: 90%;
  max-height: 90vh;
  overflow-y: auto;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 24px;
  border-bottom: 1px solid #eee;
}

.modal-header h3 {
  margin: 0;
  color: #2c3e50;
  font-size: 1.2rem;
}

.modal-close {
  background: none;
  border: none;
  font-size: 1.5rem;
  color: #999;
  cursor: pointer;
  line-height: 1;
  padding: 0;
  width: 30px;
  height: 30px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  transition: all 0.3s ease;
}

.modal-close:hover {
  background: #f5f5f5;
  color: #333;
}

.modal-body {
  padding: 24px;
}

.form-group {
  margin-bottom: 20px;
}

.form-group label {
  display: block;
  margin-bottom: 8px;
  color: #2c3e50;
  font-weight: 500;
  font-size: 0.95rem;
}

.form-input {
  width: 100%;
  padding: 12px 16px;
  border: 1px solid #ddd;
  border-radius: 8px;
  font-size: 0.95rem;
  transition: all 0.3s ease;
  box-sizing: border-box;
}

.form-input:focus {
  outline: none;
  border-color: #6B8E23;
  box-shadow: 0 0 0 3px rgba(107, 142, 35, 0.1);
}

.error-message {
  color: #e74c3c;
  font-size: 0.9rem;
  margin-top: 10px;
  padding: 10px;
  background: #fdf2f2;
  border-radius: 6px;
  border-left: 3px solid #e74c3c;
}

.success-message {
  color: #27ae60;
  font-size: 0.9rem;
  margin-top: 10px;
  padding: 10px;
  background: #f0fdf4;
  border-radius: 6px;
  border-left: 3px solid #27ae60;
}

.modal-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  padding: 20px 24px;
  border-top: 1px solid #eee;
}

.btn {
  padding: 10px 24px;
  border-radius: 8px;
  font-size: 0.95rem;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;
  border: none;
}

.btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.btn-cancel {
  background: #f5f5f5;
  color: #666;
}

.btn-cancel:hover {
  background: #e8e8e8;
}

.btn-confirm {
  background: linear-gradient(135deg, #6B8E23, #8FBC8F);
  color: white;
}

.btn-confirm:hover:not(:disabled) {
  transform: translateY(-1px);
  box-shadow: 0 5px 15px rgba(107, 142, 35, 0.3);
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
  .user-info,
  .nav-expand-icon,
  .nav-group-items {
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
  
  .modal-content {
    width: 95%;
    margin: 10px;
  }
}
</style>