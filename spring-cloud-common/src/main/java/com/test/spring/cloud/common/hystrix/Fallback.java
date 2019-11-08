package com.test.spring.cloud.common.hystrix;

import com.google.common.collect.Lists;
import com.test.spring.cloud.common.constants.HttpStatusConstants;
import com.test.spring.cloud.common.dto.BaseResult;
import com.test.spring.cloud.common.utils.MapperUtils;

public class Fallback {
    /*通用的回滚方法*/
    public static String badGateway(){
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
}
