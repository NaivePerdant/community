package top.perdant.community.controller;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import top.perdant.community.dto.CommentCreateDTO;
import top.perdant.community.dto.CommentDTO;
import top.perdant.community.dto.ResultDTO;
import top.perdant.community.enums.CommentTypeEnum;
import top.perdant.community.exception.CustomizeErrorCode;
import top.perdant.community.model.Comment;
import top.perdant.community.model.User;
import top.perdant.community.service.CommentService;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

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
    public Object post(@RequestBody CommentCreateDTO commentCreateDTO,
                       HttpServletRequest request){
        User user = (User) request.getSession().getAttribute("user");
        if (null == user) {
            return ResultDTO.errorOf(CustomizeErrorCode.NO_LOGIN);
        }
        if (null == commentCreateDTO || StringUtils.isBlank(commentCreateDTO.getContent())) {
            return ResultDTO.errorOf(CustomizeErrorCode.COMMENT_NOT_EMPTY);
        }
        Comment comment = new Comment();
        comment.setParentId(commentCreateDTO.getParentId());
        comment.setContent(commentCreateDTO.getContent());
        comment.setType(commentCreateDTO.getType());
        comment.setCommentator(user.getId());
        comment.setGmtModified(System.currentTimeMillis());
        comment.setGmtCreate(System.currentTimeMillis());
        comment.setLikeCount(0L);
        commentService.insert(comment);
        return ResultDTO.okOf();
    }

    @RequestMapping(value = "/comment/{id}",method = RequestMethod.GET)
    @ResponseBody
    public ResultDTO comment(@PathVariable(name = "id") Long id) {
        List<CommentDTO> commentDTOS = commentService.listByTargetId(id, CommentTypeEnum.COMMENT);
        return ResultDTO.okOf(commentDTOS);
    }
}
