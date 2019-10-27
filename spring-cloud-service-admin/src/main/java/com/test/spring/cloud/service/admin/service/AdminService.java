package com.test.spring.cloud.service.admin.service;

import com.test.spring.cloud.service.admin.domain.TbSysUser;

public interface AdminService {

    /*注册*/
    void register(TbSysUser tbSysUser);



    /*登陆*/
    /**
     *
     * @param loginCode 登陆账号
     * @param plantPassword 明文密码
     */
    TbSysUser login(String loginCode,String plantPassword);
}
