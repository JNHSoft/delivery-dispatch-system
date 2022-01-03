package kr.co.cntt.core.service.api.impl;

import kr.co.cntt.core.enums.ErrorCodeEnum;
import kr.co.cntt.core.exception.AppTrException;
import kr.co.cntt.core.mapper.TrackerMapper;
import kr.co.cntt.core.model.login.User;
import kr.co.cntt.core.model.tracker.Tracker;
import kr.co.cntt.core.service.ServiceSupport;
import kr.co.cntt.core.service.api.TrackerService;
import kr.co.cntt.core.util.AES256Util;
import kr.co.cntt.core.util.CustomEncryptUtil;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service("trackerService")
public class TrackerServiceImpl extends ServiceSupport implements TrackerService {

    @Value("${api.tracker.key}")
    private String tKey;

    /**
     * Tracker DAO
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
    public Map selectLoginTracker(User user) {
        return trackerMapper.selectLoginTracker(user);
    }

    @Override
    public int selectTrackerTokenCheck(User user) {
        return trackerMapper.selectTrackerTokenCheck(user);
    }

    @Override
    public User selectTrackerTokenLoginCheck(User user) {
        return trackerMapper.selectTrackerTokenLoginCheck(user);
    }

    @Override
    public int insertTrackerSession(User user) { return trackerMapper.insertTrackerSession(user); }

    @Secured({"ROLE_TRACKER"})
    @Override
    public Tracker getJsonTracker(Tracker tracker) throws AppTrException {
        Tracker S_Tracker = trackerMapper.selectTracker(tracker);

        if (S_Tracker == null) {
            throw new AppTrException(getMessage(ErrorCodeEnum.E00016), ErrorCodeEnum.E00016.name());
        }

        return S_Tracker;
    }

    @Secured({"ROLE_TRACKER"})
    @Override
    public Tracker getTracker(String encParam) throws AppTrException {
        Tracker tracker = new Tracker();

        try {
            AES256Util aesUtil = new AES256Util(tKey);

            String decParam = aesUtil.aesDecode(CustomEncryptUtil.decodeBase64(encParam));

            JSONParser jsonParser = new JSONParser();
            JSONObject jsonObject = (JSONObject) jsonParser.parse(decParam);

            String regOrderId = jsonObject.get("regOrderId").toString();
            String code = jsonObject.get("code").toString();
            String webOrderId = jsonObject.get("webOrderId").toString();

            tracker.setRegOrderId(regOrderId);
            tracker.setCode(code);
            tracker.setWebOrderId(webOrderId);

        } catch (Exception e) {
            e.printStackTrace();
        }

        Tracker S_Tracker = trackerMapper.selectTracker(tracker);

        if (S_Tracker == null) {
            throw new AppTrException(getMessage(ErrorCodeEnum.E00016), ErrorCodeEnum.E00016.name());
        }

        return S_Tracker;
    }

}
