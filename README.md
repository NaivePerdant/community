# 社区项目

## 工具

Spring Boot

Git/GitHub

Visual Paradigm 社区版 : 用来画图

BootStrap 整合好的css和js 前端框架

OkHttp 代替client http

thymeleaf 模版

fastJson 快速把java对象转换为json

MySQL

H2数据库

flyway 方便团队编程时数据库的修改和合并


## 资料

如何用Spring Boot启动一个web：[Spring web](https://spring.io/guides/gs/serving-web-content/)

如何用Git将项目push到GitHub：[Git](https://www.runoob.com/manual/git-guide/)

登陆功能 

1. 使用bootstrap框架 快速搭建：[Bootstrap 文档](https://v3.bootcss.com/getting-started/)
    
    构建首页 IndexController

2. 接入GitHub登录功能： [GitHub OAuth app](https://developer.github.com/apps/building-oauth-apps/authorizing-oauth-apps/)

    使用的是 OAuth 授权机制：[OAuth 简介](http://www.ruanyifeng.com/blog/2019/04/oauth_design.html)
    
    构建登录功能 AuthorizeController
    
3. 设置session和cookie

    把GitHub返回给服务器的user对象添加到session中，Spring自动为其生成cookie返回到前端
    
    添加h2数据库
    
4. MySQL的基本使用CURD：[MySQL 菜鸟教程](https://www.runoob.com/mysql/mysql-tutorial.html)

    但是本项目实际使用的是h2数据库，[h2 快速部署](http://www.h2database.com/html/quickstart.html) h2的好处是，体积小，可以直接通
    过导入jar包的形式导入到项目中，省去了很多配置的成本。
    
    需要注意的是**IDEA其实自带了链接数据库的功能，不需要下载Navicat等第三方软件**。
    
5. mybatis Spring boot 整合，需要一个jar包**MyBatis Spring Boot Starter**

    其中在链接数据库时自动寻找Springboot官方支持的连接池配置，[详情见Springboot文档](https://docs.spring.io/spring-boot/docs/2.2.6.RELEASE/reference/html/spring-boot-features.html#boot-features-embedded-database-support)
    
    添加 UserMapper

6. 通过使用数据库记录user的token，实现了登录的持久化：

    将user信息保存在数据库中，并添加一个token

    将token保存到cookie中
    
    在首页，先检查cookie中的token，去数据库中寻找对应的token，如果找到，取出对应的user 说明登录成功，返回前端，保持登录状态 
    
7. 使用flyway优化数据库版本整合

8. 使用bootstrap布局publish页面 添加PublishController

9. 添加问题，question table； Question 类；QuestionMapper





 ## 脚本
 ```sql
create table USER
(
	ID INT auto_increment,
	ACCOUNT_ID VARCHAR(100),
	NAME VARCHAR(50),
	TOKEN CHAR(36),
	GMT_CREATE BIGINT,
	GMT_MODIFIED BIGINT,
	constraint USER_PK
		primary key (ID)
);
```

```shell script
mvn flyway:migrate
```