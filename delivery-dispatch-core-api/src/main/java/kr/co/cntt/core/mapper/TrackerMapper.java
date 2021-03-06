package kr.co.cntt.core.mapper;

import kr.co.cntt.core.annotation.DeliveryDispatchMapper;
import kr.co.cntt.core.model.login.User;
import kr.co.cntt.core.model.tracker.Tracker;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

/**
 * <p> kr.co.cntt.core.mapper </p>
 * <p> TrackerMapper.java </p>
 * <p> Tracker 관련 </p>
 * @see DeliveryDispatchMapper
 * @author Aiden
 */
@DeliveryDispatchMapper
public interface TrackerMapper {

    /**
     * <p> selectLoginTracker
     *
     * @param user
     * @return loginId String
     */
    public Map selectLoginTracker(User user);

    /**
     * <p> selectTrackerTokenCheck
     *
     * @param user
     * @return Count Int
     */
    public int selectTrackerTokenCheck(User user);

    /**
     * <p> selectTrackerTokenLoginCheck
     *
     * @param user
     * @return Count Int
     */
    public User selectTrackerTokenLoginCheck(User user);

    /**
     * <p> insertTrackerSession
     *
     * @param user
     * @return Insert 결과값
     */
    public int insertTrackerSession(User user);

    /**
     * <p> selectTracker
     *
     * @param tracker
     * @return tracker
     */
    public Tracker selectTracker(Tracker tracker);

    /**
     * 별점 등록
     * */
    int updateOrderStarPoint(Tracker tracker);
}
