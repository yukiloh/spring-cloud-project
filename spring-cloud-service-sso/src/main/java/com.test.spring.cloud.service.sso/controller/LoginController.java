package com.test.spring.cloud.service.sso.controller;

import com.test.spring.cloud.common.domain.TbSysUser;
import com.test.spring.cloud.common.utils.CookieUtils;
import com.test.spring.cloud.common.utils.MapperUtils;
import com.test.spring.cloud.service.sso.service.LoginService;
import com.test.spring.cloud.service.sso.service.consumer.RedisService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

/*单点登录，返回页面*/
@Controller
public class LoginController {

    @Autowired
    private LoginService loginService;

    @Autowired
    private RedisService redisService;

    @GetMapping("/")
    public String index(){
        return "index";
    }


    @GetMapping("/login")
    public String login(@RequestParam(required = false) String url,HttpServletRequest request,Model model){

        /*通过token检查是否已经登陆*/
        String token = CookieUtils.getCookieValue(request, "token");
        /*检查token是否为空*/
        if (StringUtils.isNotBlank(token)) {
            /*获取的loginCode*/
            String loginCode = redisService.get(token);
            /*检查loginCode是否为空*/
            if (StringUtils.isNotBlank(loginCode)){
                /*获取user的json数据*/
                String json = redisService.get(loginCode);
                /*检查json是否为空*/
                if (StringUtils.isNotBlank(json)){
                    try {
                        /*最终获取到user*/
                        TbSysUser tbSysUser = MapperUtils.json2pojo(json, TbSysUser.class);
                        /*检查是否携带访问地址*/
                        if (StringUtils.isNotBlank(url)) {
                            return "redirect:"+url; /*原路返回*/
                        }

                        /*如果没有登陆信息则显示 登陆成功，此处建议模拟,向登录页面返回welcome和user信息*/
                        model.addAttribute("message","welcome");
                        model.addAttribute("tbSysUser",tbSysUser);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            }
        }
        if (StringUtils.isNotBlank(url)) {
            model.addAttribute("url",url);
        }

        return "login";
    }

    /*登陆页面*/
    @PostMapping("/login")
    public String login(String loginCode, String password,
                        @RequestParam(required = false) String url,     /*用于获取来访地址（并在登陆成功后跳转），设定默认可以没有（false）*/
                        HttpServletRequest request, HttpServletResponse response,Model model/*, RedirectAttributes  redirectAttributes*/){
        TbSysUser tbSysUser = loginService.login(loginCode, password);

        /*登陆失败*/
        if (tbSysUser == null){
            /*↓ = request.getSession().setAttribute() ,springMVC的功能，因为登录失败后会重定向*/
            model.addAttribute("message","用户名密码错误"+url);

            /*注释理由：redirectAttributes可以用于跳转后msg传参，但重定向后会向地址栏添加静态资源地址，暂时无法解决，搁置*/
//            redirectAttributes.addFlashAttribute("message","用户名密码错误"+url);
        }

        /*登陆成功,则设置一个全局的token，存放loginCode供其他服务端调取*/
        else {
            String token = UUID.randomUUID().toString();
            String result = redisService.put(token, loginCode, 60 * 60 * 24);   /*在redis中存放token（内含loginCode）,结果为"ok"或者null*/

            /*判断是否存放成功(可能触发熔断),成功则进行存放并跳转*/
            if (StringUtils.isNotBlank(result) && "ok".equals(result)) {   /*当result不为空且为ok*/
                CookieUtils.setCookie(request,response,"token",token,60 * 60 * 24);  /*在cookie中存放token的值*/
                if (StringUtils.isNotBlank(url)){   /*当存在来访地址时让其返回原地址，否则统一返回login*/
                    return "redirect:"+url;
                }
            }else { /*熔断的处理*/
//                redirectAttributes.addFlashAttribute("message","服务器异常，稍后重试"+url);
                model.addAttribute("message","服务器异常，稍后重试"+url);


            }
        }
        /*如果登陆错误,返回至login(并返回错误信息,暂时略)*/
        return "login";
    }


    /*登出功能，删除cookie 返回一个login的方法（比较新奇）*/
    @GetMapping("/logout")
    public String logout(@RequestParam(required = false) String url,HttpServletRequest request,HttpServletResponse response,Model model){
        try {
            CookieUtils.deleteCookie(request,response,"token");
        } catch (Exception e) {
            e.printStackTrace();
        }

        return login(url,request,model);
    }
}
