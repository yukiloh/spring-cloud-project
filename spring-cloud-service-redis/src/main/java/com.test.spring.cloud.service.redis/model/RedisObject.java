package com.test.spring.cloud.service.redis.model;

import java.io.Serializable;

public class RedisObject implements Serializable {
    private String value;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
