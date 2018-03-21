package kr.co.cntt.core.service.api;


import kr.co.cntt.core.model.login.User;
import kr.co.cntt.core.model.order.Order;

public interface TrackerService {
    /**
     * <p> selectLoginTracker
     *
     * @return
     */
    public String selectLoginTracker(User user);

    /**
     * <p> selectAdminTokenCheck
     *
     * @return
     */
    public int selectTrackerTokenCheck(User user);

    /**
     * <p> insertTrackerSession
     *
     * @param user
     * @return
     */
    public int insertTrackerSession(User user);

    /**
     * <p> getTracker
     *
     * @param order
     * @return
     */
    public Order getTracker(Order order);

}
