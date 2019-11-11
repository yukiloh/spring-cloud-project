package com.test.spring.cloud.service.upload;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@EnableEurekaClient
@SpringBootApplication(scanBasePackages = {"com.test.spring.cloud"})
/*用于dao的注入,告知mybatis路径*/
public class ServiceUploadApplication {
    public static void main(String[] args) {
        SpringApplication.run(ServiceUploadApplication.class, args);
    }
}