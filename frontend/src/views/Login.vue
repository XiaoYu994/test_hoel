<template>
    <div class="login-container">
        <div class="login-box">
            <h2 class="title" @click="router.push('/front')">{{ sysTitle }}</h2>
            <el-form :model="loginForm" label-position="left">
                <el-form-item>
                    <el-input v-model="loginForm.username" placeholder="请输入账户">
                        <template #prefix>
                            <el-icon><User /></el-icon>
                        </template>
                    </el-input>
                </el-form-item>
                <el-form-item>
                    <el-input v-model="loginForm.password" type="password" placeholder="请输入密码" show-password>
                        <template #prefix>
                            <el-icon><Lock /></el-icon>
                        </template>
                    </el-input>
                </el-form-item>
                <el-form-item>
                    <el-radio-group v-model="loginForm.role">
                        <el-radio :label="1">管理员</el-radio>
                        <el-radio :label="2">用户</el-radio>
                    </el-radio-group>
                </el-form-item>
                <el-form-item>
                    <el-button type="primary" style="width: 100%;" @click="login">登录</el-button>
                    <div class="text-right">
                        没有账号?去<router-link to="/register">注册</router-link>
                    </div>
                </el-form-item>
            </el-form>
        </div>
    </div>
</template>

<script setup>
import { reactive, getCurrentInstance } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { User, Lock } from '@element-plus/icons-vue'
import { loginAPI, getUserInfoByTokenAPI } from '@/api/system'
import { setToken as setTokenUtil } from '@/utils/auth'
import { useAuthStore } from '@/stores/auth'

const loginForm = reactive({ role: 1, username: '', password: '' })
const router = useRouter()
const authStore = useAuthStore()
const { appContext } = getCurrentInstance()
const sysTitle = appContext.config.globalProperties.$sysTitle

async function login() {
  const res = await loginAPI(loginForm)
  ElMessage[res.flag ? 'success' : 'error'](res.message)
  if (res.flag) {
    setTokenUtil(res.data)
    authStore.setToken(res.data)
    const result = await getUserInfoByTokenAPI()
    authStore.setUser(result.data)
    authStore.setRole(loginForm.role)
    router.replace(loginForm.role == 1 ? '/admin' : '/front')
  }
}
</script>

<style lang="less" scoped>
/* 全局样式覆盖，确保图标垂直居中 */
:deep(.el-input__prefix) {
    display: flex !important;
    align-items: center !important;
    justify-content: center !important;
    width: 35px !important;
    left: 0 !important;
}

.login-container {
    width: 100vw;
    height: 100vh;
    background-image: url("../assets/2.png");
    background-size: 100% 100%;
    display: flex;
    
    .login-box {
        padding: 40px 50px;
        min-width: 400px;
        min-height: 320px;
        background-color: #ffffff;
        margin: auto;
        border-radius: 3px;
        box-shadow: 0 15px 30px rgba(0, 0, 0, 0.1);
        position: relative;
        transition: all 0.3s ease;

        &:hover {
            box-shadow: 0 20px 40px rgba(0, 0, 0, 0.15);
            transform: translateY(-5px);
        }

        .title {
            text-align: center;
            margin-bottom: 40px;
            font-size: 36px;
            //font-weight: bold;
            color: #333;
            cursor: pointer;
            background: linear-gradient(to right, #000000, #000000);
            background-clip: text;
            -webkit-background-clip: text;
            -webkit-text-fill-color: transparent;
            letter-spacing: 2px;
            
            &:hover {
                background: linear-gradient(to right, #000000, #000000);
                background-clip: text;
                -webkit-background-clip: text;
                -webkit-text-fill-color: transparent;
            }
        }

        :deep(.el-input) {
            .el-input__wrapper {
                height: 50px;
                border-radius: 8px;
                padding-left: 35px;
                font-size: 14px;
                border: 1px solid #e0e0e0;
                box-shadow: 0 2px 6px rgba(0, 0, 0, 0.03);
                transition: all 0.3s ease;
            }

            .is-focus .el-input__wrapper,
            .el-input__wrapper.is-focus {
                border-color: #3B82F6;
                box-shadow: 0 3px 8px rgba(59, 130, 246, 0.15);
            }

            .el-input__prefix {
                left: 0;
                height: 100%;
                width: 35px;
                display: flex;
                align-items: center;
                justify-content: center;
                transition: all 0.3s;
                color: #909399;

                i, .el-icon {
                    font-size: 16px;
                }
            }

            &:hover .el-input__prefix {
                color: #409EFF;
            }
        }

        :deep(.el-radio) {
            margin-right: 20px;
            
            .el-radio__input.is-checked .el-radio__inner {
                background-color: #3B82F6;
                border-color: #3B82F6;
            }
            
            .el-radio__input.is-checked + .el-radio__label {
                color: #3B82F6;
            }
        }

        :deep(.el-button--primary) {
            height: 50px;
            font-size: 16px;
            font-weight: 500;
            letter-spacing: 1px;
            background: linear-gradient(to right, #3b82f6, #3B82F6);
            border: none;
            border-radius: 8px;
            box-shadow: 0 4px 12px rgba(59, 130, 246, 0.2);
            transition: all 0.3s ease;
            margin-top: 10px;
            
            &:hover {
                transform: translateY(-2px);
                box-shadow: 0 6px 16px rgba(59, 130, 246, 0.3);
                background: linear-gradient(to right, #2563EB, #60A5FA);
            }
            
            &:active {
                transform: translateY(0);
            }
        }

        .text-right {
            text-align: right;
            margin-top: 12px;
            font-size: 14px;
            color: #606266;
            
            a {
                color: #409EFF;
                text-decoration: none;
                margin-left: 4px;
                transition: all 0.3s;
                
                &:hover {
                    color: #66b1ff;
                    text-decoration: underline;
                }
            }
        }

        :deep(.el-form-item) {
            transition: all 0.3s;
            margin-bottom: 20px;
        }
        
        :deep(.el-form-item:last-child) {
            margin-bottom: 0;
        }
    }
}

@media screen and (max-width: 768px) {
    .login-container {
        .login-box {
            padding: 30px 25px;
            min-width: 300px;
            min-height: 300px;
            
            .title {
                font-size: 22px;
                margin-bottom: 20px;
            }
        }
    }
}

@media screen and (max-width: 480px) {
    .login-container {
        .login-box {
            padding: 25px 20px;
            min-width: 280px;
            
            .title {
                font-size: 28px;
            }
            
            :deep(.el-button--primary) {
                width: 100% !important;
            }
        }
    }
}
</style>
