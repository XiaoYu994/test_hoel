<template>
  <div :class="{ home: true, openMenus: !isCollapse, closeMenus: isCollapse }">
    <div class="menus">
      <!-- 标题与Logo区域 -->
      <div class="logo-container">
        <div class="logo-inner">
          <img src="@/assets/premium-hotel-logo.svg" class="logo-image"/>
          <span class="logo-text" v-if="!isCollapse">酒店管理系统</span>
        </div>
      </div>
      
      <!-- 导航菜单 -->
      <el-menu 
        :collapse="isCollapse" 
        default-active="home" 
        background-color="#1E293B" 
        text-color="#94A3B8" 
        active-text-color="#FFFFFF"
        :collapse-transition="false"
        router
      >
        <el-menu-item index="/admin" class="menu-item">
          <i class="menu-icon fas fa-tachometer-alt"></i>
          <span>控制面板</span>
        </el-menu-item>
        
        <el-menu-item index="/admin/admin" class="menu-item">
          <i class="menu-icon fas fa-user-shield"></i>
          <span>管理员管理</span>
        </el-menu-item>
        
        <el-menu-item index="/admin/member" class="menu-item">
          <i class="menu-icon fas fa-users"></i>
          <span>用户管理</span>
        </el-menu-item>
        
        <el-menu-item index="/admin/category" class="menu-item">
          <i class="menu-icon fas fa-bed"></i>
          <span>房型管理</span>
        </el-menu-item>
        
        <el-menu-item index="/admin/room" class="menu-item">
          <i class="menu-icon fas fa-door-open"></i>
          <span>房间管理</span>
        </el-menu-item>
        
        <el-menu-item index="/admin/appointment" class="menu-item">
          <i class="menu-icon fas fa-calendar-check"></i>
          <span>预约管理</span>
        </el-menu-item>
        
        <el-menu-item index="/admin/orders" class="menu-item">
          <i class="menu-icon fas fa-clipboard-list"></i>
          <span>入住管理</span>
        </el-menu-item>
        
        <el-menu-item index="/admin/message" class="menu-item">
          <i class="menu-icon fas fa-comments"></i>
          <span>留言管理</span>
        </el-menu-item>
        
        <el-menu-item index="/admin/notice" class="menu-item">
          <i class="menu-icon fas fa-bullhorn"></i>
          <span>公告管理</span>
        </el-menu-item>
      </el-menu>
      

    </div>
    
    <div class="content">
      <div class="navbar">
        <div class="navbar-btn" @click="isCollapse = !isCollapse">
          <i class="el-icon-s-fold" v-if="!isCollapse"></i>
          <i class="el-icon-s-unfold" v-else></i>
        </div>
        <div class="navbar-right">
          <el-dropdown trigger="click">
            <span class="el-dropdown-link">
              <div class="user-profile">
                <el-avatar 
                  :size="36" 
                  :src="user?.head ? baseFileUrl + user.head : defaultAvatar"
                  class="user-avatar"
                ></el-avatar>
                <span class="username">{{ user?.name }}</span>
                <i class="el-icon-caret-bottom"></i>
              </div>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item icon="el-icon-s-custom" @click="router.push('/admin/adminInfo')">个人信息</el-dropdown-item>
                <el-dropdown-item icon="el-icon-caret-left" @click="logout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </div>
      <!-- 内容部分 -->
      <div class="section">
        <router-view></router-view>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, getCurrentInstance } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { storeToRefs } from 'pinia'
import { removeToken } from '@/utils/auth'
import defaultAvatar from '@/assets/head.jpg'
import { ElMessageBox } from 'element-plus'

const isCollapse = ref(false)
const authStore = useAuthStore()
const { user } = storeToRefs(authStore)
const router = useRouter()
const { appContext } = getCurrentInstance()
const baseFileUrl = appContext.config.globalProperties.$baseFileUrl

function logout() {
  ElMessageBox.confirm('确认退出登录吗?', '提示', {
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

function loadFontAwesome() {
  const link = document.createElement('link')
  link.rel = 'stylesheet'
  link.href = 'https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.4/css/all.min.css'
  document.head.appendChild(link)
}

onMounted(loadFontAwesome)
</script>

<style lang="less" scoped>
.openMenus {
  .menus {
    height: 100vh;
    width: 240px;
    overflow-y: auto;
    position: fixed;
    left: 0;
    top: 0;
    background-color: #1E293B;
    transition: all 0.3s ease;
    display: flex;
    flex-direction: column;
    box-shadow: 0 0 15px rgba(0, 0, 0, 0.1);

    .el-menu {
      border-right: none;
      flex: 1;
    }
  }

  .content {
    margin-left: 240px;
    width: calc(100% - 240px);
    height: 100vh;
    transition: all 0.3s ease;
  }
}

.closeMenus {
  .menus {
    height: 100vh;
    width: 64px;
    overflow-y: auto;
    position: fixed;
    left: 0;
    top: 0;
    transition: all 0.3s ease;
    background-color: #1E293B;
    box-shadow: 0 0 15px rgba(0, 0, 0, 0.1);
    display: flex;
    flex-direction: column;

    .el-menu {
      border-right: none;
      flex: 1;
    }
  }

  .content {
    margin-left: 64px;
    width: calc(100% - 64px);
    height: 100vh;
    transition: all 0.3s ease;
  }
}

.home {
  // Logo样式
  .logo-container {
    padding: 0;
    text-align: center;
    position: relative;
    background: linear-gradient(135deg, #0A1A3A, #152A4F);
    height: 70px;
    display: flex;
    align-items: center;
    justify-content: center;
    border-bottom: 1px solid rgba(255, 255, 255, 0.05);
    
    .logo-inner {
      display: flex;
      align-items: center;
      justify-content: flex-start;
      padding: 0 16px;
      width: 100%;
      height: 100%;
      
      .logo-image {
        height: 42px;
        width: auto;
        transition: all 0.3s ease;
        filter: drop-shadow(0 2px 4px rgba(0, 0, 0, 0.2));
      }
      
      .logo-text {
        color: #fff;
        font-size: 18px;
        font-weight: 600;
        margin-left: 12px;
        letter-spacing: 1px;
        white-space: nowrap;
        background: linear-gradient(90deg, #FFFFFF, #8BB8FF);
        background-clip: text;
        -webkit-background-clip: text;
        -webkit-text-fill-color: transparent;
        text-shadow: 0 1px 2px rgba(0, 0, 0, 0.1);
      }
    }
  }
  
  // 菜单项样式
  .menu-item {
    height: 56px;
    line-height: 56px;
    margin: 4px 0;
    border-radius: 0 !important;
    
    &:hover {
      background-color: rgba(255, 255, 255, 0.05) !important;
    }
    
    &.is-active {
      background: linear-gradient(90deg, rgba(30, 58, 138, 0.8), rgba(30, 58, 138, 0));
      box-shadow: inset 4px 0 0 #3988fd;
    }
    
    .menu-icon {
      margin-right: 10px;
      width: 24px;
      text-align: center;
      font-size: 16px;
      color: inherit;
    }
  }
  
  // 底部版权信息
  .sidebar-footer {
    padding: 16px;
    color: #64748B;
    font-size: 12px;
    text-align: center;
    background-color: rgba(0, 0, 0, 0.1);
  }

  .navbar {
    height: 64px;
    display: flex;
    align-items: center;
    padding: 0 20px;
    justify-content: space-between;
    border-bottom: 1px solid #eee;
    background-color: #fff;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);

    .navbar-btn {
      height: 40px;
      line-height: 40px;
      text-align: center;
      cursor: pointer;
      width: 40px;
      border-radius: 50%;
      transition: all 0.3s;
      
      &:hover {
        background-color: rgba(0, 0, 0, 0.05);
      }
      
      i {
        font-size: 20px;
        color: #666;
      }
    }

    .navbar-right {
      min-width: 60px;
      height: 40px;
      display: flex;
      align-items: center;
      cursor: pointer;
      
      .user-profile {
        display: flex;
        align-items: center;
        padding: 2px 12px;
        background-color: rgba(0,0,0,0.03);
        border-radius: 20px;
        transition: all 0.3s;
        
        &:hover {
          background-color: rgba(0,0,0,0.08);
        }
        
        .user-avatar {
          border: 2px solid #fff;
          box-shadow: 0 2px 6px rgba(0, 0, 0, 0.1);
        }
        
        .username {
          margin: 0 8px;
          font-size: 14px;
          font-weight: 500;
          color: #333;
        }
        
        i {
          color: #666;
          font-size: 12px;
        }
      }
    }
  }

  .content {
    .section {
      padding: 16px;
      background-color: #f5f7fa;
      min-height: calc(100vh - 64px);
    }
  }
}
</style>
