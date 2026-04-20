<template>
  <div class="carousel-management">
    <div class="action-bar">
      <button class="add-btn" @click="openAddModal">
        <span>➕</span> 添加轮播图
      </button>
    </div>
    
    <div class="carousel-list" v-if="carousels.length > 0">
      <div v-for="carousel in carousels" :key="carousel.id" class="carousel-card">
        <div class="carousel-image">
          <img :src="carousel.imageUrl" :alt="carousel.title" @error="handleImageError" />
          <div class="image-overlay">
            <span class="sort-badge">排序: {{ carousel.sortOrder }}</span>
            <span class="status-badge" :class="{ active: carousel.status === 1 }">
              {{ carousel.status === 1 ? '启用' : '禁用' }}
            </span>
          </div>
        </div>
        <div class="carousel-info">
          <h3 class="carousel-title">{{ carousel.title }}</h3>
          <p class="carousel-desc" v-if="carousel.description">{{ carousel.description }}</p>
          <div class="carousel-meta">
            <span v-if="carousel.category" class="meta-item">📁 {{ carousel.category }}</span>
            <span class="meta-item">📅 {{ formatDate(carousel.createTime) }}</span>
          </div>
        </div>
        <div class="carousel-actions">
          <button class="action-btn edit" @click="openEditModal(carousel)">✏️ 编辑</button>
          <button class="action-btn delete" @click="confirmDelete(carousel)">🗑️ 删除</button>
          <button 
            class="action-btn toggle" 
            :class="carousel.status === 1 ? 'disable' : 'enable'"
            @click="toggleStatus(carousel)"
          >
            {{ carousel.status === 1 ? '❌ 禁用' : '✅ 启用' }}
          </button>
        </div>
      </div>
    </div>
    
    <div class="empty-state" v-else>
      <div class="empty-icon">🖼️</div>
      <h3>暂无轮播图</h3>
      <p>点击上方按钮添加第一个轮播图</p>
    </div>
    
    <div class="modal-overlay" :class="{ show: showModal }" @click="closeModal">
      <div class="modal-container" @click.stop>
        <div class="modal-header">
          <h2>{{ isEdit ? '编辑轮播图' : '添加轮播图' }}</h2>
          <button class="close-btn" @click="closeModal">×</button>
        </div>
        
        <div class="modal-body">
          <div class="form-group">
            <label class="form-label">标题 <span class="required">*</span></label>
            <input 
              type="text" 
              v-model="form.title" 
              class="form-input"
              placeholder="请输入轮播图标题"
            />
          </div>
          
          <div class="form-group">
            <label class="form-label">描述</label>
            <textarea 
              v-model="form.description" 
              class="form-textarea"
              placeholder="请输入轮播图描述（可选）"
              rows="3"
            ></textarea>
          </div>
          
          <div class="form-group">
            <label class="form-label">图片链接 <span class="required">*</span></label>
            <input 
              type="text" 
              v-model="form.imageUrl" 
              class="form-input"
              placeholder="请输入图片URL"
            />
            <div class="image-preview" v-if="form.imageUrl">
              <img :src="form.imageUrl" alt="预览" @error="previewError = true" />
              <span v-if="previewError" class="preview-error">图片加载失败</span>
            </div>
          </div>
          
          <div class="form-row">
            <div class="form-group">
              <label class="form-label">分类</label>
              <input 
                type="text" 
                v-model="form.category" 
                class="form-input"
                placeholder="请输入分类（可选）"
              />
            </div>
            
            <div class="form-group">
              <label class="form-label">排序</label>
              <input 
                type="number" 
                v-model.number="form.sortOrder" 
                class="form-input"
                placeholder="数字越小越靠前"
                min="0"
              />
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
    
    <div class="modal-overlay" :class="{ show: showDeleteModal }" @click="closeDeleteModal">
      <div class="modal-container small" @click.stop>
        <div class="modal-header">
          <h2>确认删除</h2>
        </div>
        <div class="modal-body">
          <p>确定要删除轮播图 "<strong>{{ deletingCarousel?.title }}</strong>" 吗？</p>
          <p class="warning-text">此操作不可撤销</p>
        </div>
        <div class="modal-footer">
          <button class="btn-secondary" @click="closeDeleteModal">取消</button>
          <button class="btn-danger" @click="handleDelete" :disabled="isDeleting">
            {{ isDeleting ? '删除中...' : '确认删除' }}
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import request from '../../utils/request'

const carousels = ref([])
const showModal = ref(false)
const showDeleteModal = ref(false)
const isEdit = ref(false)
const isSubmitting = ref(false)
const isDeleting = ref(false)
const errorMessage = ref('')
const successMessage = ref('')
const previewError = ref(false)
const deletingCarousel = ref(null)

const form = reactive({
  id: null,
  title: '',
  description: '',
  imageUrl: '',
  category: '',
  sortOrder: 0,
  status: 1
})

const resetForm = () => {
  form.id = null
  form.title = ''
  form.description = ''
  form.imageUrl = ''
  form.category = ''
  form.sortOrder = 0
  form.status = 1
  errorMessage.value = ''
  successMessage.value = ''
  previewError.value = false
}

const loadCarousels = async () => {
  try {
    const response = await request('/api/admin/carousels?pageNum=1&pageSize=100')
    const data = await response.json()
    
    if (data.code === 200) {
      carousels.value = data.data.records || []
    }
  } catch (error) {
    console.error('加载轮播图失败:', error)
  }
}

const openAddModal = () => {
  resetForm()
  isEdit.value = false
  showModal.value = true
}

const openEditModal = (carousel) => {
  resetForm()
  isEdit.value = true
  form.id = carousel.id
  form.title = carousel.title || ''
  form.description = carousel.description || ''
  form.imageUrl = carousel.imageUrl || ''
  form.category = carousel.category || ''
  form.sortOrder = carousel.sortOrder || 0
  form.status = carousel.status ?? 1
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
  
  if (!form.imageUrl.trim()) {
    errorMessage.value = '请输入图片链接'
    return
  }
  
  isSubmitting.value = true
  
  try {
    const url = isEdit.value 
      ? `/api/admin/carousels/${form.id}` 
      : '/api/admin/carousels'
    const method = isEdit.value ? 'PUT' : 'POST'
    
    const response = await request(url, {
      method: method,
      body: JSON.stringify({
        title: form.title,
        description: form.description,
        imageUrl: form.imageUrl,
        category: form.category,
        sortOrder: form.sortOrder,
        status: form.status
      })
    })
    
    const data = await response.json()
    
    if (data.code === 200) {
      successMessage.value = isEdit.value ? '更新成功' : '创建成功'
      setTimeout(() => {
        closeModal()
        loadCarousels()
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

const confirmDelete = (carousel) => {
  deletingCarousel.value = carousel
  showDeleteModal.value = true
}

const closeDeleteModal = () => {
  showDeleteModal.value = false
  deletingCarousel.value = null
}

const handleDelete = async () => {
  if (!deletingCarousel.value) return
  
  isDeleting.value = true
  
  try {
    const response = await request(`/api/admin/carousels/${deletingCarousel.value.id}`, {
      method: 'DELETE'
    })
    
    const data = await response.json()
    
    if (data.code === 200) {
      closeDeleteModal()
      loadCarousels()
    } else {
      alert(data.message || '删除失败')
    }
  } catch (error) {
    console.error('删除失败:', error)
    alert('网络错误，请稍后重试')
  } finally {
    isDeleting.value = false
  }
}

const toggleStatus = async (carousel) => {
  const newStatus = carousel.status === 1 ? 0 : 1
  
  try {
    const response = await request(`/api/admin/carousels/${carousel.id}`, {
      method: 'PUT',
      body: JSON.stringify({
        status: newStatus
      })
    })
    
    const data = await response.json()
    
    if (data.code === 200) {
      carousel.status = newStatus
    } else {
      alert(data.message || '操作失败')
    }
  } catch (error) {
    console.error('切换状态失败:', error)
    alert('网络错误，请稍后重试')
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
  loadCarousels()
})
</script>

<style scoped>
.carousel-management {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.action-bar {
  display: flex;
  justify-content: flex-end;
}

.add-btn {
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

.add-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(107, 142, 35, 0.3);
}

.carousel-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.carousel-card {
  display: flex;
  background: white;
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  transition: all 0.3s ease;
}

.carousel-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.12);
}

.carousel-image {
  width: 300px;
  min-width: 300px;
  position: relative;
  overflow: hidden;
}

.carousel-image img {
  width: 100%;
  height: 200px;
  object-fit: cover;
}

.image-overlay {
  position: absolute;
  top: 10px;
  right: 10px;
  display: flex;
  gap: 8px;
}

.sort-badge,
.status-badge {
  padding: 4px 12px;
  border-radius: 20px;
  font-size: 0.85rem;
  font-weight: 600;
}

.sort-badge {
  background: rgba(0, 0, 0, 0.6);
  color: white;
}

.status-badge {
  background: rgba(196, 91, 26, 0.9);
  color: white;
}

.status-badge.active {
  background: rgba(93, 124, 74, 0.9);
}

.carousel-info {
  flex: 1;
  padding: 20px;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.carousel-title {
  font-size: 1.2rem;
  color: #2c3e50;
  margin: 0;
  font-weight: 600;
}

.carousel-desc {
  color: #7f8c8d;
  font-size: 0.95rem;
  margin: 0;
  line-height: 1.5;
}

.carousel-meta {
  display: flex;
  gap: 20px;
  margin-top: auto;
}

.meta-item {
  color: #95a5a6;
  font-size: 0.9rem;
}

.carousel-actions {
  display: flex;
  flex-direction: column;
  gap: 10px;
  padding: 20px;
  border-left: 1px solid #e4e8eb;
}

.action-btn {
  padding: 10px 16px;
  border: none;
  border-radius: 8px;
  font-size: 0.9rem;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;
  white-space: nowrap;
}

.action-btn.edit {
  background: #e8f4fd;
  color: #3498db;
}

.action-btn.edit:hover {
  background: #3498db;
  color: white;
}

.action-btn.delete {
  background: #fdf2f2;
  color: #dc2626;
}

.action-btn.delete:hover {
  background: #dc2626;
  color: white;
}

.action-btn.toggle {
  background: #fef3c7;
  color: #d97706;
}

.action-btn.toggle.enable:hover {
  background: #16a34a;
  color: white;
}

.action-btn.toggle.disable:hover {
  background: #dc2626;
  color: white;
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

.modal-container.small {
  max-width: 400px;
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

.form-row {
  display: flex;
  gap: 20px;
}

.form-row .form-group {
  flex: 1;
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
  min-height: 80px;
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
.btn-secondary,
.btn-danger {
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

.btn-danger {
  background: linear-gradient(135deg, #C45B1A, #9E3B1B);
  color: white;
}

.btn-danger:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(196, 91, 26, 0.3);
}

.btn-danger:disabled {
  opacity: 0.7;
  cursor: not-allowed;
}

.warning-text {
  color: #dc2626;
  font-size: 0.9rem;
  margin: 8px 0 0 0;
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
  .carousel-card {
    flex-direction: column;
  }
  
  .carousel-image {
    width: 100%;
    min-width: auto;
  }
  
  .carousel-image img {
    height: 180px;
  }
  
  .carousel-actions {
    flex-direction: row;
    border-left: none;
    border-top: 1px solid #e4e8eb;
    padding: 16px 20px;
  }
  
  .action-btn {
    flex: 1;
  }
  
  .form-row {
    flex-direction: column;
    gap: 20px;
  }
  
  .modal-container {
    width: 95%;
    margin: 10px;
    max-height: 95vh;
  }
}
</style>
