<template>
  <div class="member-container">
    <!-- 标题区域 -->
    <div class="section-header">
      <h1 class="section-title">用户管理</h1>
      <div class="section-subtitle">管理系统会员及个人信息</div>
    </div>
    
    <!-- 搜索和操作区域 -->
    <div class="member-card">
      <el-form :inline="true" class="search-form" :model="searchForm">
        <el-form-item>
          <el-input 
            placeholder="请输入账号" 
            v-model="searchForm.username"
            prefix-icon="el-icon-user"
            clearable
          ></el-input>
        </el-form-item>
        <el-form-item>
          <el-input 
            placeholder="请输入姓名" 
            v-model="searchForm.name"
            prefix-icon="el-icon-s-custom"
            clearable
          ></el-input>
        </el-form-item>
        <el-form-item>
          <el-input 
            placeholder="请输入手机" 
            v-model="searchForm.phone"
            prefix-icon="el-icon-mobile-phone"
            clearable
          ></el-input>
        </el-form-item>
        <el-form-item>
          <el-input 
            placeholder="请输入身份证" 
            v-model="searchForm.idcard"
            prefix-icon="el-icon-document"
            clearable
          ></el-input>
        </el-form-item>
        <el-form-item>
          <el-button 
            type="primary" 
            @click="handleSearch" 
            icon="el-icon-search"
            class="search-button"
          >查询</el-button>
          <el-button 
            type="success" 
            @click="handleAdd" 
            icon="el-icon-plus"
            class="add-button"
          >新增用户</el-button>
        </el-form-item>
      </el-form>
      
      <!-- 表格区域 -->
      <div class="table-container">
        <el-table
          v-loading="listLoading"
          :data="list"
          element-loading-text="数据加载中..."
          border
          fit
          highlight-current-row
          :header-cell-style="{ background: '#f5f7fa', color: '#606266', fontWeight: 'bold' }"
          :row-class-name="tableRowClassName"
          style="width: 100%"
        >
          <el-table-column align="center" label="ID" prop="id" width="70"></el-table-column>
          <el-table-column align="center" label="账号" prop="username" min-width="110"></el-table-column>
          <el-table-column align="center" label="姓名" prop="name" min-width="100"></el-table-column>
          
          <el-table-column align="center" label="性别" width="90">
            <template #default="{ row }">
              <el-tag 
                :type="row.gender == 1 ? 'primary' : 'danger'"
                effect="plain"
                size="medium"
                class="gender-tag"
              >
                <div class="gender-content">
                  <i :class="row.gender == 1 ? 'el-icon-male' : 'el-icon-female'"></i>
                  <span>{{ row.gender == 1 ? '男' : '女' }}</span>
                </div>
              </el-tag>
            </template>
          </el-table-column>
          
          <el-table-column align="center" label="手机" prop="phone" min-width="130"></el-table-column>
          <el-table-column align="center" label="身份证" prop="idcard" min-width="190"></el-table-column>
          
          <el-table-column align="center" label="头像" width="100">
            <template #default="{ row }">
              <el-avatar 
                :size="50" 
                :src="row.head ? resolveImage(row.head) : defaultAvatar"
                fit="cover"
              ></el-avatar>
            </template>
          </el-table-column>
          
          <el-table-column align="center" label="注册时间" prop="createTime" min-width="170"></el-table-column>
          
          <el-table-column label="操作" min-width="170" align="center">
            <template #default="{ row }">
              <div class="action-buttons">
                <el-button 
                  @click="handleEdit(row)" 
                  type="primary" 
                  size="small" 
                  icon="el-icon-edit"
                  plain
                  class="action-button"
                >编辑</el-button>
                <el-popconfirm 
                  title="确定删除该用户吗？" 
                  @confirm="deleteCofirm(row)"
                  icon="el-icon-warning"
                  confirm-button-text="确定"
                  cancel-button-text="取消"
                  confirm-button-type="danger"
                >
                  <template #reference>
                    <el-button 
                      type="danger" 
                      size="small" 
                      icon="el-icon-delete" 
                      plain
                      class="action-button"
                    >删除</el-button>
                  </template>
                </el-popconfirm>
              </div>
            </template>
          </el-table-column>
        </el-table>

        <!-- 空数据状态 -->
        <div class="empty-state" v-if="list && list.length === 0 && !listLoading">
          <i class="el-icon-user-solid"></i>
          <p>暂无用户数据</p>
          <el-button type="primary" @click="handleAdd">添加用户</el-button>
        </div>
        
        <!-- 分页区域 -->
        <el-pagination
          class="pagination"
          background
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
          :current-page="pageNum"
          :page-sizes="[10, 20, 50]"
          :page-size="pageSize"
          layout="total, sizes, prev, pager, next, jumper"
          :total="total"
        >
        </el-pagination>
      </div>
    </div>
    
    <!-- 添加/修改的弹框 -->
    <el-dialog 
      :title="dialogTitle" 
      v-model="dialogFormVisible"
      width="500px"
      center
      :close-on-click-modal="false"
      :before-close="handleDialogClose"
    >
      <el-form 
        label-position="right" 
        :model="dialogForm" 
        label-width="80px"
        class="dialog-form"
        :rules="formRules"
        ref="dialogFormRef"
      >
        <el-form-item label="头像" class="avatar-form-item">
          <div class="avatar-upload-container">
            <div class="avatar-preview-wrapper">
              <el-avatar 
                :size="100" 
                :src="dialogForm.head ? resolveImage(dialogForm.head) : defaultAvatar"
                class="avatar-preview"
                fit="cover"
              ></el-avatar>
            </div>
            <div class="avatar-controls">
              <el-upload
                class="avatar-uploader"
                action="#"
                :http-request="uploadHeadFile"
                :show-file-list="false"
                :file-list="fileList"
                accept="image/*"
              >
                <el-button type="primary" size="small" icon="el-icon-upload2" plain>
                  选择头像
                </el-button>
              </el-upload>
              <div class="upload-tips">
                <p>支持JPG、PNG格式，最大10MB</p>
              </div>
            </div>
          </div>
        </el-form-item>
        
        <el-form-item label="账号" prop="username">
          <el-input 
            placeholder="请输入账号" 
            v-model="dialogForm.username" 
            prefix-icon="el-icon-user"
            autocomplete="off"
          ></el-input>
        </el-form-item>
        
        <el-form-item label="密码" prop="password">
          <el-input 
            type="password" 
            placeholder="请输入密码" 
            v-model="dialogForm.password" 
            prefix-icon="el-icon-lock"
            autocomplete="off"
            show-password
          ></el-input>
        </el-form-item>
        
        <el-form-item label="姓名" prop="name">
          <el-input 
            placeholder="请输入姓名" 
            v-model="dialogForm.name" 
            prefix-icon="el-icon-s-custom"
            autocomplete="off"
          ></el-input>
        </el-form-item>
        
        <el-form-item label="性别" prop="gender">
          <el-radio-group v-model="dialogForm.gender">
            <el-radio :label="1">
              <i class="el-icon-male" style="color: #409EFF;"></i> 男
            </el-radio>
            <el-radio :label="2">
              <i class="el-icon-female" style="color: #F56C6C;"></i> 女
            </el-radio>
          </el-radio-group>
        </el-form-item>
        
        <el-form-item label="手机" prop="phone">
          <el-input 
            placeholder="请输入手机号码" 
            v-model="dialogForm.phone" 
            prefix-icon="el-icon-mobile-phone"
            autocomplete="off"
          ></el-input>
        </el-form-item>
        
        <el-form-item label="身份证" prop="idcard">
          <el-input 
            placeholder="请输入身份证号码" 
            v-model="dialogForm.idcard" 
            prefix-icon="el-icon-document"
            autocomplete="off"
          ></el-input>
        </el-form-item>
      </el-form>
      
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="handleDialogClose" plain>取 消</el-button>
          <el-button type="primary" @click="dialogConfirm" :loading="submitLoading">确 定</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, nextTick, onMounted, getCurrentInstance } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  findMemberPageAPI,
  addMemberAPI,
  modifyMemberAPI,
  removeMemberAPI,
} from "@/api/member";
import request from '@/utils/request'
import defaultAvatar from '@/assets/head.jpg'

const list = ref([])
const listLoading = ref(true)
const pageSize = ref(10)
const pageNum = ref(1)
const total = ref(0)
const searchForm = reactive({})

const dialogTitle = ref('添加用户')
const dialogFormVisible = ref(false)
const dialogForm = reactive({})
const submitLoading = ref(false)
const genderOptions = [
  { label: '男', value: 1 },
  { label: '女', value: 2 },
]
const fileList = ref([])
const formRules = {
  username: [
    { required: true, message: '请输入账号', trigger: 'blur' },
    { min: 3, max: 20, message: '长度在 3 到 20 个字符', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '长度在 6 到 20 个字符', trigger: 'blur' }
  ],
  name: [
    { required: true, message: '请输入姓名', trigger: 'blur' }
  ],
  gender: [
    { required: true, message: '请选择性别', trigger: 'change' }
  ],
  phone: [
    { required: true, message: '请输入手机号码', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码', trigger: 'blur' }
  ],
  idcard: [
    { required: true, message: '请输入身份证号码', trigger: 'blur' },
    { pattern: /(^\d{15}$)|(^\d{18}$)|(^\d{17}(\d|X|x)$)/, message: '请输入正确的身份证号码', trigger: 'blur' }
  ]
}

const dialogFormRef = ref()

const { appContext } = getCurrentInstance()
const baseFileUrl = appContext.config.globalProperties.$baseFileUrl
function resolveImage(path) {
  const resolver = appContext.config.globalProperties.$resolveImageUrl
  return typeof resolver === 'function' ? resolver(path) : ''
}

function tableRowClassName({ rowIndex }) {
  return rowIndex % 2 === 0 ? 'even-row' : 'odd-row'
}

function handleDialogClose() {
  ElMessageBox.confirm('确认关闭？未保存的数据将会丢失', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    dialogFormVisible.value = false
    nextTick(() => {
      for (const key of Object.keys(dialogForm)) delete dialogForm[key]
      fileList.value = []
      if (dialogFormRef.value) {
        dialogFormRef.value.clearValidate()
        dialogFormRef.value.resetFields()
      }
    })
  }).catch(() => {})
}

function handleSizeChange(val) {
  pageSize.value = val
  fetchData()
}

function handleCurrentChange(val) {
  pageNum.value = val
  fetchData()
}

function handleSearch() {
  pageNum.value = 1
  fetchData()
}

async function fetchData() {
  listLoading.value = true
  try {
    const response = await findMemberPageAPI(
      pageNum.value,
      pageSize.value,
      searchForm
    )
    list.value = response.data.records
    total.value = response.data.total
  } catch (error) {
    console.error('获取用户列表失败', error)
  } finally {
    listLoading.value = false
  }
}

function handleAdd() {
  for (const key of Object.keys(dialogForm)) delete dialogForm[key]
  fileList.value = []
  if (dialogFormRef.value) {
    dialogFormRef.value.clearValidate()
    dialogFormRef.value.resetFields()
  }
  dialogTitle.value = '新增用户'
  dialogFormVisible.value = true
  nextTick(() => {
    Object.assign(dialogForm, {
      username: '',
      password: '',
      name: '',
      gender: 1,
      phone: '',
      idcard: '',
      head: ''
    })
  })
}

function handleEdit(row) {
  for (const key of Object.keys(dialogForm)) delete dialogForm[key]
  fileList.value = []
  if (dialogFormRef.value) {
    dialogFormRef.value.clearValidate()
    dialogFormRef.value.resetFields()
  }
  dialogTitle.value = '修改用户'
  dialogFormVisible.value = true
  nextTick(() => {
    Object.assign(dialogForm, JSON.parse(JSON.stringify(row)))
    fileList.value = row.head ? [{
      name: dialogForm.head,
      url: resolveImage(dialogForm.head)
    }] : []
  })
}

function dialogConfirm() {
  dialogFormRef.value.validate(async (valid) => {
    if (!valid) return false
    submitLoading.value = true
    try {
      let res = null
      if (dialogTitle.value === '新增用户') {
        res = await addMemberAPI(dialogForm)
      } else {
        res = await modifyMemberAPI(dialogForm)
      }
      ElMessage[res.flag ? 'success' : 'error'](res.message)
      if (res.flag) {
        dialogFormVisible.value = false
        fetchData()
        nextTick(() => {
          for (const key of Object.keys(dialogForm)) delete dialogForm[key]
          fileList.value = []
          if (dialogFormRef.value) {
            dialogFormRef.value.clearValidate()
            dialogFormRef.value.resetFields()
          }
        })
      }
    } catch (error) {
      console.error('操作失败', error)
    } finally {
      submitLoading.value = false
    }
  })
}

async function deleteCofirm(row) {
  try {
    const res = await removeMemberAPI(row.id)
    ElMessage[res.flag ? 'success' : 'error'](res.message)
    if (res.flag) {
      if (list.value.length === 1 && pageNum.value > 1) {
        pageNum.value -= 1
      }
      fetchData()
    }
  } catch (error) {
    console.error('删除失败', error)
  }
}

function validateFile(file) {
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

function uploadHeadFile(item) {
  const file = item.file
  if (!validateFile(file)) return
  const form = new FormData()
  form.append('photo', file)
  ElMessage.info('头像上传中，请稍候...')
  request.post('/file/uploadPhoto', form, 'form').then((resp) => {
    if (resp.data && resp.data.filename) {
      dialogForm.head = resp.data.filename
      fileList.value = [{
        name: dialogForm.head,
        url: resolveImage(dialogForm.head)
      }]
      ElMessage.success('头像上传成功')
    } else {
      ElMessage.error('头像上传失败，请重试')
    }
  }).catch((error) => {
    console.error('头像上传失败', error)
  })
}

onMounted(fetchData)
</script>

<style lang="less" scoped>
.member-container {
  padding: 20px;
  
  .section-header {
    background: linear-gradient(135deg, #1E3A8A, #274597);
    color: #fff;
    padding: 25px;
    border-radius: 10px;
    margin-bottom: 20px;
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
    
    .section-title {
      font-size: 24px;
      font-weight: 700;
      margin: 0;
      letter-spacing: 1px;
    }
    
    .section-subtitle {
      font-size: 14px;
      margin-top: 8px;
      color: #ffd700;
      letter-spacing: 0.5px;
    }
  }
  
  .member-card {
    background: #fff;
    border-radius: 10px;
    box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
    padding: 20px;
    
    .search-form {
      display: flex;
      align-items: center;
      flex-wrap: wrap;
      margin-bottom: 20px;
      
      .el-form-item {
        margin-bottom: 10px;
        margin-right: 15px;
      }
      
      .search-button {
        background: #1E3A8A;
        border-color: #1E3A8A;
        transition: all 0.3s;
        
        &:hover {
          background: darken(#1E3A8A, 10%);
          transform: translateY(-2px);
        }
      }
      
      .add-button {
        transition: all 0.3s;
        
        &:hover {
          transform: translateY(-2px);
        }
      }
    }
    
    .table-container {
      position: relative;
      overflow-x: hidden; // 防止水平滚动条
      
      .gender-tag {
        padding: 4px 8px;
        border-radius: 4px;
        
        .gender-content {
          display: flex;
          align-items: center;
          justify-content: center;
          
          i {
            margin-right: 4px;
            font-size: 14px;
          }
          
          span {
            line-height: 1;
          }
        }
      }
      
      .empty-state {
        text-align: center;
        padding: 40px 0;
        color: #909399;
        
        i {
          font-size: 60px;
          color: #DCDFE6;
          margin-bottom: 15px;
        }
        
        p {
          font-size: 16px;
          margin-bottom: 20px;
        }
      }
      
      .pagination {
        margin-top: 20px;
        display: flex;
        justify-content: flex-end;
      }
    }
  }
}

.dialog-form {
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
      
      .avatar-uploader {
        margin-bottom: 8px;
        
        :deep(.el-button) {
          position: relative;
          overflow: hidden;
          
          &:hover {
            transform: translateY(-2px);
          }
        }
      }
      
      .upload-tips {
        font-size: 12px;
        color: #909399;
        margin-top: 5px;
      }
    }
  }
}

// 表格行样式
:deep(.even-row) {
  background-color: #fafafa;
}

:deep(.odd-row) {
  background-color: #ffffff;
}

// 表格hover样式
:deep(.el-table__body tr:hover > td) {
  background-color: rgba(30, 58, 138, 0.1) !important;
}

.action-buttons {
  display: flex;
  justify-content: center;
  gap: 12px;
  
  .action-button {
    margin: 0;
  }
}
</style>
