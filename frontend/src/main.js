import './assets/main.css'

import { createApp } from 'vue'
import { createPinia } from 'pinia'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import 'element-plus/theme-chalk/dark/css-vars.css'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'
import zhCn from 'element-plus/dist/locale/zh-cn.mjs'

import App from './App.vue'
import router from './router'
import { useAuthStore } from '@/stores/auth'

// Import global styles
import './styles/index.scss'

// Day.js global configuration
import dayjs from 'dayjs'
import relativeTime from 'dayjs/plugin/relativeTime'
import 'dayjs/locale/zh-cn'
dayjs.extend(relativeTime)
dayjs.locale('zh-cn')

const app = createApp(App)

// Register Element Plus icons
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component)
}

const pinia = createPinia()
app.use(pinia)
app.use(router)
app.use(ElementPlus, {
  locale: zhCn,
})

// Global app properties
// Title shown across pages
app.config.globalProperties.$sysTitle = '酒店管理系统'
// Base file URL for images; can be overridden at runtime via window.$baseFileUrl
app.config.globalProperties.$baseFileUrl = window?.$baseFileUrl || '/api/files/'

// Resolve image URL supporting absolute links and relative filenames
app.config.globalProperties.$resolveImageUrl = (input) => {
  if (!input) return ''
  const value = String(input)
  if (/^(?:https?:)?\/\//i.test(value) || value.startsWith('data:') || value.startsWith('blob:')) {
    return value
  }
  const base = app.config.globalProperties.$baseFileUrl || ''
  if (value.startsWith(base)) return value
  const normalizedBase = base.replace(/\/+$/g, '')
  const normalizedPath = value.replace(/^\/+/, '')
  return `${normalizedBase}/${normalizedPath}`
}

// 初始化认证状态
const authStore = useAuthStore()
authStore.initAuth()

app.mount('#app')
