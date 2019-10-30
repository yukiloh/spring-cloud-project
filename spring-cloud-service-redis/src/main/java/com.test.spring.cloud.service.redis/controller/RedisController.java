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

    @PostMapping("/put")
    public String put(String key, String value, long seconds){
        redisService.put(key,value,seconds);
        return "ok" ;
    }

    @GetMapping("/get")
    public String get(String key){
        Object o = redisService.get(key);
        if (o != null) {
//            String json = o.getValue();
            String json = String.valueOf(o);
            System.out.println(json);
            return json ;
        }
        return null;
    }

    @GetMapping("/test")
    public String test(String key){
        Object o = redisService.get(key);
        System.out.println(o);
        return key;
    }

}
