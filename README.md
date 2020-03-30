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

## 步骤

登陆功能 

1. 启动一个web 服务器 创建首页

    创建 **index.html** 新增 **IndexController类(\index)**
    
    使用bootstrap框架 快速搭建：[Bootstrap 文档](https://v3.bootcss.com/getting-started/)

2. 新增接入GitHub登录功能： [GitHub OAuth app](https://developer.github.com/apps/building-oauth-apps/authorizing-oauth-apps/)
    
    GitHub Authoring OAuth 的工作流程：
    
        - 去GitHub申请一个client_id 和 client_secret
        - 点击登录 通过`GET https://github.com/login/oauth/authorize` （在url中有几个参数，如client_id、
        redirect_uri、state等） 会访问一个GitHub授权的页面，点击确认授权之后，会跳转到redirect_uri指定的地址(\callback)，
        所以我们要编写一个**AuthorizeController类(\callback)**，从该地址中获取到一个临时的code和state
        - 通过 `POST https://github.com/login/oauth/access_token`（url中有几个参数: client_id client_secret code redirect_uri state
        将这5个参数封装成**AccessTokenDTO类**) 获取到token
        - 通过 `GET https://api.github.com/user` 发送上一步的token 获取到user信息
        - 最后两步使用了OkHttp，新建**GitHubProvider类** 更方便的完成GET POST，新建**GitHubUser类**封装获取到的user信息
        
    延伸：OAuth 授权机制：[OAuth 简介](http://www.ruanyifeng.com/blog/2019/04/oauth_design.html)
 
3. 设置session和cookie 拿到GitHubUser信息已经说明我们登录成功了，但是当我们重启web会继续重复授权过程

    为了保证登录的持久化，类似现在大多数网站的自动登录功能，我们要引入session和cookie功能

    服务器将为user对象自动生成一个token，以token(key),user(value) 的格式存入session保存在服务器中
    
    并且将token返回给浏览器，浏览器将token保存在cookie中
    
    当下次登录时，浏览器会先去寻找cookie中是否有token，如果有，将token发送给服务器，服务器通过token寻找对应的user信息
    
    如果存在对应的user，说明登录成功
    
4. 上述过程，需要**User类**封装token和GitHubUser信息
        
    需要一个数据库COMMUNITY 并新建**表user table**保存token和GitHubUser，为了尽可能的简单，使用h2数据库，h2数据库的优势在于嵌入式、体积小，
    可以直接通过导jar包的形式加载进项目，缺点是，同一时间只能链接一个connection

    延伸：MySQL的基本使用CURD：[MySQL 菜鸟教程](https://www.runoob.com/mysql/mysql-tutorial.html)

    但是本项目实际使用的是h2数据库，[h2 快速部署](http://www.h2database.com/html/quickstart.html) 
    
    需要注意的是**IDEA其实自带了链接数据库的功能，不需要下载Navicat等第三方软件**
    
5. 将User类写入user table需要用到 **Mybatis** 新建 **UserMapper接口** 实现 CURD
    
    Spring Boot 有快速配置 mybatis 的方法 mybatis Spring boot 整合，需要一个jar包MyBatis Spring Boot Starter

    其中在链接数据库时自动寻找Springboot官方支持的连接池配置，[详情见Springboot文档](https://docs.spring.io/spring-boot/docs/2.2.6.RELEASE/reference/html/spring-boot-features.html#boot-features-embedded-database-support)
    
6. 通过使用数据库记录user的token，实现了登录的持久化：

    将GitHubUser信息保存在数据库中，并添加一个token

    将token保存到cookie中
    
    登录时，先检查cookie中的token，去数据库中寻找对应的token，如果找到，取出对应的GitHubUser 说明登录成功，
    
    通过对index.html添加 `th:if="${session.user != null}` 语句展现出已经登录的样式，从而使浏览器保持登录状态 
    
7. 使用flyway优化数据库版本整合 如果在团队编程时，一个人对数据库进行了修改，其他人在本地也需要使用相同的ddl语句对数据库
做同样的修改，这样很麻烦，flyway通过把ddl按照修改的顺序写成文件，其他人pull到本地后，通过一句指令直接执行所有的数据库操作，非常的方便。

8. 添加发布问题功能 思路就是：

    - 写一个publish.html 需要添加问题 问题描述 标签 然后点击发布即可将问题发布出去
    - 将问题：title、问题描述：description、标签：tag 与user等其他信息封装成一个**Question类**
    - 数据库中新建一个**表question table**通过mybatis将Question存入其中，所以需要新建一个**QuestionMapper接口**
    - 新建一个**PublishController类(/publish)** GET 直接返回publish.html，POST 将 title description tag 以及之前的user信息
    传递给question，将question插入question table
    - 处理一些判空的情况，user title description tag 都不能为空


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