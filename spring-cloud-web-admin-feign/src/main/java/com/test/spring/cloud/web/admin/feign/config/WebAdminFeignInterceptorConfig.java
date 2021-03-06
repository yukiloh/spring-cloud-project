package com.test.spring.cloud.web.admin.feign.config;

import com.test.spring.cloud.web.admin.feign.interceptor.WebAdminFeignInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebAdminFeignInterceptorConfig implements WebMvcConfigurer {

    @Autowired
    WebAdminFeignInterceptor webAdminFeignInterceptor;
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        /*拦截所有路径,排除静态资源/static */
        registry.addInterceptor(webAdminFeignInterceptor).addPathPatterns("/**").excludePathPatterns("/static");
    }
}
