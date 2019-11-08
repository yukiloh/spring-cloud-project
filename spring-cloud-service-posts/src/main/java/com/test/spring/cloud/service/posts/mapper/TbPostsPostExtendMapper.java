package com.test.spring.cloud.service.posts.mapper;

import com.test.spring.cloud.common.domain.TbPostsPost;
import tk.mybatis.mapper.MyMapper;

/*此处是否添加@Repository只是对开发过程有用，与编译期间无关*/
/*对于非通用的mapper功能，可以写在拓展的mapper中*/
public interface TbPostsPostExtendMapper extends MyMapper<TbPostsPost> {
}