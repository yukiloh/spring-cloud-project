package com.test.spring.cloud.web.backend.service.fallback;

import com.test.spring.cloud.common.hystrix.Fallback;
import com.test.spring.cloud.web.backend.service.BackendService;
import org.springframework.stereotype.Component;

@Component
public class BackendServiceFallback implements BackendService {

    @Override
    public String page(int pageNum, int pageSize, String tbPostsPostJson) {
        return Fallback.badGateway();
    }
}
