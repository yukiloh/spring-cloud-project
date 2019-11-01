package com.test.spring.cloud.common.web.interceptor;

import org.springframework.lang.Nullable;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/*用于初始化静态资源的拦截器*/
public class StaticSourcesInterceptor implements HandlerInterceptor {
    private static final String HOST_CND = "http://192.168.1.90:28080";
    private static final String STATIC_SOURCES_PATH = "/upload/";       /*应为/static，但需修改spc，暂*/


    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        return true;
    }

    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable ModelAndView modelAndView) throws Exception {
        if (modelAndView != null) {
            modelAndView.addObject("staticSources",HOST_CND+STATIC_SOURCES_PATH);
        }
    }

    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable Exception ex) throws Exception {
    }
}
