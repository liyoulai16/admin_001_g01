<template>
  <div class="login-container">
    <div class="particles">
      <div v-for="i in 30" :key="i" class="particle" :style="getParticleStyle(i)"></div>
    </div>
    <div class="login-card">
      <div class="login-header">
        <span class="logo-icon animate-float">🏠</span>
        <h1 class="login-title animate-slide-up">社区生活服务</h1>
        <p class="login-subtitle animate-slide-up-delay">欢迎回来，请登录您的账户</p>
      </div>
      <form class="login-form animate-fade-in" @submit.prevent="handleLogin">
        <div class="form-group">
          <label class="form-label" for="username">用户名</label>
          <input 
            type="text" 
            id="username"
            v-model="username" 
            class="form-input"
            placeholder="请输入用户名（3-50位）"
            autocomplete="username"
          />
        </div>
        <div class="form-group">
          <label class="form-label" for="password">密码</label>
          <input 
            type="password" 
            id="password"
            v-model="password" 
            class="form-input"
            placeholder="请输入密码（6-16位）"
            autocomplete="current-password"
            @keyup.enter="handleLogin"
          />
        </div>
        <div v-if="errorMessage" class="error-message animate-shake">
          {{ errorMessage }}
        </div>
        <button 
          type="submit" 
          class="login-btn"
          :disabled="isLoading"
          :class="{ 'animate-pulse': isLoading }"
        >
          <span v-if="!isLoading">登录</span>
          <span v-else>登录中...</span>
        </button>
      </form>
      <div class="login-footer animate-fade-in">
        <p>还没有账户？</p>
        <router-link to="/register" class="register-link">立即注册</router-link>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { setToken } from '../utils/request'

const router = useRouter()
const username = ref('')
const password = ref('')
const errorMessage = ref('')
const isLoading = ref(false)

const getParticleStyle = (index) => {
  const size = Math.random() * 8 + 4
  const left = Math.random() * 100
  const duration = Math.random() * 20 + 15
  const delay = Math.random() * 5
  
  return {
    width: `${size}px`,
    height: `${size}px`,
    left: `${left}%`,
    animationDuration: `${duration}s`,
    animationDelay: `${delay}s`,
    opacity: Math.random() * 0.5 + 0.2
  }
}

const handleLogin = async () => {
  errorMessage.value = ''
  
  if (!username.value.trim()) {
    errorMessage.value = '请输入用户名'
    return
  }
  
  if (!password.value) {
    errorMessage.value = '请输入密码'
    return
  }
  
  if (password.value.length < 6 || password.value.length > 16) {
    errorMessage.value = '密码长度必须为6-16位'
    return
  }
  
  isLoading.value = true
  
  try {
    const response = await fetch('http://localhost:9090/api/auth/login', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({
        username: username.value,
        password: password.value
      })
    })
    
    const data = await response.json()
    
    if (data.code === 200) {
      localStorage.setItem('isLoggedIn', 'true')
      localStorage.setItem('username', data.data.username)
      if (data.data.nickname) {
        localStorage.setItem('nickname', data.data.nickname)
      }
      if (data.data.token) {
        setToken(data.data.token)
      }
      localStorage.setItem('showLoginSuccess', 'true')
      router.push('/home')
    } else {
      errorMessage.value = data.message || '登录失败，请重试'
      isLoading.value = false
    }
  } catch (error) {
    console.error('登录请求失败:', error)
    errorMessage.value = '网络错误，请稍后重试'
    isLoading.value = false
  }
}
</script>

<style scoped>
.login-container {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #8B7355 0%, #6B8E23 100%);
  padding: 20px;
  position: relative;
  overflow: hidden;
}

.particles {
  position: absolute;
  width: 100%;
  height: 100%;
  top: 0;
  left: 0;
  overflow: hidden;
  pointer-events: none;
}

.particle {
  position: absolute;
  background: rgba(255, 255, 255, 0.15);
  border-radius: 4px;
  bottom: -100px;
  animation: float-up linear infinite;
}

@keyframes float-up {
  0% {
    transform: translateY(0) rotate(0deg);
    opacity: 0;
  }
  10% {
    opacity: 1;
  }
  90% {
    opacity: 1;
  }
  100% {
    transform: translateY(-100vh) rotate(720deg);
    opacity: 0;
  }
}

.login-card {
  background: white;
  border-radius: 20px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
  padding: 40px;
  width: 100%;
  max-width: 420px;
  position: relative;
  z-index: 10;
  animation: card-enter 0.8s cubic-bezier(0.4, 0, 0.2, 1);
}

@keyframes card-enter {
  0% {
    opacity: 0;
    transform: translateY(30px) scale(0.95);
  }
  100% {
    opacity: 1;
    transform: translateY(0) scale(1);
  }
}

.login-header {
  text-align: center;
  margin-bottom: 30px;
}

.logo-icon {
  font-size: 3rem;
  display: block;
  margin-bottom: 15px;
}

.animate-float {
  animation: float 3s ease-in-out infinite, icon-enter 0.6s cubic-bezier(0.4, 0, 0.2, 1) 0.1s both;
}

@keyframes float {
  0%, 100% {
    transform: translateY(0);
  }
  50% {
    transform: translateY(-10px);
  }
}

@keyframes icon-enter {
  0% {
    opacity: 0;
    transform: scale(0.5);
  }
  100% {
    opacity: 1;
    transform: scale(1);
  }
}

.login-title {
  font-size: 1.8rem;
  color: #2c3e50;
  margin-bottom: 10px;
  background: linear-gradient(135deg, #6B8E23, #8FBC8F);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.animate-slide-up {
  animation: slide-up 0.6s cubic-bezier(0.4, 0, 0.2, 1) 0.2s both;
}

.animate-slide-up-delay {
  animation: slide-up 0.6s cubic-bezier(0.4, 0, 0.2, 1) 0.3s both;
}

@keyframes slide-up {
  0% {
    opacity: 0;
    transform: translateY(20px);
  }
  100% {
    opacity: 1;
    transform: translateY(0);
  }
}

.login-subtitle {
  font-size: 1rem;
  color: #7f8c8d;
}

.animate-fade-in {
  animation: fade-in 0.6s cubic-bezier(0.4, 0, 0.2, 1) 0.4s both;
}

@keyframes fade-in {
  0% {
    opacity: 0;
  }
  100% {
    opacity: 1;
  }
}

.animate-shake {
  animation: shake 0.5s ease-in-out;
}

@keyframes shake {
  0%, 100% {
    transform: translateX(0);
  }
  10%, 30%, 50%, 70%, 90% {
    transform: translateX(-5px);
  }
  20%, 40%, 60%, 80% {
    transform: translateX(5px);
  }
}

.animate-pulse {
  animation: pulse 1.5s ease-in-out infinite;
}

@keyframes pulse {
  0%, 100% {
    opacity: 1;
  }
  50% {
    opacity: 0.7;
  }
}

.login-form {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.form-label {
  font-size: 0.95rem;
  font-weight: 600;
  color: #2c3e50;
}

.form-input {
  padding: 14px 18px;
  border: 2px solid #e4e8eb;
  border-radius: 10px;
  font-size: 1rem;
  transition: all 0.3s ease;
  outline: none;
}

.form-input:focus {
  border-color: #6B8E23;
  box-shadow: 0 0 0 4px rgba(107, 142, 35, 0.1);
}

.form-input::placeholder {
  color: #bdc3c7;
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

.login-btn {
  padding: 14px 30px;
  background: linear-gradient(135deg, #6B8E23, #8FBC8F);
  color: white;
  font-size: 1.1rem;
  font-weight: bold;
  border: none;
  border-radius: 10px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.login-btn:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 10px 25px rgba(107, 142, 35, 0.3);
}

.login-btn:disabled {
  opacity: 0.7;
  cursor: not-allowed;
}

.login-footer {
  text-align: center;
  margin-top: 20px;
  padding-top: 20px;
  border-top: 1px solid #e4e8eb;
}

.login-footer p {
  margin: 0;
  color: #7f8c8d;
  display: inline;
  margin-right: 5px;
}

.register-link {
  color: #6B8E23;
  text-decoration: none;
  font-weight: 600;
  transition: color 0.3s ease;
}

.register-link:hover {
  color: #8FBC8F;
  text-decoration: underline;
}

@media (max-width: 480px) {
  .login-card {
    padding: 30px 20px;
  }
  
  .login-title {
    font-size: 1.5rem;
  }
}
</style>