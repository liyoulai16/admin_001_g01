<template>
  <div class="forum">
    <div class="page-header">
      <div class="particles">
        <div v-for="i in 30" :key="i" class="particle" :style="getParticleStyle(i)"></div>
      </div>
      <div class="container">
        <h1 class="page-title">社区论坛</h1>
        <p class="page-subtitle">交流分享，共建美好社区</p>
      </div>
    </div>

    <div class="container">
      <div class="forum-content">
        <aside class="sidebar">
          <div class="forum-section">
            <h3 class="section-title">论坛板块</h3>
            <ul class="category-list">
              <li 
                class="category-item" 
                :class="{ active: selectedCategory === '全部' }"
                @click="selectCategory('全部', null)"
              >
                <span class="category-icon">📋</span>
                <span class="category-name">全部帖子</span>
                <span class="category-count">{{ totalPosts || 0 }}</span>
              </li>
              <li 
                v-for="category in categories" 
                :key="category.id"
                class="category-item" 
                :class="{ active: selectedCategory === category.name }"
                @click="selectCategory(category.name, category.id)"
              >
                <span class="category-icon">{{ category.icon || '📄' }}</span>
                <span class="category-name">{{ category.name }}</span>
                <span class="category-count">{{ category.postCount || 0 }}</span>
              </li>
            </ul>
          </div>

          <div class="forum-section">
            <h3 class="section-title">热门话题</h3>
            <div class="hot-topics">
              <div 
                v-if="hotTopics.length > 0"
                v-for="(topic, index) in hotTopics" 
                :key="topic.id || index" 
                class="topic-item"
                @click="selectTopic(topic)"
              >
                <span class="topic-rank" :class="{ top: index < 3 }">{{ index + 1 }}</span>
                <span class="topic-text">{{ topic.title || topic }}</span>
              </div>
              <div v-else class="no-hot-topics">
                <span>暂无热门话题</span>
              </div>
            </div>
          </div>

          <div class="forum-section">
            <h3 class="section-title">活跃用户</h3>
            <div class="active-users">
              <div 
                v-for="(user, index) in activeUsers" 
                :key="index" 
                class="user-item"
              >
                <div class="user-avatar">{{ user.avatar || '👤' }}</div>
                <div class="user-info">
                  <span class="user-name">{{ user.name || user.nickname || '匿名用户' }}</span>
                  <span class="user-posts">{{ user.posts || 0 }}篇帖子</span>
                </div>
              </div>
            </div>
          </div>
        </aside>

        <main class="main-content">
          <div class="forum-toolbar">
            <div class="search-box">
              <input 
                type="text" 
                v-model="searchQuery" 
                placeholder="搜索帖子内容..."
                @keyup.enter="handleSearch"
              />
              <button class="search-btn" @click="handleSearch">搜索</button>
            </div>
            <div class="toolbar-right">
              <button class="new-post-btn" @click="router.push('/forum/create')">
                <span class="btn-icon">✏️</span>
                <span>发布新帖</span>
              </button>
              <div class="sort-select">
                <select v-model="sortBy" @change="handleSort">
                  <option value="latest">最新发布</option>
                  <option value="hot">最热讨论</option>
                  <option value="essence">精华帖</option>
                </select>
              </div>
            </div>
          </div>

          <div class="posts-list">
            <div 
              v-for="post in filteredPosts" 
              :key="post.id" 
              class="post-card"
              @click="openPostDetail(post)"
            >
              <div class="post-header">
                <div class="post-author">
                  <div class="author-avatar">{{ post.authorAvatar }}</div>
                  <div class="author-info">
                    <span class="author-name">{{ post.authorName }}</span>
                    <span class="post-time">{{ post.createTime }}</span>
                  </div>
                </div>
                <div class="post-tags">
                  <span class="tag category-tag">{{ post.category }}</span>
                  <span v-if="post.isEssence" class="tag essence-tag">精华</span>
                  <span v-if="post.tag === 1" class="tag hot-tag">热门</span>
                  <span v-if="post.tag === 2" class="tag top-tag">置顶</span>
                  <span v-if="post.tag === 3" class="tag hot-top-tag">热门+置顶</span>
                </div>
              </div>
              
              <h3 class="post-title">{{ post.title }}</h3>
              <p class="post-excerpt">{{ post.excerpt }}</p>
              
              <div class="post-images" v-if="post.images && post.images.length > 0">
                <div 
                  v-for="(img, index) in post.images.slice(0, 3)" 
                  :key="index" 
                  class="post-image"
                >
                  <div class="image-placeholder">{{ img }}</div>
                </div>
              </div>
              
              <div class="post-footer">
                <div class="post-stats">
                  <span class="stat-item">
                    <span class="stat-icon">👁️</span>
                    <span class="stat-value">{{ post.views }}</span>
                  </span>
                  <span class="stat-item">
                    <span class="stat-icon">💬</span>
                    <span class="stat-value">{{ post.comments }}</span>
                  </span>
                  <button 
                    class="stat-item like-btn" 
                    :class="{ liked: post.isLiked }"
                    @click.stop="likePost(post.id)"
                  >
                    <span class="stat-icon">{{ post.isLiked ? '❤️' : '🤍' }}</span>
                    <span class="stat-value">{{ post.likes }}</span>
                  </button>
                </div>
                <button class="reply-btn" @click.stop="openReplyModal(post)">
                  <span>回复</span>
                </button>
              </div>
            </div>
          </div>

          <div class="pagination-section">
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
                  <option :value="5">5条</option>
                  <option :value="10">10条</option>
                  <option :value="20">20条</option>
                  <option :value="50">50条</option>
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
                共 <span class="info-highlight">{{ totalPosts }}</span> 条记录
              </div>
            </div>
            
            <div class="pagination-toast" :class="{ show: showToast }">
              <span class="toast-icon">{{ toastIcon }}</span>
              <span class="toast-message">{{ toastMessage }}</span>
            </div>
          </div>
        </main>
      </div>
    </div>

    <div class="modal-overlay detail-modal" :class="{ show: showPostDetail }" @click="closePostDetail">
      <div class="modal-container large" @click.stop>
        <div class="modal-header">
          <h3 class="modal-title">{{ currentPost?.title }}</h3>
          <button class="modal-close-btn" @click="closePostDetail">×</button>
        </div>
        <div class="modal-body">
          <div v-if="currentPost" class="post-detail-content">
            <div class="post-detail-header">
              <div class="post-author">
                <div class="author-avatar large">{{ currentPost.authorAvatar }}</div>
                <div class="author-info">
                  <span class="author-name">{{ currentPost.authorName }}</span>
                  <span class="post-time">发布于 {{ currentPost.createTime }}</span>
                </div>
              </div>
              <div class="post-tags">
                <span class="tag category-tag">{{ currentPost.category }}</span>
                <span v-if="currentPost.isEssence" class="tag essence-tag">精华</span>
                <span v-if="currentPost.tag === 1" class="tag hot-tag">热门</span>
                <span v-if="currentPost.tag === 2" class="tag top-tag">置顶</span>
                <span v-if="currentPost.tag === 3" class="tag hot-top-tag">热门+置顶</span>
              </div>
            </div>
            
            <div class="post-detail-body">
              <p>{{ currentPost.content || currentPost.excerpt }}</p>
            </div>
            
            <div class="post-detail-stats">
              <div class="stat-card">
                <span class="stat-number">{{ currentPost.views }}</span>
                <span class="stat-label">浏览</span>
              </div>
              <div class="stat-card">
                <span class="stat-number">{{ currentPost.comments }}</span>
                <span class="stat-label">评论</span>
              </div>
              <div 
                class="stat-card like-card" 
                :class="{ liked: currentPost.isLiked }"
                @click="likePost(currentPost.id)"
                style="cursor: pointer;"
              >
                <span class="stat-number">{{ currentPost.likes }}</span>
                <span class="stat-label">{{ currentPost.isLiked ? '已点赞' : '点赞' }}</span>
              </div>
            </div>
            
            <div class="comments-section">
              <h4 class="comments-title">评论区 ({{ comments.length }})</h4>
              <div class="comments-list">
                <div v-for="comment in comments" :key="comment.id" class="comment-item">
                  <div class="comment-header">
                    <div class="comment-avatar">{{ comment.userAvatar }}</div>
                    <div class="comment-info">
                      <span class="comment-author">{{ comment.userName }}</span>
                      <span class="comment-time">{{ comment.createTime }}</span>
                    </div>
                  </div>
                  <p class="comment-content">{{ comment.content }}</p>
                  <div class="comment-actions">
                    <button 
                      class="action-btn" 
                      :class="{ liked: comment.isLiked }"
                      @click="likeComment(comment.id, false, null)"
                    >
                      <span>{{ comment.isLiked ? '❤️' : '🤍' }}</span>
                      <span>{{ comment.likes }}</span>
                    </button>
                    <button class="action-btn" @click="startReply(comment)">
                      <span>💬</span>
                      <span>回复</span>
                    </button>
                  </div>
                  
                  <div v-if="comment.replies && comment.replies.length > 0" class="replies-list">
                    <div v-for="reply in comment.replies" :key="reply.id" class="reply-item">
                      <div class="comment-header">
                        <div class="comment-avatar">{{ reply.userAvatar }}</div>
                        <div class="comment-info">
                          <span class="comment-author">
                            {{ reply.userName }}
                            <span v-if="reply.replyToUserName" class="reply-to">
                              回复 @{{ reply.replyToUserName }}
                            </span>
                          </span>
                          <span class="comment-time">{{ reply.createTime }}</span>
                        </div>
                      </div>
                      <p class="comment-content">{{ reply.content }}</p>
                      <div class="comment-actions">
                        <button 
                          class="action-btn" 
                          :class="{ liked: reply.isLiked }"
                          @click="likeComment(reply.id, true, comment)"
                        >
                          <span>{{ reply.isLiked ? '❤️' : '🤍' }}</span>
                          <span>{{ reply.likes }}</span>
                        </button>
                        <button class="action-btn" @click="startReply(reply)">
                          <span>💬</span>
                          <span>回复</span>
                        </button>
                      </div>
                    </div>
                  </div>
                </div>
                
                <div v-if="comments.length === 0" class="no-comments">
                  <span>暂无评论，快来抢沙发吧！</span>
                </div>
              </div>
              
              <div class="reply-box">
                <div v-if="replyingTo" class="replying-to">
                  <span>回复 @{{ replyingTo.userName || replyingTo.replyToUserName }}</span>
                  <button class="cancel-reply-btn" @click="cancelReply">取消</button>
                </div>
                <textarea 
                  v-model="replyContent" 
                  class="reply-textarea"
                  :placeholder="replyingTo ? '写下您的回复...' : '写下您的评论...'"
                  rows="3"
                ></textarea>
                <div class="reply-actions">
                  <button 
                    class="btn-primary" 
                    @click="replyingTo ? submitReply(replyingTo) : submitComment()"
                  >
                    {{ replyingTo ? '发表回复' : '发表评论' }}
                  </button>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import request from '../utils/request'

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

const searchQuery = ref('')
const selectedCategory = ref('全部')
const selectedCategoryId = ref(null)
const sortBy = ref('latest')
const currentPage = ref(1)
const pageSize = ref(10)
const totalPages = ref(1)
const totalPosts = ref(0)
const loading = ref(false)

const showPostDetail = ref(false)
const currentPost = ref(null)
const replyContent = ref('')
const replyingTo = ref(null)

const jumpPageInput = ref('')
const showToast = ref(false)
const toastMessage = ref('')
const toastIcon = ref('⚠️')
let toastTimer = null

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

const categories = ref([])
const activeUsers = ref([])
const posts = ref([])
const filteredPosts = ref([])
const hotTopics = ref([])
const comments = ref([])

const formatTime = (timeStr) => {
  if (!timeStr) return ''
  const date = new Date(timeStr)
  return date.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  }).replace(/\//g, '-')
}

const getTagInfo = (tag) => {
  if (!tag) return { isHot: false, isTop: false, label: '' }
  const t = Number(tag)
  return {
    isHot: t === 1 || t === 3,
    isTop: t === 2 || t === 3,
    label: t === 1 ? '热门' : t === 2 ? '置顶' : t === 3 ? '热门+置顶' : ''
  }
}

const getCategoryNameById = (id) => {
  const category = categories.value.find(c => c.id === id)
  return category ? category.name : '未知'
}

const fetchCategories = async () => {
  try {
    const response = await request('/api/forum/categories', {
      method: 'GET'
    })
    
    if (response.ok) {
      const data = await response.json()
      if (data.code === 200 && data.data) {
        categories.value = data.data
      }
    }
  } catch (error) {
    console.error('获取分类失败:', error)
  }
}

const fetchActiveUsers = async () => {
  try {
    const response = await request('/api/forum/active-users?limit=5', {
      method: 'GET'
    })
    
    if (response.ok) {
      const data = await response.json()
      if (data.code === 200 && data.data) {
        activeUsers.value = data.data
      }
    }
  } catch (error) {
    console.error('获取活跃用户失败:', error)
  }
}

const fetchPosts = async () => {
  loading.value = true
  try {
    const params = new URLSearchParams()
    params.append('page', currentPage.value)
    params.append('size', pageSize.value)
    params.append('sortBy', sortBy.value)
    
    if (selectedCategoryId.value) {
      params.append('categoryId', selectedCategoryId.value)
    }
    
    if (searchQuery.value.trim()) {
      params.append('keyword', searchQuery.value.trim())
    }
    
    const response = await request(`/api/forum/posts?${params.toString()}`, {
      method: 'GET'
    })
    
    if (response.ok) {
      const data = await response.json()
      if (data.code === 200 && data.data) {
        const postList = data.data.records || []
        postList.forEach(post => {
          if (!post.category && post.categoryId) {
            post.category = getCategoryNameById(post.categoryId)
          }
          if (!post.authorName) {
            post.authorName = '匿名用户'
          }
          if (!post.authorAvatar) {
            post.authorAvatar = '👤'
          }
          if (post.imageList && post.imageList.length > 0) {
            post.images = post.imageList
          } else {
            post.images = []
          }
          if (post.createTime) {
            const date = new Date(post.createTime)
            post.createTime = date.toLocaleString('zh-CN', {
              year: 'numeric',
              month: '2-digit',
              day: '2-digit',
              hour: '2-digit',
              minute: '2-digit'
            }).replace(/\//g, '-')
          }
        })
        posts.value = postList
        filteredPosts.value = postList
        totalPosts.value = data.data.total || 0
        totalPages.value = data.data.pages || 1
      }
    }
  } catch (error) {
    console.error('获取帖子列表失败:', error)
  } finally {
    loading.value = false
  }
}

const fetchHotPosts = async () => {
  try {
    const response = await request('/api/forum/hot-posts', {
      method: 'GET'
    })
    
    if (response.ok) {
      const data = await response.json()
      if (data.code === 200 && data.data) {
        hotTopics.value = data.data
      }
    }
  } catch (error) {
    console.error('获取热门帖子失败:', error)
    hotTopics.value = []
  }
}

const fetchComments = async (postId) => {
  try {
    const response = await request(`/api/forum/posts/${postId}/comments`, {
      method: 'GET'
    })
    
    if (response.ok) {
      const data = await response.json()
      if (data.code === 200 && data.data) {
        const commentList = data.data || []
        commentList.forEach(comment => {
          if (!comment.userName) comment.userName = '匿名用户'
          if (!comment.userAvatar) comment.userAvatar = '👤'
          if (comment.createTime) {
            comment.createTime = formatTime(comment.createTime)
          }
          if (comment.replies && comment.replies.length > 0) {
            comment.replies.forEach(reply => {
              if (!reply.userName) reply.userName = '匿名用户'
              if (!reply.userAvatar) reply.userAvatar = '👤'
              if (reply.createTime) {
                reply.createTime = formatTime(reply.createTime)
              }
            })
          }
        })
        comments.value = commentList
      }
    }
  } catch (error) {
    console.error('获取评论失败:', error)
    comments.value = []
  }
}

const fetchPostDetail = async (id) => {
  try {
    const response = await request(`/api/forum/posts/${id}`, {
      method: 'GET'
    })
    
    if (response.ok) {
      const data = await response.json()
      if (data.code === 200 && data.data) {
        const post = data.data
        if (!post.category && post.categoryId) {
          post.category = getCategoryNameById(post.categoryId)
        }
        if (!post.authorName) {
          post.authorName = '匿名用户'
        }
        if (!post.authorAvatar) {
          post.authorAvatar = '👤'
        }
        if (post.imageList && post.imageList.length > 0) {
          post.images = post.imageList
        } else {
          post.images = []
        }
        if (post.createTime) {
          post.createTime = formatTime(post.createTime)
        }
        currentPost.value = post
        
        await fetchComments(id)
      }
    }
  } catch (error) {
    console.error('获取帖子详情失败:', error)
  }
}

const submitComment = async () => {
  if (!currentPost.value) return
  
  if (!replyContent.value.trim()) {
    showToastMessage('请输入评论内容', '⚠️')
    return
  }
  
  try {
    const formData = new URLSearchParams()
    formData.append('content', replyContent.value)
    
    const response = await request(`/api/forum/posts/${currentPost.value.id}/comments?${formData.toString()}`, {
      method: 'POST'
    })
    
    if (response.ok) {
      const data = await response.json()
      if (data.code === 200) {
        showToastMessage('评论发表成功', '✅')
        replyContent.value = ''
        replyingTo.value = null
        await fetchComments(currentPost.value.id)
      } else {
        showToastMessage(data.message || '评论失败', '⚠️')
      }
    }
  } catch (error) {
    console.error('发表评论失败:', error)
    showToastMessage('评论失败，请稍后重试', '⚠️')
  }
}

const submitReply = async (comment) => {
  if (!currentPost.value || !comment) return
  
  if (!replyContent.value.trim()) {
    showToastMessage('请输入回复内容', '⚠️')
    return
  }
  
  try {
    const formData = new URLSearchParams()
    formData.append('parentId', comment.id)
    formData.append('content', replyContent.value)
    formData.append('replyToUserId', comment.userId || '')
    
    const response = await request(`/api/forum/posts/${currentPost.value.id}/comments/reply?${formData.toString()}`, {
      method: 'POST'
    })
    
    if (response.ok) {
      const data = await response.json()
      if (data.code === 200) {
        showToastMessage('回复发表成功', '✅')
        replyContent.value = ''
        replyingTo.value = null
        await fetchComments(currentPost.value.id)
      } else {
        showToastMessage(data.message || '回复失败', '⚠️')
      }
    }
  } catch (error) {
    console.error('发表回复失败:', error)
    showToastMessage('回复失败，请稍后重试', '⚠️')
  }
}

const likePost = async (postId) => {
  try {
    const response = await request(`/api/forum/posts/${postId}/like-v2`, {
      method: 'POST'
    })
    
    if (response.ok) {
      const data = await response.json()
      if (data.code === 200) {
        if (currentPost.value && currentPost.value.id === postId) {
          currentPost.value.likes = data.data.likes
          currentPost.value.isLiked = data.data.isLiked
        }
        
        const postInList = posts.value.find(p => p.id === postId)
        if (postInList) {
          postInList.likes = data.data.likes
          postInList.isLiked = data.data.isLiked
        }
        
        showToastMessage(data.data.message || '操作成功', '✅')
      }
    }
  } catch (error) {
    console.error('点赞失败:', error)
    showToastMessage('操作失败，请先登录', '⚠️')
  }
}

const likeComment = async (commentId, isReply = false, parentComment = null) => {
  try {
    const response = await request(`/api/forum/comments/${commentId}/like`, {
      method: 'POST'
    })
    
    if (response.ok) {
      const data = await response.json()
      if (data.code === 200) {
        if (!isReply) {
          const comment = comments.value.find(c => c.id === commentId)
          if (comment) {
            comment.likes = data.data.likes
            comment.isLiked = data.data.isLiked
          }
        } else if (parentComment) {
          const reply = parentComment.replies?.find(r => r.id === commentId)
          if (reply) {
            reply.likes = data.data.likes
            reply.isLiked = data.data.isLiked
          }
        }
        
        showToastMessage(data.data.message || '操作成功', '✅')
      }
    }
  } catch (error) {
    console.error('评论点赞失败:', error)
    showToastMessage('操作失败，请先登录', '⚠️')
  }
}

const startReply = (comment) => {
  replyingTo.value = comment
  replyContent.value = comment.replyToUserName ? `回复 @${comment.replyToUserName || comment.userName}：` : `回复 @${comment.userName}：`
}

const selectCategory = (category, categoryId = null) => {
  selectedCategory.value = category
  selectedCategoryId.value = categoryId
  currentPage.value = 1
  fetchPosts()
}

const selectTopic = (topic) => {
  if (topic && topic.id) {
    openPostDetail(topic)
  } else {
    searchQuery.value = topic
    currentPage.value = 1
    fetchPosts()
  }
}

const handleSearch = () => {
  currentPage.value = 1
  fetchPosts()
}

const handleSort = () => {
  currentPage.value = 1
  fetchPosts()
}

const handlePageSizeChange = () => {
  currentPage.value = 1
  fetchPosts()
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
  fetchPosts()
  window.scrollTo({ top: 0, behavior: 'smooth' })
}

const goToFirstPage = () => {
  if (currentPage.value === 1) {
    showToastMessage('已经是第一页了', '⚠️')
    return
  }
  goToPage(1)
}

const goToPrevPage = () => {
  if (currentPage.value === 1) {
    showToastMessage('已经是第一页了', '⚠️')
    return
  }
  goToPage(currentPage.value - 1)
}

const goToNextPage = () => {
  const totalPagesVal = totalPages.value || 1
  if (currentPage.value >= totalPagesVal) {
    showToastMessage('已经是最后一页了', '⚠️')
    return
  }
  goToPage(currentPage.value + 1)
}

const goToLastPage = () => {
  const totalPagesVal = totalPages.value || 1
  if (currentPage.value === totalPagesVal) {
    showToastMessage('已经是最后一页了', '⚠️')
    return
  }
  goToPage(totalPagesVal)
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

const openPostDetail = async (post) => {
  await fetchPostDetail(post.id)
  showPostDetail.value = true
}

const closePostDetail = () => {
  showPostDetail.value = false
  currentPost.value = null
  replyContent.value = ''
  replyingTo.value = null
  comments.value = []
}

const openReplyModal = (post) => {
  openPostDetail(post)
}

const cancelReply = () => {
  replyingTo.value = null
  replyContent.value = ''
}

onMounted(() => {
  fetchCategories()
  fetchActiveUsers()
  fetchPosts()
  fetchHotPosts()
})
</script>

<style scoped>
.forum {
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

.forum-content {
  display: flex;
  gap: 30px;
  padding: 40px 0;
}

.sidebar {
  width: 280px;
  flex-shrink: 0;
}

.forum-section {
  background: white;
  border-radius: 15px;
  padding: 25px;
  margin-bottom: 20px;
  box-shadow: 0 5px 20px rgba(0, 0, 0, 0.08);
}

.section-title {
  font-size: 1.1rem;
  margin-bottom: 15px;
  color: #2c3e50;
  font-weight: bold;
  padding-bottom: 10px;
  border-bottom: 2px solid #e4e8eb;
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
  align-items: center;
  gap: 10px;
}

.category-item:hover {
  background-color: #f5f7fa;
}

.category-item.active {
  background: linear-gradient(135deg, #6B8E23, #8FBC8F);
  color: white;
}

.category-icon {
  font-size: 1.2rem;
}

.category-name {
  flex: 1;
}

.category-count {
  background: rgba(0, 0, 0, 0.1);
  padding: 2px 8px;
  border-radius: 10px;
  font-size: 0.8rem;
}

.category-item.active .category-count {
  background: rgba(255, 255, 255, 0.2);
}

.hot-topics {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.topic-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 10px;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.topic-item:hover {
  background: #f5f7fa;
}

.topic-rank {
  width: 24px;
  height: 24px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #e4e8eb;
  border-radius: 50%;
  font-size: 0.85rem;
  font-weight: bold;
  color: #7f8c8d;
}

.topic-rank.top {
  background: linear-gradient(135deg, #C45B1A, #9E3B1B);
  color: white;
}

.topic-text {
  font-size: 0.9rem;
  color: #555;
  flex: 1;
}

.active-users {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.user-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 10px;
  border-radius: 8px;
  transition: all 0.3s ease;
}

.user-item:hover {
  background: #f5f7fa;
}

.user-avatar {
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #6B8E23, #8FBC8F);
  border-radius: 50%;
  font-size: 1.2rem;
}

.user-info {
  display: flex;
  flex-direction: column;
}

.user-name {
  font-size: 0.95rem;
  font-weight: 600;
  color: #2c3e50;
}

.user-posts {
  font-size: 0.8rem;
  color: #7f8c8d;
}

.main-content {
  flex: 1;
}

.forum-toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  gap: 20px;
}

.search-box {
  display: flex;
  gap: 10px;
  flex: 1;
  max-width: 400px;
}

.search-box input {
  flex: 1;
  padding: 12px 20px;
  border: 2px solid #e4e8eb;
  border-radius: 10px;
  font-size: 0.95rem;
  transition: border-color 0.3s ease;
}

.search-box input:focus {
  outline: none;
  border-color: #6B8E23;
}

.search-btn {
  padding: 12px 25px;
  background: linear-gradient(135deg, #6B8E23, #8FBC8F);
  color: white;
  border-radius: 10px;
  font-size: 0.95rem;
  font-weight: 600;
  transition: all 0.3s ease;
}

.search-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 5px 15px rgba(107, 142, 35, 0.3);
}

.toolbar-right {
  display: flex;
  align-items: center;
  gap: 15px;
}

.new-post-btn {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 25px;
  background: linear-gradient(135deg, #C45B1A, #9E3B1B);
  color: white;
  border-radius: 10px;
  font-size: 0.95rem;
  font-weight: 600;
  transition: all 0.3s ease;
}

.new-post-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 5px 15px rgba(196, 91, 26, 0.3);
}

.btn-icon {
  font-size: 1rem;
}

.sort-select select {
  padding: 12px 20px;
  border: 2px solid #e4e8eb;
  border-radius: 10px;
  font-size: 0.95rem;
  background: white;
  cursor: pointer;
  transition: border-color 0.3s ease;
}

.sort-select select:focus {
  outline: none;
  border-color: #6B8E23;
}

.posts-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.post-card {
  background: white;
  border-radius: 15px;
  padding: 25px;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 5px 20px rgba(0, 0, 0, 0.08);
  position: relative;
  overflow: hidden;
}

.post-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  width: 4px;
  height: 0;
  background: linear-gradient(135deg, #6B8E23, #8FBC8F);
  transition: height 0.3s ease;
}

.post-card:hover::before {
  height: 100%;
}

.post-card:hover {
  transform: translateY(-3px);
  box-shadow: 0 15px 30px rgba(0, 0, 0, 0.15);
}

.post-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 15px;
}

.post-author {
  display: flex;
  align-items: center;
  gap: 12px;
}

.author-avatar {
  width: 44px;
  height: 44px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #6B8E23, #8FBC8F);
  border-radius: 50%;
  font-size: 1.3rem;
}

.author-info {
  display: flex;
  flex-direction: column;
}

.author-name {
  font-size: 0.95rem;
  font-weight: 600;
  color: #2c3e50;
}

.post-time {
  font-size: 0.8rem;
  color: #7f8c8d;
}

.post-tags {
  display: flex;
  gap: 8px;
}

.tag {
  padding: 4px 12px;
  border-radius: 20px;
  font-size: 0.8rem;
  font-weight: 500;
}

.category-tag {
  background: linear-gradient(135deg, #6B8E23, #8FBC8F);
  color: white;
}

.essence-tag {
  background: linear-gradient(135deg, #C45B1A, #9E3B1B);
  color: white;
}

.hot-tag {
  background: linear-gradient(135deg, #f39c12, #e67e22);
  color: white;
}

.top-tag {
  background: linear-gradient(135deg, #e74c3c, #c0392b);
  color: white;
}

.hot-top-tag {
  background: linear-gradient(135deg, #9b59b6, #8e44ad);
  color: white;
}

.like-btn {
  background: none;
  border: none;
  cursor: pointer;
  transition: all 0.3s ease;
}

.like-btn:hover {
  transform: scale(1.1);
}

.like-btn.liked .stat-icon {
  animation: heartbeat 0.3s ease;
}

@keyframes heartbeat {
  0% { transform: scale(1); }
  50% { transform: scale(1.3); }
  100% { transform: scale(1); }
}

.post-title {
  font-size: 1.2rem;
  margin-bottom: 10px;
  color: #2c3e50;
  font-weight: 600;
  line-height: 1.4;
}

.post-excerpt {
  font-size: 0.95rem;
  color: #7f8c8d;
  line-height: 1.6;
  margin-bottom: 15px;
}

.post-images {
  display: flex;
  gap: 10px;
  margin-bottom: 15px;
}

.post-image {
  width: 80px;
  height: 80px;
  border-radius: 8px;
  overflow: hidden;
}

.image-placeholder {
  width: 100%;
  height: 100%;
  background: linear-gradient(135deg, #f5f7fa, #e4e8eb);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 2rem;
}

.post-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 15px;
  border-top: 1px solid #e4e8eb;
}

.post-stats {
  display: flex;
  gap: 20px;
}

.stat-item {
  display: flex;
  align-items: center;
  gap: 5px;
  font-size: 0.9rem;
  color: #7f8c8d;
}

.stat-icon {
  font-size: 1rem;
}

.stat-value {
  font-weight: 500;
}

.reply-btn {
  padding: 8px 20px;
  background: linear-gradient(135deg, #6B8E23, #8FBC8F);
  color: white;
  border-radius: 20px;
  font-size: 0.85rem;
  font-weight: 500;
  transition: all 0.3s ease;
}

.reply-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 5px 15px rgba(107, 142, 35, 0.3);
}

.pagination-section {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-top: 40px;
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
  z-index: 2000;
  opacity: 0;
  visibility: hidden;
  transition: all 0.3s ease;
  padding: 20px;
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

.modal-container.large {
  max-width: 800px;
}

.modal-overlay.show .modal-container {
  transform: scale(1) translateY(0);
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 24px;
  border-bottom: 1px solid #e4e8eb;
}

.modal-title {
  font-size: 1.3rem;
  font-weight: 600;
  color: #2c3e50;
  margin: 0;
}

.modal-close-btn {
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

.modal-close-btn:hover {
  background: #C45B1A;
  color: white;
  transform: rotate(90deg);
}

.modal-body {
  padding: 24px;
}

.form-group {
  margin-bottom: 20px;
  position: relative;
}

.form-label {
  display: block;
  font-size: 0.95rem;
  font-weight: 500;
  color: #2c3e50;
  margin-bottom: 8px;
}

.form-select,
.form-input {
  width: 100%;
  padding: 12px 16px;
  border: 2px solid #e4e8eb;
  border-radius: 10px;
  font-size: 0.95rem;
  background: white;
  transition: border-color 0.3s ease;
}

.form-select:focus,
.form-input:focus {
  outline: none;
  border-color: #6B8E23;
}

.form-textarea {
  width: 100%;
  padding: 12px 16px;
  border: 2px solid #e4e8eb;
  border-radius: 10px;
  font-size: 0.95rem;
  resize: vertical;
  min-height: 120px;
  transition: border-color 0.3s ease;
}

.form-textarea:focus {
  outline: none;
  border-color: #6B8E23;
}

.char-count {
  position: absolute;
  bottom: -20px;
  right: 5px;
  font-size: 0.75rem;
  color: #7f8c8d;
}

.upload-area {
  border: 2px dashed #e4e8eb;
  border-radius: 10px;
  padding: 30px;
  text-align: center;
  transition: all 0.3s ease;
  cursor: pointer;
}

.upload-area:hover {
  border-color: #6B8E23;
  background: rgba(107, 142, 35, 0.02);
}

.upload-placeholder {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
}

.upload-icon {
  font-size: 2.5rem;
}

.upload-text {
  font-size: 0.95rem;
  color: #555;
  font-weight: 500;
}

.upload-hint {
  font-size: 0.8rem;
  color: #7f8c8d;
}

.modal-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  padding: 20px 24px;
  border-top: 1px solid #e4e8eb;
}

.btn-primary {
  padding: 12px 28px;
  background: linear-gradient(135deg, #6B8E23, #8FBC8F);
  color: white;
  border: none;
  border-radius: 10px;
  font-size: 0.95rem;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
}

.btn-primary:hover {
  transform: translateY(-2px);
  box-shadow: 0 5px 15px rgba(107, 142, 35, 0.3);
}

.btn-secondary {
  padding: 12px 28px;
  background: #f5f7fa;
  color: #7f8c8d;
  border: 1px solid #e4e8eb;
  border-radius: 10px;
  font-size: 0.95rem;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;
}

.btn-secondary:hover {
  background: #e8ecef;
  color: #555;
}

.post-detail-content {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.post-detail-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  padding-bottom: 20px;
  border-bottom: 1px solid #e4e8eb;
}

.author-avatar.large {
  width: 56px;
  height: 56px;
  font-size: 1.6rem;
}

.post-detail-body {
  font-size: 1rem;
  line-height: 1.8;
  color: #555;
}

.post-detail-body p {
  margin-bottom: 15px;
}

.post-detail-body ul {
  margin-left: 20px;
  margin-bottom: 15px;
}

.post-detail-body li {
  margin-bottom: 8px;
}

.post-detail-stats {
  display: flex;
  justify-content: center;
  gap: 40px;
  padding: 20px;
  background: linear-gradient(135deg, rgba(107, 142, 35, 0.05), rgba(143, 188, 143, 0.05));
  border-radius: 10px;
}

.stat-card {
  text-align: center;
}

.stat-number {
  display: block;
  font-size: 1.8rem;
  font-weight: bold;
  color: #6B8E23;
}

.stat-label {
  font-size: 0.85rem;
  color: #7f8c8d;
}

.comments-section {
  margin-top: 20px;
  padding-top: 20px;
  border-top: 1px solid #e4e8eb;
}

.comments-title {
  font-size: 1.1rem;
  font-weight: 600;
  color: #2c3e50;
  margin-bottom: 20px;
}

.comments-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
  margin-bottom: 25px;
}

.comment-item {
  padding: 15px;
  background: #f8f9fa;
  border-radius: 10px;
}

.comment-header {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 10px;
}

.comment-avatar {
  width: 36px;
  height: 36px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #6B8E23, #8FBC8F);
  border-radius: 50%;
  font-size: 1rem;
}

.comment-info {
  display: flex;
  flex-direction: column;
}

.comment-author {
  font-size: 0.9rem;
  font-weight: 600;
  color: #2c3e50;
}

.comment-time {
  font-size: 0.75rem;
  color: #7f8c8d;
}

.comment-content {
  font-size: 0.95rem;
  color: #555;
  line-height: 1.6;
  margin-bottom: 10px;
}

.comment-actions {
  display: flex;
  gap: 15px;
}

.action-btn {
  display: flex;
  align-items: center;
  gap: 5px;
  font-size: 0.85rem;
  color: #7f8c8d;
  background: none;
  cursor: pointer;
  transition: color 0.3s ease;
}

.action-btn:hover {
  color: #6B8E23;
}

.reply-box {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.reply-textarea {
  width: 100%;
  padding: 15px;
  border: 2px solid #e4e8eb;
  border-radius: 10px;
  font-size: 0.95rem;
  resize: vertical;
  min-height: 100px;
  transition: border-color 0.3s ease;
}

.reply-textarea:focus {
  outline: none;
  border-color: #6B8E23;
}

.reply-actions {
  display: flex;
  justify-content: flex-end;
}

.action-btn.liked {
  color: #e74c3c;
}

.replies-list {
  margin-left: 30px;
  margin-top: 15px;
  padding-left: 15px;
  border-left: 2px solid #e4e8eb;
}

.reply-item {
  padding: 10px;
  background: #f5f7fa;
  border-radius: 8px;
  margin-bottom: 10px;
}

.reply-item:last-child {
  margin-bottom: 0;
}

.reply-to {
  color: #6B8E23;
  font-size: 0.85rem;
  margin-left: 5px;
}

.replying-to {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8px 12px;
  background: linear-gradient(135deg, rgba(107, 142, 35, 0.1), rgba(143, 188, 143, 0.1));
  border-radius: 8px;
  margin-bottom: 10px;
}

.replying-to span {
  font-size: 0.9rem;
  color: #6B8E23;
  font-weight: 500;
}

.cancel-reply-btn {
  background: none;
  border: none;
  color: #7f8c8d;
  font-size: 0.85rem;
  cursor: pointer;
  transition: color 0.3s ease;
}

.cancel-reply-btn:hover {
  color: #e74c3c;
}

.no-comments {
  text-align: center;
  padding: 40px 20px;
  color: #7f8c8d;
  font-size: 1rem;
}

.no-hot-topics {
  text-align: center;
  padding: 20px 10px;
  color: #7f8c8d;
  font-size: 0.9rem;
}

.like-card {
  transition: all 0.3s ease;
}

.like-card:hover {
  transform: scale(1.05);
}

.like-card.liked .stat-number {
  color: #e74c3c;
}

.like-card.liked .stat-label {
  color: #e74c3c;
}

@media (max-width: 992px) {
  .forum-content {
    flex-direction: column;
  }

  .sidebar {
    width: 100%;
    display: flex;
    gap: 20px;
    overflow-x: auto;
  }

  .forum-section {
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

  .forum-content {
    padding: 20px 0;
  }

  .sidebar {
    flex-direction: column;
  }

  .forum-section {
    min-width: auto;
  }

  .forum-toolbar {
    flex-direction: column;
    align-items: stretch;
  }

  .search-box {
    max-width: none;
  }

  .toolbar-right {
    justify-content: space-between;
  }

  .new-post-btn,
  .sort-select select {
    flex: 1;
    text-align: center;
    justify-content: center;
  }

  .post-header {
    flex-direction: column;
    gap: 10px;
  }

  .post-tags {
    align-self: flex-start;
  }

  .post-images {
    flex-wrap: wrap;
  }

  .post-footer {
    flex-direction: column;
    gap: 15px;
    align-items: stretch;
  }

  .post-stats {
    justify-content: center;
  }

  .reply-btn {
    width: 100%;
    text-align: center;
  }

  .modal-container {
    width: 95%;
    margin: 10px;
    max-height: 90vh;
  }

  .modal-header {
    padding: 16px 20px;
  }

  .modal-body {
    padding: 20px;
  }

  .modal-footer {
    padding: 16px 20px;
  }

  .post-detail-header {
    flex-direction: column;
    gap: 15px;
  }

  .post-detail-stats {
    gap: 20px;
  }

  .stat-number {
    font-size: 1.5rem;
  }

  .pagination {
    flex-wrap: wrap;
    justify-content: center;
    gap: 6px;
  }

  .first-btn, .last-btn, .prev-btn, .next-btn {
    padding: 0 12px;
    font-size: 0.85rem;
    min-width: auto;
  }

  .page-btn {
    min-width: 34px;
    height: 34px;
    font-size: 0.85rem;
  }

  .page-size-select {
    width: 100%;
    justify-content: center;
    order: 98;
    margin-left: 0;
  }

  .page-jump {
    width: 100%;
    justify-content: center;
    order: 99;
    margin-left: 0;
  }

  .page-info {
    width: 100%;
    justify-content: center;
    order: 100;
  }
}
</style>
