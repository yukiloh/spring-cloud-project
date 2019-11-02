package com.test.spring.cloud.common.web.interceptor;

import org.springframework.lang.Nullable;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/*用于初始化静态资源的拦截器*/
public class StaticSourcesInterceptor implements HandlerInterceptor {
    /*http://192.168.2.110:28080/static/bootstrap.min.js*/
    private static final String HOST_CND = "http://3.113.65.65:28080/";
    private static final String STATIC_SOURCES_PATH = "static/";
    private static final String JS_PATH = "js/";
    private static final String CSS_PATH = "css/";
    private static final String IMAGES_PATH = "images/";


    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler){
        return true;
    }

    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable ModelAndView modelAndView) throws Exception {
        if (modelAndView != null) {
            modelAndView.addObject("staticJS",HOST_CND+STATIC_SOURCES_PATH+JS_PATH);
            modelAndView.addObject("staticCSS",HOST_CND+STATIC_SOURCES_PATH+CSS_PATH);
            modelAndView.addObject("staticImages",HOST_CND+STATIC_SOURCES_PATH+IMAGES_PATH);
        }
    }

    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable Exception ex) throws Exception {
    }
}
