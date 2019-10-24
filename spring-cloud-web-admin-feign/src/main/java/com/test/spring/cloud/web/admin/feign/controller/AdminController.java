package com.test.spring.cloud.web.admin.feign.controller;

import com.test.spring.cloud.web.admin.feign.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AdminController {

    @Autowired
    private AdminService adminService;

    @GetMapping("/hi")
    /*因为service层已经添加@RequestParam,此处注解可以省略*/
    public String sayHi(@RequestParam("message") String message){
        return adminService.sayHi(message);

    }
}
