# 社区项目

## 工具

Spring Boot

Git/GitHub

Visual Paradigm 社区版 : 用来画图

BootStrap 整合好的css和js 前端框架

OkHttp 代替client http

fastjson 快速把java对象转换为json

MySQL

H2数据库


## 资料

如何用Spring Boot启动一个web：[Spring web](https://spring.io/guides/gs/serving-web-content/)

如何用Git将项目push到GitHub：[Git](https://www.runoob.com/manual/git-guide/)

登陆功能 

1. 使用bootstrap框架 快速搭建：[Bootstrap 文档](https://v3.bootcss.com/getting-started/)
2. 接入GitHub登录功能： [GitHub OAuth app](https://developer.github.com/apps/building-oauth-apps/authorizing-oauth-apps/)
    使用的是 OAuth 授权机制：[OAuth 简介](http://www.ruanyifeng.com/blog/2019/04/oauth_design.html)
3. 设置session和cookie
    把GitHub返回给服务器的user对象添加到session中，Spring自动为其生成cookie返回到前端
4. MySQL的基本使用CURD：[MySQL 菜鸟教程](https://www.runoob.com/mysql/mysql-tutorial.html)
    但是本项目实际使用的是h2数据库，[h2 快速部署](http://www.h2database.com/html/quickstart.html) h2的好处是，体积小，可以直接通
    过导入jar包的形式导入到项目中，省去了很多配置的成本。
    需要注意的是**IDEA其实自带了链接数据库的功能，不需要下载Navicat等第三方软件**。
5. mybatis Spring boot 整合，需要一个jar包**MyBatis Spring Boot Starter**
    其中在链接数据库时自动寻找Springboot官方支持的连接池配置，[详情见Springboot文档](https://docs.spring.io/spring-boot/docs/2.2.6.RELEASE/reference/html/spring-boot-features.html#boot-features-embedded-database-support)
    
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