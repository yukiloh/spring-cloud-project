package com.test.spring.cloud.common.mapper;

import com.test.spring.cloud.common.domain.TbPostsPost;
import com.test.spring.cloud.common.utils.RedisCache;
import org.apache.ibatis.annotations.CacheNamespace;
import tk.mybatis.mapper.MyMapper;

@CacheNamespace(implementation = RedisCache.class)
public interface TbPostsPostMapper extends MyMapper<TbPostsPost> {
}