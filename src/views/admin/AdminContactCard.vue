<template>
  <div class="contact-card-management">
    <div class="action-bar">
      <button class="add-btn" @click="openAddModal">
        <span>➕</span> 添加联系方式
      </button>
    </div>
    
    <div class="card-list" v-if="contactCards.length > 0">
      <div v-for="card in contactCards" :key="card.id" class="card-item">
        <div class="card-icon">
          <span class="icon-text">{{ card.icon || '📞' }}</span>
        </div>
        <div class="card-info">
          <h3 class="card-title">{{ card.title }}</h3>
          <p class="card-detail" v-if="card.detail">{{ card.detail }}</p>
          <p class="card-hint" v-if="card.hint">{{ card.hint }}</p>
          <div class="card-meta">
            <span class="meta-item">🔢 排序: {{ card.sortOrder }}</span>
            <span class="meta-item">📅 {{ formatDate(card.createTime) }}</span>
          </div>
        </div>
        <div class="card-status">
          <span class="status-badge" :class="{ active: card.status === 1 }">
            {{ card.status === 1 ? '启用' : '禁用' }}
          </span>
        </div>
        <div class="card-actions">
          <button class="action-btn edit" @click="openEditModal(card)">✏️ 编辑</button>
          <button class="action-btn delete" @click="confirmDelete(card)">🗑️ 删除</button>
          <button 
            class="action-btn toggle" 
            :class="card.status === 1 ? 'disable' : 'enable'"
            @click="toggleStatus(card)"
          >
            {{ card.status === 1 ? '❌ 禁用' : '✅ 启用' }}
          </button>
        </div>
      </div>
    </div>
    
    <div class="empty-state" v-else>
      <div class="empty-icon">📞</div>
      <h3>暂无联系方式</h3>
      <p>点击上方按钮添加第一个联系方式</p>
    </div>
    
    <div class="modal-overlay" :class="{ show: showModal }" @click="closeModal">
      <div class="modal-container" @click.stop>
        <div class="modal-header">
          <h2>{{ isEdit ? '编辑联系方式' : '添加联系方式' }}</h2>
          <button class="close-btn" @click="closeModal">×</button>
        </div>
        
        <div class="modal-body">
          <div class="form-group">
            <label class="form-label">图标</label>
            <input 
              type="text" 
              v-model="form.icon" 
              class="form-input"
              placeholder="请输入图标（如：📞、📧、📍）"
            />
            <div class="icon-preview" v-if="form.icon">
              <span class="preview-icon">{{ form.icon }}</span>
              <span class="preview-text">预览</span>
            </div>
          </div>
          
          <div class="form-group">
            <label class="form-label">标题 <span class="required">*</span></label>
            <input 
              type="text" 
              v-model="form.title" 
              class="form-input"
              placeholder="请输入标题（如：客服热线）"
            />
          </div>
          
          <div class="form-group">
            <label class="form-label">详情</label>
            <input 
              type="text" 
              v-model="form.detail" 
              class="form-input"
              placeholder="请输入详情（如：400-123-4567）"
            />
          </div>
          
          <div class="form-group">
            <label class="form-label">提示信息</label>
            <input 
              type="text" 
              v-model="form.hint" 
              class="form-input"
              placeholder="请输入提示信息（如：工作时间：09:00 - 18:00）"
            />
          </div>
          
          <div class="form-row">
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
          <p>确定要删除联系方式 "<strong>{{ deletingCard?.title }}</strong>" 吗？</p>
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

const contactCards = ref([])
const showModal = ref(false)
const showDeleteModal = ref(false)
const isEdit = ref(false)
const isSubmitting = ref(false)
const isDeleting = ref(false)
const errorMessage = ref('')
const successMessage = ref('')
const deletingCard = ref(null)

const form = reactive({
  id: null,
  icon: '',
  title: '',
  detail: '',
  hint: '',
  sortOrder: 0,
  status: 1
})

const resetForm = () => {
  form.id = null
  form.icon = ''
  form.title = ''
  form.detail = ''
  form.hint = ''
  form.sortOrder = 0
  form.status = 1
  errorMessage.value = ''
  successMessage.value = ''
}

const loadContactCards = async () => {
  try {
    const response = await request('/api/admin/contact-cards?pageNum=1&pageSize=100')
    const data = await response.json()
    
    if (data.code === 200) {
      contactCards.value = data.data.records || []
    }
  } catch (error) {
    console.error('加载联系方式失败:', error)
  }
}

const openAddModal = () => {
  resetForm()
  isEdit.value = false
  showModal.value = true
}

const openEditModal = (card) => {
  resetForm()
  isEdit.value = true
  form.id = card.id
  form.icon = card.icon || ''
  form.title = card.title || ''
  form.detail = card.detail || ''
  form.hint = card.hint || ''
  form.sortOrder = card.sortOrder || 0
  form.status = card.status ?? 1
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
      ? `/api/admin/contact-cards/${form.id}` 
      : '/api/admin/contact-cards'
    const method = isEdit.value ? 'PUT' : 'POST'
    
    const response = await request(url, {
      method: method,
      body: JSON.stringify({
        icon: form.icon,
        title: form.title,
        detail: form.detail,
        hint: form.hint,
        sortOrder: form.sortOrder,
        status: form.status
      })
    })
    
    const data = await response.json()
    
    if (data.code === 200) {
      successMessage.value = isEdit.value ? '更新成功' : '创建成功'
      setTimeout(() => {
        closeModal()
        loadContactCards()
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

const confirmDelete = (card) => {
  deletingCard.value = card
  showDeleteModal.value = true
}

const closeDeleteModal = () => {
  showDeleteModal.value = false
  deletingCard.value = null
}

const handleDelete = async () => {
  if (!deletingCard.value) return
  
  isDeleting.value = true
  
  try {
    const response = await request(`/api/admin/contact-cards/${deletingCard.value.id}`, {
      method: 'DELETE'
    })
    
    const data = await response.json()
    
    if (data.code === 200) {
      closeDeleteModal()
      loadContactCards()
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

const toggleStatus = async (card) => {
  const newStatus = card.status === 1 ? 0 : 1
  
  try {
    const response = await request(`/api/admin/contact-cards/${card.id}`, {
      method: 'PUT',
      body: JSON.stringify({
        status: newStatus
      })
    })
    
    const data = await response.json()
    
    if (data.code === 200) {
      card.status = newStatus
    } else {
      alert(data.message || '操作失败')
    }
  } catch (error) {
    console.error('切换状态失败:', error)
    alert('网络错误，请稍后重试')
  }
}

const formatDate = (dateStr) => {
  if (!dateStr) return '-'
  const date = new Date(dateStr)
  return date.toLocaleDateString('zh-CN')
}

onMounted(() => {
  loadContactCards()
})
</script>

<style scoped>
.contact-card-management {
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

.card-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.card-item {
  display: flex;
  align-items: center;
  background: white;
  border-radius: 16px;
  padding: 20px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  transition: all 0.3s ease;
  gap: 20px;
}

.card-item:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.12);
}

.card-icon {
  width: 60px;
  height: 60px;
  background: linear-gradient(135deg, #f5f7fa, #e4e8eb);
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.icon-text {
  font-size: 1.8rem;
}

.card-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.card-title {
  font-size: 1.15rem;
  color: #2c3e50;
  margin: 0;
  font-weight: 600;
}

.card-detail {
  font-size: 1rem;
  color: #6B8E23;
  margin: 0;
  font-weight: bold;
}

.card-hint {
  font-size: 0.85rem;
  color: #7f8c8d;
  margin: 0;
}

.card-meta {
  display: flex;
  gap: 20px;
  flex-wrap: wrap;
  margin-top: 4px;
}

.meta-item {
  color: #95a5a6;
  font-size: 0.9rem;
}

.card-status {
  flex-shrink: 0;
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

.card-actions {
  display: flex;
  flex-direction: column;
  gap: 8px;
  flex-shrink: 0;
}

.action-btn {
  padding: 8px 14px;
  border: none;
  border-radius: 8px;
  font-size: 0.85rem;
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
  max-width: 500px;
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

.icon-preview {
  margin-top: 10px;
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 10px;
  background: #fafbfc;
  border-radius: 8px;
}

.preview-icon {
  font-size: 1.5rem;
}

.preview-text {
  color: #7f8c8d;
  font-size: 0.9rem;
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
  .card-item {
    flex-direction: column;
    align-items: flex-start;
  }
  
  .card-icon {
    width: 50px;
    height: 50px;
  }
  
  .icon-text {
    font-size: 1.5rem;
  }
  
  .card-actions {
    flex-direction: row;
    width: 100%;
  }
  
  .action-btn {
    flex: 1;
    text-align: center;
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
