# 社区项目

## 工具

Spring Boot

Git/GitHub

Visual Paradigm 社区版 : 用来画图

BootStrap 整合好的 css 和 js 前端框架

OkHttp 代替 client http

thymeleaf 模版

fastJson  完成 java json 的快速转换

MySQL 数据库

H2 数据库

flyway 方便团队编程时数据库的修改和合并


## 资料

如何用 Spring Boot 启动一个web：[Spring web](https://spring.io/guides/gs/serving-web-content/)

如何用 Git 将项目 push 到 GitHub：[Git](https://www.runoob.com/manual/git-guide/)

OAuth 授权机制：[OAuth 简介](http://www.ruanyifeng.com/blog/2019/04/oauth_design.html)

使用 bootstrap 框架 快速搭建：[Bootstrap 文档](https://v3.bootcss.com/getting-started/)

MySQL的基本使用CURD：[MySQL 菜鸟教程](https://www.runoob.com/mysql/mysql-tutorial.html)

## 步骤

1. 启动一个 web 服务器 创建首页

    使用 bootstrap 很方便的创建首页 **index.html** 新增 **IndexController 类(\index)**

2. 新增接入 GitHub 登录功能： [GitHub OAuth app](https://developer.github.com/apps/building-oauth-apps/authorizing-oauth-apps/)
    
    GitHub Authoring OAuth 的工作流程：
    
    1. 去 GitHub 申请一个 client_id 和 client_secret
    
    2. 点击登录 通过`GET https://github.com/login/oauth/authorize` （在 url 中有几个参数，如 client_id、
    
    redirect_uri、state 等） 会访问一个 GitHub 授权的页面，点击确认授权之后，会跳转到 redirect_uri 指定的地址(\callback)，
    
    所以我们要编写一个 **AuthorizeController 类(\callback)**，从该地址中获取到一个临时的 code 和 state
    
    3. 通过 `POST https://github.com/login/oauth/access_token`（ url 中有几个参数: client_id client_secret code redirect_uri state
    
    将这5个参数封装成 **AccessTokenDTO 类**) 获取到 token
    
    4. 通过 `GET https://api.github.com/user` 发送上一步的 token 获取到 user 信息
    
    5. 最后两步使用了 OkHttp，新建 **GitHubProvider 类** 更方便的完成GET POST，新建 **GitHubUser 类**封装获取到的 user 信息
    
    [OKHttp GET POST 写法](https://square.github.io/okhttp/)
 
3. 拿到 GitHubUser 信息已经说明登录已经成功了，但是当重启 web 会继续重复授权过程

    为了保证登录的持久化，类似现在大多数网站的自动登录功能，我们要引入 session 和 cookie 功能

    服务器将为 user 对象自动生成一个 token，以 token(key), user(value) 的格式存入 session 保存在服务器中
    
    并且将 token 返回给浏览器，浏览器将 token 保存在 cookie 中
    
    当下次登录时，浏览器会先去寻找 cookie 中是否有 token，如果有，将 token 发送给服务器，服务器通过 token 寻找对应的 user 信息
    
    如果存在对应的 user，说明登录成功
    
4. 上述过程，需要 **User 类**封装 token 和 GitHubUser 信息
        
    需要一个数据库 COMMUNITY 并新建**表 user table** 保存 token 和 GitHubUser，为了尽可能的简单，使用 h2 数据库，h2 数据库的优势在于嵌入式、体积小，
    可以直接通过导 jar 包的形式加载进项目，缺点是，同一时间只能链接一个 connection [h2 快速部署](http://www.h2database.com/html/quickstart.html) 
    
    需要注意的是IDEA其实自带了链接数据库的功能，不需要下载 Navicat 等第三方软件
    
5. 将 User 类写入 user table 需要用到 Mybatis，新建 **UserMapper 接口** 实现 CURD
    
    Spring Boot 有快速配置 mybatis 的方法： mybatis Spring boot 整合，需要一个 jar 包 MyBatis Spring Boot Starter

    其中在链接数据库时自动寻找Springboot官方支持的连接池配置，[详情见Springboot文档](https://docs.spring.io/spring-boot/docs/2.2.6.RELEASE/reference/html/spring-boot-features.html#boot-features-embedded-database-support)
    
6. 通过使用数据库记录 user 的 token，实现了登录的持久化：

    将 GitHubUser 信息保存在数据库中，并添加一个 token

    将 token 保存到 cookie 中
    
    登录时，先检查 cookie 中的 token，去数据库中寻找对应的 token，如果找到，取出对应的 GitHubUser 说明登录成功，
    
    通过对 index.html 添加 `th:if="${session.user != null}` 语句展现出已经登录的样式，从而使浏览器保持登录状态 
    
7. 使用 flyway 优化数据库版本整合 [flyway maven 配置](https://flywaydb.org/getstarted/firststeps/maven)

    如果在团队编程时，一个人对数据库进行了修改，其他人在本地也需要使用相同的 ddl 语句对数据库做同样的修改，
    
    这样很麻烦，flyway 通过把 ddl 按照修改的顺序写成文件，其他人 pull 到本地后，通过一句指令直接执行所有的数据库操作，非常的方便。

8. 添加发布问题功能 思路就是：

   1. 写一个 **publish.html**，需要添加问题、问题描述、标签，然后点击发布即可将问题发布出去
   
   2. 将问题：title；问题描述：description；标签：tag 与 user 等其他信息封装成一个 **Question 类**
   
   3. 数据库中新建一个**表 question table** 通过 mybatis 将 Question 存入其中，所以需要新建一个 **QuestionMapper 接口**
   
   4. 新建一个 **PublishController 类(/publish)** GET 直接返回 publish.html，POST 将 title description tag 以及之前的 user 信息
   
    传递给 question，将 question 插入 question table
    
   5. 处理一些判空的情况，user title description tag 都不能为空


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