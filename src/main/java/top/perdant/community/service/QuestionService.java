package top.perdant.community.service;

import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.perdant.community.dto.PaginationDTO;
import top.perdant.community.dto.QuestionDTO;
import top.perdant.community.dto.QuestionQueryDTO;
import top.perdant.community.exception.CustomizeErrorCode;
import top.perdant.community.exception.CustomizeException;
import top.perdant.community.mapper.QuestionExtMapper;
import top.perdant.community.mapper.QuestionMapper;
import top.perdant.community.mapper.UserMapper;
import top.perdant.community.model.Question;
import top.perdant.community.model.QuestionExample;
import top.perdant.community.model.User;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class QuestionService {
    @Autowired
    UserMapper userMapper;

    @Autowired
    QuestionMapper questionMapper;

    @Autowired
    QuestionExtMapper questionExtMapper;

    /**
     * 根据当前页码和一页展示的问题数，返回当前页面的所有问题信息 和 页码的信息
     *
     * @param page 当前的页码
     * @param size 每页展示的问题数
     * @return PaginationDTO  包装了 当前页码对应的所有问题 当前页码前后的页码数 是否展示下一页 上一页 尾页 首页的标识
     */
    public PaginationDTO list(String search, Integer page, Integer size) {
        if (StringUtils.isNotBlank(search)) {
            String[] keywords = StringUtils.split(search, " ");
            search = Arrays.stream(keywords).collect(Collectors.joining("|"));
        }

        PaginationDTO<QuestionDTO> paginationDTO = new PaginationDTO<>();
        // 所有问题的总数量
        QuestionQueryDTO questionQueryDTO = new QuestionQueryDTO();
        questionQueryDTO.setSearch(search);
        Integer totalCount = questionExtMapper.countBySearch(questionQueryDTO);

        // 设置页码
        paginationDTO.setPagination(totalCount, page, size);
        // 判断传递的 page 是否超出范围
        if (page < 1) {
            page = 1;
        }
        if (page > paginationDTO.getTotalPage()) {
            page = paginationDTO.getTotalPage();
        }

        // 传递给 sql 的参数应该是查询开始的位置 offset ，和查询的个数 size
        // 根据当前页码 page 计算出 offset
        Integer offset = size * (page - 1);
        // 问题列表
        questionQueryDTO.setPage(offset);
        questionQueryDTO.setSize(size);
        List<Question> questions = questionExtMapper.selectBySearch(questionQueryDTO);
        // 组合：问题 + 提问者信息列表
        List<QuestionDTO> questionDTOList = new ArrayList<>();
        for (Question question : questions) {
            User user = userMapper.selectByPrimaryKey(question.getCreator());
            QuestionDTO questionDTO = new QuestionDTO();
            // 直接使用 Spring 自带的方法，将 question 里的各种属性 传递给 questionDTO
            // 省去了 set get 操作
            BeanUtils.copyProperties(question, questionDTO);
            questionDTO.setUser(user);
            questionDTOList.add(questionDTO);
        }
        paginationDTO.setData(questionDTOList);
        return paginationDTO;
    }

    /**
     * 用户详情页面展示该用户提出的问题列表
     * @param userId
     * @param page
     * @param size
     * @return
     */
    public PaginationDTO list(Long userId, Integer page, Integer size) {
        PaginationDTO<QuestionDTO> paginationDTO = new PaginationDTO<>();
        // 所有问题的总数量
        QuestionExample example = new QuestionExample();
        example.createCriteria()
                .andCreatorEqualTo(userId);
        Integer totalCount = (int) questionMapper.countByExample(example);
        // 设置页码
        paginationDTO.setPagination(totalCount, page, size);

        if (page < 1) {
            page = 1;
        }
        if (page > paginationDTO.getTotalPage()) {
            page = paginationDTO.getTotalPage();
        }

        // 传递给 sql 的参数应该是查询开始的位置 offset ，和查询的个数 size
        // 根据当前页码 page 计算出 offset
        Integer offset = size * (page - 1);
        // 问题列表
        QuestionExample questionExample = new QuestionExample();
        questionExample.setOrderByClause("gmt_create desc");
        questionExample.createCriteria()
                .andCreatorEqualTo(userId);
        List<Question> questions = questionMapper.selectByExampleWithRowbounds(questionExample, new RowBounds(offset, size));
        // 组合：问题 + 提问者信息列表
        List<QuestionDTO> questionDTOList = new ArrayList<>();
        for (Question question : questions) {
            User user = userMapper.selectByPrimaryKey(question.getCreator());
            QuestionDTO questionDTO = new QuestionDTO();
            // 直接使用 Spring 自带的方法，将 question 里的各种属性 传递给 questionDTO
            // 省去了 set get 操作
            BeanUtils.copyProperties(question, questionDTO);
            questionDTO.setUser(user);
            questionDTOList.add(questionDTO);
        }
        paginationDTO.setData(questionDTOList);
        return paginationDTO;
    }

    public QuestionDTO getById(Long id) {
        Question question = questionMapper.selectByPrimaryKey(id);
        if (question == null) {
            throw new CustomizeException(CustomizeErrorCode.QUESTION_NOT_FOUND);
        }
        QuestionDTO questionDTO = new QuestionDTO();
        BeanUtils.copyProperties(question, questionDTO);
        User user = userMapper.selectByPrimaryKey(question.getCreator());
        questionDTO.setUser(user);
        return questionDTO;
    }

    /**
     * 整个过程和 userService 的 createOrUpdate 类似
     *
     * @param question
     */
    public void createOrUpdate(Question question) {
        // id 为空，说明是第一次发布的问题，第一次发布的问题不会传递 id
        if (question.getId() == null) {
            // 插入
            question.setGmtCreate(System.currentTimeMillis());
            question.setGmtModified(question.getGmtCreate());
            question.setViewCount(0);
            question.setCommentCount(0);
            questionMapper.insertSelective(question);
        } else {
            // 更新 不能简单的使用 updateByPrimaryKey
            // 因为 question 只是记录了 常见的修改属性 title description tag GmtModified
            // 而其他的属性 GmtCreate viewCount commentCount 等等 都需要保留原来数据库里面的
            // 不然的话，没有赋值的属性默认为 null，null 也会更新进数据库
            question.setGmtModified(System.currentTimeMillis());
            // 此方法没有赋值的属性为 null 的值不会更新进数据库
            int updated = questionMapper.updateByPrimaryKeySelective(question);
            // 判断是否更新成功
            if (updated != 1){
                throw new CustomizeException(CustomizeErrorCode.QUESTION_NOT_FOUND);
            }
        }
    }

    /**
     * 增加问题的阅读数
     * @param id
     */
    public void incView(Long id) {
        Question record = new Question();
        record.setId(id);
        record.setViewCount(1);
        questionExtMapper.incView(record);
    }

    /**
     * 查询包含相同标签的问题
     * @param queryDTO
     * @return
     */
    public List<QuestionDTO> selectRelated(QuestionDTO queryDTO) {
        String tag = queryDTO.getTag();
        if (StringUtils.isBlank(tag)) {
            return new ArrayList<>();
        } else {
            String[] tags = StringUtils.split(tag, ",");
            String regexpTag = Arrays.stream(tags).collect(Collectors.joining("|"));
            Question question = new Question();
            question.setId(queryDTO.getId());
            question.setTag(regexpTag);
            List<Question> questions = questionExtMapper.selectRelated(question);
            List<QuestionDTO> questionDTOs = questions.stream().map(q -> {
                QuestionDTO questionDTO = new QuestionDTO();
                BeanUtils.copyProperties(q, questionDTO);
                return questionDTO;
            }).collect(Collectors.toList());
            return questionDTOs;
        }
    }
}
