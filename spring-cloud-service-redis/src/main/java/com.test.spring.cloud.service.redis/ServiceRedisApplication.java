package com.test.spring.cloud.service.redis;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient     /*开启eureka Client服务器*/
public class ServiceRedisApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServiceRedisApplication.class, args);
    }

}
