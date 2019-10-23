package com.test.spring.cloud.web.admin.ribbon.config;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration  /*springContext.xml */
public class RestTemplateConfiguration {

    /*创建RestTemplate*/
    @Bean
    @LoadBalanced       /*实现负载均衡*/
    /*RestTemplate是spring提供的用于访问rest类型服务的客户端，非常便捷*/
    public RestTemplate restTemplate(){
        return new RestTemplate();
    }
}
