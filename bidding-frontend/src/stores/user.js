import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { login as loginApi } from '@/api/auth'
import { setToken, removeToken, getToken } from '@/utils/auth'

export const useUserStore = defineStore('user', () => {
  const userInfo = ref(JSON.parse(localStorage.getItem('userInfo') || 'null'))
  const token = ref(getToken() || '')
  const loginDialogVisible = ref(false)

  // 初始化方法：检查本地是否有有效的登录状态
  const init = async () => {
    console.log('[UserStore] 初始化用户状态', {
      hasToken: !!token.value,
      hasUserInfo: !!userInfo.value,
      timestamp: new Date().toISOString()
    })
    
    // 如果本地有 token 和用户信息，则认为已登录
    // 如果只有 token 但没有用户信息，则清除 token（状态不一致）
    if (token.value && !userInfo.value) {
      console.warn('[UserStore] 检测到 token 存在但用户信息缺失，清除登录状态')
      logout()
    }
  }

  const login = async (loginForm) => {
    console.log('[UserStore] 开始登录流程', {
      username: loginForm.username,
      timestamp: new Date().toISOString()
    })
    
    try {
      const data = await loginApi(loginForm)
      
      // 登录返回的数据包含 token 和 userInfo
      token.value = data.token
      userInfo.value = data.userInfo
      
      // 保存到本地存储
      setToken(data.token)
      localStorage.setItem('userInfo', JSON.stringify(data.userInfo))
      
      console.log('[UserStore] 登录成功', {
        userId: data.userInfo.id,
        username: data.userInfo.username,
        role: data.userInfo.role,
        tokenPreview: data.token.substring(0, 20) + '...',
        timestamp: new Date().toISOString()
      })
      
      return data
    } catch (error) {
      console.error('[UserStore] 登录失败:', error.message)
      throw error
    }
  }

  const logout = () => {
    console.log('[UserStore] 执行登出操作', {
      timestamp: new Date().toISOString()
    })
    
    token.value = ''
    userInfo.value = null
    removeToken()
    localStorage.removeItem('userInfo')
  }

  const isAdmin = () => userInfo.value?.role === 'ADMIN'
  const isSupplier = () => userInfo.value?.role === 'SUPPLIER'

  const isLoggedIn = computed(() => !!token.value && !!userInfo.value)

  return {
    userInfo,
    token,
    loginDialogVisible,
    isLoggedIn,
    init,
    login,
    logout,
    isAdmin,
    isSupplier
  }
})
