<template>
  <div class="create-post">
    <div class="page-header">
      <div class="particles">
        <div v-for="i in 30" :key="i" class="particle" :style="getParticleStyle(i)"></div>
      </div>
      <div class="container">
        <h1 class="page-title">发布新帖</h1>
        <p class="page-subtitle">分享您的想法和经验</p>
      </div>
    </div>

    <div class="container">
      <div class="create-post-content">
        <div class="form-card">
          <form @submit.prevent="handleSubmit">
            <div class="form-group">
              <label class="form-label">选择板块</label>
              <select v-model="form.categoryId" class="form-select" required>
                <option value="">请选择板块</option>
                <option v-for="category in categories" :key="category.id" :value="category.id">
                  {{ category.name }}
                </option>
              </select>
            </div>

            <div class="form-group">
              <label class="form-label">
                帖子标题
                <span class="required">*</span>
              </label>
              <input 
                type="text" 
                v-model="form.title" 
                class="form-input"
                placeholder="请输入帖子标题（10-100字）"
                maxlength="100"
                required
              />
              <div class="char-count">{{ form.title.length }}/100</div>
              <div v-if="form.title.length > 0 && form.title.length < 10" class="form-error">
                标题至少需要10个字符
              </div>
            </div>

            <div class="form-group">
              <label class="form-label">
                帖子内容
                <span class="required">*</span>
              </label>
              <textarea 
                v-model="form.content" 
                class="form-textarea"
                placeholder="请输入帖子内容..."
                rows="12"
                maxlength="5000"
                required
              ></textarea>
              <div class="char-count">{{ form.content.length }}/5000</div>
            </div>

            <div class="form-group">
              <label class="form-label">添加图片（可选）</label>
              <div class="upload-area">
                <div class="upload-placeholder">
                  <span class="upload-icon">📷</span>
                  <span class="upload-text">点击或拖拽上传图片</span>
                  <span class="upload-hint">支持 JPG、PNG 格式，最多 5 张</span>
                </div>
              </div>
            </div>

            <div class="form-actions">
              <button type="button" class="btn-secondary" @click="handleCancel">
                取消
              </button>
              <button type="submit" class="btn-primary" :disabled="isSubmitting || !isFormValid">
                <span v-if="!isSubmitting">发布帖子</span>
                <span v-else>发布中...</span>
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import request from '../utils/request'

const router = useRouter()

const categories = ref([])
const isSubmitting = ref(false)

const form = ref({
  categoryId: null,
  title: '',
  content: ''
})

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

const isFormValid = computed(() => {
  return form.value.categoryId && 
         form.value.title.trim() && 
         form.value.title.length >= 10 && 
         form.value.content.trim()
})

const fetchCategories = async () => {
  try {
    const response = await request('/api/forum/categories', {
      method: 'GET'
    })
    
    if (response.ok) {
      const data = await response.json()
      if (data.code === 200 && data.data) {
        categories.value = data.data
      }
    }
  } catch (error) {
    console.error('获取分类失败:', error)
  }
}

const handleSubmit = async () => {
  if (!isFormValid.value) {
    return
  }
  
  isSubmitting.value = true
  
  try {
    const params = new URLSearchParams()
    params.append('categoryId', form.value.categoryId)
    params.append('title', form.value.title.trim())
    params.append('content', form.value.content.trim())
    
    const response = await request('/api/forum/posts', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/x-www-form-urlencoded'
      },
      body: params
    })
    
    if (response.ok) {
      const data = await response.json()
      if (data.code === 200) {
        alert('帖子发布成功！')
        router.push('/forum')
      } else {
        alert(data.message || '发布失败')
      }
    } else if (response.status === 401) {
      alert('请先登录')
      router.push('/login')
    } else {
      alert('发布失败，请稍后重试')
    }
  } catch (error) {
    console.error('发布帖子失败:', error)
    alert('发布失败，请稍后重试')
  } finally {
    isSubmitting.value = false
  }
}

const handleCancel = () => {
  router.push('/forum')
}

onMounted(() => {
  fetchCategories()
})
</script>

<style scoped>
.create-post {
  min-height: calc(100vh - 200px);
}

.page-header {
  background: linear-gradient(135deg, #8B7355 0%, #6B8E23 100%);
  padding: 50px 0;
  text-align: center;
  color: white;
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
    transform: translateY(-400px) rotate(720deg);
    opacity: 0;
  }
}

.page-title {
  font-size: 2.5rem;
  margin-bottom: 10px;
}

.page-subtitle {
  font-size: 1.1rem;
  opacity: 0.9;
}

.create-post-content {
  padding: 40px 0;
  max-width: 800px;
  margin: 0 auto;
}

.form-card {
  background: white;
  border-radius: 16px;
  padding: 40px;
  box-shadow: 0 5px 20px rgba(0, 0, 0, 0.08);
}

.form-group {
  margin-bottom: 28px;
  position: relative;
}

.form-label {
  display: block;
  font-size: 1rem;
  font-weight: 600;
  color: #2c3e50;
  margin-bottom: 10px;
}

.form-label .required {
  color: #e74c3c;
  margin-left: 4px;
}

.form-select,
.form-input {
  width: 100%;
  padding: 14px 18px;
  border: 2px solid #e4e8eb;
  border-radius: 10px;
  font-size: 1rem;
  background: white;
  transition: border-color 0.3s ease, box-shadow 0.3s ease;
  outline: none;
  box-sizing: border-box;
}

.form-select:focus,
.form-input:focus {
  border-color: #6B8E23;
  box-shadow: 0 0 0 4px rgba(107, 142, 35, 0.1);
}

.form-select {
  cursor: pointer;
}

.form-textarea {
  width: 100%;
  padding: 14px 18px;
  border: 2px solid #e4e8eb;
  border-radius: 10px;
  font-size: 1rem;
  resize: vertical;
  min-height: 200px;
  transition: border-color 0.3s ease, box-shadow 0.3s ease;
  outline: none;
  box-sizing: border-box;
  font-family: inherit;
  line-height: 1.6;
}

.form-textarea:focus {
  border-color: #6B8E23;
  box-shadow: 0 0 0 4px rgba(107, 142, 35, 0.1);
}

.char-count {
  position: absolute;
  bottom: -24px;
  right: 5px;
  font-size: 0.8rem;
  color: #7f8c8d;
}

.form-error {
  color: #e74c3c;
  font-size: 0.85rem;
  margin-top: 8px;
}

.upload-area {
  border: 2px dashed #e4e8eb;
  border-radius: 10px;
  padding: 40px;
  text-align: center;
  transition: all 0.3s ease;
  cursor: pointer;
}

.upload-area:hover {
  border-color: #6B8E23;
  background: rgba(107, 142, 35, 0.02);
}

.upload-placeholder {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
}

.upload-icon {
  font-size: 3rem;
}

.upload-text {
  font-size: 1rem;
  color: #555;
  font-weight: 500;
}

.upload-hint {
  font-size: 0.85rem;
  color: #7f8c8d;
}

.form-actions {
  display: flex;
  justify-content: flex-end;
  gap: 16px;
  margin-top: 40px;
  padding-top: 28px;
  border-top: 1px solid #e4e8eb;
}

.btn-primary {
  padding: 14px 36px;
  background: linear-gradient(135deg, #6B8E23, #8FBC8F);
  color: white;
  border: none;
  border-radius: 10px;
  font-size: 1rem;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
}

.btn-primary:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 5px 15px rgba(107, 142, 35, 0.3);
}

.btn-primary:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.btn-secondary {
  padding: 14px 36px;
  background: #f5f7fa;
  color: #555;
  border: 2px solid #e4e8eb;
  border-radius: 10px;
  font-size: 1rem;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;
}

.btn-secondary:hover {
  background: #e8ecef;
  border-color: #d1d8dd;
}

@media (max-width: 768px) {
  .page-header {
    padding: 30px 0;
  }

  .page-title {
    font-size: 1.8rem;
  }

  .create-post-content {
    padding: 20px 0;
  }

  .form-card {
    padding: 24px;
  }

  .form-actions {
    flex-direction: column;
  }

  .btn-primary,
  .btn-secondary {
    width: 100%;
    text-align: center;
  }
}
</style>
