<template>
  <div class="forum-post-management">
    <div class="load-error" v-if="loadError">
      <div class="error-icon">⚠️</div>
      <h3>加载失败</h3>
      <p>{{ loadError }}</p>
      <button class="retry-btn" @click="loadPosts">🔄 重试</button>
    </div>
    
    <template v-else>
      <div class="search-bar">
        <div class="search-left">
          <select v-model="searchForm.categoryId" class="search-select" @change="loadPosts">
            <option :value="null">全部版块</option>
            <option v-for="cat in categories" :key="cat.id" :value="cat.id">
              {{ cat.name }}
            </option>
          </select>
          <select v-model="searchForm.status" class="search-select" @change="loadPosts">
            <option :value="null">全部状态</option>
            <option :value="1">启用</option>
            <option :value="0">禁用</option>
          </select>
          <input 
            type="text" 
            v-model="searchForm.keyword" 
            class="search-input"
            placeholder="搜索帖子标题或内容..."
            @keyup.enter="loadPosts"
          />
          <button class="search-btn" @click="loadPosts">🔍 搜索</button>
        </div>
        <button class="add-btn" @click="openAddModal">
          <span>➕</span> 添加帖子
        </button>
      </div>
      
      <div class="loading-state" v-if="isLoading">
        <div class="loading-spinner"></div>
        <p>加载中...</p>
      </div>
      
      <div class="post-list" v-else-if="posts.length > 0">
        <div v-for="post in posts" :key="post.id" class="post-card">
          <div class="post-main">
            <div class="post-header">
              <h3 class="post-title">
                <span class="post-tag top" v-if="isPostTop(post)">📌 置顶</span>
                <span class="post-tag essence" v-if="post.isEssence === 1">⭐ 精华</span>
                <span class="post-tag hot" v-if="post.isHot === 1">🔥 热门</span>
                {{ post.title }}
              </h3>
            </div>
            <div class="post-excerpt" v-if="post.excerpt">
              {{ post.excerpt }}
            </div>
            <div class="post-meta">
              <span class="meta-item">👤 {{ post.authorName || '匿名' }}</span>
              <span class="meta-item">📁 {{ post.categoryName || '未分类' }}</span>
              <span class="meta-item">👁️ {{ post.views || 0 }} 浏览</span>
              <span class="meta-item">💬 {{ post.comments || 0 }} 评论</span>
              <span class="meta-item">❤️ {{ post.likes || 0 }} 点赞</span>
              <span class="meta-item">📅 {{ formatDate(post.createTime) }}</span>
            </div>
          </div>
          <div class="post-status">
            <span class="status-badge" :class="{ active: post.status === 1 }">
              {{ post.status === 1 ? '启用' : '禁用' }}
            </span>
          </div>
          <div class="post-actions">
            <div class="action-row">
              <button class="action-btn edit" @click="openEditModal(post)">✏️ 编辑</button>
              <button class="action-btn delete" @click="confirmDelete(post)">🗑️ 删除</button>
            </div>
            <div class="action-row">
              <button 
                class="action-btn tag-btn" 
                :class="{ active: isPostTop(post) }"
                @click="toggleTop(post)"
              >
                {{ isPostTop(post) ? '📌 取消置顶' : '📌 置顶' }}
              </button>
              <button 
                class="action-btn tag-btn" 
                :class="{ active: post.isHot === 1 }"
                @click="toggleHot(post)"
              >
                {{ post.isHot === 1 ? '🔥 取消热门' : '🔥 热门' }}
              </button>
              <button 
                class="action-btn tag-btn" 
                :class="{ active: post.isEssence === 1 }"
                @click="toggleEssence(post)"
              >
                {{ post.isEssence === 1 ? '⭐ 取消精华' : '⭐ 精华' }}
              </button>
            </div>
            <div class="action-row">
              <button 
                class="action-btn toggle" 
                :class="post.status === 1 ? 'disable' : 'enable'"
                @click="toggleStatus(post)"
              >
                {{ post.status === 1 ? '❌ 禁用' : '✅ 启用' }}
              </button>
            </div>
          </div>
        </div>
      </div>
      
      <div class="empty-state" v-else>
        <div class="empty-icon">📝</div>
        <h3>暂无帖子</h3>
        <p>点击上方按钮添加第一个帖子</p>
      </div>
    </template>
    
    <div class="modal-overlay" :class="{ show: showModal }" @click="closeModal">
      <div class="modal-container large" @click.stop>
        <div class="modal-header">
          <h2>{{ isEdit ? '编辑帖子' : '添加帖子' }}</h2>
          <button class="close-btn" @click="closeModal">×</button>
        </div>
        
        <div class="modal-body">
          <div class="form-group">
            <label class="form-label">帖子标题 <span class="required">*</span></label>
            <input 
              type="text" 
              v-model="form.title" 
              class="form-input"
              placeholder="请输入帖子标题"
            />
          </div>
          
          <div class="form-row">
            <div class="form-group">
              <label class="form-label">所属版块 <span class="required">*</span></label>
              <select v-model="form.categoryId" class="form-select">
                <option :value="null">请选择版块</option>
                <option v-for="cat in categories" :key="cat.id" :value="cat.id">
                  {{ cat.name }}
                </option>
              </select>
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
          
          <div class="form-group">
            <label class="form-label">帖子内容 <span class="required">*</span></label>
            <textarea 
              v-model="form.content" 
              class="form-textarea"
              placeholder="请输入帖子内容"
              rows="8"
            ></textarea>
          </div>
          
          <div class="form-group">
            <label class="form-label">标签设置</label>
            <div class="checkbox-group">
              <label class="checkbox-label">
                <input type="checkbox" v-model="form.isTop" /> 📌 置顶
              </label>
              <label class="checkbox-label">
                <input type="checkbox" v-model="form.isHot" /> 🔥 热门
              </label>
              <label class="checkbox-label">
                <input type="checkbox" v-model="form.isEssence" /> ⭐ 精华
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
          <p>确定要删除帖子 "<strong>{{ deletingPost?.title }}</strong>" 吗？</p>
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
import { ref, reactive, onMounted } from 'vue'
import request from '../../utils/request'

const posts = ref([])
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
const deletingPost = ref(null)

const searchForm = reactive({
  keyword: '',
  categoryId: null,
  status: null
})

const form = reactive({
  id: null,
  title: '',
  categoryId: null,
  content: '',
  status: 1,
  isTop: false,
  isHot: false,
  isEssence: false
})

const resetForm = () => {
  form.id = null
  form.title = ''
  form.categoryId = null
  form.content = ''
  form.status = 1
  form.isTop = false
  form.isHot = false
  form.isEssence = false
  errorMessage.value = ''
  successMessage.value = ''
}

const isPostTop = (post) => {
  const tag = post.tag || 0
  return tag === 2 || tag === 3
}

const loadCategories = async () => {
  try {
    const response = await request('/api/admin/forum-categories/all')
    const data = await response.json()
    
    if (data.code === 200) {
      categories.value = data.data || []
    }
  } catch (error) {
    console.error('加载版块失败:', error)
  }
}

const loadPosts = async () => {
  isLoading.value = true
  loadError.value = ''
  
  try {
    let url = '/api/admin/forum-posts?pageNum=1&pageSize=100'
    if (searchForm.keyword) {
      url += `&keyword=${encodeURIComponent(searchForm.keyword)}`
    }
    if (searchForm.categoryId) {
      url += `&categoryId=${searchForm.categoryId}`
    }
    if (searchForm.status !== null) {
      url += `&status=${searchForm.status}`
    }
    
    const response = await request(url)
    const data = await response.json()
    
    if (data.code === 200) {
      posts.value = data.data.records || []
    } else if (data.code === 401) {
      loadError.value = '登录已过期，请重新登录'
    } else {
      loadError.value = data.message || '加载失败'
    }
  } catch (error) {
    console.error('加载帖子失败:', error)
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

const openEditModal = (post) => {
  resetForm()
  isEdit.value = true
  form.id = post.id
  form.title = post.title || ''
  form.categoryId = post.categoryId || null
  form.content = post.content || ''
  form.status = post.status ?? 1
  form.isTop = isPostTop(post)
  form.isHot = post.isHot === 1
  form.isEssence = post.isEssence === 1
  showModal.value = true
}

const closeModal = () => {
  showModal.value = false
}

const handleSubmit = async () => {
  errorMessage.value = ''
  successMessage.value = ''
  
  if (!form.title.trim()) {
    errorMessage.value = '请输入帖子标题'
    return
  }
  
  if (!form.content.trim()) {
    errorMessage.value = '请输入帖子内容'
    return
  }
  
  if (!form.categoryId) {
    errorMessage.value = '请选择所属版块'
    return
  }
  
  isSubmitting.value = true
  
  try {
    const url = isEdit.value 
      ? `/api/admin/forum-posts/${form.id}` 
      : '/api/admin/forum-posts'
    const method = isEdit.value ? 'PUT' : 'POST'
    
    const response = await request(url, {
      method: method,
      body: JSON.stringify({
        title: form.title,
        categoryId: form.categoryId,
        content: form.content,
        status: form.status
      })
    })
    
    const data = await response.json()
    
    if (data.code === 200) {
      if (isEdit.value) {
        await updatePostTags(form.id, form.isTop, form.isHot, form.isEssence)
      } else {
        const postId = data.data?.id
        if (postId && (form.isTop || form.isHot || form.isEssence)) {
          await updatePostTags(postId, form.isTop, form.isHot, form.isEssence)
        }
      }
      
      successMessage.value = isEdit.value ? '更新成功' : '创建成功'
      setTimeout(() => {
        closeModal()
        loadPosts()
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

const updatePostTags = async (postId, isTop, isHot, isEssence) => {
  try {
    await request(`/api/admin/forum-posts/${postId}/top?isTop=${isTop}`, {
      method: 'PUT'
    })
    
    await request(`/api/admin/forum-posts/${postId}/hot?isHot=${isHot}`, {
      method: 'PUT'
    })
    
    await request(`/api/admin/forum-posts/${postId}/essence?isEssence=${isEssence}`, {
      method: 'PUT'
    })
  } catch (error) {
    console.error('更新标签失败:', error)
  }
}

const confirmDelete = (post) => {
  deletingPost.value = post
  showDeleteModal.value = true
}

const closeDeleteModal = () => {
  showDeleteModal.value = false
  deletingPost.value = null
}

const handleDelete = async () => {
  if (!deletingPost.value) return
  
  isDeleting.value = true
  
  try {
    const response = await request(`/api/admin/forum-posts/${deletingPost.value.id}`, {
      method: 'DELETE'
    })
    
    const data = await response.json()
    
    if (data.code === 200) {
      closeDeleteModal()
      loadPosts()
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

const toggleTop = async (post) => {
  const newIsTop = !isPostTop(post)
  
  try {
    const response = await request(`/api/admin/forum-posts/${post.id}/top?isTop=${newIsTop}`, {
      method: 'PUT'
    })
    
    const data = await response.json()
    
    if (data.code === 200) {
      loadPosts()
    } else {
      alert(data.message || '操作失败')
    }
  } catch (error) {
    console.error('切换置顶失败:', error)
    alert('网络错误，请稍后重试')
  }
}

const toggleHot = async (post) => {
  const newIsHot = post.isHot !== 1
  
  try {
    const response = await request(`/api/admin/forum-posts/${post.id}/hot?isHot=${newIsHot}`, {
      method: 'PUT'
    })
    
    const data = await response.json()
    
    if (data.code === 200) {
      loadPosts()
    } else {
      alert(data.message || '操作失败')
    }
  } catch (error) {
    console.error('切换热门失败:', error)
    alert('网络错误，请稍后重试')
  }
}

const toggleEssence = async (post) => {
  const newIsEssence = post.isEssence !== 1
  
  try {
    const response = await request(`/api/admin/forum-posts/${post.id}/essence?isEssence=${newIsEssence}`, {
      method: 'PUT'
    })
    
    const data = await response.json()
    
    if (data.code === 200) {
      loadPosts()
    } else {
      alert(data.message || '操作失败')
    }
  } catch (error) {
    console.error('切换精华失败:', error)
    alert('网络错误，请稍后重试')
  }
}

const toggleStatus = async (post) => {
  const newStatus = post.status === 1 ? 0 : 1
  
  try {
    const response = await request(`/api/admin/forum-posts/${post.id}/status?status=${newStatus}`, {
      method: 'PUT'
    })
    
    const data = await response.json()
    
    if (data.code === 200) {
      loadPosts()
    } else {
      alert(data.message || '操作失败')
    }
  } catch (error) {
    console.error('切换状态失败:', error)
    alert('网络错误，请稍后重试')
  }
}

const formatDate = (dateStr) => {
  if (!dateStr) return '-'
  const date = new Date(dateStr)
  return date.toLocaleDateString('zh-CN')
}

onMounted(() => {
  loadCategories()
  loadPosts()
})
</script>

<style scoped>
.forum-post-management {
  display: flex;
  flex-direction: column;
  gap: 24px;
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
  min-width: 120px;
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

.loading-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 60px 20px;
  background: white;
  border-radius: 16px;
}

.loading-spinner {
  width: 40px;
  height: 40px;
  border: 4px solid #e4e8eb;
  border-top-color: #6B8E23;
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin-bottom: 16px;
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

.loading-state p {
  color: #7f8c8d;
  margin: 0;
}

.post-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.post-card {
  display: flex;
  align-items: stretch;
  background: white;
  border-radius: 16px;
  padding: 20px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  transition: all 0.3s ease;
  gap: 20px;
}

.post-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.12);
}

.post-main {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 12px;
  min-width: 0;
}

.post-header {
  display: flex;
  align-items: center;
  gap: 12px;
  flex-wrap: wrap;
}

.post-title {
  font-size: 1.15rem;
  color: #2c3e50;
  margin: 0;
  font-weight: 600;
  display: flex;
  align-items: center;
  gap: 8px;
  flex-wrap: wrap;
}

.post-tag {
  padding: 2px 8px;
  border-radius: 4px;
  font-size: 0.75rem;
  font-weight: 600;
}

.post-tag.top {
  background: #fef3c7;
  color: #d97706;
}

.post-tag.essence {
  background: #fce7f3;
  color: #db2777;
}

.post-tag.hot {
  background: #fee2e2;
  color: #dc2626;
}

.post-excerpt {
  color: #7f8c8d;
  font-size: 0.95rem;
  line-height: 1.6;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.post-meta {
  display: flex;
  gap: 16px;
  flex-wrap: wrap;
}

.meta-item {
  color: #95a5a6;
  font-size: 0.85rem;
}

.post-status {
  flex-shrink: 0;
  display: flex;
  align-items: flex-start;
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

.post-actions {
  display: flex;
  flex-direction: column;
  gap: 8px;
  flex-shrink: 0;
}

.action-row {
  display: flex;
  gap: 8px;
}

.action-btn {
  padding: 6px 12px;
  border: none;
  border-radius: 6px;
  font-size: 0.8rem;
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

.action-btn.tag-btn {
  background: #f3f4f6;
  color: #6b7280;
}

.action-btn.tag-btn:hover {
  background: #e5e7eb;
  color: #374151;
}

.action-btn.tag-btn.active {
  background: linear-gradient(135deg, #6B8E23, #8FBC8F);
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
  min-height: 150px;
  font-family: inherit;
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

.checkbox-group {
  display: flex;
  gap: 24px;
  flex-wrap: wrap;
}

.checkbox-label {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  font-size: 0.95rem;
  color: #555;
}

.checkbox-label input {
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
  
  .post-card {
    flex-direction: column;
    align-items: stretch;
  }
  
  .post-actions {
    flex-direction: row;
    flex-wrap: wrap;
  }
  
  .action-row {
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
}
</style>
