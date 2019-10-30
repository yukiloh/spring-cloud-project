package com.test.spring.cloud.service.redis.service;


/*只需要提供get put 2中方法便可*/
public interface RedisService {

    /**
     *
     * @param key
     * @param value
     * @param seconds   超时时间
     */
    void put(String key,String value,long seconds);


    Object get(String key);
}
