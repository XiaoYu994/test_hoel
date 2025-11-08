package com.yjh.controller;

import com.baomidou.mybatisplus.plugins.Page;
import com.yjh.entity.Admin;
import com.yjh.entity.model.Result;
import com.yjh.entity.model.StatusCode;
import com.yjh.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;





    /**
    * 根据条件分页查询
    * @param admin
    * @return
    */
    @GetMapping("search/{current}/{size}")
    public Result search(@PathVariable Integer current,@PathVariable Integer size ,Admin admin){
        Page<Admin> page = adminService.search(new Page<Admin>(current, size), admin);
        return new Result(true, StatusCode.OK,"查询成功",page);
    }

    /**
    * 新增
    * @param admin
    * @return
    */
    @PostMapping
    public Result add(@RequestBody Admin admin){
        adminService.add(admin);
        return new Result(true, StatusCode.OK,"新增成功");
    }

    /**
    * 修改
    * @param admin
    * @return
    */
    @PutMapping
    public Result modify(@RequestBody Admin admin){
        adminService.modify(admin);
        return new Result(true, StatusCode.OK,"修改成功");
    }

    /**
    * 根据id删除
    * @param id
    * @return
    */
    @DeleteMapping("{id}")
    public Result removeById(@PathVariable("id") Integer id){
        adminService.removeById(id);
        return new Result(true, StatusCode.OK,"删除成功");
    }

}