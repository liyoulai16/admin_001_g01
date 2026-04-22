<template>
  <div class="message-management">
    <div class="search-bar">
      <div class="search-row">
        <div class="search-item">
          <label class="search-label">关键词</label>
          <input 
            type="text" 
            v-model="searchForm.keyword" 
            class="search-input"
            placeholder="姓名/电话/邮箱/内容"
            @keyup.enter="handleSearch"
          />
        </div>
        <div class="search-item">
          <label class="search-label">咨询类型</label>
          <select v-model="searchForm.type" class="search-select">
            <option value="">全部</option>
            <option value="service">服务咨询</option>
            <option value="complaint">投诉建议</option>
            <option value="cooperation">商务合作</option>
            <option value="other">其他问题</option>
          </select>
        </div>
        <div class="search-item">
          <label class="search-label">状态</label>
          <select v-model="searchForm.status" class="search-select">
            <option value="">全部</option>
            <option value="0">未处理</option>
            <option value="1">已处理</option>
            <option value="2">已回复</option>
          </select>
        </div>
        <div class="search-item actions">
          <button class="btn-search" @click="handleSearch">
            <span>🔍</span> 搜索
          </button>
          <button class="btn-reset" @click="handleReset">
            <span>🔄</span> 重置
          </button>
        </div>
      </div>
    </div>
    
    <div class="message-list" v-if="messages.length > 0">
      <div v-for="message in messages" :key="message.id" class="message-card" :class="{ 'is-new': message.status === 0 }">
        <div class="message-header">
          <div class="message-info">
            <span class="message-name">{{ message.name }}</span>
            <span class="message-phone">📞 {{ message.phone }}</span>
            <span v-if="message.email" class="message-email">📧 {{ message.email }}</span>
          </div>
          <div class="message-meta">
            <span class="type-badge" :class="getMessageTypeClass(message.type)">
              {{ getMessageTypeLabel(message.type) }}
            </span>
            <span class="status-badge" :class="getStatusClass(message.status)">
              {{ getStatusLabel(message.status) }}
            </span>
          </div>
        </div>
        
        <div class="message-content">
          <div class="content-label">留言内容：</div>
          <p class="content-text">{{ message.message }}</p>
        </div>
        
        <div v-if="message.reply" class="message-reply">
          <div class="reply-label">管理员回复：</div>
          <p class="reply-text">{{ message.reply }}</p>
          <div class="reply-meta">
            <span v-if="message.replyTime">回复时间：{{ formatDateTime(message.replyTime) }}</span>
          </div>
        </div>
        
        <div class="message-footer">
          <div class="message-time">
            <span>📅 提交时间：{{ formatDateTime(message.createTime) }}</span>
          </div>
          <div class="message-actions">
            <button class="action-btn view" @click="openViewModal(message)">
              👁️ 详情
            </button>
            <button 
              v-if="message.status !== 2" 
              class="action-btn reply" 
              @click="openReplyModal(message)"
            >
              💬 回复
            </button>
            <button 
              class="action-btn status" 
              :class="message.status === 0 ? 'process' : 'unprocess'"
              @click="toggleStatus(message)"
            >
              {{ message.status === 0 ? '✅ 标记已处理' : '↩️ 标记未处理' }}
            </button>
            <button class="action-btn delete" @click="confirmDelete(message)">
              🗑️ 删除
            </button>
          </div>
        </div>
      </div>
    </div>
    
    <div class="empty-state" v-else>
      <div class="empty-icon">📭</div>
      <h3>暂无留言</h3>
      <p>当前没有符合条件的留言</p>
    </div>
    
    <div class="pagination" v-if="total > 0">
      <span class="total-info">共 {{ total }} 条记录</span>
      <div class="pagination-controls">
        <button 
          class="page-btn" 
          :disabled="currentPage === 1" 
          @click="currentPage > 1 && goToPage(currentPage - 1)"
        >
          上一页
        </button>
        <span class="page-info">第 {{ currentPage }} / {{ totalPages }} 页</span>
        <button 
          class="page-btn" 
          :disabled="currentPage === totalPages" 
          @click="currentPage < totalPages && goToPage(currentPage + 1)"
        >
          下一页
        </button>
      </div>
    </div>
    
    <div class="modal-overlay" :class="{ show: showViewModal }" @click="closeViewModal">
      <div class="modal-container large" @click.stop>
        <div class="modal-header">
          <h2>留言详情</h2>
          <button class="close-btn" @click="closeViewModal">×</button>
        </div>
        
        <div class="modal-body">
          <div v-if="viewingMessage" class="detail-content">
            <div class="detail-row">
              <span class="detail-label">姓名：</span>
              <span class="detail-value">{{ viewingMessage.name }}</span>
            </div>
            <div class="detail-row">
              <span class="detail-label">电话：</span>
              <span class="detail-value">{{ viewingMessage.phone }}</span>
            </div>
            <div class="detail-row" v-if="viewingMessage.email">
              <span class="detail-label">邮箱：</span>
              <span class="detail-value">{{ viewingMessage.email }}</span>
            </div>
            <div class="detail-row">
              <span class="detail-label">咨询类型：</span>
              <span class="detail-value">{{ getMessageTypeLabel(viewingMessage.type) }}</span>
            </div>
            <div class="detail-row">
              <span class="detail-label">状态：</span>
              <span class="detail-value">
                <span class="status-badge" :class="getStatusClass(viewingMessage.status)">
                  {{ getStatusLabel(viewingMessage.status) }}
                </span>
              </span>
            </div>
            <div class="detail-row">
              <span class="detail-label">提交时间：</span>
              <span class="detail-value">{{ formatDateTime(viewingMessage.createTime) }}</span>
            </div>
            <div class="detail-row full-width">
              <span class="detail-label">留言内容：</span>
              <p class="detail-text">{{ viewingMessage.message }}</p>
            </div>
            <div v-if="viewingMessage.reply" class="detail-row full-width">
              <span class="detail-label">管理员回复：</span>
              <p class="detail-text reply">{{ viewingMessage.reply }}</p>
              <div class="reply-time" v-if="viewingMessage.replyTime">
                回复时间：{{ formatDateTime(viewingMessage.replyTime) }}
              </div>
            </div>
          </div>
        </div>
        
        <div class="modal-footer">
          <button class="btn-secondary" @click="closeViewModal">关闭</button>
        </div>
      </div>
    </div>
    
    <div class="modal-overlay" :class="{ show: showReplyModal }" @click="closeReplyModal">
      <div class="modal-container" @click.stop>
        <div class="modal-header">
          <h2>回复留言</h2>
          <button class="close-btn" @click="closeReplyModal">×</button>
        </div>
        
        <div class="modal-body">
          <div v-if="replyingMessage" class="reply-preview">
            <div class="preview-info">
              <span class="preview-label">留言人：</span>
              <span>{{ replyingMessage.name }}</span>
            </div>
            <div class="preview-info">
              <span class="preview-label">联系电话：</span>
              <span>{{ replyingMessage.phone }}</span>
            </div>
            <div class="preview-content">
              <span class="preview-label">留言内容：</span>
              <p>{{ replyingMessage.message }}</p>
            </div>
          </div>
          
          <div class="form-group">
            <label class="form-label">回复内容 <span class="required">*</span></label>
            <textarea 
              v-model="replyForm.reply" 
              class="form-textarea"
              placeholder="请输入回复内容..."
              rows="6"
            ></textarea>
          </div>
          
          <div v-if="replyErrorMessage" class="error-message">{{ replyErrorMessage }}</div>
          <div v-if="replySuccessMessage" class="success-message">{{ replySuccessMessage }}</div>
        </div>
        
        <div class="modal-footer">
          <button class="btn-secondary" @click="closeReplyModal">取消</button>
          <button class="btn-primary" @click="handleReply" :disabled="isReplying">
            {{ isReplying ? '提交中...' : '提交回复' }}
          </button>
        </div>
      </div>
    </div>
    
    <div class="modal-overlay" :class="{ show: showDeleteModal }" @click="closeDeleteModal">
      <div class="modal-container small" @click.stop>
        <div class="modal-header">
          <h2>确认删除</h2>
        </div>
        <div class="modal-body">
          <p>确定要删除该留言吗？</p>
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
import { ref, reactive, onMounted, computed } from 'vue'
import request from '../../utils/request'

const messages = ref([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(10)

const searchForm = reactive({
  keyword: '',
  type: '',
  status: ''
})

const showViewModal = ref(false)
const viewingMessage = ref(null)

const showReplyModal = ref(false)
const replyingMessage = ref(null)
const isReplying = ref(false)
const replyErrorMessage = ref('')
const replySuccessMessage = ref('')
const replyForm = reactive({
  reply: ''
})

const showDeleteModal = ref(false)
const deletingMessage = ref(null)
const isDeleting = ref(false)

const totalPages = computed(() => {
  return Math.ceil(total.value / pageSize.value) || 1
})

const getMessageTypeLabel = (type) => {
  const labels = {
    'service': '服务咨询',
    'complaint': '投诉建议',
    'cooperation': '商务合作',
    'other': '其他问题'
  }
  return labels[type] || '未知'
}

const getMessageTypeClass = (type) => {
  const classes = {
    'service': 'type-service',
    'complaint': 'type-complaint',
    'cooperation': 'type-cooperation',
    'other': 'type-other'
  }
  return classes[type] || ''
}

const getStatusLabel = (status) => {
  const labels = {
    0: '未处理',
    1: '已处理',
    2: '已回复'
  }
  return labels[status] || '未知'
}

const getStatusClass = (status) => {
  const classes = {
    0: 'status-pending',
    1: 'status-processed',
    2: 'status-replied'
  }
  return classes[status] || ''
}

const formatDateTime = (dateStr) => {
  if (!dateStr) return '-'
  const date = new Date(dateStr)
  return date.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}

const loadMessages = async () => {
  try {
    let url = `/api/admin/messages?pageNum=${currentPage.value}&pageSize=${pageSize.value}`
    if (searchForm.type) {
      url += `&type=${encodeURIComponent(searchForm.type)}`
    }
    if (searchForm.status !== '') {
      url += `&status=${searchForm.status}`
    }
    if (searchForm.keyword) {
      url += `&keyword=${encodeURIComponent(searchForm.keyword)}`
    }
    
    const response = await request(url)
    const data = await response.json()
    
    if (data.code === 200) {
      messages.value = data.data.records || []
      total.value = data.data.total || 0
    }
  } catch (error) {
    console.error('加载留言列表失败:', error)
  }
}

const handleSearch = () => {
  currentPage.value = 1
  loadMessages()
}

const handleReset = () => {
  searchForm.keyword = ''
  searchForm.type = ''
  searchForm.status = ''
  currentPage.value = 1
  loadMessages()
}

const goToPage = (page) => {
  currentPage.value = page
  loadMessages()
}

const openViewModal = (message) => {
  viewingMessage.value = message
  showViewModal.value = true
}

const closeViewModal = () => {
  showViewModal.value = false
  viewingMessage.value = null
}

const openReplyModal = (message) => {
  replyingMessage.value = message
  replyForm.reply = ''
  replyErrorMessage.value = ''
  replySuccessMessage.value = ''
  showReplyModal.value = true
}

const closeReplyModal = () => {
  showReplyModal.value = false
  replyingMessage.value = null
  replyForm.reply = ''
  replyErrorMessage.value = ''
  replySuccessMessage.value = ''
}

const handleReply = async () => {
  if (!replyingMessage.value) return
  if (!replyForm.reply.trim()) {
    replyErrorMessage.value = '请输入回复内容'
    return
  }
  
  isReplying.value = true
  replyErrorMessage.value = ''
  replySuccessMessage.value = ''
  
  try {
    const response = await request(`/api/admin/messages/${replyingMessage.value.id}/reply`, {
      method: 'PUT',
      body: JSON.stringify({
        reply: replyForm.reply
      })
    })
    
    const data = await response.json()
    
    if (data.code === 200) {
      replySuccessMessage.value = '回复成功'
      setTimeout(() => {
        closeReplyModal()
        loadMessages()
      }, 1000)
    } else {
      replyErrorMessage.value = data.message || '回复失败'
    }
  } catch (error) {
    console.error('回复失败:', error)
    replyErrorMessage.value = '网络错误，请稍后重试'
  } finally {
    isReplying.value = false
  }
}

const toggleStatus = async (message) => {
  const newStatus = message.status === 0 ? 1 : 0
  
  try {
    const response = await request(`/api/admin/messages/${message.id}/status`, {
      method: 'PUT',
      body: JSON.stringify({
        status: newStatus
      })
    })
    
    const data = await response.json()
    
    if (data.code === 200) {
      message.status = newStatus
    } else {
      alert(data.message || '操作失败')
    }
  } catch (error) {
    console.error('切换状态失败:', error)
    alert('网络错误，请稍后重试')
  }
}

const confirmDelete = (message) => {
  deletingMessage.value = message
  showDeleteModal.value = true
}

const closeDeleteModal = () => {
  showDeleteModal.value = false
  deletingMessage.value = null
}

const handleDelete = async () => {
  if (!deletingMessage.value) return
  
  isDeleting.value = true
  
  try {
    const response = await request(`/api/admin/messages/${deletingMessage.value.id}`, {
      method: 'DELETE'
    })
    
    const data = await response.json()
    
    if (data.code === 200) {
      closeDeleteModal()
      loadMessages()
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

onMounted(() => {
  loadMessages()
})
</script>

<style scoped>
.message-management {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.search-bar {
  background: white;
  border-radius: 16px;
  padding: 24px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
}

.search-row {
  display: flex;
  flex-wrap: wrap;
  gap: 16px;
  align-items: flex-end;
}

.search-item {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.search-item.actions {
  flex-direction: row;
  align-items: flex-end;
}

.search-label {
  font-size: 0.9rem;
  font-weight: 500;
  color: #555;
}

.search-input,
.search-select {
  padding: 10px 14px;
  border: 2px solid #e4e8eb;
  border-radius: 10px;
  font-size: 0.95rem;
  min-width: 180px;
  outline: none;
  transition: all 0.3s ease;
}

.search-input:focus,
.search-select:focus {
  border-color: #6B8E23;
}

.btn-search,
.btn-reset {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 20px;
  border: none;
  border-radius: 10px;
  font-size: 0.95rem;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;
}

.btn-search {
  background: linear-gradient(135deg, #6B8E23, #8FBC8F);
  color: white;
}

.btn-search:hover {
  transform: translateY(-2px);
  box-shadow: 0 5px 15px rgba(107, 142, 35, 0.3);
}

.btn-reset {
  background: #f5f7fa;
  color: #7f8c8d;
  border: 1px solid #e4e8eb;
}

.btn-reset:hover {
  background: #e8ecef;
  color: #555;
}

.message-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.message-card {
  background: white;
  border-radius: 16px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  overflow: hidden;
  transition: all 0.3s ease;
  border-left: 4px solid #e4e8eb;
}

.message-card.is-new {
  border-left-color: #6B8E23;
  background: linear-gradient(135deg, rgba(107, 142, 35, 0.02) 0%, white 100%);
}

.message-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.12);
}

.message-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 24px 16px;
  border-bottom: 1px solid #f0f0f0;
}

.message-info {
  display: flex;
  flex-wrap: wrap;
  gap: 16px;
  align-items: center;
}

.message-name {
  font-size: 1.1rem;
  font-weight: 600;
  color: #2c3e50;
}

.message-phone,
.message-email {
  font-size: 0.9rem;
  color: #7f8c8d;
}

.message-meta {
  display: flex;
  gap: 10px;
}

.type-badge,
.status-badge {
  padding: 4px 12px;
  border-radius: 20px;
  font-size: 0.85rem;
  font-weight: 500;
}

.type-badge.type-service {
  background: #dbeafe;
  color: #1d4ed8;
}

.type-badge.type-complaint {
  background: #fee2e2;
  color: #dc2626;
}

.type-badge.type-cooperation {
  background: #fef3c7;
  color: #d97706;
}

.type-badge.type-other {
  background: #f3e8ff;
  color: #7c3aed;
}

.status-badge.status-pending {
  background: #fef3c7;
  color: #d97706;
}

.status-badge.status-processed {
  background: #dbeafe;
  color: #1d4ed8;
}

.status-badge.status-replied {
  background: #dcfce7;
  color: #16a34a;
}

.message-content {
  padding: 16px 24px;
  border-bottom: 1px dashed #f0f0f0;
}

.content-label {
  font-size: 0.9rem;
  font-weight: 500;
  color: #555;
  margin-bottom: 8px;
  display: block;
}

.content-text {
  color: #2c3e50;
  line-height: 1.7;
  margin: 0;
  white-space: pre-wrap;
}

.message-reply {
  padding: 16px 24px;
  background: linear-gradient(135deg, rgba(107, 142, 35, 0.03) 0%, rgba(143, 188, 143, 0.03) 100%);
  border-bottom: 1px dashed #f0f0f0;
}

.reply-label {
  font-size: 0.9rem;
  font-weight: 500;
  color: #6B8E23;
  margin-bottom: 8px;
  display: block;
}

.reply-text {
  color: #2c3e50;
  line-height: 1.7;
  margin: 0;
  white-space: pre-wrap;
}

.reply-meta {
  margin-top: 8px;
  font-size: 0.85rem;
  color: #7f8c8d;
}

.message-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 24px;
  background: #fafbfc;
}

.message-time {
  font-size: 0.9rem;
  color: #7f8c8d;
}

.message-actions {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.action-btn {
  padding: 8px 16px;
  border: none;
  border-radius: 8px;
  font-size: 0.9rem;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;
}

.action-btn.view {
  background: #e8f4fd;
  color: #3498db;
}

.action-btn.view:hover {
  background: #3498db;
  color: white;
}

.action-btn.reply {
  background: #f0fdf4;
  color: #16a34a;
}

.action-btn.reply:hover {
  background: #16a34a;
  color: white;
}

.action-btn.status {
  background: #fef3c7;
  color: #d97706;
}

.action-btn.status.process:hover {
  background: #16a34a;
  color: white;
}

.action-btn.status.unprocess:hover {
  background: #d97706;
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
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
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

.pagination {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 24px;
  background: white;
  border-radius: 16px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
}

.total-info {
  font-size: 0.95rem;
  color: #555;
}

.pagination-controls {
  display: flex;
  align-items: center;
  gap: 16px;
}

.page-btn {
  padding: 8px 16px;
  border: 1px solid #e4e8eb;
  background: white;
  border-radius: 8px;
  font-size: 0.9rem;
  cursor: pointer;
  transition: all 0.3s ease;
}

.page-btn:hover:not(:disabled) {
  border-color: #6B8E23;
  color: #6B8E23;
}

.page-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.page-info {
  font-size: 0.9rem;
  color: #7f8c8d;
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
}

.reply-preview {
  background: #f8f9fa;
  border-radius: 12px;
  padding: 16px;
  margin-bottom: 20px;
}

.preview-info {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 8px;
}

.preview-label {
  font-weight: 500;
  color: #555;
}

.preview-content {
  margin-top: 8px;
}

.preview-content p {
  margin: 8px 0 0 0;
  color: #2c3e50;
  line-height: 1.6;
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

.form-textarea {
  padding: 12px 16px;
  border: 2px solid #e4e8eb;
  border-radius: 10px;
  font-size: 1rem;
  color: #2c3e50;
  outline: none;
  transition: all 0.3s ease;
  resize: vertical;
  min-height: 120px;
}

.form-textarea:focus {
  border-color: #6B8E23;
  box-shadow: 0 0 0 4px rgba(107, 142, 35, 0.1);
}

.detail-content {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.detail-row {
  display: flex;
  gap: 8px;
}

.detail-row.full-width {
  flex-direction: column;
}

.detail-label {
  font-weight: 500;
  color: #555;
  min-width: 80px;
}

.detail-value {
  color: #2c3e50;
}

.detail-text {
  margin: 8px 0 0 0;
  color: #2c3e50;
  line-height: 1.7;
  white-space: pre-wrap;
}

.detail-text.reply {
  color: #6B8E23;
}

.reply-time {
  margin-top: 8px;
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

.error-message {
  background: #fdf2f2;
  border: 1px solid #fecaca;
  color: #dc2626;
  padding: 12px 16px;
  border-radius: 8px;
  font-size: 0.9rem;
  text-align: center;
}

.success-message {
  background: #f0fdf4;
  border: 1px solid #bbf7d0;
  color: #16a34a;
  padding: 12px 16px;
  border-radius: 8px;
  font-size: 0.9rem;
  text-align: center;
}

.warning-text {
  color: #dc2626;
  font-size: 0.9rem;
  margin: 8px 0 0 0;
}

@media (max-width: 768px) {
  .search-row {
    flex-direction: column;
    align-items: stretch;
  }
  
  .search-item {
    width: 100%;
  }
  
  .search-item.actions {
    flex-direction: row;
  }
  
  .search-input,
  .search-select {
    width: 100%;
    min-width: auto;
  }
  
  .message-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
  }
  
  .message-info {
    flex-direction: column;
    align-items: flex-start;
    gap: 8px;
  }
  
  .message-footer {
    flex-direction: column;
    align-items: flex-start;
    gap: 16px;
  }
  
  .message-actions {
    width: 100%;
    justify-content: flex-start;
  }
  
  .pagination {
    flex-direction: column;
    gap: 12px;
  }
  
  .modal-container {
    width: 95%;
    margin: 10px;
    max-height: 95vh;
  }
}
</style>
