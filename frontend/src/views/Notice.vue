<template>
  <div class="notice-container">
    <!-- 标题区域 -->
    <div class="section-header">
      <h1 class="section-title">公告管理</h1>
      <div class="section-subtitle">管理酒店通知与重要事项的发布</div>
    </div>
    
    <!-- 搜索和操作区域 -->
    <div class="notice-card">
      <el-form :inline="true" class="search-form" :model="searchForm">
        <el-form-item>
          <el-input 
            placeholder="请输入公告内容" 
            v-model="searchForm.content"
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
          >新增公告</el-button>
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
        >
          <el-table-column align="center" label="ID" prop="id" width="60"></el-table-column>
          <el-table-column align="center" label="标题" prop="title" min-width="200" show-overflow-tooltip></el-table-column>
          <el-table-column align="center" label="发布时间" width="180">
            <template #default="scope">
              {{ formatDate(scope.row.createTime) }}
            </template>
          </el-table-column>
          <el-table-column label="操作" width="180" align="center">
            <template #default="scope">
              <div class="action-buttons">
                <el-button 
                  @click="handleEdit(scope.row)" 
                  type="primary" 
                  size="small" 
                  icon="el-icon-edit"
                  plain
                  class="action-button"
                >编辑</el-button>
                <el-popconfirm 
                  title="确定删除该公告吗？" 
                  @confirm="deleteCofirm(scope.row)"
                  icon="el-icon-warning"
                  confirm-button-text="确定"
                  cancel-button-text="取消"
                  confirm-button-type="danger"
                >
                  <template #reference>
                    <el-button 
                      type="danger" 
                      size="小" 
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
          <i class="el-icon-document"></i>
          <p>暂无公告数据</p>
          <el-button type="primary" @click="handleAdd">添加公告</el-button>
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
      width="600px"
      center
      :close-on-click-modal="false"
      :before-close="handleDialogClose"
    >
      <el-form 
        label-position="right" 
        :model="dialogForm" 
        label-width="100px"
        class="dialog-form"
        :rules="formRules"
        ref="dialogFormRef"
      >
        <el-form-item label="公告标题" prop="title">
          <el-input 
            placeholder="请输入公告标题" 
            v-model="dialogForm.title" 
            prefix-icon="el-icon-collection-tag"
            class="form-input"
          ></el-input>
        </el-form-item>
        
        <el-form-item label="公告内容" prop="content">
          <el-input 
            type="textarea" 
            :rows="6" 
            placeholder="请输入公告内容" 
            v-model="dialogForm.content"
            class="form-textarea"
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
import { ref, reactive, onMounted, nextTick } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  findNoticePageAPI,
  addNoticeAPI,
  modifyNoticeAPI,
  removeNoticeAPI,
} from "@/api/notice";

const list = ref(null)
const listLoading = ref(true)
const pageSize = ref(10)
const pageNum = ref(1)
const total = ref(0)
const searchForm = reactive({})
const dialogTitle = ref('新增公告')
const dialogFormVisible = ref(false)
const dialogForm = reactive({})
const submitLoading = ref(false)
const dialogFormRef = ref(null)

const formRules = {
  title: [
    { required: true, message: '请输入公告标题', trigger: 'blur' },
    { min: 2, max: 50, message: '长度在 2 到 50 个字符', trigger: 'blur' }
  ],
  content: [
    { required: true, message: '请输入公告内容', trigger: 'blur' }
  ]
}

function tableRowClassName({ row, rowIndex }) {
  return rowIndex % 2 === 0 ? 'even-row' : 'odd-row'
}

function formatDate(dateStr) {
  if (!dateStr) return '—'
  return dateStr.replace('T', ' ').substring(0, 19)
}

function handleDialogClose() {
  ElMessageBox.confirm('确认关闭？未保存的数据将会丢失', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    dialogFormVisible.value = false
    dialogFormRef.value?.resetFields?.()
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
    const response = await findNoticePageAPI(
      pageNum.value,
      pageSize.value,
      searchForm
    )
    list.value = response.data.records
    total.value = response.data.total
  } catch (error) {
    console.error('获取公告列表失败', error)
  } finally {
    listLoading.value = false
  }
}

function handleAdd() {
  dialogTitle.value = '新增公告'
  dialogFormVisible.value = true
  Object.keys(dialogForm).forEach(k => delete dialogForm[k])
  nextTick(() => dialogFormRef.value?.resetFields?.())
}

function handleEdit(row) {
  dialogTitle.value = '修改公告'
  dialogFormVisible.value = true
  Object.assign(dialogForm, { ...row })
  nextTick(() => dialogFormRef.value?.resetFields?.())
}

function dialogConfirm() {
  dialogFormRef.value.validate(async (valid) => {
    if (!valid) return
    submitLoading.value = true
    try {
      let res = null
      if (dialogTitle.value === '新增公告') {
        res = await addNoticeAPI(dialogForm)
      } else {
        res = await modifyNoticeAPI(dialogForm)
      }
      ElMessage({ message: res.message, type: res.flag ? 'success' : 'error' })
      if (res.flag) {
        dialogFormVisible.value = false
        fetchData()
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
    const res = await removeNoticeAPI(row.id)
    ElMessage({ message: res.message, type: res.flag ? 'success' : 'error' })
    if (list.value.length === 1 && pageNum.value > 1) {
      pageNum.value -= 1
    }
    fetchData()
  } catch (error) {
    console.error('删除失败', error)
  }
}

onMounted(fetchData)
</script>

<style lang="less" scoped>
.notice-container {
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
  
  .notice-card {
    background: #fff;
    border-radius: 10px;
    box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
    padding: 20px;
    
    .search-form {
      display: flex;
      align-items: center;
      margin-bottom: 20px;
      
      .el-form-item {
        margin-bottom: 0;
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
  .form-input {
    width: 100%;
  }
  
  .form-textarea {
    width: 100%;
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
  flex-wrap: nowrap;
  
  .action-button {
    margin: 0;
    white-space: nowrap;
  }
}
</style>
