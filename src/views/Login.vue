<template>
  <div class="login-container">
    <div class="login-card">
      <div class="login-header">
        <span class="logo-icon">🏠</span>
        <h1 class="login-title">社区生活服务</h1>
        <p class="login-subtitle">欢迎回来，请登录您的账户</p>
      </div>
      <form class="login-form" @submit.prevent="handleLogin">
        <div class="form-group">
          <label class="form-label" for="username">用户名</label>
          <input 
            type="text" 
            id="username"
            v-model="username" 
            class="form-input"
            placeholder="请输入用户名"
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
        <div v-if="errorMessage" class="error-message">
          {{ errorMessage }}
        </div>
        <button 
          type="submit" 
          class="login-btn"
          :disabled="isLoading"
        >
          <span v-if="!isLoading">登录</span>
          <span v-else>登录中...</span>
        </button>
      </form>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const username = ref('')
const password = ref('')
const errorMessage = ref('')
const isLoading = ref(false)

const handleLogin = () => {
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
  
  setTimeout(() => {
    if (username.value === 'user_01' && password.value === '123456') {
      localStorage.setItem('isLoggedIn', 'true')
      localStorage.setItem('username', username.value)
      router.push('/')
    } else {
      errorMessage.value = '用户名或密码错误'
      isLoading.value = false
    }
  }, 500)
}
</script>

<style scoped>
.login-container {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 20px;
}

.login-card {
  background: white;
  border-radius: 20px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
  padding: 40px;
  width: 100%;
  max-width: 420px;
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

.login-title {
  font-size: 1.8rem;
  color: #2c3e50;
  margin-bottom: 10px;
  background: linear-gradient(135deg, #3498db, #2ecc71);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.login-subtitle {
  font-size: 1rem;
  color: #7f8c8d;
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
  border-color: #3498db;
  box-shadow: 0 0 0 4px rgba(52, 152, 219, 0.1);
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
  background: linear-gradient(135deg, #3498db, #2ecc71);
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
  box-shadow: 0 10px 25px rgba(52, 152, 219, 0.3);
}

.login-btn:disabled {
  opacity: 0.7;
  cursor: not-allowed;
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