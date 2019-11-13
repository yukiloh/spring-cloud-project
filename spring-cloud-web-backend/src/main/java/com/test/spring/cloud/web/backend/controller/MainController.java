package com.test.spring.cloud.web.backend.controller;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletResponse;
import javax.swing.text.Utilities;
import java.io.IOException;

@Controller
public class MainController {

    /*用于聚合所有模块的主页面*/
    @GetMapping("/")
    public String main() {
        return "main";
    }

    @GetMapping("/welcome")
    public String welcome() {
        return "welcome";
    }


    @GetMapping("/adminIndex/{pageNum}/{pageSize}")
    public String adminIndex(HttpServletResponse response,
                             @PathVariable(value = "pageNum") int pageNum,
                             @PathVariable(value = "pageSize") int pageSize,
                             @RequestParam(required = false,value = "tbPostsPostJson") String tbPostsPostJson    /*非必须提供*/
    ) {
        if (pageNum == 0){
            pageNum = 1;
        }
        if (pageSize == 0){
            pageSize = 10;
        }
        try {
            response.sendRedirect("http://localhost:8774/v1/posts/page/"+pageNum+"/"+pageSize);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    @GetMapping("/adminForm/{postGuid}")
    public String adminForm(HttpServletResponse response,
            @PathVariable(value = "postGuid")String postGuid ){
        if(postGuid.equals("null")){
            postGuid = "d1c2eb1d-e6c3-4120-9e5b-006cf665ac7b";
            try {
                response.sendRedirect("http://localhost:8774/v1/posts/"+postGuid);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }





}
