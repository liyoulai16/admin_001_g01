<template>
  <div class="announcements">
    <section class="page-header">
      <div class="particles">
        <div v-for="i in 30" :key="i" class="particle" :style="getParticleStyle(i)"></div>
      </div>
      <div class="container">
        <h1 class="page-title">公告信息</h1>
        <p class="page-subtitle">了解平台最新动态与重要通知</p>
      </div>
    </section>

    <section class="announcements-section">
      <div class="container">
        <div v-if="pinnedAnnouncements.length > 0" class="pinned-section">
          <h3 class="section-subtitle">
            <span class="pin-icon">📌</span> 置顶公告
          </h3>
          <div class="pinned-list">
            <div 
              v-for="announcement in pinnedAnnouncements" 
              :key="announcement.id" 
              class="pinned-card"
              @click="openDetail(announcement)"
            >
              <div class="card-badge">置顶</div>
              <div class="card-content">
                <h4 class="card-title">{{ announcement.title }}</h4>
                <p class="card-excerpt">{{ announcement.summary || truncateContent(announcement.content, 100) }}</p>
                <div class="card-meta">
                  <span class="meta-item">
                    <span class="meta-icon">👁️</span>
                    <span>{{ announcement.viewCount || 0 }}</span>
                  </span>
                  <span class="meta-item">
                    <span class="meta-icon">📅</span>
                    <span>{{ formatDate(announcement.createTime) }}</span>
                  </span>
                </div>
              </div>
            </div>
          </div>
        </div>

        <div class="list-section">
          <h3 class="section-subtitle">全部公告</h3>
          
          <div v-if="announcements.length > 0" class="announcements-list">
            <div 
              v-for="announcement in announcements" 
              :key="announcement.id" 
              class="announcement-card"
              @click="openDetail(announcement)"
            >
              <div class="card-header">
                <h4 class="card-title">{{ announcement.title }}</h4>
                <div class="card-tags">
                  <span v-if="announcement.isPinned === 1" class="tag pin-tag">置顶</span>
                </div>
              </div>
              <p class="card-excerpt">{{ announcement.summary || truncateContent(announcement.content, 150) }}</p>
              <div class="card-footer">
                <div class="card-meta">
                  <span class="meta-item">
                    <span class="meta-icon">👁️</span>
                    <span>{{ announcement.viewCount || 0 }} 次浏览</span>
                  </span>
                  <span class="meta-item">
                    <span class="meta-icon">📅</span>
                    <span>{{ formatDate(announcement.createTime) }}</span>
                  </span>
                </div>
                <span class="view-more">查看详情 →</span>
              </div>
            </div>
          </div>

          <div v-else class="empty-state">
            <div class="empty-icon">📢</div>
            <h3>暂无公告</h3>
            <p>敬请期待平台最新动态</p>
          </div>

          <div v-if="totalPages > 1" class="pagination-section">
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
        </div>
      </div>
    </section>

    <div class="modal-overlay" :class="{ show: showDetail }" @click="closeDetail">
      <div class="modal-container large" @click.stop>
        <div class="modal-header">
          <div class="header-left">
            <span v-if="currentAnnouncement?.isPinned === 1" class="modal-badge pin">置顶</span>
            <h3 class="modal-title">{{ currentAnnouncement?.title }}</h3>
          </div>
          <button class="modal-close-btn" @click="closeDetail">×</button>
        </div>
        <div class="modal-body">
          <div v-if="currentAnnouncement" class="announcement-detail">
            <div class="detail-meta">
              <span class="meta-item">
                <span class="meta-icon">👁️</span>
                <span>{{ currentAnnouncement.viewCount || 0 }} 次浏览</span>
              </span>
              <span class="meta-item">
                <span class="meta-icon">📅</span>
                <span>发布时间：{{ formatDateTime(currentAnnouncement.createTime) }}</span>
              </span>
            </div>
            <div v-if="currentAnnouncement.summary" class="detail-summary">
              <span class="summary-label">摘要：</span>
              <span>{{ currentAnnouncement.summary }}</span>
            </div>
            <div class="detail-content">
              <div v-html="formatContent(currentAnnouncement.content)"></div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import request from '../utils/request'

const announcements = ref([])
const pinnedAnnouncements = ref([])
const currentPage = ref(1)
const pageSize = ref(10)
const totalPages = ref(1)
const total = ref(0)
const showDetail = ref(false)
const currentAnnouncement = ref(null)
const jumpPageInput = ref('')
const showToast = ref(false)
const toastMessage = ref('')
const toastIcon = ref('⚠️')
let toastTimer = null

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

const fetchAnnouncements = async () => {
  try {
    const response = await request(`/api/announcements/page?pageNum=${currentPage.value}&pageSize=${pageSize.value}`, {
      method: 'GET'
    })
    
    if (response.ok) {
      const data = await response.json()
      if (data.code === 200 && data.data) {
        const records = data.data.records || []
        announcements.value = records
        totalPages.value = data.data.pages || 1
        total.value = data.data.total || 0
      }
    }
  } catch (error) {
    console.error('获取公告列表失败:', error)
  }
}

const fetchPinnedAnnouncements = async () => {
  try {
    const response = await request('/api/announcements', {
      method: 'GET'
    })
    
    if (response.ok) {
      const data = await response.json()
      if (data.code === 200 && data.data) {
        pinnedAnnouncements.value = data.data.filter(a => a.isPinned === 1)
      }
    }
  } catch (error) {
    console.error('获取置顶公告失败:', error)
  }
}

const fetchAnnouncementDetail = async (id) => {
  try {
    const response = await request(`/api/announcements/${id}`, {
      method: 'GET'
    })
    
    if (response.ok) {
      const data = await response.json()
      if (data.code === 200 && data.data) {
        currentAnnouncement.value = data.data
      }
    }
  } catch (error) {
    console.error('获取公告详情失败:', error)
  }
}

const openDetail = async (announcement) => {
  await fetchAnnouncementDetail(announcement.id)
  showDetail.value = true
}

const closeDetail = () => {
  showDetail.value = false
  currentAnnouncement.value = null
}

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

const goToPage = (page) => {
  const totalPagesVal = totalPages.value || 1
  if (page < 1) {
    showToastMessage('已经是第一页了', '⚠️')
    return
  }
  if (page > totalPagesVal) {
    showToastMessage(`只有 ${totalPagesVal} 页`, '⚠️')
    return
  }
  if (page === currentPage.value) {
    showToastMessage('已经在当前页', 'ℹ️')
    return
  }
  currentPage.value = page
  fetchAnnouncements()
  window.scrollTo({ top: 0, behavior: 'smooth' })
}

const goToFirstPage = () => {
  if (currentPage.value === 1) {
    showToastMessage('已经是第一页了', '⚠️')
    return
  }
  goToPage(1)
}

const goToLastPage = () => {
  const totalPagesVal = totalPages.value || 1
  if (currentPage.value === totalPagesVal) {
    showToastMessage('已经是最后一页了', '⚠️')
    return
  }
  goToPage(totalPagesVal)
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

const handlePageSizeChange = () => {
  currentPage.value = 1
  fetchAnnouncements()
}

const handleJumpPage = () => {
  const inputValue = jumpPageInput.value
  if (inputValue === '' || inputValue === null || inputValue === undefined) {
    showToastMessage('请输入页码', '⚠️')
    return
  }
  const pageNum = Number(inputValue)
  if (isNaN(pageNum) || !Number.isInteger(pageNum)) {
    showToastMessage('请输入有效的数字', '⚠️')
    return
  }
  if (pageNum < 1) {
    showToastMessage('页码不能小于1', '⚠️')
    return
  }
  const totalPagesVal = totalPages.value || 1
  if (pageNum > totalPagesVal) {
    showToastMessage(`页码不能大于 ${totalPagesVal}`, '⚠️')
    return
  }
  jumpPageInput.value = ''
  goToPage(pageNum)
}

onMounted(() => {
  fetchPinnedAnnouncements()
  fetchAnnouncements()
})
</script>

<style scoped>
.announcements {
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

.announcements-section {
  padding: 50px 0;
  background: linear-gradient(180deg, #f8f9fa 0%, #ffffff 100%);
}

.section-subtitle {
  font-size: 1.3rem;
  color: #2c3e50;
  margin-bottom: 25px;
  font-weight: 600;
  display: flex;
  align-items: center;
  gap: 10px;
}

.pin-icon {
  font-size: 1.2rem;
}

.pinned-section {
  margin-bottom: 50px;
}

.pinned-list {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(350px, 1fr));
  gap: 20px;
}

.pinned-card {
  background: linear-gradient(135deg, #fff9e6 0%, #fffef5 100%);
  border-radius: 16px;
  padding: 25px;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  position: relative;
  border-left: 4px solid #f59e0b;
}

.pinned-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.15);
}

.card-badge {
  position: absolute;
  top: 15px;
  right: 15px;
  background: linear-gradient(135deg, #f59e0b, #d97706);
  color: white;
  padding: 4px 12px;
  border-radius: 20px;
  font-size: 0.75rem;
  font-weight: 600;
}

.card-content {
  padding-right: 60px;
}

.card-title {
  font-size: 1.1rem;
  color: #2c3e50;
  margin: 0 0 10px 0;
  font-weight: 600;
  line-height: 1.4;
}

.card-excerpt {
  font-size: 0.9rem;
  color: #7f8c8d;
  line-height: 1.6;
  margin: 0 0 15px 0;
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

.list-section {
  background: white;
  border-radius: 20px;
  padding: 30px;
  box-shadow: 0 5px 20px rgba(0, 0, 0, 0.06);
}

.announcements-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
  margin-bottom: 30px;
}

.announcement-card {
  background: #fafbfc;
  border-radius: 12px;
  padding: 25px;
  cursor: pointer;
  transition: all 0.3s ease;
  border: 2px solid transparent;
}

.announcement-card:hover {
  background: white;
  border-color: #6B8E23;
  box-shadow: 0 5px 20px rgba(107, 142, 35, 0.15);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 10px;
}

.card-tags {
  display: flex;
  gap: 8px;
}

.tag {
  padding: 4px 10px;
  border-radius: 20px;
  font-size: 0.75rem;
  font-weight: 600;
}

.pin-tag {
  background: linear-gradient(135deg, #f59e0b, #d97706);
  color: white;
}

.card-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 15px;
  border-top: 1px solid #e4e8eb;
}

.view-more {
  font-size: 0.9rem;
  color: #6B8E23;
  font-weight: 500;
  transition: color 0.3s ease;
}

.announcement-card:hover .view-more {
  color: #556B2F;
}

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 60px 20px;
  text-align: center;
}

.empty-icon {
  font-size: 4rem;
  margin-bottom: 20px;
}

.empty-state h3 {
  font-size: 1.3rem;
  color: #2c3e50;
  margin: 0 0 10px 0;
}

.empty-state p {
  color: #7f8c8d;
  margin: 0;
}

.pagination-section {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding-top: 20px;
  position: relative;
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

.first-btn, .last-btn, .prev-btn, .next-btn {
  font-weight: 500;
  padding: 0 18px;
}

.first-btn {
  background: linear-gradient(135deg, #f8f9fa, #ffffff);
}

.last-btn {
  background: linear-gradient(135deg, #f8f9fa, #ffffff);
}

.page-size-select {
  display: flex;
  align-items: center;
  gap: 6px;
  margin-left: 8px;
  padding: 8px 12px;
  background: #f8f9fa;
  border-radius: 8px;
}

.size-label {
  font-size: 0.9rem;
  color: #555;
  white-space: nowrap;
}

.size-select {
  padding: 6px 10px;
  border: 2px solid #e4e8eb;
  border-radius: 6px;
  font-size: 0.9rem;
  background: white;
  cursor: pointer;
  transition: border-color 0.3s ease;
}

.size-select:focus {
  outline: none;
  border-color: #6B8E23;
}

.page-jump {
  display: flex;
  align-items: center;
  gap: 6px;
  margin-left: 8px;
  padding: 8px 12px;
  background: #f8f9fa;
  border-radius: 8px;
}

.jump-label {
  font-size: 0.9rem;
  color: #555;
  white-space: nowrap;
}

.jump-input {
  width: 80px;
  height: 36px;
  padding: 0 8px;
  border: 2px solid #e4e8eb;
  border-radius: 6px;
  font-size: 0.95rem;
  text-align: center;
  transition: border-color 0.3s ease;
}

.jump-input:focus {
  outline: none;
  border-color: #6B8E23;
}

.jump-input::placeholder {
  color: #aaa;
}

.jump-btn {
  height: 36px;
  padding: 0 14px;
  background: linear-gradient(135deg, #6B8E23, #8FBC8F);
  color: white;
  border: none;
  border-radius: 6px;
  font-size: 0.9rem;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;
}

.jump-btn:hover {
  background: linear-gradient(135deg, #556B2F, #5D7C4A);
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

.pagination-toast {
  position: absolute;
  top: -40px;
  left: 50%;
  transform: translateX(-50%) translateY(-10px);
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 20px;
  background: white;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  border-left: 4px solid #f39c12;
  opacity: 0;
  visibility: hidden;
  transition: all 0.3s ease;
  z-index: 100;
}

.pagination-toast.show {
  opacity: 1;
  visibility: visible;
  transform: translateX(-50%) translateY(0);
}

.toast-icon {
  font-size: 1.1rem;
}

.toast-message {
  font-size: 0.9rem;
  color: #333;
  font-weight: 500;
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
  z-index: 2000;
  opacity: 0;
  visibility: hidden;
  transition: all 0.3s ease;
  padding: 20px;
}

.modal-overlay.show {
  opacity: 1;
  visibility: visible;
}

.modal-container {
  background: white;
  border-radius: 16px;
  width: 90%;
  max-width: 800px;
  max-height: 90vh;
  overflow-y: auto;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
  transform: scale(0.9) translateY(20px);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.modal-container.large {
  max-width: 800px;
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
  position: sticky;
  top: 0;
  background: white;
  z-index: 10;
  border-radius: 16px 16px 0 0;
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

.modal-body {
  padding: 24px;
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
  .page-header {
    padding: 40px 0;
  }

  .page-title {
    font-size: 2rem;
  }

  .page-subtitle {
    font-size: 1rem;
  }

  .announcements-section {
    padding: 30px 0;
  }

  .pinned-list {
    grid-template-columns: 1fr;
  }

  .list-section {
    padding: 20px;
    border-radius: 15px;
  }

  .announcement-card {
    padding: 20px;
  }

  .card-header {
    flex-direction: column;
    gap: 10px;
  }

  .card-footer {
    flex-direction: column;
    gap: 15px;
    align-items: flex-start;
  }

  .view-more {
    align-self: flex-end;
  }

  .modal-header {
    padding: 20px;
  }

  .modal-title {
    font-size: 1.1rem;
  }

  .modal-body {
    padding: 20px;
  }

  .detail-meta {
    flex-direction: column;
    gap: 10px;
  }

  .pagination {
    flex-wrap: wrap;
    justify-content: center;
  }
}
</style>
