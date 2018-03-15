package kr.co.cntt.core.service.api.impl;

import kr.co.cntt.core.exception.AppTrException;
import kr.co.cntt.core.mapper.UserMapper;
import kr.co.cntt.core.model.login.User;
import kr.co.cntt.core.service.ServiceSupport;
import kr.co.cntt.core.service.api.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Slf4j
@Service("userService")
public class UserServiceImpl extends ServiceSupport implements UserService {

    /**
     * Admin DAO
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
    public int updatePushToken(User user) throws AppTrException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        user.setAccessToken(user.getToken());
        /*if (authentication.getAuthorities().toString().equals("[ROLE_RIDER]")) {

        } else if (authentication.getAuthorities().toString().equals("[ROLE_USER]")) {
            // rider 가 아닌 user 가 넘어왔을때 token 값 null 로 셋팅
            user.setAccessToken(null);
        }*/

        int nRet = -1;

        if(user.getLevel().equals("3")){
            nRet = userMapper.updateRiderPushToken(user);
        }else if(user.getLevel().equals("2")){
            nRet = userMapper.updateStorePushToken(user);
        }

        return nRet;
    }

}
