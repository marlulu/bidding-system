import { createRouter, createWebHistory } from 'vue-router'
import { getToken } from '@/utils/auth'
import { useUserStore } from '@/stores/user'

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
        path: 'suppliers/audit/:id',
        name: 'SupplierAuditDetail',
        component: () => import('@/views/supplier/AuditDetail.vue'),
        meta: { title: '审核供应商', requiresAuth: true, role: 'ADMIN' }
      },
      {
        path: 'suppliers/audit',
        name: 'SupplierAudit',
        component: () => import('@/views/supplier/Audit.vue'),
        meta: { title: '供应商审核列表', requiresAuth: true, role: 'ADMIN' }
      },
      {
        path: 'suppliers/create',
        name: 'SupplierCreate',
        component: () => import('@/views/supplier/Form.vue'),
        meta: { title: '新增供应商', requiresAuth: true, role: 'ADMIN' }
      },
      {
        path: 'suppliers/edit/:id',
        name: 'SupplierEdit',
        component: () => import('@/views/supplier/Form.vue'),
        meta: { title: '编辑供应商', requiresAuth: true, role: 'ADMIN' }
      },
      {
        path: 'announcements/create',
        name: 'AnnouncementCreate',
        component: () => import('@/views/announcement/Form.vue'),
        meta: { title: '发布招标公告', requiresAuth: true, role: 'ADMIN' }
      },
      {
        path: 'announcements/edit/:id',
        name: 'AnnouncementEdit',
        component: () => import('@/views/announcement/Form.vue'),
        meta: { title: '编辑招标公告', requiresAuth: true, role: 'ADMIN' }
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
        path: 'policies/create',
        name: 'PolicyCreate',
        component: () => import('@/views/policy/Form.vue'),
        meta: { title: '发布政策法规', requiresAuth: true, role: 'ADMIN' }
      },
      {
        path: 'policies/edit/:id',
        name: 'PolicyEdit',
        component: () => import('@/views/policy/Form.vue'),
        meta: { title: '编辑政策法规', requiresAuth: true, role: 'ADMIN' }
      },
      {
        path: 'notices',
        name: 'NoticeList',
        component: () => import('@/views/notice/List.vue'),
        meta: { title: '系统通知', requiresAuth: true }
      },
      {
        path: 'notices/detail/:id',
        name: 'NoticeDetail',
        component: () => import('@/views/notice/Detail.vue'),
        meta: { title: '通知详情', requiresAuth: true }
      },
      {
        path: 'notices/create',
        name: 'NoticeCreate',
        component: () => import('@/views/notice/Form.vue'),
        meta: { title: '发布通知', requiresAuth: true, role: 'ADMIN' }
      },
      {
        path: 'notices/edit/:id',
        name: 'NoticeEdit',
        component: () => import('@/views/notice/Form.vue'),
        meta: { title: '编辑通知', requiresAuth: true, role: 'ADMIN' }
      },
      {
        path: 'profile',
        name: 'Profile',
        component: () => import('@/views/user/Profile.vue'),
        meta: { title: '个人中心', requiresAuth: true }
      },

      {
        path: 'users',
        name: 'UserList',
        component: () => import('@/views/user/List.vue'),
        meta: { title: '后台管理', requiresAuth: true, role: 'ADMIN' }
      },
      {
        path: 'experts',
        name: 'ExpertList',
        component: () => import('@/views/expert/List.vue'),
        meta: { title: '专家库管理', requiresAuth: true, role: 'ADMIN' }
      },
      {
        path: 'bids/my',
        name: 'MyBids',
        component: () => import('@/views/bid/MyBids.vue'),
        meta: { title: '我的投标', requiresAuth: true, role: 'SUPPLIER' }
      },
      {
        path: 'announcements/:id/bids',
        name: 'AdminBidList',
        component: () => import('@/views/bid/AdminBidList.vue'),
        meta: { title: '投标管理', requiresAuth: true, role: 'ADMIN' }
      },
    ]
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/views/Register.vue'),
    meta: { title: '用户注册', requiresAuth: false }
  },
  {
    path: '/reset-password',
    name: 'ResetPassword',
    component: () => import('@/views/ResetPassword.vue'),
    meta: { title: '重置密码', requiresAuth: false }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  const token = getToken()
  const userStore = useUserStore()

  // 设置页面标题
  if (to.meta.title) {
    document.title = `${to.meta.title} - 重庆潼星建设有限公司招标采购网`
  }

  // 1. 检查路由元信息中的 requiresAuth
  if (to.meta.requiresAuth === false) {
    next()
    return
  }

  // 2. 如果需要登录但没有 token
  if (to.meta.requiresAuth && !token) {
    // 触发全局登录弹窗
    userStore.loginDialogVisible = true

    // 如果是从首页或列表页点击进入的，保持在原页面，不进行跳转
    // 这样用户登录成功后，可以直接再次点击或由逻辑自动触发进入
    if (from.path && from.path !== '/') {
      next(false) // 中断当前导航，留在原处
    } else {
      // 如果是直接输入 URL 进入受限页面，则跳转到首页并弹窗
      next({ path: '/', query: { redirect: to.fullPath } })
    }
  } else {
    next()
  }
})

export default router
