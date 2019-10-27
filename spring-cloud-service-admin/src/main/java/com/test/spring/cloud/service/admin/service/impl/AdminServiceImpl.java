package com.test.spring.cloud.service.admin.service.impl;

import com.test.spring.cloud.service.admin.domain.TbSysUser;
import com.test.spring.cloud.service.admin.mapper.TbSysUserMapper;
import com.test.spring.cloud.service.admin.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;
import tk.mybatis.mapper.entity.Example;

@Transactional(readOnly = true) /*声明式事务*/
@Service
public class AdminServiceImpl implements AdminService {
    /*注册&登陆功能完成，后续需要添加restful的controller*/

    @Autowired
    private TbSysUserMapper tbSysUserMapper;

    /*注册*/
    @Override
    @Transactional(readOnly = false)        /*对于注册需要关闭readOnly*/
    public void register(TbSysUser tbSysUser) {
        tbSysUser.setPassword(DigestUtils.md5DigestAsHex(tbSysUser.getPassword().getBytes()));  /*将密码加密*/
        tbSysUserMapper.insert(tbSysUser);

    }

    /*登陆*/
    @Override
    public TbSysUser login(String loginCode, String plantPassword) {
        Example example = new Example(TbSysUser.class); /*tk提供的mybatis的查询工具*/
        example.createCriteria().andEqualTo("loginCode",loginCode);

        TbSysUser tbSysUser = tbSysUserMapper.selectOneByExample(example);

        String password = DigestUtils.md5DigestAsHex(plantPassword.getBytes()); /*明文加密，需要传入字节码*/
        if (tbSysUser.getPassword().equals(password)) {
            return tbSysUser;
        }
        return null;    /*如果密码不匹配返回null*/
    }
}
