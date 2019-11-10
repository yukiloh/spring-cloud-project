package com.test.spring.cloud.web.posts.service;

import com.test.spring.cloud.web.posts.service.fallback.RedisServiceFallback;
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
    String put(@RequestParam(value = "key") String key,
               @RequestParam(value = "value") String value,
               @RequestParam(value = "seconds") long seconds);

    @GetMapping("/get")
    String get(@RequestParam(value = "key") String key);



}
