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
                  <span class="meta-item">
                    <span class="meta-icon">📍</span>
                    <span class="meta-text">{{ order.serviceAddress }}</span>
                  </span>
                </div>
                <div class="appointment-time">
                  <span class="appointment-icon">⏰</span>
                  <span class="appointment-label">预约时间：</span>
                  <span class="appointment-value">{{ order.appointmentTime }}</span>
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

      <div class="pagination" v-if="filteredOrders.length > 0">
        <button 
          class="pagination-btn prev-btn"
          :disabled="currentPage === 1"
          @click="prevPage"
        >
          上一页
        </button>
        <div class="pagination-numbers">
          <button 
            v-for="page in totalPages" 
            :key="page"
            class="pagination-number"
            :class="{ active: currentPage === page }"
            @click="goToPage(page)"
          >
            {{ page }}
          </button>
        </div>
        <button 
          class="pagination-btn next-btn"
          :disabled="currentPage === totalPages"
          @click="nextPage"
        >
          下一页
        </button>
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
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'

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

const orderTabs = ref([
  { label: '全部', value: 'all', count: 0 },
  { label: '待付款', value: 'pending', count: 2 },
  { label: '待服务', value: 'confirmed', count: 1 },
  { label: '进行中', value: 'inProgress', count: 1 },
  { label: '已完成', value: 'completed', count: 3 },
  { label: '已取消', value: 'cancelled', count: 1 }
])

const mockOrders = ref([
  {
    id: 1,
    orderNo: 'ORD202401150001',
    serviceName: '家庭日常保洁',
    serviceDescription: '专业保洁人员上门服务，包含客厅、卧室、厨房、卫生间等区域的清洁服务。',
    categoryName: '保洁服务',
    serviceImage: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=professional%20home%20cleaning%20service%20with%20cleaning%20tools%20and%20supplies%20in%20a%20tidy%20living%20room&image_size=square',
    unitPrice: 150.00,
    quantity: 1,
    totalAmount: 150.00,
    extraFee: 0,
    discount: 0,
    status: 'pending',
    appointmentTime: '2024-01-20 09:00-12:00',
    serviceAddress: '北京市朝阳区某某小区1号楼101室',
    contactName: '张先生',
    contactPhone: '138****1234',
    createTime: '2024-01-15 10:30:25',
    payTime: null,
    isReviewed: false,
    remark: '请携带专业清洁工具'
  },
  {
    id: 2,
    orderNo: 'ORD202401140002',
    serviceName: '空调清洗服务',
    serviceDescription: '专业空调清洗，包括内机清洗、外机清洗、过滤网清洁等服务。',
    categoryName: '维修服务',
    serviceImage: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=professional%20air%20conditioner%20cleaning%20service%20technician%20cleaning%20AC%20unit%20with%20tools&image_size=square',
    unitPrice: 120.00,
    quantity: 2,
    totalAmount: 240.00,
    extraFee: 0,
    discount: 20,
    status: 'pending',
    appointmentTime: '2024-01-18 14:00-17:00',
    serviceAddress: '北京市海淀区某某小区3号楼202室',
    contactName: '李女士',
    contactPhone: '139****5678',
    createTime: '2024-01-14 15:20:30',
    payTime: null,
    isReviewed: false,
    remark: '两台空调，一台挂机一台柜机'
  },
  {
    id: 3,
    orderNo: 'ORD202401130003',
    serviceName: '家电维修服务',
    serviceDescription: '专业家电维修工程师上门服务，快速诊断和修复各类家电故障。',
    categoryName: '维修服务',
    serviceImage: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=professional%20home%20appliance%20repair%20service%20technician%20repairing%20refrigerator%20with%20tools&image_size=square',
    unitPrice: 80.00,
    quantity: 1,
    totalAmount: 80.00,
    extraFee: 50,
    discount: 0,
    status: 'confirmed',
    appointmentTime: '2024-01-16 10:00-12:00',
    serviceAddress: '北京市西城区某某小区5号楼301室',
    contactName: '王先生',
    contactPhone: '136****9012',
    createTime: '2024-01-13 09:15:45',
    payTime: '2024-01-13 09:20:00',
    isReviewed: false,
    remark: '冰箱不制冷，需要检查'
  },
  {
    id: 4,
    orderNo: 'ORD202401120004',
    serviceName: '老人陪护服务',
    serviceDescription: '专业陪护人员上门服务，为老人提供日常照料、陪伴聊天、协助就医等服务。',
    categoryName: '护理服务',
    serviceImage: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=professional%20elderly%20care%20service%20caregiver%20helping%20senior%20person%20in%20comfortable%20home%20environment&image_size=square',
    unitPrice: 200.00,
    quantity: 1,
    totalAmount: 200.00,
    extraFee: 0,
    discount: 0,
    status: 'inProgress',
    appointmentTime: '2024-01-15 08:00-18:00',
    serviceAddress: '北京市东城区某某小区2号楼401室',
    contactName: '赵女士',
    contactPhone: '137****3456',
    createTime: '2024-01-12 16:30:00',
    payTime: '2024-01-12 16:35:00',
    isReviewed: false,
    remark: '需要协助老人去医院复查'
  },
  {
    id: 5,
    orderNo: 'ORD202401100005',
    serviceName: '深度保洁服务',
    serviceDescription: '全屋深度清洁服务，包括擦玻璃、厨房深度清洁、卫生间深度清洁等。',
    categoryName: '保洁服务',
    serviceImage: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=professional%20deep%20house%20cleaning%20service%20spotless%20clean%20kitchen%20and%20living%20room%20with%20cleaning%20crew&image_size=square',
    unitPrice: 300.00,
    quantity: 1,
    totalAmount: 300.00,
    extraFee: 0,
    discount: 30,
    status: 'completed',
    appointmentTime: '2024-01-12 09:00-17:00',
    serviceAddress: '北京市丰台区某某小区4号楼102室',
    contactName: '刘先生',
    contactPhone: '135****7890',
    createTime: '2024-01-10 11:20:15',
    payTime: '2024-01-10 11:25:00',
    isReviewed: true,
    remark: '新房入住前清洁'
  },
  {
    id: 6,
    orderNo: 'ORD202401080006',
    serviceName: '鲜花配送服务',
    serviceDescription: '精选优质鲜花，专业配送人员准时送达，可代写贺卡。',
    categoryName: '配送服务',
    serviceImage: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=professional%20flower%20delivery%20service%20beautiful%20bouquet%20of%20roses%20and%20lilies%20in%20elegant%20wrapping&image_size=square',
    unitPrice: 168.00,
    quantity: 1,
    totalAmount: 168.00,
    extraFee: 20,
    discount: 0,
    status: 'completed',
    appointmentTime: '2024-01-10 14:00-16:00',
    serviceAddress: '北京市朝阳区某某写字楼A座1001室',
    contactName: '陈女士',
    contactPhone: '133****4567',
    createTime: '2024-01-08 09:00:00',
    payTime: '2024-01-08 09:05:00',
    isReviewed: false,
    remark: '情人节惊喜，请准时送达'
  },
  {
    id: 7,
    orderNo: 'ORD202401050007',
    serviceName: '管道疏通服务',
    serviceDescription: '专业管道疏通服务，解决马桶堵塞、下水道堵塞等问题。',
    categoryName: '维修服务',
    serviceImage: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=professional%20plumbing%20drain%20cleaning%20service%20plumber%20using%20tools%20to%20unclog%20kitchen%20sink&image_size=square',
    unitPrice: 100.00,
    quantity: 1,
    totalAmount: 100.00,
    extraFee: 0,
    discount: 0,
    status: 'completed',
    appointmentTime: '2024-01-05 15:00-17:00',
    serviceAddress: '北京市石景山区某某小区6号楼503室',
    contactName: '孙先生',
    contactPhone: '132****8901',
    createTime: '2024-01-05 10:30:00',
    payTime: '2024-01-05 17:00:00',
    isReviewed: true,
    remark: '厨房下水道堵塞'
  },
  {
    id: 8,
    orderNo: 'ORD202401030008',
    serviceName: '搬家服务',
    serviceDescription: '专业搬家团队，提供打包、搬运、拆装家具等一站式搬家服务。',
    categoryName: '配送服务',
    serviceImage: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=professional%20moving%20service%20movers%20carrying%20furniture%20boxes%20into%20moving%20truck%20professional%20uniforms&image_size=square',
    unitPrice: 500.00,
    quantity: 1,
    totalAmount: 500.00,
    extraFee: 100,
    discount: 0,
    status: 'cancelled',
    appointmentTime: '2024-01-08 08:00-18:00',
    serviceAddress: '北京市通州区某某小区1号楼302室',
    contactName: '周女士',
    contactPhone: '131****2345',
    createTime: '2024-01-03 14:20:30',
    payTime: null,
    isReviewed: false,
    remark: '需要拆装衣柜和床'
  }
])

const filteredOrders = computed(() => {
  let orders = [...mockOrders.value]
  
  if (activeTab.value !== 'all') {
    orders = orders.filter(order => order.status === activeTab.value)
  }
  
  if (filterCategory.value) {
    const categoryMap = {
      'cleaning': '保洁服务',
      'repair': '维修服务',
      'delivery': '配送服务',
      'care': '护理服务'
    }
    const categoryName = categoryMap[filterCategory.value]
    if (categoryName) {
      orders = orders.filter(order => order.categoryName === categoryName)
    }
  }
  
  orders.sort((a, b) => {
    switch (sortBy.value) {
      case 'createTimeDesc':
        return new Date(b.createTime) - new Date(a.createTime)
      case 'createTimeAsc':
        return new Date(a.createTime) - new Date(b.createTime)
      case 'amountDesc':
        return b.totalAmount - a.totalAmount
      case 'amountAsc':
        return a.totalAmount - b.totalAmount
      default:
        return 0
    }
  })
  
  return orders
})

const totalPages = computed(() => {
  return Math.ceil(filteredOrders.value.length / pageSize.value)
})

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

const switchTab = (tabValue) => {
  activeTab.value = tabValue
  currentPage.value = 1
}

const viewOrderDetail = (order) => {
  selectedOrder.value = order
  showDetailModal.value = true
}

const closeDetailModal = () => {
  showDetailModal.value = false
  selectedOrder.value = null
}

const payOrder = (order) => {
  console.log('支付订单:', order)
  alert(`即将支付订单 ${order.orderNo}，金额 ¥${order.totalAmount}`)
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

const confirmCancelOrder = () => {
  if (!cancelReason.value) {
    alert('请选择取消原因')
    return
  }
  
  if (selectedOrder.value) {
    const orderIndex = mockOrders.value.findIndex(o => o.id === selectedOrder.value.id)
    if (orderIndex !== -1) {
      mockOrders.value[orderIndex].status = 'cancelled'
      
      const pendingTab = orderTabs.value.find(tab => tab.value === 'pending')
      const cancelledTab = orderTabs.value.find(tab => tab.value === 'cancelled')
      if (pendingTab) pendingTab.count--
      if (cancelledTab) cancelledTab.count++
    }
  }
  
  closeCancelModal()
  alert('订单已取消')
}

const contactService = (order) => {
  console.log('联系服务人员:', order)
  alert('即将联系服务人员...')
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

const submitReview = () => {
  if (reviewContent.value.trim().length === 0) {
    alert('请填写评价内容')
    return
  }
  
  if (selectedOrder.value) {
    const orderIndex = mockOrders.value.findIndex(o => o.id === selectedOrder.value.id)
    if (orderIndex !== -1) {
      mockOrders.value[orderIndex].isReviewed = true
    }
  }
  
  closeReviewModal()
  alert('评价提交成功！')
}

const viewReview = (order) => {
  console.log('查看评价:', order)
  alert('该订单已评价，评价内容：服务非常专业，强烈推荐！')
}

const prevPage = () => {
  if (currentPage.value > 1) {
    currentPage.value--
  }
}

const nextPage = () => {
  if (currentPage.value < totalPages.value) {
    currentPage.value++
  }
}

const goToPage = (page) => {
  currentPage.value = page
}

onMounted(() => {
  orderTabs.value.forEach(tab => {
    if (tab.value === 'all') {
      tab.count = mockOrders.value.length
    } else {
      tab.count = mockOrders.value.filter(order => order.status === tab.value).length
    }
  })
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

.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 10px;
  margin-top: 40px;
}

.pagination-btn {
  padding: 10px 20px;
  background: white;
  border: 2px solid #e4e8eb;
  border-radius: 8px;
  font-size: 0.95rem;
  font-weight: 500;
  color: #555;
  cursor: pointer;
  transition: all 0.3s ease;
}

.pagination-btn:hover:not(:disabled) {
  border-color: #6B8E23;
  color: #6B8E23;
}

.pagination-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.pagination-numbers {
  display: flex;
  gap: 8px;
}

.pagination-number {
  width: 40px;
  height: 40px;
  display: flex;
  justify-content: center;
  align-items: center;
  background: white;
  border: 2px solid #e4e8eb;
  border-radius: 8px;
  font-size: 0.95rem;
  font-weight: 500;
  color: #555;
  cursor: pointer;
  transition: all 0.3s ease;
}

.pagination-number:hover {
  border-color: #6B8E23;
  color: #6B8E23;
}

.pagination-number.active {
  background: linear-gradient(135deg, #6B8E23, #8FBC8F);
  border-color: #6B8E23;
  color: white;
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