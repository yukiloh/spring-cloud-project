package com.test.spring.cloud.web.posts.config;

import com.test.spring.cloud.web.posts.interceptor.WebPostsInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebPostsInterceptorConfig implements WebMvcConfigurer {

    @Autowired
    WebPostsInterceptor webPostsInterceptor;
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        /*拦截所有路径,排除静态资源/static */
        registry.addInterceptor(webPostsInterceptor).addPathPatterns("/**").excludePathPatterns("/static");
    }
}
