package com.test.spring.cloud.web.admin.ribbon.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/*因为需要实现负载均衡，所以需要去服务提供者*/
@Service
public class AdminService {
    @Autowired
    private RestTemplate restTemplate;

    public String sayHi(String message){
        /*指定当请求服务生产者下的hi路径时会实现负载均衡效果*/  /*通过服务器名称寻找服务提供者*/
        return restTemplate.getForObject("http://spring-cloud-service-admin/hi?message=" + message,String.class);
    }
}
