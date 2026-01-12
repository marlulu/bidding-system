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
    if (token.value && !userInfo.value) {
      try {
        await getUserInfo()
      } catch (error) {
        console.error('初始化用户信息失败:', error)
        logout()
      }
    }
  }

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
      localStorage.setItem('userInfo', JSON.stringify(data))
      return data
    } catch (error) {
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
