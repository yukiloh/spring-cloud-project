package com.test.spring.cloud.service.admin.controller;


import com.github.pagehelper.PageInfo;
import com.test.spring.cloud.common.service.domain.TbSysUser;
import com.test.spring.cloud.common.service.dto.BaseResult;
import com.test.spring.cloud.common.service.utils.MapperUtils;
import com.test.spring.cloud.service.admin.service.AdminService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/*旧：用于测试eureka客户端*/   /*现：用于提供文章类api查询服务*/
@RestController
@RequestMapping("v1/admins")    /*按照restFul风格进行路径设计*/
public class AdminController {

    @Autowired
    private AdminService<TbSysUser> adminService;

    /*使用restFul风格，进行分页查询*/
    @GetMapping("/page/{pageNum}/{pageSize}")
    public BaseResult page(
            @PathVariable(required = true)int pageNum,
            @PathVariable(required = true)int pageSize,
//            @RequestParam(required = false) TbSysUser tbSysUser    /*可能分页查询不存在user因此false*/   /*此处注解是因为feign可能无法传输对象数据,因此使用json*/
            @RequestParam(required = false) String tbSysUserJson    /*可能分页查询不存在user因此false*/

    ){
        /*初始化*/
        TbSysUser tbSysUser = null;

        /*如果获取到了json数据*/
        if (StringUtils.isNotBlank(tbSysUserJson)) {
            try {
                tbSysUser = MapperUtils.json2pojo(tbSysUserJson, TbSysUser.class);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        /*具体分页查询逻辑*/
        PageInfo pageInfo = adminService.page(pageNum, pageSize,tbSysUser);

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


    /*根据ID获取管理员*/
    @GetMapping("{userCode}")
    public BaseResult get(@PathVariable String userCode){
        TbSysUser tbSysUser = new TbSysUser();
        tbSysUser.setUserCode(userCode);
        TbSysUser obj = adminService.selectOne(tbSysUser);
        return BaseResult.ok(obj);
    }


    /*保存管理员*/
    @PostMapping("/")
    public BaseResult save(
            @RequestParam String tbUserJson,    /*当不在同一局域网下莫名原因feign无法传输对象,因此传输json数据*/
            @RequestParam String optsBy         /*创建者*/
    ){
        /*初始化*/
        int result = 0;
        TbSysUser tbSysUser = null;

        /*获取user*/
        try {
            tbSysUser = MapperUtils.json2pojo(tbUserJson, TbSysUser.class);
        } catch (Exception e) {
            e.printStackTrace();
        }

        /*判空,如无user则返回保存失败*/
        if (tbSysUser != null) {
            /*将密码加密;    使用spring的加密功能*/
            String password = DigestUtils.md5DigestAsHex(tbSysUser.getPassword().getBytes());
            tbSysUser.setPassword(password);

            /*新增用户,通过判断是否存已存在userCode来决定; StringUtils为apache.commons的功能*/
            if (StringUtils.isNotBlank(tbSysUser.getUserCode())) {
                tbSysUser.setUserCode(UUID.randomUUID().toString());
                result = adminService.insert(tbSysUser, optsBy);

            /*如已存在userCode,则修改用户*/
            }else {
                result = adminService.update(tbSysUser, optsBy);
            }

            /*最终判断,确认是否有数据被影响*/
            if (result > 0) {
                return BaseResult.ok("保存管理员成功");
            }
        }


        return BaseResult.ok("保存管理员失败");
    }















    /*过期,收藏用，登陆功能移至sso*/
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
