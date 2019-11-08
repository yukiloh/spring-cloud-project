package com.test.spring.cloud.service.posts.service;

import com.test.spring.cloud.common.domain.TbPostsPost;
import com.test.spring.cloud.common.service.BaseService;
import org.springframework.stereotype.Service;

/*提供一个posts专用的扩展service接口*/
@Service
public interface PostsService extends BaseService<TbPostsPost> {

}
