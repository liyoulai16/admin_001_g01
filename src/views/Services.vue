<template>
  <div class="services">
    <div class="page-header">
      <div class="particles">
        <div v-for="i in 30" :key="i" class="particle" :style="getParticleStyle(i)"></div>
      </div>
      <div class="container">
        <h1 class="page-title">服务列表</h1>
        <p class="page-subtitle">为您提供全方位的社区生活服务</p>
      </div>
    </div>

    <div class="container">
      <div class="services-content">
        <aside class="sidebar">
          <div class="filter-section">
            <h3 class="filter-title">服务分类</h3>
            <ul class="category-list">
              <li 
                class="category-item" 
                :class="{ active: selectedCategory === '全部' }"
                @click="selectCategory('全部')"
              >
                全部服务
              </li>
              <li 
                v-for="category in categories" 
                :key="category.id"
                class="category-item" 
                :class="{ active: selectedCategory === category.name }"
                @click="selectCategory(category.name)"
              >
                {{ category.name }}
                <span class="category-badge">{{ category.count }}</span>
              </li>
            </ul>
          </div>
        </aside>

        <main class="main-content">
          <div class="search-bar">
            <div class="search-input-wrapper">
              <input 
                type="text" 
                v-model="searchQuery" 
                placeholder="搜索服务名称或描述..."
                @keyup.enter="handleSearch"
              />
              <button class="search-btn" @click="handleSearch">搜索</button>
            </div>
            
            <div class="filter-controls">
              <div class="price-filter-dropdown" :class="{ open: showPriceFilter }">
                <button class="filter-toggle" @click="togglePriceFilter">
                  <span class="filter-icon">💰</span>
                  <span class="filter-text">{{ priceFilterText }}</span>
                  <svg class="dropdown-arrow" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                    <path d="M6 9L12 15L18 9" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                  </svg>
                </button>
                
                <div class="price-filter-panel" v-if="showPriceFilter">
                  <div class="price-slider-section">
                    <div class="price-range-display">
                      <span class="price-label">价格区间:</span>
                      <span class="price-value">
                        ¥{{ minPrice }} - ¥{{ maxPrice }}
                      </span>
                    </div>
                    
                    <div class="dual-slider" ref="sliderRef">
                      <div class="slider-track">
                        <div class="slider-active-track" :style="sliderTrackStyle"></div>
                      </div>
                      <input 
                        type="range" 
                        v-model="minPrice" 
                        :min="0" 
                        :max="maxRange" 
                        :step="10"
                        class="slider-thumb min-thumb"
                        @input="onMinThumbChange"
                      />
                      <input 
                        type="range" 
                        v-model="maxPrice" 
                        :min="0" 
                        :max="maxRange" 
                        :step="10"
                        class="slider-thumb max-thumb"
                        @input="onMaxThumbChange"
                      />
                      <div class="slider-tooltip min-tooltip" :style="minTooltipStyle">¥{{ minPrice }}</div>
                      <div class="slider-tooltip max-tooltip" :style="maxTooltipStyle">¥{{ maxPrice }}</div>
                    </div>
                  </div>
                  
                  <div class="price-quick-select">
                    <span class="quick-label">快捷选择:</span>
                    <div class="quick-buttons">
                      <button 
                        class="quick-btn" 
                        :class="{ active: isQuickSelected('全部') }"
                        @click="selectQuickPrice('全部')"
                      >全部</button>
                      <button 
                        class="quick-btn" 
                        :class="{ active: isQuickSelected('免费') }"
                        @click="selectQuickPrice('免费')"
                      >免费</button>
                      <button 
                        class="quick-btn" 
                        :class="{ active: isQuickSelected('低价') }"
                        @click="selectQuickPrice('低价')"
                      >¥50以下</button>
                      <button 
                        class="quick-btn" 
                        :class="{ active: isQuickSelected('中价') }"
                        @click="selectQuickPrice('中价')"
                      >¥50-100</button>
                      <button 
                        class="quick-btn" 
                        :class="{ active: isQuickSelected('高价') }"
                        @click="selectQuickPrice('高价')"
                      >¥100以上</button>
                    </div>
                  </div>
                  
                  <div class="price-filter-actions">
                    <button class="reset-btn" @click="resetPriceFilter">重置</button>
                    <button class="apply-btn" @click="applyPriceFilter">确定</button>
                  </div>
                </div>
              </div>
              
              <div class="sort-select">
                <select v-model="sortBy" @change="handleSort">
                  <option value="default">默认排序</option>
                  <option value="rating">评分最高</option>
                  <option value="reviews">评价最多</option>
                  <option value="price-asc">价格从低到高</option>
                  <option value="price-desc">价格从高到低</option>
                </select>
              </div>
            </div>
          </div>

          <div class="results-info">
            共找到 <span class="results-count">{{ total }}</span> 项服务
          </div>

          <div class="services-grid" v-if="services.length > 0">
            <div 
              v-for="service in services" 
              :key="service.id" 
              class="service-card"
              @click="goToServiceDetail(service.id)"
            >
              <div class="service-header">
                <span class="service-icon">{{ service.icon }}</span>
                <span class="service-category-badge">{{ service.category }}</span>
              </div>
              <h3 class="service-name">{{ service.name }}</h3>
              <p class="service-description">{{ service.description }}</p>
              <div class="service-features">
                <span 
                  v-for="(feature, index) in service.features.slice(0, 3)" 
                  :key="index"
                  class="feature-tag"
                >
                  {{ feature }}
                </span>
              </div>
              <div class="service-footer">
                <div class="service-price-info">
                  <span class="service-price">{{ service.price }}</span>
                </div>
                <div class="service-rating-info">
                  <span class="rating-stars">⭐</span>
                  <span class="rating-value">{{ service.rating }}</span>
                  <span class="rating-count">({{ service.reviews }}条评价)</span>
                </div>
              </div>
            </div>
          </div>

          <div class="no-results" v-else-if="!loading">
            <span class="no-results-icon">🔍</span>
            <h3>未找到相关服务</h3>
            <p>请尝试调整搜索条件或筛选条件</p>
            <button class="reset-btn" @click="resetFilters">重置筛选</button>
          </div>

          <div class="pagination-section" v-if="pages > 1">
            <div class="pagination">
              <button 
                class="page-btn prev-btn" 
                :disabled="currentPage === 1"
                @click="goToPage(currentPage - 1)"
              >
                <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                  <path d="M15 18L9 12L15 6" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                </svg>
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
                :disabled="currentPage === pages"
                @click="goToPage(currentPage + 1)"
              >
                <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                  <path d="M9 18L15 12L9 6" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                </svg>
              </button>
            </div>
          </div>
        </main>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import request from '../utils/request'

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

const services = ref([])
const categories = ref([])
const searchQuery = ref('')
const selectedCategory = ref('全部')
const selectedPrice = ref('全部')
const sortBy = ref('default')
const loading = ref(false)
const total = ref(0)
const currentPage = ref(1)
const size = ref(6)
const pages = ref(0)

const showPriceFilter = ref(false)
const maxRange = ref(500)
const minPrice = ref(0)
const maxPrice = ref(500)
const tempMinPrice = ref(0)
const tempMaxPrice = ref(500)
const tempSelectedPrice = ref('全部')
const sliderRef = ref(null)

const priceFilterText = computed(() => {
  if (selectedPrice === '全部' && minPrice.value === 0 && maxPrice.value === maxRange.value) {
    return '价格区间'
  }
  if (selectedPrice === '免费') {
    return '免费服务'
  }
  if (selectedPrice === '低价') {
    return '¥50以下'
  }
  if (selectedPrice === '中价') {
    return '¥50-100'
  }
  if (selectedPrice === '高价') {
    return '¥100以上'
  }
  return `¥${minPrice.value}-¥${maxPrice.value}`
})

const sliderTrackStyle = computed(() => {
  const minPercent = (minPrice.value / maxRange.value) * 100
  const maxPercent = (maxPrice.value / maxRange.value) * 100
  return {
    left: `${minPercent}%`,
    right: `${100 - maxPercent}%`
  }
})

const minTooltipStyle = computed(() => {
  const percent = (minPrice.value / maxRange.value) * 100
  return {
    left: `${percent}%`
  }
})

const maxTooltipStyle = computed(() => {
  const percent = (maxPrice.value / maxRange.value) * 100
  return {
    left: `${percent}%`
  }
})

const visiblePages = computed(() => {
  const current = currentPage.value
  const totalPages = pages.value
  const delta = 2
  const range = []
  const rangeWithDots = []
  let l

  for (let i = 1; i <= totalPages; i++) {
    if (i === 1 || i === totalPages || (i >= current - delta && i <= current + delta)) {
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

const togglePriceFilter = () => {
  if (!showPriceFilter.value) {
    tempMinPrice.value = minPrice.value
    tempMaxPrice.value = maxPrice.value
    tempSelectedPrice.value = selectedPrice.value
  }
  showPriceFilter.value = !showPriceFilter.value
}

const onMinThumbChange = () => {
  if (minPrice.value > maxPrice.value) {
    const temp = minPrice.value
    minPrice.value = maxPrice.value
    maxPrice.value = temp
  }
}

const onMaxThumbChange = () => {
  if (maxPrice.value < minPrice.value) {
    const temp = maxPrice.value
    maxPrice.value = minPrice.value
    minPrice.value = temp
  }
}

const isQuickSelected = (price) => {
  return selectedPrice.value === price && minPrice.value === 0 && maxPrice.value === maxRange.value
}

const selectQuickPrice = (price) => {
  selectedPrice.value = price
  if (price === '免费') {
    minPrice.value = 0
    maxPrice.value = 0
  } else if (price === '低价') {
    minPrice.value = 0
    maxPrice.value = 50
  } else if (price === '中价') {
    minPrice.value = 50
    maxPrice.value = 100
  } else if (price === '高价') {
    minPrice.value = 100
    maxPrice.value = maxRange.value
  } else {
    minPrice.value = 0
    maxPrice.value = maxRange.value
  }
}

const resetPriceFilter = () => {
  minPrice.value = 0
  maxPrice.value = maxRange.value
  selectedPrice.value = '全部'
}

const applyPriceFilter = () => {
  showPriceFilter.value = false
  currentPage.value = 1
  fetchServices()
}

const fetchServices = async () => {
  loading.value = true
  try {
    let priceRange = null
    if (selectedPrice.value !== '全部' && minPrice.value === 0 && maxPrice.value === maxRange.value) {
      priceRange = selectedPrice.value
    }

    let sortParam = null
    if (sortBy.value === 'rating') {
      sortParam = 'rating'
    } else if (sortBy.value === 'reviews') {
      sortParam = 'reviews'
    } else if (sortBy.value === 'price-asc') {
      sortParam = 'price-asc'
    } else if (sortBy.value === 'price-desc') {
      sortParam = 'price-desc'
    }

    const params = new URLSearchParams()
    if (searchQuery.value) {
      params.append('keyword', searchQuery.value)
    }
    if (selectedCategory.value !== '全部') {
      params.append('category', selectedCategory.value)
    }
    if (priceRange) {
      params.append('priceRange', priceRange)
    }
    
    if (minPrice.value > 0 || maxPrice.value < maxRange.value) {
      if (selectedPrice.value === '全部' || (minPrice.value !== 0 || maxPrice.value !== maxRange.value)) {
        params.append('minPrice', minPrice.value)
        params.append('maxPrice', maxPrice.value)
      }
    }
    
    if (sortParam) {
      params.append('sortBy', sortParam)
    }
    params.append('current', currentPage.value)
    params.append('size', size.value)

    const url = `/api/services/search?${params.toString()}`

    const response = await request(url, {
      method: 'GET'
    })

    if (response.ok) {
      const data = await response.json()
      if (data.code === 200 && data.data) {
        services.value = data.data.services || []
        total.value = data.data.total || 0
        currentPage.value = data.data.current || 1
        size.value = data.data.size || 6
        pages.value = data.data.pages || 0
        if (categories.value.length === 0) {
          categories.value = data.data.categories || []
        }
      }
    }
  } catch (error) {
    console.error('获取服务数据失败:', error)
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  currentPage.value = 1
  fetchServices()
}

const handleSort = () => {
  currentPage.value = 1
  fetchServices()
}

const selectCategory = (category) => {
  selectedCategory.value = category
  currentPage.value = 1
  fetchServices()
}

const goToServiceDetail = (id) => {
  router.push(`/services/${id}`)
}

const resetFilters = () => {
  searchQuery.value = ''
  selectedCategory.value = '全部'
  selectedPrice.value = '全部'
  sortBy.value = 'default'
  minPrice.value = 0
  maxPrice.value = maxRange.value
  currentPage.value = 1
  fetchServices()
}

const goToPage = (page) => {
  if (page >= 1 && page <= pages.value) {
    currentPage.value = page
    fetchServices()
    window.scrollTo({ top: 0, behavior: 'smooth' })
  }
}

watch([searchQuery, selectedCategory, selectedPrice, sortBy, minPrice, maxPrice], () => {
  currentPage.value = 1
  fetchServices()
}, { deep: true })

onMounted(() => {
  const searchParam = route.query.search
  const categoryParam = route.query.category
  
  if (searchParam) {
    searchQuery.value = decodeURIComponent(searchParam)
  }
  
  if (categoryParam) {
    selectedCategory.value = decodeURIComponent(categoryParam)
  }
  
  fetchServices()
})
</script>

<style scoped>
.services {
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

.services-content {
  display: flex;
  gap: 30px;
  padding: 40px 0;
}

.sidebar {
  width: 280px;
  flex-shrink: 0;
}

.filter-section {
  background: white;
  border-radius: 15px;
  padding: 25px;
  margin-bottom: 20px;
  box-shadow: 0 5px 20px rgba(0, 0, 0, 0.08);
}

.filter-title {
  font-size: 1.1rem;
  margin-bottom: 15px;
  color: #2c3e50;
  font-weight: bold;
}

.category-list {
  list-style: none;
  padding: 0;
}

.category-item {
  padding: 12px 15px;
  margin-bottom: 5px;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s ease;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.category-item:hover {
  background-color: #f5f7fa;
}

.category-item.active {
  background: linear-gradient(135deg, #6B8E23, #8FBC8F);
  color: white;
}

.category-badge {
  background: rgba(0, 0, 0, 0.1);
  padding: 2px 8px;
  border-radius: 10px;
  font-size: 0.8rem;
}

.main-content {
  flex: 1;
}

.search-bar {
  display: flex;
  flex-direction: column;
  gap: 15px;
  margin-bottom: 20px;
}

.search-input-wrapper {
  display: flex;
  gap: 10px;
  flex: 1;
}

.search-input-wrapper input {
  flex: 1;
  padding: 15px 20px;
  border: 2px solid #e4e8eb;
  border-radius: 10px;
  font-size: 1rem;
  transition: border-color 0.3s ease;
}

.search-input-wrapper input:focus {
  outline: none;
  border-color: #6B8E23;
}

.search-btn {
  padding: 15px 30px;
  background: linear-gradient(135deg, #6B8E23, #8FBC8F);
  color: white;
  border-radius: 10px;
  font-size: 1rem;
  font-weight: bold;
  transition: all 0.3s ease;
  cursor: pointer;
}

.search-btn:hover {
  background: linear-gradient(135deg, #556B2F, #5D7C4A);
  transform: translateY(-2px);
  box-shadow: 0 5px 15px rgba(107, 142, 35, 0.3);
}

.filter-controls {
  display: flex;
  gap: 15px;
  align-items: center;
}

.price-filter-dropdown {
  position: relative;
  z-index: 100;
}

.filter-toggle {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 20px;
  background: white;
  border: 2px solid #e4e8eb;
  border-radius: 10px;
  font-size: 1rem;
  cursor: pointer;
  transition: all 0.3s ease;
}

.filter-toggle:hover {
  border-color: #6B8E23;
}

.filter-toggle.open {
  border-color: #6B8E23;
  border-bottom-left-radius: 0;
  border-bottom-right-radius: 0;
}

.filter-icon {
  font-size: 1.2rem;
}

.filter-text {
  font-weight: 500;
  color: #2c3e50;
}

.dropdown-arrow {
  width: 16px;
  height: 16px;
  color: #7f8c8d;
  transition: transform 0.3s ease;
}

.filter-toggle.open .dropdown-arrow {
  transform: rotate(180deg);
}

.price-filter-panel {
  position: absolute;
  top: 100%;
  left: 0;
  min-width: 350px;
  background: white;
  border: 2px solid #6B8E23;
  border-top: none;
  border-radius: 0 0 10px 10px;
  padding: 20px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.15);
}

.price-slider-section {
  margin-bottom: 20px;
  padding-bottom: 20px;
  border-bottom: 1px solid #e4e8eb;
}

.price-range-display {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
}

.price-label {
  font-weight: 500;
  color: #2c3e50;
}

.price-value {
  font-weight: bold;
  color: #6B8E23;
  font-size: 1.1rem;
}

.dual-slider {
  position: relative;
  height: 50px;
  margin: 0 10px;
}

.slider-track {
  position: absolute;
  top: 50%;
  left: 0;
  right: 0;
  height: 6px;
  background: #e4e8eb;
  border-radius: 3px;
  transform: translateY(-50%);
}

.slider-active-track {
  position: absolute;
  top: 0;
  bottom: 0;
  background: linear-gradient(90deg, #6B8E23, #8FBC8F);
  border-radius: 3px;
}

.slider-thumb {
  position: absolute;
  top: 50%;
  width: 20px;
  height: 20px;
  background: white;
  border: 3px solid #6B8E23;
  border-radius: 50%;
  cursor: pointer;
  transform: translate(-50%, -50%);
  appearance: none;
  -webkit-appearance: none;
  pointer-events: auto;
  z-index: 10;
}

.slider-thumb::-webkit-slider-thumb {
  appearance: none;
  -webkit-appearance: none;
  width: 20px;
  height: 20px;
  background: white;
  border: 3px solid #6B8E23;
  border-radius: 50%;
  cursor: pointer;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.2);
}

.slider-thumb::-moz-range-thumb {
  width: 20px;
  height: 20px;
  background: white;
  border: 3px solid #6B8E23;
  border-radius: 50%;
  cursor: pointer;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.2);
}

.slider-thumb:hover {
  transform: translate(-50%, -50%) scale(1.1);
  box-shadow: 0 3px 10px rgba(107, 142, 35, 0.3);
}

.slider-tooltip {
  position: absolute;
  top: -5px;
  transform: translate(-50%, -100%);
  background: linear-gradient(135deg, #6B8E23, #8FBC8F);
  color: white;
  padding: 4px 10px;
  border-radius: 4px;
  font-size: 0.85rem;
  font-weight: 500;
  white-space: nowrap;
  pointer-events: none;
}

.slider-tooltip::after {
  content: '';
  position: absolute;
  bottom: -6px;
  left: 50%;
  transform: translateX(-50%);
  border: 6px solid transparent;
  border-top-color: #6B8E23;
}

.price-quick-select {
  margin-bottom: 15px;
}

.quick-label {
  display: block;
  font-weight: 500;
  color: #2c3e50;
  margin-bottom: 10px;
}

.quick-buttons {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.quick-btn {
  padding: 6px 14px;
  background: #f5f7fa;
  border: 1px solid #e4e8eb;
  border-radius: 20px;
  font-size: 0.85rem;
  cursor: pointer;
  transition: all 0.3s ease;
}

.quick-btn:hover {
  background: #e4e8eb;
}

.quick-btn.active {
  background: linear-gradient(135deg, #6B8E23, #8FBC8F);
  color: white;
  border-color: transparent;
}

.price-filter-actions {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  padding-top: 15px;
  border-top: 1px solid #e4e8eb;
}

.price-filter-actions .reset-btn {
  padding: 8px 20px;
  background: transparent;
  border: 2px solid #e4e8eb;
  border-radius: 8px;
  font-size: 0.9rem;
  cursor: pointer;
  transition: all 0.3s ease;
}

.price-filter-actions .reset-btn:hover {
  border-color: #7f8c8d;
}

.price-filter-actions .apply-btn {
  padding: 8px 20px;
  background: linear-gradient(135deg, #6B8E23, #8FBC8F);
  color: white;
  border: none;
  border-radius: 8px;
  font-size: 0.9rem;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;
}

.price-filter-actions .apply-btn:hover {
  background: linear-gradient(135deg, #556B2F, #5D7C4A);
}

.sort-select select {
  padding: 12px 20px;
  border: 2px solid #e4e8eb;
  border-radius: 10px;
  font-size: 1rem;
  background: white;
  cursor: pointer;
  transition: border-color 0.3s ease;
}

.sort-select select:focus {
  outline: none;
  border-color: #6B8E23;
}

.results-info {
  margin-bottom: 20px;
  color: #7f8c8d;
  font-size: 0.95rem;
}

.results-count {
  font-weight: bold;
  color: #6B8E23;
}

.services-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 25px;
}

.service-card {
  background: white;
  border-radius: 15px;
  padding: 25px;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 5px 20px rgba(0, 0, 0, 0.08);
  display: flex;
  flex-direction: column;
}

.service-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 15px 30px rgba(0, 0, 0, 0.15);
}

.service-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 15px;
}

.service-icon {
  font-size: 2.5rem;
}

.service-category-badge {
  background: linear-gradient(135deg, #6B8E23, #8FBC8F);
  color: white;
  padding: 5px 12px;
  border-radius: 20px;
  font-size: 0.8rem;
}

.service-name {
  font-size: 1.2rem;
  margin-bottom: 10px;
  color: #2c3e50;
}

.service-description {
  font-size: 0.9rem;
  color: #7f8c8d;
  margin-bottom: 15px;
  line-height: 1.5;
  flex: 1;
}

.service-features {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-bottom: 15px;
}

.feature-tag {
  background: #f5f7fa;
  color: #555;
  padding: 4px 10px;
  border-radius: 15px;
  font-size: 0.8rem;
}

.service-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 15px;
  border-top: 1px solid #e4e8eb;
}

.service-price {
  font-size: 1.2rem;
  font-weight: bold;
  color: #C45B1A;
}

.service-rating-info {
  display: flex;
  align-items: center;
  gap: 5px;
  font-size: 0.9rem;
}

.rating-stars {
  color: #f39c12;
}

.rating-value {
  font-weight: bold;
  color: #2c3e50;
}

.rating-count {
  color: #7f8c8d;
}

.no-results {
  text-align: center;
  padding: 60px 20px;
}

.no-results-icon {
  font-size: 4rem;
  margin-bottom: 20px;
  display: block;
}

.no-results h3 {
  font-size: 1.3rem;
  margin-bottom: 10px;
  color: #2c3e50;
}

.no-results p {
  color: #7f8c8d;
  margin-bottom: 20px;
}

.reset-btn {
  padding: 12px 30px;
  background: linear-gradient(135deg, #6B8E23, #8FBC8F);
  color: white;
  border-radius: 25px;
  font-size: 1rem;
  transition: all 0.3s ease;
}

.reset-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 10px 20px rgba(107, 142, 35, 0.3);
}

.pagination-section {
  display: flex;
  justify-content: center;
  margin-top: 40px;
  padding-top: 20px;
}

.pagination {
  display: flex;
  align-items: center;
  gap: 8px;
}

.page-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  min-width: 40px;
  height: 40px;
  padding: 0 12px;
  background: white;
  border: 2px solid #e4e8eb;
  border-radius: 8px;
  font-size: 1rem;
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

.page-btn svg {
  width: 18px;
  height: 18px;
}

@media (max-width: 992px) {
  .services-content {
    flex-direction: column;
  }

  .sidebar {
    width: 100%;
    display: flex;
    gap: 20px;
    overflow-x: auto;
  }

  .filter-section {
    min-width: 250px;
    margin-bottom: 0;
  }
}

@media (max-width: 768px) {
  .page-header {
    padding: 30px 0;
  }

  .page-title {
    font-size: 1.8rem;
  }

  .services-content {
    padding: 20px 0;
  }

  .sidebar {
    flex-direction: column;
  }

  .filter-section {
    min-width: auto;
  }

  .search-bar {
    flex-direction: column;
  }

  .search-input-wrapper {
    flex-direction: column;
  }

  .search-btn {
    width: 100%;
  }

  .filter-controls {
    flex-direction: column;
    align-items: stretch;
  }

  .price-filter-panel {
    min-width: 100%;
    left: 0;
    right: 0;
  }

  .sort-select select {
    width: 100%;
  }

  .services-grid {
    grid-template-columns: 1fr;
  }

  .pagination {
    flex-wrap: wrap;
    justify-content: center;
  }
}
</style>
