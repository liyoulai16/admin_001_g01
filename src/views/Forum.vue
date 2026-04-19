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
                @click="selectCategory('全部')"
              >
                <span class="category-icon">📋</span>
                <span class="category-name">全部帖子</span>
                <span class="category-count">128</span>
              </li>
              <li 
                class="category-item" 
                :class="{ active: selectedCategory === '生活分享' }"
                @click="selectCategory('生活分享')"
              >
                <span class="category-icon">🏠</span>
                <span class="category-name">生活分享</span>
                <span class="category-count">45</span>
              </li>
              <li 
                class="category-item" 
                :class="{ active: selectedCategory === '互助问答' }"
                @click="selectCategory('互助问答')"
              >
                <span class="category-icon">❓</span>
                <span class="category-name">互助问答</span>
                <span class="category-count">32</span>
              </li>
              <li 
                class="category-item" 
                :class="{ active: selectedCategory === '邻里活动' }"
                @click="selectCategory('邻里活动')"
              >
                <span class="category-icon">🎉</span>
                <span class="category-name">邻里活动</span>
                <span class="category-count">28</span>
              </li>
              <li 
                class="category-item" 
                :class="{ active: selectedCategory === '二手交易' }"
                @click="selectCategory('二手交易')"
              >
                <span class="category-icon">🛒</span>
                <span class="category-name">二手交易</span>
                <span class="category-count">23</span>
              </li>
            </ul>
          </div>

          <div class="forum-section">
            <h3 class="section-title">热门话题</h3>
            <div class="hot-topics">
              <div 
                v-for="(topic, index) in hotTopics" 
                :key="index" 
                class="topic-item"
                @click="selectTopic(topic)"
              >
                <span class="topic-rank" :class="{ top: index < 3 }">{{ index + 1 }}</span>
                <span class="topic-text">{{ topic }}</span>
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
                <div class="user-avatar">{{ user.avatar }}</div>
                <div class="user-info">
                  <span class="user-name">{{ user.name }}</span>
                  <span class="user-posts">{{ user.posts }}篇帖子</span>
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
              <button class="new-post-btn" @click="showNewPostModal = true">
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
                  <span v-if="post.isHot" class="tag hot-tag">热门</span>
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
                  <span class="stat-item">
                    <span class="stat-icon">👍</span>
                    <span class="stat-value">{{ post.likes }}</span>
                  </span>
                </div>
                <button class="reply-btn" @click.stop="openReplyModal(post)">
                  <span>回复</span>
                </button>
              </div>
            </div>
          </div>

          <div class="pagination">
            <button class="page-btn prev" :disabled="currentPage === 1" @click="changePage(currentPage - 1)">上一页</button>
            <button 
              v-for="page in totalPages" 
              :key="page" 
              class="page-btn" 
              :class="{ active: currentPage === page }"
              @click="changePage(page)"
            >
              {{ page }}
            </button>
            <button class="page-btn next" :disabled="currentPage === totalPages" @click="changePage(currentPage + 1)">下一页</button>
          </div>
        </main>
      </div>
    </div>

    <div class="modal-overlay" :class="{ show: showNewPostModal }" @click="closeNewPostModal">
      <div class="modal-container" @click.stop>
        <div class="modal-header">
          <h3 class="modal-title">发布新帖</h3>
          <button class="modal-close-btn" @click="closeNewPostModal">×</button>
        </div>
        <div class="modal-body">
          <div class="form-group">
            <label class="form-label">选择板块</label>
            <select v-model="newPostForm.category" class="form-select">
              <option value="">请选择板块</option>
              <option value="生活分享">生活分享</option>
              <option value="互助问答">互助问答</option>
              <option value="邻里活动">邻里活动</option>
              <option value="二手交易">二手交易</option>
            </select>
          </div>
          <div class="form-group">
            <label class="form-label">帖子标题</label>
            <input 
              type="text" 
              v-model="newPostForm.title" 
              class="form-input"
              placeholder="请输入帖子标题（10-100字）"
              maxlength="100"
            />
            <span class="char-count">{{ newPostForm.title.length }}/100</span>
          </div>
          <div class="form-group">
            <label class="form-label">帖子内容</label>
            <textarea 
              v-model="newPostForm.content" 
              class="form-textarea"
              placeholder="请输入帖子内容..."
              rows="8"
              maxlength="2000"
            ></textarea>
            <span class="char-count">{{ newPostForm.content.length }}/2000</span>
          </div>
          <div class="form-group">
            <label class="form-label">添加图片（可选）</label>
            <div class="upload-area">
              <div class="upload-placeholder">
                <span class="upload-icon">📷</span>
                <span class="upload-text">点击或拖拽上传图片</span>
                <span class="upload-hint">支持 JPG、PNG 格式，最多 5 张</span>
              </div>
            </div>
          </div>
        </div>
        <div class="modal-footer">
          <button class="btn-secondary" @click="closeNewPostModal">取消</button>
          <button class="btn-primary" @click="submitNewPost">发布帖子</button>
        </div>
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
              </div>
            </div>
            
            <div class="post-detail-body">
              <p>{{ currentPost.content || currentPost.excerpt }}</p>
              <p>这是帖子的详细内容区域。在实际应用中，这里会显示完整的帖子内容，包括富文本格式、图片、视频等多媒体内容。</p>
              <p>社区论坛为居民提供了一个交流分享的平台，您可以在这里：</p>
              <ul>
                <li>分享生活中的点滴趣事</li>
                <li>向邻里求助或提供帮助</li>
                <li>组织和参与社区活动</li>
                <li>发布闲置物品交易信息</li>
              </ul>
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
              <div class="stat-card">
                <span class="stat-number">{{ currentPost.likes }}</span>
                <span class="stat-label">点赞</span>
              </div>
            </div>
            
            <div class="comments-section">
              <h4 class="comments-title">评论区 ({{ sampleComments.length }})</h4>
              <div class="comments-list">
                <div v-for="(comment, index) in sampleComments" :key="index" class="comment-item">
                  <div class="comment-header">
                    <div class="comment-avatar">{{ comment.avatar }}</div>
                    <div class="comment-info">
                      <span class="comment-author">{{ comment.author }}</span>
                      <span class="comment-time">{{ comment.time }}</span>
                    </div>
                  </div>
                  <p class="comment-content">{{ comment.content }}</p>
                  <div class="comment-actions">
                    <button class="action-btn">
                      <span>👍</span>
                      <span>{{ comment.likes }}</span>
                    </button>
                    <button class="action-btn">
                      <span>💬</span>
                      <span>回复</span>
                    </button>
                  </div>
                </div>
              </div>
              
              <div class="reply-box">
                <textarea 
                  v-model="replyContent" 
                  class="reply-textarea"
                  placeholder="写下您的评论..."
                  rows="3"
                ></textarea>
                <div class="reply-actions">
                  <button class="btn-primary" @click="submitReply">发表评论</button>
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
import { ref } from 'vue'

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
const sortBy = ref('latest')
const currentPage = ref(1)
const totalPages = ref(5)

const showNewPostModal = ref(false)
const showPostDetail = ref(false)
const currentPost = ref(null)
const replyContent = ref('')

const newPostForm = ref({
  category: '',
  title: '',
  content: ''
})

const hotTopics = [
  '周末小区运动会报名',
  '求助：水管漏水怎么办',
  '闲置物品交换群',
  '小区绿化建议征集',
  '邻里互助倡议书'
]

const activeUsers = [
  { avatar: '👨', name: '张大哥', posts: 89 },
  { avatar: '👩', name: '李阿姨', posts: 76 },
  { avatar: '🧑', name: '小王', posts: 65 },
  { avatar: '👴', name: '陈叔叔', posts: 58 }
]

const posts = ref([
  {
    id: 1,
    title: '【生活分享】周末组织了小区烧烤活动，大家都很开心！',
    excerpt: '上周末我们单元组织了一次烧烤聚会，大家一起准备食材、生火烤肉，气氛非常热闹。小朋友们在旁边玩游戏，大人们聊天交流...',
    content: '上周末我们单元组织了一次烧烤聚会，大家一起准备食材、生火烤肉，气氛非常热闹。小朋友们在旁边玩游戏，大人们聊天交流，增进了邻里之间的感情。希望以后能多组织这样的活动！',
    category: '生活分享',
    authorName: '张大哥',
    authorAvatar: '👨',
    createTime: '2024-01-15 14:30',
    views: 356,
    comments: 28,
    likes: 45,
    isEssence: true,
    isHot: true,
    images: ['🍖', '🔥', '🎉']
  },
  {
    id: 2,
    title: '【互助问答】求助：家里水管漏水了，有靠谱的维修师傅推荐吗？',
    excerpt: '厨房的水管突然漏水了，想找个靠谱的维修师傅，请问邻里们有推荐的吗？最好是价格公道、技术好的，谢谢大家！',
    content: '厨房的水管突然漏水了，想找个靠谱的维修师傅，请问邻里们有推荐的吗？最好是价格公道、技术好的，谢谢大家！',
    category: '互助问答',
    authorName: '李阿姨',
    authorAvatar: '👩',
    createTime: '2024-01-15 10:20',
    views: 234,
    comments: 15,
    likes: 12,
    isEssence: false,
    isHot: false,
    images: ['🔧']
  },
  {
    id: 3,
    title: '【邻里活动】小区春季运动会报名开始啦！',
    excerpt: '一年一度的小区春季运动会即将举行，现面向全体业主开放报名。比赛项目包括：趣味接力、亲子游戏、拔河比赛等...',
    content: '一年一度的小区春季运动会即将举行，现面向全体业主开放报名。比赛项目包括：趣味接力、亲子游戏、拔河比赛等，欢迎大家踊跃参与！',
    category: '邻里活动',
    authorName: '物业小王',
    authorAvatar: '🧑',
    createTime: '2024-01-14 16:45',
    views: 567,
    comments: 42,
    likes: 78,
    isEssence: true,
    isHot: true,
    images: ['🏃', '⚽', '🏆']
  },
  {
    id: 4,
    title: '【二手交易】九成新婴儿车转让，价格可议',
    excerpt: '家里宝宝长大了，婴儿车用不上了，转让给有需要的邻里。品牌是好孩子的，九成新，原价1200，现在400出，可小刀...',
    content: '家里宝宝长大了，婴儿车用不上了，转让给有需要的邻里。品牌是好孩子的，九成新，原价1200，现在400出，可小刀。有意者请在评论区留言或私信我。',
    category: '二手交易',
    authorName: '陈叔叔',
    authorAvatar: '👴',
    createTime: '2024-01-14 09:15',
    views: 189,
    comments: 8,
    likes: 5,
    isEssence: false,
    isHot: false,
    images: ['👶', '🚗']
  },
  {
    id: 5,
    title: '【生活分享】小区的腊梅花开了，真漂亮！',
    excerpt: '今早路过小区花园，发现腊梅花开得正盛，香气扑鼻。拍了几张照片和大家分享，也提醒大家注意保暖，天气冷了...',
    content: '今早路过小区花园，发现腊梅花开得正盛，香气扑鼻。拍了几张照片和大家分享，也提醒大家注意保暖，天气冷了多穿点衣服。',
    category: '生活分享',
    authorName: '王阿姨',
    authorAvatar: '👵',
    createTime: '2024-01-13 08:30',
    views: 423,
    comments: 35,
    likes: 56,
    isEssence: false,
    isHot: false,
    images: ['🌸', '🌺', '❄️']
  }
])

const sampleComments = [
  {
    avatar: '👩',
    author: '李阿姨',
    time: '2小时前',
    content: '写得真好！我也参加了那次活动，确实很开心。',
    likes: 12
  },
  {
    avatar: '🧑',
    author: '小王',
    time: '1小时前',
    content: '期待下次活动！我报名当志愿者帮忙组织。',
    likes: 8
  },
  {
    avatar: '👴',
    author: '陈叔叔',
    time: '30分钟前',
    content: '邻里关系就是要这样多交流，支持！',
    likes: 5
  }
]

const filteredPosts = ref([...posts.value])

const selectCategory = (category) => {
  selectedCategory.value = category
  if (category === '全部') {
    filteredPosts.value = [...posts.value]
  } else {
    filteredPosts.value = posts.value.filter(p => p.category === category)
  }
}

const selectTopic = (topic) => {
  searchQuery.value = topic
  handleSearch()
}

const handleSearch = () => {
  if (searchQuery.value.trim()) {
    filteredPosts.value = posts.value.filter(p => 
      p.title.includes(searchQuery.value) || 
      p.excerpt.includes(searchQuery.value)
    )
  } else {
    selectCategory(selectedCategory.value)
  }
}

const handleSort = () => {
  const sorted = [...filteredPosts.value]
  switch (sortBy.value) {
    case 'latest':
      sorted.sort((a, b) => new Date(b.createTime) - new Date(a.createTime))
      break
    case 'hot':
      sorted.sort((a, b) => b.views - a.views)
      break
    case 'essence':
      filteredPosts.value = posts.value.filter(p => p.isEssence)
      return
  }
  filteredPosts.value = sorted
}

const changePage = (page) => {
  currentPage.value = page
}

const openPostDetail = (post) => {
  currentPost.value = post
  showPostDetail.value = true
}

const closePostDetail = () => {
  showPostDetail.value = false
  currentPost.value = null
  replyContent.value = ''
}

const openReplyModal = (post) => {
  openPostDetail(post)
}

const closeNewPostModal = () => {
  showNewPostModal.value = false
  newPostForm.value = { category: '', title: '', content: '' }
}

const submitNewPost = () => {
  if (!newPostForm.value.category) {
    alert('请选择板块')
    return
  }
  if (!newPostForm.value.title.trim()) {
    alert('请输入标题')
    return
  }
  if (newPostForm.value.title.length < 10) {
    alert('标题至少10个字符')
    return
  }
  if (!newPostForm.value.content.trim()) {
    alert('请输入内容')
    return
  }
  
  alert('帖子发布成功！（演示模式）')
  closeNewPostModal()
}

const submitReply = () => {
  if (!replyContent.value.trim()) {
    alert('请输入评论内容')
    return
  }
  alert('评论发表成功！（演示模式）')
  replyContent.value = ''
}
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

.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 10px;
  margin-top: 30px;
}

.page-btn {
  padding: 10px 18px;
  border: 2px solid #e4e8eb;
  background: white;
  border-radius: 8px;
  font-size: 0.95rem;
  color: #555;
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
}
</style>
