package top.perdant.community.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import top.perdant.community.dto.QuestionDTO;
import top.perdant.community.mapper.UserMapper;
import top.perdant.community.model.Question;
import top.perdant.community.model.User;
import top.perdant.community.service.QuestionService;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
public class IndexController {
    @Autowired
    UserMapper userMapper;

    @Autowired
    QuestionService questionService;

    @GetMapping("/")
    public String index(HttpServletRequest request,
                        Model model) {
        // 从 cookies 里找是否有 name 为 token 的那条 cookie
        Cookie[] cookies = request.getCookies();
        if (cookies != null && cookies.length != 0) {
            for (Cookie cookie : cookies) {
                // 找到 token 的 cookie 之后，取出 token 的值
                if (cookie.getName().equals("token")) {
                    String token = cookie.getValue();
                    // 利用 token 值去数据库里找对应的 token 值的那条 user 信息
                    User user = userMapper.findByToken(token);
                    if (user != null) {
                        // 把这条 user 信息写到 session 里，发给前端
                        request.getSession().setAttribute("user", user);
                    }
                    break;
                }
            }
        }

        List<QuestionDTO> questionDTOList = questionService.list();
        for (QuestionDTO questionDTO : questionDTOList) {
            questionDTO.setDescription("测试热部署");
        }
        model.addAttribute("questions",questionDTOList);
        return "index";
    }

}
