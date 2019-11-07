package com.test.spring.cloud.service.admin.controller;


import com.github.pagehelper.PageInfo;
import com.test.spring.cloud.common.domain.TbSysUser;
import com.test.spring.cloud.common.dto.BaseResult;
import com.test.spring.cloud.service.admin.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/*旧：用于测试eureka客户端*/   /*现：用于提供文章类api查询服务*/
@RestController
@RequestMapping("v1/admins")    /*按照restFul风格进行路径设计*/
public class AdminController {

    @Autowired
    private AdminService adminService;

    /*使用restFul风格，进行分页查询*/
    @GetMapping("/page/{pageNum}/{pageSize}")
    public BaseResult page(
            @PathVariable(required = true)int pageNum,
            @PathVariable(required = true)int pageSize,
            @RequestParam(required = false) TbSysUser tbSysUser    /*可能分页查询不存在user因此false*/

    ){
        /*具体分页查询逻辑*/
        PageInfo pageInfo = adminService.page(pageNum, pageSize, tbSysUser);

        /*分页后的结果集*/
        List<TbSysUser> list = pageInfo.getList();

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



    /*过期，登陆功能移至sso*/
//
//    @Autowired
//    private AdminService adminService;
//
//    @GetMapping("/login")
//    public BaseResult login(String loginCode, String password){
//        BaseResult baseResult = checkLogin(loginCode, password);
//
//        /*检查登陆信息*/
//        if (baseResult != null) {
//            return baseResult;      /*根据checkLogin的方法,用户名密码不为空时,验证信息应为null,继续执行登陆业务;而不为null则直接返回结果*/
//        }
//
//        /*执行登陆*/
//        TbSysUser tbSysUser = adminService.login(loginCode, password);
//
//        if (tbSysUser != null) {
//            return BaseResult.ok(tbSysUser);    /*登陆成功*/
//        }else {
//            return BaseResult.notOk(Lists.newArrayList(new BaseResult.Error("","登陆失败")));    /*登陆失败*/
//        }
//    }
//
//
//    /*检查登陆状态*/
//    private BaseResult checkLogin(String loginCode, String password){
//        BaseResult baseResult = null;
//
//        if (StringUtils.isBlank(loginCode) || StringUtils.isBlank(password)) {
//            baseResult = BaseResult.notOk(
//                    Lists.newArrayList( /*使用了Google的guava api,与new ArrayList效果相同*/
//                    new BaseResult.Error("loginCode","账号不能为空"),
//                    new BaseResult.Error("password","密码不能为空")
//                    )
//            );
//        }
//
//        /*另一种笨重的写法*/
//////        ArrayList<BaseResult.Error> errors = new ArrayList<>();     /*使用java原有api的语法*/
////        ArrayList<BaseResult.Error> errors = Lists.newArrayList();;     /*使用guava的api语法*/
////
////
////        if (StringUtils.isBlank(loginCode)) {
////            BaseResult.Error error = new BaseResult.Error();
////            error.setField("loginCode");
////            error.setMessage("账号不能为空");
////            errors.add(error);
////        }
////        if (StringUtils.isBlank(password)) {
////            BaseResult.Error error = new BaseResult.Error();
////            error.setField("password");
////            error.setMessage("密码不能为空");
////            errors.add(error);
////        }
//
//        return baseResult;  /*返回结果的解释:只有当loginCode与password都不为空时,返回null*/
//    }






    /*===下方为测试用===*/

    @GetMapping("/")
    public String index(){
        return "hello index!";
    }

    @Value("${server.port}")
    private String port;

    @GetMapping("/hi")
    public String sayHi(String message){
        /*通过占位符的方式输出一句话*/
        return String.format("your massage is %s ,port is %s",message,port);
    }

}
