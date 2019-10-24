package com.test.spring.cloud.zull.filter;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class LoginFilter extends ZuulFilter {

    /*zuul定义了4类过滤器类型
    * pre:路由前
    * routing:路由中
    * post:路由后
    * error:错误时
    * */
    @Override
    public String filterType() {
        return "pre";
    }

    /*配置 过滤顺序,越小越靠前*/
    @Override
    public int filterOrder() {
        return 0;
    }

    /*配置 是否需要过滤(通常为true)*/  /*当处罚特殊条件时可以选择关闭过滤器*/
    @Override
    public boolean shouldFilter() {
        return true;
    }

    /*主业务逻辑*/
    @Override
    public Object run() throws ZuulException {
        /*从CurrentContext中获取HttpRequest*/
        RequestContext currentContext = RequestContext.getCurrentContext();
        HttpServletRequest request = currentContext.getRequest();

        /*当没有授权(token=null)时禁止访问*/
        String token = request.getParameter("token");
        if (token == null) {
            currentContext.setSendZuulResponse(false);
            currentContext.setResponseStatusCode(401);  /*401:未授权*/
            try {
                HttpServletResponse response = currentContext.getResponse();
                response.setContentType("text/html;charset=utf-8");    /*设置响应编码格式*/
                response.getWriter().write("非法请求");     /*与servlet的写法一致*/
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
        }
        return null;
    }
}
