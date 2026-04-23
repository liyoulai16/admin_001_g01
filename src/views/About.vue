<template>
  <div class="about">
    <div class="page-header">
      <div class="particles">
        <div v-for="i in 30" :key="i" class="particle" :style="getParticleStyle(i)"></div>
      </div>
      <div class="container">
        <h1 class="page-title">关于我们</h1>
        <p class="page-subtitle">了解社区生活服务平台</p>
      </div>
    </div>

    <div class="container">
      <section class="about-section" v-if="mission">
        <div class="about-content">
          <div class="about-text">
            <h2 class="section-title">{{ mission.title || '我们的使命' }}</h2>
            <p class="about-description" v-if="mission.description">
              {{ mission.description }}
            </p>
            <p class="about-description" v-else>
              社区生活服务平台致力于为社区居民提供便捷、优质的生活服务。我们相信，通过整合社区资源，可以让每一位居民的生活更加便利、更加美好。
            </p>
          </div>
          <div class="about-image">
            <div class="image-placeholder" :class="{ 'has-image': hasImageUrl }">
              <div class="image-display" v-if="hasImageUrl">
                <img :src="mission.imageUrl" alt="图片" @error="handleImageError" />
              </div>
              <template v-else>
                <span class="image-icon">{{ mission.imageUrl || '🏘️' }}</span>
                <span class="image-text">{{ mission.imageText || '社区服务中心' }}</span>
              </template>
            </div>
          </div>
        </div>
      </section>

      <section class="values-section" v-if="values.length > 0">
        <h2 class="section-title">我们的价值观</h2>
        <div class="values-grid">
          <div class="value-item" v-for="value in values" :key="value.id">
            <span class="value-icon">{{ value.icon || '⭐' }}</span>
            <h3 class="value-title">{{ value.title || '价值观' }}</h3>
            <p class="value-description" v-if="value.description">
              {{ value.description }}
            </p>
            <p class="value-description" v-else>
              提供优质的生活服务，让社区生活更美好。
            </p>
          </div>
        </div>
      </section>

      <section class="stats-section" v-if="stats.length > 0">
        <h2 class="section-title">平台数据</h2>
        <div class="stats-grid">
          <div class="stat-item" v-for="stat in stats" :key="stat.id">
            <span class="stat-number">{{ stat.numberText || '0' }}</span>
            <span class="stat-label">{{ stat.label || '数据' }}</span>
          </div>
        </div>
      </section>

      <section class="team-section" v-if="teams.length > 0">
        <h2 class="section-title">核心团队</h2>
        <div class="team-grid">
          <div class="team-member" v-for="member in teams" :key="member.id">
            <div class="member-avatar">{{ member.avatar || '👤' }}</div>
            <h3 class="member-name">{{ member.name || '成员' }}</h3>
            <p class="member-role">{{ member.role || '职位' }}</p>
            <p class="member-bio" v-if="member.bio">
              {{ member.bio }}
            </p>
            <p class="member-bio" v-else>
              致力于为用户提供优质的服务体验。
            </p>
          </div>
        </div>
      </section>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { request } from '../utils/request'

const mission = ref(null)
const values = ref([])
const stats = ref([])
const teams = ref([])

const hasImageUrl = computed(() => {
  if (!mission.value || !mission.value.imageUrl) {
    return false
  }
  const url = mission.value.imageUrl.trim()
  return url.startsWith('http://') || url.startsWith('https://')
})

const handleImageError = (e) => {
  e.target.style.display = 'none'
  e.target.parentElement.innerHTML = '<span class="image-icon">🏘️</span>'
}

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

const fetchAboutData = async () => {
  try {
    const response = await request('/api/about/data')
    const data = await response.json()
    
    if (data.code === 200 || data.success) {
      const result = data.data || data.result || data
      
      if (result.mission) {
        mission.value = result.mission
      }
      
      if (result.values && Array.isArray(result.values)) {
        values.value = result.values
      }
      
      if (result.stats && Array.isArray(result.stats)) {
        stats.value = result.stats
      }
      
      if (result.teams && Array.isArray(result.teams)) {
        teams.value = result.teams
      }
    }
  } catch (error) {
    console.error('Failed to fetch about data:', error)
  }
}

onMounted(() => {
  fetchAboutData()
})
</script>

<style scoped>
.about {
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

.about-section,
.values-section,
.stats-section,
.team-section {
  padding: 60px 0;
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

.about-content {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 50px;
  align-items: center;
}

.about-text {
  padding: 20px;
}

.about-text .section-title {
  text-align: left;
  margin-bottom: 20px;
}

.about-text .section-title::after {
  left: 0;
  transform: none;
}

.about-description {
  font-size: 1rem;
  line-height: 1.8;
  color: #555;
  margin-bottom: 20px;
}

.about-image {
  display: flex;
  justify-content: center;
}

.image-placeholder {
  width: 100%;
  max-width: 400px;
  min-height: 300px;
  background: linear-gradient(135deg, #f5f7fa, #e4e8eb);
  border-radius: 15px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1);
  overflow: hidden;
}

.image-placeholder.has-image {
  padding: 0;
  min-height: auto;
  height: auto;
}

.image-display {
  width: 100%;
}

.image-display img {
  width: 100%;
  height: auto;
  display: block;
  border-radius: 15px;
}

.image-icon {
  font-size: 5rem;
  margin-bottom: 15px;
}

.image-text {
  font-size: 1.1rem;
  color: #7f8c8d;
}

.values-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 30px;
}

.value-item {
  background: white;
  border-radius: 15px;
  padding: 30px;
  text-align: center;
  box-shadow: 0 5px 20px rgba(0, 0, 0, 0.08);
  transition: all 0.3s ease;
}

.value-item:hover {
  transform: translateY(-5px);
  box-shadow: 0 15px 30px rgba(0, 0, 0, 0.15);
}

.value-icon {
  font-size: 3rem;
  margin-bottom: 20px;
  display: block;
}

.value-title {
  font-size: 1.2rem;
  margin-bottom: 15px;
  color: #2c3e50;
}

.value-description {
  font-size: 0.95rem;
  color: #7f8c8d;
  line-height: 1.6;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 30px;
}

.stat-item {
  background: linear-gradient(135deg, #8B7355 0%, #6B8E23 100%);
  border-radius: 15px;
  padding: 30px;
  text-align: center;
  color: white;
}

.stat-number {
  font-size: 2.5rem;
  font-weight: bold;
  margin-bottom: 10px;
  display: block;
}

.stat-label {
  font-size: 1rem;
  opacity: 0.9;
}

.team-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 30px;
}

.team-member {
  background: white;
  border-radius: 15px;
  padding: 30px;
  text-align: center;
  box-shadow: 0 5px 20px rgba(0, 0, 0, 0.08);
  transition: all 0.3s ease;
}

.team-member:hover {
  transform: translateY(-5px);
  box-shadow: 0 15px 30px rgba(0, 0, 0, 0.15);
}

.member-avatar {
  font-size: 4rem;
  margin-bottom: 15px;
  display: block;
}

.member-name {
  font-size: 1.2rem;
  margin-bottom: 5px;
  color: #2c3e50;
}

.member-role {
  font-size: 0.9rem;
  color: #6B8E23;
  margin-bottom: 15px;
}

.member-bio {
  font-size: 0.9rem;
  color: #7f8c8d;
  line-height: 1.6;
}

@media (max-width: 768px) {
  .page-header {
    padding: 30px 0;
  }

  .page-title {
    font-size: 1.8rem;
  }

  .about-section,
  .values-section,
  .stats-section,
  .team-section {
    padding: 40px 0;
  }

  .section-title {
    font-size: 1.5rem;
  }

  .about-content {
    grid-template-columns: 1fr;
    gap: 30px;
  }

  .about-text .section-title {
    text-align: center;
  }

  .about-text .section-title::after {
    left: 50%;
    transform: translateX(-50%);
  }

  .image-placeholder {
    height: 250px;
  }

  .stat-number {
    font-size: 2rem;
  }
}
</style>
