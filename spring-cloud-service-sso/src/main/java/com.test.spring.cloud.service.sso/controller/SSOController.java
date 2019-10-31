package com.test.spring.cloud.service.sso.controller;

import com.test.spring.cloud.common.domain.TbSysUser;
import com.test.spring.cloud.common.utils.CookieUtils;
import com.test.spring.cloud.service.sso.service.LoginService;
import com.test.spring.cloud.service.sso.service.consumer.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

/*单点登录，返回页面*/
@Controller
public class SSOController {

    @Autowired
    private LoginService loginService;

    @Autowired
    private RedisService redisService;

    @GetMapping("/")
    public String login(){
        return "index";
    }

    /*登陆页面*/
    @PostMapping("/login")
    public String login(String loginCode, String password,
                        @RequestParam(required = false) String url,     /*用于获取来访地址（并在登陆成功后跳转），设定默认可以没有（false）*/
                        HttpServletRequest request, HttpServletResponse response){
        TbSysUser tbSysUser = loginService.login(loginCode, password);

        /*登陆成功,则设置一个全局的token，存放loginCode供其他服务端调取*/
        if (tbSysUser != null) {
            String token = UUID.randomUUID().toString();
            String result = redisService.put(token, loginCode, 60 * 60 * 24);   /*在redis中存放token（内含loginCode）,结果为"ok"或者null*/

            /*判断是否存放成功(可能触发熔断),成功则进行存放并跳转*/
            if ("ok".equals(result)) {
                CookieUtils.setCookie(request,response,"token",token);  /*在cookie中存放token的值*/
                return "redirect:"+url;
            }

        }
        /*如果登陆错误,返回至login(并返回错误信息,暂时略)*/
        return "login";
    }
}
