package com.test.spring.cloud.web.posts.interceptor;

import com.test.spring.cloud.common.domain.TbSysUser;
import com.test.spring.cloud.common.utils.CookieUtils;
import com.test.spring.cloud.common.utils.MapperUtils;
import com.test.spring.cloud.constants.WebConstants;
import com.test.spring.cloud.utils.HttpServletUtils;
import com.test.spring.cloud.web.posts.service.RedisService;
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
public class WebPostsInterceptor implements HandlerInterceptor {

    @Value("${hosts.sso}")
    private String host_sso;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        /*检查token*/                                     /*共性抽取,使用常量值代替字符串*/
        String token = CookieUtils.getCookieValue(request, WebConstants.SESSION_TOKEN);

        /*如果token为空*/
        if (token == null) {
            /*重定向至单点登陆sso,并携带自身的url; 此处的常量值用@Value获取*/
            try {
//                response.sendRedirect("http://localhost:8773/login?url=http://localhost:"+port);  /*传统方法跳转*/

                /*使用httpServletUtils获取完整路径跳转*/
                response.sendRedirect(String.format("%s/login?url=%s",host_sso, HttpServletUtils.getFullPath(request)));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    @Autowired
    private RedisService redisService;

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView){
        TbSysUser tbSysUser = (TbSysUser) request.getSession().getAttribute(WebConstants.SESSION_USER);
        /*表明为已登录*/
        if (tbSysUser != null) {
            if (modelAndView != null) {
                modelAndView.addObject(WebConstants.SESSION_USER,tbSysUser);
            }
            
        }

        /*未登录的情况*/
        else {
            String token = CookieUtils.getCookieValue(request, WebConstants.SESSION_TOKEN);

            /*再次判断token是否为空；如不写则为隐性bug，可能正好超过redis的存活时间*/
            if (token != null) {
                String loginCode = redisService.get(token);
                if (StringUtils.isNotBlank(loginCode)) {
                    String json = redisService.get(loginCode);
                    System.out.println(json);
                    if (StringUtils.isNotBlank(json)) {
                        try {
                            /*存在登录信息*/
                            tbSysUser = MapperUtils.json2pojo(json,TbSysUser.class);
                            if (modelAndView != null) {
                                modelAndView.addObject(WebConstants.SESSION_USER,tbSysUser);
                            }
                            /*存入局部会话*/
                            request.getSession().setAttribute(WebConstants.SESSION_USER,tbSysUser);
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
                response.sendRedirect(String.format("%s/login?url=%s",host_sso, HttpServletUtils.getFullPath(request)));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }
}
