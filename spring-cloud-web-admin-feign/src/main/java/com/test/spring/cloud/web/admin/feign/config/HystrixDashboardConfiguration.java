package com.test.spring.cloud.web.admin.feign.config;

import com.netflix.hystrix.contrib.metrics.eventstream.HystrixMetricsStreamServlet;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/*用于创建熔断器仪表监控dashboard的servlet*/
/*因为创建servlet时需要用到xml配置,而springboot不存在xml,因此需要额外创建配置类*/
@Configuration
public class HystrixDashboardConfiguration {

    @Bean
    public ServletRegistrationBean getServlet() {
        HystrixMetricsStreamServlet streamServlet = new HystrixMetricsStreamServlet();
        ServletRegistrationBean registrationBean = new ServletRegistrationBean(streamServlet);  /*spring提供用于创建servlet的bean*/
        registrationBean.setLoadOnStartup(1);   /*启动顺序*/
        registrationBean.addUrlMappings("/hystrix.stream");         /*servlet访问路径*/
        registrationBean.setName("HystrixMetricsStreamServlet");    /*servlet的名称*/
        return registrationBean;
    }
}
