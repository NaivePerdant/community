package top.perdant.community.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import top.perdant.community.dto.QuestionDTO;
import top.perdant.community.mapper.QuestionMapper;
import top.perdant.community.model.Question;
import top.perdant.community.model.User;
import top.perdant.community.service.QuestionService;

import javax.servlet.http.HttpServletRequest;

/**
 * 问题的查看发布修改API
 *
 * @author perdant
 */
@Controller
@RequestMapping("/publish")
public class PublishController {

    @Autowired
    QuestionMapper questionMapper;

    @Autowired
    QuestionService questionService;

    /**
     * 点击编辑按钮，返回一个id ，根据 id 找到要编辑的问题
     * 把问题重新写回浏览器
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public String edit(@PathVariable(name = "id") Long id,
                       Model model){
        QuestionDTO question = questionService.getById(id);
        model.addAttribute("title",question.getTitle());
        model.addAttribute("description",question.getDescription());
        model.addAttribute("tag",question.getTag());
        // 找到每个问题的唯一标识
        model.addAttribute("id",question.getId());
        return "publish";
    }

    @GetMapping()
    public String publish(){
        return "publish";
    }

    @PostMapping()
    public String doPublish(
            @RequestParam(value = "title",required = false) String title,
            @RequestParam(value = "description",required = false) String description,
            @RequestParam(value = "tag",required = false) String tag,
            @RequestParam(value = "id",required = false) Long id,
            HttpServletRequest request,
            Model model){

        model.addAttribute("title",title);
        model.addAttribute("description",description);
        model.addAttribute("tag",tag);

        // 正常情况 下面的判空应该放到前端，用js来校验
        if (title == null || title == ""){
            model.addAttribute("error","标题不能为空");
            return "publish";
        }
        if (description == null || description == ""){
            model.addAttribute("error","问题补充不能为空");
            return "publish";
        }
        if (tag == null || tag == ""){
            model.addAttribute("error","标签不能为空");
            return "publish";
        }

        // 为了拿到question的id，首先拿到user
        User user = (User)request.getSession().getAttribute("user");
        // 如果没找到user 添加错误信息给model，用来给页面显示错误信息
        // 这是前后端没有分离导致的笨拙方法
        if (user == null){
            model.addAttribute("error","用户未登录");
            // 没发布成功 还在此页面 并且弹出错误信息
            return "publish";
        }
        // 给 question 赋值
        Question question = new Question();
        question.setTitle(title);
        question.setDescription(description);
        question.setTag(tag);
        // 把 user Id 赋给 creator
        question.setCreator(user.getId());
        question.setId(id);
        // insert into question
        questionService.createOrUpdate(question);
        // 发布成功的话返回首页
        return "redirect:/";
    }
}
