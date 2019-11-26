# Spring Cloud 入门项目
一个基于Spring Boot + Spring Cloud的综合练习微服务项目，从项目中能详细了解企业开发时从立项到上线的基本流程和相对完整的设计方式。


#### 部署环境
操作系统：Amazon AWS 服务器（1C1G） Ubuntu 18
虚拟化技术：Docker（通过Docker-Compose配置容器）
数据库：MariaDB
Java Version：12(原使用1.8，因需要部署至Docker-OpenJDK11容器中；已解决javax.xml.bind.JAXBContext的依赖问题)

#### 项目工具
开发工具：Intellij IDEA
项目构建：Maven
代码仓库：GitHub


#### 后台主要技术栈
核心框架：Spring Boot + Spring Cloud
视图框架：Spring MVC
页面引擎：Thymeleaf
ORM 框架：tk.mybatis（通用mybatis）
数据库连接池：Hikari
数据库缓存：Redis（集群部署）
反向代理负载均衡：Nginx
接口文档引擎：Swagger2
分布式链路追踪：ZipKin
分布式文件系统：FastDFS
分布式服务监控：Spring Boot Admin
分布式协调系统：Spring Cloud Eureka
分布式配置中心：Spring Cloud Config


#### 前端主要技术栈
前端框架：JavaScript + Vue（Core + Axios库）
前端模板：AdminLTE


#### 模块规划
服务名称				服务说明
-Cloud
itoken-eureka			服务注册与发现
itoken-config			分布式配置中心
itoken-zipkin			分布式链路追踪
itoken-zuul			分布式路由网关
itoken-admin			分布式系统监控

-Service
itoken-service-admin		管理员服务提供者
itoken-service-redis		数据缓存服务提供者
itoken-service-sso		单点登录服务提供者
itoken-service-posts		文章服务提供者
itoken-service-upload		文件上传服务提供者
itoken-service-digiccy		数字货币服务提供者
itoken-service-collection	数据采集服务提供者

-Web
itoken-web-admin		管理员服务消费者
itoken-web-posts		文章服务消费者
itoken-web-backend		后台服务聚合
