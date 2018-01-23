package kr.co.cntt.core.mapper;

import kr.co.cntt.core.annotation.DeliveryDispatchMapper;
import kr.co.cntt.core.model.login.User;

/**
 * <p> kr.co.cntt.core.mapper </p>
 * <p> UserMapper.java </p>
 * @see DeliveryDispatchMapper
 * @author Merlin
 */
@DeliveryDispatchMapper
public interface UserMapper {
    /**
     * <p> selectLoginRider
     *
     * @return loginId String
     */
    public String selectLoginRider(User user);

    /**
     * <p> selectLoginStore
     *
     * @return loginId String
     */
    public String selectLoginStore(User user);
}
