package com.test.spring.cloud.web.admin.feign.controller;

import com.test.spring.cloud.common.domain.Message;
import com.test.spring.cloud.common.domain.TbSysUser;
import com.test.spring.cloud.common.utils.MapperUtils;
import com.test.spring.cloud.web.admin.feign.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class AdminController {

    @Autowired
    private AdminService adminService;

    @GetMapping({"/adminList"})
    public String adminList() {
        return "adminList";
    }

    /*获取管理员列表*/
    @ResponseBody
    @PostMapping({"/admins"})
    public String admins(){
        String admin = adminService.page(1, 10, null);
        return admin;
    }


    /*用于提供admin页面的消息message*/
    @ResponseBody
    @PostMapping({"/{uuid}/msg"})
    public Message recentMessage(@PathVariable String uuid){
//        System.out.println(uuid);   /*模拟获取uuid*/
        Message message = new Message();
        message.setMessage("hello");/*模拟获取json数据*/
        return message;
    }



    /*测试用*/
//    @GetMapping({"/"})
//    public String  login(){
//        return "index";
//    }

}
