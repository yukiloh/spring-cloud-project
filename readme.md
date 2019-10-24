### spring cloud 测试项目
关于分布式：所有项目可单独运行，而非单个模块。


========================================================================================================================

##### dependencies 
管理全局依赖环境，其他项目皆继承此类
仅有一个pom文件


========================================================================================================================

##### eureka        --eureka服务端，注册于发现中心
用于管理eureka客户端
配置步骤：
1.配置pom文件
2.配置启动类application，为其添加注解@EnableEurekaServer
3.配置yml
坑：注意使用jdk8，java8后弃用javax.xml.bind.JAXBContext，java11中被删除

项目访问地址访问地址：   http://localhost:8761/

eureka一般为高可用模式，也即集群模式

eureka的注解有3类:
@EnableEurekaServer             服务端,eureka的服务器
@EnableEurekaClient             客户端,服务提供者,诸如ServiceAdmin
@EnableEurekaDiscoveryClient    发现客户端,消费者,诸如Ribbon,feign

========================================================================================================================

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


========================================================================================================================

#####   web-admin-ribbon       -- web客户端，模式为ribbon，服务消费者，ribbon：捆绑
web的客户端，模拟消费情况
示意图：
消费者（通过RestTemplate）访问服务器（ServiceAdmin 1，ServiceAdmin 2），并启用了负载均衡模式

其他：该模块中pom中主要新增了spring-cloud-starter-netflix-ribbon

访问地址：http://localhost:8764/hi?message=hello


========================================================================================================================

##### web-admin-feign  --web客户端,作用与ribbon相同
feign默认继承了ribbon,因此本案例使用feign
与ribbon用途相同,为服务消费者
但通过注解@FeignClient的配置方式,更加灵活,可插拔

与ribbon的区别:
添加service层的接口,在类上添加注解@FeignClient("生产者的名称"),在类上添加注解@Get/PostMapping,如有参数需要@RequestParam来接收
(此步骤类似于ribbon中的service实体类)


========================================================================================================================

##### hystrix 熔断器功能       
spring-cloud-starter-netflix-hystrix

-- 在ribbon中的步骤:
1.入口方法Application中添加@EnableHystrix
2.service层中添加熔断方法(hiError)
3.在需要进行熔断保护的方法上添加@HystrixCommand(fallbackMethod = "HiError")

测试方法:
关闭服务提供者,页面会返回错误信息



-- feign自带熔断器,步骤:
1.在yml中开启hystrix
2.添加熔断实现类(AdminServiceHystrix),并实现接口(AdminService),完善方法内熔断机制
3.为接口中的@FeignClient添加参数 fallback = Hystrix.class

*feign相比ribbon更优雅,面向接口变成贯彻从简原则,一个接口只实现一个用途



-- 开启熔断器仪表监控功能
feign中,
1.向pom中添加
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-netflix-hystrix-dashboard</artifactId>
    </dependency>
2.在入口类中添加@EnableHystrixDashboard
3.springboot 2.x后需额外配置config类(HystrixDashboardConfiguration)用于创建servlet(1.x中不需要)

hystrix dashboard访问路径:   http://localhost:8765//hystrix
页面中可填写: 1.需要监控熔断器的的地址;2.间隔毫秒;3.自定义名称



========================================================================================================================

#####   zuul  路由转发、过滤器

zuul的开启步骤：
1.dependence中添加spring-cloud-starter-netflix-zuul
2.入口类上添加@EnableZuulProxy,开启zuul
3.yml中配置zuul.routes.xxx


注意!此网关为 聚合器微服务 + 链式微服务 的设计模式
外部请求统一访问zuul负载均衡,转发至内部的service(feign&ribbon)    -- 聚合
但services之间又互相依赖于serviceAdmin(生产者和消费者的关系)       -- 链式


访问示意:
-- ribbon-a:
http://localhost:8769/api/a/hi?message=hello

-- feign-b:
http://localhost:8769/api/b/hi?message=hello
(b不填写message内容前会出现bad request)

可直接通过访问zuul的路径来跳转至其下的services


##### 关于路由网关失败时的回调:
创建新的provider类(WebAdminFeignFallbackProvider),并实现FallbackProvider,重写其中的方法;
(测试时可通过关闭service admin来模拟)
(本案例只对feign设置了回调)
(原则上一个api需要一个回调提供类,形成一对一)


##### 关于zuul的过滤器功能
创建一个filter(LoginFilter),并继承ZuulFilter
重写四个方法：
filterType      -- 何时进行过滤
filterOrder     --优先等级
shouldFilter    --是否需要进行过滤
run             --主业务逻辑（拦截的具体方法）

测试: 当访问 http://localhost:8769/api/b/hi?message=hello    
不携带token时,会进行拦截，并向页面输出"非法请求"


========================================================================================================================


#### spring-cloud-config 分布式配置中心
spring-cloud-config-server
应对分布式系统中服务数量多的情景，统一进行配置文件管理

步骤:
1.开启入口类中添加注解@EnableConfigServer,开启配置服务器功能




========================================================================================================================
========================================================================================================================
========================================================================================================================


#### 关于java8以上版本的适用性
需要在eureka的pom中添加
<dependency>
    <groupId>com.sun.xml.bind</groupId>
    <artifactId>jaxb-core</artifactId>
    <version>2.3.0.1</version>
</dependency>
<dependency>
    <groupId>javax.xml.bind</groupId>
    <artifactId>jaxb-api</artifactId>
    <version>2.3.1</version>
</dependency>
<dependency>
    <groupId>com.sun.xml.bind</groupId>
    <artifactId>jaxb-impl</artifactId>
    <version>2.3.1</version>
</dependency>

以解决Type javax.xml.bind.JAXBContext not present的问题
参考：https://crunchify.com/java-11-and-javax-xml-bind-jaxbcontext/?unapproved=63874&moderation-hash=6971fd34bc611ebd58f1ec9c01f42c9b#comment-63874