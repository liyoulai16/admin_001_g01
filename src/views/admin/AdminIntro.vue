<template>
  <div class="intro-management">
    <div class="intro-card" v-if="intro">
      <div class="card-header">
        <h2 class="card-title">平台简介设置</h2>
        <button class="edit-btn" @click="openEditModal">
          <span>✏️</span> 编辑
        </button>
      </div>
      
      <div class="card-body">
        <div class="intro-preview">
          <div class="preview-section">
            <h3 class="preview-label">标题</h3>
            <p class="preview-value">{{ intro.title || '未设置' }}</p>
          </div>
          
          <div class="preview-section">
            <h3 class="preview-label">内容</h3>
            <div class="preview-content">
              <p v-if="intro.content" class="preview-text">{{ intro.content }}</p>
              <p v-else class="preview-empty">未设置</p>
            </div>
          </div>
          
          <div class="preview-section">
            <h3 class="preview-label">图片</h3>
            <div class="preview-image" v-if="intro.imageUrl">
              <img :src="intro.imageUrl" alt="平台简介图片" @error="handleImageError" />
            </div>
            <p v-else class="preview-empty">未设置图片</p>
          </div>
          
          <div class="preview-section">
            <h3 class="preview-label">统计数据</h3>
            <div class="stats-grid">
              <div class="stat-item">
                <span class="stat-label">{{ intro.stat1Label || '统计项1' }}</span>
                <span class="stat-value">{{ intro.stat1Value || '-' }}</span>
              </div>
              <div class="stat-item">
                <span class="stat-label">{{ intro.stat2Label || '统计项2' }}</span>
                <span class="stat-value">{{ intro.stat2Value || '-' }}</span>
              </div>
              <div class="stat-item">
                <span class="stat-label">{{ intro.stat3Label || '统计项3' }}</span>
                <span class="stat-value">{{ intro.stat3Value || '-' }}</span>
              </div>
              <div class="stat-item">
                <span class="stat-label">{{ intro.stat4Label || '统计项4' }}</span>
                <span class="stat-value">{{ intro.stat4Value || '-' }}</span>
              </div>
            </div>
          </div>
          
          <div class="preview-section">
            <h3 class="preview-label">状态</h3>
            <span class="status-badge" :class="{ active: intro.status === 1 }">
              {{ intro.status === 1 ? '✅ 启用' : '❌ 禁用' }}
            </span>
          </div>
        </div>
      </div>
    </div>
    
    <div class="empty-card" v-else>
      <div class="empty-icon">📋</div>
      <h3>暂无平台简介</h3>
      <p>点击下方按钮创建平台简介</p>
      <button class="create-btn" @click="openEditModal">
        <span>➕</span> 创建平台简介
      </button>
    </div>
    
    <div class="modal-overlay" :class="{ show: showModal }" @click="closeModal">
      <div class="modal-container" @click.stop>
        <div class="modal-header">
          <h2>{{ intro ? '编辑平台简介' : '创建平台简介' }}</h2>
          <button class="close-btn" @click="closeModal">×</button>
        </div>
        
        <div class="modal-body">
          <div class="form-group">
            <label class="form-label">标题</label>
            <input 
              type="text" 
              v-model="form.title" 
              class="form-input"
              placeholder="请输入平台简介标题"
            />
          </div>
          
          <div class="form-group">
            <label class="form-label">内容</label>
            <textarea 
              v-model="form.content" 
              class="form-textarea"
              placeholder="请输入平台简介内容（支持换行）"
              rows="5"
            ></textarea>
          </div>
          
          <div class="form-group">
            <label class="form-label">图片链接</label>
            <input 
              type="text" 
              v-model="form.imageUrl" 
              class="form-input"
              placeholder="请输入图片URL（可选）"
            />
            <div class="image-preview" v-if="form.imageUrl">
              <img :src="form.imageUrl" alt="预览" @error="previewError = true" />
              <span v-if="previewError" class="preview-error">图片加载失败</span>
            </div>
          </div>
          
          <div class="form-section">
            <h3 class="section-title">统计数据配置</h3>
            <p class="section-hint">用于展示在首页的统计数据，如服务项目数、合作商家数等</p>
            
            <div class="stats-form-grid">
              <div class="stat-form-item">
                <label class="form-label">统计项1标签</label>
                <input 
                  type="text" 
                  v-model="form.stat1Label" 
                  class="form-input small"
                  placeholder="如：服务项目"
                />
                <label class="form-label">统计项1数值</label>
                <input 
                  type="text" 
                  v-model="form.stat1Value" 
                  class="form-input small"
                  placeholder="如：1000+"
                />
              </div>
              
              <div class="stat-form-item">
                <label class="form-label">统计项2标签</label>
                <input 
                  type="text" 
                  v-model="form.stat2Label" 
                  class="form-input small"
                  placeholder="如：合作商家"
                />
                <label class="form-label">统计项2数值</label>
                <input 
                  type="text" 
                  v-model="form.stat2Value" 
                  class="form-input small"
                  placeholder="如：500+"
                />
              </div>
              
              <div class="stat-form-item">
                <label class="form-label">统计项3标签</label>
                <input 
                  type="text" 
                  v-model="form.stat3Label" 
                  class="form-input small"
                  placeholder="如：服务用户"
                />
                <label class="form-label">统计项3数值</label>
                <input 
                  type="text" 
                  v-model="form.stat3Value" 
                  class="form-input small"
                  placeholder="如：10万+"
                />
              </div>
              
              <div class="stat-form-item">
                <label class="form-label">统计项4标签</label>
                <input 
                  type="text" 
                  v-model="form.stat4Label" 
                  class="form-input small"
                  placeholder="如：好评率"
                />
                <label class="form-label">统计项4数值</label>
                <input 
                  type="text" 
                  v-model="form.stat4Value" 
                  class="form-input small"
                  placeholder="如：98%"
                />
              </div>
            </div>
          </div>
          
          <div class="form-group">
            <label class="form-label">状态</label>
            <div class="radio-group">
              <label class="radio-label">
                <input type="radio" v-model="form.status" :value="1" /> 启用
              </label>
              <label class="radio-label">
                <input type="radio" v-model="form.status" :value="0" /> 禁用
              </label>
            </div>
          </div>
        </div>
        
        <div class="modal-footer">
          <button class="btn-secondary" @click="closeModal">取消</button>
          <button class="btn-primary" @click="handleSubmit" :disabled="isSubmitting">
            {{ isSubmitting ? '保存中...' : '保存' }}
          </button>
        </div>
        
        <div v-if="errorMessage" class="error-message">{{ errorMessage }}</div>
        <div v-if="successMessage" class="success-message">{{ successMessage }}</div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import request from '../../utils/request'

const intro = ref(null)
const showModal = ref(false)
const isSubmitting = ref(false)
const errorMessage = ref('')
const successMessage = ref('')
const previewError = ref(false)

const form = reactive({
  id: null,
  title: '',
  content: '',
  imageUrl: '',
  stat1Label: '',
  stat1Value: '',
  stat2Label: '',
  stat2Value: '',
  stat3Label: '',
  stat3Value: '',
  stat4Label: '',
  stat4Value: '',
  status: 1
})

const resetForm = () => {
  form.id = null
  form.title = ''
  form.content = ''
  form.imageUrl = ''
  form.stat1Label = ''
  form.stat1Value = ''
  form.stat2Label = ''
  form.stat2Value = ''
  form.stat3Label = ''
  form.stat3Value = ''
  form.stat4Label = ''
  form.stat4Value = ''
  form.status = 1
  errorMessage.value = ''
  successMessage.value = ''
  previewError.value = false
}

const loadIntro = async () => {
  try {
    const response = await request('/api/admin/intro')
    const data = await response.json()
    
    if (data.code === 200 && data.data) {
      intro.value = data.data
    }
  } catch (error) {
    console.error('加载平台简介失败:', error)
  }
}

const openEditModal = () => {
  resetForm()
  if (intro.value) {
    form.id = intro.value.id
    form.title = intro.value.title || ''
    form.content = intro.value.content || ''
    form.imageUrl = intro.value.imageUrl || ''
    form.stat1Label = intro.value.stat1Label || ''
    form.stat1Value = intro.value.stat1Value || ''
    form.stat2Label = intro.value.stat2Label || ''
    form.stat2Value = intro.value.stat2Value || ''
    form.stat3Label = intro.value.stat3Label || ''
    form.stat3Value = intro.value.stat3Value || ''
    form.stat4Label = intro.value.stat4Label || ''
    form.stat4Value = intro.value.stat4Value || ''
    form.status = intro.value.status ?? 1
  }
  showModal.value = true
}

const closeModal = () => {
  showModal.value = false
}

const handleSubmit = async () => {
  errorMessage.value = ''
  successMessage.value = ''
  
  isSubmitting.value = true
  
  try {
    const response = await request('/api/admin/intro', {
      method: 'PUT',
      body: JSON.stringify({
        id: form.id,
        title: form.title,
        content: form.content,
        imageUrl: form.imageUrl,
        stat1Label: form.stat1Label,
        stat1Value: form.stat1Value,
        stat2Label: form.stat2Label,
        stat2Value: form.stat2Value,
        stat3Label: form.stat3Label,
        stat3Value: form.stat3Value,
        stat4Label: form.stat4Label,
        stat4Value: form.stat4Value,
        status: form.status
      })
    })
    
    const data = await response.json()
    
    if (data.code === 200) {
      successMessage.value = '保存成功'
      setTimeout(() => {
        closeModal()
        loadIntro()
      }, 1000)
    } else {
      errorMessage.value = data.message || '保存失败'
    }
  } catch (error) {
    console.error('保存失败:', error)
    errorMessage.value = '网络错误，请稍后重试'
  } finally {
    isSubmitting.value = false
  }
}

const handleImageError = (e) => {
  e.target.src = 'data:image/svg+xml,%3Csvg xmlns="http://www.w3.org/2000/svg" width="400" height="200" viewBox="0 0 400 200"%3E%3Crect fill="%23e4e8eb" width="400" height="200"/%3E%3Ctext fill="%237f8c8d" font-family="sans-serif" font-size="16" x="50%25" y="50%25" text-anchor="middle" dominant-baseline="middle"%3E图片加载失败%3C/text%3E%3C/svg%3E'
}

onMounted(() => {
  loadIntro()
})
</script>

<style scoped>
.intro-management {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.intro-card,
.empty-card {
  background: white;
  border-radius: 16px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  overflow: hidden;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 24px;
  border-bottom: 1px solid #e4e8eb;
  background: linear-gradient(135deg, #f8f9fa 0%, #fff 100%);
}

.card-title {
  font-size: 1.4rem;
  color: #2c3e50;
  margin: 0;
}

.edit-btn {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 20px;
  background: linear-gradient(135deg, #6B8E23, #8FBC8F);
  color: white;
  border: none;
  border-radius: 10px;
  font-size: 0.95rem;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
}

.edit-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(107, 142, 35, 0.3);
}

.card-body {
  padding: 24px;
}

.intro-preview {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.preview-section {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.preview-label {
  font-size: 0.95rem;
  font-weight: 600;
  color: #7f8c8d;
  margin: 0;
}

.preview-value {
  font-size: 1.1rem;
  color: #2c3e50;
  margin: 0;
  font-weight: 500;
}

.preview-content {
  background: #f8f9fa;
  padding: 16px 20px;
  border-radius: 10px;
  border-left: 4px solid #6B8E23;
}

.preview-text {
  color: #2c3e50;
  line-height: 1.8;
  margin: 0;
  white-space: pre-wrap;
}

.preview-empty {
  color: #95a5a6;
  font-style: italic;
  margin: 0;
}

.preview-image {
  border-radius: 12px;
  overflow: hidden;
  border: 2px solid #e4e8eb;
}

.preview-image img {
  width: 100%;
  max-height: 300px;
  object-fit: cover;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 16px;
}

.stat-item {
  display: flex;
  flex-direction: column;
  gap: 8px;
  padding: 20px;
  background: linear-gradient(135deg, #f8f9fa 0%, #fff 100%);
  border-radius: 12px;
  border: 1px solid #e4e8eb;
  transition: all 0.3s ease;
}

.stat-item:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.08);
}

.stat-label {
  font-size: 0.9rem;
  color: #7f8c8d;
  font-weight: 500;
}

.stat-value {
  font-size: 1.8rem;
  font-weight: 700;
  background: linear-gradient(135deg, #6B8E23, #8FBC8F);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.status-badge {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 8px 20px;
  border-radius: 20px;
  font-size: 0.95rem;
  font-weight: 600;
  background: #fdf2f2;
  color: #dc2626;
  border: 1px solid #fecaca;
}

.status-badge.active {
  background: #f0fdf4;
  color: #16a34a;
  border: 1px solid #bbf7d0;
}

.empty-card {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 60px 20px;
  text-align: center;
}

.empty-icon {
  font-size: 4rem;
  margin-bottom: 16px;
}

.empty-card h3 {
  font-size: 1.3rem;
  color: #2c3e50;
  margin: 0 0 8px 0;
}

.empty-card p {
  color: #7f8c8d;
  margin: 0 0 24px 0;
}

.create-btn {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 14px 32px;
  background: linear-gradient(135deg, #6B8E23, #8FBC8F);
  color: white;
  border: none;
  border-radius: 10px;
  font-size: 1rem;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
}

.create-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(107, 142, 35, 0.3);
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
  z-index: 1000;
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
  max-width: 700px;
  max-height: 90vh;
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
  padding: 24px;
  border-bottom: 1px solid #e4e8eb;
}

.modal-header h2 {
  margin: 0;
  font-size: 1.4rem;
  color: #2c3e50;
}

.close-btn {
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

.close-btn:hover {
  background: #C45B1A;
  color: white;
}

.modal-body {
  padding: 24px;
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

.form-input,
.form-textarea {
  padding: 12px 16px;
  border: 2px solid #e4e8eb;
  border-radius: 10px;
  font-size: 1rem;
  color: #2c3e50;
  outline: none;
  transition: all 0.3s ease;
}

.form-input:focus,
.form-textarea:focus {
  border-color: #6B8E23;
  box-shadow: 0 0 0 4px rgba(107, 142, 35, 0.1);
}

.form-textarea {
  resize: vertical;
  min-height: 120px;
}

.form-input.small {
  padding: 10px 14px;
  font-size: 0.9rem;
}

.form-section {
  padding: 20px;
  background: #f8f9fa;
  border-radius: 12px;
  border: 1px solid #e4e8eb;
}

.section-title {
  font-size: 1.1rem;
  color: #2c3e50;
  margin: 0 0 8px 0;
}

.section-hint {
  font-size: 0.9rem;
  color: #7f8c8d;
  margin: 0 0 16px 0;
}

.stats-form-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 16px;
}

.stat-form-item {
  display: flex;
  flex-direction: column;
  gap: 8px;
  padding: 16px;
  background: white;
  border-radius: 10px;
  border: 1px solid #e4e8eb;
}

.radio-group {
  display: flex;
  gap: 24px;
}

.radio-label {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  font-size: 0.95rem;
  color: #555;
}

.radio-label input {
  width: 18px;
  height: 18px;
  accent-color: #6B8E23;
}

.image-preview {
  margin-top: 10px;
  border: 2px dashed #e4e8eb;
  border-radius: 10px;
  padding: 10px;
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 120px;
  background: #fafbfc;
}

.image-preview img {
  max-width: 100%;
  max-height: 150px;
  border-radius: 8px;
  object-fit: contain;
}

.preview-error {
  color: #dc2626;
  font-size: 0.9rem;
}

.modal-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  padding: 24px;
  border-top: 1px solid #e4e8eb;
}

.btn-primary,
.btn-secondary {
  padding: 12px 28px;
  border: none;
  border-radius: 10px;
  font-size: 0.95rem;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
}

.btn-primary {
  background: linear-gradient(135deg, #6B8E23, #8FBC8F);
  color: white;
}

.btn-primary:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(107, 142, 35, 0.3);
}

.btn-primary:disabled {
  opacity: 0.7;
  cursor: not-allowed;
}

.btn-secondary {
  background: #f5f7fa;
  color: #7f8c8d;
  border: 1px solid #e4e8eb;
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
  margin: 0 24px 24px;
}

.success-message {
  background: #f0fdf4;
  border: 1px solid #bbf7d0;
  color: #16a34a;
  padding: 12px 16px;
  border-radius: 8px;
  font-size: 0.9rem;
  text-align: center;
  margin: 0 24px 24px;
}

@media (max-width: 768px) {
  .card-header {
    flex-direction: column;
    gap: 16px;
    align-items: flex-start;
  }
  
  .edit-btn {
    width: 100%;
    justify-content: center;
  }
  
  .stats-grid {
    grid-template-columns: 1fr;
  }
  
  .stats-form-grid {
    grid-template-columns: 1fr;
  }
  
  .modal-container {
    width: 95%;
    margin: 10px;
    max-height: 95vh;
  }
}
</style>
