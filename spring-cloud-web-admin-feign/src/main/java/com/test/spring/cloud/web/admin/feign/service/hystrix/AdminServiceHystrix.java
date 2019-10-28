package com.test.spring.cloud.web.admin.feign.service.hystrix;

import com.google.common.collect.Lists;
import com.test.spring.cloud.common.dto.BaseResult;
import com.test.spring.cloud.web.admin.feign.service.AdminService;
import org.springframework.stereotype.Component;

/*触发熔断后的业务*/
@Component
public class AdminServiceHystrix implements AdminService {
    @Override
    public String login(String loginCode, String password) {
        /*直接返回baseResult*/
        return BaseResult.notOk(Lists.newArrayList(new BaseResult.Error("502","从上游服务器接收到无效相应")));
    }


















    /*===下方为测试用===*/

    @Override
    public String sayHi(String message) {
        return String.format("your massage is %s,but request is bad",message);
    }
}
