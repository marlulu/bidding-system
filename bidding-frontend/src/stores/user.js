import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { login as loginApi, getUserInfo as getUserInfoApi } from '@/api/auth'
import { setToken, removeToken, getToken } from '@/utils/auth'

export const useUserStore = defineStore('user', () => {
  // 初始状态从 localStorage 恢复
  const userInfo = ref(JSON.parse(localStorage.getItem('userInfo') || 'null'))
  const token = ref(getToken() || '')
  const loginDialogVisible = ref(false)

  const login = async (loginForm) => {
    const data = await loginApi(loginForm)
    token.value = data.token
    setToken(data.token)
    
    // 登录成功后获取用户信息
    await getUserInfo()
    return data
  }

  const getUserInfo = async () => {
    try {
      const data = await getUserInfoApi()
      userInfo.value = data
      // 持久化用户信息
      localStorage.setItem('userInfo', JSON.stringify(data))
      return data
    } catch (error) {
      // 如果获取失败（如 token 过期），则清除状态
      logout()
      throw error
    }
  }

  const logout = () => {
    token.value = ''
    userInfo.value = null
    removeToken()
    localStorage.removeItem('userInfo')
  }

  const isAdmin = () => userInfo.value?.role === 'ADMIN'
  const isSupplier = () => userInfo.value?.role === 'SUPPLIER'

  const isLoggedIn = computed(() => !!token.value)

  return {
    userInfo,
    token,
    loginDialogVisible,
    isLoggedIn,
    login,
    getUserInfo,
    logout,
    isAdmin,
    isSupplier
  }
})
