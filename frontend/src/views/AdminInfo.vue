<template>
    <div class="page-container">
        <el-card class="box-card">
            <template #header>
                <span class="card-title">个人信息</span>
            </template>
            <el-form label-position="left" :model="form" label-width="80px">
                <el-form-item label="头像" class="avatar-form-item">
                    <div class="avatar-upload-container">
                        <div class="avatar-preview-wrapper">
                            <el-avatar 
                                :size="100" 
                                :src="form.head ? $baseFileUrl + form.head : defaultAvatar"
                                class="avatar-preview"
                                fit="cover"
                            ></el-avatar>
                        </div>
                        <div class="avatar-controls">
                            <input 
                                type="file" 
                                ref="fileInput" 
                                accept="image/*" 
                                style="display: none"
                                @change="handleFileChange" 
                            />
                            <el-button 
                                type="primary" 
                                size="small" 
                                icon="el-icon-upload2" 
                                plain
                                @click="fileInput && fileInput.click()"
                            >
                                选择头像
                            </el-button>
                            <div class="upload-tips">
                                <p>支持JPG、PNG格式，最大10MB</p>
                            </div>
                        </div>
                    </div>
                </el-form-item>
                <el-form-item label="账号">
                    <el-input disabled prefix-icon="el-icon-user" v-model="form.username" autocomplete="off"></el-input>
                </el-form-item>
                <el-form-item label="密码">
                    <el-input type="password" prefix-icon="el-icon-lock" placeholder="请输入密码" v-model="form.password" autocomplete="off" show-password></el-input>
                </el-form-item>
                <el-form-item label="姓名">
                    <el-input prefix-icon="el-icon-s-custom" placeholder="请输入姓名" v-model="form.name" autocomplete="off"></el-input>
                </el-form-item>
                <el-form-item label="性别">
                    <el-radio-group v-model="form.gender">
                        <el-radio :label="1">
                            <i class="el-icon-male" style="color: #409EFF;"></i> 男
                        </el-radio>
                        <el-radio :label="2">
                            <i class="el-icon-female" style="color: #F56C6C;"></i> 女
                        </el-radio>
                    </el-radio-group>
                </el-form-item>
                <el-form-item>
                    <el-button type="primary" @click="handleModify" :loading="submitLoading">
                        <i class="el-icon-check"></i> 保存修改
                    </el-button>
                </el-form-item>
            </el-form>
        </el-card>
    </div>
</template>

<script setup>
import { ref, reactive, getCurrentInstance } from 'vue'
import { useRouter } from 'vue-router'
import { storeToRefs } from 'pinia'
import { useAuthStore } from '@/stores/auth'
import request from '@/utils/request'
import { modifyAdminAPI } from "@/api/admin";
import { getUserInfoByTokenAPI } from '@/api/system'
import defaultAvatar from '@/assets/head.jpg'
import { ElMessage } from 'element-plus'

const form = reactive({})
const submitLoading = ref(false)
const genderOptions = [
  { label: '男', value: 1 },
  { label: '女', value: 2 },
]
const fileInput = ref()

const authStore = useAuthStore()
const router = useRouter()

getUserInfoByTokenAPI().then((result) => {
  Object.assign(form, result.data)
}).catch((error) => {
  console.error('获取用户信息失败', error)
})

function handleFileChange(e) {
  const file = e.target.files && e.target.files[0]
  if (!file) return
  if (!beforeAvatarUpload(file)) return
  const formData = new FormData()
  formData.append('photo', file)
  ElMessage.info('头像上传中，请稍候...')
  request.post('/file/uploadPhoto', formData, 'form').then((resp) => {
    if (resp.data && resp.data.filename) {
      form.head = resp.data.filename
      ElMessage.success('头像上传成功')
    } else {
      ElMessage.error('头像上传失败，请重试')
    }
  }).catch((error) => {
    console.error('头像上传失败', error)
  })
}

function beforeAvatarUpload(file) {
  const isImage = file.type.startsWith('image/')
  const isLt10M = file.size / 1024 / 1024 < 10
  if (!isImage) {
    ElMessage.error('只能上传图片文件!')
    return false
  }
  if (!isLt10M) {
    ElMessage.error('图片大小不能超过 10MB!')
    return false
  }
  return true
}

async function handleModify() {
  submitLoading.value = true
  try {
    const res = await modifyAdminAPI(form)
    ElMessage[res.flag ? 'success' : 'error'](res.message)
    if (res.flag) {
      authStore.setUser(form)
      ElMessage.success('个人信息修改成功，页面将在1秒后刷新')
      setTimeout(() => {
        router.go(0)
      }, 1000)
    }
  } catch (error) {
    console.error('修改用户信息失败', error)
  } finally {
    submitLoading.value = false
  }
}
</script>

<style lang="less" scoped>
.page-container {
    width: 600px;
    margin: 30px auto;
}

.box-card {
    border-radius: 8px;
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
    
    .card-title {
        font-size: 18px;
        font-weight: 700;
        color: #303133;
    }
}

.avatar-form-item {
    margin-bottom: 30px;
}

.avatar-upload-container {
    display: flex;
    align-items: center;
    padding: 20px 0;
    
    .avatar-preview-wrapper {
        display: flex;
        justify-content: center;
        width: 120px;
        
        .avatar-preview {
            border: 3px solid #fff;
            box-shadow: 0 2px 10px rgba(0, 0, 0, 0.15);
            transition: all 0.3s;
            
            &:hover {
                transform: scale(1.05);
                box-shadow: 0 3px 12px rgba(0, 0, 0, 0.2);
            }
        }
    }
    
    .avatar-controls {
        flex: 1;
        padding-left: 20px;
        
        .el-button {
            position: relative;
            overflow: hidden;
            margin-bottom: 8px;
            
            &:hover {
                transform: translateY(-2px);
            }
        }
        
        .upload-tips {
            font-size: 12px;
            color: #909399;
            margin-top: 5px;
        }
    }
}

.el-form-item {
    margin-bottom: 25px;
}

.el-button {
    padding: 12px 25px;
    border-radius: 4px;
    font-weight: 500;
    transition: all 0.3s ease;
    
    &:hover {
        transform: translateY(-2px);
        box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
    }
    
    i {
        margin-right: 5px;
    }
}
</style>