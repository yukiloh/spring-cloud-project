package com.test.spring.cloud.web.backend.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {

    /*用于聚合所有模块的主页面*/
    @GetMapping("/")
    public String main(){
        return "main";
    }
}
