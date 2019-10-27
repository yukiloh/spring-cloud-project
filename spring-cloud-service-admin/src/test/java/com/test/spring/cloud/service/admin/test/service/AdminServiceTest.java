package com.test.spring.cloud.service.admin.test.service;

import com.test.spring.cloud.service.admin.ServiceAdminApplication;
import com.test.spring.cloud.service.admin.domain.TbSysUser;
import com.test.spring.cloud.service.admin.service.impl.AdminServiceImpl;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.UUID;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = ServiceAdminApplication.class)
@ActiveProfiles("dev")      /*用于指定配置文件类型(dev prod)*/
@Transactional  /*与rollback结合，测试回滚*/
@Rollback

public class AdminServiceTest {

    /*完成 登陆 和 注册 2个功能*/

    @Autowired
    private AdminServiceImpl adminService;


    @Test
    public void register(){
        /*not null项需要填补*/
        TbSysUser tbSysUser = new TbSysUser();
        String loginCode = "test@test.com";
        String plantPassword = "111111";
        String username = "username";
        tbSysUser.setUserCode(UUID.randomUUID().toString());
        tbSysUser.setUserName(username);
        tbSysUser.setLoginCode(loginCode);
        tbSysUser.setPassword(plantPassword);
        tbSysUser.setUserType("0");
        tbSysUser.setMgrType("1");  /*1：1级管理员*/
        tbSysUser.setStatus("0");   /*0:正常*/
        tbSysUser.setCreateDate(new Date());
        tbSysUser.setCreateBy(tbSysUser.getUserCode());
        tbSysUser.setUpdateDate(new Date());
        tbSysUser.setUpdateBy(tbSysUser.getUserCode());
        tbSysUser.setCorpCode("0");
        tbSysUser.setCorpName("test");
        adminService.register(tbSysUser);

    }

    @Test
    public void login(){
        String loginCode = "test@test.com";
        String plantPassword = "111111";
        TbSysUser user = adminService.login(loginCode, plantPassword);
        Assert.assertNotNull(user);     /*断言：结果不为空*/

    }
}
