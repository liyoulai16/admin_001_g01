<template>
  <footer class="footer">
    <div class="container">
      <div class="footer-content">
        <div class="footer-section">
          <h3 class="footer-title">社区生活服务</h3>
          <p class="footer-description">
            为您提供便捷的社区生活服务，让生活更美好。
          </p>
          <div class="social-links">
            <a href="#" class="social-link">微信</a>
            <a href="#" class="social-link">微博</a>
            <a href="#" class="social-link">QQ</a>
          </div>
        </div>
        <div class="footer-section">
          <h4 class="footer-subtitle">快速链接</h4>
          <ul class="footer-links">
            <li><router-link to="/">首页</router-link></li>
            <li><router-link to="/services">服务列表</router-link></li>
            <li><router-link to="/about">关于我们</router-link></li>
            <li><router-link to="/contact">联系我们</router-link></li>
          </ul>
        </div>
        <div class="footer-section">
          <h4 class="footer-subtitle">服务项目</h4>
          <ul class="footer-links">
            <li v-for="category in categories" :key="category.id">
              <router-link :to="`/services?category=${encodeURIComponent(category.name)}`">
                {{ category.icon || '📋' }} {{ category.name }}
              </router-link>
            </li>
          </ul>
        </div>
        <div class="footer-section">
          <h4 class="footer-subtitle">联系方式</h4>
          <ul class="contact-info">
            <li>📞 电话：400-123-4567</li>
            <li>📧 邮箱：service@community.com</li>
            <li>📍 地址：幸福社区服务中心</li>
          </ul>
        </div>
      </div>
      <div class="footer-bottom">
        <p>&copy; 2024 社区生活服务平台. 保留所有权利.</p>
      </div>
    </div>
  </footer>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import request from '../utils/request'

const categories = ref([])

const fetchCategories = async () => {
  try {
    const response = await request('/api/services/categories', {
      method: 'GET'
    })
    
    if (response.ok) {
      const data = await response.json()
      if (data.code === 200 && data.data) {
        categories.value = data.data.slice(0, 4)
      }
    }
  } catch (error) {
    console.error('获取服务分类失败:', error)
    categories.value = [
      { id: 1, name: '家政服务', icon: '🧹' },
      { id: 2, name: '维修服务', icon: '🔧' },
      { id: 3, name: '配送服务', icon: '🚚' },
      { id: 4, name: '社区活动', icon: '🎉' }
    ]
  }
}

onMounted(() => {
  fetchCategories()
})
</script>

<style scoped>
.footer {
  background-color: #2c3e50;
  color: #ecf0f1;
  padding: 50px 0 20px;
  margin-top: auto;
}

.footer-content {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 30px;
  margin-bottom: 30px;
}

.footer-section {
  display: flex;
  flex-direction: column;
}

.footer-title {
  font-size: 1.3rem;
  margin-bottom: 15px;
  background: linear-gradient(135deg, #6B8E23, #8FBC8F);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.footer-description {
  font-size: 0.9rem;
  line-height: 1.6;
  margin-bottom: 15px;
  color: #bdc3c7;
}

.social-links {
  display: flex;
  gap: 15px;
}

.social-link {
  color: #bdc3c7;
  font-size: 0.9rem;
  transition: color 0.3s ease;
}

.social-link:hover {
  color: #6B8E23;
}

.footer-subtitle {
  font-size: 1.1rem;
  margin-bottom: 15px;
  color: #ecf0f1;
}

.footer-links {
  list-style: none;
  padding: 0;
}

.footer-links li {
  margin-bottom: 10px;
}

.footer-links a {
  color: #bdc3c7;
  font-size: 0.9rem;
  transition: color 0.3s ease;
}

.footer-links a:hover {
  color: #6B8E23;
}

.contact-info {
  list-style: none;
  padding: 0;
}

.contact-info li {
  margin-bottom: 10px;
  font-size: 0.9rem;
  color: #bdc3c7;
}

.footer-bottom {
  border-top: 1px solid #34495e;
  padding-top: 20px;
  text-align: center;
}

.footer-bottom p {
  font-size: 0.85rem;
  color: #95a5a6;
}

@media (max-width: 768px) {
  .footer-content {
    grid-template-columns: 1fr;
    gap: 20px;
  }
}
</style>
