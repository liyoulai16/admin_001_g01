<template>
  <div class="home">
    <Transition name="toast">
      <div v-if="showSuccessMessage" class="toast-container">
        <div class="toast-message toast-success">
          <div class="toast-icon-wrapper">
            <svg class="toast-icon" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
              <path d="M20 6L9 17L4 12" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round"/>
            </svg>
          </div>
          <div class="toast-content">
            <span class="toast-title">登录成功</span>
            <span class="toast-subtitle">欢迎回来，{{ displayName }}！</span>
          </div>
          <div class="toast-progress">
            <div class="toast-progress-bar"></div>
          </div>
        </div>
      </div>
    </Transition>

    <section class="hero">
      <div class="particles">
        <div v-for="i in 30" :key="i" class="particle" :style="getParticleStyle(i)"></div>
      </div>
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

    <section class="carousel-section">
      <div class="container">
        <h2 class="section-title">精选服务</h2>
        <div class="carousel-container">
          <div 
            class="carousel-slide"
            v-for="(slide, index) in slides" 
            :key="index"
            :class="{ 
              'active': currentSlide === index,
              'prev': currentSlide === (index - 1 + slides.length) % slides.length,
              'next': currentSlide === (index + 1) % slides.length
            }"
          >
            <div class="slide-content">
              <img :src="slide.image" :alt="slide.title" class="slide-image" />
              <div class="slide-overlay">
                <h3 class="slide-title">{{ slide.title }}</h3>
                <p class="slide-description">{{ slide.description }}</p>
                <button class="slide-btn" @click="goToServices(slide.category)">了解更多</button>
              </div>
            </div>
          </div>
          
          <div class="carousel-controls">
            <button class="carousel-btn prev-btn" @click="prevSlide">
              <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                <path d="M15 18L9 12L15 6" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              </svg>
            </button>
            <button class="carousel-btn next-btn" @click="nextSlide">
              <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                <path d="M9 18L15 12L9 6" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              </svg>
            </button>
          </div>
          
          <div class="carousel-indicators">
            <span 
              v-for="(_, index) in slides" 
              :key="index"
              class="indicator"
              :class="{ active: currentSlide === index }"
              @click="goToSlide(index)"
            ></span>
          </div>
        </div>
      </div>
    </section>

    <section class="about-section">
      <div class="container">
        <div class="about-content">
          <div class="about-text">
            <h2 class="section-title">关于我们</h2>
            <div class="about-description">
              <p>
                社区生活服务平台致力于为您提供全方位的社区生活服务解决方案。我们汇聚了社区周边各类优质服务资源，
                从家政保洁、维修服务到教育培训、健康医疗，让您足不出户就能享受到便捷、高效、专业的服务体验。
              </p>
              <p>
                我们的使命是让社区生活更便捷、更温暖。通过智能化的服务匹配系统，我们能够快速为您找到最适合的服务提供商，
                同时提供安全保障和质量监督，确保每一次服务都让您满意。
              </p>
              <p>
                加入我们，体验全新的社区生活方式。无论是日常生活中的小事，还是重要的家庭需求，
                我们都将竭诚为您服务，让您的生活更加轻松愉快。
              </p>
            </div>
            <div class="about-stats">
              <div class="stat-item">
                <span class="stat-number">1000+</span>
                <span class="stat-label">服务项目</span>
              </div>
              <div class="stat-item">
                <span class="stat-number">500+</span>
                <span class="stat-label">合作商家</span>
              </div>
              <div class="stat-item">
                <span class="stat-number">10万+</span>
                <span class="stat-label">服务用户</span>
              </div>
              <div class="stat-item">
                <span class="stat-number">98%</span>
                <span class="stat-label">好评率</span>
              </div>
            </div>
          </div>
          <div class="about-image">
            <img 
              src="https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=community%20life%20service%20platform%20showcase%20with%20diverse%20services%20like%20cleaning%2C%20repair%2C%20education%2C%20healthcare%20in%20modern%20urban%20setting&image_size=landscape_16_9" 
              alt="社区生活服务平台" 
            />
          </div>
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

    <section class="cta-section">
      <div class="container">
        <div class="cta-content">
          <h2 class="cta-title">准备好体验便捷的社区生活服务了吗？</h2>
          <p class="cta-subtitle">立即浏览我们的服务，让生活更轻松</p>
          <div class="cta-buttons">
            <router-link to="/services" class="cta-btn primary-btn">浏览服务</router-link>
            <router-link to="/contact" class="cta-btn secondary-btn">联系我们</router-link>
          </div>
        </div>
      </div>
    </section>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const searchQuery = ref('')
const showSuccessMessage = ref(false)
const currentSlide = ref(0)
let autoPlayInterval = null

const slides = ref([
  {
    title: '家政保洁服务',
    description: '专业保洁团队，让您的家焕然一新。日常保洁、深度清洁、开荒保洁等多种服务可选。',
    image: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=professional%20home%20cleaning%20service%20with%20cleaners%20working%20in%20modern%20apartment&image_size=landscape_16_9',
    category: '家政服务'
  },
  {
    title: '家电维修服务',
    description: '经验丰富的维修师傅，快速解决您的家电问题。空调、冰箱、洗衣机等各类家电维修。',
    image: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=professional%20appliance%20repair%20technician%20fixing%20air%20conditioner%20in%20modern%20home&image_size=landscape_16_9',
    category: '维修服务'
  },
  {
    title: '教育培训服务',
    description: '优质的教育资源，助力孩子成长。课外辅导、兴趣培养、职业技能培训等多种课程。',
    image: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=modern%20education%20tutoring%20service%20with%20teacher%20helping%20students%20in%20bright%20classroom&image_size=landscape_16_9',
    category: '教育培训'
  },
  {
    title: '健康医疗服务',
    description: '专业的健康管理服务，守护您和家人的健康。在线问诊、健康咨询、体检预约等服务。',
    image: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=professional%20healthcare%20medical%20service%20with%20doctor%20consulting%20patient%20in%20modern%20clinic&image_size=landscape_16_9',
    category: '健康医疗'
  }
])

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

const displayName = computed(() => {
  return localStorage.getItem('nickname') || localStorage.getItem('username') || '用户'
})

const startAutoPlay = () => {
  autoPlayInterval = setInterval(() => {
    nextSlide()
  }, 5000)
}

const stopAutoPlay = () => {
  if (autoPlayInterval) {
    clearInterval(autoPlayInterval)
    autoPlayInterval = null
  }
}

const nextSlide = () => {
  currentSlide.value = (currentSlide.value + 1) % slides.value.length
}

const prevSlide = () => {
  currentSlide.value = (currentSlide.value - 1 + slides.value.length) % slides.value.length
}

const goToSlide = (index) => {
  currentSlide.value = index
  stopAutoPlay()
  startAutoPlay()
}

const handleSearch = () => {
  if (searchQuery.value.trim()) {
    router.push(`/services?search=${encodeURIComponent(searchQuery.value)}`)
  }
}

const goToServices = (category) => {
  if (category) {
    router.push(`/services?category=${encodeURIComponent(category)}`)
  } else {
    router.push('/services')
  }
}

onMounted(() => {
  if (localStorage.getItem('showLoginSuccess') === 'true') {
    localStorage.removeItem('showLoginSuccess')
    showSuccessMessage.value = true
    setTimeout(() => {
      showSuccessMessage.value = false
    }, 4000)
  }
  startAutoPlay()
})

onUnmounted(() => {
  stopAutoPlay()
})
</script>

<style scoped>
.home {
  padding-bottom: 0;
}

.hero {
  background: linear-gradient(135deg, #8B7355 0%, #6B8E23 100%);
  padding: 80px 0;
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
    transform: translateY(-500px) rotate(720deg);
    opacity: 0;
  }
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
  background: linear-gradient(135deg, #6B8E23, #8FBC8F);
  color: white;
  font-size: 1rem;
  font-weight: bold;
  transition: all 0.3s ease;
}

.search-btn:hover {
  background: linear-gradient(135deg, #556B2F, #5D7C4A);
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
  background: linear-gradient(135deg, #6B8E23, #8FBC8F);
  border-radius: 2px;
}

.carousel-section {
  padding: 80px 0;
  background: linear-gradient(180deg, #f8f9fa 0%, #ffffff 100%);
}

.carousel-container {
  position: relative;
  max-width: 1000px;
  margin: 0 auto;
  height: 500px;
  perspective: 1500px;
}

.carousel-slide {
  position: absolute;
  width: 70%;
  height: 100%;
  left: 15%;
  transition: all 0.8s cubic-bezier(0.4, 0, 0.2, 1);
  transform-style: preserve-3d;
  opacity: 0;
  transform: translateX(0) scale(0.8);
}

.carousel-slide.active {
  opacity: 1;
  transform: translateX(0) scale(1) translateZ(50px);
  z-index: 10;
}

.carousel-slide.prev {
  opacity: 0.6;
  transform: translateX(-80%) scale(0.7) rotateY(15deg);
  z-index: 5;
}

.carousel-slide.next {
  opacity: 0.6;
  transform: translateX(80%) scale(0.7) rotateY(-15deg);
  z-index: 5;
}

.slide-content {
  position: relative;
  width: 100%;
  height: 100%;
  border-radius: 20px;
  overflow: hidden;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
}

.slide-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.slide-overlay {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  background: linear-gradient(transparent, rgba(0, 0, 0, 0.8));
  padding: 40px;
  color: white;
}

.slide-title {
  font-size: 1.8rem;
  margin-bottom: 10px;
  font-weight: bold;
  animation: slide-up 0.6s ease-out;
}

.slide-description {
  font-size: 1rem;
  margin-bottom: 20px;
  opacity: 0.9;
  line-height: 1.6;
  animation: slide-up 0.6s ease-out 0.1s backwards;
}

.slide-btn {
  padding: 12px 30px;
  background: linear-gradient(135deg, #6B8E23, #8FBC8F);
  color: white;
  border-radius: 25px;
  font-weight: bold;
  transition: all 0.3s ease;
  animation: slide-up 0.6s ease-out 0.2s backwards;
}

.slide-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 10px 30px rgba(107, 142, 35, 0.5);
}

@keyframes slide-up {
  from {
    opacity: 0;
    transform: translateY(30px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.carousel-controls {
  position: absolute;
  top: 50%;
  left: 0;
  right: 0;
  transform: translateY(-50%);
  display: flex;
  justify-content: space-between;
  z-index: 20;
  pointer-events: none;
}

.carousel-btn {
  width: 50px;
  height: 50px;
  background: white;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 5px 20px rgba(0, 0, 0, 0.2);
  transition: all 0.3s ease;
  pointer-events: auto;
  cursor: pointer;
}

.carousel-btn:hover {
  background: linear-gradient(135deg, #6B8E23, #8FBC8F);
  color: white;
  transform: scale(1.1);
}

.carousel-btn svg {
  width: 24px;
  height: 24px;
}

.prev-btn {
  margin-left: -25px;
}

.next-btn {
  margin-right: -25px;
}

.carousel-indicators {
  position: absolute;
  bottom: -50px;
  left: 50%;
  transform: translateX(-50%);
  display: flex;
  gap: 10px;
  z-index: 20;
}

.indicator {
  width: 12px;
  height: 12px;
  border-radius: 50%;
  background: #c4c4c4;
  transition: all 0.3s ease;
  cursor: pointer;
}

.indicator.active {
  background: linear-gradient(135deg, #6B8E23, #8FBC8F);
  width: 30px;
  border-radius: 6px;
}

.indicator:hover {
  background: #6B8E23;
}

.about-section {
  padding: 80px 0;
  background: white;
}

.about-content {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 60px;
  align-items: center;
}

.about-text .section-title {
  text-align: left;
  margin-bottom: 30px;
}

.about-text .section-title::after {
  left: 0;
  transform: none;
}

.about-description {
  margin-bottom: 40px;
}

.about-description p {
  font-size: 1rem;
  color: #555;
  line-height: 1.8;
  margin-bottom: 20px;
}

.about-description p:last-child {
  margin-bottom: 0;
}

.about-stats {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
}

.stat-item {
  text-align: center;
  padding: 20px 10px;
  background: linear-gradient(135deg, #f5f7fa, #e4e8eb);
  border-radius: 15px;
  transition: all 0.3s ease;
}

.stat-item:hover {
  transform: translateY(-5px);
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1);
}

.stat-number {
  display: block;
  font-size: 2rem;
  font-weight: bold;
  color: #6B8E23;
  margin-bottom: 5px;
}

.stat-label {
  font-size: 0.9rem;
  color: #7f8c8d;
}

.about-image {
  position: relative;
}

.about-image img {
  width: 100%;
  border-radius: 20px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.15);
  transition: all 0.3s ease;
}

.about-image img:hover {
  transform: scale(1.02);
}

.about-image::before {
  content: '';
  position: absolute;
  top: 20px;
  left: 20px;
  right: -20px;
  bottom: -20px;
  background: linear-gradient(135deg, #6B8E23, #8FBC8F);
  border-radius: 20px;
  z-index: -1;
  opacity: 0.3;
}

.features-section {
  padding: 80px 0;
  background: linear-gradient(180deg, #ffffff 0%, #f8f9fa 100%);
}

.features-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 30px;
}

.feature-item {
  text-align: center;
  padding: 40px 30px;
  background: white;
  border-radius: 20px;
  box-shadow: 0 5px 20px rgba(0, 0, 0, 0.08);
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
}

.feature-item::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 4px;
  background: linear-gradient(135deg, #6B8E23, #8FBC8F);
  transform: scaleX(0);
  transition: transform 0.3s ease;
}

.feature-item:hover::before {
  transform: scaleX(1);
}

.feature-item:hover {
  transform: translateY(-10px);
  box-shadow: 0 15px 40px rgba(0, 0, 0, 0.15);
}

.feature-icon {
  font-size: 3.5rem;
  margin-bottom: 20px;
  display: block;
  animation: bounce 2s infinite;
}

@keyframes bounce {
  0%, 100% {
    transform: translateY(0);
  }
  50% {
    transform: translateY(-10px);
  }
}

.feature-title {
  font-size: 1.3rem;
  margin-bottom: 15px;
  color: #2c3e50;
  font-weight: bold;
}

.feature-description {
  font-size: 0.95rem;
  color: #7f8c8d;
  line-height: 1.6;
}

.cta-section {
  padding: 80px 0;
  background: linear-gradient(135deg, #8B7355 0%, #6B8E23 100%);
  text-align: center;
  position: relative;
  overflow: hidden;
}

.cta-section::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: url("data:image/svg+xml,%3Csvg width='60' height='60' viewBox='0 0 60 60' xmlns='http://www.w3.org/2000/svg'%3E%3Cg fill='none' fill-rule='evenodd'%3E%3Cg fill='%23ffffff' fill-opacity='0.05'%3E%3Cpath d='M36 34v-4h-2v4h-4v2h4v4h2v-4h4v-2h-4zm0-30V0h-2v4h-4v2h4v4h2V6h4V4h-4zM6 34v-4H4v4H0v2h4v4h2v-4h4v-2H6zM6 4V0H4v4H0v2h4v4h2V6h4V4H6z'/%3E%3C/g%3E%3C/g%3E%3C/svg%3E");
  pointer-events: none;
}

.cta-content {
  position: relative;
  z-index: 1;
}

.cta-title {
  font-size: 2.5rem;
  color: white;
  margin-bottom: 15px;
  font-weight: bold;
}

.cta-subtitle {
  font-size: 1.2rem;
  color: rgba(255, 255, 255, 0.9);
  margin-bottom: 40px;
}

.cta-buttons {
  display: flex;
  justify-content: center;
  gap: 20px;
}

.cta-btn {
  padding: 15px 40px;
  border-radius: 50px;
  font-weight: bold;
  font-size: 1.1rem;
  transition: all 0.3s ease;
}

.primary-btn {
  background: white;
  color: #6B8E23;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.2);
}

.primary-btn:hover {
  transform: translateY(-3px);
  box-shadow: 0 15px 40px rgba(0, 0, 0, 0.3);
}

.secondary-btn {
  background: transparent;
  color: white;
  border: 2px solid white;
}

.secondary-btn:hover {
  background: white;
  color: #6B8E23;
  transform: translateY(-3px);
}

.toast-container {
  position: fixed;
  top: 80px;
  right: 20px;
  z-index: 9999;
}

.toast-message {
  display: flex;
  align-items: center;
  gap: 14px;
  padding: 18px 22px;
  border-radius: 14px;
  background: white;
  box-shadow: 
    0 8px 32px rgba(0, 0, 0, 0.12),
    0 2px 8px rgba(0, 0, 0, 0.08),
    0 0 0 1px rgba(0, 0, 0, 0.05);
  position: relative;
  overflow: hidden;
  min-width: 280px;
}

.toast-success {
  border-left: 4px solid #5D7C4A;
}

.toast-icon-wrapper {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background: linear-gradient(135deg, #5D7C4A, #8FBC8F);
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  box-shadow: 0 4px 12px rgba(39, 174, 96, 0.3);
}

.toast-icon {
  width: 22px;
  height: 22px;
  color: white;
}

.toast-content {
  display: flex;
  flex-direction: column;
  gap: 2px;
  flex: 1;
}

.toast-title {
  font-size: 1rem;
  font-weight: 600;
  color: #2c3e50;
}

.toast-subtitle {
  font-size: 0.875rem;
  color: #7f8c8d;
  font-weight: 400;
}

.toast-progress {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  height: 3px;
  background: #f0f0f0;
}

.toast-progress-bar {
  height: 100%;
  background: linear-gradient(90deg, #5D7C4A, #8FBC8F);
  width: 100%;
  animation: progress-shrink 3.8s linear forwards;
}

@keyframes progress-shrink {
  from {
    width: 100%;
  }
  to {
    width: 0%;
  }
}

.toast-enter-active {
  animation: toast-in 0.4s cubic-bezier(0.4, 0, 0.2, 1);
}

.toast-leave-active {
  animation: toast-out 0.3s cubic-bezier(0.4, 0, 1, 1);
}

@keyframes toast-in {
  from {
    opacity: 0;
    transform: translateX(100%) translateY(0);
  }
  to {
    opacity: 1;
    transform: translateX(0) translateY(0);
  }
}

@keyframes toast-out {
  from {
    opacity: 1;
    transform: translateX(0) translateY(0);
  }
  to {
    opacity: 0;
    transform: translateX(100%) translateY(0);
  }
}

@media (max-width: 992px) {
  .about-content {
    grid-template-columns: 1fr;
    gap: 40px;
  }
  
  .about-text .section-title {
    text-align: center;
  }
  
  .about-text .section-title::after {
    left: 50%;
    transform: translateX(-50%);
  }
  
  .about-image {
    order: -1;
  }
  
  .about-image::before {
    display: none;
  }
  
  .carousel-slide {
    width: 80%;
    left: 10%;
  }
  
  .carousel-slide.prev,
  .carousel-slide.next {
    opacity: 0;
  }
  
  .prev-btn {
    margin-left: 10px;
  }
  
  .next-btn {
    margin-right: 10px;
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

  .carousel-section,
  .about-section,
  .features-section,
  .cta-section {
    padding: 50px 0;
  }

  .carousel-container {
    height: 400px;
  }

  .carousel-slide {
    width: 90%;
    left: 5%;
  }

  .slide-overlay {
    padding: 25px 20px;
  }

  .slide-title {
    font-size: 1.4rem;
  }

  .slide-description {
    font-size: 0.9rem;
  }

  .about-stats {
    grid-template-columns: repeat(2, 1fr);
    gap: 15px;
  }

  .stat-number {
    font-size: 1.5rem;
  }

  .cta-title {
    font-size: 1.8rem;
  }

  .cta-subtitle {
    font-size: 1rem;
  }

  .cta-buttons {
    flex-direction: column;
    align-items: center;
  }

  .cta-btn {
    width: 100%;
    max-width: 250px;
  }
}
</style>
