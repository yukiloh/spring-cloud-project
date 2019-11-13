package com.test.spring.cloud.common.config;

import com.test.spring.cloud.common.interceptor.StaticSourcesInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


@Configuration
public class InterceptorConfig implements WebMvcConfigurer {

    @Bean
    StaticSourcesInterceptor staticSourcesInterceptor() {
        return new StaticSourcesInterceptor();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(staticSourcesInterceptor()).addPathPatterns("/**");
    }
}
