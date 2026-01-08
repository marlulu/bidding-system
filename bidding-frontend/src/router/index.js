import { createRouter, createWebHistory } from 'vue-router'
import { getToken } from '@/utils/auth'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login.vue')
  },
  {
    path: '/',
    component: () => import('@/components/Layout/index.vue'),
    redirect: '/dashboard',
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('@/views/Dashboard.vue'),
        meta: { title: '首页' }
      },
      {
        path: 'announcements',
        name: 'AnnouncementList',
        component: () => import('@/views/announcement/List.vue'),
        meta: { title: '招标公告' }
      },
      {
        path: 'announcements/create',
        name: 'AnnouncementCreate',
        component: () => import('@/views/announcement/Form.vue'),
        meta: { title: '创建招标公告', requireAdmin: true }
      },
      {
        path: 'announcements/edit/:id',
        name: 'AnnouncementEdit',
        component: () => import('@/views/announcement/Form.vue'),
        meta: { title: '编辑招标公告', requireAdmin: true }
      },
      {
        path: 'announcements/detail/:id',
        name: 'AnnouncementDetail',
        component: () => import('@/views/announcement/Detail.vue'),
        meta: { title: '招标公告详情' }
      },
      {
        path: 'suppliers',
        name: 'SupplierList',
        component: () => import('@/views/supplier/List.vue'),
        meta: { title: '供应商管理' }
      },
      {
        path: 'suppliers/create',
        name: 'SupplierCreate',
        component: () => import('@/views/supplier/Form.vue'),
        meta: { title: '创建供应商', requireAdmin: true }
      },
      {
        path: 'suppliers/edit/:id',
        name: 'SupplierEdit',
        component: () => import('@/views/supplier/Form.vue'),
        meta: { title: '编辑供应商', requireAdmin: true }
      },
      {
        path: 'suppliers/detail/:id',
        name: 'SupplierDetail',
        component: () => import('@/views/supplier/Detail.vue'),
        meta: { title: '供应商详情' }
      },
      {
        path: 'policies',
        name: 'PolicyList',
        component: () => import('@/views/policy/List.vue'),
        meta: { title: '政策法规' }
      },
      {
        path: 'policies/create',
        name: 'PolicyCreate',
        component: () => import('@/views/policy/Form.vue'),
        meta: { title: '创建政策法规', requireAdmin: true }
      },
      {
        path: 'policies/edit/:id',
        name: 'PolicyEdit',
        component: () => import('@/views/policy/Form.vue'),
        meta: { title: '编辑政策法规', requireAdmin: true }
      },
      {
        path: 'policies/detail/:id',
        name: 'PolicyDetail',
        component: () => import('@/views/policy/Detail.vue'),
        meta: { title: '政策法规详情' }
      },
      {
        path: 'notices',
        name: 'NoticeList',
        component: () => import('@/views/notice/List.vue'),
        meta: { title: '系统通知' }
      },
      {
        path: 'notices/create',
        name: 'NoticeCreate',
        component: () => import('@/views/notice/Form.vue'),
        meta: { title: '创建系统通知', requireAdmin: true }
      },
      {
        path: 'notices/detail/:id',
        name: 'NoticeDetail',
        component: () => import('@/views/notice/Detail.vue'),
        meta: { title: '系统通知详情' }
      },
      {
        path: 'users',
        name: 'UserList',
        component: () => import('@/views/user/List.vue'),
        meta: { title: '用户管理', requireAdmin: true }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
  const token = getToken()
  
  if (to.path === '/login') {
    if (token) {
      next('/')
    } else {
      next()
    }
  } else {
    if (!token) {
      next('/login')
    } else {
      next()
    }
  }
})

export default router
