package com.yjh.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.yjh.dao.*;
import com.yjh.entity.*;
import com.yjh.entity.model.Result;
import com.yjh.entity.model.StatusCode;
import com.yjh.entity.query.RoomQuery;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class AppointmentService {

    @Autowired
    private AppointmentMapper appointmentMapper;
    @Autowired
    private CategoryMapper categoryMapper;
    @Autowired
    private MemberMapper memberMapper;
    @Autowired
    private RoomService roomService;
    @Autowired
    private RoomMapper roomMapper;
    @Autowired
    private OrdersMapper ordersMapper;
    @Autowired
    private CategoryService categoryService;

    public List<Appointment> findAll() {
        return appointmentMapper.selectList(null);
    }

    public Page<Appointment> search(Page<Appointment> page, Appointment appointment) {
        List<Appointment> list = appointmentMapper.selectPage(page, getEntityWrapper(appointment));
        for (Appointment a : list) {
            Room room = roomService.findById(a.getRoomId());
            a.setRoom(room);
            Member member = memberMapper.selectById(a.getMemberId());
            a.setMember(member);
        }
        return page.setRecords(list);
    }

    public List<Appointment> search(Appointment appointment) {
        return appointmentMapper.selectList(getEntityWrapper(appointment));
    }

    //条件构造器
    private EntityWrapper getEntityWrapper(Appointment appointment) {
        EntityWrapper entityWrapper = new EntityWrapper();
        if (null != appointment) {
            if (!StringUtils.isEmpty(appointment.getRemark())) {
                entityWrapper.like("remark", String.valueOf(appointment.getRemark()));
            }
            if (!StringUtils.isEmpty(appointment.getMemberId())) {
                entityWrapper.eq("member_id", String.valueOf(appointment.getMemberId()));
            }
            if (!StringUtils.isEmpty(appointment.getStatus())) {
                entityWrapper.eq("status", String.valueOf(appointment.getStatus()));
            }
            entityWrapper.orderBy("id",false);
        }
        return entityWrapper;
    }

    public void modify(Appointment appointment) {
        appointmentMapper.updateById(appointment);
    }

    public Result add(Appointment appointment) {
        // 判断是否存在与当前预约时间区间重叠的【同一用户、同一房间】待确认预约
        List<Appointment> pendingList = appointmentMapper.selectList(new EntityWrapper<Appointment>()
                .eq("member_id", appointment.getMemberId())
                .eq("room_id", appointment.getRoomId())
                .eq("status", 1));
        if (pendingList != null && !pendingList.isEmpty()) {
            try {
                String newStartStr = DateFormatUtils.format(appointment.getStartTime(), "yyyy-MM-dd");
                Date newStart = DateUtils.parseDate(newStartStr, "yyyy-MM-dd");
                Date newEndInclusive = DateUtils.addDays(newStart, appointment.getDays() - 1);

                for (Appointment exist : pendingList) {
                    String exStartStr = DateFormatUtils.format(exist.getStartTime(), "yyyy-MM-dd");
                    Date exStart = DateUtils.parseDate(exStartStr, "yyyy-MM-dd");
                    Date exEndInclusive = DateUtils.addDays(exStart, exist.getDays() - 1);
                    boolean overlapped = !(newEndInclusive.before(exStart) || newStart.after(exEndInclusive));
                    if (overlapped) {
                        return new Result(false, StatusCode.ERROR, "请勿重复预订！");
                    }
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        //判断是否已预约或者已入住这个房间
//        ArrayList<Integer> statusList = new ArrayList<>();
//        statusList.add(1);//已预订
//        statusList.add(2);//已入住
//        List<Orders> ordersList = ordersMapper.selectList(new EntityWrapper()
//                .eq("room_id", appointment.getRoomId())
//                .in("status", statusList));
//        if(ordersList.size() != 0){
//            return new Result(false, StatusCode.ERROR, "预订失败,房间已满！");
//        }
        //插入预约表
        Room r = roomService.findById(appointment.getRoomId());
        // 金额字段保持为"每晚房价"，押金仅用于展示/备注
        appointment.setMoney(r.getCategory().getPrice());
        java.math.BigDecimal deposit = r.getCategory().getDeposit() == null ? java.math.BigDecimal.ZERO : r.getCategory().getDeposit();
        appointment.setDeposit(deposit);
        appointment.setStatus(1);//1-待确认 2-预约成功 3-已取消
        appointmentMapper.insert(appointment);
        return new Result(true, StatusCode.OK, "操作成功,等待酒店确认！");
    }

    public Appointment findById(Integer id) {
        return appointmentMapper.selectById(id);
    }

    public void removeById(Integer id) {
        appointmentMapper.deleteById(id);
    }

    public List<Room> findRoomList(RoomQuery roomQuery) {
        EntityWrapper wrapper = new EntityWrapper();
        wrapper.eq("status",1);
        if(!StringUtils.isEmpty(roomQuery.getCategoryId())){
            wrapper.eq("category_id",roomQuery.getCategoryId());
        }
        List<Room> list = roomMapper.selectList(wrapper);
        for (Room r : list) {
            //查询房型
            Category category = categoryService.findById(r.getCategoryId());
            r.setCategory(category);
            //1.根据当前年月日和房间id判断是否入住，入住则不可预约和入住 2.根据当前年月日和房间id判断是否预约，预约成功则不可预约和入住
            ArrayList<Integer> statusList = new ArrayList<>();
            statusList.add(1);//已预订
            statusList.add(2);//已入住
            List<Orders> ordersList = ordersMapper.selectList(new EntityWrapper()
                    .eq("room_id", r.getId())
                    .in("status", statusList));
            if(ordersList.size() == 0){
                r.setCanUse(true);
                continue;
            }
            // 根据查询的入住日期，与该房间所有(已预订/已入住)订单逐一比对，只要查询日期不在任一订单的入住区间中，则可用
            boolean available = true;
            for (Orders o : ordersList) {
                String startDate = DateFormatUtils.format(o.getStartTime(),"yyyy-MM-dd");
                if(!canAppointment(roomQuery.getStartDate(), startDate, o.getDays())){
                    available = false;
                    break;
                }
            }
            r.setCanUse(available);
        }
        //过滤
        if(!StringUtils.isEmpty(roomQuery.getCanUse())){
            List<Room> rooms = list.stream().filter(f -> f.getCanUse() == roomQuery.getCanUse()).collect(Collectors.toList());
            return rooms;
        }
        return list;
    }

    //判断是否可预约
    public boolean canAppointment(String currentDate,String startDate,Integer days)  {
        // 判断给定的 currentDate 是否在 [startDate, startDate+days-1] 区间内
        try {
            Date current = DateUtils.parseDate(currentDate, "yyyy-MM-dd");
            Date start = DateUtils.parseDate(startDate, "yyyy-MM-dd");
            Date endInclusive = DateUtils.addDays(start, days - 1);
            // 如果 current 在区间内，则不可预约
            boolean isWithin = !current.before(start) && !current.after(endInclusive);
            return !isWithin;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return true;
    }

    public Result confirm(Integer appointmentId) {
        Appointment appointment = appointmentMapper.selectById(appointmentId);
        // 仅当存在与预约区间重叠的订单时才视为被占用
        ArrayList<Integer> statusList = new ArrayList<>();
        statusList.add(1);//已预订
        statusList.add(2);//已入住
        List<Orders> ordersList = ordersMapper.selectList(new EntityWrapper()
                .eq("room_id", appointment.getRoomId())
                .in("status", statusList));

        try {
            String apptStartStr = DateFormatUtils.format(appointment.getStartTime(), "yyyy-MM-dd");
            Date apptStart = DateUtils.parseDate(apptStartStr, "yyyy-MM-dd");
            Date apptEndInclusive = DateUtils.addDays(apptStart, appointment.getDays() - 1);

            for (Orders o : ordersList) {
                String orderStartStr = DateFormatUtils.format(o.getStartTime(), "yyyy-MM-dd");
                Date orderStart = DateUtils.parseDate(orderStartStr, "yyyy-MM-dd");
                Date orderEndInclusive = DateUtils.addDays(orderStart, o.getDays() - 1);
                boolean overlapped = !(apptEndInclusive.before(orderStart) || apptStart.after(orderEndInclusive));
                if (overlapped) {
                    return new Result(false, StatusCode.ERROR, "该房间在所选日期已被占用，请选择其它日期或房间！");
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        //修改预订状态，预订成功
        appointment.setStatus(2);
        appointmentMapper.updateById(appointment);
        //生成入住订单，状态为已预订
        Orders order = new Orders();
        order.setMemberId(appointment.getMemberId());
        order.setRoomId(appointment.getRoomId());
        order.setDays(appointment.getDays());
        order.setStartTime(appointment.getStartTime());
        java.math.BigDecimal totalPrice = appointment.getMoney().multiply(new java.math.BigDecimal(appointment.getDays()));
        java.math.BigDecimal deposit = appointment.getDeposit() == null ? java.math.BigDecimal.ZERO : appointment.getDeposit();
        order.setMoney(totalPrice.add(deposit));
        order.setDeposit(deposit);
        order.setStatus(1);
        ordersMapper.insert(order);
        return new Result(true, StatusCode.OK,"操作成功！");
    }
}
