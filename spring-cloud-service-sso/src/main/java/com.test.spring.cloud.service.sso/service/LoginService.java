package com.test.spring.cloud.service.sso.service;


import com.test.spring.cloud.common.domain.TbSysUser;
import org.springframework.stereotype.Service;

@Service
public interface LoginService {

    /*注册*/
    void register(TbSysUser tbSysUser);



    /*登陆*/
    /**
     *
     * @param loginCode 登陆账号
     * @param plantPassword 明文密码
     */
    TbSysUser login(String loginCode, String plantPassword);


}
