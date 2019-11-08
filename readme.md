# spring cloud 测试项目
关于分布式：所有项目可单独运行，而非单个模块。
面向对象的设计:最少知识原则（Least Knowledge Principle）
(参考：https://www.cnblogs.com/gaochundong/p/least_knowledge_principle.html)
极限编程:只管当下

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

单点登录示意图          
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

关于loginController的逻辑:
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

#### 后半场的工作
1.实现Spring Cloud Config Client 通用配置
2.实现Spring Boot MyBatis Redis 二级缓存，不怎么变的
3.管理员服务、文章服务实现CRUD 功能
4.使用FastDFS 实现图片上传

#### 关于通用配置
可以通过配置通用配置文件（common-service），使一些通用的配置（如eureka、admin、zipkin等）统一归类
各项目读取时，在bootstrap.yml中加载common-service即可，如：
spring:
  cloud:
    config:
      uri: http://3.113.65.65:8888
      name: spring-cloud-web-admin-ribbon,spring-cloud-common-service-remote
      label: master
      profile: dev

*有坑，导致service-admin出现sql类型的错误，暂时禁用


#### 关于管理员服务、文章服务类的crud功能

##### 前置工作（对原有service-admin项目进行重构）
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


##### service-posts 文章服务的提供者
1.创建service-posts项目,pom内容基本基于service-admin
2.创建mapper.TbPostsPostExtendMapper(扩展mapper),和相对应的resources下的mapper.xml
3.编写service层;写接口,写实现类;
 (类似于adminController,提供保存(更新)文章 根据id获取文章 分页查询的功能;   实际工作中需要*严格*依据API文档,为前端提供功能!!)
*错误点：因框架布局混乱导致mbg生成的实体类和mapper文件混乱，最后拓展的实体类统一至common-service（基类BaseDomain位于common-domain下）
且service-posts的config只会读取数据库service-posts（service-admin相同），而讲师的mbg是全读取
因此生成的实体类文件 @Table(name = "service-posts..tb_posts_post")处不相同
















================================================================================
================================================================================
================================================================================
================================================================================
