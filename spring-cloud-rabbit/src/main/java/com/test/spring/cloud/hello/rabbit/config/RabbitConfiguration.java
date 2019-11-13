package com.test.spring.cloud.hello.rabbit.config;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/*rabbitMQ的配置类*/
@Configuration
public class RabbitConfiguration {

    @Bean
    /*创建一个队列*/
    /*注意Queue的依赖位置,由spring提供*/
    public Queue queue(){
        /*创建一个名为hello-rabbit的队列*/
        return new Queue("hello-rabbit");
    }
}
