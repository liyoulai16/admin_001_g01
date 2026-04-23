<template>
  <div class="faq-management">
    <div class="action-bar">
      <button class="add-btn" @click="openAddModal">
        <span>➕</span> 添加常见问题
      </button>
    </div>
    
    <div class="faq-list" v-if="faqs.length > 0">
      <div v-for="faq in faqs" :key="faq.id" class="faq-item">
        <div class="faq-info">
          <h3 class="faq-question">{{ faq.question }}</h3>
          <p class="faq-answer">{{ faq.answer }}</p>
          <div class="faq-meta">
            <span class="meta-item">🔢 排序: {{ faq.sortOrder }}</span>
            <span class="meta-item">📅 {{ formatDate(faq.createTime) }}</span>
          </div>
        </div>
        <div class="faq-status">
          <span class="status-badge" :class="{ active: faq.status === 1 }">
            {{ faq.status === 1 ? '启用' : '禁用' }}
          </span>
        </div>
        <div class="faq-actions">
          <button class="action-btn edit" @click="openEditModal(faq)">✏️ 编辑</button>
          <button 
            class="action-btn toggle" 
            :class="faq.status === 1 ? 'disable' : 'enable'"
            @click="toggleStatus(faq)"
          >
            {{ faq.status === 1 ? '❌ 禁用' : '✅ 启用' }}
          </button>
          <button class="action-btn delete" @click="confirmDelete(faq)">🗑️ 删除</button>
        </div>
      </div>
    </div>
    
    <div class="pagination-section" v-if="faqs.length > 0">
        <div class="pagination">
          <button 
            class="page-btn first-btn" 
            :disabled="currentPage === 1"
            @click="goToFirstPage"
          >
            首页
          </button>
          
          <button 
            class="page-btn prev-btn" 
            :disabled="currentPage === 1"
            @click="goToPrevPage"
          >
            上一页
          </button>
          
          <button 
            v-for="page in visiblePages" 
            :key="page"
            class="page-btn" 
            :class="{ active: currentPage === page, 'ellipsis': page === '...' }"
            @click="page !== '...' && goToPage(page)"
            :disabled="page === '...'"
          >
            {{ page }}
          </button>
          
          <button 
            class="page-btn next-btn" 
            :disabled="currentPage === totalPages || totalPages === 0"
            @click="goToNextPage"
          >
            下一页
          </button>
          
          <button 
            class="page-btn last-btn" 
            :disabled="currentPage === totalPages || totalPages === 0"
            @click="goToLastPage"
          >
            尾页
          </button>
          
          <div class="page-size-select">
            <span class="size-label">每页显示</span>
            <select v-model="pageSize" @change="handlePageSizeChange" class="size-select">
              <option :value="10">10条</option>
              <option :value="20">20条</option>
              <option :value="50">50条</option>
              <option :value="100">100条</option>
            </select>
          </div>
          
          <div class="page-jump">
            <span class="jump-label">跳转到</span>
            <input 
              type="number" 
              v-model="jumpPageInput" 
              min="1"
              :max="totalPages || 1"
              @keyup.enter="handleJumpPage"
              class="jump-input"
              placeholder="页码"
            />
            <span class="jump-label">页</span>
            <button class="jump-btn" @click="handleJumpPage">
              确定
            </button>
          </div>
          
          <div class="page-info">
            共 <span class="info-highlight">{{ totalPages || 1 }}</span> 页，
            当前第 <span class="info-highlight">{{ currentPage }}</span> 页，
            共 <span class="info-highlight">{{ total }}</span> 条记录
          </div>
        </div>
        
        <div class="pagination-toast" :class="{ show: showToast }">
          <span class="toast-icon">{{ toastIcon }}</span>
          <span class="toast-message">{{ toastMessage }}</span>
        </div>
      </div>
    
    <div class="empty-state" v-else>
      <div class="empty-icon">❓</div>
      <h3>暂无常见问题</h3>
      <p>点击上方按钮添加常见问题</p>
    </div>
    
    <div class="modal-overlay" :class="{ show: showModal }" @click="closeModal">
      <div class="modal-container" @click.stop>
        <div class="modal-header">
          <h2>{{ isEdit ? '编辑常见问题' : '添加常见问题' }}</h2>
          <button class="close-btn" @click="closeModal">×</button>
        </div>
        
        <div class="modal-body">
          <div class="form-group">
            <label class="form-label">问题 <span class="required">*</span></label>
            <input 
              type="text" 
              v-model="form.question" 
              class="form-input"
              placeholder="请输入问题"
            />
          </div>
          
          <div class="form-group">
            <label class="form-label">答案 <span class="required">*</span></label>
            <textarea 
              v-model="form.answer" 
              class="form-textarea"
              placeholder="请输入答案"
              rows="6"
            ></textarea>
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
          <p>确定要删除该常见问题吗？</p>
          <p class="question-preview" v-if="deletingFAQ?.question">{{ deletingFAQ.question }}</p>
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
import { ref, reactive, computed, onMounted } from 'vue'
import request from '../../utils/request'

const faqs = ref([])
const showModal = ref(false)
const showDeleteModal = ref(false)
const isEdit = ref(false)
const isSubmitting = ref(false)
const isDeleting = ref(false)
const errorMessage = ref('')
const successMessage = ref('')
const deletingFAQ = ref(null)
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const totalPages = ref(0)
const jumpPageInput = ref('')
const showToast = ref(false)
const toastMessage = ref('')
const toastIcon = ref('⚠️')
let toastTimer = null

const form = reactive({
  id: null,
  question: '',
  answer: '',
  sortOrder: 0,
  status: 1
})

const visiblePages = computed(() => {
  const current = currentPage.value
  const totalPagesVal = totalPages.value
  const delta = 2
  const range = []
  const rangeWithDots = []
  let l

  for (let i = 1; i <= totalPagesVal; i++) {
    if (i === 1 || i === totalPagesVal || (i >= current - delta && i <= current + delta)) {
      range.push(i)
    }
  }

  for (const i of range) {
    if (l) {
      if (i - l === 2) {
        rangeWithDots.push(l + 1)
      } else if (i - l !== 1) {
        rangeWithDots.push('...')
      }
    }
    rangeWithDots.push(i)
    l = i
  }

  return rangeWithDots
})

const showToastMessage = (message, icon = '⚠️') => {
  toastMessage.value = message
  toastIcon.value = icon
  showToast.value = true
  
  if (toastTimer) {
    clearTimeout(toastTimer)
  }
  
  toastTimer = setTimeout(() => {
    showToast.value = false
  }, 2000)
}

const resetForm = () => {
  form.id = null
  form.question = ''
  form.answer = ''
  form.sortOrder = 0
  form.status = 1
  errorMessage.value = ''
  successMessage.value = ''
}

const loadFAQs = async () => {
  try {
    const response = await request(`/api/admin/faqs?pageNum=${currentPage.value}&pageSize=${pageSize.value}`)
    const data = await response.json()
    
    if (data.code === 200) {
      faqs.value = data.data.records || []
      total.value = data.data.total || 0
      totalPages.value = data.data.pages || 0
    }
  } catch (error) {
    console.error('加载常见问题失败:', error)
  }
}

const openAddModal = () => {
  resetForm()
  isEdit.value = false
  showModal.value = true
}

const openEditModal = (faq) => {
  resetForm()
  isEdit.value = true
  form.id = faq.id
  form.question = faq.question || ''
  form.answer = faq.answer || ''
  form.sortOrder = faq.sortOrder || 0
  form.status = faq.status ?? 1
  showModal.value = true
}

const closeModal = () => {
  showModal.value = false
}

const handleSubmit = async () => {
  errorMessage.value = ''
  successMessage.value = ''
  
  if (!form.question.trim()) {
    errorMessage.value = '请输入问题'
    return
  }
  if (!form.answer.trim()) {
    errorMessage.value = '请输入答案'
    return
  }
  
  isSubmitting.value = true
  
  try {
    const url = isEdit.value 
      ? `/api/admin/faqs/${form.id}` 
      : '/api/admin/faqs'
    const method = isEdit.value ? 'PUT' : 'POST'
    
    const response = await request(url, {
      method: method,
      body: JSON.stringify({
        question: form.question,
        answer: form.answer,
        sortOrder: form.sortOrder,
        status: form.status
      })
    })
    
    const data = await response.json()
    
    if (data.code === 200) {
      successMessage.value = isEdit.value ? '更新成功' : '创建成功'
      setTimeout(() => {
        closeModal()
        loadFAQs()
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

const confirmDelete = (faq) => {
  deletingFAQ.value = faq
  showDeleteModal.value = true
}

const closeDeleteModal = () => {
  showDeleteModal.value = false
  deletingFAQ.value = null
}

const handleDelete = async () => {
  if (!deletingFAQ.value) return
  
  isDeleting.value = true
  
  try {
    const response = await request(`/api/admin/faqs/${deletingFAQ.value.id}`, {
      method: 'DELETE'
    })
    
    const data = await response.json()
    
    if (data.code === 200) {
      closeDeleteModal()
      loadFAQs()
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

const toggleStatus = async (faq) => {
  const newStatus = faq.status === 1 ? 0 : 1
  
  try {
    const response = await request(`/api/admin/faqs/${faq.id}`, {
      method: 'PUT',
      body: JSON.stringify({
        status: newStatus
      })
    })
    
    const data = await response.json()
    
    if (data.code === 200) {
      faq.status = newStatus
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

const goToPage = (page) => {
  if (page < 1 || page > totalPages.value) {
    showToastMessage('页码超出范围', '⚠️')
    return
  }
  currentPage.value = page
  loadFAQs()
}

const goToPrevPage = () => {
  if (currentPage.value > 1) {
    currentPage.value--
    loadFAQs()
  }
}

const goToNextPage = () => {
  if (currentPage.value < totalPages.value) {
    currentPage.value++
    loadFAQs()
  }
}

const goToFirstPage = () => {
  if (currentPage.value !== 1) {
    currentPage.value = 1
    loadFAQs()
  }
}

const goToLastPage = () => {
  if (currentPage.value !== totalPages.value && totalPages.value > 0) {
    currentPage.value = totalPages.value
    loadFAQs()
  }
}

const handlePageSizeChange = () => {
  currentPage.value = 1
  loadFAQs()
}

const handleJumpPage = () => {
  const page = parseInt(jumpPageInput.value)
  
  if (!jumpPageInput.value || isNaN(page)) {
    showToastMessage('请输入有效的页码', '⚠️')
    return
  }
  
  if (page < 1 || page > totalPages.value) {
    showToastMessage(`请输入1到${totalPages.value}之间的页码`, '⚠️')
    return
  }
  
  currentPage.value = page
  jumpPageInput.value = ''
  loadFAQs()
}

onMounted(() => {
  loadFAQs()
})
</script>

<style scoped>
.faq-management {
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

.faq-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.faq-item {
  display: flex;
  align-items: flex-start;
  background: white;
  border-radius: 16px;
  padding: 20px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  transition: all 0.3s ease;
  gap: 20px;
}

.faq-item:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.12);
}

.faq-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.faq-question {
  font-size: 1.1rem;
  color: #2c3e50;
  margin: 0;
  font-weight: 600;
}

.faq-answer {
  font-size: 0.95rem;
  color: #555;
  margin: 0;
  line-height: 1.6;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.faq-meta {
  display: flex;
  gap: 20px;
  flex-wrap: wrap;
  margin-top: 4px;
}

.meta-item {
  color: #95a5a6;
  font-size: 0.9rem;
}

.faq-status {
  flex-shrink: 0;
  margin-top: 4px;
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

.faq-actions {
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

.question-preview {
  background: #f8f9fa;
  padding: 12px;
  border-radius: 8px;
  font-size: 0.95rem;
  color: #555;
  margin: 8px 0;
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

.pagination-section {
  margin-top: 24px;
  position: relative;
}

.pagination {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  flex-wrap: wrap;
  padding: 16px;
  background: white;
  border-radius: 16px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
}

.page-btn {
  min-width: 40px;
  height: 40px;
  padding: 0 14px;
  border: 2px solid #e4e8eb;
  background: white;
  color: #555;
  border-radius: 10px;
  font-size: 0.9rem;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  justify-content: center;
}

.page-btn:hover:not(:disabled):not(.ellipsis):not(.active) {
  border-color: #6B8E23;
  background: #f8faf6;
  color: #6B8E23;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(107, 142, 35, 0.2);
}

.page-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
  background: #f5f7fa;
}

.page-btn.active {
  background: linear-gradient(135deg, #6B8E23, #8FBC8F);
  color: white;
  border-color: #6B8E23;
  box-shadow: 0 4px 15px rgba(107, 142, 35, 0.4);
}

.page-btn.first-btn,
.page-btn.prev-btn,
.page-btn.next-btn,
.page-btn.last-btn {
  padding: 0 16px;
  min-width: auto;
}

.page-size-select {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-left: 16px;
}

.size-label {
  font-size: 0.9rem;
  color: #7f8c8d;
  font-weight: 500;
}

.size-select {
  padding: 8px 14px;
  border: 2px solid #e4e8eb;
  border-radius: 10px;
  font-size: 0.9rem;
  color: #2c3e50;
  background: white;
  cursor: pointer;
  outline: none;
  transition: all 0.3s ease;
  font-weight: 500;
}

.size-select:focus {
  border-color: #6B8E23;
  box-shadow: 0 0 0 4px rgba(107, 142, 35, 0.1);
}

.page-jump {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-left: 16px;
}

.jump-label {
  font-size: 0.9rem;
  color: #7f8c8d;
  font-weight: 500;
}

.jump-input {
  width: 60px;
  padding: 8px 12px;
  border: 2px solid #e4e8eb;
  border-radius: 10px;
  font-size: 0.9rem;
  color: #2c3e50;
  text-align: center;
  outline: none;
  transition: all 0.3s ease;
  font-weight: 500;
}

.jump-input:focus {
  border-color: #6B8E23;
  box-shadow: 0 0 0 4px rgba(107, 142, 35, 0.1);
}

.jump-btn {
  padding: 8px 16px;
  border: none;
  border-radius: 10px;
  font-size: 0.9rem;
  font-weight: 600;
  background: linear-gradient(135deg, #6B8E23, #8FBC8F);
  color: white;
  cursor: pointer;
  transition: all 0.3s ease;
}

.jump-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 15px rgba(107, 142, 35, 0.4);
}

.page-info {
  font-size: 0.9rem;
  color: #7f8c8d;
  margin-left: 16px;
  font-weight: 500;
}

.info-highlight {
  color: #6B8E23;
  font-weight: 600;
  font-size: 1rem;
}

.pagination-toast {
  position: absolute;
  top: -50px;
  left: 50%;
  transform: translateX(-50%);
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 20px;
  background: #fff3cd;
  border: 1px solid #ffc107;
  border-radius: 10px;
  color: #856404;
  font-size: 0.9rem;
  font-weight: 500;
  opacity: 0;
  visibility: hidden;
  transition: all 0.3s ease;
  z-index: 10;
  box-shadow: 0 4px 15px rgba(255, 193, 7, 0.2);
}

.pagination-toast.show {
  opacity: 1;
  visibility: visible;
  top: -45px;
}

.toast-icon {
  font-size: 1.1rem;
}

@media (max-width: 768px) {
  .faq-item {
    flex-direction: column;
    align-items: flex-start;
  }
  
  .faq-status {
    margin-top: 0;
  }
  
  .faq-actions {
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
