package com.test.spring.cloud.hello.rabbit;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
@RabbitListener(queues = "hello-rabbit")     /*监听hello-rabbit队列的消息*/
public class RabbitConsumer {

    /*接受消息*/
    @RabbitHandler
    public void process(String content){

        System.out.println("consumer:" + content);
    }
}
