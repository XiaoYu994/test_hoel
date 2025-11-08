<template>
  <div class="orders-container">
    <!-- 标题区域 -->
    <div class="section-header">
      <h1 class="section-title">入住管理</h1>
      <div class="section-subtitle">管理酒店住客入住、退房及结算信息</div>
    </div>
    
    <!-- 搜索和操作区域 -->
    <div class="orders-card">
      <el-form :inline="true" class="search-form" :model="searchForm">
        <el-form-item>
          <el-select 
            v-model="searchForm.status" 
            placeholder="请选择状态" 
            clearable
            class="custom-select"
          >
            <el-option 
              v-for="(label, index) in ['已预订', '已入住', '已退房']" 
              :key="index + 1" 
              :label="label" 
              :value="index + 1"
            >
              <span :style="{color: getStatusColor(index + 1)}">
                <i :class="getStatusIcon(index + 1)"></i>
                {{ label }}
              </span>
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-select 
            v-model="searchForm.memberId" 
            placeholder="请选择用户" 
            clearable
            filterable
            class="custom-select member-select"
          >
            <el-option 
              v-for="item in memberList" 
              :key="item.id" 
              :label="item.name+'-'+item.idcard" 
              :value="item.id"
            >
            </el-option>
          </el-select>
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
            @click="$router.push('/admin/addOrders')" 
            icon="el-icon-plus"
            class="add-button"
          >新增入住</el-button>
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
          <el-table-column align="center" label="ID" prop="id" width="50"></el-table-column>
          <el-table-column align="center" label="用户" prop="member.name" min-width="80"></el-table-column>
          <el-table-column align="center" label="电话" prop="member.phone" min-width="120"></el-table-column>
          <el-table-column align="center" label="身份证" prop="member.idcard" min-width="170" show-overflow-tooltip></el-table-column>
          <el-table-column align="center" label="性别" width="80">
            <template #default="scope">
              <el-tag 
                :type="(scope.row.member && scope.row.member.gender == 1) ? 'primary' : 'danger'"
                effect="plain"
                size="medium"
                class="gender-tag"
              >
                <div class="gender-content">
                  <i :class="(scope.row.member && scope.row.member.gender == 1) ? 'el-icon-male' : 'el-icon-female'"></i>
                  <span>{{ (scope.row.member && scope.row.member.gender == 1) ? '男' : '女' }}</span>
                </div>
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column align="center" label="房型" prop="room.category.categoryName" min-width="110"></el-table-column>
          <el-table-column align="center" label="房号" width="80">
            <template #default="scope">
              <el-tag type="primary" effect="plain" class="room-number">
                {{ scope.row.room.roomNum }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column align="center" label="入住时间" min-width="110">
            <template #default="scope">
              {{ scope.row.startTime.slice(0,10) }}
            </template>
          </el-table-column>
          <el-table-column align="center" label="天数" prop="days" width="70"></el-table-column>
          <el-table-column align="center" label="状态" width="90">
            <template #default="scope">
              <el-tag 
                :type="getStatusType(scope.row.status)"
                effect="plain"
                size="medium"
              >
                <i :class="getStatusIcon(scope.row.status)"></i>
                {{ getStatusText(scope.row.status) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column align="center" label="实付金额" min-width="100">
            <template #default="scope">
              <span class="money-value">￥{{ scope.row.money * scope.row.days }}</span>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="200" align="center">
            <template #default="scope">
              <div class="action-buttons">
                <el-button 
                  v-if="scope.row.status == 1" 
                  @click="confirmOrder(scope.row)" 
                  type="primary" 
                  size="small" 
                  icon="el-icon-check"
                  plain
                  class="action-button"
                >入住</el-button>
                <el-button 
                  v-if="scope.row.status != 3" 
                  @click="checkOutOrder(scope.row)" 
                  type="warning" 
                  size="small"
                  icon="el-icon-switch-button" 
                  plain
                  class="action-button"
                >退房</el-button>
                <el-popconfirm 
                  title="确定删除该入住记录吗？" 
                  @confirm="deleteCofirm(scope.row)"
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
          <i class="el-icon-house"></i>
          <p>暂无入住数据</p>
          <el-button type="primary" @click="$router.push('/admin/addOrders')">添加入住</el-button>
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

    <!-- 退房弹框 -->
    <el-dialog 
      title="退房" 
      v-model="showCheckOutDialog" 
      :before-close="handleCheckOutDialogClose"
      width="500px"
      center
      :close-on-click-modal="false"
    >
      <el-form 
        label-position="right" 
        label-width="80px"
        class="dialog-form"
      >
        <el-form-item label="备注">
          <el-input 
            type="textarea" 
            :rows="4" 
            placeholder="请输入退房备注(非必须)" 
            v-model="checkoutDialogForm.remark"
            class="textarea-input"
          ></el-input>
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="showCheckOutDialog = false" plain>取 消</el-button>
          <el-button type="primary" @click="checkOutDialogConfirm" :loading="submitLoading">确 定</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import {findOrdersPageAPI,removeOrdersAPI,confirmAPI,checkOutAPI} from "@/api/orders";
import { findAllMemberAPI } from '@/api/member'

const router = useRouter()

const list = ref(null)
const listLoading = ref(true)
const pageSize = ref(10)
const pageNum = ref(1)
const total = ref(0)
const searchForm = reactive({})
const dialogTitle = ref("添加")
const dialogFormVisible = ref(false)
const dialogForm = reactive({})
const memberList = ref([])
const checkoutDialogForm = reactive({})
const showCheckOutDialog = ref(false)
const submitLoading = ref(false)

function tableRowClassName({ row, rowIndex }) {
  return rowIndex % 2 === 0 ? 'even-row' : 'odd-row'
}

function getStatusText(status) {
  const statusMap = { 1: '已预订', 2: '已入住', 3: '已退房' }
  return statusMap[status] || '未知状态'
}

function getStatusType(status) {
  const typeMap = { 1: 'warning', 2: 'success', 3: 'danger' }
  return typeMap[status] || 'info'
}

function getStatusColor(status) {
  const colorMap = { 1: '#E6A23C', 2: '#67C23A', 3: '#F56C6C' }
  return colorMap[status] || '#909399'
}

function getStatusIcon(status) {
  const iconMap = { 1: 'el-icon-time', 2: 'el-icon-check', 3: 'el-icon-close' }
  return iconMap[status] || 'el-icon-info'
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
    const response = await findOrdersPageAPI(
      pageNum.value,
      pageSize.value,
      searchForm
    )
    list.value = response.data.records
    total.value = response.data.total
  } catch (error) {
    console.error('获取入住列表失败', error)
  } finally {
    listLoading.value = false
  }
}

async function deleteCofirm(row) {
  try {
    const res = await removeOrdersAPI(row.id)
    ElMessage({ message: res.message, type: res.flag ? 'success' : 'error' })
    if (list.value.length === 1 && pageNum.value > 1) {
      pageNum.value -= 1
    } else {
      pageNum.value = 1
    }
    fetchData()
  } catch (error) {
    console.error('删除失败', error)
  }
}

function confirmOrder(row) {
  ElMessageBox.confirm('确定入住吗?', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      const res = await confirmAPI(row.id)
      ElMessage({ message: res.message, type: res.flag ? 'success' : 'error' })
      fetchData()
    } catch (error) {
      console.error('确认入住失败', error)
    }
  }).catch(() => {})
}

function handleCheckOutDialogClose() {
  ElMessageBox.confirm('确认关闭？未保存的数据将会丢失', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    showCheckOutDialog.value = false
    checkoutDialogForm.remark = ''
  }).catch(() => {})
}

function checkOutOrder({ id }) {
  showCheckOutDialog.value = true
  Object.assign(checkoutDialogForm, { id })
}

async function checkOutDialogConfirm() {
  submitLoading.value = true
  try {
    const res = await checkOutAPI(checkoutDialogForm)
    ElMessage({ message: res.message, type: res.flag ? 'success' : 'error' })
    if (res.flag) {
      showCheckOutDialog.value = false
      fetchData()
    }
  } catch (error) {
    console.error('退房操作失败', error)
  } finally {
    submitLoading.value = false
  }
}

onMounted(async () => {
  fetchData()
  try {
    const res = await findAllMemberAPI()
    memberList.value = res.data
  } catch (error) {
    console.error('获取用户列表失败', error)
  }
})
</script>

<style lang="less" scoped>
.orders-container {
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
  
  .orders-card {
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
      
      .custom-select {
        width: 140px;
      }
      
      .member-select {
        width: 220px;
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
      overflow-x: auto;
      width: 100%;
      
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
      
      .room-number {
        font-weight: bold;
        font-size: 14px;
      }
      
      .money-value {
        font-weight: bold;
        color: #ff6b6b;
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
  .textarea-input {
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
  flex-wrap: wrap;
  gap: 8px;
  
  .action-button {
    margin: 0;
    margin-bottom: 5px;
    padding: 6px 10px;
  }
}
</style>
