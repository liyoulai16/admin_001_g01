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
import AdminLayout from '../views/admin/AdminLayout.vue'
import AdminCarousel from '../views/admin/AdminCarousel.vue'
import AdminIntro from '../views/admin/AdminIntro.vue'

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
