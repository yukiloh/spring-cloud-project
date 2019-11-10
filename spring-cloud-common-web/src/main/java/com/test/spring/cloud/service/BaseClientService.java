package com.test.spring.cloud.service;


import com.test.spring.cloud.common.hystrix.Fallback;

/**
 * 通用服务消费者接口    需要传入页码,总页数,Json数据
 * <p>Title: BaseClientService</p>
 * <p>Description: </p>
 *
 * @author Lusifer
 * @version 1.0.0
 * @date 2018/8/12 13:56
 */
public interface BaseClientService {
    /*使用default*/
    default String page(int pageNum, int pageSize, String domainJson) {
        return Fallback.badGateway();
    }
}
