package com.test.spring.cloud.service.posts;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import springfox.documentation.swagger2.annotations.EnableSwagger2;
import tk.mybatis.spring.annotation.MapperScan;


@EnableSwagger2         /*开启swagger2*/
@EnableEurekaClient     /*服务提供者*/
@SpringBootApplication(scanBasePackages = {"com.test.spring.cloud"})
@MapperScan(basePackages = {"com.test.spring.cloud.common.mapper","com.test.spring.cloud.service.posts.mapper"})
public class ServicePostsApplication {
    public static void main(String[] args) {
        SpringApplication.run(ServicePostsApplication.class,args);
    }
}
