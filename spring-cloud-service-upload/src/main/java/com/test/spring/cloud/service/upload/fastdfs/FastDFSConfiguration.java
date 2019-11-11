package com.test.spring.cloud.service.upload.fastdfs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/*配置文件存储服务工厂类*/
/*Java 配置方式定义 StorageFactory 的 Bean 使其可以被依赖注入*/
@Configuration
public class FastDFSConfiguration {
    @Bean
    public StorageFactory storageFactory() {
        return new StorageFactory();
    }
}