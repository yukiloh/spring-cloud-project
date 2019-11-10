package com.test.spring.cloud.common.interceptor;

import com.test.spring.cloud.common.domain.TbSysUser;
import com.test.spring.cloud.common.utils.CookieUtils;
import com.test.spring.cloud.common.utils.MapperUtils;
import com.test.spring.cloud.constants.WebConstants;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/* 拦截器通用函数*/
@Component
public class BaseInterceptorMethods {

    /**
     * 登录方法拦截
     *
     * @param request
     * @param response
     * @param handler
     * @param url      跳转地址
     * @return
     */
    public static boolean preHandleForLogin(HttpServletRequest request, HttpServletResponse response, Object handler, String url,String HOSTS_SSO) {
        String token = CookieUtils.getCookieValue(request, WebConstants.SESSION_TOKEN);

        // token 为空表示一定没有登录
        if (StringUtils.isBlank(token)) {
            try {
                response.sendRedirect(String.format("%s/login?url=%s", HOSTS_SSO, url));
            } catch (IOException e) {
                e.printStackTrace();
            }
            return false;
        }

        return true;
    }

    /**
     * 登录方法拦截
     *
     * @param request
     * @param response
     * @param handler
     * @param modelAndView
     * @param tbSysUserJson 登录用户 JSON 对象
     * @param url           跳转地址
     */
    public static void postHandleForLogin(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView, String tbSysUserJson, String url,String HOSTS_SSO) {
        HttpSession session = request.getSession();
        TbSysUser tbSysUser = (TbSysUser) session.getAttribute(WebConstants.SESSION_USER);

        // 已登录状态
        if (tbSysUser != null) {
            if (modelAndView != null) {
                modelAndView.addObject(WebConstants.SESSION_USER, tbSysUser);
            }
        }

        // 未登录状态
        else {
            if (StringUtils.isNotBlank(tbSysUserJson)) {
                try {
                    // 已登录状态，创建局部会话
                    tbSysUser = MapperUtils.json2pojo(tbSysUserJson, TbSysUser.class);
                    if (modelAndView != null) {
                        modelAndView.addObject(WebConstants.SESSION_USER, tbSysUser);
                    }
                    request.getSession().setAttribute(WebConstants.SESSION_USER, tbSysUser);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        // 二次确认是否有用户信息
        if (tbSysUser == null) {
            try {
                response.sendRedirect(String.format("%s/login?url=", HOSTS_SSO, url));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
