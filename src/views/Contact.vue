<template>
  <div class="contact">
    <div class="page-header">
      <div class="container">
        <h1 class="page-title">联系我们</h1>
        <p class="page-subtitle">有任何问题？请随时与我们联系</p>
      </div>
    </div>

    <div class="container">
      <div class="contact-content">
        <div class="left-column">
          <div class="contact-cards-section">
            <h2 class="section-title">联系方式</h2>
            <div class="contact-cards">
              <div class="contact-card">
                <span class="contact-icon">📞</span>
                <h3 class="contact-title">客服热线</h3>
                <p class="contact-detail">400-123-4567</p>
                <p class="contact-hint">工作时间：09:00 - 18:00</p>
              </div>
              <div class="contact-card">
                <span class="contact-icon">📧</span>
                <h3 class="contact-title">电子邮箱</h3>
                <p class="contact-detail">service@community.com</p>
                <p class="contact-hint">24小时内回复</p>
              </div>
              <div class="contact-card">
                <span class="contact-icon">📍</span>
                <h3 class="contact-title">公司地址</h3>
                <p class="contact-detail">幸福社区服务中心</p>
                <p class="contact-hint">欢迎前来咨询</p>
              </div>
              <div class="contact-card">
                <span class="contact-icon">💬</span>
                <h3 class="contact-title">在线客服</h3>
                <p class="contact-detail">微信公众号</p>
                <p class="contact-hint">扫码关注咨询</p>
              </div>
            </div>
          </div>

          <div class="working-hours">
            <h3 class="subsection-title">工作时间</h3>
            <div class="hours-list">
              <div class="hours-item">
                <span class="hours-day">周一至周五</span>
                <span class="hours-time">09:00 - 18:00</span>
              </div>
              <div class="hours-item">
                <span class="hours-day">周六</span>
                <span class="hours-time">09:00 - 17:00</span>
              </div>
              <div class="hours-item">
                <span class="hours-day">周日</span>
                <span class="hours-time">10:00 - 16:00</span>
              </div>
            </div>
          </div>

          <div class="faq-section">
            <h3 class="subsection-title">常见问题</h3>
            <div class="faq-list">
              <div 
                v-for="(faq, index) in faqs" 
                :key="index" 
                class="faq-item"
                :class="{ open: openFaq === index }"
                @click="toggleFaq(index)"
              >
                <div class="faq-question">
                  <span class="faq-icon">{{ openFaq === index ? '−' : '+' }}</span>
                  <span class="faq-text">{{ faq.question }}</span>
                </div>
                <div class="faq-answer" v-show="openFaq === index">
                  {{ faq.answer }}
                </div>
              </div>
            </div>
          </div>
        </div>

        <div class="right-column">
          <div class="contact-form-section">
            <h2 class="section-title">在线留言</h2>
            <p class="form-intro">
              填写以下表单，我们会尽快与您联系。
            </p>
            <form class="contact-form" @submit.prevent="handleSubmit">
              <div class="form-fields">
                <div class="form-row">
                  <div class="form-group">
                    <label class="form-label">姓名 *</label>
                    <input 
                      type="text" 
                      v-model="formData.name" 
                      placeholder="请输入您的姓名"
                      class="form-input"
                      required
                    />
                  </div>
                  <div class="form-group">
                    <label class="form-label">电话 *</label>
                    <input 
                      type="tel" 
                      v-model="formData.phone" 
                      placeholder="请输入您的电话"
                      class="form-input"
                      required
                    />
                  </div>
                </div>
                <div class="form-group">
                  <label class="form-label">邮箱</label>
                  <input 
                    type="email" 
                    v-model="formData.email" 
                    placeholder="请输入您的邮箱"
                    class="form-input"
                  />
                </div>
                <div class="form-group">
                  <label class="form-label">咨询类型</label>
                  <select v-model="formData.type" class="form-select">
                    <option value="">请选择咨询类型</option>
                    <option value="service">服务咨询</option>
                    <option value="complaint">投诉建议</option>
                    <option value="cooperation">商务合作</option>
                    <option value="other">其他问题</option>
                  </select>
                </div>
                <div class="form-group">
                  <label class="form-label">留言内容 *</label>
                  <textarea 
                    v-model="formData.message" 
                    placeholder="请详细描述您的问题或需求..."
                    class="form-textarea"
                    rows="6"
                    required
                  ></textarea>
                </div>

                <div class="form-highlights">
                  <div class="highlight-item">
                    <span class="highlight-icon">✨</span>
                    <div class="highlight-content">
                      <h4>专业客服团队</h4>
                      <p>5年以上社区服务经验，熟悉各类问题处理流程</p>
                    </div>
                  </div>
                  <div class="highlight-item">
                    <span class="highlight-icon">📋</span>
                    <div class="highlight-content">
                      <h4>全程跟踪服务</h4>
                      <p>从提交到解决，每一步都有记录可查</p>
                    </div>
                  </div>
                </div>
              </div>

              <div class="form-footer">
                <div class="form-tips">
                  <div class="tip-item">
                    <span class="tip-icon">⏱️</span>
                    <span>提交后15分钟内回复</span>
                  </div>
                  <div class="tip-item">
                    <span class="tip-icon">🔒</span>
                    <span>信息安全加密保护</span>
                  </div>
                </div>

                <button type="submit" class="submit-btn">
                  提交留言
                </button>
              </div>
            </form>

            <div class="success-message" v-if="showSuccess">
              <span class="success-icon">✅</span>
              <h3>留言提交成功！</h3>
              <p>感谢您的留言，我们会尽快与您联系。</p>
            </div>
          </div>

          <div class="extra-info-section">
            <h3 class="section-title">为什么选择我们？</h3>
            <div class="extra-features">
              <div class="extra-feature">
                <span class="extra-icon">⚡</span>
                <div class="extra-content">
                  <h4>快速响应</h4>
                  <p>平均15分钟内回复您的咨询</p>
                </div>
              </div>
              <div class="extra-feature">
                <span class="extra-icon">🛡️</span>
                <div class="extra-content">
                  <h4>安全保障</h4>
                  <p>您的个人信息严格保密</p>
                </div>
              </div>
              <div class="extra-feature">
                <span class="extra-icon">💯</span>
                <div class="extra-content">
                  <h4>专业团队</h4>
                  <p>10年+社区服务经验</p>
                </div>
              </div>
              <div class="extra-feature">
                <span class="extra-icon">🌟</span>
                <div class="extra-content">
                  <h4>用户好评</h4>
                  <p>98%的用户满意度</p>
                </div>
              </div>
            </div>

            <div class="contact-cta">
              <p class="cta-text">
                <span class="cta-icon">📞</span>
                紧急问题？直接拨打客服热线
              </p>
              <p class="cta-phone">400-123-4567</p>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'

const openFaq = ref(null)

const formData = reactive({
  name: '',
  phone: '',
  email: '',
  type: '',
  message: ''
})

const showSuccess = ref(false)

const faqs = ref([
  {
    question: '如何预约服务？',
    answer: '您可以通过我们的平台在线预约服务。选择您需要的服务项目，填写预约信息（日期、时间、联系方式等），提交后我们的客服会尽快与您确认预约详情。'
  },
  {
    question: '服务价格如何确定？',
    answer: '我们的服务价格明码标价，在服务详情页面可以看到具体的价格信息。部分服务可能根据实际情况有所调整，我们会在预约前与您确认最终价格。'
  },
  {
    question: '如何取消或修改预约？',
    answer: '如需取消或修改预约，请提前24小时联系我们的客服。您可以通过客服热线400-123-4567或在线客服进行操作。紧急情况下请直接拨打客服电话。'
  },
  {
    question: '服务质量有问题怎么办？',
    answer: '如果您对服务质量不满意，请及时联系我们的客服。我们会认真处理您的反馈，并根据情况提供退款、重新服务或其他解决方案。您的满意是我们最大的追求。'
  },
  {
    question: '如何成为服务提供者？',
    answer: '如果您想成为我们平台的服务提供者，请通过商务合作渠道联系我们。我们会对您的资质进行审核，审核通过后即可入驻平台提供服务。'
  }
])

const toggleFaq = (index) => {
  openFaq.value = openFaq.value === index ? null : index
}

const handleSubmit = () => {
  if (!formData.name || !formData.phone || !formData.message) {
    alert('请填写必填项')
    return
  }
  
  showSuccess.value = true
  
  formData.name = ''
  formData.phone = ''
  formData.email = ''
  formData.type = ''
  formData.message = ''
  
  setTimeout(() => {
    showSuccess.value = false
  }, 5000)
}
</script>

<style scoped>
.contact {
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

.contact-content {
  display: grid;
  grid-template-columns: 1fr 1fr;
  grid-template-rows: auto auto auto;
  gap: 30px 50px;
  padding: 60px 0;
}

.left-column {
  display: contents;
}

.right-column {
  display: contents;
}

.contact-cards-section {
  grid-column: 1;
  grid-row: 1;
}

.working-hours {
  grid-column: 1;
  grid-row: 2;
  margin-bottom: 0;
}

.faq-section {
  grid-column: 1;
  grid-row: 3;
  margin-bottom: 0;
}

.section-title {
  font-size: 1.5rem;
  margin-bottom: 30px;
  color: #2c3e50;
  padding-bottom: 10px;
  border-bottom: 2px solid #e4e8eb;
}

.contact-cards {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 20px;
  margin-bottom: 40px;
}

.contact-card {
  background: white;
  border-radius: 15px;
  padding: 25px;
  text-align: center;
  box-shadow: 0 5px 20px rgba(0, 0, 0, 0.08);
  transition: all 0.3s ease;
}

.contact-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 15px 30px rgba(0, 0, 0, 0.15);
}

.contact-icon {
  font-size: 2.5rem;
  margin-bottom: 15px;
  display: block;
}

.contact-title {
  font-size: 1.1rem;
  margin-bottom: 10px;
  color: #2c3e50;
}

.contact-detail {
  font-size: 1rem;
  font-weight: bold;
  color: #3498db;
  margin-bottom: 5px;
}

.contact-hint {
  font-size: 0.85rem;
  color: #7f8c8d;
}

.working-hours,
.faq-section {
  background: white;
  border-radius: 15px;
  padding: 25px;
  margin-bottom: 30px;
  box-shadow: 0 5px 20px rgba(0, 0, 0, 0.08);
}

.subsection-title {
  font-size: 1.2rem;
  margin-bottom: 20px;
  color: #2c3e50;
}

.hours-list {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.hours-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 15px;
  background: #f5f7fa;
  border-radius: 8px;
}

.hours-day {
  font-weight: 500;
  color: #2c3e50;
}

.hours-time {
  color: #3498db;
  font-weight: bold;
}

.faq-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.faq-item {
  border: 1px solid #e4e8eb;
  border-radius: 8px;
  overflow: hidden;
  transition: all 0.3s ease;
}

.faq-item:hover {
  border-color: #3498db;
}

.faq-item.open {
  border-color: #3498db;
}

.faq-question {
  display: flex;
  align-items: center;
  gap: 15px;
  padding: 15px 20px;
  cursor: pointer;
  background: #f5f7fa;
  transition: background 0.3s ease;
}

.faq-item.open .faq-question {
  background: linear-gradient(135deg, #3498db, #2ecc71);
  color: white;
}

.faq-icon {
  width: 24px;
  height: 24px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: white;
  border-radius: 50%;
  font-weight: bold;
  color: #3498db;
  font-size: 1.2rem;
}

.faq-item.open .faq-icon {
  color: #e74c3c;
}

.faq-text {
  flex: 1;
  font-weight: 500;
}

.faq-answer {
  padding: 20px;
  color: #555;
  line-height: 1.6;
  background: white;
}

.contact-form-section {
  grid-column: 2;
  grid-row: 1 / span 2;
  background: white;
  border-radius: 15px;
  padding: 25px;
  box-shadow: 0 5px 20px rgba(0, 0, 0, 0.08);
  display: flex;
  flex-direction: column;
}

.form-intro {
  color: #7f8c8d;
  margin-bottom: 20px;
  font-size: 0.95rem;
}

.contact-form {
  display: flex;
  flex-direction: column;
  gap: 14px;
  flex: 1;
}

.form-fields {
  display: flex;
  flex-direction: column;
  gap: 14px;
}

.form-highlights {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 12px;
  margin-top: 8px;
}

.highlight-item {
  display: flex;
  align-items: flex-start;
  gap: 10px;
  padding: 12px;
  background: linear-gradient(135deg, rgba(52, 152, 219, 0.03), rgba(46, 204, 113, 0.03));
  border-radius: 8px;
  border: 1px solid rgba(52, 152, 219, 0.1);
}

.highlight-icon {
  font-size: 1.3rem;
  flex-shrink: 0;
}

.highlight-content h4 {
  font-size: 0.9rem;
  color: #2c3e50;
  margin-bottom: 3px;
  font-weight: 600;
}

.highlight-content p {
  font-size: 0.8rem;
  color: #7f8c8d;
  margin: 0;
  line-height: 1.4;
}

.form-footer {
  margin-top: auto;
}

.form-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.form-label {
  font-size: 0.9rem;
  color: #555;
  font-weight: 500;
}

.form-input,
.form-select,
.form-textarea {
  width: 100%;
  padding: 12px 15px;
  border: 2px solid #e4e8eb;
  border-radius: 8px;
  font-size: 1rem;
  transition: border-color 0.3s ease;
  box-sizing: border-box;
}

.form-input:focus,
.form-select:focus,
.form-textarea:focus {
  outline: none;
  border-color: #3498db;
}

.form-textarea {
  resize: vertical;
  min-height: 120px;
}

.form-tips {
  display: flex;
  gap: 20px;
  padding: 15px;
  background: linear-gradient(135deg, rgba(52, 152, 219, 0.05), rgba(46, 204, 113, 0.05));
  border-radius: 10px;
  margin-top: 10px;
}

.tip-item {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 0.85rem;
  color: #555;
}

.tip-icon {
  font-size: 1rem;
}

.submit-btn {
  padding: 15px;
  background: linear-gradient(135deg, #3498db, #2ecc71);
  color: white;
  font-size: 1.1rem;
  font-weight: bold;
  border-radius: 8px;
  transition: all 0.3s ease;
  margin-top: 15px;
}

.submit-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 10px 20px rgba(52, 152, 219, 0.3);
}

.success-message {
  text-align: center;
  padding: 40px 20px;
  background: linear-gradient(135deg, #d4edda, #c3e6cb);
  border-radius: 10px;
  margin-top: 20px;
}

.success-icon {
  font-size: 3rem;
  margin-bottom: 15px;
  display: block;
}

.success-message h3 {
  color: #155724;
  margin-bottom: 10px;
}

.success-message p {
  color: #155724;
}

.extra-info-section {
  grid-column: 2;
  grid-row: 3;
  background: white;
  border-radius: 15px;
  padding: 25px;
  box-shadow: 0 5px 20px rgba(0, 0, 0, 0.08);
  display: flex;
  flex-direction: column;
}

.extra-info-section .section-title {
  font-size: 1.2rem;
  margin-bottom: 20px;
  color: #2c3e50;
  padding-bottom: 10px;
  border-bottom: 2px solid #e4e8eb;
}

.extra-features {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 12px;
  margin-bottom: 20px;
}

.extra-feature {
  display: flex;
  align-items: flex-start;
  gap: 10px;
  padding: 12px;
  background: #f8f9fa;
  border-radius: 10px;
  transition: all 0.3s ease;
}

.extra-feature:hover {
  background: linear-gradient(135deg, rgba(52, 152, 219, 0.1), rgba(46, 204, 113, 0.1));
  transform: translateY(-2px);
}

.extra-icon {
  font-size: 1.5rem;
  flex-shrink: 0;
}

.extra-content h4 {
  font-size: 0.95rem;
  color: #2c3e50;
  margin-bottom: 3px;
}

.extra-content p {
  font-size: 0.85rem;
  color: #7f8c8d;
  margin: 0;
}

.contact-cta {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 12px;
  padding: 20px;
  text-align: center;
  color: white;
  animation: pulse 2s infinite;
}

@keyframes pulse {
  0%, 100% {
    box-shadow: 0 0 0 0 rgba(102, 126, 234, 0.4);
  }
  50% {
    box-shadow: 0 0 0 10px rgba(102, 126, 234, 0);
  }
}

.cta-text {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  font-size: 0.95rem;
  margin-bottom: 8px;
  opacity: 0.9;
}

.cta-icon {
  font-size: 1.2rem;
}

.cta-phone {
  font-size: 1.4rem;
  font-weight: bold;
  margin: 0;
  letter-spacing: 1px;
}

@media (max-width: 992px) {
  .contact-content {
    display: flex;
    flex-direction: column;
    gap: 30px;
  }

  .left-column,
  .right-column {
    display: flex;
    flex-direction: column;
    gap: 30px;
  }

  .contact-cards-section,
  .working-hours,
  .faq-section,
  .contact-form-section,
  .extra-info-section {
    grid-column: auto;
    grid-row: auto;
  }

  .working-hours,
  .faq-section {
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

  .contact-content {
    padding: 40px 0;
  }

  .contact-cards {
    grid-template-columns: 1fr;
  }

  .form-row {
    grid-template-columns: 1fr;
  }

  .contact-form-section,
  .extra-info-section {
    padding: 20px;
  }

  .extra-features {
    grid-template-columns: 1fr;
  }

  .form-tips {
    flex-direction: column;
    gap: 10px;
  }

  .form-highlights {
    grid-template-columns: 1fr;
  }
}
</style>
