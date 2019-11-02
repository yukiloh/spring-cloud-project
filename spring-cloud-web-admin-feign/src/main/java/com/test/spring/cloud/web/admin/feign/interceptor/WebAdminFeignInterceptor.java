package com.test.spring.cloud.web.admin.feign.interceptor;

import com.test.spring.cloud.common.utils.CookieUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class WebAdminFeignInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        /*检查token*/
        String token = CookieUtils.getCookieValue(request, "token");

        /*如果token为空*/
        if (token == null) {
            /*重定向至单点登陆sso,并携带自身的url; 此时的常量值后期可以考虑放入config中用@Value获取*/
            try {
                response.sendRedirect("http://localhost:8773/login?url=http://localhost:8762");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }
}
