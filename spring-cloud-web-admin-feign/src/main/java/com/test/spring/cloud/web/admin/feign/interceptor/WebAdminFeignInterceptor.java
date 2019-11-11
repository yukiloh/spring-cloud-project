package com.test.spring.cloud.web.admin.feign.interceptor;

import com.test.spring.cloud.common.interceptor.BaseInterceptorMethods;
import com.test.spring.cloud.common.utils.CookieUtils;
import com.test.spring.cloud.constants.WebConstants;
import com.test.spring.cloud.web.admin.feign.service.RedisService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class WebAdminFeignInterceptor implements HandlerInterceptor {

    @Value("${hosts.sso}")
    private String HOSTS_SSO;

    @Autowired
    private RedisService redisService;


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        return BaseInterceptorMethods.preHandleForLogin(request, response, handler, "http://localhost:8765" + request.getServletPath(),HOSTS_SSO);

        /*原拦截器内容，已重构至common-web*/
//        /*检查token*/
//        String token = CookieUtils.getCookieValue(request, WebConstants.SESSION_TOKEN);
//
//        /*如果token为空*/
//        if (token == null) {
//            /*重定向至单点登陆sso,并携带自身的url; 此处的常量值用@Value获取*/
//            try {
//                response.sendRedirect(String.format("%s/login?url=%s",HOSTS_SSO, HttpServletUtils.getFullPath(request)));
//                return false;
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView){
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

        /*原拦截器内容，已重构至common-web*/
//        /*未登录的情况*/
//        else {
//            String token = CookieUtils.getCookieValue(request, WebConstants.SESSION_TOKEN);
//
//            /*再次判断token是否为空；如不写则为隐性bug，可能正好超过redis的存活时间*/
//            if (token != null) {
//                String loginCode = redisService.get(token);
//                if (StringUtils.isNotBlank(loginCode)) {
//                    String json = redisService.get(loginCode);
//                    if (StringUtils.isNotBlank(json)) {
//                        try {
//                            /*存在登录信息*/
//                            tbSysUser = MapperUtils.json2pojo(json,TbSysUser.class);
//                            if (modelAndView != null) {
//                                modelAndView.addObject(WebConstants.SESSION_USER,tbSysUser);
//                            }
//                            /*存入局部会话*/
//                            request.getSession().setAttribute(WebConstants.SESSION_USER,tbSysUser);
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                    }
//                }
//            }
//        }
//
//        /*进行二次确认，避免意外，如果仍未获取tbUser则跳转sso*/
//        if (tbSysUser == null) {
//            try {
//                response.sendRedirect(String.format("%s/login?url=%s", HOSTS_SSO, HttpServletUtils.getFullPath(request)));
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

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
