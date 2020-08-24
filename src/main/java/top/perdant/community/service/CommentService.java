package top.perdant.community.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.perdant.community.enums.CommentTypeEnum;
import top.perdant.community.exception.CustomizeErrorCode;
import top.perdant.community.exception.CustomizeException;
import top.perdant.community.mapper.CommentMapper;
import top.perdant.community.mapper.QuestionExtMapper;
import top.perdant.community.mapper.QuestionMapper;
import top.perdant.community.model.Comment;
import top.perdant.community.model.Question;

@Service
public class CommentService {
    @Autowired
    private CommentMapper commentMapper;
    @Autowired
    private QuestionMapper questionMapper;
    @Autowired
    private QuestionExtMapper questionExtMapper;

    @Transactional
    public void insert(Comment comment) {
        if (null == comment.getParentId() || comment.getParentId() == 0) {
            throw new CustomizeException(CustomizeErrorCode.TARGET_PARAM_NOT_FOUND);
        }
        if (null == comment.getType() || !CommentTypeEnum.isExist(comment.getType())) {
            throw new CustomizeException(CustomizeErrorCode.TYPE_PARAM_WRONG);
        }
        if (comment.getType().equals(CommentTypeEnum.COMMENT.getType())) {
            // 回复评论
            Comment dbComment = commentMapper.selectByPrimaryKey(comment.getParentId());
            if (null == dbComment) {
                throw new CustomizeException(CustomizeErrorCode.COMMENT_NOT_FOUND);
            }
            else {
                commentMapper.insert(comment);
            }
        }
        else {
            // 回复问题
            Question question = questionMapper.selectByPrimaryKey(comment.getParentId());
            if (null == question) {
                throw new CustomizeException(CustomizeErrorCode.QUESTION_NOT_FOUND);
            }
            else {
                commentMapper.insert(comment);
                question.setCommentCount(1);
                questionExtMapper.incCommentCount(question);
            }
        }
    }
}
