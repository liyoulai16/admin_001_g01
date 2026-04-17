<template>
  <div class="register-container">
    <div class="particles">
      <div v-for="i in 30" :key="i" class="particle" :style="getParticleStyle(i)"></div>
    </div>
    <div class="register-card">
      <div class="register-header">
        <span class="logo-icon animate-float">🏠</span>
        <h1 class="register-title animate-slide-up">社区生活服务</h1>
        <p class="register-subtitle animate-slide-up-delay">创建新账户，加入我们的社区</p>
      </div>
      <form class="register-form animate-fade-in" @submit.prevent="handleRegister">
        <div class="form-group">
          <label class="form-label" for="username">
            用户名
            <span class="char-count">{{ username.length }}/50</span>
          </label>
          <input 
            type="text" 
            id="username"
            v-model="username" 
            class="form-input"
            :class="{ 'input-error': usernameError || usernameExists, 'input-valid': usernameAvailable }"
            placeholder="请输入用户名（3-50位）"
            autocomplete="username"
            maxlength="50"
            @input="validateUsername"
            @blur="checkUsername"
          />
          <div v-if="isCheckingUsername" class="field-checking">
            正在检查用户名...
          </div>
          <div v-else-if="usernameAvailable" class="field-valid">
            ✓ 用户名可用
          </div>
          <div v-else-if="usernameExists" class="field-error">
            ✗ 用户名已存在
          </div>
          <div v-else-if="usernameError" class="field-error">
            {{ usernameError }}
          </div>
        </div>
        <div class="form-group">
          <label class="form-label" for="nickname">
            昵称
            <span class="char-count">{{ nickname.length }}/50</span>
          </label>
          <input 
            type="text" 
            id="nickname"
            v-model="nickname" 
            class="form-input"
            placeholder="请输入昵称（可选）"
            autocomplete="nickname"
            maxlength="50"
          />
        </div>
        <div class="form-group">
          <label class="form-label" for="password">
            密码
            <span class="char-count">{{ password.length }}/16</span>
          </label>
          <input 
            type="password" 
            id="password"
            v-model="password" 
            class="form-input"
            :class="{ 'input-error': passwordError }"
            placeholder="请输入密码（6-16位）"
            autocomplete="new-password"
            maxlength="16"
            @input="validatePassword"
          />
          <div v-if="passwordStrength.level > 0" class="password-strength">
            <div class="strength-bar">
              <div 
                class="strength-fill" 
                :class="`strength-${passwordStrength.level}`"
                :style="{ width: passwordStrength.width }"
              ></div>
            </div>
            <span class="strength-text" :class="`text-${passwordStrength.level}`">
              {{ passwordStrength.text }}
            </span>
          </div>
          <div v-if="passwordError" class="field-error">
            {{ passwordError }}
          </div>
          <div v-if="password && !passwordError" class="password-tips">
            <span :class="passwordTip.hasLowercase ? 'tip-valid' : 'tip-invalid'">
              {{ passwordTip.hasLowercase ? '✓' : '○' }} 包含小写字母
            </span>
            <span :class="passwordTip.hasUppercase ? 'tip-valid' : 'tip-invalid'">
              {{ passwordTip.hasUppercase ? '✓' : '○' }} 包含大写字母
            </span>
            <span :class="passwordTip.hasNumber ? 'tip-valid' : 'tip-invalid'">
              {{ passwordTip.hasNumber ? '✓' : '○' }} 包含数字
            </span>
            <span :class="passwordTip.hasSpecial ? 'tip-valid' : 'tip-invalid'">
              {{ passwordTip.hasSpecial ? '✓' : '○' }} 包含特殊字符
            </span>
            <span :class="passwordTip.hasLength ? 'tip-valid' : 'tip-invalid'">
              {{ passwordTip.hasLength ? '✓' : '○' }} 长度6-16位
            </span>
          </div>
        </div>
        <div class="form-group">
          <label class="form-label" for="confirmPassword">
            确认密码
            <span class="char-count">{{ confirmPassword.length }}/16</span>
          </label>
          <input 
            type="password" 
            id="confirmPassword"
            v-model="confirmPassword" 
            class="form-input"
            :class="{ 'input-error': confirmPasswordError }"
            placeholder="请再次输入密码"
            autocomplete="new-password"
            maxlength="16"
            @input="validateConfirmPassword"
            @keyup.enter="handleRegister"
          />
          <div v-if="confirmPasswordError" class="field-error">
            {{ confirmPasswordError }}
          </div>
          <div v-if="confirmPassword && !confirmPasswordError && password" class="match-status">
            <span :class="passwordsMatch ? 'match-valid' : 'match-invalid'">
              {{ passwordsMatch ? '✓ 密码匹配' : '✗ 密码不匹配' }}
            </span>
          </div>
        </div>
        <div v-if="errorMessage" class="error-message animate-shake">
          {{ errorMessage }}
        </div>
        <button 
          type="submit" 
          class="register-btn"
          :disabled="isLoading"
          :class="{ 'animate-pulse': isLoading }"
        >
          <span v-if="!isLoading">注册</span>
          <span v-else>注册中...</span>
        </button>
      </form>
      <div class="register-footer animate-fade-in">
        <p>已有账户？</p>
        <router-link to="/login" class="login-link">立即登录</router-link>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const username = ref('')
const password = ref('')
const confirmPassword = ref('')
const nickname = ref('')
const errorMessage = ref('')
const isLoading = ref(false)

const usernameError = ref('')
const passwordError = ref('')
const confirmPasswordError = ref('')

const isCheckingUsername = ref(false)
const usernameExists = ref(false)
const usernameAvailable = ref(false)

const passwordStrength = ref({
  level: 0,
  text: '',
  width: '0%'
})

const passwordTip = ref({
  hasLowercase: false,
  hasUppercase: false,
  hasNumber: false,
  hasSpecial: false,
  hasLength: false
})

const passwordsMatch = ref(false)

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

const validateUsername = () => {
  usernameError.value = ''
  usernameExists.value = false
  usernameAvailable.value = false
  
  if (!username.value.trim()) {
    return
  }
  
  if (username.value.length < 3) {
    usernameError.value = '用户名至少需要3个字符'
  } else if (username.value.length > 50) {
    usernameError.value = '用户名不能超过50个字符'
  }
}

const checkUsername = async () => {
  if (!username.value.trim() || username.value.length < 3 || username.value.length > 50) {
    return
  }
  
  usernameExists.value = false
  usernameAvailable.value = false
  isCheckingUsername.value = true
  
  try {
    const response = await fetch(`http://localhost:9090/api/auth/check-username?username=${encodeURIComponent(username.value)}`, {
      method: 'GET',
      headers: {
        'Content-Type': 'application/json'
      }
    })
    
    const data = await response.json()
    
    if (data.code === 200) {
      if (data.data === true) {
        usernameExists.value = true
        usernameAvailable.value = false
      } else {
        usernameExists.value = false
        usernameAvailable.value = true
      }
    }
  } catch (error) {
    console.error('检查用户名失败:', error)
  } finally {
    isCheckingUsername.value = false
  }
}

const validatePassword = () => {
  passwordError.value = ''
  const pwd = password.value
  
  if (!pwd) {
    passwordStrength.value = { level: 0, text: '', width: '0%' }
    passwordTip.value = {
      hasLowercase: false,
      hasUppercase: false,
      hasNumber: false,
      hasSpecial: false,
      hasLength: false
    }
    return
  }
  
  passwordTip.value.hasLowercase = /[a-z]/.test(pwd)
  passwordTip.value.hasUppercase = /[A-Z]/.test(pwd)
  passwordTip.value.hasNumber = /[0-9]/.test(pwd)
  passwordTip.value.hasSpecial = /[!@#$%^&*(),.?":{}|<>]/.test(pwd)
  passwordTip.value.hasLength = pwd.length >= 6 && pwd.length <= 16
  
  let strengthScore = 0
  
  if (passwordTip.value.hasLowercase) strengthScore++
  if (passwordTip.value.hasUppercase) strengthScore++
  if (passwordTip.value.hasNumber) strengthScore++
  if (passwordTip.value.hasSpecial) strengthScore++
  if (pwd.length >= 8) strengthScore++
  if (pwd.length >= 12) strengthScore++
  
  if (pwd.length < 6) {
    passwordStrength.value = { level: 0, text: '', width: '0%' }
    passwordError.value = '密码长度必须为6-16位'
  } else if (pwd.length > 16) {
    passwordStrength.value = { level: 0, text: '', width: '0%' }
    passwordError.value = '密码长度必须为6-16位'
  } else if (strengthScore <= 2) {
    passwordStrength.value = { level: 1, text: '弱', width: '33%' }
  } else if (strengthScore <= 4) {
    passwordStrength.value = { level: 2, text: '中', width: '66%' }
  } else {
    passwordStrength.value = { level: 3, text: '强', width: '100%' }
  }
  
  if (confirmPassword.value) {
    validateConfirmPassword()
  }
}

const validateConfirmPassword = () => {
  confirmPasswordError.value = ''
  
  if (!confirmPassword.value) {
    passwordsMatch.value = false
    return
  }
  
  if (!password.value) {
    passwordsMatch.value = false
    return
  }
  
  if (confirmPassword.value.length < 6) {
    confirmPasswordError.value = '密码长度必须为6-16位'
    passwordsMatch.value = false
  } else if (confirmPassword.value.length > 16) {
    confirmPasswordError.value = '密码长度必须为6-16位'
    passwordsMatch.value = false
  } else if (password.value !== confirmPassword.value) {
    passwordsMatch.value = false
  } else {
    passwordsMatch.value = true
  }
}

const handleRegister = async () => {
  errorMessage.value = ''
  
  validateUsername()
  validatePassword()
  validateConfirmPassword()
  
  if (usernameError.value || passwordError.value || confirmPasswordError.value) {
    return
  }
  
  if (!username.value.trim()) {
    errorMessage.value = '请输入用户名'
    return
  }
  
  if (usernameExists.value) {
    errorMessage.value = '用户名已存在，请更换用户名'
    return
  }
  
  if (!password.value) {
    errorMessage.value = '请输入密码'
    return
  }
  
  if (!confirmPassword.value) {
    errorMessage.value = '请输入确认密码'
    return
  }
  
  if (password.value !== confirmPassword.value) {
    errorMessage.value = '两次输入的密码不一致'
    return
  }
  
  isLoading.value = true
  
  try {
    const response = await fetch('http://localhost:9090/api/auth/register', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({
        username: username.value,
        password: password.value,
        confirmPassword: confirmPassword.value,
        nickname: nickname.value || null
      })
    })
    
    const data = await response.json()
    
    if (data.code === 200) {
      alert('注册成功！')
      router.push('/login')
    } else {
      errorMessage.value = data.message || '注册失败，请重试'
      isLoading.value = false
    }
  } catch (error) {
    console.error('注册请求失败:', error)
    errorMessage.value = '网络错误，请稍后重试'
    isLoading.value = false
  }
}
</script>

<style scoped>
.register-container {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
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
  background: rgba(255, 255, 255, 0.3);
  border-radius: 50%;
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

.register-card {
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

.register-header {
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

.register-title {
  font-size: 1.8rem;
  color: #2c3e50;
  margin-bottom: 10px;
  background: linear-gradient(135deg, #3498db, #2ecc71);
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

.register-subtitle {
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

.register-form {
  display: flex;
  flex-direction: column;
  gap: 15px;
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
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.char-count {
  font-size: 0.85rem;
  color: #95a5a6;
  font-weight: normal;
}

.input-error {
  border-color: #e74c3c !important;
}

.input-error:focus {
  border-color: #e74c3c !important;
  box-shadow: 0 0 0 4px rgba(231, 76, 60, 0.1) !important;
}

.field-error {
  color: #e74c3c;
  font-size: 0.85rem;
  margin-top: 4px;
  padding-left: 4px;
}

.field-valid {
  color: #27ae60;
  font-size: 0.85rem;
  margin-top: 4px;
  padding-left: 4px;
}

.field-checking {
  color: #3498db;
  font-size: 0.85rem;
  margin-top: 4px;
  padding-left: 4px;
}

.input-valid {
  border-color: #27ae60 !important;
}

.input-valid:focus {
  border-color: #27ae60 !important;
  box-shadow: 0 0 0 4px rgba(39, 174, 96, 0.1) !important;
}

.password-strength {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-top: 8px;
}

.strength-bar {
  flex: 1;
  height: 6px;
  background: #e4e8eb;
  border-radius: 3px;
  overflow: hidden;
}

.strength-fill {
  height: 100%;
  border-radius: 3px;
  transition: all 0.3s ease;
}

.strength-1 {
  background: #e74c3c;
}

.strength-2 {
  background: #f39c12;
}

.strength-3 {
  background: #27ae60;
}

.strength-text {
  font-size: 0.9rem;
  font-weight: 600;
  min-width: 30px;
}

.text-1 {
  color: #e74c3c;
}

.text-2 {
  color: #f39c12;
}

.text-3 {
  color: #27ae60;
}

.password-tips {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-top: 8px;
  padding: 12px;
  background: #f8f9fa;
  border-radius: 8px;
  border: 1px solid #e4e8eb;
}

.password-tips span {
  font-size: 0.8rem;
  display: flex;
  align-items: center;
  gap: 4px;
}

.tip-valid {
  color: #27ae60;
}

.tip-invalid {
  color: #95a5a6;
}

.match-status {
  margin-top: 8px;
  padding: 8px 12px;
  border-radius: 8px;
  font-size: 0.9rem;
}

.match-valid {
  color: #27ae60;
  background: rgba(39, 174, 96, 0.1);
}

.match-invalid {
  color: #e74c3c;
  background: rgba(231, 76, 60, 0.1);
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

.register-btn {
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

.register-btn:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 10px 25px rgba(52, 152, 219, 0.3);
}

.register-btn:disabled {
  opacity: 0.7;
  cursor: not-allowed;
}

.register-footer {
  text-align: center;
  margin-top: 20px;
  padding-top: 20px;
  border-top: 1px solid #e4e8eb;
}

.register-footer p {
  margin: 0;
  color: #7f8c8d;
  display: inline;
  margin-right: 5px;
}

.login-link {
  color: #3498db;
  text-decoration: none;
  font-weight: 600;
  transition: color 0.3s ease;
}

.login-link:hover {
  color: #2ecc71;
  text-decoration: underline;
}

@media (max-width: 480px) {
  .register-card {
    padding: 30px 20px;
  }
  
  .register-title {
    font-size: 1.5rem;
  }
}
</style>
