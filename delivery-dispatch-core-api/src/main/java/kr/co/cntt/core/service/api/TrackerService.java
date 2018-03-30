package kr.co.cntt.core.service.api;


import kr.co.cntt.core.exception.AppTrException;
import kr.co.cntt.core.model.login.User;
import kr.co.cntt.core.model.tracker.Tracker;
import kr.co.cntt.core.model.login.User;

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
     * <p> selectAdminTokenLoginCheck
     *
     * @return
     */
    public User selectTrackerTokenLoginCheck(User user);

    /**
     * <p> insertTrackerSession
     *
     * @param user
     * @return
     */
    public int insertTrackerSession(User user);

    /**
     * <p> getJsonTracker
     *
     * @param tracker
     * @return
     */
    public Tracker getJsonTracker(Tracker tracker) throws AppTrException;

    /**
     * <p> getTracker
     *
     * @param encParam
     * @return
     */
    public Tracker getTracker(String encParam) throws AppTrException;

}
