package top.perdant.community.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import top.perdant.community.dto.CommentDTO;
import top.perdant.community.dto.ResultDTO;
import top.perdant.community.exception.CustomizeErrorCode;
import top.perdant.community.model.Comment;
import top.perdant.community.model.User;
import top.perdant.community.service.CommentService;

import javax.servlet.http.HttpServletRequest;

/**
 * 问题评论/回复 API
 *
 * @author perdant
 */
@Controller
public class CommentController {
    @Autowired
    private CommentService commentService;

    /**
     * 使用JSON格式返回结果
     * 优化：里面的大部分逻辑可以写在service层！
     *
     * @return
     */
    @RequestMapping(value = "/comment",method = RequestMethod.POST)
    @ResponseBody
    public Object post(@RequestBody CommentDTO commentDTO,
                       HttpServletRequest request){
        User user = (User) request.getSession().getAttribute("user");
        if (null == user) {
            return ResultDTO.errorOf(CustomizeErrorCode.NO_LOGIN);
        }
        Comment comment = new Comment();
        comment.setParentId(commentDTO.getParentId());
        comment.setContent(commentDTO.getContent());
        comment.setType(commentDTO.getType());
        comment.setGmtModified(System.currentTimeMillis());
        comment.setGmtCreate(System.currentTimeMillis());
        comment.setCommentator(3L);
        commentService.insert(comment);
        return ResultDTO.okOf();
    }
}
