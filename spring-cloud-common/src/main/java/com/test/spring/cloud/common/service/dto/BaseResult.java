package com.test.spring.cloud.common.service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;
import java.util.List;


/*文档要求:封装结果result data success cursor errors,并返回结果ok或者not_ok*/
@Data
public class BaseResult implements Serializable {
    public static final String RESULT_OK = "ok";
    public static final String RESULT_NOT_OK = "not_ok";
    public static final String SUCCESS = "操作成功";

    private String result;
    private Object data;
    private String success;
    private Cursor cursor;  /*游标*/
    private List<Error> errors;

    /*对ok和not_ok的结果进行封装 */
    /*ok的结果集*/
    public static BaseResult ok(){
        return createResult(RESULT_OK,null,SUCCESS,null,null);
    }
    /*重载,带有object的结果集*/
    public static BaseResult ok(Object data){
        return createResult(RESULT_OK,data,SUCCESS,null,null);
    }
    /*重载，带有cursor的结果集*/
    public static BaseResult ok(Object data,Cursor cursor){
        return createResult(RESULT_OK,data,SUCCESS,cursor,null);
    }


    /*notOk的结果集*/
    public static BaseResult notOk(List<Error> errors){
        return createResult(RESULT_NOT_OK,null,"",null,errors);
    }


    /**
     *
     * @param result
     * @param data
     * @param success
     * @param cursor
     * @param errors
     * @return
     */
    private static  BaseResult createResult(String result,Object data,String  success,Cursor cursor,List<Error> errors){
        BaseResult baseResult = new BaseResult();
        baseResult.setResult(result);
        baseResult.setData(data);
        baseResult.setSuccess(success);
        baseResult.setCursor(cursor);
        baseResult.setErrors(errors);

        return baseResult;
    }



    /*内部类;在开发文档中为一个集合*/
    @Data
    public static class Cursor{
        private int total;
        private int offset;
        private int limit;
    }

    @Data
    @AllArgsConstructor /*添加全参构造*/
    public static class Error {
        private String field;
        private String message;
    }
}
