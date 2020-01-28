package kr.co.deliverydispatch.service.serviceImpl;

import kr.co.cntt.core.exception.AppTrException;
import kr.co.cntt.core.mapper.TrackerMapper;
import kr.co.cntt.core.model.tracker.Tracker;
import kr.co.cntt.core.service.ServiceSupport;
import kr.co.cntt.core.util.AES256Util;
import kr.co.cntt.core.util.CustomEncryptUtil;
import kr.co.deliverydispatch.service.TrackerService;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import javax.activation.MimetypesFileTypeMap;
import java.io.Console;
import java.io.File;

@Slf4j
@Service("mvcTrackerService")
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
    public Tracker getTracker(String encParam) throws AppTrException {
        Tracker tracker = new Tracker();
        try {
            AES256Util aesUtil = new AES256Util(tKey);

            String decParam = aesUtil.aesDecode(CustomEncryptUtil.decodeBase64(encParam));
//            System.out.println(decParam);
            JSONParser jsonParser = new JSONParser();
            JSONObject jsonObject = (JSONObject) jsonParser.parse(decParam);

//            String regOrderId = jsonObject.get("regOrderId").toString();
            String webOrderId = jsonObject.get("webOrderId").toString();
            String code = jsonObject.get("code").toString();
            String requestDate = jsonObject.get("reqDate").toString();

            tracker.setWebOrderId(webOrderId);
            tracker.setCode(code);
            tracker = trackerMapper.selectTracker(tracker);
            if(tracker != null){
                tracker.setRequestDate(requestDate);
            }

            // 이미지 경로 Check & Value Save
            String brandPath = "";
            String riderLeftPath = "";
            String riderRightPath = "";

            if (!tracker.getBrandCode().isEmpty()){
                brandPath = "images/tracker/pin_store" + tracker.getBrandCode() + ".png";
                riderLeftPath = "images/tracker/pin_moto_left" + tracker.getBrandCode() + ".png";
                riderRightPath = "images/tracker/pin_moto_right" + tracker.getBrandCode() + ".png";
            }

            // Brand Image Check
            if (new ClassPathResource(brandPath).exists()){
                tracker.setBrandImg("./resources/" + brandPath + "?ver=0.4");
            }else{
                tracker.setBrandImg("./resources/images/tracker/pin_store.jpg?ver=0.4");
            }

            // Rider Left Image Check
            if (new ClassPathResource(riderLeftPath).exists()){
                tracker.setRiderLeftImg("./resources/" + riderLeftPath);
            }else{
                tracker.setRiderLeftImg("./resources/pin_moto_left.png");
            }

            // Rider Right Image Check
            if (new ClassPathResource(riderRightPath).exists()){
                tracker.setRiderRightImg("./resources/" + riderRightPath);
            }else{
                tracker.setRiderRightImg("./resources/pin_moto_right.png");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return tracker;
    }
}

