<template>
  <div class="coupons-page">
    <div class="page-header">
      <div class="particles">
        <div v-for="i in 30" :key="i" class="particle" :style="getParticleStyle(i)"></div>
      </div>
      <div class="container">
        <h1 class="page-title">我的优惠券</h1>
        <p class="page-subtitle">查看和管理您的专属优惠券</p>
        <div class="browse-reward-info" v-if="!canClaimReward && !isRewardClaimed">
          <div class="reward-progress">
            <div class="progress-bar">
              <div class="progress-fill" :style="{ width: progressPercent + '%' }"></div>
            </div>
            <div class="progress-info">
              <span class="progress-text">浏览服务列表满1分钟可获得优惠券</span>
              <span class="progress-time">{{ browsingTimeFormatted }} / 1分钟</span>
            </div>
          </div>
        </div>
        <div class="reward-available" v-else-if="canClaimReward && !isRewardClaimed">
          <div class="reward-alert">
            <span class="reward-icon">🎉</span>
            <span class="reward-text">恭喜！您已获得浏览奖励优惠券</span>
            <button class="claim-btn" @click="claimReward">立即领取</button>
          </div>
        </div>
      </div>
    </div>

    <div class="container">
      <div class="coupon-tabs">
        <button 
          v-for="tab in couponTabs" 
          :key="tab.value"
          class="tab-btn"
          :class="{ active: activeTab === tab.value }"
          @click="switchTab(tab.value)"
        >
          {{ tab.label }}
          <span v-if="tab.count > 0" class="tab-count">{{ tab.count }}</span>
        </button>
      </div>

      <div class="coupons-grid" v-if="filteredCoupons.length > 0">
        <div 
          v-for="coupon in filteredCoupons" 
          :key="coupon.id" 
          class="coupon-card"
          :class="getCouponCardClass(coupon)"
        >
          <div class="coupon-left">
            <div class="coupon-value">
              <span class="currency" v-if="coupon.type === 'full_reduction'">¥</span>
              <span class="value">
                {{ getCouponDisplayValue(coupon) }}
              </span>
            </div>
            <div class="coupon-condition">
              {{ getCouponCondition(coupon) }}
            </div>
          </div>
          <div class="coupon-middle">
            <div class="circle top-circle"></div>
            <div class="coupon-divider"></div>
            <div class="circle bottom-circle"></div>
          </div>
          <div class="coupon-right">
            <div class="coupon-info">
              <div class="coupon-type-badge" :class="coupon.type === 'discount' ? 'discount-badge' : 'reduction-badge'">
                {{ getCouponTypeText(coupon.type) }}
              </div>
              <h3 class="coupon-name">{{ coupon.name }}</h3>
              <p class="coupon-desc">{{ coupon.description }}</p>
              <div class="coupon-validity">
                <span class="validity-icon">📅</span>
                <span class="validity-text">{{ coupon.validFrom }} 至 {{ coupon.validTo }}</span>
              </div>
              <div class="coupon-category" v-if="coupon.category">
                <span class="category-icon">📌</span>
                <span class="category-text">仅限 {{ coupon.category }} 使用</span>
              </div>
            </div>
            <div class="coupon-action">
              <template v-if="coupon.status === 'available'">
                <button class="action-btn use-btn" @click="useCoupon(coupon)">
                  立即使用
                </button>
              </template>
              <template v-else-if="coupon.status === 'used'">
                <div class="used-badge">
                  <span class="used-icon">✓</span>
                  <span class="used-text">已使用</span>
                </div>
                <div class="used-time">{{ coupon.useTime }}</div>
              </template>
              <template v-else>
                <div class="expired-badge">
                  <span class="expired-icon">✗</span>
                  <span class="expired-text">已过期</span>
                </div>
              </template>
            </div>
          </div>
          <div class="coupon-overlay" v-if="coupon.status !== 'available'"></div>
        </div>
      </div>

      <div class="empty-state" v-else>
        <div class="empty-icon">🎫</div>
        <h3 class="empty-title">暂无优惠券</h3>
        <p class="empty-text">{{ getEmptyMessage() }}</p>
        <router-link to="/services" class="empty-action-btn">
          去浏览服务
        </router-link>
      </div>
    </div>

    <div class="modal-overlay" :class="{ show: showUseModal }" @click="closeUseModal">
      <div class="modal-container" @click.stop>
        <div class="modal-header">
          <h3 class="modal-title">使用优惠券</h3>
          <button class="modal-close-btn" @click="closeUseModal">×</button>
        </div>
        <div class="modal-body">
          <div class="coupon-preview" v-if="selectedCoupon">
            <div class="preview-left">
              <div class="preview-value">
                <span class="currency" v-if="selectedCoupon.type === 'full_reduction'">¥</span>
                <span class="value">{{ getCouponDisplayValue(selectedCoupon) }}</span>
              </div>
              <div class="preview-condition">{{ getCouponCondition(selectedCoupon) }}</div>
            </div>
            <div class="preview-right">
              <h4 class="preview-name">{{ selectedCoupon.name }}</h4>
              <p class="preview-desc">{{ selectedCoupon.description }}</p>
              <div class="preview-validity">
                <span>有效期：{{ selectedCoupon.validFrom }} 至 {{ selectedCoupon.validTo }}</span>
              </div>
            </div>
          </div>
          <div class="use-hint">
            <p class="hint-text">优惠券将在服务订单支付时自动抵扣相应金额</p>
            <p class="hint-subtext">请选择服务进行预约，在支付环节选择使用此优惠券</p>
          </div>
        </div>
        <div class="modal-footer">
          <button class="modal-btn secondary-btn" @click="closeUseModal">取消</button>
          <button class="modal-btn primary-btn" @click="goToServices">去浏览服务</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted, watch } from 'vue'
import { useRouter, onBeforeRouteLeave } from 'vue-router'
import {
  COUPON_TYPES,
  COUPON_STATUS,
  getUserCoupons,
  getUserCouponsByStatus,
  fetchUserCoupons,
  fetchBrowsingStatus,
  updateBrowsingTime,
  claimBrowseReward,
  formatTime,
  getCouponTypeText,
  getCouponDisplayValue,
  getCouponCondition
} from '../data/coupons'

const router = useRouter()

const activeTab = ref('available')
const coupons = ref([])
const selectedCoupon = ref(null)
const showUseModal = ref(false)
const browsingTime = ref(0)
const isRewardClaimed = ref(false)
const requiredSeconds = ref(60)
let browsingTimer = null

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

const couponTabs = computed(() => [
  { label: '可使用', value: 'available', count: getUserCouponsByStatus(COUPON_STATUS.AVAILABLE).length },
  { label: '已使用', value: 'used', count: getUserCouponsByStatus(COUPON_STATUS.USED).length },
  { label: '已过期', value: 'expired', count: getUserCouponsByStatus(COUPON_STATUS.EXPIRED).length },
  { label: '全部', value: 'all', count: getUserCoupons().length }
])

const filteredCoupons = computed(() => {
  if (activeTab.value === 'all') {
    return coupons.value
  }
  return coupons.value.filter(c => c.status === activeTab.value)
})

const progressPercent = computed(() => {
  const percent = (browsingTime.value / requiredSeconds.value) * 100
  return Math.min(percent, 100)
})

const browsingTimeFormatted = computed(() => {
  return formatTime(browsingTime.value)
})

const canClaimReward = computed(() => {
  return browsingTime.value >= requiredSeconds.value && !isRewardClaimed.value
})

const loadCoupons = async () => {
  const result = await fetchUserCoupons(activeTab.value, 1, 50)
  if (result.records && result.records.length > 0) {
    coupons.value = result.records
  } else {
    coupons.value = getUserCoupons()
  }
}

const switchTab = (tabValue) => {
  activeTab.value = tabValue
}

const getCouponCardClass = (coupon) => {
  if (coupon.status === COUPON_STATUS.USED) return 'used'
  if (coupon.status === COUPON_STATUS.EXPIRED) return 'expired'
  if (coupon.type === COUPON_TYPES.DISCOUNT) return 'discount'
  return ''
}

const getEmptyMessage = () => {
  if (activeTab.value === 'available') {
    return '您当前没有可使用的优惠券，快去浏览服务获取吧！'
  }
  if (activeTab.value === 'used') {
    return '您还没有使用过优惠券'
  }
  if (activeTab.value === 'expired') {
    return '您没有已过期的优惠券'
  }
  return '您还没有任何优惠券'
}

const useCoupon = (coupon) => {
  if (coupon.status !== COUPON_STATUS.AVAILABLE) return
  selectedCoupon.value = coupon
  showUseModal.value = true
}

const closeUseModal = () => {
  showUseModal.value = false
  selectedCoupon.value = null
}

const goToServices = () => {
  closeUseModal()
  router.push('/services')
}

const claimReward = async () => {
  if (isRewardClaimed.value || browsingTime.value < requiredSeconds.value) return
  
  const result = await claimBrowseReward()
  
  if (result.code === 200) {
    isRewardClaimed.value = true
    loadCoupons()
    const coupon = result.data?.coupon
    if (coupon) {
      alert(`🎉 恭喜！您已成功领取浏览奖励优惠券！\n\n获得：${coupon.name}`)
    } else {
      alert('🎉 恭喜！您已成功领取浏览奖励优惠券！')
    }
  } else {
    alert(`领取失败：${result.message || '未知错误'}`)
  }
}

const loadBrowsingStatus = async () => {
  const status = await fetchBrowsingStatus()
  browsingTime.value = status.totalSeconds || 0
  requiredSeconds.value = status.requiredSeconds || 60
  isRewardClaimed.value = status.rewardClaimed || false
}

const startBrowsingTimer = () => {
  if (browsingTimer) return
  
  browsingTimer = setInterval(async () => {
    if (browsingTime.value < requiredSeconds.value) {
      browsingTime.value++
      await updateBrowsingTime(1)
    }
  }, 1000)
}

const stopBrowsingTimer = () => {
  if (browsingTimer) {
    clearInterval(browsingTimer)
    browsingTimer = null
  }
}

onMounted(async () => {
  await loadCoupons()
  await loadBrowsingStatus()
  startBrowsingTimer()
})

onUnmounted(() => {
  stopBrowsingTimer()
})

onBeforeRouteLeave(() => {
  stopBrowsingTimer()
})

watch(
  () => activeTab.value,
  () => {
    loadCoupons()
  }
)
</script>

<style scoped>
.coupons-page {
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
  font-weight: bold;
}

.page-subtitle {
  font-size: 1.1rem;
  opacity: 0.9;
  margin-bottom: 20px;
}

.browse-reward-info {
  max-width: 500px;
  margin: 20px auto 0;
  background: rgba(255, 255, 255, 0.15);
  padding: 20px;
  border-radius: 12px;
  backdrop-filter: blur(10px);
}

.reward-progress {
  text-align: left;
}

.progress-bar {
  height: 8px;
  background: rgba(255, 255, 255, 0.3);
  border-radius: 4px;
  overflow: hidden;
  margin-bottom: 10px;
}

.progress-fill {
  height: 100%;
  background: linear-gradient(90deg, #FFD700, #FFA500);
  border-radius: 4px;
  transition: width 0.3s ease;
}

.progress-info {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 0.9rem;
  opacity: 0.9;
}

.progress-text {
  color: rgba(255, 255, 255, 0.9);
}

.progress-time {
  font-weight: bold;
  color: #FFD700;
}

.reward-available {
  max-width: 500px;
  margin: 20px auto 0;
}

.reward-alert {
  display: flex;
  align-items: center;
  gap: 15px;
  background: linear-gradient(135deg, #FFD700, #FFA500);
  padding: 15px 20px;
  border-radius: 12px;
  color: #5D4E37;
  font-weight: 500;
}

.reward-icon {
  font-size: 1.5rem;
}

.reward-text {
  flex: 1;
}

.claim-btn {
  padding: 8px 20px;
  background: white;
  color: #5D4E37;
  border: none;
  border-radius: 20px;
  font-weight: bold;
  cursor: pointer;
  transition: all 0.3s ease;
}

.claim-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.2);
}

.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 24px;
}

.coupon-tabs {
  display: flex;
  justify-content: center;
  gap: 15px;
  margin: 40px 0;
  flex-wrap: wrap;
}

.tab-btn {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 28px;
  background: white;
  border: 2px solid #e4e8eb;
  border-radius: 25px;
  font-size: 1rem;
  font-weight: 500;
  color: #555;
  cursor: pointer;
  transition: all 0.3s ease;
}

.tab-btn:hover {
  border-color: #6B8E23;
  color: #6B8E23;
  transform: translateY(-2px);
}

.tab-btn.active {
  background: linear-gradient(135deg, #6B8E23, #8FBC8F);
  border-color: #6B8E23;
  color: white;
  box-shadow: 0 5px 15px rgba(107, 142, 35, 0.3);
}

.tab-count {
  background: rgba(255, 255, 255, 0.2);
  padding: 2px 10px;
  border-radius: 10px;
  font-size: 0.85rem;
  font-weight: 600;
}

.tab-btn:not(.active) .tab-count {
  background: #e4e8eb;
  color: #7f8c8d;
}

.coupons-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(400px, 1fr));
  gap: 30px;
  padding: 20px 0 40px;
}

.coupon-card {
  display: flex;
  background: white;
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 5px 20px rgba(0, 0, 0, 0.08);
  position: relative;
  transition: all 0.3s ease;
  border: 2px solid transparent;
}

.coupon-card:hover:not(.used):not(.expired) {
  transform: translateY(-5px);
  box-shadow: 0 15px 30px rgba(0, 0, 0, 0.15);
  border-color: #6B8E23;
}

.coupon-card.discount {
  background: linear-gradient(135deg, #FFF5F5, #FFFFFF);
}

.coupon-card.used,
.coupon-card.expired {
  opacity: 0.7;
}

.coupon-left {
  width: 140px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  padding: 20px;
  background: linear-gradient(135deg, #6B8E23, #8FBC8F);
  color: white;
  position: relative;
}

.coupon-card.discount .coupon-left {
  background: linear-gradient(135deg, #E74C3C, #C0392B);
}

.coupon-card.used .coupon-left,
.coupon-card.expired .coupon-left {
  background: linear-gradient(135deg, #95A5A6, #7F8C8D);
}

.coupon-value {
  display: flex;
  align-items: baseline;
  justify-content: center;
  margin-bottom: 8px;
}

.currency {
  font-size: 1.5rem;
  font-weight: bold;
}

.value {
  font-size: 2.5rem;
  font-weight: bold;
  line-height: 1;
}

.coupon-card.discount .value {
  font-size: 2rem;
}

.coupon-card.discount .value::after {
  content: '折';
  font-size: 1rem;
}

.coupon-condition {
  font-size: 0.85rem;
  opacity: 0.9;
  text-align: center;
}

.coupon-middle {
  width: 30px;
  position: relative;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
}

.circle {
  width: 20px;
  height: 20px;
  border-radius: 50%;
  background: #f8f9fa;
  position: absolute;
  left: -10px;
  z-index: 1;
}

.top-circle {
  top: -10px;
}

.bottom-circle {
  bottom: -10px;
}

.coupon-divider {
  width: 2px;
  height: calc(100% - 40px);
  border-left: 2px dashed #e4e8eb;
}

.coupon-right {
  flex: 1;
  padding: 20px;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}

.coupon-info {
  flex: 1;
}

.coupon-type-badge {
  display: inline-block;
  padding: 4px 12px;
  border-radius: 4px;
  font-size: 0.8rem;
  font-weight: 600;
  margin-bottom: 8px;
}

.reduction-badge {
  background: rgba(107, 142, 35, 0.1);
  color: #6B8E23;
}

.discount-badge {
  background: rgba(231, 76, 60, 0.1);
  color: #E74C3C;
}

.coupon-name {
  font-size: 1.1rem;
  font-weight: bold;
  color: #2c3e50;
  margin: 0 0 6px;
}

.coupon-desc {
  font-size: 0.9rem;
  color: #7f8c8d;
  margin: 0 0 12px;
}

.coupon-validity,
.coupon-category {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 0.85rem;
  color: #7f8c8d;
  margin-bottom: 4px;
}

.validity-icon,
.category-icon {
  font-size: 0.9rem;
}

.coupon-action {
  margin-top: 15px;
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  gap: 5px;
}

.action-btn {
  padding: 8px 24px;
  border: none;
  border-radius: 20px;
  font-size: 0.9rem;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
}

.use-btn {
  background: linear-gradient(135deg, #6B8E23, #8FBC8F);
  color: white;
}

.use-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 5px 15px rgba(107, 142, 35, 0.3);
}

.used-badge,
.expired-badge {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 0.95rem;
  font-weight: 600;
}

.used-badge {
  color: #27AE60;
}

.expired-badge {
  color: #95A5A6;
}

.used-time {
  font-size: 0.8rem;
  color: #7f8c8d;
}

.coupon-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(255, 255, 255, 0.5);
  pointer-events: none;
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
  box-shadow: 0 10px 20px rgba(107, 142, 35, 0.4);
}

.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
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
  max-width: 500px;
  max-height: 85vh;
  overflow-y: auto;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
  transform: scale(0.9) translateY(20px);
  transition: all 0.3s ease;
}

.modal-overlay.show .modal-container {
  transform: scale(1) translateY(0);
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

.coupon-preview {
  display: flex;
  gap: 20px;
  padding: 20px;
  background: linear-gradient(135deg, #f8f9fa, #f0f3f5);
  border-radius: 12px;
  margin-bottom: 20px;
}

.preview-left {
  width: 100px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  padding: 15px;
  background: linear-gradient(135deg, #6B8E23, #8FBC8F);
  color: white;
  border-radius: 8px;
}

.preview-value {
  display: flex;
  align-items: baseline;
  justify-content: center;
  margin-bottom: 5px;
}

.preview-value .currency {
  font-size: 1rem;
}

.preview-value .value {
  font-size: 1.8rem;
}

.preview-condition {
  font-size: 0.75rem;
  opacity: 0.9;
  text-align: center;
}

.preview-right {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.preview-name {
  font-size: 1.1rem;
  font-weight: bold;
  color: #2c3e50;
  margin: 0 0 6px;
}

.preview-desc {
  font-size: 0.9rem;
  color: #7f8c8d;
  margin: 0 0 8px;
}

.preview-validity {
  font-size: 0.85rem;
  color: #7f8c8d;
}

.use-hint {
  text-align: center;
  padding: 15px;
  background: linear-gradient(135deg, rgba(107, 142, 35, 0.05), rgba(143, 188, 143, 0.05));
  border-radius: 8px;
}

.hint-text {
  font-size: 0.95rem;
  font-weight: 500;
  color: #6B8E23;
  margin: 0 0 8px;
}

.hint-subtext {
  font-size: 0.85rem;
  color: #7f8c8d;
  margin: 0;
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

@media (max-width: 768px) {
  .page-title {
    font-size: 2rem;
  }

  .coupon-tabs {
    gap: 10px;
  }

  .tab-btn {
    padding: 10px 20px;
    font-size: 0.9rem;
  }

  .coupons-grid {
    grid-template-columns: 1fr;
  }

  .coupon-card {
    flex-direction: column;
  }

  .coupon-left {
    width: 100%;
    flex-direction: row;
    justify-content: space-between;
    padding: 15px 20px;
  }

  .coupon-middle {
    width: 100%;
    height: 30px;
  }

  .circle {
    width: 20px;
    height: 20px;
    top: -10px;
    left: auto;
  }

  .top-circle {
    left: -10px;
  }

  .bottom-circle {
    right: -10px;
    left: auto;
  }

  .coupon-divider {
    width: calc(100% - 40px);
    height: 2px;
    border-left: none;
    border-top: 2px dashed #e4e8eb;
  }

  .browse-reward-info,
  .reward-available {
    margin: 20px;
  }

  .progress-info {
    flex-direction: column;
    gap: 5px;
    align-items: flex-start;
  }

  .reward-alert {
    flex-wrap: wrap;
  }

  .reward-text {
    order: 1;
    width: 100%;
    margin: 10px 0;
  }
}
</style>
