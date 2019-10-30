package com.test.spring.cloud.service.sso;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import tk.mybatis.spring.annotation.MapperScan;

@SpringBootApplication
@EnableDiscoveryClient      /*服务消费者*/
@EnableEurekaClient
@EnableFeignClients
@MapperScan(basePackages = "com.test.spring.cloud.service.sso.mapper")        /*用于dao的注入,告知mybatis路径*/

public class ServiceSSOApplication {
    public static void main(String[] args) {
        SpringApplication.run(ServiceSSOApplication.class, args);
    }
}