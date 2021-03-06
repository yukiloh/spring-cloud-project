package com.test.spring.cloud.web.admin.feign.service;


import com.test.spring.cloud.web.admin.feign.service.hystrix.AdminServiceHystrix;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@Service      /*原教程中未添加,此处需要向spring注入*/
/*feign使用时需要创建接口,而ribbon则是创建实体类AdminService*/
/*@FeignClient:指向服务提供者的名称*/  /*fallback:提供熔断后的方法名称*/
@FeignClient(value = "spring-cloud-service-admin",fallback = AdminServiceHystrix.class)
public interface AdminService {

    @GetMapping("/login")
    String login(@RequestParam("loginCode") String loginCode,@RequestParam("password") String password);

















    /*===下方为测试用===*/

    @GetMapping("/hi")
    /*前端发送get请求,message=xxx,需要使用RequestParam来接收*/
    String sayHi(@RequestParam("message") String message);
}
