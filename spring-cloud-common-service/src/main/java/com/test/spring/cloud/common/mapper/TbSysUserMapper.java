package com.test.spring.cloud.common.mapper;

import com.test.spring.cloud.common.domain.TbSysUser;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.MyMapper;

@Repository("tbSysUserMapper")
public interface TbSysUserMapper extends MyMapper<TbSysUser> {
}