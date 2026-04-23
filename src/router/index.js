import { createRouter, createWebHistory } from 'vue-router'
import Home from '../views/Home.vue'
import Services from '../views/Services.vue'
import ServiceDetail from '../views/ServiceDetail.vue'
import Forum from '../views/Forum.vue'
import CreatePost from '../views/CreatePost.vue'
import About from '../views/About.vue'
import Contact from '../views/Contact.vue'
import Login from '../views/Login.vue'
import Register from '../views/Register.vue'
import Orders from '../views/Orders.vue'
import Announcements from '../views/Announcements.vue'
import AdminLayout from '../views/admin/AdminLayout.vue'
import AdminCarousel from '../views/admin/AdminCarousel.vue'
import AdminIntro from '../views/admin/AdminIntro.vue'
import AdminServiceCategory from '../views/admin/AdminServiceCategory.vue'
import AdminService from '../views/admin/AdminService.vue'
import AdminForumPost from '../views/admin/AdminForumPost.vue'
import AdminForumCategory from '../views/admin/AdminForumCategory.vue'
import AdminOrder from '../views/admin/AdminOrder.vue'
import AdminContactMessage from '../views/admin/AdminContactMessage.vue'
import AdminContactCard from '../views/admin/AdminContactCard.vue'
import AdminWorkingHour from '../views/admin/AdminWorkingHour.vue'
import AdminFAQ from '../views/admin/AdminFAQ.vue'
import AdminFeature from '../views/admin/AdminFeature.vue'
import AdminAboutMission from '../views/admin/AdminAboutMission.vue'
import AdminAboutValue from '../views/admin/AdminAboutValue.vue'
import AdminAboutStat from '../views/admin/AdminAboutStat.vue'
import AdminAboutTeam from '../views/admin/AdminAboutTeam.vue'
import AdminAnnouncement from '../views/admin/AdminAnnouncement.vue'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: Login,
    meta: { requiresAuth: false }
  },
  {
    path: '/register',
    name: 'Register',
    component: Register,
    meta: { requiresAuth: false }
  },
  {
    path: '/',
    name: 'Home',
    component: Home,
    meta: { requiresAuth: true, userType: 'user' }
  },
  {
    path: '/services',
    name: 'Services',
    component: Services,
    meta: { requiresAuth: true, userType: 'user' }
  },
  {
    path: '/services/:id',
    name: 'ServiceDetail',
    component: ServiceDetail,
    meta: { requiresAuth: true, userType: 'user' }
  },
  {
    path: '/forum',
    name: 'Forum',
    component: Forum,
    meta: { requiresAuth: true, userType: 'user' }
  },
  {
    path: '/forum/create',
    name: 'CreatePost',
    component: CreatePost,
    meta: { requiresAuth: true, userType: 'user' }
  },
  {
    path: '/about',
    name: 'About',
    component: About,
    meta: { requiresAuth: true, userType: 'user' }
  },
  {
    path: '/contact',
    name: 'Contact',
    component: Contact,
    meta: { requiresAuth: true, userType: 'user' }
  },
  {
    path: '/orders',
    name: 'Orders',
    component: Orders,
    meta: { requiresAuth: true, userType: 'user' }
  },
  {
    path: '/announcements',
    name: 'Announcements',
    component: Announcements,
    meta: { requiresAuth: true, userType: 'user' }
  },
  {
    path: '/admin',
    component: AdminLayout,
    meta: { requiresAuth: true, userType: 'admin' },
    children: [
      {
        path: '',
        redirect: '/admin/carousel'
      },
      {
        path: 'home',
        redirect: '/admin/carousel'
      },
      {
        path: 'carousel',
        name: 'AdminCarousel',
        component: AdminCarousel,
        meta: { requiresAuth: true, userType: 'admin' }
      },
      {
        path: 'intro',
        name: 'AdminIntro',
        component: AdminIntro,
        meta: { requiresAuth: true, userType: 'admin' }
      },
      {
        path: 'service-category',
        name: 'AdminServiceCategory',
        component: AdminServiceCategory,
        meta: { requiresAuth: true, userType: 'admin' }
      },
      {
        path: 'service',
        name: 'AdminService',
        component: AdminService,
        meta: { requiresAuth: true, userType: 'admin' }
      },
      {
        path: 'forum-category',
        name: 'AdminForumCategory',
        component: AdminForumCategory,
        meta: { requiresAuth: true, userType: 'admin' }
      },
      {
        path: 'forum-post',
        name: 'AdminForumPost',
        component: AdminForumPost,
        meta: { requiresAuth: true, userType: 'admin' }
      },
      {
        path: 'order',
        name: 'AdminOrder',
        component: AdminOrder,
        meta: { requiresAuth: true, userType: 'admin' }
      },
      {
        path: 'message',
        name: 'AdminContactMessage',
        component: AdminContactMessage,
        meta: { requiresAuth: true, userType: 'admin' }
      },
      {
        path: 'contact-card',
        name: 'AdminContactCard',
        component: AdminContactCard,
        meta: { requiresAuth: true, userType: 'admin' }
      },
      {
        path: 'working-hour',
        name: 'AdminWorkingHour',
        component: AdminWorkingHour,
        meta: { requiresAuth: true, userType: 'admin' }
      },
      {
        path: 'faq',
        name: 'AdminFAQ',
        component: AdminFAQ,
        meta: { requiresAuth: true, userType: 'admin' }
      },
      {
        path: 'feature',
        name: 'AdminFeature',
        component: AdminFeature,
        meta: { requiresAuth: true, userType: 'admin' }
      },
      {
        path: 'about-mission',
        name: 'AdminAboutMission',
        component: AdminAboutMission,
        meta: { requiresAuth: true, userType: 'admin' }
      },
      {
        path: 'about-value',
        name: 'AdminAboutValue',
        component: AdminAboutValue,
        meta: { requiresAuth: true, userType: 'admin' }
      },
      {
        path: 'about-stat',
        name: 'AdminAboutStat',
        component: AdminAboutStat,
        meta: { requiresAuth: true, userType: 'admin' }
      },
      {
        path: 'about-team',
        name: 'AdminAboutTeam',
        component: AdminAboutTeam,
        meta: { requiresAuth: true, userType: 'admin' }
      },
      {
        path: 'announcement',
        name: 'AdminAnnouncement',
        component: AdminAnnouncement,
        meta: { requiresAuth: true, userType: 'admin' }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes,
  scrollBehavior(to, from, savedPosition) {
    if (savedPosition) {
      return savedPosition
    } else {
      return { top: 0 }
    }
  }
})

router.beforeEach((to, from, next) => {
  const isLoggedIn = localStorage.getItem('isLoggedIn') === 'true'
  const userType = localStorage.getItem('userType') || 'user'
  
  if (to.meta.requiresAuth && !isLoggedIn) {
    next('/login')
  } else if (to.path === '/login' && isLoggedIn) {
    if (userType === 'admin') {
      next('/admin/carousel')
    } else {
      next('/')
    }
  } else if (to.meta.userType && to.meta.userType !== userType && isLoggedIn) {
    if (userType === 'admin') {
      next('/admin/carousel')
    } else {
      next('/')
    }
  } else {
    next()
  }
})

export default router
