<template>
  <div class="mission-management">
    <div class="action-bar">
      <button class="add-btn" @click="openAddModal" v-if="!hasMission">
        <span>➕</span> 添加使命介绍
      </button>
      <button class="edit-btn" @click="openEditModal(currentMission)" v-else>
        <span>✏️</span> 编辑使命介绍
      </button>
    </div>
    
    <div class="mission-preview" v-if="currentMission">
      <div class="preview-card">
        <div class="preview-header">
          <h3 class="preview-title">{{ currentMission.title }}</h3>
          <span class="status-badge" :class="{ active: currentMission.status === 1 }">
            {{ currentMission.status === 1 ? '启用' : '禁用' }}
          </span>
        </div>
        <div class="preview-content">
          <div class="preview-text">
            <h4 class="preview-label">描述</h4>
            <p class="preview-desc">{{ currentMission.description || '暂无描述' }}</p>
          </div>
          <div class="preview-image" v-if="currentMission.imageUrl || currentMission.imageText">
            <h4 class="preview-label">图片区域</h4>
            <div class="image-display" v-if="currentMission.imageUrl">
              <img :src="currentMission.imageUrl" alt="图片" @error="handleImageError" />
            </div>
            <div class="image-text-display" v-else>
              <span class="image-icon">🏘️</span>
              <span class="image-text">{{ currentMission.imageText || '社区服务中心' }}</span>
            </div>
          </div>
        </div>
        <div class="preview-meta">
          <span>📅 创建时间: {{ formatDate(currentMission.createTime) }}</span>
          <span>📅 更新时间: {{ formatDate(currentMission.updateTime) }}</span>
        </div>
      </div>
    </div>
    
    <div class="empty-state" v-else>
      <div class="empty-icon">📋</div>
      <h3>暂无使命介绍</h3>
      <p>点击上方按钮添加使命介绍</p>
    </div>
    
    <div class="modal-overlay" :class="{ show: showModal }" @click="closeModal">
      <div class="modal-container" @click.stop>
        <div class="modal-header">
          <h2>{{ isEdit ? '编辑使命介绍' : '添加使命介绍' }}</h2>
          <button class="close-btn" @click="closeModal">×</button>
        </div>
        
        <div class="modal-body">
          <div class="form-group">
            <label class="form-label">标题 <span class="required">*</span></label>
            <input 
              type="text" 
              v-model="form.title" 
              class="form-input"
              placeholder="请输入标题（如：我们的使命）"
            />
          </div>
          
          <div class="form-group">
            <label class="form-label">描述</label>
            <textarea 
              v-model="form.description" 
              class="form-textarea"
              placeholder="请输入描述内容"
              rows="6"
            ></textarea>
          </div>
          
          <div class="form-group">
            <label class="form-label">图片URL</label>
            <input 
              type="text" 
              v-model="form.imageUrl" 
              class="form-input"
              placeholder="请输入图片URL（可选，不填则使用默认图标）"
            />
            <div class="image-preview" v-if="form.imageUrl">
              <img :src="form.imageUrl" alt="预览" @error="previewError = true" />
              <span v-if="previewError" class="preview-error">图片加载失败</span>
            </div>
          </div>
          
          <div class="form-group">
            <label class="form-label">图片说明文字</label>
            <input 
              type="text" 
              v-model="form.imageText" 
              class="form-input"
              placeholder="请输入图片下方的说明文字（如：社区服务中心）"
            />
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
import { ref, reactive, onMounted, computed } from 'vue'
import request from '../../utils/request'

const currentMission = ref(null)
const showModal = ref(false)
const isEdit = ref(false)
const isSubmitting = ref(false)
const errorMessage = ref('')
const successMessage = ref('')
const previewError = ref(false)

const hasMission = computed(() => currentMission.value !== null)

const form = reactive({
  id: null,
  title: '',
  description: '',
  imageUrl: '',
  imageText: '',
  status: 1
})

const resetForm = () => {
  form.id = null
  form.title = ''
  form.description = ''
  form.imageUrl = ''
  form.imageText = ''
  form.status = 1
  errorMessage.value = ''
  successMessage.value = ''
  previewError.value = false
}

const loadMission = async () => {
  try {
    const response = await request('/api/about/mission')
    const data = await response.json()
    
    if (data.code === 200) {
      currentMission.value = data.data
    } else {
      currentMission.value = null
    }
  } catch (error) {
    console.error('加载使命数据失败:', error)
    currentMission.value = null
  }
}

const openAddModal = () => {
  resetForm()
  isEdit.value = false
  showModal.value = true
}

const openEditModal = (mission) => {
  resetForm()
  isEdit.value = true
  form.id = mission.id
  form.title = mission.title || ''
  form.description = mission.description || ''
  form.imageUrl = mission.imageUrl || ''
  form.imageText = mission.imageText || ''
  form.status = mission.status ?? 1
  showModal.value = true
}

const closeModal = () => {
  showModal.value = false
}

const handleSubmit = async () => {
  errorMessage.value = ''
  successMessage.value = ''
  
  if (!form.title.trim()) {
    errorMessage.value = '请输入标题'
    return
  }
  
  isSubmitting.value = true
  
  try {
    const url = isEdit.value 
      ? `/api/admin/about-missions/${form.id}` 
      : '/api/admin/about-missions'
    const method = isEdit.value ? 'PUT' : 'POST'
    
    const response = await request(url, {
      method: method,
      body: JSON.stringify({
        title: form.title,
        description: form.description,
        imageUrl: form.imageUrl,
        imageText: form.imageText,
        status: form.status
      })
    })
    
    const data = await response.json()
    
    if (data.code === 200) {
      successMessage.value = isEdit.value ? '更新成功' : '创建成功'
      setTimeout(() => {
        closeModal()
        loadMission()
      }, 1000)
    } else {
      errorMessage.value = data.message || '操作失败'
    }
  } catch (error) {
    console.error('提交失败:', error)
    errorMessage.value = '网络错误，请稍后重试'
  } finally {
    isSubmitting.value = false
  }
}

const handleImageError = (e) => {
  e.target.src = 'data:image/svg+xml,%3Csvg xmlns="http://www.w3.org/2000/svg" width="400" height="200" viewBox="0 0 400 200"%3E%3Crect fill="%23e4e8eb" width="400" height="200"/%3E%3Ctext fill="%237f8c8d" font-family="sans-serif" font-size="16" x="50%25" y="50%25" text-anchor="middle" dominant-baseline="middle"%3E图片加载失败%3C/text%3E%3C/svg%3E'
}

const formatDate = (dateStr) => {
  if (!dateStr) return '-'
  const date = new Date(dateStr)
  return date.toLocaleDateString('zh-CN')
}

onMounted(() => {
  loadMission()
})
</script>

<style scoped>
.mission-management {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.action-bar {
  display: flex;
  justify-content: flex-end;
}

.add-btn, .edit-btn {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 24px;
  background: linear-gradient(135deg, #6B8E23, #8FBC8F);
  color: white;
  border: none;
  border-radius: 10px;
  font-size: 1rem;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
}

.add-btn:hover, .edit-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(107, 142, 35, 0.3);
}

.mission-preview {
  background: white;
  border-radius: 16px;
  padding: 24px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
}

.preview-card {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.preview-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-bottom: 16px;
  border-bottom: 2px solid #e4e8eb;
}

.preview-title {
  font-size: 1.4rem;
  color: #2c3e50;
  margin: 0;
  font-weight: 600;
}

.status-badge {
  padding: 6px 16px;
  border-radius: 20px;
  font-size: 0.85rem;
  font-weight: 600;
  background: rgba(196, 91, 26, 0.9);
  color: white;
}

.status-badge.active {
  background: rgba(93, 124, 74, 0.9);
}

.preview-content {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 30px;
}

.preview-text {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.preview-label {
  font-size: 0.95rem;
  font-weight: 600;
  color: #2c3e50;
  margin: 0;
}

.preview-desc {
  font-size: 0.95rem;
  color: #555;
  margin: 0;
  line-height: 1.8;
  white-space: pre-wrap;
}

.preview-image {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.image-display {
  width: 100%;
  height: 200px;
  border-radius: 12px;
  overflow: hidden;
  background: #f5f7fa;
}

.image-display img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.image-text-display {
  width: 100%;
  height: 200px;
  background: linear-gradient(135deg, #f5f7fa, #e4e8eb);
  border-radius: 12px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 10px;
}

.image-icon {
  font-size: 4rem;
}

.image-text {
  font-size: 1.1rem;
  color: #7f8c8d;
}

.preview-meta {
  display: flex;
  gap: 30px;
  padding-top: 16px;
  border-top: 1px solid #e4e8eb;
  color: #95a5a6;
  font-size: 0.9rem;
}

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 60px 20px;
  background: white;
  border-radius: 16px;
  text-align: center;
}

.empty-icon {
  font-size: 4rem;
  margin-bottom: 16px;
}

.empty-state h3 {
  font-size: 1.3rem;
  color: #2c3e50;
  margin: 0 0 8px 0;
}

.empty-state p {
  color: #7f8c8d;
  margin: 0;
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
  max-width: 600px;
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

.required {
  color: #dc2626;
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
  flex-direction: column;
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
  margin-top: 8px;
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
  .preview-content {
    grid-template-columns: 1fr;
    gap: 20px;
  }
  
  .preview-meta {
    flex-direction: column;
    gap: 8px;
  }
  
  .modal-container {
    width: 95%;
    margin: 10px;
    max-height: 95vh;
  }
}
</style>
