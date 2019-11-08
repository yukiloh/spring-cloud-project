package com.test.spring.cloud.common.mapper;

import com.test.spring.cloud.common.domain.TbPostsPost;
import com.test.spring.cloud.common.utils.RedisCache;
import org.apache.ibatis.annotations.CacheNamespace;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.MyMapper;

@CacheNamespace(implementation = RedisCache.class)
public interface TbPostsPostMapper extends MyMapper<TbPostsPost> {
}