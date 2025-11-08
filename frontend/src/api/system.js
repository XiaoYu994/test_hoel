import request from '@/utils/request'

//登录
export const loginAPI = data => {
    return request({
        url: `/system/login`,
        method: 'post',
        data
    })
}

//注册
export const registerAPI = data => {
    return request({
        url: `/system/register`,
        method: 'post',
        data
    })
}

//根据token获取用户信息
export const getUserInfoByTokenAPI = () => {
    return request({
        url: `/system/getUserInfoByToken`,
        method: 'get'
    })
}

//修改登录用户信息
export const modifyInfoAPI = data => {
    return request({
        url: '/system/modifyInfo',
        method: 'put',
        data
    })
}

//更新头像
export const updateAvatarAPI = data => {
    return request({
        url: '/system/updateAvatar',
        method: 'post',
        data
    })
}

// 获取统计数据 - 首页图表使用
export const getDashboardStatsAPI = () => {
    return request({
        url: '/system/dashboardStats',
        method: 'get'
    })
}

// 获取最近7天的订单数据
export const getWeeklyOrdersAPI = () => {
    return request({
        url: '/system/weeklyOrders',
        method: 'get'
    })
}

// 获取房型占比数据
export const getRoomCategoryStatsAPI = () => {
    return request({
        url: '/system/roomCategoryStats',
        method: 'get'
    })
}