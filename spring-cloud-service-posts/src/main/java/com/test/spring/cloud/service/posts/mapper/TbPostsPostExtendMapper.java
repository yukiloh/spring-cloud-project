package com.test.spring.cloud.service.posts.mapper;

import com.test.spring.cloud.common.domain.TbSysUser;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.MyMapper;


/*对于非通用的mapper功能，可以写在拓展的mapper中*/
@Repository
public interface TbPostsPostExtendMapper extends MyMapper<TbSysUser> {
}