package com.test.spring.cloud.service.sso.service.consumer;

import com.test.spring.cloud.service.sso.service.consumer.fallback.RedisServiceFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;


@Service
/*从service-redis中获取数据！*/
@FeignClient(value = "spring-cloud-service-redis",fallback = RedisServiceFallback.class)
public interface RedisService {

    @PostMapping("/put")
    String put(@RequestParam("key") String key, @RequestParam("value") String value, @RequestParam("seconds") long seconds);

    @GetMapping("/get")
    String get(@RequestParam("key") String key);




    @GetMapping("/test")
    String test(@RequestParam("key") String key);
}
