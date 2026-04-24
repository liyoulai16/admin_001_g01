<template>
  <div class="service-detail" v-if="service">
    <div class="page-header">
      <div class="particles">
        <div v-for="i in 30" :key="i" class="particle" :style="getParticleStyle(i)"></div>
      </div>
      <div class="container">
        <div class="breadcrumb">
          <router-link to="/">首页</router-link>
          <span class="separator">/</span>
          <router-link to="/services">服务列表</router-link>
          <span class="separator">/</span>
          <span class="current">{{ service.name }}</span>
        </div>
      </div>
    </div>

    <div class="container">
      <div class="detail-content">
        <div class="service-main">
          <div class="service-header-card">
            <div class="service-icon-large">{{ service.icon }}</div>
            <div class="service-info">
              <div class="service-category-badge">{{ service.category }}</div>
              <h1 class="service-title">{{ service.name }}</h1>
              <div class="service-meta">
                <div class="rating-info">
                  <span class="rating-stars">⭐</span>
                  <span class="rating-value">{{ service.rating }}</span>
                  <span class="rating-count">({{ service.reviews }}条评价)</span>
                </div>
                <div class="price-info">
                  <span class="price-label">价格：</span>
                  <span class="price-value">{{ service.price }}</span>
                </div>
              </div>
            </div>
          </div>

          <div class="service-section">
            <h2 class="section-title">服务介绍</h2>
            <p class="service-description">{{ service.details }}</p>
          </div>

          <div class="service-section">
            <h2 class="section-title">服务特色</h2>
            <div class="features-grid">
              <div v-for="(feature, index) in service.features" :key="index" class="feature-item">
                <span class="feature-icon">✓</span>
                <span class="feature-text">{{ feature }}</span>
              </div>
            </div>
          </div>

          <div class="service-section">
            <h2 class="section-title">用户评价</h2>
            <div class="reviews-list">
              <div v-for="(review, index) in reviews" :key="index" class="review-item">
                <div class="review-header">
                  <div class="user-avatar">{{ review.avatar }}</div>
                  <div class="user-info">
                    <div class="user-name">{{ review.name }}</div>
                    <div class="review-date">{{ review.date }}</div>
                  </div>
                  <div class="review-rating">
                    <span class="rating-stars">⭐</span>
                    <span class="rating-value">{{ review.rating }}</span>
                  </div>
                </div>
                <p class="review-content">{{ review.content }}</p>
              </div>
            </div>
          </div>
        </div>

        <aside class="sidebar">
          <div class="booking-card">
            <h3 class="booking-title">立即预约</h3>
            <div class="price-display">
              <span class="price-label">价格</span>
              <span class="price-value">{{ service.price }}</span>
            </div>
            
            <div class="coupon-section" v-if="isLoggedIn">
              <div class="coupon-selector" @click="showCouponSelector = true">
                <div class="coupon-label">
                  <span class="coupon-icon">🎫</span>
                  <span>优惠券</span>
                </div>
                <div class="coupon-value" :class="{ selected: selectedCoupon }">
                  <template v-if="selectedCoupon">
                    <span class="coupon-name">-¥{{ discountAmount }}</span>
                    <span class="coupon-arrow">›</span>
                  </template>
                  <template v-else>
                    <span v-if="hasAvailableCoupons" class="has-coupon">
                      有{{ availableCoupons.length }}张可用
                    </span>
                    <span v-else class="no-coupon">无可用优惠券</span>
                    <span class="coupon-arrow">›</span>
                  </template>
                </div>
              </div>
              
              <div class="discount-display" v-if="selectedCoupon">
                <div class="discount-row">
                  <span class="discount-label">原价</span>
                  <span class="original-price">¥{{ servicePrice }}</span>
                </div>
                <div class="discount-row">
                  <span class="discount-label">优惠券减免</span>
                  <span class="discount-price">-¥{{ discountAmount }}</span>
                </div>
                <div class="discount-row final">
                  <span class="discount-label">实付</span>
                  <span class="final-price">¥{{ finalPrice }}</span>
                </div>
              </div>
            </div>
            
            <div class="coupon-login-hint" v-else>
              <span>登录后可使用优惠券</span>
              <router-link to="/login" class="login-link">去登录</router-link>
            </div>
            
            <div class="booking-form">
              <div class="form-group">
                <label class="form-label">预约日期</label>
                <input type="date" v-model="bookingDate" :min="todayDate" class="form-input" @change="onDateChange" />
              </div>
              <div class="form-group">
                <label class="form-label">预约时间</label>
                <select v-model="bookingTime" class="form-select">
                  <option value="">请选择时间</option>
                  <option 
                    v-for="slot in availableTimeSlots" 
                    :key="slot.value" 
                    :value="slot.value"
                    :disabled="slot.disabled"
                  >
                    {{ slot.label }}
                  </option>
                </select>
              </div>
              <div class="form-group">
                <label class="form-label">联系人姓名</label>
                <input 
                  type="text" 
                  v-model="contactName" 
                  placeholder="请输入您的姓名"
                  class="form-input"
                />
              </div>
              <div class="form-group">
                <label class="form-label">联系电话</label>
                <input 
                  type="tel" 
                  v-model="phone" 
                  placeholder="请输入您的手机号"
                  class="form-input"
                />
              </div>
              <div class="form-group">
                <label class="form-label">服务地址</label>
                <input 
                  type="text" 
                  v-model="address" 
                  placeholder="请输入详细地址"
                  class="form-input"
                />
              </div>
              <div class="form-group">
                <label class="form-label">备注信息</label>
                <textarea 
                  v-model="notes" 
                  placeholder="如有特殊需求请备注"
                  class="form-textarea"
                  rows="3"
                ></textarea>
              </div>
              <button class="booking-btn" @click="handleBooking">
                立即预约
              </button>
            </div>
          </div>

          <div class="contact-card">
            <h3 class="contact-title">联系方式</h3>
            <ul class="contact-list">
              <li class="contact-item">
                <span class="contact-icon">📞</span>
                <span class="contact-text">400-123-4567</span>
              </li>
              <li class="contact-item">
                <span class="contact-icon">📧</span>
                <span class="contact-text">service@community.com</span>
              </li>
              <li class="contact-item">
                <span class="contact-icon">⏰</span>
                <span class="contact-text">服务时间：09:00 - 18:00</span>
              </li>
            </ul>
          </div>
        </aside>
      </div>
    </div>
  </div>

  <div class="not-found" v-else>
    <span class="not-found-icon">😕</span>
    <h2>服务不存在</h2>
    <p>您访问的服务可能已下架或不存在</p>
    <router-link to="/services" class="back-btn">返回服务列表</router-link>
  </div>
  
  <div class="coupon-modal-overlay" :class="{ show: showCouponSelector }" @click="closeCouponSelector">
    <div class="coupon-modal" @click.stop>
      <div class="coupon-modal-header">
        <h3 class="coupon-modal-title">选择优惠券</h3>
        <button class="coupon-modal-close" @click="closeCouponSelector">×</button>
      </div>
      <div class="coupon-modal-body">
        <div v-if="availableCoupons.length === 0" class="no-coupons-hint">
          <span class="no-coupons-icon">🎫</span>
          <p>暂无可用优惠券</p>
          <p class="hint-text">前往服务列表浏览满60分钟可获得优惠券</p>
        </div>
        <div v-else class="coupon-list">
          <div 
            v-for="coupon in availableCoupons" 
            :key="coupon.id"
            class="coupon-item"
            :class="{ 
              selected: selectedCoupon?.id === coupon.id,
              'full-reduction': coupon.type === COUPON_TYPES.FULL_REDUCTION,
              'discount': coupon.type === COUPON_TYPES.DISCOUNT
            }"
            @click="selectCoupon(coupon)"
          >
            <div class="coupon-left">
              <div class="coupon-value-display">
                <template v-if="coupon.type === COUPON_TYPES.FULL_REDUCTION">
                  <span class="currency">¥</span>
                  <span class="amount">{{ coupon.amount }}</span>
                </template>
                <template v-else>
                  <span class="discount-text">{{ Math.round(coupon.discount * 10) }}折</span>
                </template>
              </div>
              <div class="coupon-type-label">
                {{ coupon.type === COUPON_TYPES.FULL_REDUCTION ? '满减券' : '折扣券' }}
              </div>
            </div>
            <div class="coupon-right">
              <div class="coupon-info">
                <h4 class="coupon-name">{{ coupon.name }}</h4>
                <p class="coupon-condition">
                  {{ coupon.condition.minAmount > 0 ? `满${coupon.condition.minAmount}元可用` : '无门槛' }}
                  <template v-if="coupon.type === COUPON_TYPES.DISCOUNT && coupon.maxDiscount">
                    （最高减¥{{ coupon.maxDiscount }}）
                  </template>
                </p>
                <p class="coupon-validity">
                  有效期至 {{ formatCouponDate(coupon.validUntil) }}
                </p>
              </div>
              <div class="coupon-selected-mark" v-if="selectedCoupon?.id === coupon.id">
                ✓ 已选择
              </div>
            </div>
          </div>
        </div>
      </div>
      <div class="coupon-modal-footer">
        <button class="btn-cancel" @click="closeCouponSelector">取消</button>
        <button class="btn-confirm" @click="confirmCoupon">确认使用</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import request from '../utils/request'
import {
  getAvailableCouponsForService,
  calculateDiscount,
  COUPON_TYPES,
  useCoupon
} from '../data/coupons'

const route = useRoute()
const router = useRouter()

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

const service = ref(null)
const loading = ref(false)
const bookingDate = ref('')
const bookingTime = ref('')
const contactName = ref('')
const phone = ref('')
const address = ref('')
const notes = ref('')

const selectedCoupon = ref(null)
const showCouponSelector = ref(false)
const isLoggedIn = ref(false)

const todayDate = computed(() => {
  const today = new Date()
  return today.toISOString().split('T')[0]
})

const servicePrice = computed(() => {
  if (!service.value) return 0
  const priceStr = String(service.value.price).replace(/[^0-9.]/g, '')
  return parseFloat(priceStr) || 0
})

const availableCoupons = computed(() => {
  if (!service.value) return []
  return getAvailableCouponsForService(service.value.category, servicePrice.value)
})

const discountAmount = computed(() => {
  if (!selectedCoupon.value || servicePrice.value === 0) return 0
  return calculateDiscount(selectedCoupon.value, servicePrice.value)
})

const finalPrice = computed(() => {
  return Math.max(0, servicePrice.value - discountAmount.value)
})

const hasAvailableCoupons = computed(() => {
  return availableCoupons.value.length > 0 && isLoggedIn.value
})

const timeSlots = [
  { value: '09:00-10:00', label: '09:00 - 10:00', startTime: '09:00' },
  { value: '10:00-11:00', label: '10:00 - 11:00', startTime: '10:00' },
  { value: '11:00-12:00', label: '11:00 - 12:00', startTime: '11:00' },
  { value: '14:00-15:00', label: '14:00 - 15:00', startTime: '14:00' },
  { value: '15:00-16:00', label: '15:00 - 16:00', startTime: '15:00' },
  { value: '16:00-17:00', label: '16:00 - 17:00', startTime: '16:00' },
  { value: '17:00-18:00', label: '17:00 - 18:00', startTime: '17:00' }
]

const availableTimeSlots = computed(() => {
  const now = new Date()
  const currentDate = now.toISOString().split('T')[0]
  const currentTime = now.getHours().toString().padStart(2, '0') + ':' + 
                      now.getMinutes().toString().padStart(2, '0')
  
  return timeSlots.map(slot => {
    if (bookingDate.value === currentDate && slot.startTime < currentTime) {
      return { ...slot, disabled: true }
    }
    return { ...slot, disabled: false }
  })
})

const onDateChange = () => {
  bookingTime.value = ''
}

const reviews = computed(() => [
  {
    name: '张先生',
    avatar: '👨',
    date: '2024-01-15',
    rating: 5,
    content: '服务非常专业，保洁阿姨很认真负责，把家里打扫得干干净净。下次还会继续预约！'
  },
  {
    name: '李女士',
    avatar: '👩',
    date: '2024-01-10',
    rating: 4,
    content: '整体服务不错，就是预约时间稍微有点延迟，不过服务质量很好，值得推荐。'
  },
  {
    name: '王先生',
    avatar: '👨‍💼',
    date: '2024-01-05',
    rating: 5,
    content: '已经是第三次预约了，每次都很满意。服务人员态度好，工作细致，强烈推荐！'
  }
])

const fetchServiceDetail = async () => {
  loading.value = true
  try {
    const serviceId = route.params.id
    const response = await request(`/api/services/${serviceId}`, {
      method: 'GET'
    })

    if (response.ok) {
      const data = await response.json()
      if (data.code === 200 && data.data) {
        service.value = data.data
      }
    }
  } catch (error) {
    console.error('获取服务详情失败:', error)
  } finally {
    loading.value = false
  }
}

const formatCouponDate = (dateString) => {
  const date = new Date(dateString)
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`
}

const selectCoupon = (coupon) => {
  if (selectedCoupon.value?.id === coupon.id) {
    selectedCoupon.value = null
  } else {
    selectedCoupon.value = coupon
  }
}

const closeCouponSelector = () => {
  showCouponSelector.value = false
}

const confirmCoupon = () => {
  closeCouponSelector()
}

const handleBooking = async () => {
  if (!bookingDate.value) {
    alert('请选择预约日期')
    return
  }
  if (!bookingTime.value) {
    alert('请选择预约时间')
    return
  }
  if (!contactName.value) {
    alert('请输入联系人姓名')
    return
  }
  if (!phone.value) {
    alert('请输入联系电话')
    return
  }
  if (!address.value) {
    alert('请输入服务地址')
    return
  }
  
  const token = localStorage.getItem('token')
  if (!token) {
    alert('请先登录')
    router.push('/login')
    return
  }
  
  try {
    const [startTime, endTime] = bookingTime.value.split('-')
    const orderData = {
      serviceId: service.value.id,
      quantity: 1,
      appointmentDate: bookingDate.value,
      appointmentStartTime: startTime,
      appointmentEndTime: endTime,
      appointmentTime: `${bookingDate.value} ${bookingTime.value}`,
      serviceAddress: address.value,
      contactName: contactName.value,
      contactPhone: phone.value,
      remark: notes.value,
      payMethod: 0,
      couponId: selectedCoupon.value?.id || null,
      couponDiscount: discountAmount.value || 0,
      originalPrice: servicePrice.value,
      finalPrice: finalPrice.value
    }
    
    console.log('创建订单数据:', orderData)
    
    const response = await request('/api/orders/create', {
      method: 'POST',
      body: JSON.stringify(orderData)
    })
    
    const data = await response.json()
    console.log('订单创建响应:', data)
    
    if (data.code === 200 && data.data) {
      if (selectedCoupon.value) {
        useCoupon(selectedCoupon.value.id)
      }
      
      const discountInfo = selectedCoupon.value 
        ? `\n优惠券减免：-¥${discountAmount.value}\n实付金额：¥${finalPrice.value}`
        : ''
      
      alert(`订单创建成功！\n订单编号：${data.data.orderNo}\n服务：${service.value.name}\n预约时间：${bookingDate.value} ${bookingTime.value}${discountInfo}\n\n请前往订单页面完成支付。`)
      router.push('/orders')
    } else {
      alert(data.message || '订单创建失败，请重试')
    }
  } catch (error) {
    console.error('创建订单失败:', error)
    alert('网络错误，请稍后重试')
  }
}

onMounted(() => {
  fetchServiceDetail()
  
  isLoggedIn.value = localStorage.getItem('isLoggedIn') === 'true' && localStorage.getItem('token')
  
  const today = new Date()
  bookingDate.value = today.toISOString().split('T')[0]
})
</script>

<style scoped>
.service-detail {
  min-height: calc(100vh - 200px);
}

.page-header {
  background: #f5f7fa;
  padding: 20px 0;
  border-bottom: 1px solid #e4e8eb;
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
  background: rgba(107, 142, 35, 0.15);
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
    transform: translateY(-200px) rotate(720deg);
    opacity: 0;
  }
}

.breadcrumb {
  font-size: 0.9rem;
  color: #7f8c8d;
}

.breadcrumb a {
  color: #6B8E23;
  transition: color 0.3s ease;
}

.breadcrumb a:hover {
  color: #6B8E23;
}

.separator {
  margin: 0 8px;
}

.current {
  color: #2c3e50;
}

.detail-content {
  display: flex;
  gap: 30px;
  padding: 40px 0;
}

.service-main {
  flex: 1;
}

.service-header-card {
  background: white;
  border-radius: 15px;
  padding: 30px;
  display: flex;
  gap: 30px;
  margin-bottom: 30px;
  box-shadow: 0 5px 20px rgba(0, 0, 0, 0.08);
}

.service-icon-large {
  font-size: 5rem;
  width: 120px;
  height: 120px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #f5f7fa, #e4e8eb);
  border-radius: 15px;
}

.service-info {
  flex: 1;
}

.service-category-badge {
  display: inline-block;
  background: linear-gradient(135deg, #6B8E23, #8FBC8F);
  color: white;
  padding: 5px 15px;
  border-radius: 20px;
  font-size: 0.85rem;
  margin-bottom: 15px;
}

.service-title {
  font-size: 2rem;
  margin-bottom: 15px;
  color: #2c3e50;
}

.service-meta {
  display: flex;
  gap: 30px;
  align-items: center;
}

.rating-info {
  display: flex;
  align-items: center;
  gap: 5px;
}

.rating-stars {
  color: #f39c12;
  font-size: 1.1rem;
}

.rating-value {
  font-weight: bold;
  color: #2c3e50;
  font-size: 1.1rem;
}

.rating-count {
  color: #7f8c8d;
}

.price-info {
  display: flex;
  align-items: center;
  gap: 5px;
}

.price-label {
  color: #7f8c8d;
}

.price-value {
  font-size: 1.3rem;
  font-weight: bold;
  color: #C45B1A;
}

.service-section {
  background: white;
  border-radius: 15px;
  padding: 30px;
  margin-bottom: 30px;
  box-shadow: 0 5px 20px rgba(0, 0, 0, 0.08);
}

.section-title {
  font-size: 1.3rem;
  margin-bottom: 20px;
  color: #2c3e50;
  padding-bottom: 10px;
  border-bottom: 2px solid #e4e8eb;
}

.service-description {
  font-size: 1rem;
  line-height: 1.8;
  color: #555;
}

.features-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 15px;
}

.feature-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 15px;
  background: #f5f7fa;
  border-radius: 10px;
}

.feature-icon {
  width: 24px;
  height: 24px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #6B8E23, #8FBC8F);
  color: white;
  border-radius: 50%;
  font-size: 0.8rem;
  font-weight: bold;
}

.feature-text {
  color: #2c3e50;
  font-weight: 500;
}

.reviews-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.review-item {
  padding: 20px;
  background: #f5f7fa;
  border-radius: 10px;
}

.review-header {
  display: flex;
  align-items: center;
  gap: 15px;
  margin-bottom: 15px;
}

.user-avatar {
  font-size: 2rem;
  width: 50px;
  height: 50px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: white;
  border-radius: 50%;
}

.user-info {
  flex: 1;
}

.user-name {
  font-weight: bold;
  color: #2c3e50;
  margin-bottom: 3px;
}

.review-date {
  font-size: 0.85rem;
  color: #7f8c8d;
}

.review-rating {
  display: flex;
  align-items: center;
  gap: 5px;
}

.review-content {
  color: #555;
  line-height: 1.6;
}

.sidebar {
  width: 350px;
  flex-shrink: 0;
}

.booking-card,
.contact-card {
  background: white;
  border-radius: 15px;
  padding: 25px;
  margin-bottom: 20px;
  box-shadow: 0 5px 20px rgba(0, 0, 0, 0.08);
}

.booking-title,
.contact-title {
  font-size: 1.2rem;
  margin-bottom: 20px;
  color: #2c3e50;
  text-align: center;
  padding-bottom: 15px;
  border-bottom: 2px solid #e4e8eb;
}

.price-display {
  text-align: center;
  margin-bottom: 20px;
  padding: 15px;
  background: linear-gradient(135deg, #f5f7fa, #e4e8eb);
  border-radius: 10px;
}

.price-display .price-label {
  display: block;
  font-size: 0.9rem;
  color: #7f8c8d;
  margin-bottom: 5px;
}

.price-display .price-value {
  font-size: 1.8rem;
  font-weight: bold;
  color: #C45B1A;
}

.form-group {
  margin-bottom: 15px;
}

.form-label {
  display: block;
  font-size: 0.9rem;
  color: #555;
  margin-bottom: 8px;
}

.form-input,
.form-select,
.form-textarea {
  width: 100%;
  padding: 12px 15px;
  border: 2px solid #e4e8eb;
  border-radius: 8px;
  font-size: 1rem;
  transition: border-color 0.3s ease;
  box-sizing: border-box;
}

.form-input:focus,
.form-select:focus,
.form-textarea:focus {
  outline: none;
  border-color: #6B8E23;
}

.form-textarea {
  resize: vertical;
}

.booking-btn {
  width: 100%;
  padding: 15px;
  background: linear-gradient(135deg, #6B8E23, #8FBC8F);
  color: white;
  font-size: 1.1rem;
  font-weight: bold;
  border-radius: 8px;
  transition: all 0.3s ease;
  margin-top: 10px;
}

.booking-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 10px 20px rgba(107, 142, 35, 0.3);
}

.coupon-section {
  margin-bottom: 15px;
}

.coupon-selector {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 15px;
  background: #f8f9fa;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s ease;
  border: 2px solid transparent;
}

.coupon-selector:hover {
  background: #e9ecef;
  border-color: #6B8E23;
}

.coupon-label {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 0.95rem;
  color: #555;
}

.coupon-icon {
  font-size: 1.2rem;
}

.coupon-value {
  display: flex;
  align-items: center;
  gap: 8px;
}

.coupon-value.selected .coupon-name {
  color: #E74C3C;
  font-weight: bold;
}

.has-coupon {
  color: #E74C3C;
  font-weight: 500;
}

.no-coupon {
  color: #999;
}

.coupon-arrow {
  color: #999;
  font-size: 1.2rem;
}

.discount-display {
  margin-top: 12px;
  padding: 12px;
  background: linear-gradient(135deg, #FFF8E7, #FFF3E0);
  border-radius: 8px;
  border: 1px solid #FFE0B2;
}

.discount-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 6px 0;
  font-size: 0.9rem;
}

.discount-row.final {
  margin-top: 8px;
  padding-top: 10px;
  border-top: 1px dashed #FFE0B2;
}

.discount-label {
  color: #666;
}

.original-price {
  color: #999;
  text-decoration: line-through;
}

.discount-price {
  color: #E74C3C;
  font-weight: 500;
}

.discount-row.final .discount-label {
  font-size: 1rem;
  font-weight: 600;
  color: #333;
}

.final-price {
  font-size: 1.3rem;
  font-weight: bold;
  color: #E74C3C;
}

.coupon-login-hint {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px 15px;
  background: #f8f9fa;
  border-radius: 8px;
  margin-bottom: 15px;
  font-size: 0.9rem;
  color: #666;
}

.login-link {
  color: #6B8E23;
  font-weight: 500;
  text-decoration: none;
}

.login-link:hover {
  text-decoration: underline;
}

.coupon-modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 2000;
  opacity: 0;
  visibility: hidden;
  transition: all 0.3s ease;
}

.coupon-modal-overlay.show {
  opacity: 1;
  visibility: visible;
}

.coupon-modal {
  width: 90%;
  max-width: 500px;
  max-height: 80vh;
  background: white;
  border-radius: 16px;
  overflow: hidden;
  display: flex;
  flex-direction: column;
  transform: translateY(20px);
  transition: transform 0.3s ease;
}

.coupon-modal-overlay.show .coupon-modal {
  transform: translateY(0);
}

.coupon-modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 24px;
  border-bottom: 1px solid #e4e8eb;
}

.coupon-modal-title {
  font-size: 1.2rem;
  font-weight: 600;
  color: #2c3e50;
  margin: 0;
}

.coupon-modal-close {
  width: 32px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f5f7fa;
  border: none;
  border-radius: 50%;
  font-size: 1.5rem;
  color: #999;
  cursor: pointer;
  transition: all 0.3s ease;
}

.coupon-modal-close:hover {
  background: #e4e8eb;
  color: #666;
}

.coupon-modal-body {
  flex: 1;
  overflow-y: auto;
  padding: 16px;
}

.no-coupons-hint {
  text-align: center;
  padding: 40px 20px;
}

.no-coupons-icon {
  font-size: 3rem;
  display: block;
  margin-bottom: 15px;
}

.no-coupons-hint p {
  color: #666;
  margin: 0 0 8px 0;
}

.no-coupons-hint .hint-text {
  color: #999;
  font-size: 0.9rem;
}

.coupon-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.coupon-item {
  display: flex;
  border-radius: 12px;
  overflow: hidden;
  cursor: pointer;
  transition: all 0.3s ease;
  border: 2px solid transparent;
}

.coupon-item:hover {
  transform: translateY(-2px);
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.1);
}

.coupon-item.selected {
  border-color: #6B8E23;
  box-shadow: 0 0 0 3px rgba(107, 142, 35, 0.15);
}

.coupon-item.full-reduction {
  background: linear-gradient(135deg, #FF6B6B, #EE5A5A);
}

.coupon-item.discount {
  background: linear-gradient(135deg, #4ECDC4, #44B7AF);
}

.coupon-left {
  width: 100px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: white;
  padding: 15px 10px;
  position: relative;
}

.coupon-left::after {
  content: '';
  position: absolute;
  right: 0;
  top: 50%;
  transform: translateY(-50%);
  width: 1px;
  height: 60%;
  background: rgba(255, 255, 255, 0.3);
}

.coupon-value-display {
  display: flex;
  align-items: baseline;
  justify-content: center;
}

.coupon-value-display .currency {
  font-size: 1rem;
  font-weight: 500;
}

.coupon-value-display .amount {
  font-size: 2rem;
  font-weight: bold;
}

.coupon-value-display .discount-text {
  font-size: 1.8rem;
  font-weight: bold;
}

.coupon-type-label {
  font-size: 0.8rem;
  opacity: 0.9;
  margin-top: 5px;
}

.coupon-right {
  flex: 1;
  background: white;
  padding: 15px;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  position: relative;
}

.coupon-info {
  flex: 1;
}

.coupon-name {
  font-size: 1rem;
  font-weight: 600;
  color: #2c3e50;
  margin: 0 0 8px 0;
}

.coupon-condition {
  font-size: 0.85rem;
  color: #666;
  margin: 0 0 6px 0;
}

.coupon-validity {
  font-size: 0.8rem;
  color: #999;
  margin: 0;
}

.coupon-selected-mark {
  position: absolute;
  top: 10px;
  right: 12px;
  background: linear-gradient(135deg, #6B8E23, #8FBC8F);
  color: white;
  font-size: 0.8rem;
  padding: 4px 10px;
  border-radius: 12px;
  font-weight: 500;
}

.coupon-modal-footer {
  display: flex;
  gap: 12px;
  padding: 16px 24px;
  border-top: 1px solid #e4e8eb;
}

.btn-cancel,
.btn-confirm {
  flex: 1;
  padding: 12px 20px;
  border-radius: 8px;
  font-size: 1rem;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;
  border: none;
}

.btn-cancel {
  background: #f5f7fa;
  color: #666;
}

.btn-cancel:hover {
  background: #e4e8eb;
}

.btn-confirm {
  background: linear-gradient(135deg, #6B8E23, #8FBC8F);
  color: white;
}

.btn-confirm:hover {
  transform: translateY(-1px);
  box-shadow: 0 5px 15px rgba(107, 142, 35, 0.3);
}

.contact-list {
  list-style: none;
  padding: 0;
}

.contact-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 0;
  border-bottom: 1px solid #e4e8eb;
}

.contact-item:last-child {
  border-bottom: none;
}

.contact-icon {
  font-size: 1.3rem;
}

.contact-text {
  color: #555;
}

.not-found {
  text-align: center;
  padding: 100px 20px;
}

.not-found-icon {
  font-size: 5rem;
  margin-bottom: 20px;
  display: block;
}

.not-found h2 {
  font-size: 1.5rem;
  margin-bottom: 10px;
  color: #2c3e50;
}

.not-found p {
  color: #7f8c8d;
  margin-bottom: 30px;
}

.back-btn {
  display: inline-block;
  padding: 12px 30px;
  background: linear-gradient(135deg, #6B8E23, #8FBC8F);
  color: white;
  border-radius: 25px;
  font-weight: bold;
  transition: all 0.3s ease;
}

.back-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 10px 20px rgba(107, 142, 35, 0.3);
}

@media (max-width: 992px) {
  .detail-content {
    flex-direction: column;
  }

  .sidebar {
    width: 100%;
  }
}

@media (max-width: 768px) {
  .service-header-card {
    flex-direction: column;
    text-align: center;
  }

  .service-icon-large {
    margin: 0 auto;
  }

  .service-meta {
    flex-direction: column;
    gap: 10px;
  }

  .features-grid {
    grid-template-columns: 1fr;
  }

  .review-header {
    flex-wrap: wrap;
  }

  .review-rating {
    width: 100%;
    justify-content: flex-end;
    margin-top: 10px;
  }
}
</style>
