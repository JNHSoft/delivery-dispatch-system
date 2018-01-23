package kr.co.cntt.core.service.api;

import kr.co.cntt.core.model.login.User;

public interface UserService {
    /**
     * <p> selectLoginRider
     *
     * @return
     */
    public String selectLoginRider(User user);

    /**
     * <p> selectLoginStore
     *
     * @return
     */
    public String selectLoginStore(User user);
}
