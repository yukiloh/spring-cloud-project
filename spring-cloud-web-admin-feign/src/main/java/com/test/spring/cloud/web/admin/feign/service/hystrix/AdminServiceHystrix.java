package com.test.spring.cloud.web.admin.feign.service.hystrix;

import com.test.spring.cloud.web.admin.feign.service.AdminService;
import org.springframework.stereotype.Component;


@Component
public class AdminServiceHystrix implements AdminService {
    @Override
    public String login(String loginCode, String password) {
        return String.format("your massage is %s,but request is bad");
    }

    @Override
    public String sayHi(String message) {
        return String.format("your massage is %s,but request is bad",message);
    }
}
