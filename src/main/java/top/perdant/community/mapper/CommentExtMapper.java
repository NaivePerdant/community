package top.perdant.community.mapper;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;
import top.perdant.community.model.Comment;
import top.perdant.community.model.CommentExample;
import top.perdant.community.model.Question;

import java.util.List;

public interface CommentExtMapper {
    int incCommentCount(Comment comment);
}