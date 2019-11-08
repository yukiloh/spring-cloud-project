package com.test.spring.cloud.common.mapper;

import com.test.spring.cloud.common.domain.TbPostsPost;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.MyMapper;


@Primary
public interface CommonMapper extends MyMapper<TbPostsPost> {
}