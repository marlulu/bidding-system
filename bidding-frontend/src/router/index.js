import { createRouter, createWebHistory } from 'vue-router'
import { getToken } from '@/utils/auth'

const routes = [
  {
    path: '/',
    component: () => import('@/components/Layout/index.vue'),
    redirect: '/dashboard',
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('@/views/Dashboard.vue'),
        meta: { title: '首页', requiresAuth: false }
      },
      {
        path: 'announcements',
        name: 'AnnouncementList',
        component: () => import('@/views/announcement/List.vue'),
        meta: { title: '招标信息', requiresAuth: false }
      },
      {
        path: 'announcements/detail/:id',
        name: 'AnnouncementDetail',
        component: () => import('@/views/announcement/Detail.vue'),
        meta: { title: '招标公告详情', requiresAuth: true }
      },
      {
        path: 'suppliers',
        name: 'SupplierList',
        component: () => import('@/views/supplier/List.vue'),
        meta: { title: '供应商管理', requiresAuth: false }
      },
      {
        path: 'suppliers/detail/:id',
        name: 'SupplierDetail',
        component: () => import('@/views/supplier/Detail.vue'),
        meta: { title: '供应商详情', requiresAuth: true }
      },
      {
        path: 'policies',
        name: 'PolicyList',
        component: () => import('@/views/policy/List.vue'),
        meta: { title: '政策法规', requiresAuth: false }
      },
      {
        path: 'policies/detail/:id',
        name: 'PolicyDetail',
        component: () => import('@/views/policy/Detail.vue'),
        meta: { title: '政策法规详情', requiresAuth: true }
      },
      {
        path: 'notices',
        name: 'NoticeList',
        component: () => import('@/views/notice/List.vue'),
        meta: { title: '系统通知', requiresAuth: true }
      },
      {
        path: 'profile',
        name: 'Profile',
        component: () => import('@/views/user/Profile.vue'),
        meta: { title: '个人中心', requiresAuth: true }
      },
      {
        path: 'favorites',
        name: 'Favorites',
        component: () => import('@/views/user/Profile.vue'),
        meta: { title: '我的收藏', requiresAuth: true }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  const token = getToken()
  
  // 设置页面标题
  if (to.meta.title) {
    document.title = `${to.meta.title} - 内部招标采购门户`
  }

  // 1. 检查路由元信息中的 requiresAuth
  // 显式判断 false，确保首页和列表页绝对放行
  if (to.meta.requiresAuth === false) {
    next()
    return
  }

  // 2. 如果需要登录但没有 token
  if (to.meta.requiresAuth && !token) {
    // 不再跳转到 /login 页面，而是留在当前页并触发登录弹窗
    // 我们通过 query 参数通知 Layout 组件弹出登录框
    if (to.path !== '/dashboard') {
      next({
        path: '/dashboard',
        query: { login: 'true', redirect: to.fullPath }
      })
    } else {
      next({
        path: '/dashboard',
        query: { login: 'true' }
      })
    }
  } else {
    next()
  }
})

export default router
