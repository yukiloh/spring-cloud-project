package com.test.spring.cloud.common.service.service;

import com.github.pagehelper.PageInfo;
import com.test.spring.cloud.common.service.domain.BaseDomain;

/*领域模型的crud*/
public interface BaseService<T extends BaseDomain> {

    /*createBy,需要填写创建者的id*/
    int insert(T t,String createBy);

    int delete(T t);

    int update(T t,String updateBy);

    T selectOne(T t);

    int count(T t);

    /*page helper的查询信息,需要提供页码和总页数,和领域模型*/
    PageInfo<T> page(int pageNum,int pageSize,T t);
}
