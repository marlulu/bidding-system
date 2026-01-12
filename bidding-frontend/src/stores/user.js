import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { login as loginApi, getUserInfo as getUserInfoApi } from '@/api/auth'
import { setToken, removeToken } from '@/utils/auth'

export const useUserStore = defineStore('user', () => {
  const userInfo = ref(null)
  const token = ref('')
  const loginDialogVisible = ref(false)

  const login = async (loginForm) => {
    const data = await loginApi(loginForm)
    token.value = data.token
    userInfo.value = data.userInfo
    setToken(data.token)
    return data
  }

  const getUserInfo = async () => {
    const data = await getUserInfoApi()
    userInfo.value = data
    return data
  }

  const logout = () => {
    token.value = ''
    userInfo.value = null
    removeToken()
  }

  const isAdmin = () => {
    return userInfo.value && userInfo.value.role === 'ADMIN'
  }

  const isSupplier = () => {
    return userInfo.value && userInfo.value.role === 'SUPPLIER'
  }

  const isLoggedIn = computed(() => !!token.value || !!localStorage.getItem('token'))

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
