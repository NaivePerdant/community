package top.perdant.community.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import top.perdant.community.dto.AccessTokenDTO;
import top.perdant.community.dto.GitHubUser;
import top.perdant.community.mapper.UserMapper;
import top.perdant.community.model.User;
import top.perdant.community.provider.GitHubProvider;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

@Controller
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
    private UserMapper userMapper;

    @GetMapping("/callback")
    public String callback(@RequestParam(name="code") String code,
                           @RequestParam(name="state") String state,
                           HttpServletRequest request,
                           HttpServletResponse response){
        // 将要传递给GitHub的参数封装进accessToken 除了 code 和 state 是从request中获取的
        // 其他的都是提前申请好放进application中
        AccessTokenDTO accessTokenDTO = new AccessTokenDTO();
        accessTokenDTO.setClient_id(clientId);
        accessTokenDTO.setClient_secret(clientSecret);
        accessTokenDTO.setCode(code);
        accessTokenDTO.setRedirect_uri(redirectUri);
        accessTokenDTO.setState(state);
        // POST https://github.com/login/oauth/access_token 获取token
        String accessToken = gitHubProvider.getAccessToken(accessTokenDTO);
        // GET https://api.github.com/user 获取user信息
        GitHubUser gitHubUser = gitHubProvider.getUser(accessToken);
        // 登录成功 有时候重置了client.secret 虽然获取到了gitHubUser但是里面的id是空的
        if (gitHubUser != null && gitHubUser.getId() != null){
            // 手动模拟 session 和 cookie 从而实现持久化登录
            // 将user信息写入数据库,这个数据库模拟服务器的session
            User user = new User();
            // 生成一个唯一标识token
            String token = UUID.randomUUID().toString();
            user.setToken(token);
            // 获取到的GitHubUser信息封装到user中
            user.setAccountId(String.valueOf(gitHubUser.getId()));
            user.setName(gitHubUser.getName());
            user.setGmtCreate(System.currentTimeMillis());
            user.setGmtModified(user.getGmtCreate());
            // user 添加到数据库
            userMapper.insert(user);
            // 将 token 写入一个 HttpServletResponse 自带的 cookie
            // 通过 response 返回给浏览器 所以 cookie 是存储在浏览器中的
            response.addCookie(new Cookie("token",token));
//            // 使用自带的 session 将 gitHubUser 自动生成的 JSESSIONID 写入到 HttpServletRequest 自带的 session
//            request.getSession().setAttribute("user",gitHubUser);
            return  "redirect:/";
        }else {
            // 登录失败
            return "redirect:/";
        }
    }
}
