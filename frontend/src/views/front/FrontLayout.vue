<template>
  <div>
    <!-- 头部导航栏 -->
    <div class="navbar-wrapper">
      <div class="navbar-container">
        <!-- 左侧Logo -->
        <div class="navbar-left">
          <router-link to="/front" class="logo-link">
            <div class="logo-wrapper">
              <div class="logo-image-container">
                <img src="@/assets/luxury-hotel-logo.svg" class="logo-image" />
              </div>
              <div class="brand-text">
                <span class="logo-text">{{ $sysTitle }}</span>
                <span class="logo-tagline">尊享舒适与优雅</span>
              </div>
            </div>
          </router-link>
        </div>

        <!-- 中间导航项 -->
        <div class="navbar-center">
          <nav class="nav-links">
            <router-link to="/front" class="nav-item" :class="{ active: $route.path === '/front' }">
              <i class="nav-icon fas fa-home"></i>
              <span>首页</span>
            </router-link>
            <router-link to="/front/notice" class="nav-item" :class="{ active: $route.path === '/front/notice' }">
              <i class="nav-icon fas fa-bullhorn"></i>
              <span>系统公告</span>
            </router-link>
            <router-link to="/front/message" class="nav-item" :class="{ active: $route.path === '/front/message' }">
              <i class="nav-icon fas fa-comments"></i>
              <span>用户留言</span>
            </router-link>
            <a @click="handleAppointmentClick" class="nav-item highlight-item" href="javascript:void(0)">
              <i class="nav-icon fas fa-concierge-bell"></i>
              <span>预约入住</span>
            </a>
          </nav>
        </div>

        <!-- 右侧用户区域 -->
        <div class="navbar-right">
          <!-- 未登录显示 登录/注册 -->
          <div v-if="!token" class="auth-links">
            <router-link to="/login" class="nav-item">
              <i class="nav-icon fas fa-sign-in-alt"></i>
              <span>登录</span>
            </router-link>
            <router-link to="/register" class="nav-item">
              <i class="nav-icon fas fa-user-plus"></i>
              <span>注册</span>
            </router-link>
          </div>

          <!-- 登录后用户菜单 -->
          <div v-if="token" class="user-menu">
            <el-dropdown trigger="hover" placement="bottom-end">
              <div class="user-info">
                <el-avatar :src="avatarUrl || undefined" class="user-avatar" :size="36">
                  <i class="fas fa-user"></i>
                </el-avatar>
                <span class="username">{{ user?.name || '用户' }}</span>
                <i class="fas fa-chevron-down user-arrow"></i>
              </div>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item @click="router.push('/front/userInfo')">
                    <i class="fas fa-user menu-icon"></i>
                    <span>个人信息</span>
                  </el-dropdown-item>
                  <el-dropdown-item @click="router.push('/front/appointment')">
                    <i class="fas fa-calendar-check menu-icon"></i>
                    <span>我的预约</span>
                  </el-dropdown-item>
                  <el-dropdown-item @click="router.push('/front/orders')">
                    <i class="fas fa-receipt menu-icon"></i>
                    <span>我的入住</span>
                  </el-dropdown-item>
                  <el-dropdown-item divided @click="logout">
                    <i class="fas fa-sign-out-alt menu-icon"></i>
                    <span>退出登录</span>
                  </el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </div>
        </div>
      </div>
    </div>
    
    <!-- 中间区域 -->
    <div class="content" :style="{ paddingTop: headerOffset + 'px', minHeight: `calc(100vh - ${headerOffset}px)` }">
      <router-view />
    </div>
    
    <!-- 底部版权信息 -->
<!--    <div class="footer">-->
<!--      <div class="footer-content">-->
<!--        <p>Copyright © {{ year }} {{ $sysTitle }}</p>-->
<!--        <p>为您提供最优质的住宿体验</p>-->
<!--      </div>-->
<!--    </div>-->
  </div>
</template>
    
<script setup>
import { ref, onMounted, onBeforeUnmount, nextTick, watch, getCurrentInstance, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { storeToRefs } from 'pinia'
import { removeToken } from '@/utils/auth'
import { ElMessageBox } from 'element-plus'

const activeIndex = ref('1')
const year = ref('')
const headerOffset = ref(80)

const authStore = useAuthStore()
const { user, role, token } = storeToRefs(authStore)
const router = useRouter()
const { appContext } = getCurrentInstance()
const baseFileUrl = appContext.config.globalProperties.$baseFileUrl

// 头像地址拼接与兜底
const avatarUrl = computed(() => {
  const head = user.value?.head
  if (!head) return ''
  // 已是绝对或 data url
  if (/^(https?:)?\/\//.test(head) || head.startsWith('data:')) return head
  const base = typeof baseFileUrl === 'string' ? baseFileUrl : ''
  if (!base) return head
  const normalizedBase = base.endsWith('/') ? base.slice(0, -1) : base
  const normalizedHead = head.startsWith('/') ? head.slice(1) : head
  return `${normalizedBase}/${normalizedHead}`
})

function loadFontAwesome() {
  if (!document.querySelector('link[href*="font-awesome"]')) {
    const link = document.createElement('link')
    link.setAttribute('rel', 'stylesheet')
    link.setAttribute('href', 'https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.4/css/all.min.css')
    document.head.appendChild(link)
  }
}

function logout() {
  ElMessageBox.confirm('确定退出吗?', '温馨提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    removeToken()
    authStore.setUser({})
    authStore.setToken('')
    router.replace('/front')
  }).catch(() => {})
}

function handleAppointmentClick() {
  router.push('/front/room')
}

onMounted(() => {
  year.value = new Date().getFullYear()
  loadFontAwesome()
  nextTick(() => updateHeaderOffset())
  window.addEventListener('resize', updateHeaderOffset)
})

onBeforeUnmount(() => {
  window.removeEventListener('resize', updateHeaderOffset)
})

function updateHeaderOffset() {
  const nav = document.querySelector('.navbar-wrapper')
  const height = nav ? Math.ceil(nav.getBoundingClientRect().height) : 70
  headerOffset.value = height + 10
}

watch([token, user], () => {
  nextTick(() => updateHeaderOffset())
})
</script>
    
<style lang="less" scoped>
.navbar-wrapper {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  z-index: 999;
  box-shadow: 0 3px 16px rgba(0, 0, 0, 0.15);
}

.navbar-container {
  height: 70px;
  background-image: linear-gradient(to right, #1E3A8A, #274597);
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
}

/* 左侧Logo区域 */
.navbar-left {
  display: flex;
  align-items: center;
}

.logo-link {
  text-decoration: none;
  color: inherit;
  display: block;
}

.logo-wrapper {
  display: flex;
  align-items: center;
  height: 100%;
  padding: 0 5px;
}

.logo-image-container {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 36px;
  width: 36px;
  margin-right: 12px;
}

.logo-image {
  max-height: 100%;
  max-width: 100%;
  object-fit: contain;
}

.brand-text {
  display: flex;
  flex-direction: column;
  justify-content: center;
  height: 40px;
}

.logo-text {
  font-size: 16px;
  font-weight: 600;
  color: white;
  line-height: 1.2;
  background-image: linear-gradient(to right, #fff, #FFD700);
  background-clip: text;
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  text-shadow: 0 0 2px rgba(0, 0, 0, 0.1);
  display: block;
}

.logo-tagline {
  font-size: 11px;
  color: rgba(255, 255, 255, 0.8);
  line-height: 1;
  margin-top: 3px;
  display: block;
}

/* 中间导航区域 */
.navbar-center {
  display: flex;
  align-items: center;
  flex: 1;
  justify-content: center;
}

.nav-links {
  display: flex;
  align-items: center;
  gap: 8px;
}

.nav-item {
  display: flex;
  align-items: center;
  height: 50px;
  padding: 0 20px;
  color: #ffffff;
  text-decoration: none;
  border-radius: 6px;
  transition: all 0.3s ease;
  font-size: 14px;
  font-weight: 500;
  
  &:hover {
    background-color: rgba(255, 255, 255, 0.1);
    transform: translateY(-1px);
  }
  
  &.active {
    background-color: rgba(255, 215, 0, 0.2);
    color: #FFD700;
  }
}

.nav-icon {
  margin-right: 6px;
  font-size: 14px;
}

.highlight-item {
  /* default state looks like other nav items; only highlight on hover */
  background: transparent;
  border: 1px solid transparent;
  
  &:hover {
    background: rgba(255, 215, 0, 0.2);
    border-color: #FFD700;
    color: #FFD700;
  }
}

/* 右侧用户区域 */
.navbar-right {
  display: flex;
  align-items: center;
}

.auth-links {
  display: flex;
  align-items: center;
  gap: 8px;
}

.user-menu {
  display: flex;
  align-items: center;
  height: 70px;
}

.user-info {
  display: flex;
  align-items: center;
  height: 50px;
  padding: 0 12px;
  transition: all 0.3s ease;
  border-radius: 6px;
  cursor: pointer;
  
  &:hover {
    background-color: rgba(255, 255, 255, 0.1);
  }
}

.user-avatar {
  margin-right: 8px;
  border: 2px solid rgba(255, 255, 255, 0.7);
  transition: all 0.3s ease;
  
  :deep(.el-avatar) {
    background-color: rgba(255, 255, 255, 0.2);
  }
}

.user-info:hover .user-avatar {
  transform: scale(1.05);
  border-color: #FFD700;
}

.username {
  margin-right: 8px;
  max-width: 120px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  color: #ffffff;
  font-size: 14px;
  font-weight: 500;
}

.user-arrow {
  font-size: 12px;
  color: #ffffff;
  transition: all 0.3s ease;
}

.user-info:hover .user-arrow {
  transform: rotate(180deg);
}

.menu-icon {
  margin-right: 8px;
  width: 16px;
}

/* 内容区域 */
.content {
  padding-bottom: 0;
  background-color: #f5f7fa;
  overflow-x: hidden;
}

/* 底部区域 */
.footer {
  background-color: #1E293B;
  color: white;
  text-align: center;
  padding: 25px 0;
  margin-top: 0;
}

.footer-content {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
}

.footer p {
  margin: 5px 0;
  font-size: 14px;
  color: rgba(255, 255, 255, 0.7);
}

/* 响应式设计 */
@media screen and (max-width: 768px) {
  .navbar-container {
    padding: 0 10px;
    flex-wrap: wrap;
    height: auto;
    min-height: 70px;
  }
  
  .navbar-left {
    flex: 0 0 auto;
  }
  
  .navbar-center {
    order: 3;
    width: 100%;
    justify-content: flex-start;
    margin-top: 8px;
  }
  
  .nav-links {
    overflow-x: auto;
    overflow-y: hidden;
    gap: 4px;
    padding-bottom: 8px;
    
    /* 隐藏水平滚动条 */
    &::-webkit-scrollbar {
      height: 0;
      display: none;
    }
    scrollbar-width: none;
  }
  
  .nav-item {
    padding: 0 12px;
    height: 40px;
    font-size: 13px;
    flex-shrink: 0;
  }
  
  .navbar-right {
    order: 2;
    margin-left: auto;
  }
  
  .logo-text {
    font-size: 15px;
  }
  
  .logo-tagline {
    font-size: 10px;
  }
  
  .user-info {
    padding: 0 8px;
    height: 45px;
  }
  
  .username {
    display: none;
  }

  .user-avatar {
    margin-right: 0;
  }

  .user-arrow {
    margin-left: 4px;
  }
  
  .auth-links .nav-item {
    padding: 0 8px;
    font-size: 12px;
    
    span {
      display: none;
    }
  }
}
</style>
