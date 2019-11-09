package com.test.spring.cloud.web.posts.service.fallback;

import com.test.spring.cloud.web.posts.service.RedisService;
import org.springframework.stereotype.Component;

@Component
public class RedisServiceFallback implements RedisService {
    /*于feign中的熔断机制相同，在common创建通用的熔断方法*/
    @Override
    public String put(String key, String value, long seconds) {
        return null;
//        return Fallback.badGateway();     /*原redis的熔断方法，因不便于判断取消*/
    }

    @Override
    public String get(String key) {
        return null;
    }


}
