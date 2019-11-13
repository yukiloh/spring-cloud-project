package com.test.spring.cloud.web.posts.controller;

import com.test.spring.cloud.common.domain.TbPostsPost;
import com.test.spring.cloud.controller.BaseController;
import com.test.spring.cloud.web.posts.service.PostsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletResponse;

@Controller
public class PostsController extends BaseController<TbPostsPost,PostsService> {

    @Autowired
    private PostsService postsService;

    @GetMapping({"/","/index"})
    public String index(Model model){
        String page = postsService.page(1, 10, null);
        String post = postsService.get("d1c2eb1d-e6c3-4120-9e5b-006cf665ac7b");
        model.addAttribute("json",page);
        model.addAttribute("post",post);
        return "index";
    }

}
