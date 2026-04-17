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
        <div class="user-section">
          <template v-if="isLoggedIn">
            <div class="user-info-group">
              <button class="profile-btn" @click="openProfileModal">
                <span class="profile-icon">👤</span>
                <span class="profile-text">{{ username }}</span>
              </button>
              <button class="logout-btn" @click="handleLogout">退出</button>
            </div>
          </template>
          <template v-else>
            <router-link to="/login" class="login-link">登录</router-link>
          </template>
        </div>
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
        <div class="mobile-user-section" v-if="isLoggedIn">
          <span class="mobile-username">👤 {{ username }}</span>
          <button class="mobile-profile-btn" @click="openProfileModal">个人信息</button>
          <button class="mobile-logout-btn" @click="handleLogout">退出登录</button>
        </div>
        <router-link to="/login" class="mobile-nav-link" v-else @click="closeMobileMenu">登录</router-link>
      </nav>
    </div>
    
    <div class="modal-overlay" :class="{ show: showProfileModal }" @click="closeProfileModal">
      <div class="modal-container" @click.stop>
        <div class="modal-header">
          <h3 class="modal-title">{{ activeTab === 'profile' ? '个人信息' : '修改密码' }}</h3>
          <button class="modal-close-btn" @click="closeProfileModal">×</button>
        </div>
        
        <div class="modal-tabs">
          <button 
            class="modal-tab" 
            :class="{ active: activeTab === 'profile' }"
            @click="switchTab('profile')"
          >
            个人信息
          </button>
          <button 
            class="modal-tab" 
            :class="{ active: activeTab === 'password' }"
            @click="switchTab('password')"
          >
            修改密码
          </button>
        </div>
        
        <div class="modal-body" v-if="userInfo">
          <div v-if="activeTab === 'profile'" class="tab-content">
            <div class="avatar-section">
              <div class="avatar-circle">
                <span class="avatar-text">{{ getAvatarText() }}</span>
              </div>
            </div>
            
            <div class="info-section">
              <div class="info-item">
                <span class="info-label">用户ID</span>
                <span class="info-value">{{ userInfo.id }}</span>
              </div>
              
              <div class="info-item editable" :class="{ editing: isEditing }">
                <span class="info-label">用户名</span>
                <template v-if="!isEditing">
                  <span class="info-value">{{ editForm.newUsername || userInfo.username }}</span>
                </template>
                <input 
                  v-else 
                  type="text" 
                  v-model="editForm.newUsername"
                  class="info-input"
                  placeholder="请输入新用户名"
                />
              </div>
              
              <div class="info-item editable" :class="{ editing: isEditing }">
                <span class="info-label">昵称</span>
                <template v-if="!isEditing">
                  <span class="info-value">{{ editForm.nickname || userInfo.nickname || userInfo.username }}</span>
                </template>
                <input 
                  v-else 
                  type="text" 
                  v-model="editForm.nickname"
                  class="info-input"
                  placeholder="请输入昵称"
                />
              </div>
              
              <div class="info-item">
                <span class="info-label">账户状态</span>
                <span class="info-value status-badge" :class="{ active: userInfo.status === 1 }">
                  {{ userInfo.status === 1 ? '正常' : '禁用' }}
                </span>
              </div>
              
              <div class="info-item">
                <span class="info-label">注册时间</span>
                <span class="info-value">{{ formatDate(userInfo.createTime) }}</span>
              </div>
            </div>
            
            <div class="action-buttons">
              <button 
                v-if="!isEditing" 
                class="btn-primary"
                @click="startEditing"
              >
                编辑信息
              </button>
              <template v-else>
                <button 
                  class="btn-secondary"
                  @click="cancelEditing"
                >
                  取消
                </button>
                <button 
                  class="btn-primary"
                  @click="saveUserInfo"
                  :disabled="isSaving"
                >
                  {{ isSaving ? '保存中...' : '保存' }}
                </button>
              </template>
            </div>
            
            <div v-if="editError" class="error-message">{{ editError }}</div>
            <div v-if="editSuccess" class="success-message">{{ editSuccess }}</div>
          </div>
          
          <div v-else class="tab-content">
            <div class="form-section">
              <div class="form-group">
                <label class="form-label">原密码</label>
                <input 
                  type="password" 
                  v-model="passwordForm.oldPassword"
                  class="form-input"
                  placeholder="请输入原密码"
                />
              </div>
              
              <div class="form-group">
                <label class="form-label">新密码</label>
                <input 
                  type="password" 
                  v-model="passwordForm.newPassword"
                  class="form-input"
                  placeholder="请输入新密码（6-16位）"
                />
              </div>
              
              <div class="form-group">
                <label class="form-label">确认新密码</label>
                <input 
                  type="password" 
                  v-model="passwordForm.confirmPassword"
                  class="form-input"
                  placeholder="请再次输入新密码"
                />
              </div>
            </div>
            
            <div class="action-buttons">
              <button 
                class="btn-primary"
                @click="savePassword"
                :disabled="isSavingPassword"
              >
                {{ isSavingPassword ? '修改中...' : '确认修改' }}
              </button>
            </div>
            
            <div v-if="passwordError" class="error-message">{{ passwordError }}</div>
            <div v-if="passwordSuccess" class="success-message">{{ passwordSuccess }}</div>
          </div>
        </div>
        
        <div class="modal-body" v-else>
          <div class="loading-container">
            <div class="loading-spinner"></div>
            <span class="loading-text">加载中...</span>
          </div>
        </div>
      </div>
    </div>
  </header>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const isMobileMenuOpen = ref(false)
const isLoggedIn = ref(false)
const username = ref('')
const showProfileModal = ref(false)
const userInfo = ref(null)
const activeTab = ref('profile')
const isEditing = ref(false)
const isSaving = ref(false)
const isSavingPassword = ref(false)
const editError = ref('')
const editSuccess = ref('')
const passwordError = ref('')
const passwordSuccess = ref('')

const editForm = reactive({
  newUsername: '',
  nickname: ''
})

const passwordForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const checkLoginStatus = () => {
  isLoggedIn.value = localStorage.getItem('isLoggedIn') === 'true'
  const nickname = localStorage.getItem('nickname')
  const storedUsername = localStorage.getItem('username') || ''
  username.value = nickname || storedUsername
}

const toggleMobileMenu = () => {
  isMobileMenuOpen.value = !isMobileMenuOpen.value
}

const closeMobileMenu = () => {
  isMobileMenuOpen.value = false
}

const handleLogout = () => {
  localStorage.removeItem('isLoggedIn')
  localStorage.removeItem('username')
  localStorage.removeItem('nickname')
  isLoggedIn.value = false
  username.value = ''
  userInfo.value = null
  closeMobileMenu()
  closeProfileModal()
  router.push('/login')
}

const openProfileModal = async () => {
  closeMobileMenu()
  showProfileModal.value = true
  userInfo.value = null
  activeTab.value = 'profile'
  resetForms()
  
  const storedUsername = localStorage.getItem('username')
  if (storedUsername) {
    try {
      const response = await fetch(`http://localhost:9090/api/auth/user-info?username=${encodeURIComponent(storedUsername)}`)
      const data = await response.json()
      
      if (data.code === 200) {
        userInfo.value = data.data
        editForm.newUsername = data.data.username || ''
        editForm.nickname = data.data.nickname || ''
      }
    } catch (error) {
      console.error('获取用户信息失败:', error)
      userInfo.value = {
        id: '-',
        username: localStorage.getItem('username') || '',
        nickname: localStorage.getItem('nickname') || localStorage.getItem('username') || '',
        status: 1,
        createTime: new Date().toISOString()
      }
      editForm.newUsername = userInfo.value.username
      editForm.nickname = userInfo.value.nickname
    }
  }
}

const closeProfileModal = () => {
  showProfileModal.value = false
  isEditing.value = false
  resetForms()
}

const resetForms = () => {
  editError.value = ''
  editSuccess.value = ''
  passwordError.value = ''
  passwordSuccess.value = ''
  passwordForm.oldPassword = ''
  passwordForm.newPassword = ''
  passwordForm.confirmPassword = ''
}

const switchTab = (tab) => {
  activeTab.value = tab
  resetForms()
}

const getAvatarText = () => {
  if (!userInfo.value) return '?'
  const name = userInfo.value.nickname || userInfo.value.username
  return name ? name.charAt(0).toUpperCase() : '?'
}

const startEditing = () => {
  isEditing.value = true
  editError.value = ''
  editSuccess.value = ''
}

const cancelEditing = () => {
  isEditing.value = false
  if (userInfo.value) {
    editForm.newUsername = userInfo.value.username
    editForm.nickname = userInfo.value.nickname
  }
  editError.value = ''
  editSuccess.value = ''
}

const saveUserInfo = async () => {
  editError.value = ''
  editSuccess.value = ''
  
  if (!editForm.newUsername.trim()) {
    editError.value = '用户名不能为空'
    return
  }
  
  if (editForm.newUsername.length < 3 || editForm.newUsername.length > 50) {
    editError.value = '用户名长度必须为3-50位'
    return
  }
  
  isSaving.value = true
  
  try {
    const storedUsername = localStorage.getItem('username')
    const response = await fetch('http://localhost:9090/api/auth/update-info', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({
        currentUsername: storedUsername,
        newUsername: editForm.newUsername,
        nickname: editForm.nickname
      })
    })
    
    const data = await response.json()
    
    if (data.code === 200) {
      editSuccess.value = '修改成功！'
      isEditing.value = false
      
      localStorage.setItem('username', editForm.newUsername)
      if (editForm.nickname) {
        localStorage.setItem('nickname', editForm.nickname)
      } else {
        localStorage.setItem('nickname', editForm.newUsername)
      }
      
      username.value = editForm.nickname || editForm.newUsername
      
      if (userInfo.value) {
        userInfo.value.username = editForm.newUsername
        userInfo.value.nickname = editForm.nickname || editForm.newUsername
      }
    } else {
      editError.value = data.message || '修改失败，请重试'
    }
  } catch (error) {
    console.error('修改用户信息失败:', error)
    editError.value = '网络错误，请稍后重试'
  } finally {
    isSaving.value = false
  }
}

const savePassword = async () => {
  passwordError.value = ''
  passwordSuccess.value = ''
  
  if (!passwordForm.oldPassword) {
    passwordError.value = '请输入原密码'
    return
  }
  
  if (!passwordForm.newPassword) {
    passwordError.value = '请输入新密码'
    return
  }
  
  if (passwordForm.newPassword.length < 6 || passwordForm.newPassword.length > 16) {
    passwordError.value = '新密码长度必须为6-16位'
    return
  }
  
  if (passwordForm.newPassword !== passwordForm.confirmPassword) {
    passwordError.value = '两次输入的新密码不一致'
    return
  }
  
  if (passwordForm.oldPassword === passwordForm.newPassword) {
    passwordError.value = '新密码不能与原密码相同'
    return
  }
  
  isSavingPassword.value = true
  
  try {
    const storedUsername = localStorage.getItem('username')
    const response = await fetch('http://localhost:9090/api/auth/update-password', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({
        username: storedUsername,
        oldPassword: passwordForm.oldPassword,
        newPassword: passwordForm.newPassword,
        confirmPassword: passwordForm.confirmPassword
      })
    })
    
    const data = await response.json()
    
    if (data.code === 200) {
      passwordSuccess.value = '密码修改成功！'
      passwordForm.oldPassword = ''
      passwordForm.newPassword = ''
      passwordForm.confirmPassword = ''
    } else {
      passwordError.value = data.message || '密码修改失败，请重试'
    }
  } catch (error) {
    console.error('修改密码失败:', error)
    passwordError.value = '网络错误，请稍后重试'
  } finally {
    isSavingPassword.value = false
  }
}

const formatDate = (dateStr) => {
  if (!dateStr) return '-'
  const date = new Date(dateStr)
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  const hours = String(date.getHours()).padStart(2, '0')
  const minutes = String(date.getMinutes()).padStart(2, '0')
  const seconds = String(date.getSeconds()).padStart(2, '0')
  return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`
}

onMounted(() => {
  checkLoginStatus()
})
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

.user-section {
  display: flex;
  align-items: center;
  gap: 15px;
}

.user-info-group {
  display: flex;
  align-items: center;
  gap: 10px;
}

.profile-btn {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 16px;
  background: linear-gradient(135deg, #3498db, #2ecc71);
  color: white;
  border: none;
  border-radius: 8px;
  font-size: 0.95rem;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;
}

.profile-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 5px 15px rgba(52, 152, 219, 0.3);
}

.profile-icon {
  font-size: 1rem;
}

.profile-text {
  flex: 1;
}

.logout-btn {
  padding: 8px 20px;
  background: linear-gradient(135deg, #e74c3c, #c0392b);
  color: white;
  border: none;
  border-radius: 8px;
  font-size: 0.95rem;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;
}

.logout-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 5px 15px rgba(231, 76, 60, 0.3);
}

.login-link {
  padding: 8px 20px;
  background: linear-gradient(135deg, #3498db, #2ecc71);
  color: white;
  border-radius: 8px;
  font-size: 0.95rem;
  font-weight: 500;
  transition: all 0.3s ease;
}

.login-link:hover {
  transform: translateY(-2px);
  box-shadow: 0 5px 15px rgba(52, 152, 219, 0.3);
}

.mobile-user-section {
  padding: 15px 20px;
  border-top: 1px solid #e4e8eb;
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.mobile-username {
  font-size: 0.95rem;
  color: #2c3e50;
  font-weight: 500;
}

.mobile-profile-btn {
  padding: 10px 16px;
  background: linear-gradient(135deg, #3498db, #2ecc71);
  color: white;
  border: none;
  border-radius: 8px;
  font-size: 0.9rem;
  cursor: pointer;
  transition: all 0.3s ease;
  text-align: center;
}

.mobile-profile-btn:hover {
  background: linear-gradient(135deg, #2980b9, #27ae60);
}

.mobile-logout-btn {
  padding: 10px 16px;
  background: linear-gradient(135deg, #e74c3c, #c0392b);
  color: white;
  border: none;
  border-radius: 8px;
  font-size: 0.9rem;
  cursor: pointer;
  transition: all 0.3s ease;
  text-align: center;
}

.mobile-logout-btn:hover {
  background: linear-gradient(135deg, #c0392b, #a93226);
}

.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 2000;
  opacity: 0;
  visibility: hidden;
  transition: all 0.3s ease;
}

.modal-overlay.show {
  opacity: 1;
  visibility: visible;
}

.modal-container {
  background: white;
  border-radius: 16px;
  width: 90%;
  max-width: 480px;
  max-height: 85vh;
  overflow-y: auto;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
  transform: scale(0.9) translateY(20px);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.modal-overlay.show .modal-container {
  transform: scale(1) translateY(0);
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 24px;
  border-bottom: 1px solid #e4e8eb;
}

.modal-title {
  font-size: 1.3rem;
  font-weight: 600;
  color: #2c3e50;
  margin: 0;
}

.modal-close-btn {
  width: 36px;
  height: 36px;
  display: flex;
  justify-content: center;
  align-items: center;
  background: #f5f7fa;
  border: none;
  border-radius: 50%;
  font-size: 1.5rem;
  color: #7f8c8d;
  cursor: pointer;
  transition: all 0.3s ease;
}

.modal-close-btn:hover {
  background: #e74c3c;
  color: white;
  transform: rotate(90deg);
}

.modal-tabs {
  display: flex;
  border-bottom: 1px solid #e4e8eb;
  padding: 0 24px;
}

.modal-tab {
  padding: 14px 24px;
  background: none;
  border: none;
  font-size: 0.95rem;
  font-weight: 500;
  color: #7f8c8d;
  cursor: pointer;
  position: relative;
  transition: all 0.3s ease;
}

.modal-tab::after {
  content: '';
  position: absolute;
  bottom: -1px;
  left: 0;
  width: 100%;
  height: 2px;
  background: linear-gradient(135deg, #3498db, #2ecc71);
  transform: scaleX(0);
  transition: transform 0.3s ease;
}

.modal-tab.active {
  color: #3498db;
}

.modal-tab.active::after {
  transform: scaleX(1);
}

.modal-tab:hover {
  color: #3498db;
}

.modal-body {
  padding: 24px;
}

.tab-content {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.loading-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 40px;
  gap: 15px;
}

.loading-spinner {
  width: 40px;
  height: 40px;
  border: 3px solid #e4e8eb;
  border-top-color: #3498db;
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

.loading-text {
  font-size: 0.95rem;
  color: #7f8c8d;
}

.avatar-section {
  display: flex;
  justify-content: center;
  margin-bottom: 8px;
}

.avatar-circle {
  width: 90px;
  height: 90px;
  border-radius: 50%;
  background: linear-gradient(135deg, #3498db, #2ecc71);
  display: flex;
  justify-content: center;
  align-items: center;
  box-shadow: 0 8px 25px rgba(52, 152, 219, 0.3);
}

.avatar-text {
  font-size: 2.5rem;
  font-weight: bold;
  color: white;
}

.info-section {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.info-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 14px 16px;
  background: #f8f9fa;
  border-radius: 10px;
  transition: all 0.3s ease;
}

.info-item:hover {
  background: #f0f3f5;
}

.info-item.editable {
  cursor: pointer;
}

.info-item.editable.editing {
  background: #fff;
  border: 1px solid #3498db;
  box-shadow: 0 0 0 3px rgba(52, 152, 219, 0.1);
}

.info-label {
  font-size: 0.95rem;
  color: #7f8c8d;
  font-weight: 500;
}

.info-value {
  font-size: 0.95rem;
  color: #2c3e50;
  font-weight: 600;
}

.info-input {
  flex: 1;
  max-width: 180px;
  padding: 8px 12px;
  border: 1px solid #e4e8eb;
  border-radius: 6px;
  font-size: 0.95rem;
  color: #2c3e50;
  outline: none;
  transition: all 0.3s ease;
}

.info-input:focus {
  border-color: #3498db;
  box-shadow: 0 0 0 3px rgba(52, 152, 219, 0.1);
}

.status-badge {
  padding: 4px 12px;
  border-radius: 20px;
  font-size: 0.85rem;
  background: #e74c3c;
  color: white;
}

.status-badge.active {
  background: #27ae60;
}

.form-section {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.form-label {
  font-size: 0.95rem;
  font-weight: 500;
  color: #2c3e50;
}

.form-input {
  padding: 12px 16px;
  border: 2px solid #e4e8eb;
  border-radius: 10px;
  font-size: 1rem;
  color: #2c3e50;
  outline: none;
  transition: all 0.3s ease;
}

.form-input:focus {
  border-color: #3498db;
  box-shadow: 0 0 0 4px rgba(52, 152, 219, 0.1);
}

.form-input::placeholder {
  color: #bdc3c7;
}

.action-buttons {
  display: flex;
  gap: 12px;
  justify-content: flex-end;
  margin-top: 8px;
}

.btn-primary {
  padding: 12px 28px;
  background: linear-gradient(135deg, #3498db, #2ecc71);
  color: white;
  border: none;
  border-radius: 10px;
  font-size: 0.95rem;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
}

.btn-primary:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 5px 15px rgba(52, 152, 219, 0.3);
}

.btn-primary:disabled {
  opacity: 0.7;
  cursor: not-allowed;
}

.btn-secondary {
  padding: 12px 28px;
  background: #f5f7fa;
  color: #7f8c8d;
  border: 1px solid #e4e8eb;
  border-radius: 10px;
  font-size: 0.95rem;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;
}

.btn-secondary:hover {
  background: #e8ecef;
  color: #555;
}

.error-message {
  background: #fdf2f2;
  border: 1px solid #fecaca;
  color: #dc2626;
  padding: 12px 16px;
  border-radius: 8px;
  font-size: 0.9rem;
  text-align: center;
}

.success-message {
  background: #f0fdf4;
  border: 1px solid #bbf7d0;
  color: #16a34a;
  padding: 12px 16px;
  border-radius: 8px;
  font-size: 0.9rem;
  text-align: center;
}

@media (max-width: 768px) {
  .user-section {
    display: none;
  }
  .nav {
    display: none;
  }

  .mobile-menu-btn {
    display: flex;
  }

  .mobile-menu {
    display: block;
  }

  .modal-container {
    width: 95%;
    margin: 10px;
    max-height: 90vh;
  }

  .modal-header {
    padding: 16px 20px;
  }

  .modal-tabs {
    padding: 0 20px;
  }

  .modal-tab {
    padding: 12px 16px;
    font-size: 0.9rem;
  }

  .modal-body {
    padding: 20px;
  }

  .avatar-circle {
    width: 80px;
    height: 80px;
  }

  .avatar-text {
    font-size: 2.2rem;
  }

  .info-item {
    flex-direction: column;
    align-items: flex-start;
    gap: 8px;
  }

  .info-item.editing {
    flex-direction: column;
  }

  .info-input {
    max-width: 100%;
    width: 100%;
  }

  .action-buttons {
    flex-direction: column;
  }

  .btn-primary,
  .btn-secondary {
    width: 100%;
  }
}
</style>
