package com.test.spring.cloud.web.posts.controller;

import com.test.spring.cloud.common.domain.TbPostsPost;
import com.test.spring.cloud.common.utils.MapperUtils;
import com.test.spring.cloud.controller.BaseController;
import com.test.spring.cloud.web.posts.service.PostsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Controller
public class PostsController extends BaseController<TbPostsPost,PostsService> {

    @Autowired
    private PostsService postsService;



    @GetMapping({"/postList"})
    public String postList() {
        return "postList";
    }


    /*获取文章列表*/
    @ResponseBody
    @PostMapping({"/posts"})
    public String posts(){
        String page = postsService.page(1, 10, null);
        return page;
    }

    /*进入文章单页*/
    @GetMapping({"/post/{guid}"})
    public String post(@PathVariable String guid, Model model) {
        String post = postsService.get(guid);
        /*此处需要解析下json，然后将title，main，time传入model*/
//        model.addAttribute("title",postsTable.getTitle());
//        model.addAttribute("main",postsTable.getMain());
//        model.addAttribute("timePublished",postsTable.getTimePublished());
        return "post";
    }

    /*添加postList前，原始项目获取数据的方式*/
//    @GetMapping({"/","/index"})
//    public String index(Model model){
//        String page = postsService.page(1, 10, null);
//        String post = postsService.get("d1c2eb1d-e6c3-4120-9e5b-006cf665ac7b");
//        model.addAttribute("json",page);
//        model.addAttribute("post",post);
//        return "index";
//    }
}
