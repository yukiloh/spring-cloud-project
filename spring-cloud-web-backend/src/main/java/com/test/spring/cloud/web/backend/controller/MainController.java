package com.test.spring.cloud.web.backend.controller;

import com.test.spring.cloud.common.domain.Message;
import com.test.spring.cloud.common.domain.UserMessages;
import com.test.spring.cloud.common.domain.UserNotification;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletResponse;
import javax.swing.text.Utilities;
import java.io.IOException;

@Controller
public class MainController {
    /*原始项目*/
    /*用于聚合所有模块的主页面*/
//    @GetMapping("/")
//    public String main() {
//        return "main";
//    }
//
//    @GetMapping("/welcome")
//    public String welcome() {
//        return "welcome";
//    }
//
//
//    @GetMapping("/adminIndex/{pageNum}/{pageSize}")
//    public String adminIndex(HttpServletResponse response,
//                             @PathVariable(value = "pageNum") int pageNum,
//                             @PathVariable(value = "pageSize") int pageSize,
//                             @RequestParam(required = false,value = "tbPostsPostJson") String tbPostsPostJson    /*非必须提供*/
//    ) {
//        if (pageNum == 0){
//            pageNum = 1;
//        }
//        if (pageSize == 0){
//            pageSize = 10;
//        }
//        try {
//            response.sendRedirect("http://localhost:8774/v1/posts/page/"+pageNum+"/"+pageSize);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        return null;
//    }
//
//    @GetMapping("/adminForm/{postGuid}")
//    public String adminForm(HttpServletResponse response,
//            @PathVariable(value = "postGuid")String postGuid ){
//        if(postGuid.equals("null")){
//            postGuid = "d1c2eb1d-e6c3-4120-9e5b-006cf665ac7b";
//            try {
//                response.sendRedirect("http://localhost:8774/v1/posts/"+postGuid);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//        return null;
//    }

    @GetMapping({"/index"})
    public String index() {
        return "index";
    }

    /*navbar的消息*/
    @ResponseBody
    @PostMapping({"/{uuid}/messages"})
    public UserMessages userMessages(@PathVariable String uuid){
        UserMessages userMessages = new UserMessages();
        userMessages.setTotal(3);
        userMessages.setFirst(userMessages.creatFirstUserMessages("Tom", "我下面有人", "今天", "/static/img/user1-128x128.jpg"));
        userMessages.setSecond(userMessages.creatSecondUserMessages("Jerry","我上面有人","今天","/static/img/user5-128x128.jpg"));
        userMessages.setThird(userMessages.creatThirdUserMessages("Spark","我下面有人","今天","/static/img/user6-128x128.jpg"));
        return userMessages;
    }

    /*navbar的提醒*/
    @ResponseBody
    @PostMapping({"/{uuid}/notifications"})
    public UserNotification userNotifications(@PathVariable String uuid){
        UserNotification userNotification = new UserNotification();
        userNotification.setMessages(userNotification.creatMessages(1,"今天"));
        userNotification.setResponses(userNotification.creatResponses(2,"今天"));
        userNotification.setReports(userNotification.creatReports(3,"今天"));
        return userNotification;
    }

    /*获取侧边栏*/
    @GetMapping({"/sidebar"})
    public String sidebar() {
        return "/sidebar";
    }

    /* for test! */
    @GetMapping({"/500"})
    public String fiveZeroZero() {
        return "/error/500";
    }
}
