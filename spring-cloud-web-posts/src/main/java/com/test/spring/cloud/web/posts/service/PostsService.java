package com.test.spring.cloud.web.posts.service;

import com.test.spring.cloud.service.BaseClientService;
import com.test.spring.cloud.web.posts.service.fallback.PostsServiceFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@Service
@FeignClient(value = "spring-cloud-service-posts",fallback = PostsServiceFallback.class)
public interface PostsService extends BaseClientService {

    /*用于查询文章分页*/
    @GetMapping("/v1/posts/page/{pageNum}/{pageSize}")
    String page(
            /*提供2个路径参数,1个json参数*/
            @PathVariable(value = "pageNum") int pageNum,
            @PathVariable(value = "pageSize") int pageSize,
            @RequestParam(required = false,value = "tbPostsPostJson") String tbPostsPostJson    /*非必须提供*/
    );

    /*用于查询文章信息*/
    @GetMapping("/v1/posts/{postGuid}")
    String get(
            /*提供2个路径参数,1个json参数*/
            @PathVariable(value = "postGuid") String postGuid
    );
}
