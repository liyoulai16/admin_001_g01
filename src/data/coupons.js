const COUPON_TYPES = {
  DISCOUNT: 'discount',
  FULL_REDUCTION: 'full_reduction'
}

const COUPON_STATUS = {
  AVAILABLE: 'available',
  USED: 'used',
  EXPIRED: 'expired'
}

const getCurrentUserCouponsKey = () => {
  const userId = localStorage.getItem('userId') || 'guest'
  return `user_coupons_${userId}`
}

const getBrowsingTimeKey = () => {
  const userId = localStorage.getItem('userId') || 'guest'
  return `browsing_time_${userId}`
}

const mockCoupons = [
  {
    id: 'c1',
    name: '新用户专享券',
    type: COUPON_TYPES.FULL_REDUCTION,
    description: '满100减20',
    discountAmount: 20,
    minSpend: 100,
    discountPercent: null,
    maxDiscount: null,
    status: COUPON_STATUS.AVAILABLE,
    validFrom: '2024-01-01',
    validTo: '2026-12-31',
    obtainTime: '2024-01-01 10:00:00',
    useTime: null,
    category: null,
    serviceId: null
  },
  {
    id: 'c2',
    name: '服务折扣券',
    type: COUPON_TYPES.DISCOUNT,
    description: '全场8折，最高减30元',
    discountAmount: null,
    minSpend: 0,
    discountPercent: 0.8,
    maxDiscount: 30,
    status: COUPON_STATUS.AVAILABLE,
    validFrom: '2024-01-01',
    validTo: '2026-12-31',
    obtainTime: '2024-02-15 14:30:00',
    useTime: null,
    category: null,
    serviceId: null
  },
  {
    id: 'c3',
    name: '保洁服务专享券',
    type: COUPON_TYPES.FULL_REDUCTION,
    description: '保洁服务满200减50',
    discountAmount: 50,
    minSpend: 200,
    discountPercent: null,
    maxDiscount: null,
    status: COUPON_STATUS.AVAILABLE,
    validFrom: '2024-01-01',
    validTo: '2026-06-30',
    obtainTime: '2024-03-01 09:00:00',
    useTime: null,
    category: '保洁服务',
    serviceId: null
  },
  {
    id: 'c4',
    name: '已使用优惠券',
    type: COUPON_TYPES.FULL_REDUCTION,
    description: '满50减10',
    discountAmount: 10,
    minSpend: 50,
    discountPercent: null,
    maxDiscount: null,
    status: COUPON_STATUS.USED,
    validFrom: '2024-01-01',
    validTo: '2025-12-31',
    obtainTime: '2024-01-15 10:00:00',
    useTime: '2024-02-20 15:30:00',
    category: null,
    serviceId: null
  },
  {
    id: 'c5',
    name: '已过期优惠券',
    type: COUPON_TYPES.DISCOUNT,
    description: '过期券示例',
    discountAmount: null,
    minSpend: 0,
    discountPercent: 0.9,
    maxDiscount: 20,
    status: COUPON_STATUS.EXPIRED,
    validFrom: '2023-01-01',
    validTo: '2023-12-31',
    obtainTime: '2023-01-01 10:00:00',
    useTime: null,
    category: null,
    serviceId: null
  }
]

const initUserCoupons = () => {
  const key = getCurrentUserCouponsKey()
  const existing = localStorage.getItem(key)
  if (!existing) {
    localStorage.setItem(key, JSON.stringify([...mockCoupons]))
  }
}

const getUserCoupons = () => {
  initUserCoupons()
  const key = getCurrentUserCouponsKey()
  const coupons = JSON.parse(localStorage.getItem(key) || '[]')
  return coupons
}

const getUserCouponsByStatus = (status) => {
  const coupons = getUserCoupons()
  if (status === 'all') {
    return coupons
  }
  return coupons.filter(c => c.status === status)
}

const getAvailableCouponsForService = (servicePrice, category = null, serviceId = null) => {
  const coupons = getUserCouponsByStatus(COUPON_STATUS.AVAILABLE)
  const now = new Date()
  
  return coupons.filter(coupon => {
    if (coupon.status !== COUPON_STATUS.AVAILABLE) return false
    
    const validTo = new Date(coupon.validTo)
    if (now > validTo) return false
    
    if (coupon.category && coupon.category !== category) return false
    
    if (coupon.serviceId && coupon.serviceId !== serviceId) return false
    
    if (coupon.minSpend && servicePrice < coupon.minSpend) return false
    
    return true
  })
}

const calculateDiscount = (coupon, originalPrice) => {
  if (!coupon) return 0
  
  if (coupon.type === COUPON_TYPES.FULL_REDUCTION) {
    if (originalPrice >= coupon.minSpend) {
      return coupon.discountAmount
    }
    return 0
  }
  
  if (coupon.type === COUPON_TYPES.DISCOUNT) {
    let discount = originalPrice * (1 - coupon.discountPercent)
    if (coupon.maxDiscount && discount > coupon.maxDiscount) {
      discount = coupon.maxDiscount
    }
    return Math.round(discount * 100) / 100
  }
  
  return 0
}

const useCoupon = (couponId, orderId) => {
  const key = getCurrentUserCouponsKey()
  const coupons = getUserCoupons()
  const couponIndex = coupons.findIndex(c => c.id === couponId)
  
  if (couponIndex === -1) return false
  
  coupons[couponIndex].status = COUPON_STATUS.USED
  coupons[couponIndex].useTime = new Date().toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit',
    second: '2-digit'
  }).replace(/\//g, '-')
  
  localStorage.setItem(key, JSON.stringify(coupons))
  return true
}

const addCoupon = (coupon) => {
  const key = getCurrentUserCouponsKey()
  const coupons = getUserCoupons()
  const newCoupon = {
    id: `c${Date.now()}`,
    status: COUPON_STATUS.AVAILABLE,
    obtainTime: new Date().toLocaleString('zh-CN', {
      year: 'numeric',
      month: '2-digit',
      day: '2-digit',
      hour: '2-digit',
      minute: '2-digit',
      second: '2-digit'
    }).replace(/\//g, '-'),
    useTime: null,
    ...coupon
  }
  coupons.unshift(newCoupon)
  localStorage.setItem(key, JSON.stringify(coupons))
  return newCoupon
}

const getBrowsingTime = () => {
  const key = getBrowsingTimeKey()
  const data = localStorage.getItem(key)
  if (!data) return 0
  return parseInt(data) || 0
}

const saveBrowsingTime = (seconds) => {
  const key = getBrowsingTimeKey()
  localStorage.setItem(key, seconds.toString())
}

const resetBrowsingTime = () => {
  const key = getBrowsingTimeKey()
  localStorage.setItem(key, '0')
}

const isBrowseRewardClaimed = () => {
  const userId = localStorage.getItem('userId') || 'guest'
  const key = `browse_reward_claimed_${userId}`
  return localStorage.getItem(key) === 'true'
}

const setBrowseRewardClaimed = () => {
  const userId = localStorage.getItem('userId') || 'guest'
  const key = `browse_reward_claimed_${userId}`
  localStorage.setItem(key, 'true')
}

const formatTime = (seconds) => {
  const hours = Math.floor(seconds / 3600)
  const minutes = Math.floor((seconds % 3600) / 60)
  const secs = seconds % 60
  
  if (hours > 0) {
    return `${hours}小时${minutes}分钟`
  } else if (minutes > 0) {
    return `${minutes}分钟${secs}秒`
  } else {
    return `${secs}秒`
  }
}

const getCouponTypeText = (type) => {
  if (type === COUPON_TYPES.DISCOUNT) return '折扣券'
  if (type === COUPON_TYPES.FULL_REDUCTION) return '满减券'
  return '优惠券'
}

const getCouponStatusText = (status) => {
  if (status === COUPON_STATUS.AVAILABLE) return '可使用'
  if (status === COUPON_STATUS.USED) return '已使用'
  if (status === COUPON_STATUS.EXPIRED) return '已过期'
  return '未知'
}

export {
  COUPON_TYPES,
  COUPON_STATUS,
  initUserCoupons,
  getUserCoupons,
  getUserCouponsByStatus,
  getAvailableCouponsForService,
  calculateDiscount,
  useCoupon,
  addCoupon,
  getBrowsingTime,
  saveBrowsingTime,
  resetBrowsingTime,
  isBrowseRewardClaimed,
  setBrowseRewardClaimed,
  formatTime,
  getCouponTypeText,
  getCouponStatusText
}
