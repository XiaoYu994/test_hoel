<template>
    <div class="notice-container">
        <div class="notice-card">
            <div class="section-header">
                <h1 class="section-title">系统公告</h1>
                <div class="section-subtitle">了解酒店最新动态与服务信息</div>
            </div>
            
            <div class="search-panel">
                <el-form :inline="true" :model="searchForm" class="search-form">
                    <el-form-item label="公告标题">
                        <el-input 
                            placeholder="搜索公告标题关键词" 
                            v-model="searchForm.title"
                            clearable
                            class="search-input">
                            <template #prefix>
                                <i class="fas fa-search"></i>
                            </template>
                        </el-input>
                    </el-form-item>
                    <el-form-item>
                        <el-button 
                            type="primary" 
                            @click="handleSearch"
                            class="search-button">
                            <i class="fas fa-search"></i> 查询
                        </el-button>
                    </el-form-item>
                </el-form>
            </div>
            
            <div class="notice-content">
                <div v-if="listLoading" class="loading-container">
                    <i class="fas fa-spinner fa-spin loading-icon"></i>
                    <div class="loading-text">正在加载公告信息...</div>
                </div>
                
                <div v-else-if="list && list.length === 0" class="empty-container">
                    <i class="fas fa-clipboard-list empty-icon"></i>
                    <div class="empty-text">暂无公告信息</div>
                </div>
                
                <div v-else class="notice-list">
                    <div v-for="item in list" :key="item.id" class="notice-item" @click="$router.push(`/front/noticeDetails/${item.id}`)">
                        <div class="notice-left">
                            <div class="notice-icon">
                                <i class="fas fa-bullhorn"></i>
                            </div>
                        </div>
                        <div class="notice-right">
                            <h3 class="notice-title">{{ item.title }}</h3>
                            <div class="notice-meta">
                                <span class="notice-id">公告编号: {{ item.id }}</span>
                                <span class="notice-time">
                                    <i class="fas fa-clock"></i> {{ item.createTime }}
                                </span>
                            </div>
                            <div class="notice-action">
                                <el-button type="text" class="view-button">
                                    查看详情 <i class="fas fa-arrow-right"></i>
                                </el-button>
                            </div>
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
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { findNoticePageAPI } from '@/api/notice'

const list = ref([])
const listLoading = ref(true)
const pageSize = ref(10)
const pageNum = ref(1)
const total = ref(0)
const searchForm = ref({ title: '' })

const router = useRouter()

function loadFontAwesome() {
  if (!document.querySelector('link[href*="font-awesome"]')) {
    const link = document.createElement('link')
    link.setAttribute('rel', 'stylesheet')
    link.setAttribute('href', 'https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.4/css/all.min.css')
    document.head.appendChild(link)
  }
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
      searchForm.value
    )
    list.value = response.data.records
    total.value = response.data.total
  } catch (error) {
    console.error('获取系统公告失败', error)
    list.value = []
    total.value = 0
  } finally {
    listLoading.value = false
  }
}

onMounted(() => {
  fetchData()
  loadFontAwesome()
})
</script>

<style lang="less" scoped>
.notice-container {
    width: 1200px;
    margin: 30px auto;
    font-family: 'Arial', sans-serif;
}

.notice-card {
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
    background-color: #f8f9fa;
    padding: 20px 30px;
    border-bottom: 1px solid #eaeaea;
    
    .search-form {
        display: flex;
        align-items: center;
        justify-content: center;
        
        .el-form-item {
            margin-bottom: 0;
            margin-right: 20px;
            
            :deep(.el-form-item__label) {
                color: #1E3A8A;
                font-weight: 600;
                padding-right: 10px;
            }
        }
        
        .search-input {
            width: 300px;
            
            :deep(.el-input__inner) {
                border-radius: 20px;
                padding-left: 15px;
            }
        }
        
        .search-button {
            background: linear-gradient(135deg, #1E3A8A, #274597);
            border: none;
            padding: 10px 20px;
            border-radius: 20px;
            transition: all 0.3s ease;
            box-shadow: 0 3px 8px rgba(30, 58, 138, 0.2);
            
            &:hover {
                background: linear-gradient(135deg, #274597, #1E3A8A);
                transform: translateY(-2px);
                box-shadow: 0 5px 12px rgba(30, 58, 138, 0.3);
            }
            
            i {
                margin-right: 5px;
            }
        }
    }
}

.notice-content {
    padding: 30px;
    min-height: 400px;
}

.loading-container, .empty-container {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    height: 300px;
    color: #8c8c8c;
    
    .loading-icon, .empty-icon {
        font-size: 50px;
        margin-bottom: 20px;
        color: #1E3A8A;
        opacity: 0.6;
    }
    
    .loading-text, .empty-text {
        font-size: 16px;
    }
}

.notice-list {
    .notice-item {
        display: flex;
        padding: 20px;
        margin-bottom: 20px;
        border-radius: 10px;
        background-color: #fff;
        box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
        transition: all 0.3s ease;
        cursor: pointer;
        
        &:hover {
            transform: translateY(-5px);
            box-shadow: 0 8px 20px rgba(0, 0, 0, 0.1);
            
            .notice-title {
                color: #1E3A8A;
            }
            
            .view-button {
                color: #1E3A8A;
                
                i {
                    transform: translateX(3px);
                }
            }
        }
        
        &:last-child {
            margin-bottom: 0;
        }
        
        .notice-left {
            margin-right: 20px;
            
            .notice-icon {
                width: 50px;
                height: 50px;
                border-radius: 50%;
                background: linear-gradient(135deg, #1E3A8A, #274597);
                display: flex;
                align-items: center;
                justify-content: center;
                color: white;
                box-shadow: 0 4px 10px rgba(30, 58, 138, 0.2);
                
                i {
                    font-size: 20px;
                }
            }
        }
        
        .notice-right {
            flex: 1;
            
            .notice-title {
                font-size: 18px;
                font-weight: 600;
                color: #333;
                margin: 0 0 10px;
                transition: color 0.3s ease;
            }
            
            .notice-meta {
                display: flex;
                justify-content: space-between;
                margin-bottom: 15px;
                font-size: 14px;
                color: #8c8c8c;
                
                .notice-id {
                    background-color: #f1f5ff;
                    padding: 3px 8px;
                    border-radius: 4px;
                    color: #1E3A8A;
                }
                
                .notice-time {
                    display: flex;
                    align-items: center;
                    
                    i {
                        margin-right: 5px;
                    }
                }
            }
            
            .notice-action {
                text-align: right;
                
                .view-button {
                    color: #555;
                    font-weight: 600;
                    font-size: 14px;
                    padding: 0;
                    
                    i {
                        margin-left: 5px;
                        transition: transform 0.3s ease;
                    }
                }
            }
        }
    }
}

.pagination-container {
    margin-top: 30px;
    display: flex;
    justify-content: center;
    
        .custom-pagination {
        :deep(.el-pagination__total),
        :deep(.el-pagination__jump) {
            color: #555;
        }
        
        :deep(.el-pagination.is-background .el-pager li:not(.disabled).active) {
            background-color: #1E3A8A;
        }
        
        :deep(.el-pagination.is-background .el-pager li:not(.disabled):hover) {
            color: #1E3A8A;
        }
    }
}
</style>