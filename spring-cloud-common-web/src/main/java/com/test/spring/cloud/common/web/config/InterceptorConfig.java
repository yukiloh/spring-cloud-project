package com.test.spring.cloud.common.web.config;

import com.test.spring.cloud.common.web.interceptor.StaticSourcesInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


@Configuration
public class InterceptorConfig implements WebMvcConfigurer {
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new StaticSourcesInterceptor()).addPathPatterns("/**");
    }
}
