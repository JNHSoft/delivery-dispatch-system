package kr.co.cntt.core.service.api.impl;

import kr.co.cntt.core.mapper.TrackerMapper;
import kr.co.cntt.core.model.login.User;
import kr.co.cntt.core.model.tracker.Tracker;
import kr.co.cntt.core.service.ServiceSupport;
import kr.co.cntt.core.service.api.TrackerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;

@Slf4j
@Service("trackerService")
public class TrackerServiceImpl extends ServiceSupport implements TrackerService {

    /**
     * Rider DAO
     */
    private TrackerMapper trackerMapper;

    /**
     * @param trackerMapper USER D A O
     */
    @Autowired
    public TrackerServiceImpl(TrackerMapper trackerMapper) {
        this.trackerMapper = trackerMapper;
    }

    @Override
    public String selectLoginTracker(User user) {
        return trackerMapper.selectLoginTracker(user);
    }

    @Override
    public int selectTrackerTokenCheck(User user) {
        return trackerMapper.selectTrackerTokenCheck(user);
    }

    @Override
    public int insertTrackerSession(User user) { return trackerMapper.insertTrackerSession(user); }

    @Secured({"ROLE_TRACKER"})
    @Override
    public Tracker getTracker(Tracker tracker) { return trackerMapper.selectTracker(tracker); }

}
