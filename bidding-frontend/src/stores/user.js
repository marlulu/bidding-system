import { defineStore } from 'pinia'
import { ref } from 'vue'
import { login as loginApi, getUserInfo as getUserInfoApi } from '@/api/auth'
import { setToken, removeToken } from '@/utils/auth'

export const useUserStore = defineStore('user', () => {
  const userInfo = ref(null)
  const token = ref('')

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

  return {
    userInfo,
    token,
    login,
    getUserInfo,
    logout,
    isAdmin,
    isSupplier
  }
})
