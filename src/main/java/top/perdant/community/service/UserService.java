package top.perdant.community.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.perdant.community.mapper.UserMapper;
import top.perdant.community.model.User;
import top.perdant.community.model.UserExample;

import java.util.List;

/**
 * 插入和更新user表
 *
 * @author perdant
 */
@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;

    public void createOrUpdate(User user) {
        UserExample userExample = new UserExample();
        userExample.createCriteria()
                .andAccountIdEqualTo(user.getAccountId());
        List<User> users = userMapper.selectByExample(userExample);
        if (users.size() == 0){
            // 插入
            user.setGmtCreate(System.currentTimeMillis());
            user.setGmtModified(user.getGmtCreate());
            userMapper.insertSelective(user);
        }else {
            // 更新
            user.setId(users.get(0).getId());
            user.setGmtCreate(users.get(0).getGmtCreate());
            user.setGmtModified(System.currentTimeMillis());
            userMapper.updateByPrimaryKey(user);
        }
    }
}
