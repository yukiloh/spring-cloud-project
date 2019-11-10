package com.test.spring.cloud.web.posts.service.fallback;

import com.test.spring.cloud.common.hystrix.Fallback;
import com.test.spring.cloud.web.posts.service.PostsService;
import org.springframework.stereotype.Component;

@Component
public class PostsServiceFallback implements PostsService {

    @Override
    public String page(int pageNum, int pageSize, String tbPostsPostJson) {
        return Fallback.badGateway();

    }
}
