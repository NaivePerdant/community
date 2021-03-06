package top.perdant.community.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import top.perdant.community.dto.PaginationDTO;
import top.perdant.community.service.QuestionService;

/**
 * 首页，分页展示论坛内容API
 *
 * @authro perdant
 */
@Controller
public class IndexController {

    @Autowired
    QuestionService questionService;

    @GetMapping("/")
    public String index(@RequestParam(name = "page", defaultValue = "1") Integer page,
                        @RequestParam(name = "size", defaultValue = "5") Integer size,
                        @RequestParam(name = "search", required = false) String search,
                        Model model) {
        PaginationDTO paginationDTO = questionService.list(search, page, size);
        model.addAttribute("pagination", paginationDTO);
        model.addAttribute("search", search);
        return "index";
    }

}
