package com.test.spring.cloud.web.admin.ribbon.service;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/*因为需要实现负载均衡，所以需要去服务提供者*/
@Service
public class AdminService {
    @Autowired
    private RestTemplate restTemplate;

    @HystrixCommand(fallbackMethod = "HiError")     /*告诉方法,当发生错误时回滚调用名称为"hiError的熔断器"*/
    public String sayHi(String message){
        /*指定当请求服务生产者下的hi路径时会实现负载均衡效果*/  /*通过服务器名称寻找服务提供者*/
        return restTemplate.getForObject("http://spring-cloud-service-admin/hi?message=" + message,String.class);
    }

    public String HiError(String message){
        return String.format("your massage is %s,but request is bad",message);
    }
}
