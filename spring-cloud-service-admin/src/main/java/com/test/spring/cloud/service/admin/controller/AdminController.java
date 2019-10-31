package com.test.spring.cloud.service.admin.controller;


import com.google.common.collect.Lists;
import com.test.spring.cloud.common.domain.TbSysUser;
import com.test.spring.cloud.common.dto.BaseResult;
import com.test.spring.cloud.service.admin.service.AdminService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/*用于测试eureka客户端*/
@RestController
public class AdminController {

    @Autowired
    private AdminService adminService;

    @GetMapping("/login")
    public BaseResult login(String loginCode, String password){
        BaseResult baseResult = checkLogin(loginCode, password);

        /*检查登陆信息*/
        if (baseResult != null) {
            return baseResult;      /*根据checkLogin的方法,用户名密码不为空时,验证信息应为null,继续执行登陆业务;而不为null则直接返回结果*/
        }

        /*执行登陆*/
        TbSysUser tbSysUser = adminService.login(loginCode, password);

        if (tbSysUser != null) {
            return BaseResult.ok(tbSysUser);    /*登陆成功*/
        }else {
            return BaseResult.notOk(Lists.newArrayList(new BaseResult.Error("","登陆失败")));    /*登陆失败*/
        }
    }


    /*检查登陆状态*/
    private BaseResult checkLogin(String loginCode, String password){
        BaseResult baseResult = null;

        if (StringUtils.isBlank(loginCode) || StringUtils.isBlank(password)) {
            baseResult = BaseResult.notOk(
                    Lists.newArrayList( /*使用了Google的guava api,与new ArrayList效果相同*/
                    new BaseResult.Error("loginCode","账号不能为空"),
                    new BaseResult.Error("password","密码不能为空")
                    )
            );
        }

        /*另一种笨重的写法*/
////        ArrayList<BaseResult.Error> errors = new ArrayList<>();     /*使用java原有api的语法*/
//        ArrayList<BaseResult.Error> errors = Lists.newArrayList();;     /*使用guava的api语法*/
//
//
//        if (StringUtils.isBlank(loginCode)) {
//            BaseResult.Error error = new BaseResult.Error();
//            error.setField("loginCode");
//            error.setMessage("账号不能为空");
//            errors.add(error);
//        }
//        if (StringUtils.isBlank(password)) {
//            BaseResult.Error error = new BaseResult.Error();
//            error.setField("password");
//            error.setMessage("密码不能为空");
//            errors.add(error);
//        }

        return baseResult;  /*返回结果的解释:只有当loginCode与password都不为空时,返回null*/
    }






    /*===下方为测试用===*/
    @Value("${server.port}")
    private String port;

    @GetMapping("/hi")
    public String sayHi(String message){
        /*通过占位符的方式输出一句话*/
        return String.format("your massage is %s ,port is %s",message,port);
    }

}
