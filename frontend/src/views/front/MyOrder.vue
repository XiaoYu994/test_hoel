<template>
    <div class="orders-container">
        <div class="orders-card">
            <!-- 标题区域 -->
            <div class="section-header">
                <h1 class="section-title">我的入住订单</h1>
                <div class="section-subtitle">查看和管理您的所有酒店入住记录</div>
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
                            placeholder="选择订单状态"
                            class="status-select"
                            clearable>
                            <el-option label="已预订" :value="1">
                                <i class="fas fa-calendar-check status-icon booked"></i> 已预订
                            </el-option>
                            <el-option label="已入住" :value="2">
                                <i class="fas fa-bed status-icon checkin"></i> 已入住
                            </el-option>
                            <el-option label="已退房" :value="3">
                                <i class="fas fa-door-open status-icon checkout"></i> 已退房
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
                    <span>正在加载您的入住订单数据...</span>
                </div>
                
                <!-- 空状态 -->
                <div v-else-if="!list || list.length === 0" class="empty-state">
                    <i class="fas fa-bed"></i>
                    <p>您还没有任何入住记录</p>
                    <el-button type="primary" class="add-button" @click="goToReservation">
                        <i class="fas fa-plus"></i> 预约入住房间
                    </el-button>
                </div>
                
                <!-- 订单列表 - 卡片式设计 -->
                <div v-else class="order-list-container">
                    <div 
                        v-for="item in list" 
                        :key="item.id" 
                        class="order-card">
                        <div class="order-header">
                            <div class="order-id">
                                <i class="fas fa-receipt"></i>
                                订单编号: <span>#{{ item.id }}</span>
                            </div>
                            <div class="status-container">
                                <el-tag 
                                    v-if="item.status == 1" 
                                    type="warning"
                                    effect="dark"
                                    class="status-tag">
                                    <div class="tag-content">
                                        <i class="fas fa-calendar-check"></i>
                                        <span>已预订</span>
                                    </div>
                                </el-tag>
                                <el-tag 
                                    v-if="item.status == 2" 
                                    type="success"
                                    effect="dark"
                                    class="status-tag">
                                    <div class="tag-content">
                                        <i class="fas fa-bed"></i>
                                        <span>已入住</span>
                                    </div>
                                </el-tag>
                                <el-tag 
                                    v-if="item.status == 3" 
                                    type="info"
                                    effect="dark"
                                    class="status-tag">
                                    <div class="tag-content">
                                        <i class="fas fa-door-open"></i>
                                        <span>已退房</span>
                                    </div>
                                </el-tag>
                            </div>
                        </div>
                        
                        <div class="order-content">
                            <div class="order-section room-info-section">
                                <div class="section-title">
                                    <i class="fas fa-hotel"></i> 房型信息
                                </div>
                                <div class="section-content">
                                    <div class="info-item">
                                        <span class="label">房型:</span>
                                        <span class="value">{{ item.room.category.categoryName }}</span>
                                    </div>
                                    <div class="info-item">
                                        <span class="label">房号:</span>
                                        <span class="value">{{ item.room.roomNum }}</span>
                                    </div>
                                    <div class="info-item">
                                        <span class="label">单价:</span>
                                        <span class="value price">￥{{ item.room.category.price ? item.room.category.price.toFixed(2) : '0.00' }}/晚</span>
                                    </div>
                                </div>
                            </div>
                            
                            <div class="order-section checkin-section">
                                <div class="section-title">
                                    <i class="fas fa-calendar-alt"></i> 入住详情
                                </div>
                                <div class="section-content">
                                    <div class="info-item">
                                        <span class="label">入住日期:</span>
                                        <span class="value">{{ formatDate(item.startTime) }}</span>
                                    </div>
                                    <div class="info-item">
                                        <span class="label">入住天数:</span>
                                        <span class="value">{{ item.days }} 晚</span>
                                    </div>
                                    <div class="info-item">
                                        <span class="label">房价:</span>
                                        <span class="value price-total">￥{{ item.room.category.price ? (item.room.category.price * item.days).toFixed(2) : '0.00' }}</span>
                                    </div>
                                    <div class="info-item">
                                        <span class="label">押金:</span>
                                        <span class="value deposit">￥{{ item.deposit ? item.deposit.toFixed(2) : '0.00' }}</span>
                                    </div>
                                    <div class="info-item">
                                        <span class="label">实付金额:</span>
                                        <span class="value total">￥{{ item.money ? item.money.toFixed(2) : '0.00' }}</span>
                                    </div>
                                    <div class="info-item" v-if="item.status == 3">
                                        <span class="label">退还押金:</span>
                                        <span class="value refund">￥{{ item.refundDeposit ? item.refundDeposit.toFixed(2) : '0.00' }}</span>
                                    </div>
                                </div>
                            </div>
                            
                            <div class="order-section user-info-section">
                                <div class="section-title">
                                    <i class="fas fa-user-circle"></i> 个人信息
                                </div>
                                <div class="section-content">
                                    <div class="info-item">
                                        <span class="label">姓名:</span>
                                        <span class="value">{{ item.member.name }}</span>
                                    </div>
                                    <div class="info-item">
                                        <span class="label">电话:</span>
                                        <span class="value">{{ item.member.phone }}</span>
                                    </div>
                                    <div class="info-item">
                                        <span class="label">性别:</span>
                                        <span class="value">
                                            <i :class="item.member.gender == 1 ? 'fas fa-mars' : 'fas fa-venus'" 
                                                :style="{ color: item.member.gender == 1 ? '#409EFF' : '#FF6B6B' }"></i>
                                            {{ item.member.gender == 1 ? '男' : '女' }}
                                        </span>
                                    </div>
                                </div>
                            </div>
                        </div>
                        
                        <div class="order-footer">
                            <div class="remark-container" v-if="item.remark">
                                <i class="fas fa-comment-alt"></i>
                                备注: <span>{{ item.remark }}</span>
                            </div>
                            <div class="actions-container">
                                <el-popconfirm 
                                    title="确定删除这条入住记录吗？" 
                                    @confirm="deleteCofirm(item)"
                                    confirm-button-text="确定"
                                    cancel-button-text="取消"
                                    icon="el-icon-warning">
                                    <template #reference>
                                        <el-button 
                                            type="danger" 
                                            size="small" 
                                            class="delete-button">
                                            <div class="button-content">
                                                <i class="fas fa-trash-alt"></i>
                                                <span>删除</span>
                                            </div>
                                        </el-button>
                                    </template>
                        </el-popconfirm>
                            </div>
                        </div>
                    </div>

            <!-- 分页器 -->
                    <div class="pagination-container">
                        <el-pagination 
                            background 
                            @size-change="handleSizeChange" 
                            @current-change="handleCurrentChange"
                            :current-page="pageNum" 
                            :page-sizes="[5, 10, 20]" 
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
import { findOrdersPageAPI, removeOrdersAPI } from '@/api/orders'

defineOptions({ name: 'MyOrder' })

const list = ref(null)
const listLoading = ref(true)
const pageSize = ref(10)
const pageNum = ref(1)
const total = ref(0)
const searchForm = ref({})

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
  // 跳到客房列表页选择房间
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
    const response = await findOrdersPageAPI(
      pageNum.value,
      pageSize.value,
      searchForm.value
    )
    list.value = response.data.records
    total.value = response.data.total
  } catch (error) {
    console.error('获取入住订单列表失败', error)
  } finally {
    listLoading.value = false
  }
}

async function deleteCofirm(row) {
  try {
    const res = await removeOrdersAPI(row.id)
    ElMessage[res.flag ? 'success' : 'error'](res.message)
    if (res.flag) fetchData()
  } catch (error) {
    console.error('删除入住订单失败', error)
  }
}

onMounted(() => {
  loadFontAwesome()
  fetchData()
})
</script>

<style lang="less" scoped>
.orders-container {
    width: 900px;
    margin: 30px auto;
    font-family: 'Arial', sans-serif;
}

.orders-card {
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

/* 新增的卡片式布局样式 */
.order-list-container {
    display: flex;
    flex-direction: column;
    gap: 20px;
}

.order-card {
    background-color: #fff;
    border-radius: 10px;
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
    overflow: hidden;
    transition: all 0.3s ease;
    border: 1px solid #eaeaea;
    
    &:hover {
        box-shadow: 0 6px 16px rgba(0, 0, 0, 0.12);
        transform: translateY(-2px);
    }
}

.order-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 15px 20px;
    background-color: #f8f9fa;
    border-bottom: 1px solid #eee;
    
    .order-id {
        font-weight: 600;
        color: #1E3A8A;
        display: flex;
        align-items: center;
        
        i {
            margin-right: 8px;
            font-size: 16px;
        }
        
        span {
            color: #1E3A8A;
            font-size: 16px;
        }
    }
    
    .status-container {
        .status-tag {
            padding: 6px 15px;
            border-radius: 20px;
            font-weight: 500;
            box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
            
            .tag-content {
                display: flex;
                align-items: center;
                justify-content: center;
                
                i {
                    margin-right: 6px;
                    font-size: 14px;
                    display: flex;
                    align-items: center;
                    height: 20px;
                }
                
                span {
                    line-height: 20px;
                    display: inline-block;
                }
            }
            
            :deep(.el-tag__content) {
                display: flex !important;
                align-items: center !important;
                justify-content: center !important;
                height: 20px;
            }
        }
    }
}

.order-content {
    display: flex;
    flex-wrap: wrap;
    padding: 15px;
    gap: 15px;
    
    @media (min-width: 768px) {
        flex-wrap: nowrap;
    }
}

.order-section {
    background-color: #f9f9f9;
    border-radius: 8px;
    padding: 15px;
    flex: 1;
    min-width: 200px;
    transition: all 0.3s ease;
    
    &:hover {
        background-color: #f0f4ff;
    }
    
    .section-title {
        font-weight: 600;
        color: #1E3A8A;
        margin-bottom: 12px;
        padding-bottom: 8px;
        border-bottom: 1px solid #e0e0e0;
        display: flex;
        align-items: center;
        
        i {
            margin-right: 8px;
            font-size: 16px;
        }
    }
    
    .section-content {
        display: flex;
        flex-direction: column;
        gap: 8px;
    }
    
    .info-item {
        display: flex;
        align-items: center;
        
        .label {
            color: #666;
            width: 80px;
            flex-shrink: 0;
        }
        
        .value {
            font-weight: 500;
            color: #333;
            
            &.price, &.price-total {
                color: #f56c6c;
                font-weight: 600;
            }
            
            &.deposit {
                color: #E6A23C;
                font-weight: 600;
            }
            
            &.total {
                color: #409EFF;
                font-weight: 700;
            }
            
            &.refund {
                color: #67C23A;
                font-weight: 700;
            }
        }
    }
}

.order-footer {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 15px 20px;
    background-color: #f8f9fa;
    border-top: 1px solid #eee;
    
    .remark-container {
        color: #666;
        font-size: 14px;
        display: flex;
        align-items: center;
        
        i {
            margin-right: 8px;
            color: #1E3A8A;
        }
        
        span {
            color: #333;
            font-style: italic;
        }
    }
    
    .actions-container {
        .delete-button {
            border-radius: 20px;
            display: flex;
            align-items: center;
            transition: all 0.3s ease;
            
            &:hover {
                transform: translateY(-2px);
                box-shadow: 0 4px 8px rgba(0, 0, 0, 0.15);
            }
            
            .button-content {
                display: flex;
                align-items: center;
                
                i {
                    margin-right: 5px;
                }
            }
        }
    }
}

.pagination-container {
    margin-top: 30px;
    display: flex;
    justify-content: center;
}

.status-icon {
    margin-right: 5px;
    
    &.booked {
        color: #E6A23C;
    }
    
    &.checkin {
        color: #67C23A;
    }
    
    &.checkout {
        color: #909399;
    }
}

@media (max-width: 768px) {
    .orders-container {
        width: 95%;
    }
    
    .order-content {
        flex-direction: column;
    }
}
</style>