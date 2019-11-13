package com.test.spring.cloud.web.backend.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@Service
@FeignClient(value = "spring-cloud-web-posts",fallback = BackendService.class)
public interface BackendService {

    @GetMapping("/v1/web/posts/page/{pageNum}/{pageSize}")
    String page(
            /*提供2个路径参数,1个json参数*/
            @PathVariable(value = "pageNum") int pageNum,
            @PathVariable(value = "pageSize") int pageSize,
            @RequestParam(required = false,value = "tbPostsPostJson") String tbPostsPostJson    /*非必须提供*/
    );
}
