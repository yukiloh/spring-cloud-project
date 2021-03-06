package com.test.spring.cloud.service.admin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import tk.mybatis.spring.annotation.MapperScan;


@EnableEurekaClient     /*开启eureka Client服务器*/
@SpringBootApplication(scanBasePackages = "com.test.spring.cloud")
/*领域模型的mapper和本项目内的mapper*/
@MapperScan(basePackages = {"com.test.spring.cloud.common.mapper","com.test.spring.cloud.service.admin.mapper"})
public class ServiceAdminApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServiceAdminApplication.class, args);
    }

}

