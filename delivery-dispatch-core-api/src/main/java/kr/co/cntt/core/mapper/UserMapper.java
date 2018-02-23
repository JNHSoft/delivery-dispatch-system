package kr.co.cntt.core.mapper;

import kr.co.cntt.core.annotation.DeliveryDispatchMapper;
import kr.co.cntt.core.model.login.User;

/**
 * <p> kr.co.cntt.core.mapper </p>
 * <p> RiderMapper.java </p>
 * <p> Rider 관련 </p>
 * @see DeliveryDispatchMapper
 * @author Merlin
 */
@DeliveryDispatchMapper
public interface UserMapper {


    /**
     * 푸쉬토큰 등록
     * @param user
     * @return
     */
    public int updateRiderPushToken(User user);
    public int updateStorePushToken(User user);
    public int updateManagerPushToken(User user);


}
