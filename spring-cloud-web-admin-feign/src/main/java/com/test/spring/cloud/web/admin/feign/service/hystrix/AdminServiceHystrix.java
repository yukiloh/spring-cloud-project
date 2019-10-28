package com.test.spring.cloud.web.admin.feign.service.hystrix;

import com.google.common.collect.Lists;
import com.test.spring.cloud.common.constants.HttpStatusConstants;
import com.test.spring.cloud.common.dto.BaseResult;
import com.test.spring.cloud.common.utils.MapperUtils;
import com.test.spring.cloud.web.admin.feign.service.AdminService;
import org.springframework.stereotype.Component;

/*触发熔断后的业务*/
@Component
public class AdminServiceHystrix implements AdminService {
    @Override
    public String login(String loginCode, String password) {
        /*直接返回baseResult*/
        BaseResult baseResult = BaseResult.notOk(Lists.newArrayList(
//                new BaseResult.Error("502", "从上游服务器接收到无效相应")  /*常规写法*/
                new BaseResult.Error(
                        String.valueOf(HttpStatusConstants.BAD_GATWAY.getStatus()),
                        HttpStatusConstants.BAD_GATWAY.getConstant())    /*使用枚举，只写一次*/
        ));
        try {
            return MapperUtils.obj2json(baseResult);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;        /*如果报错直接返回null*/
    }


















    /*===下方为测试用===*/

    @Override
    public String sayHi(String message) {
        return String.format("your massage is %s,but request is bad",message);
    }
}
