import axios from 'axios'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useAuthStore } from '@/stores/auth'
import router from '@/router'

// Create axios instance
const service = axios.create({
  baseURL: '/api',
  timeout: 15000
})

// Prevent repeated redirects on multiple concurrent 401s
let isRedirectingFor401 = false

// Serialize params with array support: key=a&key=b instead of key[]=a&key[]=b
function serializeParams(params) {
  if (!params || typeof params !== 'object') return ''
  const parts = []
  Object.keys(params).forEach((key) => {
    const value = params[key]
    if (value === undefined || value === null) return
    if (Array.isArray(value)) {
      value.forEach((item) => {
        parts.push(`${encodeURIComponent(key)}=${encodeURIComponent(item)}`)
      })
    } else {
      parts.push(`${encodeURIComponent(key)}=${encodeURIComponent(value)}`)
    }
  })
  return parts.join('&')
}

// Request interceptor
service.interceptors.request.use(
  config => {
    const authStore = useAuthStore()
    
    // Add token to headers if it exists
    if (authStore.token) {
      // Backend expects the raw JWT in Authorization header (no Bearer prefix)
      config.headers.Authorization = authStore.token
    }
    
    return config
  },
  error => {
    console.error('Request error:', error)
    return Promise.reject(error)
  }
)

// Response interceptor
service.interceptors.response.use(
  response => {
    const res = response.data
    
    // Handle successful response
    if (res.code === 200 || res.code === 0) {
      return res
    }
    
    // Token expired or invalid (backend returns business code 401 with HTTP 200)
    if (res.code === 401) {
      if (!isRedirectingFor401) {
        isRedirectingFor401 = true
        const authStore = useAuthStore()
        authStore.logout()
        ElMessage.error(res.message || '登录已过期，请重新登录')
        // Preserve intended route for post-login redirect
        const redirect = router.currentRoute?.value?.fullPath || '/'
        router.replace({ path: '/login', query: { redirect } }).finally(() => {
          // small delay to avoid thrashing
          setTimeout(() => { isRedirectingFor401 = false }, 800)
        })
      }
      return Promise.reject(new Error(res.message || 'Unauthorized'))
    }
    
    // Handle business logic errors
    ElMessage.error(res.message || 'Error occurred')
    return Promise.reject(new Error(res.message || 'Error'))
  },
  error => {
    console.error('Response error:', error)
    
    let message = '网络错误，请稍后重试'
    
    if (error.response) {
      const { status, data } = error.response
      
      switch (status) {
        case 400:
          message = data?.message || '请求参数错误'
          break
        case 401:
          message = '登录已过期，请重新登录'
          // Clear auth and redirect to login
          if (!isRedirectingFor401) {
            isRedirectingFor401 = true
            const authStore = useAuthStore()
            authStore.logout()
            const redirect = router.currentRoute?.value?.fullPath || '/'
            router.replace({ path: '/login', query: { redirect } }).finally(() => {
              setTimeout(() => { isRedirectingFor401 = false }, 800)
            })
          }
          break
        case 403:
          message = '没有权限访问该资源'
          break
        case 404:
          message = '请求的资源不存在'
          break
        case 500:
          message = '服务器内部错误'
          break
        default:
          message = data?.message || `错误代码: ${status}`
      }
    } else if (error.code === 'ECONNABORTED') {
      message = '请求超时，请稍后重试'
    }
    
    ElMessage.error(message)
    return Promise.reject(error)
  }
)

// Export request methods
export default service

// Common request methods
export const get = (url, params) => {
  return service({
    method: 'get',
    url,
    params
  })
}

export const post = (url, data) => {
  const isForm = typeof FormData !== 'undefined' && data instanceof FormData
  return service({
    method: 'post',
    url,
    data,
    headers: isForm ? { 'Content-Type': 'multipart/form-data' } : undefined
  })
}

export const put = (url, data) => {
  return service({
    method: 'put',
    url,
    data
  })
}

export const del = (url, params) => {
  let requestUrl = url
  const query = serializeParams(params)
  if (query) {
    requestUrl = `${url}?${query}`
  }
  return service({
    method: 'delete',
    url: requestUrl
  })
}

// File upload helper
export const upload = (url, formData, onUploadProgress) => {
  return service({
    method: 'post',
    url,
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    },
    onUploadProgress
  })
} 