package com.test.spring.cloud.web.admin.feign.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AdminController {



    @GetMapping({"/","/login"})
    public String  login(){
        return "index";
    }










    /*===下方为测试用===*/
//    @GetMapping("/hi")
//    /*因为service层已经添加@RequestParam,此处注解可以省略*/
//    public String sayHi(@RequestParam("message") String message){
//        return adminService.sayHi(message);
//
//    }
}
