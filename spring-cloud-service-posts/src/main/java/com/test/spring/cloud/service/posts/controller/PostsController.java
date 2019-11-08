package com.test.spring.cloud.service.posts.controller;

import com.github.pagehelper.PageInfo;
import com.test.spring.cloud.common.service.domain.TbPostsPost;
import com.test.spring.cloud.common.service.dto.BaseResult;
import com.test.spring.cloud.common.service.utils.MapperUtils;
import com.test.spring.cloud.service.posts.service.PostsService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/*与AdminController类似,都是服务提供者,因此使用rest注解*/
@RestController
@RequestMapping("v1/posts")    /*按照restFul风格进行路径设计*/
public class PostsController {

    @Autowired
    private PostsService<TbPostsPost> postsService;

    /*根据ID获取文章*/
    @GetMapping("{postGuid}")
    public BaseResult get(@PathVariable String postGuid){
        TbPostsPost tbPostsPost = new TbPostsPost();
        tbPostsPost.setPostGuid(postGuid);

        /*此处底层查询原理是tk.mybatis利用给予的属性(postGuid)来查询,如果遇到多个属性则使用and来拼接;
        * 如遇到需要使用or来查询的情况需要自行使用example方法来查询*/
        /*一般性此种功能可以查询api文档,%但实际工作中大多数情况需要自己来猜%*/
        TbPostsPost obj = postsService.selectOne(tbPostsPost);
        return BaseResult.ok(obj);
    }

    /*保存文章;    此处没有填写路径是因为遵循了restful风格设计的原则*/
    @PostMapping("/")
    public BaseResult save(
            @RequestParam String tbPostsPostJson,
            @RequestParam String optsBy) {
        /*初始化*/
        int result = 0;
        TbPostsPost tbPostsPost = null;

        /*获取post*/
        try {
            tbPostsPost = MapperUtils.json2pojo(tbPostsPostJson, TbPostsPost.class);
        } catch (Exception e) {
            e.printStackTrace();
        }

        /*判空,如无post则返回保存失败*/
        if (tbPostsPost != null) {

            /*新增用户,通过判断是否存已存在postGuid来决定; StringUtils为apache.commons的功能*/
            if (StringUtils.isBlank(tbPostsPost.getPostGuid())) {
                tbPostsPost.setPostGuid(UUID.randomUUID().toString());
                result = postsService.insert(tbPostsPost, optsBy);

                /*如已存在postGuid,则修改文章*/
            } else {
                result = postsService.update(tbPostsPost, optsBy);
            }

            /*最终判断,确认是否有数据被影响*/
            if (result > 0) {
                return BaseResult.ok("保存文章成功");
            }
        }
        /*关于失败也ok的理由:因为请求成功了    如果请求失败了可以自行编写.notOk()*/
        return BaseResult.ok("保存文章失败");
    }


    /*分页查询*/
    @GetMapping("/page/{pageNum}/{pageSize}")
    public BaseResult page(
            @PathVariable(required = true)int pageNum,
            @PathVariable(required = true)int pageSize,
            @RequestParam(required = false) String tbPostsPostJson    /*可能分页查询不存在post内容因此false*/

    ){

        /*初始化*/
        TbPostsPost tbPostsPost = null;

        /*如果获取到了json数据*/
        if (StringUtils.isNotBlank(tbPostsPostJson)) {
            try {
                tbPostsPost = MapperUtils.json2pojo(tbPostsPostJson, TbPostsPost.class);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        /*具体分页查询逻辑*/
        PageInfo pageInfo = postsService.page(pageNum, pageSize, tbPostsPost);

        /*分页后的结果集*/
        List<TbPostsPost> list = pageInfo.getList();

        /*封装cursor（关于页码的游标）对象*/  /*需要在BaseResult中添加一个带有cursor的重载*/
        BaseResult.Cursor cursor = new BaseResult.Cursor();
        /*设置cursor的总页数total*/
        cursor.setTotal(Long.valueOf(pageInfo.getTotal()).intValue());  /*!!getTotal类型为long，需要转换为int*/
        /*设置当前页数*/
        cursor.setOffset(pageInfo.getPageNum());
        /*设置每页显示条数*/
        cursor.setLimit(pageInfo.getPageSize());

        return BaseResult.ok(list,cursor);
    }


}
