package top.perdant.community.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import top.perdant.community.dto.CommentDTO;
import top.perdant.community.mapper.CommentMapper;
import top.perdant.community.model.Comment;

/**
 * 问题评论/回复 API
 *
 * @author perdant
 */
@Controller
public class CommentController {
    @Autowired
    private CommentMapper commentMapper;
    /**
     * 使用JSON格式返回结果
     * 优化：里面的大部分逻辑可以写在service层！
     *
     * @return
     */
    @RequestMapping(value = "/comment",method = RequestMethod.POST)
    @ResponseBody
    public Object post(@RequestBody CommentDTO commentDTO){
        Comment comment = new Comment();
        comment.setParentId(commentDTO.getParentId());
        comment.setContent(commentDTO.getContent());
        comment.setType(commentDTO.getType());
        comment.setGmtModified(System.currentTimeMillis());
        comment.setGmtCreate(System.currentTimeMillis());
        comment.setCommentator(3);
        commentMapper.insert(comment);
        return null;
    }
}
