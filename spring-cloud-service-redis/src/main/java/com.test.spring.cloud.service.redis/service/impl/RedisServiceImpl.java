package com.test.spring.cloud.service.redis.service.impl;

import com.test.spring.cloud.service.redis.service.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class RedisServiceImpl implements RedisService {

    /*redis的主要功能由spring提供(超复杂)*/
    @Autowired
    private RedisTemplate redisTemplate;


    @Override
    public void put(String key, Object value, long seconds) {
        redisTemplate.opsForValue().set(key,value,seconds, TimeUnit.SECONDS);

    }

    @Override
    public Object get(String key) {
        return redisTemplate.opsForValue().get(key);
    }
}
