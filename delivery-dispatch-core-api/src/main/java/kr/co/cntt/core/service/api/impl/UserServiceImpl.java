package kr.co.cntt.core.service.api.impl;

import kr.co.cntt.core.mapper.UserMapper;
import kr.co.cntt.core.model.login.User;
import kr.co.cntt.core.service.ServiceSupport;
import kr.co.cntt.core.service.api.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service("userService")
public class UserServiceImpl extends ServiceSupport implements UserService {

    /**
     * USER DAO
     */
    private UserMapper userMapper;

    /**
     * @param userMapper USER D A O
     */
    @Autowired
    public UserServiceImpl(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @Override
    public String selectLoginRider(User user) {
        return userMapper.selectLoginRider(user);
    }

    @Override
    public String selectLoginStore(User user) {
        return userMapper.selectLoginStore(user);
    }
}
