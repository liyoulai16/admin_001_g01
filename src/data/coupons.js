import request from '../utils/request'

export const COUPON_TYPES = {
  DISCOUNT: 'discount',
  FULL_REDUCTION: 'full_reduction'
}

export const COUPON_STATUS = {
  AVAILABLE: 'available',
  USED: 'used',
  EXPIRED: 'expired'
}

const getCurrentUserId = () => {
  return localStorage.getItem('userId') || '1'
}

export const fetchUserCoupons = async (status = 'all', page = 1, size = 20) => {
  try {
    const userId = getCurrentUserId()
    const params = new URLSearchParams()
    params.append('userId', userId)
    params.append('status', status)
    params.append('current', page)
    params.append('size', size)
    
    const response = await request(`/api/coupons/my?${params.toString()}`, {
      method: 'GET'
    })
    
    const data = await response.json()
    if (data.code === 200) {
      return data.data || { records: [], total: 0 }
    }
    return { records: [], total: 0 }
  } catch (error) {
    console.error('获取用户优惠券失败:', error)
    return { records: [], total: 0 }
  }
}

export const fetchAvailableCoupons = async () => {
  try {
    const userId = getCurrentUserId()
    const response = await request(`/api/coupons/available?userId=${userId}`, {
      method: 'GET'
    })
    
    const data = await response.json()
    if (data.code === 200) {
      return data.data || []
    }
    return []
  } catch (error) {
    console.error('获取可用优惠券失败:', error)
    return []
  }
}

export const fetchBrowsingStatus = async () => {
  try {
    const userId = getCurrentUserId()
    const response = await request(`/api/browse/status?userId=${userId}`, {
      method: 'GET'
    })
    
    const data = await response.json()
    if (data.code === 200) {
      return data.data || { totalSeconds: 0, requiredSeconds: 60, canClaimReward: false, rewardClaimed: false }
    }
    return { totalSeconds: 0, requiredSeconds: 60, canClaimReward: false, rewardClaimed: false }
  } catch (error) {
    console.error('获取浏览状态失败:', error)
    return { totalSeconds: 0, requiredSeconds: 60, canClaimReward: false, rewardClaimed: false }
  }
}

export const updateBrowsingTime = async (seconds = 1) => {
  try {
    const userId = getCurrentUserId()
    const params = new URLSearchParams()
    params.append('userId', userId)
    params.append('seconds', seconds)
    
    const response = await request(`/api/browse/update-time?${params.toString()}`, {
      method: 'POST'
    })
    
    const data = await response.json()
    if (data.code === 200) {
      return data.data
    }
    return null
  } catch (error) {
    console.error('更新浏览时间失败:', error)
    return null
  }
}

export const claimBrowseReward = async () => {
  try {
    const userId = getCurrentUserId()
    const response = await request(`/api/browse/claim-reward?userId=${userId}`, {
      method: 'POST'
    })
    
    const data = await response.json()
    return data
  } catch (error) {
    console.error('领取浏览奖励失败:', error)
    return { code: 500, message: '网络错误' }
  }
}

export const claimCoupon = async (couponId) => {
  try {
    const userId = getCurrentUserId()
    const response = await request(`/api/coupons/claim/${couponId}?userId=${userId}`, {
      method: 'POST'
    })
    
    const data = await response.json()
    return data
  } catch (error) {
    console.error('领取优惠券失败:', error)
    return { code: 500, message: '网络错误' }
  }
}

export const formatTime = (seconds) => {
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

export const getCouponTypeText = (type) => {
  if (type === COUPON_TYPES.DISCOUNT) return '折扣券'
  if (type === COUPON_TYPES.FULL_REDUCTION) return '满减券'
  return '优惠券'
}

export const getCouponStatusText = (status) => {
  if (status === COUPON_STATUS.AVAILABLE) return '可使用'
  if (status === COUPON_STATUS.USED) return '已使用'
  if (status === COUPON_STATUS.EXPIRED) return '已过期'
  return '未知'
}

export const getCouponDisplayValue = (coupon) => {
  if (coupon.type === COUPON_TYPES.FULL_REDUCTION) {
    return coupon.discountAmount || 0
  }
  if (coupon.type === COUPON_TYPES.DISCOUNT) {
    return Math.round((coupon.discountPercent || 0) * 10)
  }
  return 0
}

export const getCouponCondition = (coupon) => {
  if (coupon.type === COUPON_TYPES.FULL_REDUCTION) {
    return `满${coupon.minSpend || 0}元可用`
  }
  if (coupon.type === COUPON_TYPES.DISCOUNT) {
    if (coupon.maxDiscount) {
      return `最高减${coupon.maxDiscount}元`
    }
    return '无门槛'
  }
  return ''
}

export const calculateDiscount = (coupon, originalPrice) => {
  if (!coupon || !originalPrice) return 0
  
  const price = Number(originalPrice)
  
  if (coupon.type === COUPON_TYPES.FULL_REDUCTION) {
    const minSpend = Number(coupon.minSpend) || 0
    if (price >= minSpend) {
      return Number(coupon.discountAmount) || 0
    }
    return 0
  }
  
  if (coupon.type === COUPON_TYPES.DISCOUNT) {
    const discountPercent = Number(coupon.discountPercent) || 1
    let discount = price * (1 - discountPercent)
    const maxDiscount = Number(coupon.maxDiscount)
    if (maxDiscount && discount > maxDiscount) {
      discount = maxDiscount
    }
    return Math.round(discount * 100) / 100
  }
  
  return 0
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
  }
]

const initUserCoupons = () => {
  const key = getCurrentUserCouponsKey()
  const existing = localStorage.getItem(key)
  if (!existing) {
    localStorage.setItem(key, JSON.stringify([...mockCoupons]))
  }
}

export const getUserCoupons = () => {
  initUserCoupons()
  const key = getCurrentUserCouponsKey()
  const coupons = JSON.parse(localStorage.getItem(key) || '[]')
  return coupons
}

export const getUserCouponsByStatus = (status) => {
  const coupons = getUserCoupons()
  if (status === 'all') {
    return coupons
  }
  return coupons.filter(c => c.status === status)
}

export const getAvailableCouponsForService = (servicePrice, category = null, serviceId = null) => {
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

export const useCoupon = (couponId, orderId) => {
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

export const addCoupon = (coupon) => {
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

export const getBrowsingTime = () => {
  const key = getBrowsingTimeKey()
  const data = localStorage.getItem(key)
  if (!data) return 0
  return parseInt(data) || 0
}

export const saveBrowsingTime = (seconds) => {
  const key = getBrowsingTimeKey()
  localStorage.setItem(key, seconds.toString())
}

export const resetBrowsingTime = () => {
  const key = getBrowsingTimeKey()
  localStorage.setItem(key, '0')
}

export const isBrowseRewardClaimed = () => {
  const userId = localStorage.getItem('userId') || 'guest'
  const key = `browse_reward_claimed_${userId}`
  return localStorage.getItem(key) === 'true'
}

export const setBrowseRewardClaimed = () => {
  const userId = localStorage.getItem('userId') || 'guest'
  const key = `browse_reward_claimed_${userId}`
  localStorage.setItem(key, 'true')
}
