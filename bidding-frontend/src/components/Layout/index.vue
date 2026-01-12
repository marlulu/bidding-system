<template>
  <el-container class="layout-container">
    <!-- 固定顶部导航栏 -->
    <el-header class="header">
      <div class="header-content">
        <div class="logo" @click="router.push('/')">
          <el-icon :size="30" color="#fff"><Platform /></el-icon>
          <span>内部招标采购门户</span>
        </div>
        
        <el-menu
          mode="horizontal"
          :default-active="activeMenu"
          background-color="#003366"
          text-color="#fff"
          active-text-color="#FF6600"
          router
          class="nav-menu"
        >
          <el-menu-item index="/">首页</el-menu-item>
          <el-menu-item index="/announcements">招标信息</el-menu-item>
          <el-menu-item index="/suppliers">供应商库</el-menu-item>
          <el-menu-item index="/policies">政策法规</el-menu-item>
          <el-menu-item index="/notices">通知公告</el-menu-item>
        </el-menu>

        <div class="user-info">
          <!-- 已登录状态：显示头像下拉菜单 -->
          <template v-if="userStore.isLoggedIn">
            <el-dropdown @command="handleCommand" trigger="hover">
              <span class="el-dropdown-link">
                <el-avatar :size="36" :src="userStore.userInfo?.avatar || defaultAvatar" />
                <span class="username">{{ userStore.userInfo?.realName || userStore.userInfo?.username }}</span>
                <el-icon class="el-icon--right"><arrow-down /></el-icon>
              </span>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item command="profile">个人中心</el-dropdown-item>
                  <el-dropdown-item command="favorites">我的收藏</el-dropdown-item>
                  <el-dropdown-item v-if="userStore.isAdmin()" command="admin">后台管理</el-dropdown-item>
                  <el-dropdown-item divided command="logout">退出登录</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </template>
          
          <!-- 未登录状态：显示登录和注册按钮 -->
          <template v-else>
            <el-button type="primary" plain @click="showLoginDialog = true">登录</el-button>
            <el-button type="warning" @click="router.push('/register')">注册</el-button>
          </template>
        </div>
      </div>
    </el-header>

    <!-- 主要内容区 -->
    <el-main class="main">
      <router-view v-slot="{ Component }">
        <transition name="fade" mode="out-in">
          <component :is="Component" />
        </transition>
      </router-view>
    </el-main>

    <!-- 底部信息栏 -->
    <el-footer class="footer">
      <div class="footer-content">
        <div class="footer-section">
          <h4>关于我们</h4>
          <p>内部招标采购门户致力于提供公平、公正、公开的招标环境。</p>
        </div>
        <div class="footer-section">
          <h4>联系方式</h4>
          <p>电话：021-12345678</p>
          <p>邮箱：support@bidding.com</p>
        </div>
        <div class="footer-section">
          <h4>快速链接</h4>
          <el-link href="#" :underline="false">使用指南</el-link>
          <el-link href="#" :underline="false">常见问题</el-link>
        </div>
      </div>
      <div class="copyright">
        Copyright © 2026 内部招标采购门户 版权所有
      </div>
    </el-footer>

    <!-- 登录弹窗 -->
    <el-dialog v-model="showLoginDialog" title="用户登录" width="400px" center :close-on-click-modal="false">
      <el-form :model="loginForm" label-position="top">
        <el-form-item label="用户名">
          <el-input v-model="loginForm.username" placeholder="请输入用户名" @keyup.enter="handleLogin" />
        </el-form-item>
        <el-form-item label="密码">
          <el-input v-model="loginForm.password" type="password" show-password placeholder="请输入密码" @keyup.enter="handleLogin" />
        </el-form-item>
        <div class="login-options">
          <el-checkbox v-model="loginForm.remember">记住登录</el-checkbox>
          <el-link type="primary" :underline="false">忘记密码？</el-link>
        </div>
      </el-form>
      <template #footer>
        <el-button type="primary" @click="handleLogin" :loading="loading" style="width: 100%">立即登录</el-button>
        <div style="margin-top: 15px; text-align: center">
          还没有账号？<el-link type="warning" @click="showLoginDialog = false; router.push('/register')">立即注册</el-link>
        </div>
      </template>
    </el-dialog>
  </el-container>
</template>

<script setup>
import { ref, computed, reactive, watch, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { Platform, ArrowDown } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const defaultAvatar = 'https://api.dicebear.com/7.x/avataaars/svg?seed=default'

const activeMenu = computed(() => {
  const path = route.path
  if (path === '/') return '/'
  if (path.startsWith('/announcements')) return '/announcements'
  if (path.startsWith('/suppliers')) return '/suppliers'
  if (path.startsWith('/policies')) return '/policies'
  if (path.startsWith('/notices')) return '/notices'
  return path
})

const showLoginDialog = ref(false)
const loading = ref(false)

const loginForm = reactive({
  username: '',
  password: '',
  remember: false
})

// 监听路由变化，如果跳转到 /login 且有 redirect 参数，则弹出登录框
watch(() => route.path, (newPath) => {
  if (newPath === '/login' && route.query.redirect) {
    showLoginDialog.value = true
  }
}, { immediate: true })

const handleLogin = async () => {
  if (!loginForm.username || !loginForm.password) {
    return ElMessage.warning('请输入用户名和密码')
  }
  loading.value = true
  try {
    await userStore.login(loginForm)
    ElMessage.success('登录成功')
    showLoginDialog.value = false
    
    // 登录成功后，如果有重定向地址，则跳转
    const redirect = route.query.redirect
    if (redirect) {
      router.push(redirect)
    } else if (route.path === '/login') {
      router.push('/')
    }
  } catch (error) {
    ElMessage.error(error.message || '登录失败')
  } finally {
    loading.value = false
  }
}

const handleCommand = (command) => {
  if (command === 'logout') {
    userStore.logout()
    router.push('/')
    ElMessage.success('已退出登录')
  } else {
    router.push(`/${command}`)
  }
}

onMounted(() => {
  if (route.path === '/login') {
    showLoginDialog.value = true
  }
})
</script>

<style scoped>
.layout-container {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.header {
  background-color: #003366;
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 1000;
  height: 70px !important;
  box-shadow: 0 2px 10px rgba(0,0,0,0.2);
}

.header-content {
  max-width: 1200px;
  margin: 0 auto;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.logo {
  display: flex;
  align-items: center;
  cursor: pointer;
  color: #fff;
  font-size: 20px;
  font-weight: bold;
}

.logo span {
  margin-left: 10px;
}

.nav-menu {
  flex: 1;
  margin: 0 40px;
  border-bottom: none;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 15px;
}

.el-dropdown-link {
  display: flex;
  align-items: center;
  color: #fff;
  cursor: pointer;
  padding: 5px 10px;
  border-radius: 4px;
  transition: background 0.3s;
}

.el-dropdown-link:hover {
  background: rgba(255,255,255,0.1);
}

.username {
  margin: 0 8px;
  font-size: 14px;
}

.main {
  margin-top: 70px;
  padding: 0;
  flex: 1;
}

.footer {
  background-color: #333;
  color: #fff;
  padding: 40px 0 20px;
  height: auto !important;
}

.footer-content {
  max-width: 1200px;
  margin: 0 auto;
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 40px;
  padding-bottom: 30px;
}

.footer-section h4 {
  color: #FF6600;
  margin-bottom: 20px;
}

.footer-section p, .footer-section .el-link {
  color: #ccc;
  font-size: 14px;
  line-height: 1.8;
  margin-bottom: 8px;
}

.copyright {
  text-align: center;
  border-top: 1px solid #444;
  padding-top: 20px;
  font-size: 12px;
  color: #888;
}

.login-options {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}

.fade-enter-active, .fade-leave-active {
  transition: opacity 0.3s ease;
}

.fade-enter-from, .fade-leave-to {
  opacity: 0;
}

@media (max-width: 768px) {
  .nav-menu {
    display: none;
  }
  .footer-content {
    grid-template-columns: 1fr;
    padding: 0 20px;
  }
}
</style>
