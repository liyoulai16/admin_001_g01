<template>
  <div class="service-management">
    <div class="load-error" v-if="loadError">
      <div class="error-icon">⚠️</div>
      <h3>加载失败</h3>
      <p>{{ loadError }}</p>
      <button class="retry-btn" @click="loadServices">🔄 重试</button>
    </div>
    
    <template v-else>
      <div class="search-bar">
        <div class="search-left">
          <select v-model="searchForm.categoryId" class="search-select" @change="loadServices">
            <option :value="null">全部分类</option>
            <option v-for="cat in categories" :key="cat.id" :value="cat.id">
              {{ cat.name }}
            </option>
          </select>
          <input 
            type="text" 
            v-model="searchForm.keyword" 
            class="search-input"
            placeholder="搜索服务名称..."
            @keyup.enter="loadServices"
          />
          <button class="search-btn" @click="loadServices">🔍 搜索</button>
        </div>
        <button class="add-btn" @click="openAddModal">
          <span>➕</span> 添加服务
        </button>
      </div>
      
      <div class="loading-state" v-if="isLoading">
        <div class="loading-spinner"></div>
        <p>加载中...</p>
      </div>
      
      <div class="service-list" v-else-if="services.length > 0">
        <div v-for="service in services" :key="service.id" class="service-card">
          <div class="service-icon">
            <span class="icon-text">{{ service.icon || '🛠️' }}</span>
          </div>
          <div class="service-info">
            <div class="service-header">
              <h3 class="service-name">{{ service.name }}</h3>
              <span class="service-category">{{ service.category || '未分类' }}</span>
            </div>
            <p class="service-desc" v-if="service.description">{{ service.description }}</p>
            <div class="service-meta">
              <span class="meta-item price">💰 {{ service.price || '免费' }}</span>
              <span class="meta-item">⭐ {{ service.rating || 0 }}</span>
              <span class="meta-item">📝 {{ service.reviews || 0 }} 条评价</span>
              <span class="meta-item">🔢 排序: {{ service.sortOrder }}</span>
              <span class="meta-item">📅 {{ formatDate(service.createTime) }}</span>
            </div>
            <div class="service-features" v-if="service.features && service.features.length > 0">
              <span class="feature-tag" v-for="(feat, idx) in service.features" :key="idx">
                ✅ {{ feat }}
              </span>
            </div>
          </div>
          <div class="service-status">
            <span class="status-badge" :class="{ active: service.status === 1 }">
              {{ service.status === 1 ? '启用' : '禁用' }}
            </span>
          </div>
          <div class="service-actions">
            <button class="action-btn edit" @click="openEditModal(service)">✏️ 编辑</button>
            <button 
              class="action-btn toggle" 
              :class="service.status === 1 ? 'disable' : 'enable'"
              @click="toggleStatus(service)"
            >
              {{ service.status === 1 ? '❌ 禁用' : '✅ 启用' }}
            </button>
            <button class="action-btn delete" @click="confirmDelete(service)">🗑️ 删除</button>
          </div>
        </div>
      </div>
      
      <div class="pagination-section" v-if="services.length > 0">
        <div class="pagination">
          <button 
            class="page-btn first-btn" 
            :disabled="currentPage === 1"
            @click="goToFirstPage"
          >
            首页
          </button>
          
          <button 
            class="page-btn prev-btn" 
            :disabled="currentPage === 1"
            @click="goToPrevPage"
          >
            上一页
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
            :disabled="currentPage === totalPages || totalPages === 0"
            @click="goToNextPage"
          >
            下一页
          </button>
          
          <button 
            class="page-btn last-btn" 
            :disabled="currentPage === totalPages || totalPages === 0"
            @click="goToLastPage"
          >
            尾页
          </button>
          
          <div class="page-size-select">
            <span class="size-label">每页显示</span>
            <select v-model="pageSize" @change="handlePageSizeChange" class="size-select">
              <option :value="10">10条</option>
              <option :value="20">20条</option>
              <option :value="50">50条</option>
              <option :value="100">100条</option>
            </select>
          </div>
          
          <div class="page-jump">
            <span class="jump-label">跳转到</span>
            <input 
              type="number" 
              v-model="jumpPageInput" 
              min="1"
              :max="totalPages || 1"
              @keyup.enter="handleJumpPage"
              class="jump-input"
              placeholder="页码"
            />
            <span class="jump-label">页</span>
            <button class="jump-btn" @click="handleJumpPage">
              确定
            </button>
          </div>
          
          <div class="page-info">
            共 <span class="info-highlight">{{ totalPages || 1 }}</span> 页，
            当前第 <span class="info-highlight">{{ currentPage }}</span> 页，
            共 <span class="info-highlight">{{ total }}</span> 条记录
          </div>
        </div>
        
        <div class="pagination-toast" :class="{ show: showToast }">
          <span class="toast-icon">{{ toastIcon }}</span>
          <span class="toast-message">{{ toastMessage }}</span>
        </div>
      </div>
      
      <div class="empty-state" v-else>
        <div class="empty-icon">🛠️</div>
        <h3>暂无服务</h3>
        <p>点击上方按钮添加第一个服务</p>
      </div>
    </template>
    
    <div class="modal-overlay" :class="{ show: showModal }" @click="closeModal">
      <div class="modal-container large" @click.stop>
        <div class="modal-header">
          <h2>{{ isEdit ? '编辑服务' : '添加服务' }}</h2>
          <button class="close-btn" @click="closeModal">×</button>
        </div>
        
        <div class="modal-body">
          <div class="form-row">
            <div class="form-group">
              <label class="form-label">服务名称 <span class="required">*</span></label>
              <input 
                type="text" 
                v-model="form.name" 
                class="form-input"
                placeholder="请输入服务名称"
              />
            </div>
            
            <div class="form-group">
              <label class="form-label">服务分类 <span class="required">*</span></label>
              <select v-model="form.categoryId" class="form-select">
                <option :value="null">请选择分类</option>
                <option v-for="cat in categories" :key="cat.id" :value="cat.id">
                  {{ cat.name }}
                </option>
              </select>
            </div>
          </div>
          
          <div class="form-row">
            <div class="form-group">
              <label class="form-label">图标</label>
              <input 
                type="text" 
                v-model="form.icon" 
                class="form-input"
                placeholder="请输入图标（如：🛠️、💼、🎨）"
              />
              <div class="icon-preview" v-if="form.icon">
                <span class="preview-icon">{{ form.icon }}</span>
                <span class="preview-text">预览</span>
              </div>
            </div>
            
            <div class="form-group">
              <label class="form-label">排序</label>
              <input 
                type="number" 
                v-model.number="form.sortOrder" 
                class="form-input"
                placeholder="数字越小越靠前"
                min="0"
              />
            </div>
          </div>
          
          <div class="form-group">
            <label class="form-label">服务描述</label>
            <textarea 
              v-model="form.description" 
              class="form-textarea"
              placeholder="请输入服务描述"
              rows="3"
            ></textarea>
          </div>
          
          <div class="form-row">
            <div class="form-group">
              <label class="form-label">价格显示</label>
              <input 
                type="text" 
                v-model="form.price" 
                class="form-input"
                placeholder="如：免费、¥50/次、面议等"
              />
            </div>
            
            <div class="form-group">
              <label class="form-label">价格数值</label>
              <input 
                type="number" 
                v-model.number="form.priceValue" 
                class="form-input"
                placeholder="用于排序筛选（数字）"
                min="0"
                step="0.01"
              />
            </div>
          </div>
          
          <div class="form-group">
            <label class="form-label">服务特性（每行一个）</label>
            <textarea 
              v-model="form.featuresText" 
              class="form-textarea"
              placeholder="请输入服务特性，每行一个&#10;例如：&#10;专业团队&#10;快速响应&#10;售后保障"
              rows="4"
            ></textarea>
          </div>
          
          <div class="form-group">
            <label class="form-label">服务详情</label>
            <textarea 
              v-model="form.details" 
              class="form-textarea"
              placeholder="请输入服务详细说明"
              rows="4"
            ></textarea>
          </div>
          
          <div class="form-group">
            <label class="form-label">状态</label>
            <div class="radio-group">
              <label class="radio-label">
                <input type="radio" v-model="form.status" :value="1" /> 启用
              </label>
              <label class="radio-label">
                <input type="radio" v-model="form.status" :value="0" /> 禁用
              </label>
            </div>
          </div>
        </div>
        
        <div class="modal-footer">
          <button class="btn-secondary" @click="closeModal">取消</button>
          <button class="btn-primary" @click="handleSubmit" :disabled="isSubmitting">
            {{ isSubmitting ? '保存中...' : '保存' }}
          </button>
        </div>
        
        <div v-if="errorMessage" class="error-message">{{ errorMessage }}</div>
        <div v-if="successMessage" class="success-message">{{ successMessage }}</div>
      </div>
    </div>
    
    <div class="modal-overlay" :class="{ show: showDeleteModal }" @click="closeDeleteModal">
      <div class="modal-container small" @click.stop>
        <div class="modal-header">
          <h2>确认删除</h2>
        </div>
        <div class="modal-body">
          <p>确定要删除服务 "<strong>{{ deletingService?.name }}</strong>" 吗？</p>
          <p class="warning-text">此操作不可撤销</p>
        </div>
        <div class="modal-footer">
          <button class="btn-secondary" @click="closeDeleteModal">取消</button>
          <button class="btn-danger" @click="handleDelete" :disabled="isDeleting">
            {{ isDeleting ? '删除中...' : '确认删除' }}
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import request from '../../utils/request'

const services = ref([])
const categories = ref([])
const isLoading = ref(false)
const showModal = ref(false)
const showDeleteModal = ref(false)
const isEdit = ref(false)
const isSubmitting = ref(false)
const isDeleting = ref(false)
const errorMessage = ref('')
const successMessage = ref('')
const loadError = ref('')
const deletingService = ref(null)
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const totalPages = ref(0)
const jumpPageInput = ref('')
const showToast = ref(false)
const toastMessage = ref('')
const toastIcon = ref('⚠️')
let toastTimer = null

const searchForm = reactive({
  keyword: '',
  categoryId: null
})

const form = reactive({
  id: null,
  name: '',
  categoryId: null,
  icon: '',
  description: '',
  price: '',
  priceValue: 0,
  featuresText: '',
  details: '',
  sortOrder: 0,
  status: 1
})

const features = computed(() => {
  if (!form.featuresText) return []
  return form.featuresText.split('\n').filter(f => f.trim())
})

const visiblePages = computed(() => {
  const current = currentPage.value
  const totalPagesVal = totalPages.value
  const delta = 2
  const range = []
  const rangeWithDots = []
  let l

  for (let i = 1; i <= totalPagesVal; i++) {
    if (i === 1 || i === totalPagesVal || (i >= current - delta && i <= current + delta)) {
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

const showToastMessage = (message, icon = '⚠️') => {
  toastMessage.value = message
  toastIcon.value = icon
  showToast.value = true
  
  if (toastTimer) {
    clearTimeout(toastTimer)
  }
  
  toastTimer = setTimeout(() => {
    showToast.value = false
  }, 2000)
}

const resetForm = () => {
  form.id = null
  form.name = ''
  form.categoryId = null
  form.icon = ''
  form.description = ''
  form.price = ''
  form.priceValue = 0
  form.featuresText = ''
  form.details = ''
  form.sortOrder = 0
  form.status = 1
  errorMessage.value = ''
  successMessage.value = ''
}

const loadCategories = async () => {
  try {
    const response = await request('/api/admin/service-categories/all')
    const data = await response.json()
    
    console.log('分类响应:', data)
    
    if (data.code === 200) {
      categories.value = data.data || []
    } else if (data.code === 401) {
      console.error('401错误:', data.message)
    }
  } catch (error) {
    console.error('加载分类失败:', error)
  }
}

const loadServices = async () => {
  isLoading.value = true
  loadError.value = ''
  
  try {
    let url = `/api/admin/services?pageNum=${currentPage.value}&pageSize=${pageSize.value}`
    if (searchForm.keyword) {
      url += `&keyword=${encodeURIComponent(searchForm.keyword)}`
    }
    if (searchForm.categoryId) {
      url += `&categoryId=${searchForm.categoryId}`
    }
    
    const response = await request(url)
    const data = await response.json()
    
    console.log('服务响应:', data)
    
    if (data.code === 200) {
      services.value = data.data.records || []
      total.value = data.data.total || 0
      totalPages.value = data.data.pages || 0
    } else if (data.code === 401) {
      loadError.value = '登录已过期，请重新登录'
      console.error('401错误:', data.message)
    } else {
      loadError.value = data.message || '加载失败'
      console.error('加载失败:', data.message)
    }
  } catch (error) {
    console.error('加载服务失败:', error)
    loadError.value = '网络错误，请检查后端服务是否启动'
  } finally {
    isLoading.value = false
  }
}

const openAddModal = () => {
  resetForm()
  isEdit.value = false
  showModal.value = true
}

const openEditModal = (service) => {
  resetForm()
  isEdit.value = true
  form.id = service.id
  form.name = service.name || ''
  form.categoryId = service.categoryId || null
  form.icon = service.icon || ''
  form.description = service.description || ''
  form.price = service.price || ''
  form.priceValue = service.priceValue || 0
  form.featuresText = (service.features || []).join('\n')
  form.details = service.details || ''
  form.sortOrder = service.sortOrder || 0
  form.status = service.status ?? 1
  showModal.value = true
}

const closeModal = () => {
  showModal.value = false
}

const handleSubmit = async () => {
  errorMessage.value = ''
  successMessage.value = ''
  
  if (!form.name.trim()) {
    errorMessage.value = '请输入服务名称'
    return
  }
  
  if (!form.categoryId) {
    errorMessage.value = '请选择服务分类'
    return
  }
  
  isSubmitting.value = true
  
  try {
    const url = isEdit.value 
      ? `/api/admin/services/${form.id}` 
      : '/api/admin/services'
    const method = isEdit.value ? 'PUT' : 'POST'
    
    const response = await request(url, {
      method: method,
      body: JSON.stringify({
        name: form.name,
        categoryId: form.categoryId,
        icon: form.icon,
        description: form.description,
        price: form.price,
        priceValue: form.priceValue,
        features: features.value,
        details: form.details,
        sortOrder: form.sortOrder,
        status: form.status
      })
    })
    
    const data = await response.json()
    
    if (data.code === 200) {
      successMessage.value = isEdit.value ? '更新成功' : '创建成功'
      setTimeout(() => {
        closeModal()
        loadServices()
      }, 1000)
    } else {
      errorMessage.value = data.message || '操作失败'
    }
  } catch (error) {
    console.error('提交失败:', error)
    errorMessage.value = '网络错误，请稍后重试'
  } finally {
    isSubmitting.value = false
  }
}

const confirmDelete = (service) => {
  deletingService.value = service
  showDeleteModal.value = true
}

const closeDeleteModal = () => {
  showDeleteModal.value = false
  deletingService.value = null
}

const handleDelete = async () => {
  if (!deletingService.value) return
  
  isDeleting.value = true
  
  try {
    const response = await request(`/api/admin/services/${deletingService.value.id}`, {
      method: 'DELETE'
    })
    
    const data = await response.json()
    
    if (data.code === 200) {
      closeDeleteModal()
      loadServices()
    } else {
      alert(data.message || '删除失败')
    }
  } catch (error) {
    console.error('删除失败:', error)
    alert('网络错误，请稍后重试')
  } finally {
    isDeleting.value = false
  }
}

const toggleStatus = async (service) => {
  const newStatus = service.status === 1 ? 0 : 1
  
  try {
    const response = await request(`/api/admin/services/${service.id}`, {
      method: 'PUT',
      body: JSON.stringify({
        status: newStatus
      })
    })
    
    const data = await response.json()
    
    if (data.code === 200) {
      service.status = newStatus
    } else {
      alert(data.message || '操作失败')
    }
  } catch (error) {
    console.error('切换状态失败:', error)
    alert('网络错误，请稍后重试')
  }
}

const goToPrevPage = () => {
  if (currentPage.value > 1) {
    currentPage.value--
    loadServices()
  }
}

const goToNextPage = () => {
  if (currentPage.value < totalPages.value) {
    currentPage.value++
    loadServices()
  }
}

const goToPage = (page) => {
  const totalPagesVal = totalPages.value || 1
  if (page < 1) {
    showToastMessage('已经是第一页了', '⚠️')
    return
  }
  if (page > totalPagesVal) {
    showToastMessage(`只有 ${totalPagesVal} 页`, '⚠️')
    return
  }
  if (page === currentPage.value) {
    showToastMessage('已经在当前页', 'ℹ️')
    return
  }
  currentPage.value = page
  loadServices()
  window.scrollTo({ top: 0, behavior: 'smooth' })
}

const goToFirstPage = () => {
  if (currentPage.value === 1) {
    showToastMessage('已经是第一页了', '⚠️')
    return
  }
  goToPage(1)
}

const goToLastPage = () => {
  const totalPagesVal = totalPages.value || 1
  if (currentPage.value === totalPagesVal) {
    showToastMessage('已经是最后一页了', '⚠️')
    return
  }
  goToPage(totalPagesVal)
}

const handlePageSizeChange = () => {
  currentPage.value = 1
  loadServices()
}

const handleJumpPage = () => {
  const inputValue = jumpPageInput.value
  if (inputValue === '' || inputValue === null || inputValue === undefined) {
    showToastMessage('请输入页码', '⚠️')
    return
  }
  const pageNum = Number(inputValue)
  if (isNaN(pageNum) || !Number.isInteger(pageNum)) {
    showToastMessage('请输入有效的数字', '⚠️')
    return
  }
  if (pageNum < 1) {
    showToastMessage('页码不能小于1', '⚠️')
    return
  }
  const totalPagesVal = totalPages.value || 1
  if (pageNum > totalPagesVal) {
    showToastMessage(`页码不能大于 ${totalPagesVal}`, '⚠️')
    return
  }
  jumpPageInput.value = ''
  goToPage(pageNum)
}

const formatDate = (dateStr) => {
  if (!dateStr) return '-'
  const date = new Date(dateStr)
  return date.toLocaleDateString('zh-CN')
}

onMounted(() => {
  loadCategories()
  loadServices()
})
</script>

<style scoped>
.service-management {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.search-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 20px;
  flex-wrap: wrap;
}

.search-left {
  display: flex;
  gap: 12px;
  align-items: center;
  flex-wrap: wrap;
}

.search-select,
.search-input {
  padding: 10px 16px;
  border: 2px solid #e4e8eb;
  border-radius: 10px;
  font-size: 0.95rem;
  color: #2c3e50;
  outline: none;
  transition: all 0.3s ease;
  background: white;
}

.search-select {
  min-width: 150px;
  cursor: pointer;
}

.search-input {
  min-width: 200px;
}

.search-select:focus,
.search-input:focus {
  border-color: #6B8E23;
  box-shadow: 0 0 0 4px rgba(107, 142, 35, 0.1);
}

.search-btn {
  padding: 10px 20px;
  background: linear-gradient(135deg, #6B8E23, #8FBC8F);
  color: white;
  border: none;
  border-radius: 10px;
  font-size: 0.95rem;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;
}

.search-btn:hover {
  transform: translateY(-1px);
  box-shadow: 0 5px 15px rgba(107, 142, 35, 0.3);
}

.add-btn {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 24px;
  background: linear-gradient(135deg, #6B8E23, #8FBC8F);
  color: white;
  border: none;
  border-radius: 10px;
  font-size: 1rem;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
}

.add-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(107, 142, 35, 0.3);
}

.service-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.service-card {
  display: flex;
  align-items: flex-start;
  background: white;
  border-radius: 16px;
  padding: 20px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  transition: all 0.3s ease;
  gap: 20px;
}

.service-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.12);
}

.service-icon {
  width: 60px;
  height: 60px;
  background: linear-gradient(135deg, #f5f7fa, #e4e8eb);
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.icon-text {
  font-size: 1.8rem;
}

.service-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 10px;
  min-width: 0;
}

.service-header {
  display: flex;
  align-items: center;
  gap: 12px;
  flex-wrap: wrap;
}

.service-name {
  font-size: 1.15rem;
  color: #2c3e50;
  margin: 0;
  font-weight: 600;
}

.service-category {
  padding: 4px 12px;
  background: #e8f4fd;
  color: #3498db;
  border-radius: 20px;
  font-size: 0.85rem;
  font-weight: 500;
}

.service-desc {
  color: #7f8c8d;
  font-size: 0.95rem;
  margin: 0;
  line-height: 1.5;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.service-meta {
  display: flex;
  gap: 16px;
  flex-wrap: wrap;
}

.meta-item {
  color: #95a5a6;
  font-size: 0.9rem;
}

.meta-item.price {
  color: #e74c3c;
  font-weight: 600;
}

.service-features {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.feature-tag {
  padding: 4px 10px;
  background: #f0fdf4;
  color: #16a34a;
  border-radius: 6px;
  font-size: 0.85rem;
}

.service-status {
  flex-shrink: 0;
  padding-top: 5px;
}

.status-badge {
  padding: 6px 16px;
  border-radius: 20px;
  font-size: 0.85rem;
  font-weight: 600;
  background: rgba(196, 91, 26, 0.9);
  color: white;
}

.status-badge.active {
  background: rgba(93, 124, 74, 0.9);
}

.service-actions {
  display: flex;
  flex-direction: column;
  gap: 8px;
  flex-shrink: 0;
}

.action-btn {
  padding: 8px 14px;
  border: none;
  border-radius: 8px;
  font-size: 0.85rem;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;
  white-space: nowrap;
}

.action-btn.edit {
  background: #e8f4fd;
  color: #3498db;
}

.action-btn.edit:hover {
  background: #3498db;
  color: white;
}

.action-btn.delete {
  background: #fdf2f2;
  color: #dc2626;
}

.action-btn.delete:hover {
  background: #dc2626;
  color: white;
}

.action-btn.toggle {
  background: #fef3c7;
  color: #d97706;
}

.action-btn.toggle.enable:hover {
  background: #16a34a;
  color: white;
}

.action-btn.toggle.disable:hover {
  background: #dc2626;
  color: white;
}

.load-error {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 60px 20px;
  background: white;
  border-radius: 16px;
  text-align: center;
}

.error-icon {
  font-size: 4rem;
  margin-bottom: 16px;
}

.load-error h3 {
  font-size: 1.3rem;
  color: #dc2626;
  margin: 0 0 8px 0;
}

.load-error p {
  color: #7f8c8d;
  margin: 0 0 20px 0;
}

.retry-btn {
  padding: 12px 24px;
  background: linear-gradient(135deg, #6B8E23, #8FBC8F);
  color: white;
  border: none;
  border-radius: 10px;
  font-size: 1rem;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
}

.retry-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(107, 142, 35, 0.3);
}

.loading-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 60px 20px;
  background: white;
  border-radius: 16px;
  text-align: center;
}

.loading-spinner {
  width: 40px;
  height: 40px;
  border: 4px solid #e4e8eb;
  border-top: 4px solid #6B8E23;
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin-bottom: 16px;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.loading-state p {
  color: #7f8c8d;
  margin: 0;
  font-size: 1rem;
}

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 60px 20px;
  background: white;
  border-radius: 16px;
  text-align: center;
}

.empty-icon {
  font-size: 4rem;
  margin-bottom: 16px;
}

.empty-state h3 {
  font-size: 1.3rem;
  color: #2c3e50;
  margin: 0 0 8px 0;
}

.empty-state p {
  color: #7f8c8d;
  margin: 0;
}

.pagination-section {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-top: 20px;
  padding-top: 20px;
  position: relative;
}

.pagination {
  display: flex;
  align-items: center;
  gap: 8px;
  flex-wrap: wrap;
  justify-content: center;
  row-gap: 12px;
}

.page-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  min-width: 40px;
  height: 40px;
  padding: 0 16px;
  background: white;
  border: 2px solid #e4e8eb;
  border-radius: 8px;
  font-size: 0.95rem;
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

.first-btn, .last-btn, .prev-btn, .next-btn {
  font-weight: 500;
  padding: 0 18px;
}

.first-btn {
  background: linear-gradient(135deg, #f8f9fa, #ffffff);
}

.last-btn {
  background: linear-gradient(135deg, #f8f9fa, #ffffff);
}

.page-size-select {
  display: flex;
  align-items: center;
  gap: 6px;
  margin-left: 8px;
  padding: 8px 12px;
  background: #f8f9fa;
  border-radius: 8px;
}

.size-label {
  font-size: 0.9rem;
  color: #555;
  white-space: nowrap;
}

.size-select {
  padding: 6px 10px;
  border: 2px solid #e4e8eb;
  border-radius: 6px;
  font-size: 0.9rem;
  background: white;
  cursor: pointer;
  transition: border-color 0.3s ease;
}

.size-select:focus {
  outline: none;
  border-color: #6B8E23;
}

.page-jump {
  display: flex;
  align-items: center;
  gap: 6px;
  margin-left: 8px;
  padding: 8px 12px;
  background: #f8f9fa;
  border-radius: 8px;
}

.jump-label {
  font-size: 0.9rem;
  color: #555;
  white-space: nowrap;
}

.jump-input {
  width: 80px;
  height: 36px;
  padding: 0 8px;
  border: 2px solid #e4e8eb;
  border-radius: 6px;
  font-size: 0.95rem;
  text-align: center;
  transition: border-color 0.3s ease;
}

.jump-input:focus {
  outline: none;
  border-color: #6B8E23;
}

.jump-input::placeholder {
  color: #aaa;
}

.jump-btn {
  height: 36px;
  padding: 0 14px;
  background: linear-gradient(135deg, #6B8E23, #8FBC8F);
  color: white;
  border: none;
  border-radius: 6px;
  font-size: 0.9rem;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;
}

.jump-btn:hover {
  background: linear-gradient(135deg, #556B2F, #5D7C4A);
}

.page-info {
  display: flex;
  align-items: center;
  gap: 4px;
  padding: 8px 16px;
  background: white;
  border: 1px solid #e4e8eb;
  border-radius: 8px;
  font-size: 0.9rem;
  color: #555;
}

.info-highlight {
  font-weight: bold;
  color: #6B8E23;
}

.pagination-toast {
  position: absolute;
  top: -40px;
  left: 50%;
  transform: translateX(-50%) translateY(-10px);
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 20px;
  background: white;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  border-left: 4px solid #f39c12;
  opacity: 0;
  visibility: hidden;
  transition: all 0.3s ease;
  z-index: 100;
}

.pagination-toast.show {
  opacity: 1;
  visibility: visible;
  transform: translateX(-50%) translateY(0);
}

.toast-icon {
  font-size: 1.1rem;
}

.toast-message {
  font-size: 0.9rem;
  color: #333;
  font-weight: 500;
}

.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
  opacity: 0;
  visibility: hidden;
  transition: all 0.3s ease;
}

.modal-overlay.show {
  opacity: 1;
  visibility: visible;
}

.modal-container {
  background: white;
  border-radius: 16px;
  width: 90%;
  max-width: 600px;
  max-height: 90vh;
  overflow-y: auto;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
  transform: scale(0.9) translateY(20px);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.modal-overlay.show .modal-container {
  transform: scale(1) translateY(0);
}

.modal-container.large {
  max-width: 700px;
}

.modal-container.small {
  max-width: 400px;
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 24px;
  border-bottom: 1px solid #e4e8eb;
  position: sticky;
  top: 0;
  background: white;
  z-index: 1;
}

.modal-header h2 {
  margin: 0;
  font-size: 1.4rem;
  color: #2c3e50;
}

.close-btn {
  width: 36px;
  height: 36px;
  display: flex;
  justify-content: center;
  align-items: center;
  background: #f5f7fa;
  border: none;
  border-radius: 50%;
  font-size: 1.5rem;
  color: #7f8c8d;
  cursor: pointer;
  transition: all 0.3s ease;
}

.close-btn:hover {
  background: #C45B1A;
  color: white;
}

.modal-body {
  padding: 24px;
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.form-row {
  display: flex;
  gap: 20px;
}

.form-row .form-group {
  flex: 1;
}

.form-label {
  font-size: 0.95rem;
  font-weight: 600;
  color: #2c3e50;
}

.required {
  color: #dc2626;
}

.form-input,
.form-textarea,
.form-select {
  padding: 12px 16px;
  border: 2px solid #e4e8eb;
  border-radius: 10px;
  font-size: 1rem;
  color: #2c3e50;
  outline: none;
  transition: all 0.3s ease;
  background: white;
}

.form-select {
  cursor: pointer;
}

.form-input:focus,
.form-textarea:focus,
.form-select:focus {
  border-color: #6B8E23;
  box-shadow: 0 0 0 4px rgba(107, 142, 35, 0.1);
}

.form-textarea {
  resize: vertical;
  min-height: 80px;
}

.icon-preview {
  margin-top: 10px;
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 10px;
  background: #fafbfc;
  border-radius: 8px;
}

.preview-icon {
  font-size: 1.5rem;
}

.preview-text {
  color: #7f8c8d;
  font-size: 0.9rem;
}

.radio-group {
  display: flex;
  gap: 24px;
}

.radio-label {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  font-size: 0.95rem;
  color: #555;
}

.radio-label input {
  width: 18px;
  height: 18px;
  accent-color: #6B8E23;
}

.modal-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  padding: 24px;
  border-top: 1px solid #e4e8eb;
  position: sticky;
  bottom: 0;
  background: white;
}

.btn-primary,
.btn-secondary,
.btn-danger {
  padding: 12px 28px;
  border: none;
  border-radius: 10px;
  font-size: 0.95rem;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
}

.btn-primary {
  background: linear-gradient(135deg, #6B8E23, #8FBC8F);
  color: white;
}

.btn-primary:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(107, 142, 35, 0.3);
}

.btn-primary:disabled {
  opacity: 0.7;
  cursor: not-allowed;
}

.btn-secondary {
  background: #f5f7fa;
  color: #7f8c8d;
  border: 1px solid #e4e8eb;
}

.btn-secondary:hover {
  background: #e8ecef;
  color: #555;
}

.btn-danger {
  background: linear-gradient(135deg, #C45B1A, #9E3B1B);
  color: white;
}

.btn-danger:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(196, 91, 26, 0.3);
}

.btn-danger:disabled {
  opacity: 0.7;
  cursor: not-allowed;
}

.warning-text {
  color: #dc2626;
  font-size: 0.9rem;
  margin: 8px 0 0 0;
}

.error-message {
  background: #fdf2f2;
  border: 1px solid #fecaca;
  color: #dc2626;
  padding: 12px 16px;
  border-radius: 8px;
  font-size: 0.9rem;
  text-align: center;
  margin: 0 24px 24px;
}

.success-message {
  background: #f0fdf4;
  border: 1px solid #bbf7d0;
  color: #16a34a;
  padding: 12px 16px;
  border-radius: 8px;
  font-size: 0.9rem;
  text-align: center;
  margin: 0 24px 24px;
}

@media (max-width: 768px) {
  .search-bar {
    flex-direction: column;
    align-items: stretch;
  }
  
  .search-left {
    flex-direction: column;
  }
  
  .search-select,
  .search-input {
    width: 100%;
  }
  
  .service-card {
    flex-direction: column;
    align-items: flex-start;
  }
  
  .service-icon {
    width: 50px;
    height: 50px;
  }
  
  .icon-text {
    font-size: 1.5rem;
  }
  
  .service-actions {
    flex-direction: row;
    width: 100%;
  }
  
  .action-btn {
    flex: 1;
    text-align: center;
  }
  
  .form-row {
    flex-direction: column;
    gap: 20px;
  }
  
  .modal-container {
    width: 95%;
    margin: 10px;
    max-height: 95vh;
  }
  
  .modal-header,
  .modal-footer {
    padding: 16px 20px;
  }
}
</style>
