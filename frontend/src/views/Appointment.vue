<template>
  <div class="appointment-container">
    <!-- 标题区域 -->
    <div class="section-header">
      <h1 class="section-title">预约管理</h1>
      <div class="section-subtitle">管理客户的房间预订信息</div>
    </div>
    
    <!-- 搜索和操作区域 -->
    <div class="appointment-card">
      <el-form :inline="true" class="search-form" :model="searchForm">
        <el-form-item>
          <el-select 
            v-model="searchForm.status" 
            placeholder="请选择状态" 
            clearable
            class="custom-select"
          >
            <el-option 
              v-for="(label, index) in ['待确认', '预订成功', '预订失败']" 
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
          <el-table-column align="center" label="用户" prop="member.name" width="90"></el-table-column>
          <el-table-column align="center" label="电话" prop="member.phone" width="120"></el-table-column>
          <el-table-column align="center" label="身份证" prop="member.idcard" width="150" show-overflow-tooltip></el-table-column>
          <el-table-column align="center" label="性别" width="70">
            <template #default="scope">
              <div v-if="scope.row.member">
                <el-tag 
                  :type="scope.row.member.gender == 1 ? 'success' : 'danger'" 
                  effect="light"
                  size="small"
                >
                  {{ scope.row.member.gender == 1 ? '男' : '女' }}
                </el-tag>
              </div>
              <span v-else>--</span>
            </template>
          </el-table-column>
          <el-table-column align="center" label="房型" prop="room.category.categoryName" width="110">
            <template #default="scope">
              {{ scope.row.room?.category?.categoryName || '--' }}
            </template>
          </el-table-column>
          <el-table-column align="center" label="房号" width="90">
            <template #default="scope">
              <el-tag type="primary" effect="plain" class="room-number" v-if="scope.row.room">
                {{ scope.row.room.roomNum }}
              </el-tag>
              <span v-else>--</span>
            </template>
          </el-table-column>
          <el-table-column align="center" label="入住时间" width="110">
            <template #default="scope">
              {{ scope.row.startTime.slice(0,10) }}
            </template>
          </el-table-column>
          <el-table-column align="center" label="天数" prop="days" width="70"></el-table-column>
          <el-table-column align="center" label="状态" width="90">
            <template #default="scope">
              <el-tag 
                :type="getStatusType(scope.row.status)"
                effect="light"
                size="medium"
              >
                <i :class="getStatusIcon(scope.row.status)"></i>
                {{ getStatusText(scope.row.status) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column align="center" label="房价" width="90">
            <template #default="scope">
              <span class="money-value">￥{{ scope.row.money ? (scope.row.money * scope.row.days).toFixed(2) : '0.00' }}</span>
            </template>
          </el-table-column>
          <el-table-column align="center" label="押金" width="90">
            <template #default="scope">
              <span class="deposit-value">￥{{ scope.row.deposit ? scope.row.deposit.toFixed(2) : '0.00' }}</span>
            </template>
          </el-table-column>
          <el-table-column align="center" label="应付金额" width="100">
            <template #default="scope">
              <span class="total-value">￥{{ scope.row.money && scope.row.deposit ? ((scope.row.money * scope.row.days) + scope.row.deposit).toFixed(2) : '0.00' }}</span>
            </template>
          </el-table-column>
          <el-table-column label="操作" min-width="240" align="center">
            <template #default="scope">
              <div class="action-buttons">
                <el-button 
                  @click="showAppointmentDetail(scope.row)" 
                  type="info" 
                  size="small" 
                  icon="el-icon-document"
                  plain
                  class="action-button"
                >详情</el-button>
                <el-button 
                  v-if="scope.row.status == 1" 
                  @click="handleCofirmAppointemnt(scope.row)" 
                  type="primary" 
                  size="small" 
                  icon="el-icon-check"
                  plain
                  class="action-button"
                >确认</el-button>
                <el-button 
                  v-if="scope.row.status == 1" 
                  @click="handleCancelAppointemnt(scope.row)" 
                  type="warning" 
                  size="small" 
                  icon="el-icon-close"
                  plain
                  class="action-button"
                >取消</el-button>
                <el-popconfirm 
                  title="确定删除该预订记录吗？" 
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
          <i class="el-icon-date"></i>
          <p>暂无预订数据</p>
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

    <!-- 取消预订弹框 -->
    <el-dialog 
      title="取消预订" 
      v-model="dialogFormVisible"
      width="500px"
      center
      :close-on-click-modal="false"
    >
      <el-form 
        label-position="right" 
        :model="dialogForm" 
        label-width="80px"
        class="dialog-form"
      >
        <el-form-item label="备注">
          <el-input 
            type="textarea" 
            placeholder="请输入取消的备注信息" 
            v-model="dialogForm.remark" 
            rows="4"
            autocomplete="off"
          ></el-input>
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="dialogFormVisible = false" plain>取 消</el-button>
          <el-button type="primary" @click="dialogConfirm">确 定</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 预约详情弹框 -->
    <el-dialog 
      title="预约详情" 
      v-model="showDetailDialog" 
      width="600px"
      center
    >
      <el-descriptions :column="2" border>
        <el-descriptions-item label="用户姓名">{{ detailData.member?.name }}</el-descriptions-item>
        <el-descriptions-item label="电话">{{ detailData.member?.phone }}</el-descriptions-item>
        <el-descriptions-item label="身份证" :span="2">{{ detailData.member?.idcard }}</el-descriptions-item>
        <el-descriptions-item label="性别">
          <el-tag :type="detailData.member?.gender == 1 ? 'primary' : 'danger'">{{ detailData.member?.gender == 1 ? '男' : '女' }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="房型">{{ detailData.room?.category?.categoryName }}</el-descriptions-item>
        <el-descriptions-item label="房号">{{ detailData.room?.roomNum }}</el-descriptions-item>
        <el-descriptions-item label="入住时间">{{ detailData.startTime?.slice(0,10) }}</el-descriptions-item>
        <el-descriptions-item label="入住天数">{{ detailData.days }} 天</el-descriptions-item>
        <el-descriptions-item label="房价">
          <span class="price-value">￥{{ detailData.money ? (detailData.money * detailData.days).toFixed(2) : '0.00' }}</span>
        </el-descriptions-item>
        <el-descriptions-item label="押金">
          <span class="deposit-value">￥{{ detailData.deposit ? detailData.deposit.toFixed(2) : '0.00' }}</span>
        </el-descriptions-item>
        <el-descriptions-item label="应付金额">
          <span class="total-value">￥{{ (detailData.money && detailData.deposit) ? ((detailData.money * detailData.days) + detailData.deposit).toFixed(2) : '0.00' }}</span>
        </el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="getStatusType(detailData.status)">{{ getStatusText(detailData.status) }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="备注" :span="2" v-if="detailData.remark">{{ detailData.remark }}</el-descriptions-item>
      </el-descriptions>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="showDetailDialog = false">关 闭</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  findAppointmentPageAPI,
  cancelAppointmentAPI,
  confirmAppointmentAPI,
  removeAppointmentAPI,
} from "@/api/appointment";
import { findAllMemberAPI } from '@/api/member'

const list = ref(null)
const listLoading = ref(true)
const pageSize = ref(10)
const pageNum = ref(1)
const total = ref(0)
const searchForm = reactive({})
const dialogFormVisible = ref(false)
const dialogForm = reactive({})
const memberList = ref([])
const showDetailDialog = ref(false)
const detailData = reactive({})

function tableRowClassName({ row, rowIndex }) {
  return rowIndex % 2 === 0 ? 'even-row' : 'odd-row'
}

function getStatusText(status) {
  const statusMap = { 1: '待确认', 2: '预订成功', 3: '预订失败' }
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

function handleCofirmAppointemnt(row) {
  ElMessageBox.confirm('此操作将同意用户预订, 是否继续?', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    const res = await confirmAppointmentAPI(row.id)
    ElMessage({ message: res.message, type: res.flag ? 'success' : 'error' })
    fetchData()
  }).catch(() => {})
}

function handleCancelAppointemnt(row) {
  dialogFormVisible.value = true
  Object.assign(dialogForm, { ...row })
}

function showAppointmentDetail(row) {
  showDetailDialog.value = true
  Object.assign(detailData, { ...row })
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
    const response = await findAppointmentPageAPI(
      pageNum.value,
      pageSize.value,
      searchForm
    )
    list.value = response.data.records
    total.value = response.data.total
  } catch (error) {
    console.error('获取预订列表失败', error)
  } finally {
    listLoading.value = false
  }
}

async function dialogConfirm() {
  if (!dialogForm.remark) {
    dialogForm.remark = '抱歉，此房间已满请重新预订其它房间'
  }
  try {
    const res = await cancelAppointmentAPI({ ...dialogForm })
    ElMessage({ message: res.message, type: res.flag ? 'success' : 'error' })
    if (res.flag) {
      dialogFormVisible.value = false
      fetchData()
    }
  } catch (error) {
  }
}

async function deleteCofirm(row) {
  try {
    const res = await removeAppointmentAPI(row.id)
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

onMounted(async () => {
  fetchData()
  const res = await findAllMemberAPI()
  memberList.value = res.data
})
</script>

<style lang="less" scoped>
.appointment-container {
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
  
  .appointment-card {
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
    }
    
    .table-container {
      position: relative;
      
      .room-number {
        font-weight: bold;
        font-size: 14px;
      }
      
      .money-value {
        font-weight: bold;
        color: #ff6b6b;
      }
      
      .deposit-value {
        font-weight: bold;
        color: #E6A23C;
      }
      
      .total-value {
        font-weight: bold;
        color: #409EFF;
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
  .el-textarea {
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
  
  .action-button {
    margin: 0;
  }
}

// 预约详情样式
.price-value {
  color: #ff6b6b;
  font-weight: bold;
}

.deposit-value {
  color: #E6A23C;
  font-weight: bold;
}

.total-value {
  color: #409EFF;
  font-weight: bold;
  font-size: 16px;
}
</style>
