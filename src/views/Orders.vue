<template>
  <div class="orders-page">
    <div class="container">
      <div class="page-header">
        <h1 class="page-title">我的订单</h1>
        <p class="page-subtitle">查看和管理您的所有服务预约订单</p>
      </div>

      <div class="order-tabs">
        <button 
          v-for="tab in orderTabs" 
          :key="tab.value"
          class="order-tab-btn"
          :class="{ active: activeTab === tab.value }"
          @click="switchTab(tab.value)"
        >
          {{ tab.label }}
          <span v-if="tab.count > 0" class="tab-count">{{ tab.count }}</span>
        </button>
      </div>

      <div class="order-filters">
        <div class="filter-group">
          <label class="filter-label">服务类型</label>
          <select v-model="filterCategory" class="filter-select">
            <option value="">全部服务</option>
            <option value="cleaning">保洁服务</option>
            <option value="repair">维修服务</option>
            <option value="delivery">配送服务</option>
            <option value="care">护理服务</option>
          </select>
        </div>
        <div class="filter-group">
          <label class="filter-label">排序方式</label>
          <select v-model="sortBy" class="filter-select">
            <option value="createTimeDesc">按创建时间降序</option>
            <option value="createTimeAsc">按创建时间升序</option>
            <option value="amountDesc">按金额降序</option>
            <option value="amountAsc">按金额升序</option>
          </select>
        </div>
      </div>

      <div class="orders-list" v-if="filteredOrders.length > 0">
        <div 
          v-for="order in filteredOrders" 
          :key="order.id" 
          class="order-card"
        >
          <div class="order-card-header">
            <div class="order-id">
              <span class="order-label">订单编号</span>
              <span class="order-value">{{ order.orderNo }}</span>
            </div>
            <div class="order-status" :class="getStatusClass(order.status)">
              <span class="status-icon">{{ getStatusIcon(order.status) }}</span>
              <span class="status-text">{{ getStatusText(order.status) }}</span>
            </div>
          </div>

          <div class="order-card-content">
            <div class="order-service-info">
              <div class="service-image">
                <img :src="order.serviceImage" :alt="order.serviceName" />
              </div>
              <div class="service-details">
                <h3 class="service-name">{{ order.serviceName }}</h3>
                <div class="service-meta">
                  <span class="meta-item">
                    <span class="meta-icon">📋</span>
                    <span class="meta-text">{{ order.categoryName }}</span>
                  </span>
                  <span class="meta-item" v-if="order.contactName">
                    <span class="meta-icon">👤</span>
                    <span class="meta-text">{{ order.contactName }}</span>
                  </span>
                  <span class="meta-item" v-if="order.contactPhone">
                    <span class="meta-icon">📞</span>
                    <span class="meta-text">{{ order.contactPhone }}</span>
                  </span>
                </div>
                <div class="appointment-time">
                  <span class="appointment-icon">⏰</span>
                  <span class="appointment-label">预约时间：</span>
                  <span class="appointment-value">{{ order.appointmentTime }}</span>
                </div>
                <div class="appointment-time" v-if="order.serviceAddress">
                  <span class="appointment-icon">📍</span>
                  <span class="appointment-label">服务地址：</span>
                  <span class="appointment-value">{{ order.serviceAddress }}</span>
                </div>
              </div>
            </div>

            <div class="order-price-section">
              <div class="price-row">
                <span class="price-label">服务单价</span>
                <span class="price-value">¥{{ order.unitPrice }}</span>
              </div>
              <div class="price-row" v-if="order.quantity > 1">
                <span class="price-label">数量</span>
                <span class="price-value">× {{ order.quantity }}</span>
              </div>
              <div class="price-row total">
                <span class="price-label">订单金额</span>
                <span class="price-value total-price">¥{{ order.totalAmount }}</span>
              </div>
            </div>
          </div>

          <div class="order-card-footer">
            <div class="order-time-info">
              <span class="create-time">创建时间：{{ order.createTime }}</span>
              <span v-if="order.payTime" class="pay-time">支付时间：{{ order.payTime }}</span>
            </div>
            <div class="order-actions">
              <button 
                class="action-btn view-btn"
                @click="viewOrderDetail(order)"
              >
                查看详情
              </button>
              
              <button 
                v-if="order.status === 'pending'"
                class="action-btn pay-btn"
                @click="payOrder(order)"
              >
                立即支付
              </button>
              
              <button 
                v-if="order.status === 'pending'"
                class="action-btn cancel-btn"
                @click="cancelOrder(order)"
              >
                取消订单
              </button>
              
              <button 
                v-if="order.status === 'confirmed' || order.status === 'inProgress'"
                class="action-btn refund-btn"
                @click="applyRefund(order)"
              >
                申请退款
              </button>
              
              <button 
                v-if="order.status === 'refunding'"
                class="action-btn cancel-refund-btn"
                @click="cancelRefund(order)"
              >
                取消退款申请
              </button>
              
              <button 
                v-if="order.status === 'inProgress'"
                class="action-btn contact-btn"
                @click="contactService(order)"
              >
                联系服务人员
              </button>
              
              <button 
                v-if="order.status === 'completed' && !order.isReviewed"
                class="action-btn review-btn"
                @click="reviewOrder(order)"
              >
                立即评价
              </button>
              
              <button 
                v-if="order.status === 'completed' && order.isReviewed"
                class="action-btn view-review-btn"
                @click="viewReview(order)"
              >
                查看评价
              </button>
            </div>
          </div>
        </div>
      </div>

      <div class="empty-state" v-else>
        <div class="empty-icon">📭</div>
        <h3 class="empty-title">暂无订单</h3>
        <p class="empty-text">
          {{ getEmptyMessage() }}
        </p>
        <router-link to="/services" class="empty-action-btn">
          去浏览服务
        </router-link>
      </div>

      <div class="pagination-section" v-if="filteredOrders.length > 0">
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
              <option :value="5">5条</option>
              <option :value="10">10条</option>
              <option :value="20">20条</option>
              <option :value="50">50条</option>
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

    <div class="modal-overlay" :class="{ show: showDetailModal }" @click="closeDetailModal">
      <div class="modal-container detail-modal" @click.stop>
        <div class="modal-header">
          <h3 class="modal-title">订单详情</h3>
          <button class="modal-close-btn" @click="closeDetailModal">×</button>
        </div>
        <div class="modal-body">
          <div v-if="selectedOrder" class="order-detail-content">
            <div class="detail-section">
              <h4 class="detail-section-title">订单信息</h4>
              <div class="detail-items">
                <div class="detail-item">
                  <span class="detail-label">订单编号</span>
                  <span class="detail-value">{{ selectedOrder.orderNo }}</span>
                </div>
                <div class="detail-item">
                  <span class="detail-label">订单状态</span>
                  <span class="detail-value" :class="getStatusClass(selectedOrder.status)">
                    {{ getStatusText(selectedOrder.status) }}
                  </span>
                </div>
                <div class="detail-item">
                  <span class="detail-label">创建时间</span>
                  <span class="detail-value">{{ selectedOrder.createTime }}</span>
                </div>
                <div class="detail-item" v-if="selectedOrder.payTime">
                  <span class="detail-label">支付时间</span>
                  <span class="detail-value">{{ selectedOrder.payTime }}</span>
                </div>
              </div>
            </div>

            <div class="detail-section">
              <h4 class="detail-section-title">服务信息</h4>
              <div class="service-detail-card">
                <div class="service-detail-image">
                  <img :src="selectedOrder.serviceImage" :alt="selectedOrder.serviceName" />
                </div>
                <div class="service-detail-info">
                  <h5 class="service-detail-name">{{ selectedOrder.serviceName }}</h5>
                  <p class="service-detail-desc">{{ selectedOrder.serviceDescription }}</p>
                  <div class="service-detail-meta">
                    <span class="meta-item">
                      <span class="meta-icon">📋</span>
                      <span class="meta-text">{{ selectedOrder.categoryName }}</span>
                    </span>
                    <span class="meta-item">
                      <span class="meta-icon">💰</span>
                      <span class="meta-text">¥{{ selectedOrder.unitPrice }}/次</span>
                    </span>
                  </div>
                </div>
              </div>
            </div>

            <div class="detail-section">
              <h4 class="detail-section-title">预约信息</h4>
              <div class="detail-items">
                <div class="detail-item">
                  <span class="detail-label">预约时间</span>
                  <span class="detail-value">{{ selectedOrder.appointmentTime }}</span>
                </div>
                <div class="detail-item">
                  <span class="detail-label">服务地址</span>
                  <span class="detail-value">{{ selectedOrder.serviceAddress }}</span>
                </div>
                <div class="detail-item" v-if="selectedOrder.contactName">
                  <span class="detail-label">联系人</span>
                  <span class="detail-value">{{ selectedOrder.contactName }}</span>
                </div>
                <div class="detail-item" v-if="selectedOrder.contactPhone">
                  <span class="detail-label">联系电话</span>
                  <span class="detail-value">{{ selectedOrder.contactPhone }}</span>
                </div>
              </div>
            </div>

            <div class="detail-section">
              <h4 class="detail-section-title">费用明细</h4>
              <div class="price-detail">
                <div class="price-detail-row">
                  <span class="price-detail-label">服务费用</span>
                  <span class="price-detail-value">¥{{ selectedOrder.unitPrice }} × {{ selectedOrder.quantity }}</span>
                </div>
                <div class="price-detail-row" v-if="selectedOrder.extraFee > 0">
                  <span class="price-detail-label">额外费用</span>
                  <span class="price-detail-value">+ ¥{{ selectedOrder.extraFee }}</span>
                </div>
                <div class="price-detail-row" v-if="selectedOrder.discount > 0">
                  <span class="price-detail-label">优惠减免</span>
                  <span class="price-detail-value">- ¥{{ selectedOrder.discount }}</span>
                </div>
                <div class="price-detail-row total">
                  <span class="price-detail-label">实付金额</span>
                  <span class="price-detail-value">¥{{ selectedOrder.totalAmount }}</span>
                </div>
              </div>
            </div>

            <div class="detail-section" v-if="selectedOrder.remark">
              <h4 class="detail-section-title">备注信息</h4>
              <p class="remark-text">{{ selectedOrder.remark }}</p>
            </div>
          </div>
        </div>
        <div class="modal-footer">
          <button class="modal-btn secondary-btn" @click="closeDetailModal">关闭</button>
        </div>
      </div>
    </div>

    <div class="modal-overlay" :class="{ show: showCancelModal }" @click="closeCancelModal">
      <div class="modal-container small-modal" @click.stop>
        <div class="modal-header">
          <h3 class="modal-title">取消订单</h3>
          <button class="modal-close-btn" @click="closeCancelModal">×</button>
        </div>
        <div class="modal-body">
          <p class="cancel-confirm-text">确定要取消该订单吗？</p>
          <div class="cancel-reason-section">
            <label class="reason-label">请选择取消原因</label>
            <select v-model="cancelReason" class="reason-select">
              <option value="">请选择原因</option>
              <option value="planChange">计划有变</option>
              <option value="price">价格太贵</option>
              <option value="service">服务不满意</option>
              <option value="other">其他原因</option>
            </select>
          </div>
        </div>
        <div class="modal-footer">
          <button class="modal-btn secondary-btn" @click="closeCancelModal">取消</button>
          <button class="modal-btn danger-btn" @click="confirmCancelOrder">确认取消</button>
        </div>
      </div>
    </div>

    <div class="modal-overlay" :class="{ show: showReviewModal }" @click="closeReviewModal">
      <div class="modal-container review-modal" @click.stop>
        <div class="modal-header">
          <h3 class="modal-title">服务评价</h3>
          <button class="modal-close-btn" @click="closeReviewModal">×</button>
        </div>
        <div class="modal-body">
          <div class="review-content">
            <div class="rating-section">
              <label class="rating-label">服务评分</label>
              <div class="stars">
                <span 
                  v-for="star in 5" 
                  :key="star"
                  class="star"
                  :class="{ filled: star <= reviewRating }"
                  @click="reviewRating = star"
                >
                  ★
                </span>
                <span class="rating-text">{{ reviewRating }} 分</span>
              </div>
            </div>
            <div class="review-text-section">
              <label class="review-text-label">评价内容</label>
              <textarea 
                v-model="reviewContent"
                class="review-textarea"
                placeholder="请分享您的服务体验..."
                rows="4"
              ></textarea>
              <div class="char-count">{{ reviewContent.length }}/500</div>
            </div>
          </div>
        </div>
        <div class="modal-footer">
          <button class="modal-btn secondary-btn" @click="closeReviewModal">取消</button>
          <button class="modal-btn primary-btn" @click="submitReview">提交评价</button>
        </div>
      </div>
    </div>

    <div class="modal-overlay" :class="{ show: showRefundModal }" @click="closeRefundModal">
      <div class="modal-container refund-modal" @click.stop>
        <div class="modal-header">
          <h3 class="modal-title">申请退款</h3>
          <button class="modal-close-btn" @click="closeRefundModal">×</button>
        </div>
        <div class="modal-body">
          <div class="refund-content">
            <div class="refund-info" v-if="selectedOrder">
              <div class="refund-order-info">
                <div class="info-row">
                  <span class="info-label">订单编号：</span>
                  <span class="info-value">{{ selectedOrder.orderNo }}</span>
                </div>
                <div class="info-row">
                  <span class="info-label">服务名称：</span>
                  <span class="info-value">{{ selectedOrder.serviceName }}</span>
                </div>
                <div class="info-row">
                  <span class="info-label">退款金额：</span>
                  <span class="info-value price">¥{{ selectedOrder.payAmount || selectedOrder.totalAmount }}</span>
                </div>
              </div>
            </div>
            <div class="refund-reason-section">
              <label class="reason-label">请填写退款原因</label>
              <textarea 
                v-model="refundReason"
                class="reason-textarea"
                placeholder="请详细说明退款原因..."
                rows="4"
              ></textarea>
            </div>
          </div>
        </div>
        <div class="modal-footer">
          <button class="modal-btn secondary-btn" @click="closeRefundModal">取消</button>
          <button class="modal-btn danger-btn" @click="confirmRefund">提交申请</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { useRouter } from 'vue-router'
import request from '../utils/request'

const router = useRouter()

const activeTab = ref('all')
const filterCategory = ref('')
const sortBy = ref('createTimeDesc')
const currentPage = ref(1)
const pageSize = ref(5)

const showDetailModal = ref(false)
const showCancelModal = ref(false)
const showReviewModal = ref(false)
const showRefundModal = ref(false)

const selectedOrder = ref(null)
const cancelReason = ref('')
const reviewRating = ref(5)
const reviewContent = ref('')
const refundReason = ref('')

const ordersList = ref([])
const loading = ref(false)
const total = ref(0)
const totalPages = ref(0)

const jumpPageInput = ref('')
const showToast = ref(false)
const toastMessage = ref('')
const toastIcon = ref('⚠️')
let toastTimer = null

const orderTabs = ref([
  { label: '全部', value: 'all', count: 0 },
  { label: '待付款', value: 'pending', count: 0 },
  { label: '待服务', value: 'confirmed', count: 0 },
  { label: '进行中', value: 'inProgress', count: 0 },
  { label: '退款中', value: 'refunding', count: 0 },
  { label: '已完成', value: 'completed', count: 0 },
  { label: '已取消', value: 'cancelled', count: 0 }
])

const filteredOrders = computed(() => {
  return ordersList.value
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

const getStatusText = (status) => {
  const statusMap = {
    'pending': '待付款',
    'confirmed': '待服务',
    'inProgress': '进行中',
    'refunding': '退款中',
    'completed': '已完成',
    'cancelled': '已取消'
  }
  return statusMap[status] || '未知状态'
}

const getStatusClass = (status) => {
  const classMap = {
    'pending': 'status-pending',
    'confirmed': 'status-confirmed',
    'inProgress': 'status-in-progress',
    'refunding': 'status-refunding',
    'completed': 'status-completed',
    'cancelled': 'status-cancelled'
  }
  return classMap[status] || ''
}

const getStatusIcon = (status) => {
  const iconMap = {
    'pending': '💳',
    'confirmed': '✅',
    'inProgress': '🚀',
    'refunding': '🔄',
    'completed': '🎉',
    'cancelled': '❌'
  }
  return iconMap[status] || '📋'
}

const getEmptyMessage = () => {
  if (activeTab.value === 'all') {
    return '您还没有任何订单，快去浏览我们的服务吧！'
  }
  const tabName = orderTabs.value.find(tab => tab.value === activeTab.value)?.label || ''
  return `您当前没有${tabName}的订单`
}

const formatDateTime = (dateTime) => {
  if (!dateTime) return null
  const date = new Date(dateTime)
  return date.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit',
    second: '2-digit'
  })
}

const fetchOrders = async () => {
  const token = localStorage.getItem('token')
  if (!token) {
    return
  }
  
  loading.value = true
  try {
    let statusParam = activeTab.value === 'all' ? '' : activeTab.value
    
    const response = await request(
      `/api/orders/list?status=${statusParam}&sortBy=${sortBy.value}&current=${currentPage.value}&size=${pageSize.value}`,
      { method: 'GET' }
    )
    
    const data = await response.json()
    
    if (data.code === 200 && data.data) {
      ordersList.value = data.data.records || []
      total.value = data.data.total || 0
      totalPages.value = data.data.pages || 0
      
      ordersList.value.forEach(order => {
        if (order.createTime) {
          order.createTime = formatDateTime(order.createTime)
        }
        if (order.payTime) {
          order.payTime = formatDateTime(order.payTime)
        }
        if (order.discountAmount) {
          order.discount = order.discountAmount
        }
      })
    }
  } catch (error) {
    console.error('获取订单列表失败:', error)
  } finally {
    loading.value = false
  }
}

const fetchOrderStats = async () => {
  const token = localStorage.getItem('token')
  if (!token) {
    return
  }
  
  try {
    const response = await request('/api/orders/stats', { method: 'GET' })
    const data = await response.json()
    
    if (data.code === 200 && data.data) {
      orderTabs.value.forEach(tab => {
        if (tab.value === 'all') {
          tab.count = data.data.all || 0
        } else if (tab.value === 'pending') {
          tab.count = data.data.pending || 0
        } else if (tab.value === 'confirmed') {
          tab.count = data.data.confirmed || 0
        } else if (tab.value === 'inProgress') {
          tab.count = data.data.inProgress || 0
        } else if (tab.value === 'refunding') {
          tab.count = data.data.refunding || 0
        } else if (tab.value === 'completed') {
          tab.count = data.data.completed || 0
        } else if (tab.value === 'cancelled') {
          tab.count = data.data.cancelled || 0
        }
      })
    }
  } catch (error) {
    console.error('获取订单统计失败:', error)
  }
}

const switchTab = (tabValue) => {
  activeTab.value = tabValue
  currentPage.value = 1
  fetchOrders()
}

const viewOrderDetail = (order) => {
  selectedOrder.value = order
  showDetailModal.value = true
}

const closeDetailModal = () => {
  showDetailModal.value = false
  selectedOrder.value = null
}

const payOrder = async (order) => {
  const confirmed = confirm(`确认支付订单 ${order.orderNo}？\n订单金额：¥${order.totalAmount}\n\n将从您的余额中扣款。`)
  if (!confirmed) return
  
  try {
    const response = await request('/api/orders/pay', {
      method: 'POST',
      body: JSON.stringify({
        orderId: order.id,
        payMethod: 0
      })
    })
    
    const data = await response.json()
    
    if (data.code === 200) {
      alert('支付成功！')
      fetchOrders()
      fetchOrderStats()
    } else {
      alert(data.message || '支付失败，请重试')
    }
  } catch (error) {
    console.error('支付订单失败:', error)
    alert('网络错误，请稍后重试')
  }
}

const cancelOrder = (order) => {
  selectedOrder.value = order
  cancelReason.value = ''
  showCancelModal.value = true
}

const closeCancelModal = () => {
  showCancelModal.value = false
  selectedOrder.value = null
  cancelReason.value = ''
}

const confirmCancelOrder = async () => {
  if (!cancelReason.value) {
    alert('请选择取消原因')
    return
  }
  
  if (!selectedOrder.value) return
  
  try {
    const response = await request('/api/orders/cancel', {
      method: 'POST',
      body: JSON.stringify({
        orderId: selectedOrder.value.id,
        cancelReason: cancelReason.value
      })
    })
    
    const data = await response.json()
    
    if (data.code === 200) {
      alert('订单已取消')
      closeCancelModal()
      fetchOrders()
      fetchOrderStats()
    } else {
      alert(data.message || '取消订单失败，请重试')
    }
  } catch (error) {
    console.error('取消订单失败:', error)
    alert('网络错误，请稍后重试')
  }
}

const contactService = (order) => {
  alert('即将联系服务人员...\n服务热线：400-123-4567')
}

const reviewOrder = (order) => {
  selectedOrder.value = order
  reviewRating.value = 5
  reviewContent.value = ''
  showReviewModal.value = true
}

const closeReviewModal = () => {
  showReviewModal.value = false
  selectedOrder.value = null
  reviewRating.value = 5
  reviewContent.value = ''
}

const submitReview = async () => {
  if (reviewContent.value.trim().length === 0) {
    alert('请填写评价内容')
    return
  }
  
  if (!selectedOrder.value) return
  
  try {
    const response = await request('/api/orders/review', {
      method: 'POST',
      body: JSON.stringify({
        orderId: selectedOrder.value.id,
        rating: reviewRating.value,
        content: reviewContent.value,
        isAnonymous: 0
      })
    })
    
    const data = await response.json()
    
    if (data.code === 200) {
      alert('评价提交成功！')
      closeReviewModal()
      fetchOrders()
    } else {
      alert(data.message || '评价提交失败，请重试')
    }
  } catch (error) {
    console.error('提交评价失败:', error)
    alert('网络错误，请稍后重试')
  }
}

const viewReview = async (order) => {
  try {
    const response = await request(`/api/orders/review/${order.id}`, { method: 'GET' })
    const data = await response.json()
    
    if (data.code === 200 && data.data) {
      alert(`该订单已评价\n\n评分：${data.data.rating} 分\n评价内容：${data.data.content || '无'}`)
    } else {
      alert('该订单已评价')
    }
  } catch (error) {
    console.error('获取评价失败:', error)
    alert('该订单已评价')
  }
}

const applyRefund = (order) => {
  selectedOrder.value = order
  refundReason.value = ''
  showRefundModal.value = true
}

const closeRefundModal = () => {
  showRefundModal.value = false
  selectedOrder.value = null
  refundReason.value = ''
}

const confirmRefund = async () => {
  if (!refundReason.value.trim()) {
    alert('请输入退款原因')
    return
  }
  
  if (!selectedOrder.value) return
  
  try {
    const response = await request('/api/orders/refund', {
      method: 'POST',
      body: JSON.stringify({
        orderId: selectedOrder.value.id,
        refundReason: refundReason.value
      })
    })
    
    const data = await response.json()
    
    if (data.code === 200) {
      alert('退款申请已提交，等待处理')
      closeRefundModal()
      fetchOrders()
      fetchOrderStats()
    } else {
      alert(data.message || '退款申请失败，请重试')
    }
  } catch (error) {
    console.error('提交退款申请失败:', error)
    alert('网络错误，请稍后重试')
  }
}

const cancelRefund = async (order) => {
  const confirmed = confirm('确定要取消退款申请吗？')
  if (!confirmed) return
  
  try {
    const response = await request('/api/orders/refund/cancel', {
      method: 'POST',
      body: JSON.stringify({
        orderId: order.id
      })
    })
    
    const data = await response.json()
    
    if (data.code === 200) {
      alert('退款申请已取消')
      fetchOrders()
      fetchOrderStats()
    } else {
      alert(data.message || '取消退款申请失败，请重试')
    }
  } catch (error) {
    console.error('取消退款申请失败:', error)
    alert('网络错误，请稍后重试')
  }
}

const prevPage = () => {
  if (currentPage.value > 1) {
    currentPage.value--
    fetchOrders()
  }
}

const nextPage = () => {
  if (currentPage.value < totalPages.value) {
    currentPage.value++
    fetchOrders()
  }
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
  fetchOrders()
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
  fetchOrders()
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
  const token = localStorage.getItem('token')
  if (!token) {
    alert('请先登录')
    router.push('/login')
    return
  }
  
  fetchOrders()
  fetchOrderStats()
})
</script>

<style scoped>
.orders-page {
  min-height: calc(100vh - 120px);
  padding: 40px 0;
  background: linear-gradient(180deg, #f8f9fa 0%, #ffffff 100%);
}

.page-header {
  text-align: center;
  margin-bottom: 40px;
}

.page-title {
  font-size: 2.5rem;
  font-weight: bold;
  color: #2c3e50;
  margin-bottom: 10px;
}

.page-subtitle {
  font-size: 1.1rem;
  color: #7f8c8d;
}

.order-tabs {
  display: flex;
  justify-content: center;
  gap: 10px;
  margin-bottom: 30px;
  flex-wrap: wrap;
}

.order-tab-btn {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 12px 24px;
  background: white;
  border: 2px solid #e4e8eb;
  border-radius: 25px;
  font-size: 0.95rem;
  font-weight: 500;
  color: #555;
  cursor: pointer;
  transition: all 0.3s ease;
}

.order-tab-btn:hover {
  border-color: #6B8E23;
  color: #6B8E23;
  transform: translateY(-2px);
}

.order-tab-btn.active {
  background: linear-gradient(135deg, #6B8E23, #8FBC8F);
  border-color: #6B8E23;
  color: white;
  box-shadow: 0 5px 15px rgba(107, 142, 35, 0.3);
}

.tab-count {
  background: rgba(255, 255, 255, 0.2);
  padding: 2px 8px;
  border-radius: 10px;
  font-size: 0.8rem;
  font-weight: 600;
}

.order-tab-btn:not(.active) .tab-count {
  background: #e4e8eb;
  color: #7f8c8d;
}

.order-filters {
  display: flex;
  gap: 20px;
  margin-bottom: 30px;
  padding: 20px;
  background: white;
  border-radius: 12px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.05);
}

.filter-group {
  display: flex;
  align-items: center;
  gap: 10px;
}

.filter-label {
  font-size: 0.95rem;
  font-weight: 500;
  color: #555;
}

.filter-select {
  padding: 10px 16px;
  border: 2px solid #e4e8eb;
  border-radius: 8px;
  font-size: 0.95rem;
  color: #2c3e50;
  background: white;
  cursor: pointer;
  outline: none;
  transition: all 0.3s ease;
}

.filter-select:focus {
  border-color: #6B8E23;
  box-shadow: 0 0 0 3px rgba(107, 142, 35, 0.1);
}

.orders-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.order-card {
  background: white;
  border-radius: 16px;
  box-shadow: 0 2px 15px rgba(0, 0, 0, 0.08);
  overflow: hidden;
  transition: all 0.3s ease;
}

.order-card:hover {
  transform: translateY(-3px);
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.12);
}

.order-card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 24px;
  background: linear-gradient(135deg, #f8f9fa, #f0f3f5);
  border-bottom: 1px solid #e4e8eb;
}

.order-id {
  display: flex;
  align-items: center;
  gap: 8px;
}

.order-label {
  font-size: 0.9rem;
  color: #7f8c8d;
}

.order-value {
  font-size: 0.95rem;
  font-weight: 600;
  color: #2c3e50;
  font-family: 'Courier New', monospace;
}

.order-status {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 6px 16px;
  border-radius: 20px;
  font-size: 0.9rem;
  font-weight: 500;
}

.status-icon {
  font-size: 1rem;
}

.status-pending {
  background: #fef3c7;
  color: #d97706;
}

.status-confirmed {
  background: #dbeafe;
  color: #2563eb;
}

.status-in-progress {
  background: #d1fae5;
  color: #059669;
}

.status-completed {
  background: #e0e7ff;
  color: #4f46e5;
}

.status-cancelled {
  background: #fee2e2;
  color: #dc2626;
}

.order-card-content {
  display: flex;
  justify-content: space-between;
  padding: 24px;
  gap: 30px;
}

.order-service-info {
  display: flex;
  gap: 20px;
  flex: 1;
}

.service-image {
  width: 120px;
  height: 120px;
  border-radius: 12px;
  overflow: hidden;
  flex-shrink: 0;
}

.service-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s ease;
}

.service-image:hover img {
  transform: scale(1.05);
}

.service-details {
  display: flex;
  flex-direction: column;
  gap: 10px;
  flex: 1;
}

.service-name {
  font-size: 1.2rem;
  font-weight: 600;
  color: #2c3e50;
  margin: 0;
}

.service-meta {
  display: flex;
  gap: 20px;
  flex-wrap: wrap;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 0.9rem;
  color: #555;
}

.meta-icon {
  font-size: 0.9rem;
}

.appointment-time {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 8px 12px;
  background: linear-gradient(135deg, rgba(107, 142, 35, 0.1), rgba(143, 188, 143, 0.1));
  border-radius: 8px;
  font-size: 0.9rem;
  color: #5D7C4A;
}

.appointment-icon {
  font-size: 0.95rem;
}

.appointment-label {
  font-weight: 500;
}

.appointment-value {
  font-weight: 600;
}

.order-price-section {
  display: flex;
  flex-direction: column;
  gap: 8px;
  min-width: 150px;
  padding: 16px;
  background: linear-gradient(135deg, #f8f9fa, #f0f3f5);
  border-radius: 12px;
}

.price-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 0.95rem;
}

.price-label {
  color: #7f8c8d;
}

.price-value {
  color: #2c3e50;
  font-weight: 500;
}

.price-row.total {
  margin-top: 8px;
  padding-top: 8px;
  border-top: 1px solid #e4e8eb;
}

.price-row.total .price-label {
  font-size: 1rem;
  font-weight: 600;
  color: #2c3e50;
}

.total-price {
  font-size: 1.3rem;
  font-weight: bold;
  color: #6B8E23;
}

.order-card-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 24px;
  background: linear-gradient(135deg, #fafbfc, #f5f7fa);
  border-top: 1px solid #e4e8eb;
}

.order-time-info {
  display: flex;
  gap: 20px;
  font-size: 0.85rem;
  color: #7f8c8d;
}

.order-actions {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
}

.action-btn {
  padding: 8px 20px;
  border: none;
  border-radius: 8px;
  font-size: 0.9rem;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;
}

.view-btn {
  background: #e8ecef;
  color: #555;
}

.view-btn:hover {
  background: #d1d5db;
  transform: translateY(-1px);
}

.pay-btn {
  background: linear-gradient(135deg, #6B8E23, #8FBC8F);
  color: white;
}

.pay-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 5px 15px rgba(107, 142, 35, 0.3);
}

.cancel-btn {
  background: #fee2e2;
  color: #dc2626;
}

.cancel-btn:hover {
  background: #fecaca;
  transform: translateY(-1px);
}

.contact-btn {
  background: linear-gradient(135deg, #3b82f6, #60a5fa);
  color: white;
}

.contact-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 5px 15px rgba(59, 130, 246, 0.3);
}

.review-btn {
  background: linear-gradient(135deg, #8b5cf6, #a78bfa);
  color: white;
}

.review-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 5px 15px rgba(139, 92, 246, 0.3);
}

.view-review-btn {
  background: #e0e7ff;
  color: #4f46e5;
}

.view-review-btn:hover {
  background: #c7d2fe;
  transform: translateY(-1px);
}

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 80px 20px;
  text-align: center;
}

.empty-icon {
  font-size: 5rem;
  margin-bottom: 20px;
}

.empty-title {
  font-size: 1.5rem;
  font-weight: 600;
  color: #2c3e50;
  margin-bottom: 10px;
}

.empty-text {
  font-size: 1rem;
  color: #7f8c8d;
  margin-bottom: 30px;
  max-width: 400px;
}

.empty-action-btn {
  padding: 14px 40px;
  background: linear-gradient(135deg, #6B8E23, #8FBC8F);
  color: white;
  border-radius: 25px;
  font-size: 1rem;
  font-weight: 600;
  transition: all 0.3s ease;
}

.empty-action-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 5px 20px rgba(107, 142, 35, 0.4);
}

.pagination-section {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-top: 40px;
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
  max-height: 85vh;
  overflow-y: auto;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
  transform: scale(0.9) translateY(20px);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.modal-overlay.show .modal-container {
  transform: scale(1) translateY(0);
}

.small-modal {
  max-width: 400px;
}

.review-modal {
  max-width: 500px;
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 24px;
  border-bottom: 1px solid #e4e8eb;
}

.modal-title {
  font-size: 1.3rem;
  font-weight: 600;
  color: #2c3e50;
  margin: 0;
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
}

.modal-close-btn:hover {
  background: #C45B1A;
  color: white;
  transform: rotate(90deg);
}

.modal-body {
  padding: 24px;
}

.modal-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  padding: 16px 24px;
  border-top: 1px solid #e4e8eb;
}

.modal-btn {
  padding: 12px 28px;
  border: none;
  border-radius: 10px;
  font-size: 0.95rem;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
}

.secondary-btn {
  background: #f5f7fa;
  color: #7f8c8d;
  border: 1px solid #e4e8eb;
}

.secondary-btn:hover {
  background: #e8ecef;
  color: #555;
}

.primary-btn {
  background: linear-gradient(135deg, #6B8E23, #8FBC8F);
  color: white;
}

.primary-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 5px 15px rgba(107, 142, 35, 0.3);
}

.danger-btn {
  background: linear-gradient(135deg, #C45B1A, #9E3B1B);
  color: white;
}

.danger-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 5px 15px rgba(196, 91, 26, 0.3);
}

.order-detail-content {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.detail-section {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.detail-section-title {
  font-size: 1.1rem;
  font-weight: 600;
  color: #2c3e50;
  margin: 0;
  padding-bottom: 8px;
  border-bottom: 2px solid #6B8E23;
  display: inline-block;
}

.detail-items {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 12px;
}

.detail-item {
  display: flex;
  flex-direction: column;
  gap: 4px;
  padding: 12px 16px;
  background: #f8f9fa;
  border-radius: 10px;
}

.detail-label {
  font-size: 0.85rem;
  color: #7f8c8d;
}

.detail-value {
  font-size: 0.95rem;
  font-weight: 500;
  color: #2c3e50;
}

.service-detail-card {
  display: flex;
  gap: 20px;
  padding: 20px;
  background: linear-gradient(135deg, #f8f9fa, #f0f3f5);
  border-radius: 12px;
}

.service-detail-image {
  width: 100px;
  height: 100px;
  border-radius: 10px;
  overflow: hidden;
  flex-shrink: 0;
}

.service-detail-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.service-detail-info {
  display: flex;
  flex-direction: column;
  gap: 8px;
  flex: 1;
}

.service-detail-name {
  font-size: 1.1rem;
  font-weight: 600;
  color: #2c3e50;
  margin: 0;
}

.service-detail-desc {
  font-size: 0.9rem;
  color: #555;
  line-height: 1.5;
  margin: 0;
}

.service-detail-meta {
  display: flex;
  gap: 20px;
}

.price-detail {
  display: flex;
  flex-direction: column;
  gap: 12px;
  padding: 20px;
  background: linear-gradient(135deg, #f8f9fa, #f0f3f5);
  border-radius: 12px;
}

.price-detail-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 0.95rem;
}

.price-detail-label {
  color: #555;
}

.price-detail-value {
  color: #2c3e50;
  font-weight: 500;
}

.price-detail-row.total {
  margin-top: 8px;
  padding-top: 12px;
  border-top: 1px solid #e4e8eb;
}

.price-detail-row.total .price-detail-label {
  font-size: 1rem;
  font-weight: 600;
  color: #2c3e50;
}

.price-detail-row.total .price-detail-value {
  font-size: 1.3rem;
  font-weight: bold;
  color: #6B8E23;
}

.remark-text {
  padding: 16px;
  background: #f8f9fa;
  border-radius: 10px;
  font-size: 0.95rem;
  color: #555;
  line-height: 1.6;
  margin: 0;
}

.cancel-confirm-text {
  font-size: 1.1rem;
  font-weight: 500;
  color: #2c3e50;
  text-align: center;
  margin-bottom: 20px;
}

.cancel-reason-section {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.reason-label {
  font-size: 0.95rem;
  font-weight: 500;
  color: #555;
}

.reason-select {
  padding: 12px 16px;
  border: 2px solid #e4e8eb;
  border-radius: 10px;
  font-size: 0.95rem;
  color: #2c3e50;
  outline: none;
  transition: all 0.3s ease;
}

.reason-select:focus {
  border-color: #6B8E23;
  box-shadow: 0 0 0 3px rgba(107, 142, 35, 0.1);
}

.review-content {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.rating-section {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.rating-label {
  font-size: 0.95rem;
  font-weight: 500;
  color: #2c3e50;
}

.stars {
  display: flex;
  align-items: center;
  gap: 5px;
}

.star {
  font-size: 2rem;
  color: #e4e8eb;
  cursor: pointer;
  transition: all 0.3s ease;
}

.star:hover {
  transform: scale(1.1);
}

.star.filled {
  color: #fbbf24;
}

.rating-text {
  margin-left: 10px;
  font-size: 1rem;
  font-weight: 600;
  color: #2c3e50;
}

.review-text-section {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.review-text-label {
  font-size: 0.95rem;
  font-weight: 500;
  color: #2c3e50;
}

.review-textarea {
  width: 100%;
  padding: 16px;
  border: 2px solid #e4e8eb;
  border-radius: 12px;
  font-size: 0.95rem;
  color: #2c3e50;
  outline: none;
  resize: vertical;
  transition: all 0.3s ease;
  box-sizing: border-box;
}

.review-textarea:focus {
  border-color: #6B8E23;
  box-shadow: 0 0 0 3px rgba(107, 142, 35, 0.1);
}

.char-count {
  text-align: right;
  font-size: 0.85rem;
  color: #7f8c8d;
}

.status-refunding {
  background: #fef3c7;
  color: #d97706;
}

.refund-btn {
  background: linear-gradient(135deg, #f59e0b, #d97706);
  color: white;
}

.refund-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 5px 15px rgba(245, 158, 11, 0.3);
}

.cancel-refund-btn {
  background: #e0e7ff;
  color: #4f46e5;
}

.cancel-refund-btn:hover {
  background: #c7d2fe;
  transform: translateY(-1px);
}

.refund-modal {
  max-width: 500px;
}

.refund-content {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.refund-order-info {
  padding: 16px;
  background: linear-gradient(135deg, #f8f9fa, #f0f3f5);
  border-radius: 10px;
}

.info-row {
  display: flex;
  align-items: center;
  padding: 8px 0;
  border-bottom: 1px solid #e4e8eb;
}

.info-row:last-child {
  border-bottom: none;
}

.info-label {
  width: 80px;
  font-size: 0.95rem;
  color: #7f8c8d;
}

.info-value {
  font-size: 0.95rem;
  font-weight: 500;
  color: #2c3e50;
}

.info-value.price {
  color: #C45B1A;
  font-weight: bold;
  font-size: 1.1rem;
}

.refund-reason-section {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.reason-textarea {
  width: 100%;
  padding: 16px;
  border: 2px solid #e4e8eb;
  border-radius: 12px;
  font-size: 0.95rem;
  color: #2c3e50;
  outline: none;
  resize: vertical;
  transition: all 0.3s ease;
  box-sizing: border-box;
}

.reason-textarea:focus {
  border-color: #C45B1A;
  box-shadow: 0 0 0 3px rgba(196, 91, 26, 0.1);
}

@media (max-width: 768px) {
  .page-title {
    font-size: 1.8rem;
  }
  
  .order-tabs {
    gap: 8px;
  }
  
  .order-tab-btn {
    padding: 10px 16px;
    font-size: 0.85rem;
  }
  
  .order-filters {
    flex-direction: column;
    gap: 15px;
  }
  
  .filter-group {
    flex-direction: column;
    align-items: flex-start;
  }
  
  .filter-select {
    width: 100%;
  }
  
  .order-card-content {
    flex-direction: column;
    gap: 20px;
  }
  
  .order-service-info {
    flex-direction: column;
  }
  
  .service-image {
    width: 100%;
    height: 150px;
  }
  
  .service-meta {
    gap: 10px;
  }
  
  .order-price-section {
    min-width: auto;
  }
  
  .order-card-footer {
    flex-direction: column;
    gap: 15px;
    align-items: stretch;
  }
  
  .order-time-info {
    flex-direction: column;
    gap: 5px;
  }
  
  .order-actions {
    justify-content: center;
  }
  
  .action-btn {
    padding: 10px 16px;
    font-size: 0.85rem;
  }
  
  .pagination {
    flex-wrap: wrap;
  }
  
  .pagination-numbers {
    flex-wrap: wrap;
    justify-content: center;
  }
  
  .detail-items {
    grid-template-columns: 1fr;
  }
  
  .service-detail-card {
    flex-direction: column;
  }
  
  .service-detail-image {
    width: 100%;
    height: 150px;
  }
  
  .modal-container {
    width: 95%;
    margin: 10px;
    max-height: 90vh;
  }
  
  .modal-header {
    padding: 16px 20px;
  }
  
  .modal-body {
    padding: 20px;
  }
  
  .modal-footer {
    padding: 16px 20px;
  }
}
</style>