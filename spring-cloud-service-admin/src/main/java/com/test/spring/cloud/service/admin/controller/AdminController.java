package com.test.spring.cloud.service.admin.controller;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/*用于测试eureka客户端*/
@RestController
public class AdminController {








    /*===下方为测试用===*/
    @Value("${server.port}")
    private String port;

    @GetMapping("/hi")
    public String sayHi(String message){
        /*通过占位符的方式输出一句话*/
        return String.format("your massage is %s ,port is %s",message,port);
    }

}
