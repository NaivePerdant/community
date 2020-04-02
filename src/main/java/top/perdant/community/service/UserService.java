package top.perdant.community.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.perdant.community.mapper.UserMapper;
import top.perdant.community.model.User;

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
        User dbUser = userMapper.findByAccountId(user.getAccountId());
        if (dbUser == null){
            // 插入
            user.setGmtCreate(System.currentTimeMillis());
            user.setGmtModified(user.getGmtCreate());
            userMapper.insert(user);
        }else {
            // 更新
            user.setGmtCreate(dbUser.getGmtCreate()); // 其实 user 的 GmtCreate 不需要写，反正没有改变
            user.setGmtModified(System.currentTimeMillis());
            userMapper.update(user);
        }
    }
}
