<template>
    <div class="container">
        <div class="register-box">
            <h2 class="title" @click="$router.push('/front')">{{ $sysTitle }}-注册</h2>
            
            <!-- 错误提示 -->
            <el-alert
                v-if="showError"
                :title="errorMessage"
                type="error"
                show-icon
                :closable="true"
                @close="showError = false"
                class="error-alert"
            ></el-alert>
            
            <el-form :model="form" label-position="left" label-width="70px">
                <el-form-item label="账号">
                    <el-input placeholder="请输入账号" v-model="form.username" autocomplete="off" :prefix-icon="User"></el-input>
                </el-form-item>
                <el-form-item label="密码">
                    <el-input type="password" placeholder="请输入密码" v-model="form.password" autocomplete="off" :prefix-icon="Lock" show-password></el-input>
                </el-form-item>
                <el-form-item label="姓名">
                    <el-input placeholder="请输入姓名" v-model="form.name" autocomplete="off" :prefix-icon="Avatar"></el-input>
                </el-form-item>
                <el-form-item label="性别">
                    <div class="select-with-icon">
                        <el-select v-model="form.gender" placeholder="请选择" style="width: 100%;">
                            <el-option
                                v-for="item in genderOptions"
                                :key="item.value"
                                :label="item.label"
                                :value="item.value">
                            </el-option>
                        </el-select>
                    </div>
                </el-form-item>
                <el-form-item label="手机">
                    <el-input placeholder="请输入手机" v-model="form.phone" autocomplete="off" :prefix-icon="Phone"></el-input>
                </el-form-item>
                <el-form-item label="身份证">
                    <el-input placeholder="请输入身份证" v-model="form.idcard" autocomplete="off" :prefix-icon="Document"></el-input>
                </el-form-item>
                <el-form-item label="头像">
                    <div class="avatar-upload-container">
                        <el-upload 
                            name="photo" 
                            class="avatar-uploader" 
                            :action="uploadUrl" 
                            :show-file-list="false" 
                            :on-success="uploadSuccess" 
                            :on-error="uploadError"
                            :before-upload="beforeUpload"
                            :file-list="fileList"
                            list-type="picture-card"
                            :limit="1"
                            accept="image/*">
                            <div class="upload-btn-wrapper">
                                <el-button size="small" type="primary" class="upload-btn" :loading="uploadLoading">
                                    {{ uploadLoading ? '上传中...' : '选择头像' }}
                                </el-button>
                            </div>
                        </el-upload>
                    </div>
                </el-form-item>
                <el-form-item class="action-btn">
                    <el-button type="primary" style="width: 100%;" @click="submitForm" :loading="submitLoading">注册</el-button>
                    <div class="text-right">
                        已有账号?去<router-link to="/login">登录</router-link>
                    </div>
                </el-form-item>
            </el-form>
        </div>
    </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'
import { ElMessage } from 'element-plus'
import { registerAPI } from '@/api/system'
import { User, Lock, Avatar, Coordinate, Phone, Document } from '@element-plus/icons-vue'

defineOptions({ name: 'Register' })

const router = useRouter()

const form = ref({
  username: '',
  password: '',
  name: '',
  gender: '',
  phone: '',
  idcard: '',
  head: ''
})

const genderOptions = ref([
  { label: '男', value: 1 },
  { label: '女', value: 2 }
])

const fileList = ref([])
const uploadLoading = ref(false)
const submitLoading = ref(false)
const errorMessage = ref('')
const showError = ref('')
const uploadUrl = ref('/api/upload')

function getFieldLabel(field) {
  const fieldLabels = {
    username: '账号',
    password: '密码',
    name: '姓名',
    gender: '性别',
    phone: '手机',
    idcard: '身份证',
  }
  return fieldLabels[field] || field
}

async function submitForm() {
  
  errorMessage.value = ''

  const requiredFields = ['username', 'password', 'name', 'gender', 'phone', 'idcard']
  for (const key of requiredFields) {
    if (!form.value[key]) {
      ElMessage.error(`请填写${getFieldLabel(key)}`)
      return
    }
  }

  try {
    submitLoading.value = true
    const res = await registerAPI(form.value)
    ElMessage[res.flag ? 'success' : 'error'](res.message)
    if (res.flag) {
      setTimeout(() => router.push('/login'), 1000)
    }
  } catch (error) {
    errorMessage.value = '注册请求发送失败'
    showError.value = true
  } finally {
    submitLoading.value = false
  }
}

function useLocalPreview(file) {
  const reader = new FileReader()
  reader.readAsDataURL(file)
  reader.onload = () => {
    const localUrl = reader.result
    fileList.value = [{ name: file.name, url: localUrl }]
    ElMessage.info('已切换到本地预览模式，提交表单时可能无法保存头像')
  }
}

function uploadHeadFile(param) {
  uploadLoading.value = true
  const formData = new FormData()
  formData.append('photo', param.file)
  
  axios.post('/api/upload', formData, {
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  }).then(res => {
    uploadLoading.value = false
    if (res.data.flag) {
      form.value.head = res.data.data
      fileList.value = [{ name: param.file.name, url: res.data.data }]
      ElMessage.success('头像上传成功')
    } else {
      ElMessage.error(res.data.message || '头像上传失败')
    }
  }).catch(error => {
    uploadLoading.value = false
    console.error('Upload error:', error)
    useLocalPreview(param.file)
  })
}

function uploadSuccess(response, file) {
  uploadLoading.value = false
  if (response.flag) {
    form.value.head = response.data
    fileList.value = [{ name: file.name, url: response.data }]
    ElMessage.success('头像上传成功')
  } else {
    ElMessage.error(response.message || '头像上传失败')
  }
}

function uploadError(error, file) {
  uploadLoading.value = false
  console.error('Upload error:', error)
  useLocalPreview(file)
}

function beforeUpload(file) {
  const isImage = file.type.startsWith('image/')
  const isLt10M = file.size / 1024 / 1024 < 10

  if (!isImage) {
    ElMessage.error('只能上传图片文件!')
    return false
  }
  if (!isLt10M) {
    ElMessage.error('上传头像图片大小不能超过 10MB!')
    return false
  }
  
  uploadLoading.value = true
  return true
}
</script>

<style lang="less" scoped>
.container {
    width: 100vw;
    height: 100vh;
    background-image: url("../../assets/3.png");
    background-size: 100% 100%;
    display: flex;
    
    .register-box {
        padding: 30px 40px;
        min-width: 450px;
        min-height: 580px;
        background-color: #ffffff;
        margin: auto;
        border-radius: 8px;
        box-shadow: 0 10px 25px rgba(0, 0, 0, 0.1);
        position: relative;
        transition: all 0.3s ease;
        overflow-y: auto;
        max-height: 90vh;

        &:hover {
            box-shadow: 0 15px 35px rgba(0, 0, 0, 0.15);
            transform: translateY(-3px);
        }

        .title {
            text-align: center;
            margin-bottom: 25px;
            font-size: 28px;
            font-weight: bold;
            color: #333;
            cursor: pointer;
            letter-spacing: 1px;
            
            &:hover {
                color: #409EFF;
            }
        }

        .error-alert {
            margin-bottom: 15px;
        }

        :deep(.el-alert--error) {
            background-color: #fef0f0;
            color: #f56c6c;
        }

        :deep(.el-alert__title) {
            font-size: 14px;
            line-height: 1.5;
        }

        :deep(.el-form-item__label) {
            padding-right: 12px;
            font-weight: 500;
            color: #606266;
            font-size: 14px;
        }

        :deep(.el-form-item.is-required .el-form-item__label:before) {
            color: #f56c6c;
        }

        :deep(.el-input__inner) {
            height: 40px;
            border-radius: 4px;
            padding-left: 35px;
            font-size: 14px;
            border: 1px solid #dcdfe6;
            transition: all 0.3s ease;
            
            &:focus {
                border-color: #409EFF;
                box-shadow: 0 0 0 2px rgba(64, 158, 255, 0.2);
            }

            &:hover {
                border-color: #c0c4cc;
            }
        }

        :deep(.el-input__prefix) {
            left: 10px;
            transition: all 0.3s;
            color: #909399;
        }

        :deep(.el-input:hover .el-input__prefix) {
            color: #606266;
        }

        :deep(.el-select .el-input) {
            width: 100%;
        }

        :deep(.el-select-dropdown__item.selected) {
            color: #409EFF;
            font-weight: bold;
        }

        .select-with-icon {
            position: relative;
            display: flex;
            align-items: center;
            width: 100%;

            .select-prefix-icon {
                position: absolute;
                left: 10px;
                z-index: 2;
                color: #909399;
                transition: all 0.3s;
                font-size: 16px;
                pointer-events: none;
            }

            &:hover .select-prefix-icon {
                color: #409EFF;
            }
            
            :deep(.el-select) {
                width: 100%;
            }
            
            // 给选择器的输入包装留出左侧空间，避免出现"小框"重叠
            :deep(.el-select .el-input__wrapper) {
                padding-left: 35px;
            }
        }

        :deep(.el-button--primary) {
            height: 40px;
            font-size: 16px;
            font-weight: 500;
            background: #409EFF;
            border: none;
            border-radius: 4px;
            box-shadow: 0 2px 6px rgba(64, 158, 255, 0.2);
            transition: all 0.3s ease;
            margin-top: 5px;
            
            &:hover {
                background: #66b1ff;
                box-shadow: 0 4px 12px rgba(64, 158, 255, 0.3);
                transform: translateY(-2px);
            }
            
            &:active {
                transform: translateY(0);
            }
        }

        .avatar-upload-container {
            width: 100%;
            margin-bottom: 5px;
        }

        .upload-btn-wrapper {
            display: flex;
            flex-direction: column;
            align-items: flex-start;
        }

        .upload-btn {
            font-size: 12px !important;
            margin-bottom: 8px;
        }

        :deep(.el-upload) {
            width: 100%;
            
            .el-upload-dragger {
                width: 100%;
                height: auto;
                min-height: 60px;
                border: 1px dashed #d9d9d9;
                border-radius: 6px;
                cursor: pointer;
                position: relative;
                overflow: hidden;
                transition: all 0.3s;
                
                &:hover {
                    border-color: #409EFF;
                }
            }
        }

        :deep(.el-upload-list) {
            margin-top: 10px;
            
            .el-upload-list__item {
                transition: all 0.5s cubic-bezier(0.55, 0, 0.1, 1);
                border: 1px solid #c0ccda;
                border-radius: 6px;
                background-color: #fff;
                margin-top: 5px;
                
                &:first-child {
                    margin-top: 10px;
                }
                
                &:hover {
                    background-color: #f5f7fa;
                }
            }
            
            .el-upload-list__item-name {
                color: #606266;
                display: block;
                margin-right: 40px;
                overflow: hidden;
                padding-left: 4px;
                text-overflow: ellipsis;
                transition: color 0.3s;
            }
            
            .el-upload-list__item-thumbnail {
                width: 40px;
                height: 40px;
                object-fit: cover;
                border-radius: 4px;
            }
            
            .el-upload-list__item-status-label {
                position: absolute;
                right: -15px;
                top: -6px;
                width: 40px;
                height: 24px;
                background: #13ce66;
                text-align: center;
                transform: rotate(45deg);
                box-shadow: 0 0 1pc 1px rgba(0, 0, 0, 0.2);
            }
        }

        .action-btn {
            margin-top: 20px;
            
            .text-right {
                text-align: center;
                margin-top: 15px;
                font-size: 14px;
                color: #666;
                
                a {
                    color: #409EFF;
                    text-decoration: none;
                    
                    &:hover {
                        text-decoration: underline;
                    }
                }
            }
        }

        :deep(.el-form-item) {
            margin-bottom: 20px;
            
            &.action-btn {
                margin-bottom: 0;
            }
            
            .el-form-item__label {
                line-height: 40px;
                font-weight: 500;
                color: #606266;
            }
            
            .el-form-item__content {
                line-height: 40px;
            }
        }

        @media (max-width: 768px) {
            min-width: 320px;
            padding: 20px 25px;
            margin: 10px;
            
            :deep(.el-form-item__label) {
                font-size: 13px;
            }
            
            :deep(.el-button--primary) {
                font-size: 14px;
            }
        }
    }
}
</style>