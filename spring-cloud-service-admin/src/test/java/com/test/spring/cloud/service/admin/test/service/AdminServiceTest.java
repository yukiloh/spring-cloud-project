package com.test.spring.cloud.service.admin.test.service;

import com.test.spring.cloud.service.admin.ServiceAdminApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = ServiceAdminApplication.class)
@ActiveProfiles("dev")      /*用于指定配置文件类型(dev prod)*/
public class AdminServiceTest {

    /*完成 登陆 和 注册 2个功能*/

    @Test
    public void register(){


    }

    @Test
    public void login(){


    }
}
