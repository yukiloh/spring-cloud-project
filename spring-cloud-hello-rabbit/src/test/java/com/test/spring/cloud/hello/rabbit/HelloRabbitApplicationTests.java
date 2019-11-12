package com.test.spring.cloud.hello.rabbit;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = HelloRabbitApplication.class)
class HelloRabbitApplicationTests {

    @Autowired
    private RabbitProvider rabbitProvider;

    @Test
    void contextLoads() {
        /*测试发送消息*/
        for (int i = 0; i < 50; i++) {
            rabbitProvider.sent();

        }
    }

}
