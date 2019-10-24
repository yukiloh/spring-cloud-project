package com.test.spring.cloud.web.admin.ribbon;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;



@EnableDiscoveryClient  /*用于注册至服务中心*/
@SpringBootApplication
@EnableHystrix          /*熔断器*/
public class WebAdminRibbonApplication {

    public static void main(String[] args) {
        SpringApplication.run(WebAdminRibbonApplication.class, args);
    }

}
