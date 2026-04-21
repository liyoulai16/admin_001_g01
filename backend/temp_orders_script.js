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

const selectedOrder = ref(null)
const cancelReason = ref('')
const reviewRating = ref(5)
const reviewContent = ref('')

const ordersList = ref([])
const loading = ref(false)
const total = ref(0)
const totalPages = ref(0)

const orderTabs = ref([
  { label: '全部', value: 'all', count: 0 },
  { label: '待付款', value: 'pending', count: 0 },
  { label: '待服务', value: 'confirmed', count: 0 },
  { label: '进行中', value: 'inProgress', count: 0 },
  { label: '已完成', value: 'completed', count: 0 },
  { label: '已取消', value: 'cancelled', count: 0 }
])

const getStatusText = (status) => {
  const statusMap = {
    'pending': '待付款',
    'confirmed': '待服务',
    'inProgress': '进行中',
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

const goToPage = (page) => {
  currentPage.value = page
  fetchOrders()
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
