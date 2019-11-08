package com.test.spring.cloud.web.admin.feign.service.hystrix;

import com.test.spring.cloud.common.hystrix.Fallback;
import com.test.spring.cloud.web.admin.feign.service.AdminService;
import org.springframework.stereotype.Component;

/*触发熔断后的业务*/
@Component
public class AdminServiceHystrix implements AdminService {
    @Override
    public String login(String loginCode, String password) {
        return Fallback.badGateway();
    }


















    /*===下方为测试用===*/

    @Override
    public String sayHi(String message) {
        return String.format("your massage is %s,but request is bad",message);
    }
}
