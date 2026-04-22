<template>
  <div class="order-management">
    <div class="load-error" v-if="loadError">
      <div class="error-icon">⚠️</div>
      <h3>加载失败</h3>
      <p>{{ loadError }}</p>
      <button class="retry-btn" @click="loadOrders">🔄 重试</button>
    </div>
    
    <template v-else>
      <div class="search-bar">
        <div class="search-left">
          <select v-model="searchForm.categoryId" class="search-select" @change="loadOrders">
            <option :value="null">全部分类</option>
            <option v-for="cat in categories" :key="cat.id" :value="cat.id">
              {{ cat.name }}
            </option>
          </select>
          <select v-model="searchForm.status" class="search-select" @change="loadOrders">
            <option value="all">全部状态</option>
            <option value="pending">待付款</option>
            <option value="confirmed">待服务</option>
            <option value="inProgress">进行中</option>
            <option value="completed">已完成</option>
            <option value="cancelled">已取消</option>
            <option value="refunding">退款中</option>
          </select>
          <input 
            type="text" 
            v-model="searchForm.keyword" 
            class="search-input"
            placeholder="搜索订单号、服务名称、联系人..."
            @keyup.enter="loadOrders"
          />
          <button class="search-btn" @click="loadOrders">🔍 搜索</button>
        </div>
        <button class="refresh-btn" @click="loadOrders">
          <span>🔄</span> 刷新
        </button>
      </div>
      
      <div class="stats-bar">
        <div class="stat-item" v-for="(count, key) in orderStats" :key="key">
          <span class="stat-label">{{ getStatusLabel(key) }}</span>
          <span class="stat-value">{{ count }}</span>
        </div>
      </div>
      
      <div class="loading-state" v-if="isLoading">
        <div class="loading-spinner"></div>
        <p>加载中...</p>
      </div>
      
      <div class="order-list" v-else-if="orders.length > 0">
        <div v-for="order in orders" :key="order.id" class="order-card">
          <div class="order-header">
            <div class="order-basic">
              <span class="order-no">订单号: {{ order.orderNo }}</span>
              <span class="order-time">{{ formatDateTime(order.createTime) }}</span>
            </div>
            <span class="status-badge" :class="getStatusClass(order.status)">
              {{ getStatusText(order.status) }}
            </span>
          </div>
          
          <div class="order-content">
            <div class="order-icon">
              <span class="icon-text">{{ order.serviceIcon || '🛠️' }}</span>
            </div>
            <div class="order-info">
              <h3 class="service-name">{{ order.serviceName }}</h3>
              <p class="service-category" v-if="order.categoryName">{{ order.categoryName }}</p>
              <div class="order-meta">
                <span class="meta-item">💰 {{ formatAmount(order.totalAmount) }}</span>
                <span class="meta-item" v-if="order.quantity">📦 {{ order.quantity }} 份</span>
                <span class="meta-item" v-if="order.contactName">👤 {{ order.contactName }}</span>
                <span class="meta-item" v-if="order.contactPhone">📞 {{ order.contactPhone }}</span>
              </div>
              <div class="order-appointment" v-if="order.appointmentDate || order.appointmentTime">
                <span class="appointment-label">预约:</span>
                <span class="appointment-value">
                  {{ order.appointmentDate ? formatDate(order.appointmentDate) : '' }}
                  {{ order.appointmentStartTime ? order.appointmentStartTime : '' }}
                  {{ order.appointmentEndTime ? '-' + order.appointmentEndTime : '' }}
                </span>
              </div>
              <div class="order-refund" v-if="order.refundStatus === 'refunding'">
                <span class="refund-label">退款申请:</span>
                <span class="refund-reason">{{ order.refundReason }}</span>
                <span class="refund-amount">退款金额: {{ formatAmount(order.refundAmount) }}</span>
              </div>
            </div>
          </div>
          
          <div class="order-actions">
            <button class="action-btn view" @click="openDetailModal(order)">📋 详情</button>
            <template v-if="order.status === 'refunding'">
              <button class="action-btn approve" @click="approveRefund(order)">✅ 同意退款</button>
              <button class="action-btn reject" @click="openRejectModal(order)">❌ 拒绝退款</button>
            </template>
            <template v-else-if="order.status === 'confirmed' || order.status === 'inProgress'">
              <button class="action-btn complete" @click="openCompleteModal(order)">✅ 标记完成</button>
              <button class="action-btn cancel" @click="openCancelModal(order)">📛 取消订单</button>
            </template>
            <button class="action-btn delete" @click="confirmDelete(order)">🗑️ 删除</button>
          </div>
        </div>
      </div>
      
      <div class="pagination-section" v-if="orders.length > 0">
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
            @click="prevPage"
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
            @click="nextPage"
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
        <div class="empty-icon">📋</div>
        <h3>暂无订单</h3>
        <p>当前条件下没有订单记录</p>
      </div>
    </template>
    
    <div class="modal-overlay" :class="{ show: showDetailModal }" @click="closeDetailModal">
      <div class="modal-container large detail-modal" @click.stop>
        <div class="modal-header">
          <h2>订单详情</h2>
          <button class="close-btn" @click="closeDetailModal">×</button>
        </div>
        
        <div class="modal-body">
          <div class="detail-section" v-if="currentOrder">
            <div class="detail-row">
              <span class="detail-label">订单号:</span>
              <span class="detail-value">{{ currentOrder.orderNo }}</span>
            </div>
            <div class="detail-row">
              <span class="detail-label">服务名称:</span>
              <span class="detail-value">{{ currentOrder.serviceName }}</span>
            </div>
            <div class="detail-row">
              <span class="detail-label">服务分类:</span>
              <span class="detail-value">{{ currentOrder.categoryName || '-' }}</span>
            </div>
            <div class="detail-row">
              <span class="detail-label">订单状态:</span>
              <span class="detail-value">
                <span class="status-badge" :class="getStatusClass(currentOrder.status)">
                  {{ getStatusText(currentOrder.status) }}
                </span>
              </span>
            </div>
            <div class="detail-row">
              <span class="detail-label">订单金额:</span>
              <span class="detail-value highlight">{{ formatAmount(currentOrder.totalAmount) }}</span>
            </div>
            <div class="detail-row">
              <span class="detail-label">支付金额:</span>
              <span class="detail-value">{{ formatAmount(currentOrder.payAmount) }}</span>
            </div>
            <div class="detail-row">
              <span class="detail-label">支付方式:</span>
              <span class="detail-value">{{ getPayMethodText(currentOrder.payMethod) }}</span>
            </div>
            <div class="detail-row" v-if="currentOrder.quantity">
              <span class="detail-label">数量:</span>
              <span class="detail-value">{{ currentOrder.quantity }} 份</span>
            </div>
            <div class="detail-row">
              <span class="detail-label">联系人:</span>
              <span class="detail-value">{{ currentOrder.contactName || '-' }}</span>
            </div>
            <div class="detail-row">
              <span class="detail-label">联系电话:</span>
              <span class="detail-value">{{ currentOrder.contactPhone || '-' }}</span>
            </div>
            <div class="detail-row">
              <span class="detail-label">服务地址:</span>
              <span class="detail-value">{{ currentOrder.serviceAddress || '-' }}</span>
            </div>
            <div class="detail-row" v-if="currentOrder.appointmentDate || currentOrder.appointmentTime">
              <span class="detail-label">预约时间:</span>
              <span class="detail-value">
                {{ currentOrder.appointmentDate ? formatDate(currentOrder.appointmentDate) : '' }}
                {{ currentOrder.appointmentStartTime ? currentOrder.appointmentStartTime : '' }}
                {{ currentOrder.appointmentEndTime ? '-' + currentOrder.appointmentEndTime : '' }}
              </span>
            </div>
            <div class="detail-row" v-if="currentOrder.remark">
              <span class="detail-label">备注:</span>
              <span class="detail-value">{{ currentOrder.remark }}</span>
            </div>
            <div class="detail-row">
              <span class="detail-label">创建时间:</span>
              <span class="detail-value">{{ formatDateTime(currentOrder.createTime) }}</span>
            </div>
            <div class="detail-row" v-if="currentOrder.payTime">
              <span class="detail-label">支付时间:</span>
              <span class="detail-value">{{ formatDateTime(currentOrder.payTime) }}</span>
            </div>
            <div class="detail-row" v-if="currentOrder.refundStatus">
              <span class="detail-label">退款状态:</span>
              <span class="detail-value">{{ getRefundStatusText(currentOrder.refundStatus) }}</span>
            </div>
            <div class="detail-row" v-if="currentOrder.refundReason">
              <span class="detail-label">退款原因:</span>
              <span class="detail-value">{{ currentOrder.refundReason }}</span>
            </div>
            <div class="detail-row" v-if="currentOrder.cancelReason">
              <span class="detail-label">取消原因:</span>
              <span class="detail-value">{{ currentOrder.cancelReason }}</span>
            </div>
          </div>
        </div>
        
        <div class="modal-footer">
          <button class="btn-secondary" @click="closeDetailModal">关闭</button>
        </div>
      </div>
    </div>
    
    <div class="modal-overlay" :class="{ show: showDeleteModal }" @click="closeDeleteModal">
      <div class="modal-container small" @click.stop>
        <div class="modal-header">
          <h2>确认删除</h2>
        </div>
        <div class="modal-body">
          <p>确定要删除订单 "<strong>{{ deletingOrder?.orderNo }}</strong>" 吗？</p>
          <p class="warning-text">此操作不可撤销</p>
        </div>
        <div class="modal-footer">
          <button class="btn-secondary" @click="closeDeleteModal">取消</button>
          <button class="btn-danger" @click="handleDelete" :disabled="isSubmitting">
            {{ isSubmitting ? '删除中...' : '确认删除' }}
          </button>
        </div>
      </div>
    </div>
    
    <div class="modal-overlay" :class="{ show: showRejectModal }" @click="closeRejectModal">
      <div class="modal-container" @click.stop>
        <div class="modal-header">
          <h2>拒绝退款</h2>
          <button class="close-btn" @click="closeRejectModal">×</button>
        </div>
        <div class="modal-body">
          <div class="form-group">
            <label class="form-label">拒绝原因</label>
            <textarea 
              v-model="rejectReason" 
              class="form-textarea"
              placeholder="请输入拒绝退款的原因..."
              rows="4"
            ></textarea>
          </div>
        </div>
        <div class="modal-footer">
          <button class="btn-secondary" @click="closeRejectModal">取消</button>
          <button class="btn-danger" @click="handleReject" :disabled="isSubmitting">
            {{ isSubmitting ? '处理中...' : '确认拒绝' }}
          </button>
        </div>
      </div>
    </div>
    
    <div class="modal-overlay" :class="{ show: showCancelModal }" @click="closeCancelModal">
      <div class="modal-container" @click.stop>
        <div class="modal-header">
          <h2>取消订单</h2>
          <button class="close-btn" @click="closeCancelModal">×</button>
        </div>
        <div class="modal-body">
          <div class="form-group">
            <label class="form-label">取消原因</label>
            <textarea 
              v-model="cancelReason" 
              class="form-textarea"
              placeholder="请输入取消订单的原因..."
              rows="4"
            ></textarea>
          </div>
        </div>
        <div class="modal-footer">
          <button class="btn-secondary" @click="closeCancelModal">取消</button>
          <button class="btn-danger" @click="handleCancel" :disabled="isSubmitting">
            {{ isSubmitting ? '处理中...' : '确认取消' }}
          </button>
        </div>
      </div>
    </div>
    
    <div class="modal-overlay" :class="{ show: showCompleteModal }" @click="closeCompleteModal">
      <div class="modal-container small" @click.stop>
        <div class="modal-header">
          <h2>标记完成</h2>
        </div>
        <div class="modal-body">
          <p>确定要将订单 "<strong>{{ completingOrder?.orderNo }}</strong>" 标记为已完成吗？</p>
        </div>
        <div class="modal-footer">
          <button class="btn-secondary" @click="closeCompleteModal">取消</button>
          <button class="btn-primary" @click="handleComplete" :disabled="isSubmitting">
            {{ isSubmitting ? '处理中...' : '确认完成' }}
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import request from '../../utils/request'

const orders = ref([])
const categories = ref([])
const orderStats = ref({})
const isLoading = ref(false)
const loadError = ref('')
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const totalPages = ref(0)
const jumpPageInput = ref('')
const showToast = ref(false)
const toastMessage = ref('')
const toastIcon = ref('⚠️')
let toastTimer = null

const showDetailModal = ref(false)
const showDeleteModal = ref(false)
const showRejectModal = ref(false)
const showCancelModal = ref(false)
const showCompleteModal = ref(false)

const currentOrder = ref(null)
const deletingOrder = ref(null)
const completingOrder = ref(null)
const rejectReason = ref('')
const cancelReason = ref('')
const isSubmitting = ref(false)

const searchForm = reactive({
  keyword: '',
  categoryId: null,
  status: 'all'
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

const loadCategories = async () => {
  try {
    const response = await request('/api/admin/service-categories/all')
    const data = await response.json()
    
    if (data.code === 200) {
      categories.value = data.data || []
    }
  } catch (error) {
    console.error('加载分类失败:', error)
  }
}

const loadOrderStats = async () => {
  try {
    const response = await request('/api/admin/orders/stats')
    const data = await response.json()
    
    if (data.code === 200) {
      orderStats.value = data.data || {}
    }
  } catch (error) {
    console.error('加载订单统计失败:', error)
  }
}

const loadOrders = async () => {
  isLoading.value = true
  loadError.value = ''
  
  try {
    let url = `/api/admin/orders?pageNum=${currentPage.value}&pageSize=${pageSize.value}`
    if (searchForm.keyword) {
      url += `&keyword=${encodeURIComponent(searchForm.keyword)}`
    }
    if (searchForm.categoryId) {
      url += `&categoryId=${searchForm.categoryId}`
    }
    if (searchForm.status && searchForm.status !== 'all') {
      url += `&status=${searchForm.status}`
    }
    
    const response = await request(url)
    const data = await response.json()
    
    if (data.code === 200) {
      orders.value = data.data.records || []
      total.value = data.data.total || 0
      totalPages.value = data.data.pages || 0
    } else if (data.code === 401) {
      loadError.value = '登录已过期，请重新登录'
    } else {
      loadError.value = data.message || '加载失败'
    }
  } catch (error) {
    console.error('加载订单失败:', error)
    loadError.value = '网络错误，请检查后端服务是否启动'
  } finally {
    isLoading.value = false
  }
}

const prevPage = () => {
  if (currentPage.value > 1) {
    currentPage.value--
    loadOrders()
  }
}

const nextPage = () => {
  if (currentPage.value < totalPages.value) {
    currentPage.value++
    loadOrders()
  }
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
  loadOrders()
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

const handlePageSizeChange = () => {
  currentPage.value = 1
  loadOrders()
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

const getStatusText = (status) => {
  const statusMap = {
    'pending': '待付款',
    'confirmed': '待服务',
    'inProgress': '进行中',
    'completed': '已完成',
    'cancelled': '已取消',
    'refunding': '退款中',
    'refunded': '已退款',
    'rejected': '已拒绝'
  }
  return statusMap[status] || status
}

const getStatusLabel = (key) => {
  const labelMap = {
    'all': '全部',
    'pending': '待付款',
    'confirmed': '待服务',
    'inProgress': '进行中',
    'completed': '已完成',
    'cancelled': '已取消',
    'refunding': '退款中',
    'refunded': '已退款',
    'rejected': '已拒绝'
  }
  return labelMap[key] || key
}

const getStatusClass = (status) => {
  const classMap = {
    'pending': 'pending',
    'confirmed': 'confirmed',
    'inProgress': 'inProgress',
    'completed': 'completed',
    'cancelled': 'cancelled',
    'refunding': 'refunding',
    'refunded': 'refunded',
    'rejected': 'rejected'
  }
  return classMap[status] || ''
}

const getRefundStatusText = (status) => {
  const statusMap = {
    'refunding': '退款中',
    'refunded': '已退款',
    'rejected': '已拒绝'
  }
  return statusMap[status] || status
}

const getPayMethodText = (method) => {
  if (method === 0) return '余额支付'
  if (method === 1) return '微信支付'
  if (method === 2) return '支付宝'
  return '其他'
}

const formatAmount = (amount) => {
  if (amount == null) return '¥0.00'
  return `¥${Number(amount).toFixed(2)}`
}

const formatDate = (dateStr) => {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  return date.toLocaleDateString('zh-CN')
}

const formatDateTime = (dateTimeStr) => {
  if (!dateTimeStr) return '-'
  const date = new Date(dateTimeStr)
  return date.toLocaleString('zh-CN')
}

const openDetailModal = (order) => {
  currentOrder.value = order
  showDetailModal.value = true
}

const closeDetailModal = () => {
  showDetailModal.value = false
  currentOrder.value = null
}

const confirmDelete = (order) => {
  deletingOrder.value = order
  showDeleteModal.value = true
}

const closeDeleteModal = () => {
  showDeleteModal.value = false
  deletingOrder.value = null
}

const handleDelete = async () => {
  if (!deletingOrder.value) return
  
  isSubmitting.value = true
  
  try {
    const response = await request(`/api/admin/orders/${deletingOrder.value.id}`, {
      method: 'DELETE'
    })
    
    const data = await response.json()
    
    if (data.code === 200) {
      closeDeleteModal()
      loadOrders()
      loadOrderStats()
    } else {
      alert(data.message || '删除失败')
    }
  } catch (error) {
    console.error('删除失败:', error)
    alert('网络错误，请稍后重试')
  } finally {
    isSubmitting.value = false
  }
}

const openRejectModal = (order) => {
  currentOrder.value = order
  rejectReason.value = ''
  showRejectModal.value = true
}

const closeRejectModal = () => {
  showRejectModal.value = false
  currentOrder.value = null
  rejectReason.value = ''
}

const handleReject = async () => {
  if (!currentOrder.value) return
  
  isSubmitting.value = true
  
  try {
    let url = `/api/admin/orders/${currentOrder.value.id}/refund/reject`
    if (rejectReason.value) {
      url += `?rejectReason=${encodeURIComponent(rejectReason.value)}`
    }
    
    const response = await request(url, {
      method: 'POST'
    })
    
    const data = await response.json()
    
    if (data.code === 200) {
      closeRejectModal()
      loadOrders()
      loadOrderStats()
    } else {
      alert(data.message || '操作失败')
    }
  } catch (error) {
    console.error('拒绝退款失败:', error)
    alert('网络错误，请稍后重试')
  } finally {
    isSubmitting.value = false
  }
}

const approveRefund = async (order) => {
  if (!confirm(`确定要同意订单 "${order.orderNo}" 的退款申请吗？`)) {
    return
  }
  
  try {
    const response = await request(`/api/admin/orders/${order.id}/refund/approve`, {
      method: 'POST'
    })
    
    const data = await response.json()
    
    if (data.code === 200) {
      loadOrders()
      loadOrderStats()
    } else {
      alert(data.message || '操作失败')
    }
  } catch (error) {
    console.error('同意退款失败:', error)
    alert('网络错误，请稍后重试')
  }
}

const openCancelModal = (order) => {
  currentOrder.value = order
  cancelReason.value = ''
  showCancelModal.value = true
}

const closeCancelModal = () => {
  showCancelModal.value = false
  currentOrder.value = null
  cancelReason.value = ''
}

const handleCancel = async () => {
  if (!currentOrder.value) return
  
  isSubmitting.value = true
  
  try {
    let url = `/api/admin/orders/${currentOrder.value.id}/cancel`
    if (cancelReason.value) {
      url += `?cancelReason=${encodeURIComponent(cancelReason.value)}`
    }
    
    const response = await request(url, {
      method: 'PUT'
    })
    
    const data = await response.json()
    
    if (data.code === 200) {
      closeCancelModal()
      loadOrders()
      loadOrderStats()
    } else {
      alert(data.message || '操作失败')
    }
  } catch (error) {
    console.error('取消订单失败:', error)
    alert('网络错误，请稍后重试')
  } finally {
    isSubmitting.value = false
  }
}

const openCompleteModal = (order) => {
  completingOrder.value = order
  showCompleteModal.value = true
}

const closeCompleteModal = () => {
  showCompleteModal.value = false
  completingOrder.value = null
}

const handleComplete = async () => {
  if (!completingOrder.value) return
  
  isSubmitting.value = true
  
  try {
    const response = await request(`/api/admin/orders/${completingOrder.value.id}/complete`, {
      method: 'PUT'
    })
    
    const data = await response.json()
    
    if (data.code === 200) {
      closeCompleteModal()
      loadOrders()
      loadOrderStats()
    } else {
      alert(data.message || '操作失败')
    }
  } catch (error) {
    console.error('标记完成失败:', error)
    alert('网络错误，请稍后重试')
  } finally {
    isSubmitting.value = false
  }
}

onMounted(() => {
  loadCategories()
  loadOrderStats()
  loadOrders()
})
</script>

<style scoped>
.order-management {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.search-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 20px;
  flex-wrap: wrap;
}

.search-left {
  display: flex;
  gap: 12px;
  align-items: center;
  flex-wrap: wrap;
}

.search-select,
.search-input {
  padding: 10px 16px;
  border: 2px solid #e4e8eb;
  border-radius: 10px;
  font-size: 0.95rem;
  color: #2c3e50;
  outline: none;
  transition: all 0.3s ease;
  background: white;
}

.search-select {
  min-width: 130px;
  cursor: pointer;
}

.search-input {
  min-width: 200px;
}

.search-select:focus,
.search-input:focus {
  border-color: #6B8E23;
  box-shadow: 0 0 0 4px rgba(107, 142, 35, 0.1);
}

.search-btn {
  padding: 10px 20px;
  background: linear-gradient(135deg, #6B8E23, #8FBC8F);
  color: white;
  border: none;
  border-radius: 10px;
  font-size: 0.95rem;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;
}

.search-btn:hover {
  transform: translateY(-1px);
  box-shadow: 0 5px 15px rgba(107, 142, 35, 0.3);
}

.refresh-btn {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 20px;
  background: white;
  color: #6B8E23;
  border: 2px solid #6B8E23;
  border-radius: 10px;
  font-size: 0.95rem;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;
}

.refresh-btn:hover {
  background: #6B8E23;
  color: white;
}

.stats-bar {
  display: flex;
  gap: 16px;
  flex-wrap: wrap;
}

.stat-item {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 20px;
  background: white;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.stat-label {
  font-size: 0.9rem;
  color: #7f8c8d;
}

.stat-value {
  font-size: 1.3rem;
  font-weight: 700;
  color: #6B8E23;
}

.order-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.order-card {
  background: white;
  border-radius: 16px;
  padding: 20px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  transition: all 0.3s ease;
}

.order-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.12);
}

.order-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-bottom: 16px;
  border-bottom: 1px solid #e4e8eb;
  margin-bottom: 16px;
}

.order-basic {
  display: flex;
  gap: 16px;
  align-items: center;
}

.order-no {
  font-family: 'Consolas', monospace;
  font-size: 0.95rem;
  color: #2c3e50;
  font-weight: 600;
}

.order-time {
  font-size: 0.9rem;
  color: #95a5a6;
}

.status-badge {
  padding: 6px 16px;
  border-radius: 20px;
  font-size: 0.85rem;
  font-weight: 600;
}

.status-badge.pending {
  background: #fef3c7;
  color: #d97706;
}

.status-badge.confirmed {
  background: #dbeafe;
  color: #2563eb;
}

.status-badge.inProgress {
  background: #e0e7ff;
  color: #4f46e5;
}

.status-badge.completed {
  background: #dcfce7;
  color: #16a34a;
}

.status-badge.cancelled {
  background: #f3f4f6;
  color: #6b7280;
}

.status-badge.refunding {
  background: #fecaca;
  color: #dc2626;
}

.status-badge.refunded {
  background: #dcfce7;
  color: #16a34a;
}

.status-badge.rejected {
  background: #f3f4f6;
  color: #6b7280;
}

.order-content {
  display: flex;
  gap: 16px;
  margin-bottom: 16px;
}

.order-icon {
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

.order-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 8px;
  min-width: 0;
}

.service-name {
  font-size: 1.15rem;
  color: #2c3e50;
  margin: 0;
  font-weight: 600;
}

.service-category {
  padding: 4px 12px;
  background: #e8f4fd;
  color: #3498db;
  border-radius: 20px;
  font-size: 0.85rem;
  font-weight: 500;
  width: fit-content;
}

.order-meta {
  display: flex;
  gap: 16px;
  flex-wrap: wrap;
}

.meta-item {
  color: #95a5a6;
  font-size: 0.9rem;
}

.order-appointment {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 12px;
  background: #fffbeb;
  border-radius: 8px;
  width: fit-content;
}

.appointment-label {
  font-size: 0.85rem;
  color: #d97706;
  font-weight: 500;
}

.appointment-value {
  font-size: 0.9rem;
  color: #92400e;
}

.order-refund {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 12px;
  background: #fef2f2;
  border-radius: 8px;
  flex-wrap: wrap;
}

.refund-label {
  font-size: 0.85rem;
  color: #dc2626;
  font-weight: 500;
}

.refund-reason {
  font-size: 0.9rem;
  color: #7f1d1d;
}

.refund-amount {
  font-size: 0.9rem;
  color: #dc2626;
  font-weight: 600;
}

.order-actions {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
  padding-top: 16px;
  border-top: 1px solid #e4e8eb;
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

.action-btn.view {
  background: #e8f4fd;
  color: #3498db;
}

.action-btn.view:hover {
  background: #3498db;
  color: white;
}

.action-btn.approve {
  background: #dcfce7;
  color: #16a34a;
}

.action-btn.approve:hover {
  background: #16a34a;
  color: white;
}

.action-btn.reject,
.action-btn.cancel,
.action-btn.delete {
  background: #fef2f2;
  color: #dc2626;
}

.action-btn.reject:hover,
.action-btn.cancel:hover,
.action-btn.delete:hover {
  background: #dc2626;
  color: white;
}

.action-btn.complete {
  background: #dcfce7;
  color: #16a34a;
}

.action-btn.complete:hover {
  background: #16a34a;
  color: white;
}

.pagination-section {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-top: 20px;
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

.load-error {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 60px 20px;
  background: white;
  border-radius: 16px;
  text-align: center;
}

.error-icon {
  font-size: 4rem;
  margin-bottom: 16px;
}

.load-error h3 {
  font-size: 1.3rem;
  color: #dc2626;
  margin: 0 0 8px 0;
}

.load-error p {
  color: #7f8c8d;
  margin: 0 0 20px 0;
}

.retry-btn {
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

.retry-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(107, 142, 35, 0.3);
}

.loading-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 60px 20px;
  background: white;
  border-radius: 16px;
  text-align: center;
}

.loading-spinner {
  width: 40px;
  height: 40px;
  border: 4px solid #e4e8eb;
  border-top: 4px solid #6B8E23;
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin-bottom: 16px;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.loading-state p {
  color: #7f8c8d;
  margin: 0;
  font-size: 1rem;
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

.modal-container.large {
  max-width: 700px;
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
  position: sticky;
  top: 0;
  background: white;
  z-index: 1;
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

.form-textarea {
  padding: 12px 16px;
  border: 2px solid #e4e8eb;
  border-radius: 10px;
  font-size: 1rem;
  color: #2c3e50;
  outline: none;
  transition: all 0.3s ease;
  background: white;
  resize: vertical;
  min-height: 80px;
}

.form-textarea:focus {
  border-color: #6B8E23;
  box-shadow: 0 0 0 4px rgba(107, 142, 35, 0.1);
}

.detail-section {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.detail-row {
  display: flex;
  gap: 12px;
  padding: 10px 0;
  border-bottom: 1px solid #f0f0f0;
}

.detail-label {
  min-width: 100px;
  font-size: 0.95rem;
  color: #7f8c8d;
  font-weight: 500;
}

.detail-value {
  flex: 1;
  font-size: 0.95rem;
  color: #2c3e50;
}

.detail-value.highlight {
  font-weight: 600;
  color: #e74c3c;
}

.modal-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  padding: 24px;
  border-top: 1px solid #e4e8eb;
  position: sticky;
  bottom: 0;
  background: white;
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

@media (max-width: 768px) {
  .search-bar {
    flex-direction: column;
    align-items: stretch;
  }
  
  .search-left {
    flex-direction: column;
  }
  
  .search-select,
  .search-input {
    width: 100%;
  }
  
  .order-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
  }
  
  .order-basic {
    flex-direction: column;
    align-items: flex-start;
    gap: 8px;
  }
  
  .order-content {
    flex-direction: column;
  }
  
  .order-actions {
    flex-direction: row;
  }
  
  .action-btn {
    flex: 1;
    text-align: center;
  }
  
  .stats-bar {
    gap: 10px;
  }
  
  .stat-item {
    padding: 10px 14px;
  }
  
  .modal-container {
    width: 95%;
    margin: 10px;
    max-height: 95vh;
  }
  
  .modal-header,
  .modal-footer {
    padding: 16px 20px;
  }
}
</style>
