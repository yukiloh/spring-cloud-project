package com.test.spring.cloud.service.posts.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

@Configuration
public class Swagger2Config {
    @Bean
    public Docket createRestApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.test.spring.cloud.service.posts.controller"))
                .paths(PathSelectors.any())
                .build();
    }

    /*具体的页面内容*/
    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("MY API 文档")
                .description("这是文档的说明")
                .termsOfServiceUrl("http://www.这是主页地址.com")
                .version("1.0.0")
                .build();
    }
}
