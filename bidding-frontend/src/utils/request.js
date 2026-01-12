import axios from 'axios'
import { ElMessage } from 'element-plus'
import { getToken, removeToken } from './auth'
import { useUserStore } from '@/stores/user'

const request = axios.create({
  baseURL: '/api',
  timeout: 30000
})

// 请求拦截器
request.interceptors.request.use(
  config => {
    const token = getToken()
    if (token) {
      config.headers['Authorization'] = `Bearer ${token}`
    }
    return config
  },
  error => {
    return Promise.reject(error)
  }
)

// 响应拦截器
request.interceptors.response.use(
  response => {
    const res = response.data
    
    if (res.code !== 200) {
      // 401未授权，清除状态并触发登录弹窗
      if (res.code === 401) {
        const userStore = useUserStore()
        userStore.logout()
        userStore.loginDialogVisible = true
        ElMessage.warning('登录已过期，请重新登录')
      } else {
        ElMessage.error(res.message || '请求失败')
      }
      
      return Promise.reject(new Error(res.message || '请求失败'))
    }
    
    return res.data
  },
  error => {
    if (error.response && error.response.status === 401) {
      const userStore = useUserStore()
      userStore.logout()
      userStore.loginDialogVisible = true
      ElMessage.warning('未授权，请先登录')
    } else {
      ElMessage.error(error.message || '网络错误')
    }
    return Promise.reject(error)
  }
)

export default request
