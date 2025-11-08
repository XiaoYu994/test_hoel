<template>
    <div class="page-container">
        <div class="section">
            <div class="section-header">
                <h1 class="section-title">预约入住</h1>
                <div class="section-subtitle">为您提供舒适优雅的入住体验</div>
            </div>
            
            <div class="search-panel">
                <el-form :inline="true" :model="formInline" class="search-form">
                    <el-form-item label="房型">
                        <el-select v-model="formInline.categoryId" placeholder="选择房型" clearable class="custom-select">
                            <el-option v-for="item in categoryList" :label="item.categoryName" :value="item.id"
                                :key="item.id"></el-option>
                        </el-select>
                    </el-form-item>
                    <el-form-item label="状态">
                        <el-select v-model="formInline.canUse" placeholder="房间状态" clearable class="custom-select">
                            <el-option label="可预订" :value="true"></el-option>
                            <el-option label="已满房" :value="false"></el-option>
                        </el-select>
                    </el-form-item>
                    <el-form-item>
                        <el-button type="primary" @click="onSubmit" class="search-button">
                            <i class="fas fa-search"></i> 查询
                        </el-button>
                    </el-form-item>
                </el-form>
            </div>
            
            <div class="room-list">
                <div v-for="item in paginatedRoomList" :key="item.id" class="room-card"
                    @click="$router.push(`/front/roomDetails/${item.id}`)">
                    <div class="room-image-container">
                        <img :src="resolveImage(item.category.photo)" class="room-image">
                        <div class="room-status">
                            <el-tag size="medium" :type="item.canUse ? 'success' : 'danger'" effect="dark" class="status-tag">
                                <div class="status-content">
                                    <i :class="item.canUse ? 'fas fa-check-circle' : 'fas fa-ban'"></i>
                                    <span>{{ item.canUse ? '可预订' : '已满房' }}</span>
                                </div>
                            </el-tag>
                        </div>
                    </div>
                    <div class="room-info">
                        <h3 class="room-title">{{ item.category.categoryName }}-{{ item.roomNum }}</h3>
                        <div class="room-price">
                            <span class="price-label">每晚</span>
                            <span class="price-value">¥{{ item.category.price }}</span>
                        </div>
                        <div class="room-action">
                            <el-button type="text" class="details-button">查看详情 <i class="fas fa-arrow-right"></i></el-button>
                        </div>
                    </div>
                </div>

                <!-- 多写两个标签解决flex最后一行对齐问题 -->
                <div class="room-card-empty"></div>
                <div class="room-card-empty"></div>
                <div class="room-card-empty"></div>
            </div>
            
            <!-- 分页组件 -->
            <div class="pagination-container" v-if="roomList.length > 0">
                <el-pagination
                    background
                    @current-change="handleCurrentChange"
                    :current-page.sync="currentPage"
                    :page-size="pageSize"
                    layout="prev, pager, next, jumper"
                    :total="roomList.length"
                    :hide-on-single-page="false"
                >
                </el-pagination>
            </div>
            
            <!-- 无数据提示 -->
            <div class="empty-state" v-if="roomList.length === 0">
                <i class="fas fa-bed"></i>
                <p>暂无符合条件的房间</p>
            </div>
        </div>
    </div>
</template>

<script setup>
import { ref, computed, onMounted, getCurrentInstance } from 'vue'
import { useRoute } from 'vue-router'
import dayjs from 'dayjs'
import { findAllCategoryAPI } from '@/api/category'
import { findRoomListAPI } from '@/api/room'

defineOptions({ name: 'FrontCategory' })

// Element Plus v2+ 使用 disabled-date 属性禁选日期

const formInline = ref({
  startDate: dayjs().format('YYYY-MM-DD'),
  categoryId: null,
  canUse: null,
})

const categoryList = ref([])
const roomList = ref([])
const currentPage = ref(1)
const pageSize = ref(6)

const route = useRoute()

const { proxy } = getCurrentInstance()
function resolveImage(path) {
  return proxy?.$resolveImageUrl?.(path) || ''
}

const paginatedRoomList = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value
  const end = start + pageSize.value
  return roomList.value.slice(start, end)
})

function loadFontAwesome() {
  if (!document.querySelector('link[href*="font-awesome"]')) {
    const link = document.createElement('link')
    link.setAttribute('rel', 'stylesheet')
    link.setAttribute('href', 'https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.4/css/all.min.css')
    document.head.appendChild(link)
  }
}

function disabledDate(time) {
  return dayjs(time).isBefore(dayjs(), 'day')
}

async function getCategoryList() {
  const { data } = await findAllCategoryAPI()
  categoryList.value = data
}

async function getList() {
  const { data } = await findRoomListAPI(formInline.value)
  roomList.value = data
  currentPage.value = 1
}

function onSubmit() {
  getList()
}

function handleCurrentChange(val) {
  currentPage.value = val
  window.scrollTo({ top: 0, behavior: 'smooth' })
}

onMounted(() => {
  if (route.query?.categoryId) {
    formInline.value.categoryId = parseInt(route.query.categoryId)
  }
  getCategoryList()
  getList()
  loadFontAwesome()
})
</script>

<style lang="less" scoped>
.page-container {
    width: 1200px;
    margin: 30px auto;
    font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', 'PingFang SC', 'Hiragino Sans GB', 'Microsoft YaHei', 'Helvetica Neue', Helvetica, Arial, sans-serif;
    -webkit-font-smoothing: antialiased;
    -moz-osx-font-smoothing: grayscale;
    
    * {
        box-sizing: border-box;
    }
    
    // 确保所有文字都有合适的行高
    h1, h2, h3, h4, h5, h6, p, span, div {
        line-height: 1.4;
    }
}

.section {
    background-color: #fff;
    border-radius: 12px;
    box-shadow: 0 6px 20px rgba(0, 0, 0, 0.08);
    overflow: hidden;
    
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
        background-color: #f8f9fa;
        padding: 25px 30px;
        border-bottom: 1px solid #eaeaea;
        
        .search-form {
            display: flex;
            flex-wrap: wrap;
            align-items: center;
            justify-content: center;
            
            .el-form-item {
                margin-right: 20px;
                margin-bottom: 15px;
                
                .el-form-item__label {
                    color: #1E3A8A;
                    font-weight: 600;
                    padding-right: 10px;
                }
            }
            
            .search-button {
                background: linear-gradient(135deg, #1E3A8A, #274597);
                border: none;
                padding: 10px 22px;
                border-radius: 24px;
                transition: all 0.3s ease;
                box-shadow: 0 3px 8px rgba(30, 58, 138, 0.2);
                
                &:hover {
                    background: linear-gradient(135deg, #274597, #1E3A8A);
                    transform: translateY(-2px);
                    box-shadow: 0 5px 12px rgba(30, 58, 138, 0.3);
                }
                
                i {
                    margin-right: 6px;
                }
            }
        }
        
        .custom-date-picker, .custom-select {
            width: 180px;
        }
    }
    
    .room-list {
        display: grid;
        grid-template-columns: repeat(3, 1fr);
        grid-gap: 25px;
        padding: 30px;
        
        .room-card, .room-card-empty {
            width: 100%;
            border-radius: 10px;
            overflow: hidden;
            transition: all 0.3s ease;
            box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
            background-color: #fff;
            border: 1px solid #eaeaea;
            
            &:hover {
                transform: translateY(-5px);
                box-shadow: 0 8px 20px rgba(0, 0, 0, 0.12);
            }
            
            .room-image-container {
                position: relative;
                height: 200px;
                overflow: hidden;
                
                .room-image {
                    width: 100%;
                    height: 100%;
                    object-fit: cover;
                    transition: transform 0.6s ease;
                }
                
                &:hover .room-image {
                    transform: scale(1.05);
                }
                
                .room-status {
                    position: absolute;
                    top: 15px;
                    left: 15px;
                    
                    .status-tag {
                        font-weight: 600;
                        padding: 6px 12px;
                        border-radius: 20px;
                        box-shadow: 0 2px 5px rgba(0, 0, 0, 0.15);
                        
                        .status-content {
                            display: flex;
                            align-items: center;
                            justify-content: center;
                            gap: 5px;
                            
                            i {
                                font-size: 14px;
                                display: flex;
                                align-items: center;
                                flex-shrink: 0;
                            }
                            
                            span {
                                display: flex;
                                align-items: center;
                                line-height: 1.2;
                                font-size: 13px;
                                font-weight: 600;
                                white-space: nowrap;
                            }
                        }
                    }
                }
            }
            
            .room-info {
                padding: 16px;
                display: flex;
                flex-direction: column;
                height: 140px;
                position: relative;
                
                .room-title {
                    font-size: 18px;
                    font-weight: 600;
                    color: #1E3A8A;
                    margin: 0 0 10px;
                    line-height: 1.3;
                    display: flex;
                    align-items: center;
                    min-height: 24px;
                    white-space: nowrap;
                    overflow: hidden;
                    text-overflow: ellipsis;
                }
                
                .room-price {
                    display: flex;
                    align-items: center;
                    margin-bottom: 15px;
                    gap: 6px;
                    
                    .price-label {
                        font-size: 14px;
                        color: #777;
                        line-height: 1.4;
                        display: flex;
                        align-items: center;
                    }
                    
                    .price-value {
                        font-size: 24px;
                        font-weight: 700;
                        color: #ff5252;
                        line-height: 1.2;
                        display: flex;
                        align-items: center;
                    }
                }
                
                .room-action {
                    margin-top: auto;
                    text-align: right;
                    
                    .details-button {
                        background-color: rgba(30, 58, 138, 0.08);
                        padding: 8px 14px;
                        border-radius: 20px;
                        color: #1E3A8A;
                        font-weight: 600;
                        transition: all 0.3s ease;
                        
                        &:hover {
                            background-color: #1E3A8A;
                            color: #ffffff;
                            transform: translateY(-2px);
                            box-shadow: 0 4px 10px rgba(30, 58, 138, 0.2);
                            
                            i {
                                transform: translateX(3px);
                            }
                        }
                        
                        i {
                            margin-left: 5px;
                            transition: transform 0.3s ease;
                        }
                    }
                }
            }
        }
        
        .room-card-empty {
            height: 0;
            padding: 0;
            margin: 0;
            border: none;
            box-shadow: none;
            visibility: hidden;
        }
    }
    
    .pagination-container {
        display: flex;
        justify-content: center;
        padding: 0 0 30px;
    }
    
    .empty-state {
        padding: 60px 0;
        text-align: center;
        color: #909399;
        
        i {
            font-size: 60px;
            color: #DCDFE6;
            margin-bottom: 15px;
        }
        
        p {
            font-size: 16px;
            color: #909399;
        }
    }
}

// 针对Element UI组件的自定义样式
:deep(.el-select-dropdown__item.selected) {
    color: #1E3A8A;
    font-weight: 600;
}

:deep(.el-button--primary) {
    background-color: #1E3A8A;
    border-color: #1E3A8A;
    
    &:hover, &:focus {
        background-color: #274597;
        border-color: #274597;
    }
}

:deep(.el-tag--success) {
    background-color: rgba(103, 194, 58, 0.9);
    border: none;
}

:deep(.el-tag--danger) {
    background-color: rgba(245, 108, 108, 0.9);
    border: none;
}

:deep(.el-tag) {
    display: flex;
    align-items: center;
    justify-content: center;
    height: auto;
    min-height: 28px;
    line-height: 1.2;
    padding: 6px 12px;
    font-size: 13px;
    font-weight: 600;
    
    .el-tag__content {
        display: flex;
        align-items: center;
        line-height: 1.2;
    }
}

// 分页组件样式
:deep(.el-pagination) {
    .el-pagination__jump {
        color: #606266;
    }
    
    .btn-prev, .btn-next, .el-pager li {
        background-color: #fff;
        color: #606266;
        border: 1px solid #dcdfe6;
        
        &:hover {
            color: #1E3A8A;
        }
        
        &.is-active {
            background-color: #1E3A8A;
            color: #fff;
            border-color: #1E3A8A;
        }
    }
}

// 响应式字体优化
@media screen and (max-width: 1200px) {
    .page-container {
        width: 95%;
        margin: 20px auto;
    }
    
    .section .room-list {
        grid-template-columns: repeat(2, 1fr);
        grid-gap: 20px;
        padding: 20px;
    }
}

@media screen and (max-width: 768px) {
    .section .room-list {
        grid-template-columns: 1fr;
        grid-gap: 15px;
        padding: 15px;
        
        .room-card .room-info {
            .room-title {
                font-size: 16px;
                line-height: 1.3;
                min-height: 22px;
            }
            
            .room-price {
                .price-label {
                    font-size: 13px;
                }
                
                .price-value {
                    font-size: 20px;
                }
            }
        }
        
        .room-card .room-image-container .room-status .status-tag {
            .status-content {
                span {
                    font-size: 12px;
                }
                
                i {
                    font-size: 12px;
                }
            }
        }
    }
    
    .section .search-panel .search-form {
        flex-direction: column;
        align-items: stretch;
        
        .el-form-item {
            margin-right: 0;
            margin-bottom: 15px;
        }
        
        .custom-date-picker, .custom-select {
            width: 100%;
        }
    }
}
</style>