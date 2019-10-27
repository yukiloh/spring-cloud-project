### spring cloud 测试项目
关于分布式：所有项目可单独运行，而非单个模块。
面向对象的设计:最少支持原则

================================================================================

### 项目准备阶段

##### dependencies 
管理全局依赖环境，其他项目皆继承此类
仅有一个pom文件


================================================================================

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


================================================================================

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


================================================================================

#####   web-admin-ribbon       -- web客户端，模式为ribbon，服务消费者，ribbon：捆绑
web的客户端，模拟消费情况
示意图：
消费者（通过RestTemplate）访问服务器（ServiceAdmin 1，ServiceAdmin 2），并启用了负载均衡模式

其他：该模块中pom中主要新增了spring-cloud-starter-netflix-ribbon

访问地址：http://localhost:8764/hi?message=hello


================================================================================

##### web-admin-feign  --web客户端,作用与ribbon相同
feign默认继承了ribbon,因此本案例使用feign
与ribbon用途相同,为服务消费者
但通过注解@FeignClient的配置方式,更加灵活,可插拔

与ribbon的区别:
添加service层的接口,在类上添加注解@FeignClient("生产者的名称"),在类上添加注解@Get/PostMapping,如有参数需要@RequestParam来接收
(此步骤类似于ribbon中的service实体类)


================================================================================

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



================================================================================

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


================================================================================

#### spring-cloud-config 分布式配置中心
spring-cloud-config-server
应对分布式系统中服务数量多的情景，统一进行配置文件管理

配置中心-服务端使用步骤:
1.开启入口类中添加注解@EnableConfigServer,开启配置服务器功能
2.配置yml文件(重点)

*测试访问路径(以web-admin-feign为例):http://localhost:8888/master/web-admin-feign.yml


客户端:
1.pom中添加    spring-cloud-starter-config
2.将application.yml配置文件中的内容替换为云配置内容


关于配置文件区分环境的情况:
通常开发过程中会产生3个环境
1.开发环境  dev
2.测试环境  testing
3.生产环境  prod
spring cloud通过创建profile后缀来适配相应环境,例如:web-admin-feign-dev.yml

此外,通常情况下会存在 application.yml application-prod.yml 2个配置文件
并且启动jar时,bash命令可以指定application.yml的类名
java -jar hello-spring-cloud-web-admin-feign-1.0.0-SNAPSHOT.jar --spring.profiles.active=prod

================================================================================

#### zipkin 服务链追踪
右twitter提供，收集服务的定时数据,方便的监测系统中存在的瓶颈

步骤：
1.pom中添加zipkin、zipkin-server、zipkin-autoconfigure-ui
（其中zipkin在maven显示已弃用，后期考虑更换新版本）
（本项目中由dependencies统一管理包，因此需要在dependencies中的dependencyManagement.dependencies.dependence中添加上述三项jar包）
2.在入口类ZipkinApplication中添加@EnableZipkinServer
3.为所有服务项添加spring-cloud-starter-zipkin依赖,并在配置文件中添加spring.zipkin.base-url,配置zipkin的地址
4.java8以后:需要开启  main: allow-bean-definition-overriding: true

!!:关于java12存在无法启动zipkin问题，可选择切换至java8,或升级springboot&spring cloud的版本后解决，并且解决eureka不再需要依赖javax.xml.bind.JAXBContext

zipkin中的一些术语:
span：zipkin中的基本工作单元
trace：由一系列spans组成的树状结构；分布式大数据工程时，可能需要trace来追踪
Annotation：描述一个事件的情况；通常发生阻塞可以查看到各项单元调用的事件，排查阻塞情况


================================================================================

#### Spring Boot Admin  服务监控
监控每个微服务的健康状态、会话数量、并发数、服务资源、延迟等度量信息

服务端步骤
1.pom中添加jolokia-core、spring-boot-admin-starter-server2个依赖（spring-boot-admin-starter-server需要在dependencies中统一管理）
2.为入口类AdminApplication添加@EnableAdminServer
3.配置yml

客户端步骤
1.pom中添加jolokia-core、spring-boot-admin-starter-client（spring-boot-admin-starter-client需要在dependencies中统一管理）
2.入口类无特别变化;
3.yml中需要添加监控类的ip地址

可以看到类似于心跳的ping

================================================================================
服务的启动顺序：
1.分布式配置中心   config
2.注册与发现中心   eureka
3.服务提供者       serviceAdmin
4.服务消费者       ribbon、feign
5.api网关          zuul
6.监控等           zipkin、admin 


--基础部署完毕--

================================================================================


#### 关于实际部署至服务器时的注意点
1.config、admin、eureka、zipkin、zuul部署至nas上，配置（ip、端口）无特别修改
2.service-admin、feign、ribbon仍为本机运行，需要创建bootstrap.yml用于初始化时加载，并停用application.yml；线上的配置文件需要改为nas的相应地址：端口


#### 补充：关于java8以上版本的适用性    -- 2019年10月25日更新：spring cloud&springboot版本后不再需要
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




================================================================================

### 项目部署阶段

#### 微服务逻辑关系
本章节会着重对架构方面知识进行学习

服务提供者1  服务提供者2  服务提供者3

服务消费者1  服务消费者2  服务消费者3

-            网关聚合
            
-            前台调用

服务消费者 ← 服务提供者         ←   数据库
(feign)     (service-admin)
所有的服务提供者都要获取数据库信息,因此需要创建一个提供给服务提供者专用的common通用接口
因此创建common为总接口,而所有的common-service依赖于common(根据最少支持原则,单一职责来划分模块)
*关于命名:spring-cloud-service-xxxx 都为服务提供者,spring-cloud-web/api/...-...皆为服务消费者,通用为common


####配置service-admin,使其连接数据库,并使用tk.mybatis自动生成数据库查询命令
依赖部分:
1.service-admin依赖于'所有服务提供者'common-service,将service-admin中的pom全部移至common-service,并依赖spring-cloud-common-service
2.导入相关依赖(tk.mybatis、PageHelper、sql连接驱动)

创建接口：
common-service中创建MyMapper的接口,为service-admin提供

配置自动生成sql查询的代码：
1.service-admin的pom中，增加tk.mybatis代码生成插件（注意此处的sql依赖）
2.tk.mybatis通过generatorConfig.xml的配置来生成代码,因此进行.xml的配置
(需要另创建jdbc.prop,用于填写sql连接信息;xml中*标记处为需要自定义修改内容)
3.(后期准备)yml中（远程config配置）增加数据库连接信息(用户名密码)



================================================================================


关于sqlStatement: https://mybatis.org/generator/configreference/generatedKey.html