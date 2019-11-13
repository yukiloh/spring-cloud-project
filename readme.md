# spring cloud 
关于分布式：所有项目可单独运行，而非单个模块。
面向对象的设计:最少知识原则（Least Knowledge Principle）
(参考：https://www.cnblogs.com/gaochundong/p/least_knowledge_principle.html)
极限编程:只管当下

spring cloud提供了快速构建分布式系统中的常用工具（配置管理，服务发现注册，断路器...）
因此产生了一个样板模型、思想、标准
其余例如spring cloud netflix 或者spring cloud alibaba等都是基于spring cloud的思想创建的分布式构建模板

##### 关于restful风格设计的一些原则:
幂等性:HTTP 幂等方法，是指无论调用多少次都不会有不同结果的 HTTP 方法。例如:
GET     /tickets       # 获取ticket列表
GET     /tickets/12    # 查看某个具体的ticket
POST    /tickets       # 新建一个ticket
PUT     /tickets/12    # 更新ticket 12
PATCH   /tickets/12    # 更新ticket 12
DELETE  /tickets/12    # 删除ticekt 12

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
    ↓           ↓           ↓
服务消费者1  服务消费者2  服务消费者3
                ↓
-            网关聚合
                ↓
-            前台调用

聚合微服务的结构图：/config/images/聚合微服务结构图.jpg
服务消费者 ← 服务提供者         ←   数据库
(feign)     (service-admin)
所有的服务提供者都会获取数据库信息,因此需要创建一个提供给服务提供者专用的common-service通用接口
并且所有的common-service依赖于总通用接口common(根据最少支持原则,单一职责来划分模块)
*关于命名:spring-cloud-service-xxxx 都为服务提供者,spring-cloud-web/api/...等皆为服务消费者,common关键词为通用

================================================================================

#### 配置service-admin,使用tk.mybatis自动生成数据库查询命，并连接数据库
依赖部分:
1.service-admin依赖于所有服务提供者common-service；将service-admin中的pom全部移至common-service,并修改service-admin使其依赖spring-cloud-common-service
2.导入相关依赖(tk.mybatis、PageHelper、sql连接驱动)(此处遇到因为遗漏mariaDB驱动包导致test失败!)

创建接口：
common-service中创建MyMapper的接口,为service-admin提供

配置自动生成sql查询的代码：
1.service-admin的pom中，增加tk.mybatis代码生成插件配置（注意此处的sql依赖的类型）
2.tk.mybatis通过generatorConfig.xml的配置来生成代码,因此进行.xml的配置
(需要另创建jdbc.prop,用于供读取sql连接信息;   另xml中*标记处为需要自定义修改内容)
*关于xml中的sqlStatement: https://mybatis.org/generator/configreference/generatedKey.html
3.(后期准备)yml中（远程config配置）增加数据库连接信息(用户名密码)

数据库信息：
  datasource:
    url: jdbc:mariadb://192.168.1.90:3306/service-admin
    hikari:
      driver-class-name: org.mariadb.jdbc.Driver
      username: root
      password: zou9RYdAGW2MKoBY
aws：

  datasource:
    url: jdbc:mariadb://3.113.65.65:3306/service-admin
    hikari:
      driver-class-name: org.mariadb.jdbc.Driver
      username: root
      password: CVQ39Vyt3mg#B5

*关于用docker生成mariadb，docker-compose代码如下：

version: '3.1'
 
services:
  maria:
    image: mariadb
    ports:
      - "3306:3306"
    environment:
      - MYSQL_ROOT_PASSWORD=CVQ39Vyt3mg#B5
      - MYSQL_DATABASE=service-admin
      - MYSQL_USER=only4me
      - MYSQL_PASSWORD=CVQ39Vyt3mg#B5

个人用户生成语法：
INSERT INTO `service-admin`.tb_sys_user (user_code, login_code, user_name, PASSWORD, email, mobile, phone, sex, avatar, SIGN, wx_openid, mobile_imei, user_type, ref_code, ref_name, mgr_type, pwd_security_level, pwd_update_date, pwd_update_record, pwd_question, pwd_question_answer, pwd_question_2, pwd_question_answer_2, pwd_question_3, pwd_question_answer_3, pwd_quest_update_date, last_login_ip, last_login_date, freeze_date, freeze_cause, user_weight, STATUS, create_by, create_date, update_by, update_date, remarks, corp_code, corp_name, extend_s1, extend_s2, extend_s3, extend_s4, extend_s5, extend_s6, extend_s7, extend_s8, extend_i1, extend_i2, extend_i3, extend_i4, extend_f1, extend_f2, extend_f3, extend_f4, extend_d1, extend_d2, extend_d3, extend_d4) VALUES ('fccf287e-d12c-4321-930b-df27afcb6997', 'test@test.com', 'username', '96e79218965eb72c92a549dd5a330112', 'password@111111.com', NULL, NULL, NULL, NULL, NULL, NULL, NULL, '0', NULL, NULL, '1', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '0', 'fccf287e-d12c-4321-930b-df27afcb6997', '2019-10-27 19:15:48', 'fccf287e-d12c-4321-930b-df27afcb6997', '2019-10-27 19:15:48', NULL, '0', 'test', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);

#### service-admin 正式编写服务提供者的内容
*项目需要先写在测试类中(测试先行)   缺点:费时   优点:代码质量高，且越写越容易
过程:
1.在项目中编写 登陆&注册 功能（详细内容在代码中），为service-admin提供controller
2.controller返回结果一般为统一类，因此先在common项目中创建统一结果baseResult(注意结果集的类型参数)(使用Lombok)
3.AdminController中编写具体login业务逻辑(返回的结果需要符合开发手册的要求)

失误点:checkLogin方法中,返回的baseResult没有进行判断后赋值

测试访问地址: http://localhost:8762/login?loginCode=test@test.com&password=111111
*推荐使用postman,因为页面返回数据为json

================================================================================

#### web-admin 编写服务消费者的内容
同样需要一个common类:common-web,消费者直接使用已创建的web-admin-feign

常规测试：feign中创建controller
开启熔断机制：
1.创建用于熔断的接口（AdminService），并创建方法（login）（有参数传入时需要@RequestParam）
2.创建熔断的实现类（AdminServiceHystrix），并重写相应方法；
*关于实现类内的写法：1.返回结果集为baseResult.Error，因为已经触发熔断所以错误为502，msg为从上游服务器接收到无效相应
                   2.因为此类错误结果可能多次调用，所以整合为枚举（遵从：一次书写，多次调用）
                   3.使用了try/catch，如果抓住则返回错误信息的结果集；如果报错则直接返回null；并没有使用if/else的写法

测试熔断：关闭ServiceAdmin服务器

后续：静态资源从伪cdn服务处获取；因此使用nginx创建伪服务器

================================================================================

#### nginx  目的:为静态资源的cdn提供一个服务器

通过docker启动nginx
docker run --name runoob-nginx-test -p 8081:80 -d nginx

参数解释：
runoob-nginx-test 容器名称。
the -d设置容器在在后台一直运行。
the -p 端口进行映射，将本地 8081 端口映射到容器内部的 80 端口。




或者通过docker-compose up -d来启动
需要使用docker-compose.yml
version: '3.1'
services:
  nginx:
    image: nginx
    container_name: nginx
    ports:
      - 8080:8080
    volumes:
      - ./conf/nginx.conf:/etc/nginx/nginx.conf
      - ./wwwroot:/usr/share/nginx/wwwroot

*不要使用nginx原生文件夹/usr/share/nginx/html/ ！！！！！
*测试中80端口无法更改

此外还需要nginx.conf,具体可以参见下方

---conf start---
user nginx;

# 启动进程,通常设置成和 CPU 的数量相等
worker_processes  1;

events {
    # epoll 是多路复用 IO(I/O Multiplexing) 中的一种方式
    # 但是仅用于 linux2.6 以上内核,可以大大提高 nginx 的性能
    use epoll;
    # 单个后台 worker process 进程的最大并发链接数
    worker_connections  1024;
}

http {
    # 设定 mime 类型,类型由 mime.type 文件定义
    include       mime.types;
    default_type  application/octet-stream;

    # sendfile 指令指定 nginx 是否调用 sendfile 函数（zero copy 方式）来输出文件，对于普通应用，
    # 必须设为 on，如果用来进行下载等应用磁盘 IO 重负载应用，可设置为 off，以平衡磁盘与网络 I/O 处理速度，降低系统的 uptime.
    sendfile        on;
    
    # 连接超时时间
    keepalive_timeout  65;
    # 设定请求缓冲
    client_header_buffer_size 2k;

    #=====代理部分===========
	# 配置一个代理即 tomcat1 服务器
	upstream tomcatServer1 {
		server 192.168.75.145:9090;
	}

	# 配置一个代理即 tomcat2 服务器
	upstream tomcatServer2 {
		server 192.168.75.145:9091;
	}

    server {
        listen 80;
        server_name admin.service.itoken.funtl.com;
        location / {
                # 域名 admin.service.itoken.funtl.com 的请求全部转发到 tomcat_server1 即 tomcat1 服务上
                #这里的http://tomcatServer1需要与代理服务器的upstream相同
                proxy_pass http://tomcatServer1;
                # 欢迎页面，按照从左到右的顺序查找页面
                index index.jsp index.html index.htm;
        }
    }
    
    server {
        listen 80;
        server_name admin.web.itoken.funtl.com;

        location / {
            # 域名 admin.web.itoken.funtl.com 的请求全部转发到 tomcat_server2 即 tomcat2 服务上
            proxy_pass http://tomcatServer2;
            index index.jsp index.html index.htm;
        }
    }
    
    
    
    #=====常规nginx主机部分===========
    # 配置虚拟主机 192.168.75.145
    server {
	# 监听的ip和端口，配置 192.168.75.145:80
        listen       80;
	# 虚拟主机名称这里配置ip地址
        server_name  192.168.75.145;
	# 所有的请求都以 / 开始，所有的请求都可以匹配此 location
        location / {
	    # 使用 root 指令指定虚拟主机目录即网页存放目录
	    # 比如访问 http://ip/index.html 将找到 /usr/local/docker/nginx/wwwroot/html80/index.html
	    # 比如访问 http://ip/item/index.html 将找到 /usr/local/docker/nginx/wwwroot/html80/item/index.html

            root   /usr/share/nginx/wwwroot/html80;
	    # 指定欢迎页面，按从左到右顺序查找
            index  index.html index.htm;
        }

    }
    
    # 配置虚拟主机 192.168.75.245
    server {
        listen       8080;
        server_name  192.168.75.145;

        location / {
            root   /usr/share/nginx/wwwroot/html8080;
            index  index.html index.htm;
        }
    }
}

---conf end---


补充:
*关于nginx的惊群问题：用户首次进入多核多线程的nginx环境中会导致多个核心同时响应；1.13.1版本后已解决
*nginx可以作为对用户访问的负载均衡，属于软负载(软路由,与传统的路由器为"硬路由"),当超出nginx的负载均衡后可以考虑采用F5(专业负责负载均衡,收费)

因此,提供一个由nginx提供的服务器,设定路径,防止静态资源文件夹即可(略)



================================================================================

# redis 介绍略     cluster:集群
redis可通过数字自增,用于区分订单
例如: 订单号生成规格:yyyyMMddhhmmss+ms+自增数字,来区分订单号

redis的高可用,高并发方案:
三种方案:1.keepalived(较老);2.zookeeper(需要自行写逻辑);3.sentinel(专为redis集群服务,官方推荐)

补充,关于单点故障&分布式锁:
主从模式中,当主从间的心跳因为网络震荡从而导致双主模式的故障现象,可以通过分布式锁(zookeeper)来解决;
分布式锁的5(6)个条件:
1.分布式环境下,一个方法 同一时间 只能被 一台机器的 一个线程 所执行
2.高可用 高性能的获取&释放锁
3.具备可重入的特性(由多个任务并发使用一个数据时,可实现重新进入而不会导致数据错误的现象)
4.具备锁失效,防止死锁
5.具备非阻塞锁特性

redis的工具选择:
jedis(过时)→lettuce(已被springboot集成)

示意图:
服务器(内部,外部...)
 ↓
 主   ←  哨兵群进行监控(哨兵1,哨兵2..)
↓  ↓
从 从...

##### 使用docker-compose在docker中安装redis服务器
配置一下文件：（一主二从）      


vim ~/docker/redis/docker-compose.yml

version: '3.1'
services:
  master:
    image: redis
    container_name: redis-master
    ports:
      - 6379:6379

  slave1:
    image: redis
    container_name: redis-slave-1
    ports:
      - 6380:6379
    command: redis-server --slaveof redis-master 6379 

  slave2:
    image: redis
    container_name: redis-slave-2
    ports:
      - 6381:6379
    command: redis-server --slaveof redis-master 6379


如需添加redis密码：（未做测试）
command: redis-server --slaveof redis-master 6379 --requirepass yourpassword

##### 配置redis-sentinel哨兵
配置文件：（*volumes中指定了3份sentinel.conf）
vim ~/docker/sentinel/docker-compose.yml

version: '3.1'
services:
  sentinel1:
    image: redis
    container_name: redis-sentinel-1
    ports:
      - 26379:26379
    command: redis-sentinel /usr/local/etc/redis/sentinel.conf
    volumes:
      - ./sentinel1.conf:/usr/local/etc/redis/sentinel.conf

  sentinel2:
    image: redis
    container_name: redis-sentinel-2
    ports:
      - 26380:26379
    command: redis-sentinel /usr/local/etc/redis/sentinel.conf
    volumes:
      - ./sentinel2.conf:/usr/local/etc/redis/sentinel.conf

  sentinel3:
    image: redis
    container_name: redis-sentinel-3
    ports:
      - 26381:26379
    command: redis-sentinel /usr/local/etc/redis/sentinel.conf
    volumes:
      - ./sentinel3.conf:/usr/local/etc/redis/sentinel.conf


并配置3份 sentinel.conf 配置文件
第一行sentinel的参数解释：
集群名可自定义     127.0.0.1根据需求替换为 redis-master 的 ip  6379 为 redis-master 的端口     2 为最小投票数（因为有 3 台 Sentinel 所以可以设置成 2）

port 26379
dir /tmp
sentinel monitor mymaster 192.168.2.110 6379 2
sentinel down-after-milliseconds mymaster 30000
sentinel parallel-syncs mymaster 1
sentinel failover-timeout mymaster 180000
sentinel deny-scripts-reconfig yes

并创建sentinel1.conf sentinel2.conf sentinel3.conf(供docker-compose设置用)
完成后进行docker-compose up -d 创建容器
集群创建成功后,进入容器:
docker exec -it redis-sentinel-1  /bin/bash
进入redis-sentinel:26379(哨兵1)
redis-cli -p 26379
查看日志:
sentinel master mymaster
可查看到以下信息:包含2个slavers,剩余哨兵数um-other-sentinels,投票数quorum
31) "num-slaves"
32) "2"
33) "num-other-sentinels"
34) "2"
35) "quorum"
36) "2"

创建哨兵集群后可以通过RedisDesktopManager直接连接哨兵(26379)来获取redis集群的信息
至此redis集群创建全部完成.
服务器通过调用哨兵,让哨兵再去调用redis服务器,最后返回结果.

端口号整理:
redis:      192.168.2.110:6379      ,6380,6381
sentinel:   192.168.2.110:26379     ,26380,26381

*关于redis集群配置密码：https://www.cnblogs.com/hckblogs/p/11186311.html
aws-redis密码：pQxWfm339xbT@#

##### 项目中使用redis
redis属于服务提供者,因此需要创建spring-cloud-service-redis项目

关于pom依赖:
<!--供redis(lettuce)用的连接池-->
<dependency>
    <groupId>org.apache.commons</groupId>
    <artifactId>commons-pool2</artifactId>
</dependency>

<!--redis starter,内包含了lettuce-->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-redis</artifactId>
</dependency>


关于redis的config:
spring: 
  redis:
    lettuce:
      pool:
        max-active: 8
        max-idle: 8
        max-wait: -1ms
        min-idle: 0
    sentinel:
#      sentinel的master名称,为自定义
      master: mymaster
#      sentinel的各节点地址&端口
      nodes: 192.168.2.110:26379, 192.168.2.110:26380, 192.168.2.110:26381
      
      
*方法中,redis只！需！要！提供put和get操作（在接口类中创建put和get方法）;  注意存放的value对象需要序列化


##### 使用redis实现单点登录SSO（SingleSignOn）    既是服务消费者，也是提供者
使用传统的cookie可以通过共享域名来交换会话，但缺点1.域名必须统一；2.无法实现跨语言；3.cookie本身不安全


示意图：/config/images/单点登录示意图.jpg          
                      局部会话          全局会话
网页（客户端）     →     服务端     →     SSO（需要获取客户端地址用于返回跳转页面）
                登陆             校验   授权

-                 ←               ←  
-              登陆成功                返回授权（令牌，地址）


*为解决跨语言问题，可以使用redis，统一从数据库中取出登陆信息

创建工程的步骤：
1.修改原有项目，创建通用的common-domain（用于存放TbSysUser）
  因此创建spring-cloud-common-domain，并迁移service-admin下的TbSysUser
 （注意修改原有模块中的resources.mapper.Mapper.xml，和其他类的依赖，和config中的mybatis依赖路径）
2.创建对service-sso，配置config（需要配置mybatis+数据库，hystrix，redis）
3.入口类中添加@EnableDiscoveryClient  @EnableFeignClients，和@MapperScan（用于读取数据库）
  *关于操作数据库的部分，不适用共性抽取，因为每个数据库读取的方法并不一致，因此本项目在service-admin和service-sso分别创建
4.使用feign功能，去消费service-redis中的服务（因此它是服务提供者）；注意feign指向service-redis时的post/get请求
  并创建fallback熔断机制（与feign项目下的熔断机制(同一返回 return Fallback.badGateway())进行整合，至common中）
5.编写具体业务逻辑（查询redis，如存在则直接返回User，无则进行登陆验证）
6.编写controller，提供/login的post请求

传统会话的缺点
1.域名必须统一；       →通过nginx解决 
2.无法实现跨语言；      →redis
3.cookie本身不安全      →redis中使用uuid,通过uuid来验证loginCode
因此需要在loginController中完善cookie的问题3

关于loginController的逻辑:/config/images/loginController登陆逻辑示意图.jpg

登陆后进行判断     →   有token  →   查找loginCode     →   匹配则回传user对象
                 →    无token  →     进行登录,验证用户名密码    →   登陆完成后赋予token
*错误点:获取user时使用了错误的变量!

================================================================================


#### 迁移静态资源至cdn，并修改链接   使用filter拦截并赋予cdn资源路径
将所有静态资源，例如：https://cdn.bootcss.com/jquery/3.4.1/jquery.min.js 迁移至自己的cdn服务器
如使用https://192.168.1.1/jquery/3.4.1/jquery.min.js 则不便于后期维护，因此进行共性抽取，使用拦截器将地址和路径进行统一配置

步骤：
1.在common-web种创建拦截器StaticSourcesInterceptor，在后处理(postHandle)中对ModelAndView进行判断,如果存在则为非JSON请求,添加model:"static"
2.配置拦截器配置类InterceptorConfig,并注册拦截器
3.前台页面通过thymeleaf读取"static"数据;

*关于thymeleaf的2种通过@{}引入资源的方式
    <script th:src="@{${staticSources}+'jquery.min.js'}"></script>
    <script th:src="@{{staticSources}jquery.min.js(staticSources=${staticSources})}"></script>



#### 关于跨域问题
浏览器无法执行执行其他网站的脚本,称为跨域问题,是浏览器对js施加的安全限制
因此脚本必须满足同源(域名-test.com  协议-http 端口-:80 都相同的情况)
三种解决方案:
1.cors(跨资源共享),需要服务器&浏览器同时支持
2.jsonp,服务器端设置的一种"使用模式"(如果非自家的服务器则无法修改)
3.nginx反向代理 ← 本项目采用的方案

代理后静态资源地址:http://192.168.2.110:28080/static/js/bootstrap.min.js
                          nginx服务器地址     静态  js   真实文件
                          
对应服务器路径：http://192.168.2.110:28080/  static ...
              /root/docker/nginx/wwwroot/  static ...



#### 实现其他页面(web-admin-feign)登陆跳转至sso功能 
步骤:
1.检查访问feign页面的用户是否已有登陆(是否有token),因此创建feign的拦截器
 (记得在服务提供者(service-admin)的入口类中添加@SpringBootApplication(scanBasePackages = "com.test.spring.cloud"))
 在拦截其中检查token，如果没有token/token与redis中不符，则携带自身url跳转至sso
2.sso接收跳转的请求，处理问题并按照原url跳转
3.登出功能/logout，删除cookie
！！！！携带url跳转会出现问题！！！！会莫名添加静态资源路径！！！！
登出功能同样有问题，无法删除token，只是将token的值清空（应该是CookieUtils的问题）
 
 
================================================================================
================================================================================

### 后半场的工作
1.实现Spring Cloud Config Client 通用配置
2.实现Spring Boot MyBatis Redis 二级缓存，用于缓存一些不太变化的数据
3.swagger2接口文档引擎
4.管理员服务、文章服务实现CRUD 功能
5.使用FastDFS 实现图片上传


================================================================================
================================================================================

##### 1.关于通用配置
可以通过配置通用配置文件（common-service），使一些通用的配置（如eureka、admin、zipkin等）统一归类
各项目读取时，在bootstrap.yml中加载common-service即可，如：
spring:
  cloud:
    config:
      uri: http://3.113.65.65:8888
      name: spring-cloud-web-admin-ribbon,spring-cloud-common-service-remote
      label: master
      profile: dev

*有坑，导致service-admin出现sql类型的错误，暂时禁用（已解决）


================================================================================


##### 2.实现MyBatis Redis 二级缓存功能      对于一个新知识的学习方法:了解技术-实现技术
了解技术:什么是二级缓存
###### 先了解什么是一级缓存
一级缓存是 SqlSession 级别(内存级)的缓存，存在于内存区域，第二次查询时会从一级缓存中查找数据；
在操作数据库时需要构造 sqlSession 对象，在对象中存在（内存区域）数据结构（HashMap）用于存储缓存数据。
当一个 sqlSession 结束后该 sqlSession 中的一级缓存也就不存在了。Mybatis 默认开启一级缓存。
                                                            
而当服务A与服务B都将查询同一个数据时，服务之间的sqlSession不会进行共享
因此需要使用二级缓存（第三方缓存），*主要*解决缓存共享的问题

###### 再了解什么是二级缓存
而二级缓存属于 mapper （接口）级别(磁盘级)的缓存
多个 SqlSession 去操作同一个 Mapper 的 sql 语句
多个 SqlSession 去操作数据库得到数据会存在二级缓存区域
多个 SqlSession 可以共用二级缓存，二级缓存是跨 SqlSession 
其作用域是 mapper 的同一个 namespace
不同的 sqlSession 两次执行相同 namespace下的 sql 语句且向 sql 中传递参数也相同即最终执行相同的 sql 语句
第一次执行完毕会将数据库中查询的数据写到缓存（内存）
第二次会从缓存中获取数据将不再从数据库查询，从而提高查询效率
*Mybatis 默认没有开启二级缓存需要在 setting 全局参数中配置开启二级缓存


###### 开启二级缓存的步骤
1.config中开启mybatis的二级缓存
mybatis:
  configuration:
    cache-enabled: true

2.实体类实现序列化接口并声明序列号
在idea的setting中,勾选Serializable class without SerialVersionUID,使警告显示未声明序列号,然后通过idea自动补完生成序列号
如:private static final long serialVersionUID = 3619505032895675471L;

3.创建相关工具类 ApplicationContextHolder  (该工具类为开发人员通用名称,redisCache中也是该名称 )
需要实现SpringApplicationContextAware接口，用于手动注入Bean(因为某些bean不会被自动注入,因此需要手动)
当一个类实现了这个接口（ApplicationContextAware）之后,这个类就可以方便获得 ApplicationContext 中的所有 bean(通过上下文)
即，该类可以直接获取 Spring 配置文件中所有有引用到的 Bean 对象
因所有service会使用,所以放在common-service项目中的context目录下
(关于项目的路径,common-XXXX项目的路径都是com.test.spring.cloud.common下
 -基于maven依赖机制和模块化开发的原理,最终打包时会统一打包在一起
 -因此才选择扫描common下的mapper路径@MapperScan(basePackages = {"com.test.spring.cloud.common.mapper",....))

4.创建RedisCache(百度也可以搜到此工具包)
在common-service项目下创建utils.RedisCache

*虽然common项目下也存在utils包,但因maven打包机制所以可以重复创建;
 当java类名也相同时,被依赖的项目会根据依赖顺序,后依赖的会覆盖先依赖的类;
 可以适用于:引用的某框架下的某个包不适用本程序,可以通过此方法进行覆盖

5.在需要的Mapper接口(posts)中添加注解@CacheNameSpace
 此注解的名称解释:因为二级缓存的作用域是*同一nameSpace*

6.service-posts中的入口类,需要让spring扫描到ApplicationContextHolder,因此添加扫描域
 @SpringBootApplication(scanBasePackages = "com.test.spring.cloud")

测试过程中,依赖于ApplicationContextHolder中的断言功能(assertContextInjected),可以判断是否成功开启了二级缓存功能
当有插入/更新功能后,缓存会自动进行数据刷新

*错误点：
1.属性未注入：①入口类没有被扫描   ②ApplicationContextHolder没有implements ApplicationContextAware, DisposableBean！！！！！！
2.Redis put failed：没有在config中正确填写redis信息，或者redis没对齐！！！
================================================================================


#### 3.swagger2接口文档引擎   
主要作用：免去写文档的工作量
缺点:入侵式; spring则是非入侵式

步骤：
1.添加依赖;   所有service项目都需要swagger,因此在common-service中添加swagger依赖
2.在需要的service项目中,配置swagger(每个service的swagger一般不相同)
    -- 创建config.Swagger2Config,并根据项目的实际需求进行修改
3.在入口类中,启用swagger   @EnableSwagger2
4.web访问:http://localhost:8764/swagger-ui.html

补充：
swagger可以在扫描的路径下（controller层）
通过添加注解表明该接口会生成文档，包括接口名、请求方法、参数、返回信息的等等。
@Api：修饰整个类，描述 Controller 的作用
@ApiOperation：描述一个类的一个方法，或者说一个接口
@ApiParam：单个参数描述
@ApiModel：用对象来接收参数
@ApiProperty：用对象接收参数时，描述对象的一个字段
@ApiResponse：HTTP 响应其中 1 个描述
@ApiResponses：HTTP 响应整体描述
@ApiIgnore：使用该注解忽略这个API
@ApiError：发生错误返回的信息
@ApiImplicitParam：一个请求参数
@ApiImplicitParams：多个请求参数


================================================================================


##### 4.关于管理员服务、文章服务类的crud功能
###### 前置工作（对原有service-admin项目进行重构）
1.创建数据库service-posts（脚本文件于config.sql中）
2.重构项目中关于sql的代码
    -- 已规定,连接数据库的必然是服务提供者,因此将数据库连接功能移至common-service中;创建generatorConfig.xml进行配置(user和post2个数据库)
    -- 进行共性抽取,在common-domain中创建领域模型BaseDomain和2个衍生实体类post&user(注意需要继承领域模型)
3.在common-service中编写领域模型crud业务逻辑
    -- 创建接口类BaseService和实体类BaseServiceImpl,编写领域模型的crud业务逻辑(注意勿忘事务管理!)
4.在service-admin中继承领域模型的crud类
5.在service-admin中的controller编写业务
    -- 基于Restful风格进行api解口的编写
        当访问/v1/admins//page/{pageNum}/{pageSize}时，返回一个带有（user）list,（页码）cursor的结果集

###### service-posts 文章服务的提供者的具体业务步骤
1.创建service-posts项目,pom内容基本基于service-admin
2.创建mapper.TbPostsPostExtendMapper(扩展mapper),和相对应的resources下的mapper.xml
3.编写service层;写接口,写实现类;
 (类似于adminController,提供保存(更新)文章 根据id获取文章 分页查询的功能;   实际工作中需要*严格*依据API文档,为前端提供功能!!)
*错误点：因框架布局混乱导致mbg生成的实体类和mapper文件混乱，最后拓展的实体类统一至common-service（基类BaseDomain位于common-domain下）
且service-posts的config只会读取数据库service-posts（service-admin相同），而讲师的mbg是全读取
因此生成的实体类文件 @Table(name = "service-posts..tb_posts_post")处不相同

*此处因为创建了第二个mapper，因此将原有的UserMapper一起进行归类，统一继承被BaseService所调用（而BS则会被其他的service所继承）
犯错点：BaseService应该是抽象类！！！！！


#####  为admin-posts创建服务消费者web-post
创建步骤基本和web-admin相同
因为进行第二次编写,所以先对原有代码进行重构
    -- config中创建通用hosts,抽取sso的登陆地址
    -- config中进行共性抽取，将每个项目内的config配置进行分离，并配置在每个项目的bootstrap.yml中（name: spring-cloud-eureka-client,spring-cloud-admin-client...） 
    -- 在common-web下创建HttpServletUtils,用于获取完整请求路径，携带请求参数,供跳转使用（外部utils，修复了数组越界的问题）
    -- 对于多次获取页面内的数据（admin，token），进行共性抽取，创建WebConstants，提供修改字符串为常量值（WebConstants.SESSION_TOKEN）
*错误点：
1.因为项目进行了重构，导致sso入口类没有扫描sso项目内的controller，导致无法跳转！！！
2.配置文件混乱，依赖包混乱导致许多项目无法启动，因此对config进行了重构
3.远程服务器出现数据读取错误（redis），迁移至本地服务器

具体创建步骤：（基本复制自web-admin-feign）
1.创建拦截器，用于拦截未登录账号
2.创建RedisService(和fallback)，通过feign指向service-redis


##### 创建消费者分页功能 PostService     创建前端的页面，并展示效果
提供的服务内容： 分页查看（√），文章查看（√），创建文章（×）
页面模板方面（thymeleaf）:  (因html页面无法创建成功，此处是概念，没有实际内容)
    -- 为静态js资源共性抽取 在common-web的resources下创建static.assets.app,存放所有js资源
       (打包时利用maven的打包机制 <script src="/assets/app/validate.js"></script> 直接从根目录引用)
    -- 为页面进行共性抽取,使用thymeleaf的include功能  <th:block th:include="includes/head :: head"></th:block>  
       从其他页面引入模块资源  模块资源统一放置在common-web的templates.includes下
       
controller方面:
    -- 创建BaseController,使其他controller去继承他,并创建为bc服务的BaseClientService和DataTablesResult
    -- 进行共性抽取,重构service 创建BaseClientService,使其他service继承他
    -- 创建DataTablesResult,用于接收page方法的结果集
       (注意,他继承了BaseResult,并将需要的属性集进行了封装,无论从哪里请求结果都可以拿到完整,并附带额外需要的结果)
       (面向对象的修改原则，里氏替换原则
        里氏的概括:子类可以扩展父类的功能，但是不能改变父类原有的功能,即所有父类出现的地方,都可以用子类代替)
    -- 开发html，读取BaseController提供的页面

*redis存在写入/读取失败的问题，已在需要读取/存放redis的代码处插入了重试器；    或者可以通过设置feign的等待时间来解决（待开发）
*html内容跨度过大，略过，只提供返回前台简单的json数据


================================================================================


#### 5.FastDFS  分布式文件系统     为微服务提供文件上传下载功能
FastDFS是一个开源的轻量级分布式文件系统，提供文件存储、文件同步、文件访问（文件上传、文件下载）等功能
主要解决了*大容量存储*和*负载均衡*的问题

服务端有2个角色：跟踪器（tracker）和存储节点（storage）
-- 跟踪器主要完成调度工作，在访问上起负载均衡的作用
-- 存储节点则进行存储、同步和提供存取接口的功能

*fastDFS需要与nginx相结合：
-- FastDFS的客户端可进行文件的上传、下载、删除等操作，同时通过 FastDFS 的 HTTP 服务器来提供 HTTP 服务
   但是 FastDFS 的 HTTP 服务较为简单，无法提供负载均衡等高性能的服务，我们需要使用 FastDFS 的 Nginx 模块来弥补这一缺陷

FastDFS架构图：/config/images/FastDFS结构示意图.jpg

关于追踪器的功能示意图：/config/images/FastDFS追踪器的功能示意图.jpg

##### 在服务器端安装FastDFS
1.下载FastDFS.zip(/config/apps/)
2.创建并配置docker-compost.yml

version: '3.1'
services:
  fastdfs:
    build: environment
    restart: always
    container_name: fastdfs
    volumes:
      - ./storage:/fastdfs/storage
    # 主机模式,将容器的所有端口都暴露
    network_mode: host
    
3. ./environment下创建并配置Dockerfile    (FastDFS.zip中存在,注意修改port的端口)
4.修改storage.conf 中跟踪器的ip地址(修改为服务器端的ip地址)
5.修改client.conf中追踪器的ip地址
6.修改mod_fastdfs.conf中追踪器的ip地址
*4 5 6类似于服务发现与注册,FastDFS自带服务器端和客户端,需要让二者互相发现对方

7.修改nginx.conf中的端口(例如:8888→8899)
8.构建,并运行  
 -- docker-compose build        -- docker-compost up -d
9.测试上传功能
 --   上传命令↓         上传的文件↓                上传地址至
 -- fdfs_upload_file /etc/fdfs/client.conf /usr/local/src/fastdfs-5.11/INSTALL

上传完成后会返回文件的uri
本案例参考:  http://3.113.65.65:8899/group1/M00/00/00/rB8AkF3JFUKAQmogAAAeSwu9TgM2083402
补充:     宿主机的文件夹路径 /root/docker/fastDFS/environment      
          docker中的地址    /etc/fdfs/

##### 创建service-upload FastDFS的服务上传模块       只负责上传功能
1.创建pom文件(可以从redis中复制)
2.引入依赖:fastdfs-client-java; 可以参考:https://www.jianshu.com/p/c39ca1ee222b
3.添加工具类 (fastdfs包下)
4.添加云配置,此为自定义配置,需要根据服务器情况进行更改

fastdfs.base.url: http://3.113.65.65:8899/
storage:
  type: fastdfs
  fastdfs:
    tracker_server: 3.113.65.65:22122

5.创建UploadController(提供文件上传功能)
测试页面为web-posts下的index.html
再引入必要的js&css资源后可以正常使用
参考连接: http://3.113.65.65:8899/group1/M00/00/00/rB8AkF3JdWiAWQHoAAMLrbVliv8146.jpg
上传成功后会返回文件名


================================================================================\


#### 后台聚合服务
完成了2个服务消费者后，需要通过网关（zuul）来供后台页面去读取所有的消费者页面
实际开发中网关需要按照职责进行划分因此后台连接网关的中间需要创建nginx，来提供反向代理
参考：/config/images/添加了网关后的项目结构图.jpg


##### 使用行内框架iFrame实现局部页面刷新
通过th:include加载页头、侧边栏、页脚、copyright
中间内容展示部分可以通过iFrame加载
    页头....
    侧边栏....
    <iframe src="/index" name="iframe" frameborder="0" style="width: 100%; heigh= 768px;" />
    页脚....

其中src地址可以进行替换，因此使用脚本，与侧边栏的button相结合，来实现跳转页面的功能
侧边栏button的写法：
    <a href="javascript:void(0);" id="btnPostsIndex" onclick="show(this.id)">index</a>
    <a href="javascript:void(0);" id="btnPostsForm" onclick="show(this.id)">form</a>

脚本的写法：
    var iframe = document.getElementById("iframe"");    
    if (id ==="btnPostsIndex") {
        iframe.src = "/index";
    }
    else if(id ==="btnPostsForm") {
        iframe.src = "/form";
    }

或者可以使用nth-tabs插件，步骤略



##### 使用zuul聚合各模块的内容


##### 使用web-backend读取zuul提供的各模块的页面






================================================================================


#### 消息队列 MessageQueue
MQ 带给我的“协议”不是具体的通讯协议，而是更高层次通讯模型
它定义了两个对象——发送数据的叫生产者
接收数据的叫消费者， 提供一个 SDK 让我们可以定义自己的生产者和消费者实现消息通讯而无视底层通讯协议

MQ 真正的目的是为了通讯，屏蔽底层复杂的通讯协议，定义了一套应用层的、更加简单的通讯协议
即简化通讯，解决通信的问题，实现解耦

消息队列实现了FIFO（先进先出），且具有缓存的能力

##### 消息队列的流派

有broker的mq：存在中间件broker，所有消息经过broker再转发至客户端，例如：
-- 重Topic：kafka、JMS     kafka传输效率块的原因：将大量消息队列压缩，再发送至客消费者进行解压；容易造成消息丢失
-- 轻Topic：RabbitMQ  生产者将消息存放至指定的key，消费者通过消息订阅来获取属于自己队列queue（数据）；
   可以实现负载均衡，队列大小取决于服务器的内存
   此种模式下属于轻量级的topic，生产者只需关注消息存放，消费者只需关心自身的队列，用于映射key和queue的称为交换机（exchange）

无broker的mq：没有broker
-- 代表：ZeroMQ
   因为MQ解决的socket通讯问题，因此将broker设计为一个 库 而非中间件，库 即是服务端，也是消费端，通过重量级的Actor模型来实现

-- 关于Actor模型
   Actor属于并行 异步消息模型，Actor模型需要满足操作系统对进程/线程的要求
   最重要的一点是必须实现公平调度，然而java的akka是无法实现公平调度的，erLang可以
   而RabbitMQ是通过ErLang语言实现的，因此本项目采用RabbitMQ作为消息队列


##### 关于RabbitMQ 
基于ErLang语言开发，具有实现高可用高并发，适用于集群服务器
支持多语言，跨平台   有消息确认和持久化机制，可靠  且开源
RMQ可将消息设置为持久化、临时、或自动删除
RMQ中的交换机exchange类似于数据通信中的交换机
-- 生产者在传递消息时会附带一个ROUTING_KEY,exchange根据key指定给专用的消费者(即路由器中arp协议)
-- (而arp伪装即伪装自己的ip地址欺骗交换机)
而RMQ Server会创建多各虚拟的Massage Broker(即VirtualHosts),也即小型的MQ Server,保证边界隔离相互之间不会干扰
-- 因此生产者和消费者连接RMQ Server时,需要指定一个virtual host

*RMQ类似于网络组网,总RMQServer即三层交换机,MiniRMQServer即下层的交换机,消费者则为最下层的各主机

##### 创建RMQ服务器端 通过docker
基于docker，创建docker-compose.yml

version: '3.1'
services:
  rabbitmq:
    restart: always
    image: rabbitmq:management
    container_name: rabbitmq
    ports:
      - 5672:5672
      - 15672:15672
    environment:
      TZ: Asia/Shanghai
      RABBITMQ_DEFAULT_USER: rabbit
      RABBITMQ_DEFAULT_PASS: dhnB0v42aAVs
    volumes:
      - ./data:/var/lib/rabbitmq
# 注意修改用户名密码和容器位置即可

RMQ默认地址:http://3.113.65.65:15672


##### 使用rabbitMQ    基于spring提供的amqpTemplate
服务提供者部分:
1.创建配置类RabbitConfiguration,在其中创建队列
2.创建服务提供者RabbitProvider,使用amqpTemplate发送至rabbitMQ服务器端
3.创建配置文件yml,告知amqpTemplate服务器端的地址
*可以使用此处使用测试类进行消息发送的测试

服务消费者部分:
1.创建RabbitConsumer,使用注解@RabbitListener(queues = "hello-rabbit")监听队列
2.通过@RabbitHandler 创建方法，对监听的消息进行消费


================================================================================

#### Quartz 任务调度
使用cron表示，指定一个计划周期来执行任务  spring提供了Quartz的配套模板
-- cron表达式：通过规定的式子来表达执行周期，可以使用工具插件

开启步骤：
1.入口类添加@EnableScheduling 开启任务调度
2.创建Quartz的执行类  方法上添加    @Scheduled(cron = "0/2 * * * * ?")，通过cron表达式来指定执行周期
cron生成器：http://cron.qqe2.com/


================================================================================











*尝试实现网关聚合














================================================================================
================================================================================
