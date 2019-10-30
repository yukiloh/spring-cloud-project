package com.test.spring.cloud.service.redis.controller;

import com.test.spring.cloud.service.redis.service.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RedisController {

    @Autowired
    private RedisService redisService;

    @PostMapping("put")
    public String put(String key, Object value, long seconds){
        redisService.put(key,value,seconds);
        return "ok" ;
    }

    @GetMapping("/get")
    public String get(String key){
        Object o = redisService.get(key);
        if (o != null) {
            String json = String.valueOf(o);
            System.out.println(json);
            return json ;
        }
        return "error";
    }
}
