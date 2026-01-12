import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { login as loginApi, getUserInfo as getUserInfoApi } from '@/api/auth'
import { setToken, removeToken, getToken } from '@/utils/auth'

export const useUserStore = defineStore('user', () => {
  const userInfo = ref(JSON.parse(localStorage.getItem('userInfo') || 'null'))
  const token = ref(getToken() || '')
  const loginDialogVisible = ref(false)

  // 初始化方法：如果本地有 token 但没用户信息，则去后端获取
  const init = async () => {
    console.log('[UserStore] 初始化用户状态', {
      hasToken: !!token.value,
      hasUserInfo: !!userInfo.value,
      timestamp: new Date().toISOString()
    })
    
    if (token.value && !userInfo.value) {
      try {
        console.log('[UserStore] 从后端获取用户信息...')
        await getUserInfo()
      } catch (error) {
        console.error('[UserStore] 初始化用户信息失败:', error)
        logout()
      }
    }
  }

  const login = async (loginForm) => {
    console.log('[UserStore] 开始登录流程', {
      username: loginForm.username,
      timestamp: new Date().toISOString()
    })
    
    try {
      const data = await loginApi(loginForm)
      token.value = data.token
      setToken(data.token)
      
      console.log('[UserStore] 登录成功，获取用户信息...', {
        tokenPreview: data.token.substring(0, 20) + '...',
        timestamp: new Date().toISOString()
      })
      
      // 登录成功后获取用户信息
      await getUserInfo()
      return data
    } catch (error) {
      console.error('[UserStore] 登录失败:', error.message)
      throw error
    }
  }

  const getUserInfo = async () => {
    console.log('[UserStore] 调用 getUserInfo API', {
      timestamp: new Date().toISOString()
    })
    
    try {
      const data = await getUserInfoApi()
      console.log('[UserStore] 成功获取用户信息', {
        userId: data.id,
        username: data.username,
        role: data.role,
        timestamp: new Date().toISOString()
      })
      
      userInfo.value = data
      localStorage.setItem('userInfo', JSON.stringify(data))
      return data
    } catch (error) {
      console.error('[UserStore] 获取用户信息失败:', error.message)
      logout()
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
    getUserInfo,
    logout,
    isAdmin,
    isSupplier
  }
})
