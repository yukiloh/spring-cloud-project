package com.test.spring.cloud.service.sso.mapper;

import com.test.spring.cloud.common.service.domain.TbSysUser;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.MyMapper;

@Service
public interface TbSysUserMapper extends MyMapper<TbSysUser> {
        }