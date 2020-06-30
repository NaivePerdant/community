# 社区项目

## 技术栈

Spring Boot

Git/GitHub

Visual Paradigm 社区版 : 用来画图

BootStrap 整合好的 css 和 js 前端框架

OkHttp 代替 client http

thymeleaf 模版引擎 类似 JSP

fastJson  完成 java json 的快速转换

MySQL 数据库

H2 数据库

flyway 方便团队编程时数据库的修改和合并

jQuery 封装了很多方法，更方便的操作js

postman 方便测试 http method

## 资料

如何用 Spring Boot 启动一个web：[Spring web](https://spring.io/guides/gs/serving-web-content/)

[Spring Boot 文档](https://docs.spring.io/spring-boot/docs/2.2.6.RELEASE/reference/htmlsingle/)

如何用 Git 将项目 push 到 GitHub：[Git](https://www.runoob.com/manual/git-guide/)

OAuth 授权机制：[OAuth 简介](http://www.ruanyifeng.com/blog/2019/04/oauth_design.html)

使用 bootstrap 框架 快速搭建：[Bootstrap 文档](https://v3.bootcss.com/getting-started/)

MySQL的基本使用CURD：[MySQL 菜鸟教程](https://www.runoob.com/mysql/mysql-tutorial.html)

## 步骤

- 启动一个 web 服务器 创建首页。使用 bootstrap 很方便的创建首页 **index.html** 新增 **IndexController 类(\index)**
- 新增接入 GitHub 登录功能： [GitHub OAuth app](https://developer.github.com/apps/building-oauth-apps/authorizing-oauth-apps/)
    **GitHub Authoring OAuth 的工作流程：**
    1. 去 GitHub 申请一个 client_id 和 client_secret
    2. 点击登录 通过`GET https://github.com/login/oauth/authorize` （在 url 中有几个参数，如 client_id、redirect_uri、state 等） 会访问一个 GitHub 授权的页面，点击确认授权之后，会跳转到 redirect_uri 指定的地址(\callback)，所以我们要编写一个 **AuthorizeController 类(\callback)**，从该地址的url params中获取到一个临时的 code 和 state。
    3. 通过 `POST https://github.com/login/oauth/access_token`（ url 中有几个参数: client_id、client_secret、code、redirect_uri、state将这5个参数封装成 **AccessTokenDTO 类**放入request body) 获取到 token
    4. 通过 `GET https://api.github.com/user` 发送上一步的 token 获取到 user 信息
    5. 3 4两步使用了 OkHttp（简化Java的HttpClient） 和 fastjson（简化转换 JSON 格式），新建 **GitHubProvider 类** 更方便的完成GET POST[OKHttp GET POST 写法](https://square.github.io/okhttp/)，新建 **GitHubUser 类**封装获取到的 user 信息
- 拿到 GitHubUser 信息已经说明登录已经成功了，但是当重启 web 会继续重复授权过程，为了保证登录的持久化，类似现在大多数网站的自动登录功能，我们要引入 session 和 cookie 功能服务器将为 user 对象自动生成一个 token，以 "token"(key), token(value) 的格式存入 session 保存在服务器中并且将其封装成一个cookie返回给浏览器，当下次登录时，浏览器会先去寻找 cookies 中是否有 key 为 “token” 的 cookie，如果有，将 value：token 发送给服务器，服务器通过 token 寻找对应的 user 信息如果存在对应的 user，说明登录成功
- 上述过程，需要 **User 类**封装 token 和 GitHubUser 信息，需要一个数据库 COMMUNITY 并新建**表 user table** 保存 token 和 GitHubUser，为了尽可能的简单，使用 h2 数据库h2 数据库的优势在于嵌入式、体积小，可以直接通过导 jar 包的形式加载进项目，缺点是，同一时间只能链接一个 connection [h2 快速部署](http://www.h2database.com/html/quickstart.html)。 需要注意的是IDEA其实自带了链接数据库的功能，不需要下载 Navicat 等第三方软件
- 将 User 类写入 user table 需要用到 Mybatis，新建 **UserMapper 接口** 实现 CURD。Spring Boot 有快速配置 mybatis 的方法： mybatis Spring boot 整合，需要一个 jar 包 MyBatis Spring Boot Starter其中在链接数据库时自动寻找Springboot官方支持的连接池配置，[详情见Springboot文档](https://docs.spring.io/spring-boot/docs/2.2.6.RELEASE/reference/html/spring-boot-features.html#boot-features-embedded-database-support)
- 通过使用数据库记录 user 的 token，实现了登录的持久化：将 GitHubUser 信息保存在数据库中，并将 token 保存到 cookie 中，登录时，先检查 cookie 中的 token，去数据库中寻找对应的 token，如果找到，取出对应的 GitHubUser 说明登录成功，通过对 index.html 添加 [`th:if="${session.user != null}`](https://www.thymeleaf.org/doc/tutorials/3.0/usingthymeleaf.html) 语句展现出已经登录的样式，从而使浏览器保持登录状态 
- 使用 flyway 优化数据库版本整合 [flyway maven 配置](https://flywaydb.org/getstarted/firststeps/maven)如果在团队编程时，一个人对数据库进行了修改，其他人在本地也需要使用相同的 ddl 语句对数据库做同样的修改，这样很麻烦，flyway 通过把 ddl 按照修改的顺序写成文件，其他人 pull 到本地后，通过一句指令直接执行所有的数据库操作，非常的方便。
- 添加发布问题功能 思路就是：
    1. 写一个 **publish.html**，需要添加问题、问题描述、标签，然后点击发布即可将问题发布出去
    2. 将问题：title；问题描述：description；标签：tag 与 user 等其他信息封装成一个 **Question 类**
    3. 数据库中新建一个**表 question table** 通过 mybatis 将 Question 存入其中，所以需要新建一个 **QuestionMapper 接口**
    4. 新建一个 **PublishController 类(/publish)** GET 直接返回 publish.html，POST 将 title description tag 以及之前的 user 信息传递给 question，将 question 插入 question table
    5. 处理一些判空的情况，user title description tag 都不能为空
- 给 user table 添加一列 avatar_url  User 类就需要添加一条对应的属性，并且更新 set get 方法，每次添加属性都要添加 set get 方法，太麻烦了，使用 Lombok [Lombok install by maven](https://projectlombok.org/setup/maven)，Lombok 通过注解 @Data 可以自动生成 set get 方法
- 给首页添加内容，起初的首页只有导航栏，通过 bootstrap 给首页添加下面的部分——展示一些用户提出的问题需要在后端把 Question 从 question table 中提取出来，然后传递到 index.html  但是其中需要展示一部分 User 的信息，需要通过 Question 中的 creator 属性来找到 User 对应的 id，从而拿到 user 的信息，所以 需要在 Question 中加入一个 User 对象但是 Question 的属性都是和数据库属性一一对应的，所以在传输层新建一个 **QuestionDTO 类**用来传递给 Model 然后再通过 Model 返回给 index。这里又出现了一个新的问题，question table 中查到的信息只能返回给 Question，该如何返回给 QuestionDTO ？这里引出一个新的概念：**service** 在 service 层中，可以将 QuestionMapper 和 UserMapper 组装起来 一起使用使用 QuestionDTO 封装 Question 和 User ，放到一个集合 questions 中，在 index.html 中使用模版引擎 thymeleaf 快速循环遍历 questions 把其中的 title description avatarUrl 等等信息显示出来。**优化：连表查询！！！**
- 之前每一次修改代码，都需要重启项目重新访问地址，才能看到修改的效果，为了加速 加载重启 这个过程，引入一个新的工具 [Spring Develop Tools](https://docs.spring.io/spring-boot/docs/2.2.6.RELEASE/reference/htmlsingle/#using-boot-devtools)其实就是一种自动重启功能，当然之前需要 build ，相比 JRebel 的热部署，Spring Develop Tools 是会重启项目的。通过修改IDEA配置，在项目启动时也可以自动 build （只要保存，就会触发 build）然后项目就会通过 Spring Develop Tools 快速重启在配合上 [LiveReload Chrome 插件](http://livereload.com/extensions/)，可以在服务器重启的时候自动刷新浏览器页面，让整个修改的过程变得更加便捷。 
- 添加分页功能：总页数 = 数据库总记录数 / 12 向上取整,当前页码所显示的起始记录 = （当前页码 - 1）* 5，为了让前端能显示除了 question 以外的页码信息，新建 **PaginationDTO 类**
- 完善导航栏，增加下拉效果需要引入 jQuery，使用 `th:fragment` 将导航栏作为一个单独的模版，引入各个页面
- 添加 个人中心 profile.html  需要使用一个新的 Controller **ProfileController 类** 
- 在个人中心中 添加一个 "我的问题" 栏目，采用与首页展示问题类似的方式，依旧是使用之前添加分页功能时的步骤，多添加一个 userId 参数
- 当跳转不同的页面时，都需要浏览器通过 cookie session 判断一次登录状态，会造成很多冗余的代码，引入 **Interception** [参考文档](https://docs.spring.io/spring/docs/5.2.5.RELEASE/spring-framework-reference/web.html#mvc-handlermapping-interceptor)
拦截器可以在一个请求执行前，执行时，执行后 进行处理，如果返回 true 则允许请求通过，否则拒绝掉请求。
注意：拦截器有可能会拦截到 css js 等，需要把 WebConfig 上的注解 @EnableWebMvc 去掉
原因：EnableWebMvc导入DelegatingXXX（实现了 WebxxSupport），导致 WebAutoConfiguration (其中可以默认的去不过滤 static 文件夹)实例失败
- 添加编辑问题页面 **QuestionController 类**和 **question.html** 
- 修复登录功能，登录的代码逻辑有误，当获取到 GitHubUser 信息之后，应该先去数据库中查询 user table 里的 User 信息和其是否一致，一致的话就
不需要新建一条 User 数据添加进 user table 了，修复这个问题，UserMapper 就不好用了，需要新建 **UserService 类**
- 退出登录功能，需要删除浏览器中的 cookie 中的 token
- 增加问题编辑的逻辑，类似修复登录功能的步骤
- 优化之前的一个步骤：对于之前的 mapper 层，如果对数据库的 col 做了添加或者删除，数据库的映射模型会增加属性， mapper 中的方法上的注解需要重新修改 SQL 语句，
使用  [Mybatis Generator](http://mybatis.org/generator/index.html) 在修改 Question 的映射的过程中，发现之前写的分页都要改，简直反人类在 Mybatis Generator 中查找 plugins ：org.mybatis.generator.plugins.RowBoundsPlugin
有兴趣的可以研究一下这个分页插件：[GitHub pageHelper](https://github.com/pagehelper/Mybatis-PageHelper) 原理是在 mybatis 里设置一个拦截器
- 在输入地址的  和 `localhost8887/question/不存在的question` 等情况 id 会报错 展示springboot的一个默认错误页面 whitelabel Error Page，太丑了。为了防止把这些错误信息展示给client，使用 @ExceptionHandler methods 和 @ControllerAdvice 和 ErrorController 来处理异常
   **JSON restful 格式：**
  @ControllerAdvice ：类注解, 作用于整个 Spring 工程。定义了一个全局的异常处理器
  @ExceptionHandler : 方法注解, 作用范围为一个 Controller 内。为一个 Controller 定义一个异常处理器
  **HTML 格式：**
  ErrorController ：springBoot 自带默认的错误页面 "whitelabel"，想要自定义的话，只需要继承 ErrorController 类即可[参考文档](https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/#boot-features-error-handling)
  **前端：**
  白页 error.html 用来接受后端返回的modelAndView，展示错误信息message。
- 添加展示阅读数功能，不能采用先查后改的方式，要使用sql在修改的同时做 +1 操作。因为在高并发的情况下，很容易出错。


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
mvn -Dmybatis.generator.overwrite=true mybatis-generator:generate
```