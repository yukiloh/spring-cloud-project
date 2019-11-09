package com.test.spring.cloud.service.admin.service.impl;

import com.test.spring.cloud.common.domain.TbSysUser;
import com.test.spring.cloud.common.mapper.TbSysUserMapper;
import com.test.spring.cloud.common.service.impl.BaseServiceImpl;
import com.test.spring.cloud.service.admin.service.AdminService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
/*注意此处的继承和指定泛型*/    /*继承领域模型的crud功能,并预留了extendMapper的mapper接口*/
public class AdminServiceImpl extends BaseServiceImpl<TbSysUser, TbSysUserMapper> implements AdminService {


}
