package com.test.spring.cloud.service.admin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import tk.mybatis.spring.annotation.MapperScan;

@SpringBootApplication(scanBasePackages = "com.test.spring.cloud")
@EnableEurekaClient     /*开启eureka Client服务器*/
@MapperScan(basePackages = "com.test.spring.cloud.service.admin.mapper")        /*用于dao的注入,告知mybatis路径*/
public class ServiceAdminApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServiceAdminApplication.class, args);
    }

}
