<template>
    <div class="notice-detail-container">
        <div class="notice-detail-card">
            <div class="section-header">
                <h1 class="section-title">公告详情</h1>
                <div class="section-subtitle">查看酒店公告完整内容</div>
            </div>
            
            <div class="notice-content">
                <div v-if="!notice.id" class="loading-container">
                    <i class="fas fa-spinner fa-spin loading-icon"></i>
                    <div class="loading-text">正在加载公告详情...</div>
                </div>
                
                <div v-else>
                    <div class="notice-header">
                        <h2 class="notice-title">{{ notice.title }}</h2>
                        <div class="notice-meta">
                            <span class="notice-id">
                                <i class="fas fa-hashtag"></i> 公告编号: {{ notice.id }}
                            </span>
                            <span class="notice-time">
                                <i class="fas fa-clock"></i> 发布时间: {{ notice.createTime }}
                            </span>
                        </div>
                    </div>
                    
                    <div class="notice-divider"></div>
                    
                    <div class="notice-body">
                        <div class="notice-icon">
                            <i class="fas fa-bullhorn"></i>
                        </div>
                        <div class="notice-text">
                            {{ notice.content || '暂无内容' }}
                        </div>
                    </div>
                    
                    <div class="notice-footer">
                        <el-button @click="$router.push('/front/notice')" class="back-button">
                            <i class="fas fa-arrow-left"></i> 返回公告列表
                        </el-button>
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getNoticeByIdAPI } from '@/api/notice'

defineOptions({ name: 'FrontNoticeDetails' })

const notice = ref({})
const router = useRouter()
const route = useRoute()

function loadFontAwesome() {
  if (!document.querySelector('link[href*="font-awesome"]')) {
    const link = document.createElement('link')
    link.setAttribute('rel', 'stylesheet')
    link.setAttribute('href', 'https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.4/css/all.min.css')
    document.head.appendChild(link)
  }
}

onMounted(async () => {
  loadFontAwesome()
  try {
    const { data: n } = await getNoticeByIdAPI(route.params.id)
    notice.value = n
  } catch (error) {
    console.error('获取公告详情失败', error)
  }
})
</script>

<style lang="less" scoped>
.notice-detail-container {
    width: 1200px;
    margin: 30px auto;
    font-family: 'Arial', sans-serif;
}

.notice-detail-card {
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

.notice-content {
    padding: 30px;
    min-height: 400px;
}

.loading-container {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    height: 300px;
    color: #8c8c8c;
    
    .loading-icon {
        font-size: 50px;
        margin-bottom: 20px;
        color: #1E3A8A;
        opacity: 0.6;
    }
    
    .loading-text {
        font-size: 16px;
    }
}

.notice-header {
    margin-bottom: 25px;
    
    .notice-title {
        font-size: 26px;
        font-weight: 700;
        color: #1E3A8A;
        margin: 0 0 15px;
        text-align: center;
    }
    
    .notice-meta {
        display: flex;
        justify-content: center;
        flex-wrap: wrap;
        gap: 20px;
        
        .notice-id, .notice-time {
            display: flex;
            align-items: center;
            color: #777;
            font-size: 14px;
            
            i {
                margin-right: 5px;
                color: #1E3A8A;
            }
        }
    }
}

.notice-divider {
    height: 1px;
    background: linear-gradient(to right, transparent, #e0e0e0, transparent);
    margin: 25px 0;
}

.notice-body {
    position: relative;
    background-color: #f8f9fa;
    border-radius: 12px;
    padding: 30px;
    margin-bottom: 30px;
    
    .notice-icon {
        position: absolute;
        top: -20px;
        left: 50%;
        transform: translateX(-50%);
        width: 40px;
        height: 40px;
        border-radius: 50%;
        background: linear-gradient(135deg, #1E3A8A, #274597);
        display: flex;
        align-items: center;
        justify-content: center;
        color: white;
        box-shadow: 0 4px 10px rgba(30, 58, 138, 0.2);
        
        i {
            font-size: 16px;
        }
    }
    
    .notice-text {
        font-size: 16px;
        line-height: 1.8;
        color: #333;
        white-space: pre-line;
    }
}

.notice-footer {
    display: flex;
    justify-content: center;
    margin-top: 30px;
    
    .back-button {
        padding: 10px 25px;
        border-radius: 30px;
        background-color: #f1f5ff;
        color: #1E3A8A;
        border: 1px solid #dbe7ff;
        transition: all 0.3s ease;
        
        &:hover {
            background-color: #1E3A8A;
            color: white;
            transform: translateY(-3px);
            box-shadow: 0 5px 15px rgba(30, 58, 138, 0.2);
        }
        
        i {
            margin-right: 8px;
        }
    }
}
</style>