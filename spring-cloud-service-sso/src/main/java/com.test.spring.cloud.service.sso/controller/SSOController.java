package com.test.spring.cloud.service.sso.controller;

import com.test.spring.cloud.common.domain.TbSysUser;
import com.test.spring.cloud.service.sso.service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/*单点登录，返回页面*/
@Controller
public class SSOController {

    @Autowired
    private LoginService loginService;

    @GetMapping("/")
    public String login(){
        return "login";
    }

    @ResponseBody
    @PostMapping("/login")
    public String login(String loginCode,String password){
        TbSysUser tbSysUser = loginService.login(loginCode, password);
        System.out.println(tbSysUser);
        return "success";
    }
}
