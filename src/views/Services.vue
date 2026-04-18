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

          <div class="filter-section">
            <h3 class="filter-title">价格区间</h3>
            <ul class="price-list">
              <li 
                class="price-item" 
                :class="{ active: selectedPrice === '全部' }"
                @click="selectPrice('全部')"
              >
                全部价格
              </li>
              <li 
                class="price-item" 
                :class="{ active: selectedPrice === '免费' }"
                @click="selectPrice('免费')"
              >
                免费服务
              </li>
              <li 
                class="price-item" 
                :class="{ active: selectedPrice === '低价' }"
                @click="selectPrice('低价')"
              >
                ¥50以下
              </li>
              <li 
                class="price-item" 
                :class="{ active: selectedPrice === '中价' }"
                @click="selectPrice('中价')"
              >
                ¥50-100
              </li>
              <li 
                class="price-item" 
                :class="{ active: selectedPrice === '高价' }"
                @click="selectPrice('高价')"
              >
                ¥100以上
              </li>
            </ul>
          </div>
        </aside>

        <main class="main-content">
          <div class="search-bar">
            <input 
              type="text" 
              v-model="searchQuery" 
              placeholder="搜索服务名称或描述..."
              @input="handleSearch"
            />
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

          <div class="results-info">
            共找到 <span class="results-count">{{ filteredServices.length }}</span> 项服务
          </div>

          <div class="services-grid" v-if="filteredServices.length > 0">
            <div 
              v-for="service in filteredServices" 
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

          <div class="no-results" v-else>
            <span class="no-results-icon">🔍</span>
            <h3>未找到相关服务</h3>
            <p>请尝试调整搜索条件或筛选条件</p>
            <button class="reset-btn" @click="resetFilters">重置筛选</button>
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

const filteredServices = computed(() => services.value)

const fetchServices = async () => {
  loading.value = true
  try {
    let priceRange = null
    if (selectedPrice.value === '免费') {
      priceRange = '免费'
    } else if (selectedPrice.value === '低价') {
      priceRange = '低价'
    } else if (selectedPrice.value === '中价') {
      priceRange = '中价'
    } else if (selectedPrice.value === '高价') {
      priceRange = '高价'
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
    if (sortParam) {
      params.append('sortBy', sortParam)
    }

    const url = params.toString() 
      ? `/api/services/search?${params.toString()}` 
      : '/api/services/search'

    const response = await request(url, {
      method: 'GET'
    })

    if (response.ok) {
      const data = await response.json()
      if (data.code === 200 && data.data) {
        services.value = data.data.services || []
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
  fetchServices()
}

const handleSort = () => {
  fetchServices()
}

const selectCategory = (category) => {
  selectedCategory.value = category
  fetchServices()
}

const selectPrice = (price) => {
  selectedPrice.value = price
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
  fetchServices()
}

watch([searchQuery, selectedCategory, selectedPrice, sortBy], () => {
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

.category-list,
.price-list {
  list-style: none;
  padding: 0;
}

.category-item,
.price-item {
  padding: 12px 15px;
  margin-bottom: 5px;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s ease;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.category-item:hover,
.price-item:hover {
  background-color: #f5f7fa;
}

.category-item.active,
.price-item.active {
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
  gap: 15px;
  margin-bottom: 20px;
}

.search-bar input {
  flex: 1;
  padding: 15px 20px;
  border: 2px solid #e4e8eb;
  border-radius: 10px;
  font-size: 1rem;
  transition: border-color 0.3s ease;
}

.search-bar input:focus {
  outline: none;
  border-color: #6B8E23;
}

.sort-select select {
  padding: 15px 20px;
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

  .sort-select select {
    width: 100%;
  }

  .services-grid {
    grid-template-columns: 1fr;
  }
}
</style>
