<template>
    <div class="room-details-container">
        <div class="room-details-card">
            <div class="section-header">
                <h1 class="section-title">客房详情</h1>
                <div class="section-subtitle">浏览完整信息，开启您的舒适住宿体验</div>
            </div>
            
            <div class="room-content">
                <div class="room-gallery">
                    <div class="main-image-container">
                        <img :src="category.photo" class="main-image" alt="房间图片">
                        <div class="room-status-badge" v-if="room.canUse">
                            <i class="fas fa-check-circle"></i> 可预订
                        </div>
                        <div class="room-status-badge unavailable" v-else>
                            <i class="fas fa-ban"></i> 已满房
                        </div>
                    </div>
                    <div class="room-features">
                        <div class="feature-item">
                            <i class="fas fa-users"></i>
                            <div class="feature-text">
                                <div class="feature-value">{{ category.liveNum }}</div>
                                <div class="feature-label">可住人数</div>
                            </div>
                        </div>
                        <div class="feature-item">
                            <i class="fas fa-bed"></i>
                            <div class="feature-text">
                                <div class="feature-value">{{ category.bedNum }}</div>
                                <div class="feature-label">床位数</div>
                            </div>
                        </div>
                        <div class="feature-item">
                            <i class="fas fa-expand-arrows-alt"></i>
                            <div class="feature-text">
                                <div class="feature-value">{{ category.area }}</div>
                                <div class="feature-label">平方米</div>
                            </div>
                        </div>
                    </div>
                </div>
                
                <div class="room-info">
                    <div class="room-header">
                        <div class="room-title">
                            <h2>{{ category.categoryName }} - {{ room.roomNum }}</h2>
                            <div class="room-price">
                                <span class="price-value">¥{{ category.price }}</span>
                                <span class="price-label">/ 晚</span>
                            </div>
                            <div class="room-price" v-if="category.deposit != null">
                                <span class="price-label">押金</span>
                                <span class="price-value" style="margin-left:6px">¥{{ category.deposit }}</span>
                            </div>
                        </div>
                    </div>
                    
                    <div class="room-description">
                        <h3>房间介绍</h3>
                        <p>{{ category.introduce || '这是一间精心设计的高品质客房，为您提供舒适宜人的住宿环境和完善的设施配套。无论是商务出行还是休闲度假，都能满足您的需求。' }}</p>
                    </div>
                    
                    <div class="booking-section">
                        <h3>预订信息</h3>
                        <p v-if="!token" class="login-reminder">
                            <i class="fas fa-info-circle"></i> 温馨提示：请<el-button type="text" @click="$router.push('/login')">登录</el-button>后再预订
                        </p>
                        
                        <el-form :model="form" class="booking-form">
                            <div class="form-row">
                                <el-form-item label="入住日期">
                                    <el-date-picker 
                                        :clearable="false" 
                                        :disabled-date="disabledDate" 
                                        v-model="form.startTime"
                                        type="date" 
                                        placeholder="选择入住日期" 
                                        value-format="YYYY-MM-DD" 
                                        format="YYYY-MM-DD"
                                        class="date-picker">
                                    </el-date-picker>
                                </el-form-item>
                                
                                <el-form-item label="入住天数">
                                    <el-input-number 
                                        v-model="form.days" 
                                        :min="1" 
                                        :max="30"
                                        placeholder="入住天数"
                                        class="days-input">
                                    </el-input-number>
                                </el-form-item>
                            </div>
                            
                            <div class="booking-summary" v-if="form.days">
                                <div class="summary-item">
                                    <span>房间类型:</span>
                                    <span>{{ category.categoryName }}</span>
                                </div>
                                <div class="summary-item">
                                    <span>入住日期:</span>
                                    <span>{{ form.startTime || '未选择' }}</span>
                                </div>
                                <div class="summary-item">
                                    <span>入住天数:</span>
                                    <span>{{ form.days }}晚</span>
                                </div>
                                <div class="summary-item">
                                    <span>押金:</span>
                                    <span class="price-value">¥{{ showDeposit() }}</span>
                                </div>
                                <div class="summary-item total">
                                    <span>总价:</span>
                                    <span class="total-price">¥{{ calculateTotal() }}</span>
                                </div>
                            </div>
                            
                            <el-form-item>
                                <el-button 
                                    @click="handleAppointment" 
                                    type="primary" 
                                    :disabled="!room.canUse || !token"
                                    class="booking-button">
                                    <i class="fas fa-calendar-check"></i> 支付押金并预订
                                </el-button>
                                <el-button @click="goBackToRoomList" class="return-button">
                                    <i class="fas fa-arrow-left"></i> 返回房间列表
                                </el-button>
                            </el-form-item>
                        </el-form>
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { storeToRefs } from 'pinia'
import { ElMessage } from 'element-plus'
import dayjs from 'dayjs'
import { useAuthStore } from '@/stores/auth'
import { findRoomByIdAPI } from '@/api/room'
import { addAppointmentAPI } from '@/api/appointment'

defineOptions({ name: 'FrontCategoryDetails' })

// 直接通过 disabled-date 禁选日期
const room = ref({})
const category = ref({})
const form = ref({
  roomId: null,
  startTime: dayjs().format('YYYY-MM-DD'),
  days: 1,
})

const authStore = useAuthStore()
const { token } = storeToRefs(authStore)
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

function calculateTotal() {
  if (form.value.days && category.value.price) {
    const nights = form.value.days
    const price = Number(category.value.price || 0)
    const deposit = Number(category.value.deposit || 0)
    return (nights * price + deposit).toFixed(2)
  }
  return '0.00'
}

function showDeposit() {
  return Number(category.value.deposit || 0).toFixed(2)
}

function disabledDate(time) {
  if (dayjs(time).isBefore(dayjs(), 'day')) return true
  return room.value.notUseDateList && room.value.notUseDateList.includes(dayjs(time).format('YYYY-MM-DD'))
}

async function handleAppointment() {
  if (!token.value) {
    ElMessage.warning('请先登录后再进行预订！')
    setTimeout(() => router.push('/login'), 1000)
    return
  }
  if (!form.value.startTime) {
    ElMessage.warning('请选择入住日期！')
    return
  }
  if (!form.value.days) {
    ElMessage.warning('请选择入住天数！')
    return
  }
  try {
    // 简化的“支付押金”流程：仅弹提示，真实项目应对接支付网关
    if (Number(category.value.deposit || 0) > 0) {
      ElMessage.success(`押金支付成功：¥${showDeposit()}`)
    }
    const res = await addAppointmentAPI({
      ...form.value,
      startTime: form.value.startTime + ' 00:00:00',
    })
    ElMessage[res.flag ? 'success' : 'error'](res.message)
    if (res.flag) {
      goBackToRoomList()
    }
  } catch (error) {
    console.error('预订失败:', error)
  }
}

// 计算用于跳转房型筛选的参数，尽量稳健
const categoryIdForRedirect = computed(() => {
  return room.value?.categoryId ?? category.value?.id ?? category.value?.categoryId ?? null
})

function goBackToRoomList() {
  const cid = categoryIdForRedirect.value
  if (cid) {
    router.push({ path: '/front/room', query: { categoryId: cid } })
  } else {
    router.push('/front/room')
  }
}

onMounted(async () => {
  const { data } = await findRoomByIdAPI(route.params.id)
  room.value = data
  category.value = data.category
  form.value.roomId = route.params.id
  loadFontAwesome()
})
</script>

<style lang="less" scoped>
.room-details-container {
    width: 1200px;
    margin: 30px auto;
    font-family: 'Arial', sans-serif;
}

.room-details-card {
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

.room-content {
    display: flex;
    padding: 0;
    
    @media (max-width: 1000px) {
        flex-direction: column;
    }
}

.room-gallery {
    width: 40%;
    padding: 30px;
    
    @media (max-width: 1000px) {
        width: 100%;
    }
    
    .main-image-container {
        position: relative;
        border-radius: 12px;
        overflow: hidden;
        box-shadow: 0 10px 20px rgba(0, 0, 0, 0.1);
        
        .main-image {
            width: 100%;
            height: 380px;
            object-fit: cover;
            transition: transform 0.6s ease;
            
            &:hover {
                transform: scale(1.03);
            }
        }
        
        .room-status-badge {
            position: absolute;
            top: 20px;
            left: 20px;
            background: rgba(103, 194, 58, 0.9);
            color: white;
            padding: 8px 16px;
            border-radius: 30px;
            font-weight: 600;
            display: flex;
            align-items: center;
            box-shadow: 0 3px 10px rgba(0, 0, 0, 0.1);
            
            i {
                margin-right: 5px;
            }
            
            &.unavailable {
                background: rgba(245, 108, 108, 0.9);
            }
        }
    }
    
    .room-features {
        display: flex;
        justify-content: space-between;
        margin-top: 25px;
        
        .feature-item {
            display: flex;
            flex-direction: column;
            align-items: center;
            background-color: #f8f9fa;
            border-radius: 10px;
            padding: 15px;
            width: 30%;
            box-shadow: 0 3px 10px rgba(0, 0, 0, 0.05);
            
            i {
                font-size: 24px;
                color: #1E3A8A;
                margin-bottom: 10px;
            }
            
            .feature-text {
                text-align: center;
                
                .feature-value {
                    font-size: 20px;
                    font-weight: 700;
                    color: #333;
                }
                
                .feature-label {
                    font-size: 12px;
                    color: #777;
                    margin-top: 2px;
                }
            }
        }
    }
}

.room-info {
    width: 60%;
    padding: 30px;
    border-left: 1px solid #f0f0f0;
    
    @media (max-width: 1000px) {
        width: 100%;
        border-left: none;
        border-top: 1px solid #f0f0f0;
    }
    
    .room-header {
        margin-bottom: 25px;
        
        .room-title {
            display: flex;
            justify-content: space-between;
            align-items: center;
            
            h2 {
                font-size: 26px;
                color: #1E3A8A;
                margin: 0;
                font-weight: 600;
            }
            
            .room-price {
                display: flex;
                align-items: baseline;
                
                .price-value {
                    font-size: 28px;
                    font-weight: 700;
                    color: #ff5252;
                }
                
                .price-label {
                    font-size: 16px;
                    color: #777;
                    margin-left: 5px;
                }
            }
        }
    }
    
    .room-description {
        margin-bottom: 25px;
        padding-bottom: 25px;
        border-bottom: 1px solid #f0f0f0;
        
        h3 {
            font-size: 18px;
            color: #333;
            margin-bottom: 15px;
            font-weight: 600;
            position: relative;
            padding-left: 15px;
            
            &:before {
                content: '';
                position: absolute;
                left: 0;
                top: 5px;
                height: 15px;
                width: 4px;
                background: #1E3A8A;
                border-radius: 2px;
            }
        }
        
        p {
            font-size: 15px;
            line-height: 1.6;
            color: #555;
            margin: 0;
        }
    }
    
    .booking-section {
        h3 {
            font-size: 18px;
            color: #333;
            margin-bottom: 15px;
            font-weight: 600;
            position: relative;
            padding-left: 15px;
            
            &:before {
                content: '';
                position: absolute;
                left: 0;
                top: 5px;
                height: 15px;
                width: 4px;
                background: #1E3A8A;
                border-radius: 2px;
            }
        }
        
        .login-reminder {
            background-color: #fff8e6;
            border-left: 4px solid #FFD700;
            padding: 12px 15px;
            border-radius: 4px;
            margin-bottom: 20px;
            display: flex;
            align-items: center;
            
            i {
                color: #f0ad4e;
                margin-right: 8px;
                font-size: 16px;
            }
            
            .el-button--text {
                color: #1E3A8A;
                font-weight: 600;
                padding: 0 5px;
            }
        }
        
        .booking-form {
            .form-row {
                display: flex;
                flex-wrap: wrap;
                gap: 20px;
                margin-bottom: 20px;
                
                .el-form-item {
                    margin-bottom: 0;
                    
                    :deep(.el-form-item__label) {
                        color: #1E3A8A;
                        font-weight: 600;
                    }
                }
                
                .date-picker, .days-input {
                    width: 200px;
                }
            }
            
            .booking-summary {
                background-color: #f8f9fa;
                border-radius: 10px;
                padding: 20px;
                margin-bottom: 20px;
                
                .summary-item {
                    display: flex;
                    justify-content: space-between;
                    margin-bottom: 10px;
                    font-size: 14px;
                    
                    &:last-child {
                        margin-bottom: 0;
                    }
                    
                    &.total {
                        margin-top: 15px;
                        padding-top: 15px;
                        border-top: 1px dashed #ddd;
                        font-weight: 600;
                        
                        .total-price {
                            font-size: 20px;
                            color: #ff5252;
                        }
                    }
                }
            }
            
            .booking-button {
                background: linear-gradient(135deg, #1E3A8A, #274597);
                border: none;
                padding: 12px 24px;
                font-size: 16px;
                border-radius: 30px;
                font-weight: 600;
                box-shadow: 0 4px 10px rgba(30, 58, 138, 0.2);
                transition: all 0.3s ease;
                
                &:hover:not(:disabled) {
                    transform: translateY(-2px);
                    box-shadow: 0 6px 15px rgba(30, 58, 138, 0.3);
                }
                
                &:disabled {
                    opacity: 0.6;
                    cursor: not-allowed;
                }
                
                i {
                    margin-right: 8px;
                }
            }
            
            .return-button {
                margin-left: 15px;
                
                i {
                    margin-right: 5px;
                }
            }
        }
    }
}
</style>