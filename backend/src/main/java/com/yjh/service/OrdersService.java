package com.yjh.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.yjh.dao.MemberMapper;
import com.yjh.dao.OrdersMapper;
import com.yjh.entity.Member;
import com.yjh.entity.Orders;
import com.yjh.entity.Room;
import com.yjh.entity.model.Result;
import com.yjh.entity.model.StatusCode;
import com.yjh.entity.query.OrderQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.List;

@Service
@Transactional
public class OrdersService {

    @Autowired
    private OrdersMapper ordersMapper;
    @Autowired
    private RoomService roomService;
    @Autowired
    private MemberMapper memberMapper;

    public List<Orders> findAll() {
        return ordersMapper.selectList(null);
    }

    public Page<Orders> search(Page<Orders> page,Orders orders) {
        List<Orders> list = ordersMapper.selectPage(page, getEntityWrapper(orders));
        for (Orders o : list) {
            Room room = roomService.findById(o.getRoomId());
            Member member = memberMapper.selectById(o.getMemberId());
            o.setRoom(room);
            o.setMember(member);
        }
        return page.setRecords(list);
    }

    public List<Orders> search(Orders orders) {
        return ordersMapper.selectList(getEntityWrapper(orders));
    }

    //条件构造器
    private EntityWrapper getEntityWrapper(Orders orders){
        EntityWrapper entityWrapper = new EntityWrapper();
        if(null != orders){
            if(!StringUtils.isEmpty(orders.getRemark())){
                entityWrapper.like("remark",String.valueOf(orders.getRemark()));
            }
            if (!StringUtils.isEmpty(orders.getMemberId())) {
                entityWrapper.eq("member_id", String.valueOf(orders.getMemberId()));
            }
            if (!StringUtils.isEmpty(orders.getStatus())) {
                entityWrapper.eq("status", String.valueOf(orders.getStatus()));
            }
            entityWrapper.orderBy("id",false);
        }
        return entityWrapper;
    }

    public void modify(Orders orders) {
        ordersMapper.updateById(orders);
    }

    public Result add(OrderQuery orderQuery) {
        Orders orders = new Orders();
        //判断用户是否存在，不存在则创建
        List<Member> memberList = memberMapper.selectList(new EntityWrapper().eq("idcard",orderQuery.getIdcard()));
        if (memberList.size() != 0){
            orders.setMemberId(memberList.get(0).getId());
        }else{
            Member member = new Member();
            member.setCreateTime(new Date());
            member.setGender(orderQuery.getGender());
            member.setIdcard(orderQuery.getIdcard());
            member.setName(orderQuery.getName());
            member.setPhone(orderQuery.getPhone());
            member.setUsername(orderQuery.getPhone());
            member.setPassword("123456");//初始化密码
            memberMapper.insert(member);
            orders.setMemberId(member.getId());
        }
        Room room = roomService.findById(orderQuery.getRoomId());
        orders.setStartTime(new Date());
        orders.setDays(orderQuery.getDays());
        orders.setRoomId(orderQuery.getRoomId());
        orders.setStatus(2);
        java.math.BigDecimal deposit = room.getCategory().getDeposit() == null ? java.math.BigDecimal.ZERO : room.getCategory().getDeposit();
        java.math.BigDecimal totalPrice = room.getCategory().getPrice().multiply(new java.math.BigDecimal(orderQuery.getDays()));
        orders.setMoney(totalPrice.add(deposit));
        orders.setDeposit(deposit);
        ordersMapper.insert(orders);
        return new Result(true, StatusCode.OK,"操作成功");
    }

    public Orders findById(Integer id) {
        return ordersMapper.selectById(id);
    }

    public void removeById(Integer id) {
        ordersMapper.deleteById(id);
    }

    public Result checkOut(Integer id, String remark, BigDecimal refundDeposit) {
        Orders orders = ordersMapper.selectById(id);
        if (orders == null) {
            return new Result(false, StatusCode.ERROR, "订单不存在");
        }
        if (orders.getStatus() != null && orders.getStatus() == 3) {
            return new Result(false, StatusCode.ERROR, "该订单已退房");
        }
        Room room = roomService.findById(orders.getRoomId());
        if (room == null || room.getCategory() == null) {
            return new Result(false, StatusCode.ERROR, "房间信息异常");
        }
        BigDecimal pricePerDay = room.getCategory().getPrice() != null
                ? room.getCategory().getPrice() : BigDecimal.ZERO;

        Date now = new Date();
        int bookedDays = orders.getDays() == null ? 0 : orders.getDays();
        int stayedDays = calculateStayedDays(orders.getStartTime(), now, bookedDays);
        int unusedDays = Math.max(bookedDays - stayedDays, 0);
        BigDecimal refundRoomFee = pricePerDay.multiply(new BigDecimal(unusedDays));

        BigDecimal orderDeposit = orders.getDeposit() == null ? BigDecimal.ZERO : orders.getDeposit();
        if (refundDeposit == null) {
            refundDeposit = orderDeposit;
        }
        if (refundDeposit.compareTo(orderDeposit) > 0) {
            refundDeposit = orderDeposit;
        }
        if (refundDeposit.compareTo(BigDecimal.ZERO) < 0) {
            refundDeposit = BigDecimal.ZERO;
        }

        orders.setRemark(remark);
        orders.setStatus(3);
        orders.setCheckOutTime(now);
        orders.setRefundDeposit(refundDeposit);
        orders.setRefundMoney(refundRoomFee);
        ordersMapper.updateById(orders);
        String message = String.format("退房成功，已退还押金￥%s，房费￥%s",
                refundDeposit.setScale(2, RoundingMode.HALF_UP).toPlainString(),
                refundRoomFee.setScale(2, RoundingMode.HALF_UP).toPlainString());
        return new Result(true, StatusCode.OK, message);
    }

    private int calculateStayedDays(Date startTime, Date now, Integer bookedDays) {
        if (startTime == null || now == null || bookedDays == null || bookedDays <= 0) {
            return 0;
        }
        long diff = now.getTime() - startTime.getTime();
        if (diff <= 0) {
            return 1;
        }
        long millisPerDay = 1000L * 60 * 60 * 24;
        long ceilDays = (long) Math.ceil(diff / (double) millisPerDay);
        int stayed = (int) Math.max(1, ceilDays);
        return Math.min(stayed, bookedDays);
    }
}
