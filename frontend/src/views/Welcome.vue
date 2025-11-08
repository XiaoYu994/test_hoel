<template>
    <div class="dashboard-container">
        <!-- 标题区域 -->
        <div class="section-header">
            <div class="header-left">
                <div class="title-container">
                    <h1 class="section-title">仪表盘概览</h1>
                    <div class="title-decoration"></div>
                </div>
                <div class="section-subtitle">酒店系统运营数据一目了然</div>
            </div>
            <div class="header-right">
                <div class="date-display">
                    <i class="el-icon-date"></i>
                    <span>{{ currentDateTime }}</span>
                </div>
            </div>
        </div>
        
        <!-- 数据统计卡片 -->
        <div class="stat-cards-container">
            <div class="stat-card" :style="{ background: 'linear-gradient(135deg, #6ac6f8, #3988fd)' }">
                <div class="stat-icon">
                    <i class="el-icon-house"></i>
                </div>
                <div class="stat-info">
                    <div class="stat-value">{{ stats.totalRooms || 0 }}</div>
                    <div class="stat-label">总房间数</div>
                </div>
                <div class="stat-indicator">
                    <i class="el-icon-info"></i>
                </div>
            </div>
            
            <div class="stat-card" :style="{ background: 'linear-gradient(135deg, #67C23A, #85CE61)' }">
                <div class="stat-icon">
                    <i class="el-icon-s-home"></i>
                </div>
                <div class="stat-info">
                    <div class="stat-value">{{ stats.availableRooms || 0 }}</div>
                    <div class="stat-label">可用房间</div>
                </div>
                <div class="stat-indicator">
                    <i class="el-icon-info"></i>
                </div>
            </div>
            
            <div class="stat-card" :style="{ background: 'linear-gradient(135deg, #F56C6C, #FC8181)' }">
                <div class="stat-icon">
                    <i class="el-icon-tickets"></i>
                </div>
                <div class="stat-info">
                    <div class="stat-value">{{ stats.todayOrders || 0 }}</div>
                    <div class="stat-label">今日入住</div>
                </div>
                <div class="stat-indicator">
                    <i class="el-icon-info"></i>
                </div>
            </div>
            
            <div class="stat-card" :style="{ background: 'linear-gradient(135deg, #E6A23C, #F3CC62)' }">
                <div class="stat-icon">
                    <i class="el-icon-user"></i>
                </div>
                <div class="stat-info">
                    <div class="stat-value">{{ stats.totalMembers || 0 }}</div>
                    <div class="stat-label">用户总数</div>
                </div>
                <div class="stat-indicator">
                    <i class="el-icon-info"></i>
                </div>
            </div>
        </div>

        <!-- 图表区域 -->
        <div class="charts-section">
            <div class="section-divider">
                <span class="section-divider-text">数据分析</span>
            </div>
            <div class="charts-container">
                <!-- 最近7天订单数量趋势图 -->
                <div class="chart-card">
                    <div class="chart-header">
                        <div class="chart-title-group">
                            <i class="chart-icon el-icon-data-line"></i>
                            <h3 class="chart-title">最近7天订单趋势</h3>
                        </div>
                        <el-dropdown size="small" trigger="click">
                            <span class="el-dropdown-link">
                                <i class="el-icon-more"></i>
                            </span>
                            <template #dropdown>
                                <el-dropdown-menu>
                                <el-dropdown-item icon="el-icon-refresh">刷新数据</el-dropdown-item>
                                <el-dropdown-item icon="el-icon-download">导出数据</el-dropdown-item>
                                </el-dropdown-menu>
                            </template>
                        </el-dropdown>
                    </div>
                    <div class="chart-content">
                        <div id="weeklyOrdersChart" class="chart"></div>
                    </div>
                </div>
                
                <!-- 订单状态分布图 -->
                <div class="chart-card">
                    <div class="chart-header">
                        <div class="chart-title-group">
                            <i class="chart-icon el-icon-pie-chart"></i>
                            <h3 class="chart-title">订单状态分布</h3>
                        </div>
                        <el-dropdown size="small" trigger="click">
                            <span class="el-dropdown-link">
                                <i class="el-icon-more"></i>
                            </span>
                            <template #dropdown>
                                <el-dropdown-menu>
                                <el-dropdown-item icon="el-icon-refresh">刷新数据</el-dropdown-item>
                                <el-dropdown-item icon="el-icon-download">导出数据</el-dropdown-item>
                                </el-dropdown-menu>
                            </template>
                        </el-dropdown>
                    </div>
                    <div class="chart-content">
                        <div id="orderStatusChart" class="chart"></div>
                    </div>
                </div>
                
                <!-- 房型占比分布图 -->
                <div class="chart-card">
                    <div class="chart-header">
                        <div class="chart-title-group">
                            <i class="chart-icon el-icon-s-data"></i>
                            <h3 class="chart-title">房型占比分布</h3>
                        </div>
                        <el-dropdown size="small" trigger="click">
                            <span class="el-dropdown-link">
                                <i class="el-icon-more"></i>
                            </span>
                            <template #dropdown>
                                <el-dropdown-menu>
                                <el-dropdown-item icon="el-icon-refresh">刷新数据</el-dropdown-item>
                                <el-dropdown-item icon="el-icon-download">导出数据</el-dropdown-item>
                                </el-dropdown-menu>
                            </template>
                        </el-dropdown>
                    </div>
                    <div class="chart-content">
                        <div id="roomCategoryChart" class="chart"></div>
                    </div>
                </div>
            </div>
        </div>
      
    </div>
</template>

<script setup>
import { ref, reactive, onMounted, onBeforeUnmount, nextTick } from 'vue'
import * as echarts from 'echarts'
import { getDashboardStatsAPI, getWeeklyOrdersAPI, getRoomCategoryStatsAPI } from '@/api/system'

const stats = reactive({
  totalRooms: 0,
  availableRooms: 0,
  todayOrders: 0,
  totalMembers: 0,
  orderStatus: [0, 0, 0]
})

const weeklyOrders = reactive({ dateList: [], orderCountList: [] })
const roomCategoryStats = ref([])
const charts = reactive({ weeklyOrdersChart: null, orderStatusChart: null, roomCategoryChart: null })
const currentDateTime = ref('')
let timer = null

function updateCurrentTime() {
  const now = new Date()
  const y = now.getFullYear()
  const m = String(now.getMonth() + 1).padStart(2, '0')
  const d = String(now.getDate()).padStart(2, '0')
  const hh = String(now.getHours()).padStart(2, '0')
  const mm = String(now.getMinutes()).padStart(2, '0')
  const ss = String(now.getSeconds()).padStart(2, '0')
  currentDateTime.value = `${y}-${m}-${d} ${hh}:${mm}:${ss}`
}

async function fetchDashboardStats() {
  try {
    const response = await getDashboardStatsAPI()
    if (response.flag) {
      Object.assign(stats, response.data)
      nextTick(() => initOrderStatusChart())
    }
  } catch (error) {
    console.error('获取仪表盘统计数据失败', error)
  }
}

async function fetchWeeklyOrders() {
  try {
    const response = await getWeeklyOrdersAPI()
    if (response.flag) {
      weeklyOrders.dateList = response.data.dateList
      weeklyOrders.orderCountList = response.data.orderCountList
      nextTick(() => initWeeklyOrdersChart())
    }
  } catch (error) {
    console.error('获取周订单数据失败', error)
  }
}

async function fetchRoomCategoryStats() {
  try {
    const response = await getRoomCategoryStatsAPI()
    if (response.flag) {
      roomCategoryStats.value = response.data
      nextTick(() => initRoomCategoryChart())
    }
  } catch (error) {
    console.error('获取房型占比数据失败', error)
  }
}

function initWeeklyOrdersChart() {
  const chartDom = document.getElementById('weeklyOrdersChart')
  charts.weeklyOrdersChart = echarts.init(chartDom)
  const option = {
    tooltip: { trigger: 'axis', axisPointer: { type: 'shadow' } },
    grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
    xAxis: {
      type: 'category',
      data: weeklyOrders.dateList,
      axisTick: { alignWithLabel: true },
      axisLine: { lineStyle: { color: '#ddd' } },
      axisLabel: { color: '#666' }
    },
    yAxis: {
      type: 'value',
      minInterval: 1,
      axisLine: { show: false },
      axisLabel: { color: '#666' },
      splitLine: { lineStyle: { color: '#eee' } }
    },
    series: [{
      name: '订单数量',
      type: 'bar',
      barWidth: '60%',
      data: weeklyOrders.orderCountList,
      itemStyle: {
        color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
          { offset: 0, color: '#83bff6' },
          { offset: 0.5, color: '#188df0' },
          { offset: 1, color: '#188df0' }
        ]),
        borderRadius: [4, 4, 0, 0]
      },
      emphasis: {
        itemStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: '#66a6ff' },
            { offset: 0.7, color: '#0671f3' },
            { offset: 1, color: '#0671f3' }
          ])
        }
      }
    }]
  }
  charts.weeklyOrdersChart.setOption(option)
}

function initOrderStatusChart() {
  const chartDom = document.getElementById('orderStatusChart')
  charts.orderStatusChart = echarts.init(chartDom)
  const option = {
    tooltip: { trigger: 'item', formatter: '{a} <br/>{b}: {c} ({d}%)' },
    legend: { orient: 'vertical', left: 10, top: 'center', data: ['已预订', '已入住', '已退房'], textStyle: { color: '#666' }, icon: 'circle' },
    series: [{
      name: '订单状态', type: 'pie', radius: ['50%', '70%'], avoidLabelOverlap: false,
      label: { show: false, position: 'center' },
      emphasis: { label: { show: true, fontSize: '18', fontWeight: 'bold' } },
      labelLine: { show: false },
      data: [
        { value: stats.orderStatus[0], name: '已预订', itemStyle: { color: '#E6A23C' } },
        { value: stats.orderStatus[1], name: '已入住', itemStyle: { color: '#67C23A' } },
        { value: stats.orderStatus[2], name: '已退房', itemStyle: { color: '#909399' } }
      ]
    }]
  }
  charts.orderStatusChart.setOption(option)
}

function initRoomCategoryChart() {
  const chartDom = document.getElementById('roomCategoryChart')
  charts.roomCategoryChart = echarts.init(chartDom)
  const colors = ['#5470c6', '#91cc75', '#fac858', '#ee6666', '#73c0de', '#3ba272', '#fc8452', '#9a60b4', '#ea7ccc']
  const seriesData = roomCategoryStats.value.map((item, idx) => ({ value: item.value, name: item.name, itemStyle: { color: colors[idx % colors.length] } }))
  const option = {
    tooltip: { trigger: 'item', formatter: '{a} <br/>{b}: {c} ({d}%)' },
    legend: { orient: 'vertical', right: 10, top: 'center', data: roomCategoryStats.value.map(i => i.name), textStyle: { color: '#666' }, icon: 'circle' },
    series: [{ name: '房型分布', type: 'pie', radius: '70%', center: ['40%', '50%'], data: seriesData, emphasis: { itemStyle: { shadowBlur: 10, shadowOffsetX: 0, shadowColor: 'rgba(0,0,0,0.5)' } }, label: { show: false }, labelLine: { show: false } }]
  }
  charts.roomCategoryChart.setOption(option)
}

function resizeCharts() {
  Object.keys(charts).forEach(key => { charts[key]?.resize?.() })
}

onMounted(() => {
  fetchDashboardStats()
  fetchWeeklyOrders()
  fetchRoomCategoryStats()
  updateCurrentTime()
  timer = setInterval(updateCurrentTime, 1000)
  window.addEventListener('resize', resizeCharts)
})

onBeforeUnmount(() => {
  window.removeEventListener('resize', resizeCharts)
  clearInterval(timer)
  Object.keys(charts).forEach(key => { charts[key]?.dispose?.() })
})
</script>

<style lang="less" scoped>
.dashboard-container {
    padding: 24px;
    background-color: #f5f7fa;
    min-height: calc(100vh - 60px);
    
    /* 移除可能的滚动条 */
    overflow: hidden;
    
    .section-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-bottom: 30px;
        padding: 20px 30px;
        background: linear-gradient(135deg, #ffffff, #f8f9fc);
        border-radius: 16px;
        box-shadow: 0 6px 16px rgba(0, 0, 0, 0.05);
        position: relative;
        overflow: hidden;
        
        &:before {
            content: '';
            position: absolute;
            top: 0;
            left: 0;
            width: 6px;
            height: 100%;
            background: linear-gradient(to bottom, #3988fd, #6ac6f8);
        }
        
        .header-left {
            position: relative;
            
            .title-container {
                position: relative;
                display: inline-block;
                margin-bottom: 4px;
                
                .section-title {
                    font-size: 32px;
                    font-weight: 700;
                    color: #303133;
                    margin: 0;
                    line-height: 1.2;
                    position: relative;
                    display: inline-block;
                    background: linear-gradient(to right, #1E3A8A, #3B82F6);
                    background-clip: text;
                    -webkit-background-clip: text;
                    -webkit-text-fill-color: transparent;
                    letter-spacing: 1px;
                }
                
                .title-decoration {
                    position: absolute;
                    bottom: 0;
                    left: 0;
                    width: 40%;
                    height: 4px;
                    background: linear-gradient(to right, #3B82F6, rgba(59, 130, 246, 0.5));
                    border-radius: 2px;
                }
            }
            
            .section-subtitle {
                margin-top: 12px;
                color: #606266;
                font-size: 15px;
                font-weight: 500;
                letter-spacing: 0.5px;
            }
        }
        
        .header-right {
            .date-display {
                background-color: #fff;
                padding: 10px 16px;
                border-radius: 8px;
                box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05);
                display: flex;
                align-items: center;
                
                i {
                    color: #409EFF;
                    margin-right: 8px;
                    font-size: 16px;
                }
                
                span {
                    color: #606266;
                    font-size: 14px;
                }
            }
        }
    }
    
    .stat-cards-container {
        display: grid;
        grid-template-columns: repeat(4, 1fr);
        gap: 24px;
        margin-bottom: 32px;
        
        .stat-card {
            border-radius: 12px;
            padding: 24px;
            display: flex;
            align-items: center;
            color: white;
            box-shadow: 0 8px 16px rgba(0, 0, 0, 0.1);
            transition: transform 0.3s, box-shadow 0.3s;
            position: relative;
            overflow: hidden;
            
            &:hover {
                transform: translateY(-5px);
                box-shadow: 0 12px 24px rgba(0, 0, 0, 0.15);
                
                &:after {
                    transform: translateX(10px);
                }
            }
            
            &:after {
                content: '';
                position: absolute;
                top: 0;
                right: 0;
                width: 100px;
                height: 100%;
                background: linear-gradient(90deg, rgba(255,255,255,0) 0%, rgba(255,255,255,0.1) 100%);
                transform: translateX(60px);
                transition: transform 0.5s;
            }
            
            .stat-icon {
                font-size: 48px;
                margin-right: 24px;
                i {
                    filter: drop-shadow(0 4px 6px rgba(0, 0, 0, 0.1));
                }
            }
            
            .stat-info {
                flex: 1;
                .stat-value {
                    font-size: 36px;
                    font-weight: 700;
                    margin-bottom: 8px;
                    letter-spacing: 1px;
                }
                
                .stat-label {
                    font-size: 16px;
                    opacity: 0.9;
                    letter-spacing: 1px;
                }
            }
            
            .stat-indicator {
                position: absolute;
                top: 12px;
                right: 12px;
                opacity: 0.5;
                cursor: pointer;
                
                &:hover {
                    opacity: 1;
                }
            }
        }
    }
    
    .quick-actions {
        margin-bottom: 32px;
        
        .section-divider {
            text-align: center;
            border-bottom: 1px solid #EBEEF5;
            margin-bottom: 24px;
            position: relative;
            
            .section-divider-text {
                position: relative;
                top: 10px;
                padding: 0 20px;
                background-color: #f5f7fa;
                color: #606266;
                font-size: 16px;
                font-weight: 600;
            }
        }
        
        .action-buttons {
            display: flex;
            flex-wrap: wrap;
            gap: 16px;
            justify-content: center;
            
            .el-button {
                padding: 12px 24px;
                border-radius: 8px;
                font-weight: 500;
                
                i {
                    margin-right: 8px;
                }
            }
        }
    }
    
    .charts-section {
        margin-bottom: 32px;
        
        .section-divider {
            text-align: center;
            border-bottom: 1px solid #EBEEF5;
            margin-bottom: 24px;
            position: relative;
            
            .section-divider-text {
                position: relative;
                top: 10px;
                padding: 0 20px;
                background-color: #f5f7fa;
                color: #606266;
                font-size: 16px;
                font-weight: 600;
            }
        }
        
        .charts-container {
            display: grid;
            grid-template-columns: repeat(3, 1fr);
            gap: 24px;
            
            /* 确保图表容器没有滚动条 */
            overflow: hidden;
            
            .chart-card {
                background-color: #fff;
                border-radius: 12px;
                box-shadow: 0 4px 16px 0 rgba(0, 0, 0, 0.05);
                padding: 20px;
                height: 100%;
                transition: transform 0.3s, box-shadow 0.3s;
                
                &:hover {
                    transform: translateY(-3px);
                    box-shadow: 0 8px 24px 0 rgba(0, 0, 0, 0.08);
                }
                
                .chart-header {
                    display: flex;
                    align-items: center;
                    justify-content: space-between;
                    margin-bottom: 20px;
                    
                    .chart-title-group {
                        display: flex;
                        align-items: center;
                        
                        .chart-icon {
                            font-size: 20px;
                            color: #409EFF;
                            margin-right: 10px;
                        }
                        
                        .chart-title {
                            margin: 0;
                            font-size: 16px;
                            font-weight: 600;
                            color: #303133;
                        }
                    }
                    
                    .el-dropdown-link {
                        cursor: pointer;
                        color: #909399;
                        font-size: 18px;
                        
                        &:hover {
                            color: #409EFF;
                        }
                    }
                }
                
                .chart-content {
                    height: 320px;
                    
                    /* 确保图表容器没有滚动条 */
                    overflow: hidden;
                    
                    .chart {
                        width: 100%;
                        height: 100%;
                    }
                }
            }
        }
    }
    
    .system-info-section {
        margin-bottom: 24px;
        
        .section-divider {
            text-align: center;
            border-bottom: 1px solid #EBEEF5;
            margin-bottom: 24px;
            position: relative;
            
            .section-divider-text {
                position: relative;
                top: 10px;
                padding: 0 20px;
                background-color: #f5f7fa;
                color: #606266;
                font-size: 16px;
                font-weight: 600;
            }
        }
        
        .system-info {
            display: flex;
            justify-content: center;
            gap: 24px;
            
            .info-card {
                background-color: #fff;
                border-radius: 12px;
                padding: 20px;
                display: flex;
                align-items: center;
                box-shadow: 0 4px 16px 0 rgba(0, 0, 0, 0.05);
                min-width: 200px;
                transition: transform 0.3s, box-shadow 0.3s;
                
                &:hover {
                    transform: translateY(-3px);
                    box-shadow: 0 8px 24px 0 rgba(0, 0, 0, 0.08);
                }
                
                .info-icon {
                    background-color: #ecf5ff;
                    color: #409EFF;
                    width: 48px;
                    height: 48px;
                    border-radius: 12px;
                    display: flex;
                    align-items: center;
                    justify-content: center;
                    font-size: 24px;
                    margin-right: 16px;
                }
                
                .info-content {
                    .info-label {
                        color: #909399;
                        font-size: 14px;
                        margin-bottom: 4px;
                    }
                    
                    .info-value {
                        color: #303133;
                        font-size: 18px;
                        font-weight: 600;
                    }
                }
            }
        }
    }
}

/* 媒体查询，优化响应式布局 */
@media screen and (max-width: 1200px) {
    .dashboard-container {
        .stat-cards-container {
            grid-template-columns: repeat(2, 1fr);
        }
        
        .charts-container {
            grid-template-columns: repeat(2, 1fr);
        }
    }
}

@media screen and (max-width: 768px) {
    .dashboard-container {
        padding: 16px;
        
        .section-header {
            padding: 16px 20px;
            flex-direction: column;
            align-items: flex-start;
            
            .header-left {
                .title-container {
                    .section-title {
                        font-size: 24px;
                    }
                }
                
                .section-subtitle {
                    font-size: 14px;
                }
            }
            
            .header-right {
                margin-top: 16px;
                width: 100%;
                
                .date-display {
                    width: 100%;
                    justify-content: center;
                }
            }
        }
        
        .stat-cards-container {
            grid-template-columns: 1fr;
            gap: 16px;
        }
        
        .charts-container {
            grid-template-columns: 1fr;
            gap: 16px;
        }
        
        .system-info {
            flex-direction: column;
            
            .info-card {
                width: 100%;
            }
        }
    }
}
</style>