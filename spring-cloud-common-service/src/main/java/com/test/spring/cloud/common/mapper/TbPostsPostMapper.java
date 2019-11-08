package com.test.spring.cloud.common.mapper;

import com.test.spring.cloud.common.domain.TbPostsPost;
import tk.mybatis.mapper.MyMapper;

//@CacheNamespace(implementation = RedisCache.class)
public interface TbPostsPostMapper extends MyMapper<TbPostsPost> {
}