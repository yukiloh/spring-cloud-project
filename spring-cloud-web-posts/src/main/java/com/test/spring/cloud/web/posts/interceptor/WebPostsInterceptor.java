package com.test.spring.cloud.web.posts.interceptor;

import com.test.spring.cloud.common.interceptor.BaseInterceptorMethods;
import com.test.spring.cloud.common.utils.CookieUtils;
import com.test.spring.cloud.constants.WebConstants;
import com.test.spring.cloud.web.posts.service.RedisService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class WebPostsInterceptor implements HandlerInterceptor {

    @Value("${hosts.sso}")
    private String HOSTS_SSO;

    @Autowired
    private RedisService redisService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        return BaseInterceptorMethods.preHandleForLogin(request, response, handler, "http://localhost:8766" + request.getServletPath(),HOSTS_SSO);
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) {
        int retrial = 10;
        String token = CookieUtils.getCookieValue(request, WebConstants.SESSION_TOKEN);
        if (StringUtils.isNotBlank(token)) {
            String loginCode = tryToGetDataFromRedis(token, retrial);
            if (StringUtils.isNotBlank(loginCode)){
                String tbSysUserJson = tryToGetDataFromRedis(loginCode, retrial);
                if (StringUtils.isNotBlank(loginCode)) {
                    BaseInterceptorMethods.postHandleForLogin(request, response, handler, modelAndView, tbSysUserJson, "http://localhost:8766/" + request.getServletPath(), HOSTS_SSO);
                }
            }
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {

    }




    /*失败后重试*/
    private String tryToGetDataFromRedis(String key, int retrial) {
        try {
            for (int i = 0; i < retrial; i++) {
                String result = redisService.get(key);
                if (StringUtils.isNotBlank(result)){
                    return result;
                }
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            System.out.println("####redis读取数据失败，超过重试次数上限！####");
        }
        return null;
    }
}
