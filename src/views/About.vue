<template>
  <div class="about">
    <div class="page-header">
      <div class="container">
        <h1 class="page-title">关于我们</h1>
        <p class="page-subtitle">了解社区生活服务平台</p>
      </div>
    </div>

    <div class="container">
      <section class="about-section">
        <div class="about-content">
          <div class="about-text">
            <h2 class="section-title">我们的使命</h2>
            <p class="about-description">
              社区生活服务平台致力于为社区居民提供便捷、优质的生活服务。我们相信，通过整合社区资源，
              可以让每一位居民的生活更加便利、更加美好。
            </p>
            <p class="about-description">
              我们的平台连接了社区居民与各类服务提供者，包括家政服务、维修服务、配送服务等。
              通过严格的服务质量把控和用户评价体系，我们确保每一项服务都能达到高标准。
            </p>
          </div>
          <div class="about-image">
            <div class="image-placeholder">
              <span class="image-icon">🏘️</span>
              <span class="image-text">社区服务中心</span>
            </div>
          </div>
        </div>
      </section>

      <section class="values-section">
        <h2 class="section-title">我们的价值观</h2>
        <div class="values-grid">
          <div class="value-item">
            <span class="value-icon">🎯</span>
            <h3 class="value-title">用户至上</h3>
            <p class="value-description">
              始终将用户需求放在首位，不断优化服务体验，让每一位用户都能感受到贴心的服务。
            </p>
          </div>
          <div class="value-item">
            <span class="value-icon">⭐</span>
            <h3 class="value-title">品质保证</h3>
            <p class="value-description">
              严格筛选服务提供者，建立完善的质量监控体系，确保每一项服务都达到高品质标准。
            </p>
          </div>
          <div class="value-item">
            <span class="value-icon">🤝</span>
            <h3 class="value-title">诚信经营</h3>
            <p class="value-description">
              坚持透明、公正的经营理念，明码标价，无隐藏费用，让用户消费得明明白白。
            </p>
          </div>
          <div class="value-item">
            <span class="value-icon">🚀</span>
            <h3 class="value-title">持续创新</h3>
            <p class="value-description">
              不断探索新的服务模式和技术应用，为用户提供更加便捷、智能的生活服务体验。
            </p>
          </div>
        </div>
      </section>

      <section class="stats-section">
        <h2 class="section-title">平台数据</h2>
        <div class="stats-grid">
          <div class="stat-item">
            <span class="stat-number">50,000+</span>
            <span class="stat-label">注册用户</span>
          </div>
          <div class="stat-item">
            <span class="stat-number">1,000+</span>
            <span class="stat-label">服务提供者</span>
          </div>
          <div class="stat-item">
            <span class="stat-number">100+</span>
            <span class="stat-label">服务项目</span>
          </div>
          <div class="stat-item">
            <span class="stat-number">98%</span>
            <span class="stat-label">用户满意度</span>
          </div>
        </div>
      </section>

      <section class="team-section">
        <h2 class="section-title">核心团队</h2>
        <div class="team-grid">
          <div class="team-member">
            <div class="member-avatar">👨‍💼</div>
            <h3 class="member-name">张明</h3>
            <p class="member-role">创始人 & CEO</p>
            <p class="member-bio">
              拥有10年互联网行业经验，致力于打造最优质的社区生活服务平台。
            </p>
          </div>
          <div class="team-member">
            <div class="member-avatar">👩‍💻</div>
            <h3 class="member-name">李华</h3>
            <p class="member-role">技术总监</p>
            <p class="member-bio">
              资深全栈工程师，负责平台技术架构设计和产品开发。
            </p>
          </div>
          <div class="team-member">
            <div class="member-avatar">👨‍🎨</div>
            <h3 class="member-name">王强</h3>
            <p class="member-role">产品总监</p>
            <p class="member-bio">
              专注用户体验设计，致力于打造简洁易用的产品界面。
            </p>
          </div>
          <div class="team-member">
            <div class="member-avatar">👩‍💼</div>
            <h3 class="member-name">陈静</h3>
            <p class="member-role">运营总监</p>
            <p class="member-bio">
              负责平台运营和用户增长，确保服务质量和用户满意度。
            </p>
          </div>
        </div>
      </section>
    </div>
  </div>
</template>

<script setup>
</script>

<style scoped>
.about {
  min-height: calc(100vh - 200px);
}

.page-header {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 50px 0;
  text-align: center;
  color: white;
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
  background: linear-gradient(135deg, #3498db, #2ecc71);
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
  height: 300px;
  background: linear-gradient(135deg, #f5f7fa, #e4e8eb);
  border-radius: 15px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1);
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
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
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
  color: #3498db;
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
