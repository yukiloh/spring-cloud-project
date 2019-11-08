package com.test.spring.cloud.service.posts.service.impl;

import com.test.spring.cloud.common.domain.TbPostsPost;
import com.test.spring.cloud.common.mapper.TbPostsPostMapper;
import com.test.spring.cloud.common.service.impl.BaseServiceImpl;
import com.test.spring.cloud.service.posts.service.PostsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/*BaseService中抽取了其他service中的通用方法,并通过泛型的方式提供;
  因此现posts去继承BaseServiceImpl(泛型规定了T和D),并实现了拓展service:PostsService*/
@Service
@Transactional(readOnly = true)
public class PostsServiceImpl extends BaseServiceImpl<TbPostsPost, TbPostsPostMapper> implements PostsService {

}
