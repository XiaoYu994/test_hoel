package com.yjh.controller;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.yjh.dao.CategoryMapper;
import com.yjh.dao.MemberMapper;
import com.yjh.dao.OrdersMapper;
import com.yjh.dao.RoomMapper;
import com.yjh.entity.Admin;
import com.yjh.entity.Category;
import com.yjh.entity.Member;
import com.yjh.entity.Orders;
import com.yjh.entity.Room;
import com.yjh.entity.model.Result;
import com.yjh.entity.model.StatusCode;
import com.yjh.entity.query.LoginQuery;
import com.yjh.service.AdminService;
import com.yjh.service.MemberService;
import com.yjh.utils.JWTUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

@RestController
@RequestMapping("/system")
public class SystemController {

    @Autowired
    private AdminService adminService;
    @Autowired
    private MemberService memberService;
    @Autowired
    private RoomMapper roomMapper;
    @Autowired
    private OrdersMapper ordersMapper;
    @Autowired
    private MemberMapper memberMapper;
    @Autowired
    private CategoryMapper categoryMapper;

    /**
     * 登录
     * @return
     */
    @PostMapping("login")
    public Result login(@RequestBody LoginQuery query){
        if(query.getRole() == 1){
            return adminService.login(query);
        }
        return memberService.login(query);
    }

    /**
     * 注册
     * @return
     */
    @PostMapping("register")
    public Result register(@RequestBody Member member){
        return memberService.register(member);
    }

    /**
     * 根据token获取用户信息
     * @param request
     * @return
     */
    @GetMapping("getUserInfoByToken")
    public Result getUserInfoByToken(HttpServletRequest request){
        Long userId = JWTUtils.getUserId(request);
        Long roleId = JWTUtils.getRole(request);
        if(roleId == 1){
            Admin admin = adminService.findById(userId.intValue());
            return new Result(true, StatusCode.OK,"获取成功",admin);
        }
        if(roleId == 2){
            Member member = memberService.findById(userId.intValue());
            return new Result(true, StatusCode.OK,"获取成功",member);
        }
        return new Result(true, StatusCode.ERROR,"获取用户信息失败");
    }
    
    /**
     * 获取首页统计数据
     * @return
     */
    @GetMapping("dashboardStats")
    public Result getDashboardStats() {
        Map<String, Object> statsMap = new HashMap<>();
        
        // 统计总房间数
        int totalRooms = roomMapper.selectCount(new EntityWrapper<Room>());
        
        // 统计可用房间数（状态为1的房间）
        int availableRooms = roomMapper.selectCount(new EntityWrapper<Room>().eq("status", 1));
        
        // 统计今日入住订单数
        String today = DateFormatUtils.format(new Date(), "yyyy-MM-dd");
        int todayOrders = ordersMapper.selectCount(
            new EntityWrapper<Orders>()
                .like("start_time", today)
                .eq("status", 2) // 已入住状态
        );
        
        // 统计总会员数
        int totalMembers = memberMapper.selectCount(new EntityWrapper<Member>());
        
        // 统计订单状态分布
        int bookedOrders = ordersMapper.selectCount(new EntityWrapper<Orders>().eq("status", 1)); // 已预订
        int checkedInOrders = ordersMapper.selectCount(new EntityWrapper<Orders>().eq("status", 2)); // 已入住
        int checkedOutOrders = ordersMapper.selectCount(new EntityWrapper<Orders>().eq("status", 3)); // 已退房
        
        // 添加到返回数据中
        statsMap.put("totalRooms", totalRooms);
        statsMap.put("availableRooms", availableRooms);
        statsMap.put("todayOrders", todayOrders);
        statsMap.put("totalMembers", totalMembers);
        statsMap.put("orderStatus", new int[]{bookedOrders, checkedInOrders, checkedOutOrders});
        
        return new Result(true, StatusCode.OK, "获取统计数据成功", statsMap);
    }
    
    /**
     * 获取最近7天的订单数据
     * @return
     */
    @GetMapping("weeklyOrders")
    public Result getWeeklyOrders() {
        Map<String, Object> resultMap = new HashMap<>();
        List<String> dateList = new ArrayList<>();
        List<Integer> orderCountList = new ArrayList<>();
        
        try {
            // 获取最近7天的日期
            for (int i = 6; i >= 0; i--) {
                Date date = DateUtils.addDays(new Date(), -i);
                String dateStr = DateFormatUtils.format(date, "yyyy-MM-dd");
                dateList.add(DateFormatUtils.format(date, "MM-dd"));
                
                // 查询该日期的订单数
                int count = ordersMapper.selectCount(new EntityWrapper<Orders>().like("start_time", dateStr));
                orderCountList.add(count);
            }
            
            resultMap.put("dateList", dateList);
            resultMap.put("orderCountList", orderCountList);
            
            return new Result(true, StatusCode.OK, "获取周订单数据成功", resultMap);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, StatusCode.ERROR, "获取周订单数据失败");
        }
    }
    
    /**
     * 获取房型占比数据
     * @return
     */
    @GetMapping("roomCategoryStats")
    public Result getRoomCategoryStats() {
        try {
            List<Map<String, Object>> resultList = new ArrayList<>();
            
            // 查询所有房型
            List<Category> categories = categoryMapper.selectList(new EntityWrapper<Category>());
            
            for (Category category : categories) {
                Map<String, Object> item = new HashMap<>();
                // 查询该房型的房间数
                int roomCount = roomMapper.selectCount(new EntityWrapper<Room>().eq("category_id", category.getId()));
                
                item.put("name", category.getCategoryName());
                item.put("value", roomCount);
                
                resultList.add(item);
            }
            
            return new Result(true, StatusCode.OK, "获取房型占比数据成功", resultList);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, StatusCode.ERROR, "获取房型占比数据失败");
        }
    }
    
    /**
     * 修改登录用户信息
     * @param admin
     * @return
     */
    @PutMapping("modifyInfo")
    public Result modifyInfo(@RequestBody Admin admin) {
        adminService.modify(admin);
        return new Result(true, StatusCode.OK, "修改成功");
    }
    
    /**
     * 更新头像
     * @param admin
     * @return
     */
    @PostMapping("updateAvatar")
    public Result updateAvatar(@RequestBody Admin admin) {
        adminService.updateAvatar(admin);
        return new Result(true, StatusCode.OK, "更新头像成功");
    }
}
