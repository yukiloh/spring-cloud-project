package com.test.spring.cloud.web.admin.feign.interceptor;

import com.test.spring.cloud.common.service.domain.TbSysUser;
import com.test.spring.cloud.common.service.utils.CookieUtils;
import com.test.spring.cloud.common.service.utils.MapperUtils;
import com.test.spring.cloud.web.admin.feign.service.RedisService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class WebAdminFeignInterceptor implements HandlerInterceptor {

    @Value("${server.port}")
    private String port;


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        /*检查token*/
        String token = CookieUtils.getCookieValue(request, "token");

        /*如果token为空*/
        if (token == null) {
            /*重定向至单点登陆sso,并携带自身的url; 此处的常量值用@Value获取*/
            try {
                response.sendRedirect("http://localhost:8773/login?url=http://localhost:"+port);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    @Autowired
    RedisService redisService;

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView){
        TbSysUser tbSysUser = (TbSysUser) request.getSession().getAttribute("tbSysUser");
        /*表明为已登录*/
        if (tbSysUser != null) {
            if (modelAndView != null) {
                modelAndView.addObject("tbSysUser",tbSysUser);
            }
            
        }

        /*未登录的情况*/
        else {
            String token = CookieUtils.getCookieValue(request, "token");

            /*再次判断token是否为空；如不写则为隐性bug，可能正好超过redis的存活时间*/
            if (token != null) {
                String loginCode = redisService.get(token);
                if (StringUtils.isNotBlank(loginCode)) {
                    String json = redisService.get(loginCode);
                    if (StringUtils.isNotBlank(json)) {
                        try {
                            /*存在登录信息*/
                            tbSysUser = MapperUtils.json2pojo(json,TbSysUser.class);
                            if (modelAndView != null) {
                                modelAndView.addObject("tbSysUser",tbSysUser);
                            }
                            /*存入局部会话*/
                            request.getSession().setAttribute("tbSysUser",tbSysUser);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }

            }

        }


        /*进行二次确认，避免意外，如果仍未获取tbUser则跳转sso*/
        if (tbSysUser == null) {
            try {
                response.sendRedirect("http://localhost:8773/login?url=http://localhost:"+port);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }
}
