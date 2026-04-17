<template>
  <div class="home">
    <div v-if="showSuccessMessage" class="toast-message toast-success">
      <span class="toast-icon">✓</span>
      <span class="toast-text">登录成功！</span>
    </div>
    <section class="hero">
      <div class="container">
        <div class="hero-content">
          <h1 class="hero-title">社区生活服务</h1>
          <p class="hero-subtitle">让生活更便捷，让社区更温暖</p>
          <div class="hero-search">
            <input 
              type="text" 
              v-model="searchQuery" 
              placeholder="搜索您需要的服务..."
              @keyup.enter="handleSearch"
            />
            <button class="search-btn" @click="handleSearch">搜索</button>
          </div>
        </div>
      </div>
    </section>

    <section class="categories-section">
      <div class="container">
        <h2 class="section-title">服务分类</h2>
        <div class="categories-grid">
          <div 
            v-for="category in categories" 
            :key="category.id" 
            class="category-card"
            @click="goToServices(category.name)"
          >
            <span class="category-icon">{{ category.icon }}</span>
            <h3 class="category-name">{{ category.name }}</h3>
            <p class="category-count">{{ category.count }} 项服务</p>
          </div>
        </div>
      </div>
    </section>

    <section class="featured-services">
      <div class="container">
        <h2 class="section-title">热门服务</h2>
        <div class="services-grid">
          <div 
            v-for="service in featuredServices" 
            :key="service.id" 
            class="service-card"
            @click="goToServiceDetail(service.id)"
          >
            <div class="service-icon">{{ service.icon }}</div>
            <div class="service-info">
              <h3 class="service-name">{{ service.name }}</h3>
              <p class="service-category">{{ service.category }}</p>
              <p class="service-description">{{ service.description }}</p>
              <div class="service-footer">
                <span class="service-price">{{ service.price }}</span>
                <div class="service-rating">
                  <span class="rating-stars">⭐</span>
                  <span class="rating-value">{{ service.rating }}</span>
                  <span class="rating-count">({{ service.reviews }})</span>
                </div>
              </div>
            </div>
          </div>
        </div>
        <div class="view-all">
          <router-link to="/services" class="view-all-btn">查看全部服务 →</router-link>
        </div>
      </div>
    </section>

    <section class="features-section">
      <div class="container">
        <h2 class="section-title">为什么选择我们</h2>
        <div class="features-grid">
          <div class="feature-item">
            <span class="feature-icon">✅</span>
            <h3 class="feature-title">专业可靠</h3>
            <p class="feature-description">所有服务人员经过严格筛选和培训，确保服务质量</p>
          </div>
          <div class="feature-item">
            <span class="feature-icon">⚡</span>
            <h3 class="feature-title">快速响应</h3>
            <p class="feature-description">在线预约，快速响应，让您的需求及时得到满足</p>
          </div>
          <div class="feature-item">
            <span class="feature-icon">💰</span>
            <h3 class="feature-title">价格透明</h3>
            <p class="feature-description">明码标价，无隐藏费用，让您消费得明明白白</p>
          </div>
          <div class="feature-item">
            <span class="feature-icon">🛡️</span>
            <h3 class="feature-title">安全保障</h3>
            <p class="feature-description">所有服务人员实名认证，服务过程全程保障</p>
          </div>
        </div>
      </div>
    </section>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { services, categories } from '../data/services'

const router = useRouter()
const searchQuery = ref('')
const showSuccessMessage = ref(false)

const featuredServices = computed(() => {
  return services.slice(0, 4)
})

onMounted(() => {
  if (localStorage.getItem('showLoginSuccess') === 'true') {
    localStorage.removeItem('showLoginSuccess')
    showSuccessMessage.value = true
    setTimeout(() => {
      showSuccessMessage.value = false
    }, 3000)
  }
})

const handleSearch = () => {
  if (searchQuery.value.trim()) {
    router.push(`/services?search=${encodeURIComponent(searchQuery.value)}`)
  }
}

const goToServices = (category) => {
  router.push(`/services?category=${encodeURIComponent(category)}`)
}

const goToServiceDetail = (id) => {
  router.push(`/services/${id}`)
}
</script>

<style scoped>
.home {
  padding-bottom: 0;
}

.hero {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 80px 0;
  text-align: center;
  color: white;
}

.hero-title {
  font-size: 3rem;
  margin-bottom: 15px;
  font-weight: bold;
}

.hero-subtitle {
  font-size: 1.3rem;
  margin-bottom: 40px;
  opacity: 0.9;
}

.hero-search {
  display: flex;
  max-width: 600px;
  margin: 0 auto;
  background: white;
  border-radius: 50px;
  overflow: hidden;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.2);
}

.hero-search input {
  flex: 1;
  padding: 18px 25px;
  border: none;
  font-size: 1rem;
  outline: none;
}

.search-btn {
  padding: 18px 35px;
  background: linear-gradient(135deg, #3498db, #2ecc71);
  color: white;
  font-size: 1rem;
  font-weight: bold;
  transition: all 0.3s ease;
}

.search-btn:hover {
  background: linear-gradient(135deg, #2980b9, #27ae60);
}

.section-title {
  text-align: center;
  font-size: 2rem;
  margin-bottom: 40px;
  color: #2c3e50;
  position: relative;
}

.section-title::after {
  content: '';
  position: absolute;
  bottom: -10px;
  left: 50%;
  transform: translateX(-50%);
  width: 80px;
  height: 3px;
  background: linear-gradient(135deg, #3498db, #2ecc71);
  border-radius: 2px;
}

.categories-section,
.featured-services,
.features-section {
  padding: 60px 0;
}

.categories-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 20px;
}

.category-card {
  background: white;
  border-radius: 15px;
  padding: 30px 20px;
  text-align: center;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 5px 20px rgba(0, 0, 0, 0.08);
}

.category-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 15px 30px rgba(0, 0, 0, 0.15);
}

.category-icon {
  font-size: 3rem;
  margin-bottom: 15px;
  display: block;
}

.category-name {
  font-size: 1.1rem;
  margin-bottom: 8px;
  color: #2c3e50;
}

.category-count {
  font-size: 0.9rem;
  color: #7f8c8d;
}

.services-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 25px;
}

.service-card {
  background: white;
  border-radius: 15px;
  overflow: hidden;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 5px 20px rgba(0, 0, 0, 0.08);
  display: flex;
}

.service-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 15px 30px rgba(0, 0, 0, 0.15);
}

.service-icon {
  font-size: 3rem;
  padding: 30px 20px;
  background: linear-gradient(135deg, #f5f7fa, #e4e8eb);
  display: flex;
  align-items: center;
  justify-content: center;
}

.service-info {
  flex: 1;
  padding: 20px;
  display: flex;
  flex-direction: column;
}

.service-name {
  font-size: 1.2rem;
  margin-bottom: 5px;
  color: #2c3e50;
}

.service-category {
  font-size: 0.85rem;
  color: #3498db;
  margin-bottom: 10px;
}

.service-description {
  font-size: 0.9rem;
  color: #7f8c8d;
  margin-bottom: 15px;
  flex: 1;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.service-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.service-price {
  font-size: 1.1rem;
  font-weight: bold;
  color: #e74c3c;
}

.service-rating {
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

.view-all {
  text-align: center;
  margin-top: 40px;
}

.view-all-btn {
  display: inline-block;
  padding: 12px 30px;
  background: linear-gradient(135deg, #3498db, #2ecc71);
  color: white;
  border-radius: 50px;
  font-weight: bold;
  transition: all 0.3s ease;
}

.view-all-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 10px 20px rgba(52, 152, 219, 0.3);
}

.features-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 30px;
}

.feature-item {
  text-align: center;
  padding: 30px 20px;
}

.feature-icon {
  font-size: 3rem;
  margin-bottom: 20px;
  display: block;
}

.feature-title {
  font-size: 1.2rem;
  margin-bottom: 10px;
  color: #2c3e50;
}

.feature-description {
  font-size: 0.95rem;
  color: #7f8c8d;
  line-height: 1.6;
}

.toast-message {
  position: fixed;
  top: 80px;
  right: 20px;
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 16px 24px;
  border-radius: 10px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.2);
  z-index: 9999;
  animation: toast-in 0.3s ease-out, toast-out 0.3s ease-in 2.7s forwards;
}

.toast-success {
  background: linear-gradient(135deg, #27ae60, #2ecc71);
  color: white;
}

.toast-icon {
  font-size: 1.2rem;
  font-weight: bold;
}

.toast-text {
  font-size: 1rem;
  font-weight: 500;
}

@keyframes toast-in {
  from {
    opacity: 0;
    transform: translateX(100px);
  }
  to {
    opacity: 1;
    transform: translateX(0);
  }
}

@keyframes toast-out {
  from {
    opacity: 1;
    transform: translateX(0);
  }
  to {
    opacity: 0;
    transform: translateX(100px);
  }
}

@media (max-width: 768px) {
  .hero {
    padding: 50px 0;
  }

  .hero-title {
    font-size: 2rem;
  }

  .hero-subtitle {
    font-size: 1rem;
  }

  .hero-search {
    flex-direction: column;
    border-radius: 10px;
    margin: 0 20px;
  }

  .hero-search input {
    padding: 15px 20px;
  }

  .search-btn {
    padding: 15px;
  }

  .section-title {
    font-size: 1.5rem;
  }

  .categories-section,
  .featured-services,
  .features-section {
    padding: 40px 0;
  }

  .service-card {
    flex-direction: column;
  }

  .service-icon {
    padding: 20px;
  }
}
</style>
