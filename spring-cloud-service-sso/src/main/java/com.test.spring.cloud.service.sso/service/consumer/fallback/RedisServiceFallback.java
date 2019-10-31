package com.test.spring.cloud.service.sso.service.consumer.fallback;

import com.test.spring.cloud.common.hystrix.Fallback;
import com.test.spring.cloud.service.sso.service.consumer.RedisService;
import org.springframework.stereotype.Component;

@Component
public class RedisServiceFallback implements RedisService {
    /*于feign中的熔断机制相同，在common创建通用的熔断方法*/
    @Override
    public String put(String key, String value, long seconds) {
        return Fallback.badGateway();
    }

    @Override
    public String get(String key) {
        return Fallback.badGateway();
    }
}
