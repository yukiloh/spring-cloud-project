### spring cloud 测试项目
关于分布式：所有项目可单独运行，而非单个模块。

##### dependencies 
管理全局依赖环境，其他项目皆继承此类
仅有一个pom文件

##### eureka        --eureka服务端，注册于发现中心
用于管理eureka客户端
配置步骤：
1.配置pom文件
2.配置启动类application，为其添加注解@EnableEurekaServer
3.配置yml
坑：注意使用jdk8，java8后弃用javax.xml.bind.JAXBContext，java11中被删除

项目访问地址访问地址：   http://localhost:8761/

eureka一般为高可用模式，也即集群模式


##### service-admin        --eureka客户端,服务提供者
用于为消费者提供服务
1.配置pom
2.配置启动类，添加@EnableEurekaClient
3.配置yml

*启动复数项目后可以选择启动dashboard

eureka的配置思想：
将所有服务都交由服务注册与发现中心（eureka服务端）
消费者使用时通过服务器的名称（yml中的spring.application.name，本案例中为：spring-cloud-service-admin）
来访问服务器的真实ip地址

#####web-admin-ribbon       -- web客户端，模式为ribbon，服务消费者，ribbon：捆绑
web的客户端，模拟消费情况
示意图：
消费者（通过RestTemplate）访问服务器（ServiceAdmin 1，ServiceAdmin 2），并启用了负载均衡模式

其他：该模块中pom中主要新增了spring-cloud-starter-netflix-ribbon

访问地址：http://localhost:8764/hi?message=hello