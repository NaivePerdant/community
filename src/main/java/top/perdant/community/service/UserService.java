package top.perdant.community.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.perdant.community.mapper.UserMapper;
import top.perdant.community.model.User;
import top.perdant.community.model.UserExample;

import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;

    /**
     * 如果拿来的 user 中的 accountId  在数据库中已经存在，就只修改数据库中的 user 的 token name avatarUrl gmtModified
     * 否则，将新 user 添加进数据库
     * @param user
     */
    public void createOrUpdate(User user) {
        UserExample userExample = new UserExample();
        userExample.createCriteria()
                .andAccountIdEqualTo(user.getAccountId());
        List<User> users = userMapper.selectByExample(userExample);
        if (users.size() == 0){
            // 插入
            user.setGmtCreate(System.currentTimeMillis());
            user.setGmtModified(user.getGmtCreate());
            userMapper.insert(user);
        }else {
            // 根据 id 更新 除了 GmtCreate 保持原来的，其他的都更新
            user.setId(users.get(0).getId());
            user.setGmtCreate(users.get(0).getGmtCreate());
            user.setGmtModified(System.currentTimeMillis());
            userMapper.updateByPrimaryKey(user);
        }
    }
}
