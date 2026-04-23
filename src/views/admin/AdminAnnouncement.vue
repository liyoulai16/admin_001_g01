<template>
  <div class="announcement-management">
    <div class="action-bar">
      <div class="search-box">
        <input 
          type="text" 
          v-model="searchKeyword" 
          placeholder="搜索公告标题或内容..."
          @keyup.enter="handleSearch"
        />
        <button class="search-btn" @click="handleSearch">搜索</button>
        <button class="reset-btn" @click="resetSearch">重置</button>
      </div>
      <button class="add-btn" @click="openAddModal">
        <span>📢</span> 发布公告
      </button>
    </div>
    
    <div class="announcement-list" v-if="announcements.length > 0">
      <div v-for="announcement in announcements" :key="announcement.id" class="announcement-card">
        <div class="card-header">
          <div class="card-tags">
            <span v-if="announcement.isPinned === 1" class="tag pin-tag">📌 置顶</span>
            <span :class="['tag', 'status-tag', { 'status-published': announcement.status === 1, 'status-draft': announcement.status === 0 }]">
              {{ announcement.status === 1 ? '已发布' : '草稿' }}
            </span>
          </div>
          <div class="card-meta">
            <span class="meta-item">
              <span class="meta-icon">👁️</span>
              <span>{{ announcement.viewCount || 0 }} 浏览</span>
            </span>
            <span class="meta-item">
              <span class="meta-icon">📅</span>
              <span>{{ formatDate(announcement.createTime) }}</span>
            </span>
          </div>
        </div>
        <h3 class="card-title">{{ announcement.title }}</h3>
        <p class="card-excerpt">{{ announcement.summary || truncateContent(announcement.content, 120) }}</p>
        <div class="card-actions">
          <button class="action-btn edit" @click="openEditModal(announcement)">✏️ 编辑</button>
          <button class="action-btn view" @click="openPreview(announcement)">👁️ 预览</button>
          <button 
            class="action-btn pin" 
            :class="{ pinned: announcement.isPinned === 1 }"
            @click="togglePin(announcement)"
          >
            {{ announcement.isPinned === 1 ? '📌 取消置顶' : '📌 置顶' }}
          </button>
          <button 
            class="action-btn status" 
            :class="{ active: announcement.status === 1 }"
            @click="toggleStatus(announcement)"
          >
            {{ announcement.status === 1 ? '❌ 下架' : '✅ 发布' }}
          </button>
          <button class="action-btn delete" @click="confirmDelete(announcement)">🗑️ 删除</button>
        </div>
      </div>
    </div>
    
    <div class="empty-state" v-else>
      <div class="empty-icon">📢</div>
      <h3>暂无公告</h3>
      <p>点击上方按钮发布第一个公告</p>
    </div>

    <div v-if="totalPages > 1" class="pagination-section">
      <div class="pagination">
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
        
        <div class="page-info">
          共 <span class="info-highlight">{{ totalPages || 1 }}</span> 页，
          当前第 <span class="info-highlight">{{ currentPage }}</span> 页，
          共 <span class="info-highlight">{{ total }}</span> 条记录
        </div>
      </div>
    </div>
    
    <div class="modal-overlay" :class="{ show: showModal }" @click="closeModal">
      <div class="modal-container" @click.stop>
        <div class="modal-header">
          <h2>{{ isEdit ? '编辑公告' : '发布公告' }}</h2>
          <button class="close-btn" @click="closeModal">×</button>
        </div>
        
        <div class="modal-body">
          <div class="form-group">
            <label class="form-label">公告标题 <span class="required">*</span></label>
            <input 
              type="text" 
              v-model="form.title" 
              class="form-input"
              placeholder="请输入公告标题"
              maxlength="200"
            />
          </div>
          
          <div class="form-group">
            <label class="form-label">公告摘要</label>
            <textarea 
              v-model="form.summary" 
              class="form-textarea"
              placeholder="请输入公告摘要（可选，用于列表展示）"
              rows="2"
              maxlength="500"
            ></textarea>
          </div>
          
          <div class="form-group">
            <label class="form-label">公告内容 <span class="required">*</span></label>
            <textarea 
              v-model="form.content" 
              class="form-textarea large"
              placeholder="请输入公告内容"
              rows="8"
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
              <label class="form-label">发布状态</label>
              <div class="radio-group">
                <label class="radio-label">
                  <input type="radio" v-model="form.status" :value="1" /> 立即发布
                </label>
                <label class="radio-label">
                  <input type="radio" v-model="form.status" :value="0" /> 保存草稿
                </label>
              </div>
            </div>
          </div>
          
          <div class="form-group">
            <label class="form-checkbox">
              <input type="checkbox" v-model="form.isPinned" /> 
              <span class="checkbox-text">置顶公告</span>
              <span class="checkbox-hint">（置顶公告将优先展示在列表顶部）</span>
            </label>
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
          <p>确定要删除公告 "<strong>{{ deletingAnnouncement?.title }}</strong>" 吗？</p>
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

    <div class="modal-overlay" :class="{ show: showPreview }" @click="closePreview">
      <div class="modal-container large" @click.stop>
        <div class="modal-header">
          <div class="header-left">
            <span v-if="previewAnnouncement?.isPinned === 1" class="modal-badge pin">📌 置顶</span>
            <span :class="['modal-badge', { 'status-published': previewAnnouncement?.status === 1, 'status-draft': previewAnnouncement?.status === 0 }]">
              {{ previewAnnouncement?.status === 1 ? '已发布' : '草稿' }}
            </span>
            <h3 class="modal-title">{{ previewAnnouncement?.title }}</h3>
          </div>
          <button class="modal-close-btn" @click="closePreview">×</button>
        </div>
        <div class="modal-body">
          <div v-if="previewAnnouncement" class="announcement-detail">
            <div class="detail-meta">
              <span class="meta-item">
                <span class="meta-icon">👁️</span>
                <span>{{ previewAnnouncement.viewCount || 0 }} 次浏览</span>
              </span>
              <span class="meta-item">
                <span class="meta-icon">📅</span>
                <span>发布时间：{{ formatDateTime(previewAnnouncement.createTime) }}</span>
              </span>
            </div>
            <div v-if="previewAnnouncement.summary" class="detail-summary">
              <span class="summary-label">摘要：</span>
              <span>{{ previewAnnouncement.summary }}</span>
            </div>
            <div class="detail-content">
              <div v-html="formatContent(previewAnnouncement.content)"></div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import request from '../../utils/request'

const announcements = ref([])
const currentPage = ref(1)
const pageSize = ref(10)
const totalPages = ref(1)
const total = ref(0)
const searchKeyword = ref('')
const showModal = ref(false)
const showDeleteModal = ref(false)
const showPreview = ref(false)
const isEdit = ref(false)
const isSubmitting = ref(false)
const isDeleting = ref(false)
const errorMessage = ref('')
const successMessage = ref('')
const deletingAnnouncement = ref(null)
const previewAnnouncement = ref(null)

const form = reactive({
  id: null,
  title: '',
  content: '',
  summary: '',
  isPinned: false,
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

const resetForm = () => {
  form.id = null
  form.title = ''
  form.content = ''
  form.summary = ''
  form.isPinned = false
  form.sortOrder = 0
  form.status = 1
  errorMessage.value = ''
  successMessage.value = ''
}

const truncateContent = (content, maxLength) => {
  if (!content) return ''
  if (content.length <= maxLength) return content
  return content.substring(0, maxLength) + '...'
}

const formatContent = (content) => {
  if (!content) return ''
  return content.replace(/\n/g, '</p><p>')
}

const formatDate = (dateStr) => {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  return date.toLocaleDateString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit'
  }).replace(/\//g, '-')
}

const formatDateTime = (dateStr) => {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  return date.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  }).replace(/\//g, '-')
}

const loadAnnouncements = async () => {
  try {
    let url = `/api/admin/announcements?pageNum=${currentPage.value}&pageSize=${pageSize.value}`
    if (searchKeyword.value.trim()) {
      url += `&keyword=${encodeURIComponent(searchKeyword.value.trim())}`
    }
    const response = await request(url)
    const data = await response.json()
    
    if (data.code === 200) {
      announcements.value = data.data.records || []
      totalPages.value = data.data.pages || 1
      total.value = data.data.total || 0
    }
  } catch (error) {
    console.error('加载公告列表失败:', error)
  }
}

const openAddModal = () => {
  resetForm()
  isEdit.value = false
  showModal.value = true
}

const openEditModal = (announcement) => {
  resetForm()
  isEdit.value = true
  form.id = announcement.id
  form.title = announcement.title || ''
  form.content = announcement.content || ''
  form.summary = announcement.summary || ''
  form.isPinned = announcement.isPinned === 1
  form.sortOrder = announcement.sortOrder || 0
  form.status = announcement.status ?? 1
  showModal.value = true
}

const openPreview = (announcement) => {
  previewAnnouncement.value = announcement
  showPreview.value = true
}

const closePreview = () => {
  showPreview.value = false
  previewAnnouncement.value = null
}

const closeModal = () => {
  showModal.value = false
}

const handleSubmit = async () => {
  errorMessage.value = ''
  successMessage.value = ''
  
  if (!form.title.trim()) {
    errorMessage.value = '请输入公告标题'
    return
  }
  
  if (!form.content.trim()) {
    errorMessage.value = '请输入公告内容'
    return
  }
  
  isSubmitting.value = true
  
  try {
    const url = isEdit.value 
      ? `/api/admin/announcements/${form.id}` 
      : '/api/admin/announcements'
    const method = isEdit.value ? 'PUT' : 'POST'
    
    const response = await request(url, {
      method: method,
      body: JSON.stringify({
        title: form.title,
        content: form.content,
        summary: form.summary || null,
        isPinned: form.isPinned ? 1 : 0,
        sortOrder: form.sortOrder,
        status: form.status
      })
    })
    
    const data = await response.json()
    
    if (data.code === 200) {
      successMessage.value = isEdit.value ? '更新成功' : '创建成功'
      setTimeout(() => {
        closeModal()
        loadAnnouncements()
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

const confirmDelete = (announcement) => {
  deletingAnnouncement.value = announcement
  showDeleteModal.value = true
}

const closeDeleteModal = () => {
  showDeleteModal.value = false
  deletingAnnouncement.value = null
}

const handleDelete = async () => {
  if (!deletingAnnouncement.value) return
  
  isDeleting.value = true
  
  try {
    const response = await request(`/api/admin/announcements/${deletingAnnouncement.value.id}`, {
      method: 'DELETE'
    })
    
    const data = await response.json()
    
    if (data.code === 200) {
      closeDeleteModal()
      loadAnnouncements()
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

const togglePin = async (announcement) => {
  const newPinValue = announcement.isPinned === 1 ? 0 : 1
  
  try {
    const response = await request(`/api/admin/announcements/${announcement.id}/toggle-pin`, {
      method: 'PUT'
    })
    
    const data = await response.json()
    
    if (data.code === 200) {
      announcement.isPinned = newPinValue
    } else {
      alert(data.message || '操作失败')
    }
  } catch (error) {
    console.error('切换置顶状态失败:', error)
    alert('网络错误，请稍后重试')
  }
}

const toggleStatus = async (announcement) => {
  const newStatus = announcement.status === 1 ? 0 : 1
  
  try {
    const response = await request(`/api/admin/announcements/${announcement.id}/toggle-status`, {
      method: 'PUT'
    })
    
    const data = await response.json()
    
    if (data.code === 200) {
      announcement.status = newStatus
    } else {
      alert(data.message || '操作失败')
    }
  } catch (error) {
    console.error('切换发布状态失败:', error)
    alert('网络错误，请稍后重试')
  }
}

const handleSearch = () => {
  currentPage.value = 1
  loadAnnouncements()
}

const resetSearch = () => {
  searchKeyword.value = ''
  currentPage.value = 1
  loadAnnouncements()
}

const goToPage = (page) => {
  const totalPagesVal = totalPages.value || 1
  if (page < 1 || page > totalPagesVal) return
  if (page === currentPage.value) return
  
  currentPage.value = page
  loadAnnouncements()
}

const goToPrevPage = () => {
  if (currentPage.value > 1) {
    goToPage(currentPage.value - 1)
  }
}

const goToNextPage = () => {
  if (currentPage.value < totalPages.value) {
    goToPage(currentPage.value + 1)
  }
}

onMounted(() => {
  loadAnnouncements()
})
</script>

<style scoped>
.announcement-management {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.action-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 20px;
}

.search-box {
  display: flex;
  gap: 10px;
  flex: 1;
  max-width: 500px;
}

.search-box input {
  flex: 1;
  padding: 12px 16px;
  border: 2px solid #e4e8eb;
  border-radius: 10px;
  font-size: 0.95rem;
  outline: none;
  transition: border-color 0.3s ease;
}

.search-box input:focus {
  border-color: #6B8E23;
}

.search-btn,
.reset-btn {
  padding: 12px 20px;
  border: none;
  border-radius: 10px;
  font-size: 0.95rem;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;
}

.search-btn {
  background: linear-gradient(135deg, #6B8E23, #8FBC8F);
  color: white;
}

.search-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 5px 15px rgba(107, 142, 35, 0.3);
}

.reset-btn {
  background: #f5f7fa;
  color: #7f8c8d;
  border: 1px solid #e4e8eb;
}

.reset-btn:hover {
  background: #e8ecef;
  color: #555;
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

.announcement-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.announcement-card {
  background: white;
  border-radius: 16px;
  padding: 25px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  transition: all 0.3s ease;
}

.announcement-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.12);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
}

.card-tags {
  display: flex;
  gap: 8px;
}

.tag {
  padding: 4px 12px;
  border-radius: 20px;
  font-size: 0.75rem;
  font-weight: 600;
}

.pin-tag {
  background: linear-gradient(135deg, #f59e0b, #d97706);
  color: white;
}

.status-tag {
  color: white;
}

.status-tag.status-published {
  background: linear-gradient(135deg, #16a34a, #15803d);
}

.status-tag.status-draft {
  background: linear-gradient(135deg, #64748b, #475569);
}

.card-meta {
  display: flex;
  gap: 20px;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 5px;
  font-size: 0.85rem;
  color: #95a5a6;
}

.meta-icon {
  font-size: 1rem;
}

.card-title {
  font-size: 1.2rem;
  color: #2c3e50;
  margin: 0 0 10px 0;
  font-weight: 600;
  line-height: 1.4;
}

.card-excerpt {
  font-size: 0.95rem;
  color: #7f8c8d;
  line-height: 1.6;
  margin: 0 0 15px 0;
}

.card-actions {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  padding-top: 15px;
  border-top: 1px solid #e4e8eb;
}

.action-btn {
  padding: 8px 16px;
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

.action-btn.view {
  background: #f0fdf4;
  color: #16a34a;
}

.action-btn.view:hover {
  background: #16a34a;
  color: white;
}

.action-btn.pin {
  background: #fff9e6;
  color: #d97706;
}

.action-btn.pin:hover {
  background: #d97706;
  color: white;
}

.action-btn.pin.pinned {
  background: #fef3c7;
  color: #92400e;
}

.action-btn.status {
  background: #fef3c7;
  color: #d97706;
}

.action-btn.status:hover {
  background: #d97706;
  color: white;
}

.action-btn.status.active {
  background: #fef2f2;
  color: #dc2626;
}

.action-btn.status.active:hover {
  background: #dc2626;
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

.pagination-section {
  display: flex;
  justify-content: center;
  padding-top: 20px;
}

.pagination {
  display: flex;
  align-items: center;
  gap: 8px;
  flex-wrap: wrap;
  justify-content: center;
  row-gap: 12px;
}

.page-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  min-width: 40px;
  height: 40px;
  padding: 0 16px;
  background: white;
  border: 2px solid #e4e8eb;
  border-radius: 8px;
  font-size: 0.95rem;
  cursor: pointer;
  transition: all 0.3s ease;
}

.page-btn:hover:not(:disabled) {
  border-color: #6B8E23;
  color: #6B8E23;
}

.page-btn.active {
  background: linear-gradient(135deg, #6B8E23, #8FBC8F);
  color: white;
  border-color: transparent;
}

.page-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.page-btn.ellipsis {
  border: none;
  background: transparent;
  cursor: default;
}

.page-info {
  display: flex;
  align-items: center;
  gap: 4px;
  padding: 8px 16px;
  background: white;
  border: 1px solid #e4e8eb;
  border-radius: 8px;
  font-size: 0.9rem;
  color: #555;
}

.info-highlight {
  font-weight: bold;
  color: #6B8E23;
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

.modal-container.large {
  max-width: 800px;
}

.modal-container.small {
  max-width: 400px;
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

.form-textarea.large {
  min-height: 200px;
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

.form-checkbox {
  display: flex;
  align-items: center;
  gap: 10px;
  cursor: pointer;
  padding: 12px 16px;
  background: #fafbfc;
  border-radius: 10px;
  border: 2px solid transparent;
  transition: all 0.3s ease;
}

.form-checkbox:hover {
  background: #f5f7fa;
  border-color: #e4e8eb;
}

.form-checkbox input {
  width: 20px;
  height: 20px;
  accent-color: #6B8E23;
}

.checkbox-text {
  font-size: 0.95rem;
  font-weight: 600;
  color: #2c3e50;
}

.checkbox-hint {
  font-size: 0.85rem;
  color: #7f8c8d;
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

.header-left {
  display: flex;
  flex-direction: column;
  gap: 10px;
  flex: 1;
  margin-right: 20px;
}

.modal-badge {
  display: inline-flex;
  align-items: center;
  padding: 4px 12px;
  border-radius: 20px;
  font-size: 0.75rem;
  font-weight: 600;
  width: fit-content;
}

.modal-badge.pin {
  background: linear-gradient(135deg, #f59e0b, #d97706);
  color: white;
}

.modal-badge.status-published {
  background: linear-gradient(135deg, #16a34a, #15803d);
  color: white;
}

.modal-badge.status-draft {
  background: linear-gradient(135deg, #64748b, #475569);
  color: white;
}

.modal-title {
  font-size: 1.3rem;
  font-weight: 600;
  color: #2c3e50;
  margin: 0;
  line-height: 1.4;
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
  flex-shrink: 0;
}

.modal-close-btn:hover {
  background: #C45B1A;
  color: white;
  transform: rotate(90deg);
}

.announcement-detail {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.detail-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 20px;
  padding: 15px 20px;
  background: linear-gradient(135deg, #f8f9fa, #ffffff);
  border-radius: 10px;
}

.detail-summary {
  display: flex;
  gap: 10px;
  padding: 15px 20px;
  background: linear-gradient(135deg, #fff9e6, #fffef5);
  border-radius: 10px;
  border-left: 4px solid #f59e0b;
}

.summary-label {
  font-weight: 600;
  color: #d97706;
}

.detail-summary span:last-child {
  color: #555;
  line-height: 1.6;
}

.detail-content {
  font-size: 1rem;
  line-height: 2;
  color: #555;
}

.detail-content p {
  margin-bottom: 15px;
  text-indent: 2em;
}

.detail-content p:last-child {
  margin-bottom: 0;
}

@media (max-width: 768px) {
  .action-bar {
    flex-direction: column;
    align-items: stretch;
  }

  .search-box {
    max-width: none;
  }

  .form-row {
    flex-direction: column;
    gap: 20px;
  }

  .card-actions {
    flex-wrap: wrap;
  }

  .action-btn {
    flex: 1;
    min-width: calc(50% - 5px);
    justify-content: center;
  }

  .modal-container {
    width: 95%;
    margin: 10px;
    max-height: 95vh;
  }

  .modal-header {
    padding: 20px;
    flex-direction: column;
    gap: 15px;
    align-items: flex-start;
  }

  .header-left {
    margin-right: 0;
    width: 100%;
  }

  .modal-title {
    font-size: 1.1rem;
  }

  .modal-body {
    padding: 20px;
  }

  .form-checkbox {
    flex-wrap: wrap;
  }

  .checkbox-hint {
    width: 100%;
    margin-top: 5px;
  }

  .pagination {
    flex-direction: column;
  }

  .page-info {
    order: -1;
  }

  .card-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 10px;
  }

  .card-meta {
    width: 100%;
    justify-content: space-between;
  }
}
</style>
