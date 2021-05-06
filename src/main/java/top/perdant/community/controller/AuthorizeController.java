package top.perdant.community.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import top.perdant.community.dto.AccessTokenDTO;
import top.perdant.community.dto.GitHubUser;
import top.perdant.community.model.User;
import top.perdant.community.provider.GitHubProvider;
import top.perdant.community.service.UserService;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.UUID;

/**
 * 第三方认证登陆API
 *
 * @author perdant
 */
@Controller
@Slf4j
public class AuthorizeController {

    @Autowired
    private GitHubProvider gitHubProvider;

    @Value("${github.client.id}")
    private String clientId;

    @Value("${github.client.secret}")
    private String clientSecret;

    @Value("${github.redirect.uri}")
    private String redirectUri;

    @Autowired
    private UserService userService;

    /**
     * 点击登陆
     * 向GitHub服务器发送id后，GitHub会返回code并且跳转到/callback
     * 此方法就是从url的参数中拿到code，通过httpClient发送给GitHub 获取token
     * 然后再次通过httpClient发送给GitHub 获取user信息，完成第三方授权！
     *
     * @param code
     * @param state
     * @param response
     * @return
     */
    @GetMapping("/callback")
    public String callback(@RequestParam(name="code") String code,
                           @RequestParam(name="state") String state,
                           // 可以写一个BaseController把公有字段比如request和response放进去
                           HttpServletResponse response){
        // 把 request body 里的参数封装（建造者模式比set更方便）
        AccessTokenDTO accessTokenDTO = AccessTokenDTO.builder().client_id(clientId).client_secret(clientSecret)
                                                      .code(code).redirect_uri(redirectUri).state(state).build();
        // POST https://github.com/login/oauth/access_token 获取token
        String accessToken = gitHubProvider.getAccessToken(accessTokenDTO);
        // GET https://api.github.com/user 获取user信息
        GitHubUser gitHubUser = gitHubProvider.getUser(accessToken);
        if (gitHubUser != null && gitHubUser.getId() != null){
            User user = new User();
            // 服务器默认自动生成一个sessionID存入servlet默认的session中，不采用，
            // 自己生成一组sessionID，保存到自定义的h2数据库中
            String token = UUID.randomUUID().toString();
            user.setToken(token);
            // 获取到的GitHubUser信息封装到user中
            user.setAccountId(String.valueOf(gitHubUser.getId()));
            user.setName(gitHubUser.getName());
            user.setAvatarUrl(gitHubUser.getAvatarUrl());
            // user如果已经存在就更新，不存在就新增
            userService.createOrUpdate(user);
            // 将sessionID封装成cookie放入response返回给浏览器
            response.addCookie(new Cookie("token",token));
            log.info("callback success redirect to index");
            // 重定向：如果直接写 / 会导致地址不变，页面渲染成首页。使用redirect可以让地址也变成首页
            return  "redirect:/";
        }else {
            // 以此为例，打印日志，需要注解 @Slf4j
            log.error("callback get github error, {}", gitHubUser);
            // 登录失败
            return "redirect:/";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request,HttpServletResponse response){
        request.getSession().removeAttribute("user");
        Cookie cookie = new Cookie("token",null);
        cookie.setMaxAge(0);
        response.addCookie(cookie);
        return "redirect:/";
    }
}
