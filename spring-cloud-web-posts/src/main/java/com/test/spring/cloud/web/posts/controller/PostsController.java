package com.test.spring.cloud.web.posts.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PostsController {

    @GetMapping({"/","/index"})
    public String index(){

        return "index";
    }
}
