package kr.co.cntt.core.service.api;


import kr.co.cntt.core.exception.AppTrException;
import kr.co.cntt.core.model.login.User;

public interface UserService {

    public int updatePushToken(User user) throws AppTrException ;

}
