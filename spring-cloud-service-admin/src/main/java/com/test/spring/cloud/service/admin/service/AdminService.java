package com.test.spring.cloud.service.admin.service;

import com.test.spring.cloud.common.domain.BaseDomain;
import com.test.spring.cloud.common.service.BaseService;

public interface AdminService<T extends BaseDomain> extends BaseService<T> {
}
