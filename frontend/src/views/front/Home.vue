<template>
    <div class="page-container">
        <!-- 轮播图区域 -->
        <div class="carousel-section">
            <el-carousel height="520px" :interval="5000" indicator-position="outside" arrow="always" class="custom-carousel">
                <el-carousel-item>
                    <div class="carousel-item-wrapper">
                        <img src="@/assets/bg6.png" class="carousel-img" />
                    </div>
                </el-carousel-item>
                <el-carousel-item>
                    <div class="carousel-item-wrapper">
                        <img src="@/assets/bg7.png" class="carousel-img" />
                    </div>
                </el-carousel-item>
                <el-carousel-item>
                    <div class="carousel-item-wrapper">
                        <img src="@/assets/bg8.png" class="carousel-img" />
                    </div>
                </el-carousel-item>
            </el-carousel>
        </div>
        
        <!-- 客房展示区域 -->
        <div class="section room-showcase">
            <div class="section-header">
                <h2 class="section-title">客房展示</h2>
                <div class="section-divider"></div>
                <p class="section-subtitle">精心设计的各类房型，满足您的不同需求</p>
            </div>
            
            <div class="section-list">
                <div v-for="item in categoryList" :key="item.id" class="room-card" @click="$router.push(`/front/room?categoryId=${item.id}`)">
                    <div class="room-image-container">
                        <img :src="item.photo" class="room-image">
                        <div class="room-price-tag">￥{{ item.price }} / 晚</div>
                    </div>
                    <div class="room-info">
                        <h3 class="room-title">{{ item.categoryName }}</h3>
                        <p class="room-description">{{ getShortDescription(item.categoryName) }}</p>
                        <el-button type="primary" size="small" class="view-details-btn">
                            <i class="el-icon-view mr-5"></i> 查看详情
                        </el-button>
                    </div>
                </div>

                <!-- 占位元素，用于flex布局的最后一行对齐 -->
                <div class="room-card-empty"></div>
                <div class="room-card-empty"></div>
            </div>
        </div>

        <!-- 最新公告区域 -->
        <div class="section notice-section">
            <div class="section-header">
                <h2 class="section-title">最新公告</h2>
                <div class="section-divider"></div>
                <p class="section-subtitle">了解酒店的最新动态和服务信息</p>
            </div>
            
            <div class="notice-table-container">
                <el-table 
                    :data="noticeList" 
                    style="width: 100%" 
                    :row-class-name="tableRowClassName"
                    highlight-current-row 
                    @row-click="rowClick">
                    <el-table-column prop="id" label="公告编号" width="120" align="center"></el-table-column>
                    <el-table-column prop="title" label="公告标题" show-overflow-tooltip></el-table-column>
                    <el-table-column prop="createTime" label="发布时间" width="180" align="center"></el-table-column>
                    <el-table-column fixed="right" label="操作" width="100" align="center">
                        <template #default="scope">
                            <el-button @click.stop="$router.push(`/front/noticeDetails/${scope.row.id}`)" type="primary" size="mini">查看</el-button>
                        </template>
                    </el-table-column>
                </el-table>
            </div>
        </div>
    </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getTop5NoticeAPI } from '@/api/notice'
import { findAllCategoryAPI } from '@/api/category'

const router = useRouter()

const noticeList = ref([])
const categoryList = ref([])

function tableRowClassName() {
  return 'notice-table-row'
}

function rowClick(row) {
  router.push(`/front/noticeDetails/${row.id}`)
}

function getShortDescription(name) {
  const descriptions = {
    '豪华单人房': '宽敞舒适的单人房，配备豪华设施，提供高品质休息体验',
    '经济双人房': '经济实惠的双人住宿选择，基础设施齐全，性价比高',
    '总统套房': '顶级奢华的套房体验，宽敞空间配备顶级设施和服务',
    '经济单人房': '实惠单人间，舒适干净，基础设施齐全，经济实惠'
  }
  return descriptions[name] || '舒适典雅的客房，为您提供愉悦的住宿体验'
}

onMounted(async () => {
  const { data: notices } = await getTop5NoticeAPI()
  noticeList.value = notices
  const { data: categories } = await findAllCategoryAPI()
  categoryList.value = categories
})
</script>

<style lang="less" scoped>
// 页面容器
.page-container {
    width: 100%;
    overflow-x: hidden; /* 防止水平滚动条 */
    background-color: #f5f7fa;
}

// 工具类
.mr-5 {
    margin-right: 5px;
}

// 轮播图区域
.carousel-section {
    width: 100%;
    overflow: hidden;
    max-width: 1200px;
    margin: 0 auto;
    padding: 20px;
    
    .custom-carousel {
        border-radius: 20px;
        box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1);
        overflow: hidden;
        height: 520px;
        
        :deep(.el-carousel__indicators) {
            bottom: 25px;
            
            .el-carousel__indicator {
                .el-carousel__button {
                    width: 12px;
                    height: 12px;
                    border-radius: 50%;
                    background-color: rgba(255, 255, 255, 0.7);
                    transition: all 0.3s ease;
                    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.2);
                }
                
                &.is-active {
                    .el-carousel__button {
                        width: 30px;
                        border-radius: 6px;
                        background-color: #fff;
                    }
                }
            }
        }
        
        :deep(.el-carousel__arrow) {
            background-color: rgba(255, 255, 255, 0.7);
            width: 50px;
            height: 50px;
            border-radius: 25px;
            font-size: 22px;
            transition: all 0.3s ease;
            color: #1E3A8A;
            box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
            
            &:hover {
                background-color: rgba(255, 255, 255, 0.9);
                transform: scale(1.1);
            }
        }
        
        .carousel-item-wrapper {
            width: 100%;
            height: 100%;
            position: relative;
            overflow: hidden;
            
            .carousel-img {
                width: 100%;
                height: 100%;
                object-fit: cover;
                transition: transform 8s ease;
                filter: brightness(1.05) saturate(1.1);
            }
            
            &:hover .carousel-img {
                transform: scale(1.1);
            }
        }
    }
}

// 通用区域样式
.section {
    padding: 60px 20px;
    max-width: 1200px;
    margin: 0 auto;
    
    .section-header {
        text-align: center;
        margin-bottom: 40px;
        
        .section-title {
            font-size: 32px;
            font-weight: 700;
            color: #333;
            margin: 0 0 15px 0;
            position: relative;
            display: inline-block;
            
            &:after {
                content: "";
                position: absolute;
                bottom: -8px;
                left: 50%;
                width: 80px;
                height: 4px;
                background: linear-gradient(to right, #1E3A8A, #3B82F6);
                transform: translateX(-50%);
                border-radius: 2px;
            }
        }
        
        .section-divider {
            display: none; // 使用新的标题下划线样式
        }
        
        .section-subtitle {
            font-size: 16px;
            color: #666;
            max-width: 800px;
            margin: 20px auto 0;
        }
    }
}

// 房间展示区域
.room-showcase {
    background-color: white;
    border-radius: 16px;
    box-shadow: 0 5px 20px rgba(0, 0, 0, 0.05);
    margin: 20px auto;
    padding-bottom: 80px;
    
    .section-list {
        display: grid;
        grid-template-columns: repeat(auto-fill, minmax(350px, 1fr));
        gap: 30px;
        padding: 0 30px;
        
        .room-card, .room-card-empty {
            border-radius: 12px;
            overflow: hidden;
            box-shadow: 0 5px 15px rgba(0, 0, 0, 0.08);
            transition: all 0.3s ease;
            cursor: pointer;
            background: white;
            border: 1px solid #f0f0f0;
            height: 100%;
            display: flex;
            flex-direction: column;
            
            &:hover {
                transform: translateY(-5px);
                box-shadow: 0 15px 30px rgba(0, 0, 0, 0.12);
                
                .room-image {
                    transform: scale(1.05);
                }
            }
            
            .room-image-container {
                height: 220px;
                overflow: hidden;
                position: relative;
                
                .room-image {
                    width: 100%;
                    height: 100%;
                    object-fit: cover;
                    transition: transform 0.5s ease;
                }
                
                .room-price-tag {
                    position: absolute;
                    top: 20px;
                    right: 0;
                    background: linear-gradient(to right, #1E3A8A, #2563EB);
                    color: white;
                    padding: 8px 15px;
                    font-weight: 600;
                    border-radius: 4px 0 0 4px;
                    box-shadow: 0 4px 8px rgba(0, 0, 0, 0.15);
                    font-size: 14px;
                    line-height: 1.2;
                    display: flex;
                    align-items: center;
                    min-height: 32px;
                }
            }
            
            .room-info {
                padding: 25px;
                flex: 1;
                display: flex;
                flex-direction: column;
                justify-content: space-between;
                
                .room-title {
                    font-size: 20px;
                    font-weight: 600;
                    margin: 0 0 12px 0;
                    color: #1E3A8A;
                    line-height: 1.3;
                    display: flex;
                    align-items: center;
                    min-height: 26px;
                }
                
                .room-description {
                    color: #666;
                    margin: 0 0 20px 0;
                    font-size: 14px;
                    line-height: 1.6;
                    flex-grow: 1;
                    display: -webkit-box;
                    line-clamp: 3;
                    -webkit-line-clamp: 3;
                    -webkit-box-orient: vertical;
                    overflow: hidden;
                }
                
                .view-details-btn {
                    width: 100%;
                    background: linear-gradient(to right, #1E3A8A, #2563EB);
                    border: none;
                    padding: 12px 0;
                    font-weight: 500;
                    border-radius: 8px;
                    transition: all 0.3s ease;
                    margin-top: auto;
                    
                    &:hover {
                        transform: translateY(-2px);
                        box-shadow: 0 5px 15px rgba(37, 99, 235, 0.3);
                    }
                }
            }
        }
        
        .room-card-empty {
            height: 0;
            padding: 0;
            margin: 0;
            box-shadow: none;
            background: none;
            border: none;
            
            &:hover {
                transform: none;
                box-shadow: none;
            }
        }
    }
}

// 最新公告区域
.notice-section {
    padding: 60px 20px 80px;
    background-color: white;
    border-radius: 16px;
    box-shadow: 0 5px 20px rgba(0, 0, 0, 0.05);
    margin: 20px auto;
    
    .notice-table-container {
        padding: 0 20px;
        
        :deep(.el-table) {
            border-radius: 12px;
            overflow: hidden;
            box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
            
            .el-table__header-wrapper th {
                background-color: #f5f7fa;
                color: #333;
                font-weight: 600;
                padding: 16px 0;
                font-size: 15px;
            }
            
            .notice-table-row {
                cursor: pointer;
                transition: all 0.2s ease;
                
                &:hover > td {
                    background-color: #f0f5ff !important;
                }
            }
            
            .el-table__row td {
                padding: 16px 0;
                font-size: 14px;
            }
            
            :deep(.el-button--primary) {
                background: linear-gradient(to right, #1E3A8A, #2563EB);
                border: none;
                border-radius: 6px;
                box-shadow: 0 2px 6px rgba(37, 99, 235, 0.2);
                transition: all 0.3s ease;
                
                &:hover {
                    transform: translateY(-2px);
                    box-shadow: 0 5px 12px rgba(37, 99, 235, 0.3);
                }
            }
        }
    }
}

// 响应式设计
@media screen and (max-width: 768px) {
    .carousel-section {
        padding: 15px;
        
        .custom-carousel {
            height: 400px !important;
            border-radius: 15px;
            
            :deep(.el-carousel__arrow) {
                width: 40px;
                height: 40px;
                font-size: 18px;
            }
        }
    }
    
    .section {
        padding: 40px 15px;
    }
    
    .room-showcase .section-list {
        grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
        padding: 0 15px;
    }
}

@media screen and (max-width: 480px) {
    .carousel-section {
        padding: 10px;
        
        .custom-carousel {
            height: 300px !important;
            border-radius: 12px;
            
            :deep(.el-carousel__indicators) {
                bottom: 15px;
                
                .el-carousel__indicator {
                    .el-carousel__button {
                        width: 8px;
                        height: 8px;
                    }
                    
                    &.is-active {
                        .el-carousel__button {
                            width: 20px;
                        }
                    }
                }
            }
            
            :deep(.el-carousel__arrow) {
                width: 35px;
                height: 35px;
                font-size: 16px;
            }
        }
    }
    
    .section .section-header .section-title {
        font-size: 24px;
    }
    
    .room-showcase .section-list {
        grid-template-columns: 1fr;
    }
}
</style>