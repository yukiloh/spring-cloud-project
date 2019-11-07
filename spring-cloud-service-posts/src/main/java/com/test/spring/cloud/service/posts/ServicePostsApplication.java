package com.test.spring.cloud.service.posts;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import tk.mybatis.spring.annotation.MapperScan;

@SpringBootApplication
@EnableEurekaClient     /*服务提供者*/
@MapperScan(basePackages = {"com.test.spring.cloud.common.mapper","com.test.spring.cloud.service.posts.mapper"})
public class ServicePostsApplication {
    public static void main(String[] args) {
        SpringApplication.run(ServicePostsApplication.class,args);
    }
}
