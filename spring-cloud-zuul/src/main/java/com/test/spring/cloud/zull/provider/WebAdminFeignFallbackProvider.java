package com.test.spring.cloud.zull.provider;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.cloud.netflix.zuul.filters.route.FallbackProvider;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/*创建一个回调提供者,实现自spring cloud的FallbackProvider*/
/*本类是为feign创建一个回调*/
@Component
public class WebAdminFeignFallbackProvider implements FallbackProvider {

    /*获取原路由,通过名称*/
    @Override
    public String getRoute() {
        return "spring-cloud-web-admin-feign";
    }

    /*回调响应*/
    @Override
    public ClientHttpResponse fallbackResponse(String route, Throwable cause) {
        /*返回一个ClientHttpResponse,直接new*/
        return new ClientHttpResponse() {

            /*响应状态码*/
            @Override
            public HttpStatus getStatusCode() throws IOException {
                /*关于返回 OK 的原因:
                * 虽然网关向api服务(feign)请求失败,但是消费者客户端向网关请求的是成功的
                * 因此不应向客户端返回404等错误*/
                return HttpStatus.OK;           /*返回类型为枚举*/
            }
            /*状态码的值*/
            @Override
            public int getRawStatusCode() throws IOException {
                return HttpStatus.OK.value();
            }
            /*状态码的原因语句*/
            @Override
            public String getStatusText() throws IOException {
                return HttpStatus.OK.getReasonPhrase();
            }

            @Override
            public void close() {

            }


            /*设置响应体*/
            @Override
            public InputStream getBody() throws IOException {
                ObjectMapper objectMapper = new ObjectMapper();     /*封装需要返回的json数据*/
                Map<String, Object> map = new HashMap<>();
                map.put("status", 200);
                map.put("message", "无法连接，请检查您的网络");         /*重新封装状态码,并向前端写明原因*/
                return new ByteArrayInputStream(objectMapper.writeValueAsString(map).getBytes("UTF-8"));
            }
            /*设置响应头*/
            @Override
            public HttpHeaders getHeaders() {
                HttpHeaders headers = new HttpHeaders();
                // 和 getBody 中的内容编码一致
                headers.setContentType(MediaType.APPLICATION_JSON_UTF8);    /*json的数据*/
                return headers;
            }
        };
    }
}
