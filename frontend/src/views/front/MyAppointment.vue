<template>
    <div class="appointment-container">
        <div class="appointment-card">
            <!-- 标题区域 -->
            <div class="section-header">
                <h1 class="section-title">我的预约</h1>
                <div class="section-subtitle">查看和管理您的所有酒店预约记录</div>
            </div>
            
            <!-- 搜索区域 -->
            <div class="search-panel">
                <div class="search-container">
                    <div class="search-input-group">
                        <el-input 
                            placeholder="搜索备注信息" 
                            v-model="searchForm.remark" 
                            clearable
                            class="search-input">
                            <template #prefix>
                                <i class="fas fa-search"></i>
                            </template>
                        </el-input>
                    </div>
                    <div class="search-select-group">
                        <el-select 
                            v-model="searchForm.status" 
                            placeholder="选择预约状态"
                            class="status-select"
                            clearable>
                            <el-option label="待确认" :value="1">
                                <i class="fas fa-clock status-icon waiting"></i> 待确认
                            </el-option>
                            <el-option label="预订成功" :value="2">
                                <i class="fas fa-check-circle status-icon success"></i> 预订成功
                            </el-option>
                            <el-option label="预订失败" :value="3">
                                <i class="fas fa-times-circle status-icon fail"></i> 预订失败
                            </el-option>
                    </el-select>
                    </div>
                    <div class="search-button-group">
                        <el-button 
                            type="primary" 
                            @click="handleSearch"
                            class="search-button">
                            <i class="fas fa-filter"></i> 筛选结果
                        </el-button>
                    </div>
                </div>
            </div>
            
            <!-- 表格区域 -->
            <div class="table-container">
                <!-- 加载状态 -->
                <div v-if="listLoading" class="loading-state">
                    <i class="fas fa-spinner fa-spin"></i>
                    <span>正在加载您的预约数据...</span>
                </div>
                
                <!-- 空状态 -->
                <div v-else-if="!list || list.length === 0" class="empty-state">
                    <i class="fas fa-calendar-times"></i>
                    <p>您还没有任何预约记录</p>
                    <el-button type="primary" class="add-button" @click="goToReservation">
                        <i class="fas fa-plus"></i> 立即预约房间
                    </el-button>
                </div>
                
                <!-- 预约列表 -->
                <div v-else>
                    <el-table 
                        :data="list" 
                        border 
                        fit 
                        highlight-current-row
                        size="medium"
                        class="appointment-table">
                        <!-- 表格列 -->
                        <el-table-column align="center" label="预约编号" width="80">
                    <template #default="scope">
                                <span class="order-id">#{{ scope.row.id }}</span>
                    </template>
                </el-table-column>
                        
                        <el-table-column align="center" label="房型信息" width="180">
                    <template #default="scope">
                                <div class="room-info">
                                    <div class="room-type">{{ scope.row.room.category.categoryName }}</div>
                                    <div class="room-number">房号: {{ scope.row.room.roomNum }}</div>
                                </div>
                    </template>
                </el-table-column>
                        
                        <el-table-column align="center" label="预约详情">
                    <template #default="scope">
                                <div class="reservation-details">
                                    <div class="check-in-date">
                                        <i class="fas fa-calendar-alt"></i>
                                        入住日期: {{ formatDate(scope.row.startTime) }}
                                    </div>
                                    <div class="stay-duration">
                                        <i class="fas fa-moon"></i>
                                        入住天数: {{ scope.row.days }} 晚
                                    </div>
                                    <div class="total-price">
                                        <i class="fas fa-wallet"></i>
                                        总价: <span class="price-value">￥{{ scope.row.money * scope.row.days }}</span>
                                    </div>
                                </div>
                    </template>
                </el-table-column>

                        <el-table-column align="center" label="预约状态" width="120">
                    <template #default="scope">
                                <div class="status-container">
                                    <el-tag 
                                        v-if="scope.row.status == 1" 
                                        type="warning"
                                        effect="dark"
                                        class="status-tag">
                                        <div class="tag-content">
                                            <i class="fas fa-clock"></i>
                                            <span>待确认</span>
                                        </div>
                                    </el-tag>
                                    <el-tag 
                                        v-if="scope.row.status == 2" 
                                        type="success"
                                        effect="dark"
                                        class="status-tag">
                                        <div class="tag-content">
                                            <i class="fas fa-check-circle"></i>
                                            <span>预订成功</span>
                                        </div>
                                    </el-tag>
                                    <el-tag 
                                        v-if="scope.row.status == 3" 
                                        type="danger"
                                        effect="dark"
                                        class="status-tag">
                                        <div class="tag-content">
                                            <i class="fas fa-times-circle"></i>
                                            <span>预订失败</span>
                                        </div>
                                    </el-tag>
                                    <div class="remark-text" v-if="scope.row.remark">
                                        备注: {{ scope.row.remark }}
                                    </div>
                                </div>
                    </template>
                </el-table-column>
                        
                        <el-table-column align="center" label="操作" width="200">
                    <template #default="scope">
                                <el-button 
                                    v-if="scope.row.status == 1"
                                    type="warning" 
                                    size="mini"
                                    @click="openCancelDialog(scope.row)"
                                    style="margin-right:8px"
                                >取消预订</el-button>

                                <el-popconfirm 
                                    title="确定删除这条预约记录吗？" 
                                    @confirm="deleteCofirm(scope.row)"
                                    confirm-button-text="确定"
                                    cancel-button-text="取消"
                                    icon="el-icon-warning">
                                    <template #reference>
                                        <el-button 
                                            type="danger" 
                                            size="mini" 
                                            class="delete-button">
                                            <div class="button-content">
                                                <i class="fas fa-trash-alt"></i>
                                                <span>删除</span>
                                            </div>
                                        </el-button>
                                    </template>
                        </el-popconfirm>
                    </template>
                </el-table-column>
            </el-table>

            <!-- 取消原因弹框 -->
            <el-dialog title="取消预约" v-model="cancelDialogVisible" width="500px">
              <el-form :model="cancelForm" label-width="90px">
                <el-form-item label="取消原因">
                  <el-input
                    type="textarea"
                    v-model="cancelForm.remark"
                    :rows="4"
                    placeholder="请填写取消原因（必填）"
                  />
                </el-form-item>
              </el-form>
              <template #footer>
                <el-button @click="cancelDialogVisible=false">取 消</el-button>
                <el-button type="primary" @click="confirmCancel">确 定</el-button>
              </template>
            </el-dialog>
                    
            <!-- 分页器 -->
                    <div class="pagination-container">
                        <el-pagination 
                            background 
                            @size-change="handleSizeChange" 
                            @current-change="handleCurrentChange"
                            :current-page="pageNum" 
                            :page-sizes="[10, 20, 30]" 
                            :page-size="pageSize"
                            layout="total, sizes, prev, pager, next, jumper" 
                            :total="total"
                            class="custom-pagination">
            </el-pagination>
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { findAppointmentPageAPI, removeAppointmentAPI, cancelAppointmentAPI } from '@/api/appointment'

defineOptions({ name: 'MyAppointment' })

const list = ref(null)
const listLoading = ref(true)
const pageSize = ref(10)
const pageNum = ref(1)
const total = ref(0)
const searchForm = ref({})
const cancelDialogVisible = ref(false)
const cancelForm = ref({})

const router = useRouter()

function loadFontAwesome() {
  if (!document.querySelector('link[href*="font-awesome"]')) {
    const link = document.createElement('link')
    link.setAttribute('rel', 'stylesheet')
    link.setAttribute('href', 'https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.4/css/all.min.css')
    document.head.appendChild(link)
  }
}

function formatDate(dateString) {
  if (!dateString) return ''
  return dateString.slice(0, 10)
}

function goToReservation() {
  // 跳到客房列表进行预约
  router.push('/front/room')
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
      searchForm.value
    )
    list.value = response.data.records
    total.value = response.data.total
  } catch (error) {
    console.error('获取预约列表失败', error)
  } finally {
    listLoading.value = false
  }
}

async function deleteCofirm(row) {
  try {
    const res = await removeAppointmentAPI(row.id)
    ElMessage[res.flag ? 'success' : 'error'](res.message)
    if (res.flag) fetchData()
  } catch (error) {
    console.error('删除预约失败', error)
  }
}

function openCancelDialog(row){
  cancelForm.value = { id: row.id, remark: '' }
  cancelDialogVisible.value = true
}

async function confirmCancel(){
  if(!cancelForm.value.remark){
    ElMessage.error('请填写取消原因')
    return
  }
  try{
    const res = await cancelAppointmentAPI({ id: cancelForm.value.id, remark: cancelForm.value.remark })
    ElMessage[res.flag ? 'success' : 'error'](res.message)
    if(res.flag){
      cancelDialogVisible.value = false
      fetchData()
    }
  }catch(e){
    console.error('取消失败', e)
  }
}

onMounted(() => {
  loadFontAwesome()
  fetchData()
})
</script>

<style lang="less" scoped>
.appointment-container {
    width: 900px;
    margin: 30px auto;
    font-family: 'Arial', sans-serif;
}

.appointment-card {
    background-color: #fff;
    border-radius: 12px;
    box-shadow: 0 6px 30px rgba(0, 0, 0, 0.1);
    overflow: hidden;
}

.section-header {
    background: linear-gradient(135deg, #1E3A8A, #274597);
    color: #fff;
    padding: 25px;
    text-align: center;
    
    .section-title {
        font-size: 28px;
        font-weight: 700;
        margin: 0;
        letter-spacing: 1px;
        text-shadow: 0 2px 4px rgba(0, 0, 0, 0.2);
    }
    
    .section-subtitle {
        font-size: 16px;
        margin-top: 8px;
        color: #FFD700;
        letter-spacing: 0.5px;
    }
}

.search-panel {
    padding: 20px;
    background-color: #f8f9fa;
    border-bottom: 1px solid #eee;
    
    .search-container {
        display: flex;
        justify-content: center;
        align-items: center;
        flex-wrap: wrap;
        gap: 15px;
        max-width: 800px;
        margin: 0 auto;
    }
    
    .search-input-group, .search-select-group, .search-button-group {
        display: flex;
        align-items: center;
    }
    
    .search-input {
        width: 280px;
        :deep(.el-input__inner) {
            border-radius: 20px;
            height: 40px;
            padding-left: 40px;
            padding-right: 30px;
            border: 1px solid #e0e0e0;
            transition: all 0.3s ease;
            font-size: 14px;
            
            &:focus, &:hover {
                border-color: #1E3A8A;
                box-shadow: 0 0 8px rgba(30, 58, 138, 0.1);
            }
        }
        
        :deep(.el-input__prefix) {
            left: 15px;
            color: #1E3A8A;
            display: flex;
            align-items: center;
            height: 100%;
        }
        
        :deep(.el-input__icon) {
            line-height: 40px;
            width: 20px;
            font-size: 14px;
        }
        
        :deep(.el-input__suffix) {
            right: 10px;
            display: flex;
            align-items: center;
            height: 100%;
        }
        
        :deep(.el-input__clear) {
            color: #999;
            
            &:hover {
                color: #666;
            }
        }
    }
    
    .status-select {
        width: 180px;
        :deep(.el-input__inner) {
            border-radius: 20px;
            height: 40px;
            padding-left: 15px;
            padding-right: 30px;
            border: 1px solid #e0e0e0;
            transition: all 0.3s ease;
            font-size: 14px;
            
            &:focus, &:hover {
                border-color: #1E3A8A;
                box-shadow: 0 0 8px rgba(30, 58, 138, 0.1);
            }
        }
        
        :deep(.el-input__suffix) {
            right: 12px;
            display: flex;
            align-items: center;
            height: 100%;
        }
        
        :deep(.el-select__caret) {
            color: #1E3A8A;
            line-height: 40px;
            font-size: 14px;
        }
        
        :deep(.el-select-dropdown__item) {
            padding: 0 15px;
            height: 40px;
            line-height: 40px;
            
            i {
                margin-right: 8px;
                vertical-align: middle;
            }
        }
    }
    
    .search-button {
        border-radius: 20px;
        background: linear-gradient(135deg, #1E3A8A, #274597);
        border: none;
        padding: 0 25px;
        height: 40px;
        font-weight: 500;
        letter-spacing: 0.5px;
        box-shadow: 0 3px 6px rgba(30, 58, 138, 0.2);
        
        i {
            margin-right: 6px;
        }
        
        &:hover {
            background: linear-gradient(135deg, #274597, #1E3A8A);
            transform: translateY(-2px);
            box-shadow: 0 4px 8px rgba(30, 58, 138, 0.3);
        }
        
        &:active {
            transform: translateY(0);
            box-shadow: 0 2px 4px rgba(30, 58, 138, 0.2);
        }
    }
    
    @media (max-width: 768px) {
        .search-container {
            flex-direction: column;
            align-items: stretch;
        }
        
        .search-input, .status-select {
            width: 100%;
        }
        
        .search-button {
            width: 100%;
        }
    }
}

.table-container {
    padding: 20px;
}

.loading-state, .empty-state {
    text-align: center;
    padding: 40px 0;
    color: #666;
    
    i {
        font-size: 50px;
        color: #1E3A8A;
        margin-bottom: 15px;
        display: block;
    }
    
    p {
        font-size: 18px;
        margin-bottom: 20px;
    }
    
    .add-button {
        background: linear-gradient(135deg, #1E3A8A, #274597);
        border: none;
        border-radius: 20px;
        padding: 10px 25px;
        
        &:hover {
            background: linear-gradient(135deg, #274597, #1E3A8A);
            transform: translateY(-2px);
            box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
        }
    }
}

.appointment-table {
    margin-bottom: 20px;
    box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05);
    border-radius: 8px;
    overflow: hidden;
    
        :deep(.el-table__header-wrapper) {
        th {
            background-color: #f8f9fa;
            color: #1E3A8A;
            font-weight: 600;
            padding: 15px 0;
        }
    }
    
    :deep(.el-table__row) {
        transition: all 0.3s ease;
        
        &:hover {
            background-color: #f0f4ff !important;
        }
    }
}

.order-id {
    font-weight: 600;
    color: #1E3A8A;
}

.room-info {
    .room-type {
        font-weight: 600;
        color: #1E3A8A;
        margin-bottom: 5px;
    }
    
    .room-number {
        font-size: 13px;
        color: #666;
    }
}

.reservation-details {
    display: flex;
    justify-content: space-around;
    
    .check-in-date, .stay-duration, .total-price {
        display: flex;
        align-items: center;
        
        i {
            margin-right: 5px;
            color: #1E3A8A;
        }
    }
    
    .price-value {
        color: #f56c6c;
        font-weight: 600;
    }
}

.status-container {
    display: flex;
    flex-direction: column;
    align-items: center;
    
    .status-tag {
        padding: 6px 12px;
        height: auto;
        line-height: normal;
        box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
        border: none;
        min-width: 90px;
        
        .tag-content {
            display: flex;
            align-items: center;
            justify-content: center;
            
            i {
                margin-right: 5px;
                font-size: 14px;
                display: flex;
                align-items: center;
            }
            
            span {
                display: inline-block;
                line-height: 1;
                font-weight: 500;
            }
        }
        
        :deep(.el-tag__content) {
            display: flex;
            align-items: center;
            justify-content: center;
            line-height: 1;
        }
        
        // 悬停效果
        &:hover {
            transform: translateY(-2px);
            box-shadow: 0 4px 8px rgba(0, 0, 0, 0.15);
            transition: all 0.3s ease;
        }
    }
    
    .remark-text {
        margin-top: 8px;
        font-size: 12px;
        color: #666;
        max-width: 120px;
        overflow: hidden;
        text-overflow: ellipsis;
        white-space: nowrap;
        text-align: center;
    }
}

.delete-button {
    transition: all 0.3s ease;
    border-radius: 15px;
    padding: 7px 15px;
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
    
    .button-content {
        display: flex;
        align-items: center;
        justify-content: center;
        
        i {
            margin-right: 5px;
            font-size: 12px;
        }
        
        span {
            font-weight: 500;
            font-size: 12px;
        }
    }
    
    &:hover {
        transform: translateY(-2px);
        box-shadow: 0 4px 8px rgba(0, 0, 0, 0.15);
    }
}

.pagination-container {
    text-align: center;
    margin-top: 30px;
    
    .custom-pagination {
        :deep(.btn-prev), :deep(.btn-next), :deep(.number) {
            background-color: #f8f9fa;
            border: none;
            margin: 0 5px;
            font-weight: 600;
            
            &:hover {
                color: #fff;
                background-color: #1E3A8A;
            }
        }
        
        :deep(.active) {
            background-color: #1E3A8A !important;
            color: #fff;
        }
    }
}

.status-icon {
    margin-right: 5px;
    
    &.waiting {
        color: #E6A23C;
    }
    
    &.success {
        color: #67C23A;
    }
    
    &.fail {
        color: #F56C6C;
    }
}

@media (max-width: 768px) {
    .appointment-container {
        width: 95%;
    }
    
    .reservation-details {
        flex-direction: column;
        align-items: flex-start;
        
        .check-in-date, .stay-duration, .total-price {
            margin-bottom: 5px;
        }
    }
}
</style>