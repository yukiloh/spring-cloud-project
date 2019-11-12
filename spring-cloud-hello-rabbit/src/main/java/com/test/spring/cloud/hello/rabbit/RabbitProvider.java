package com.test.spring.cloud.hello.rabbit;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class RabbitProvider {

    @Autowired
    /*类似于redisTemplate,由spring进行了封装*/
    private AmqpTemplate amqpTemplate;

    /*发送消息*/
    public void sent(){
        /*创建一条内容*/
        String content = "hello" + new Date();
        System.out.println("provider:" + content);
        /*将content发送至hello-rabbit的队列中*/
        amqpTemplate.convertAndSend("hello-rabbit",content);
    }
}
