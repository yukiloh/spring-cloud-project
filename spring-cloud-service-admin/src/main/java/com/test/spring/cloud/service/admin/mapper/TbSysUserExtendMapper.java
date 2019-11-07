package com.test.spring.cloud.service.admin.mapper;

import com.test.spring.cloud.common.domain.TbSysUser;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.MyMapper;

@Repository
public interface TbSysUserExtendMapper extends MyMapper<TbSysUser> {
}