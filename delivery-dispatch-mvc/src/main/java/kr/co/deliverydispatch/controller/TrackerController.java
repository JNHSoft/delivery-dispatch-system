package kr.co.deliverydispatch.controller;

import com.google.gson.Gson;
import kr.co.cntt.core.annotation.CnttMethodDescription;
import kr.co.cntt.core.model.tracker.Tracker;
import kr.co.cntt.core.util.AES256Util;
import kr.co.cntt.core.util.CustomEncryptUtil;
import kr.co.cntt.core.util.Misc;
import kr.co.deliverydispatch.service.TrackerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Slf4j
@Controller
public class TrackerController {

    @Resource
    private MessageSource messageSource;

    @Value("${spring.mvc.locale}")
    private Locale locale;

    @Value("${api.tracker.key}")
    private String tKey;
    private TrackerService trackerService;

    @Autowired
    public TrackerController(TrackerService trackerService) {
        this.trackerService = trackerService;
    }

    @GetMapping("/tracker-test")
    public String tracker(Model model, @RequestParam(required = false) String code, @RequestParam(required = false) String webOrderId, @RequestParam(required = false) String reqDate, @RequestParam(required = false) String chkBrand, @RequestParam(required = false) String aseValue) {
//         String param = "token=eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0d190cmFja2VyIiwiYXVkaWVuY2UiOiJ3ZWIiLCJjcmVhdGVkIjoxNTIxNjAwMjIxMzc5fQ.fQYha8zo4g8i2xDhF6wpDYqawl-BQF-RcTQZ8vCl3iA&level=4&code=016&regOrderId=15";
//        String param = "{\"level\":\"4\",\"token\":\"eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0d190cmFja2VyIiwiYXVkaWVuY2UiOiJ3ZWIiLCJjcmVhdGVkIjoxNTIxNjAwMjIxMzc5fQ.fQYha8zo4g8i2xDhF6wpDYqawl-BQF-RcTQZ8vCl3iA\",\"code\":\"s01\",\"webOrderId\":\"s-20181112-cnt-s01a-0001\",\"reqDate\":\"20181112160000\"}";

//        /tracker-test?code=s01&webOrderId=s-20190524-cnt-s01t-0001&reqDate=20190524150000
//        String param = "{\"level\":\"4\",\"token\":\"eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0d190cmFja2VyIiwiYXVkaWVuY2UiOiJ3ZWIiLCJjcmVhdGVkIjoxNTIxNjAwMjIxMzc5fQ.fQYha8zo4g8i2xDhF6wpDYqawl-BQF-RcTQZ8vCl3iA\",\"code\":\"" + code + "\",\"webOrderId\":\"" + webOrderId + "\",\"reqDate\":\"" + reqDate + "\"}";

        String strToken = "";

        if (chkBrand != null && chkBrand.toUpperCase() == "KFC"){
            strToken = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0d190cmFja2VyX2tmYyIsImF1ZGllbmNlIjoid2ViIiwiY3JlYXRlZCI6MTU4MDM1OTc2ODMyMH0.85lddh4HehEyq0lRZOXvEig2y4aCtZKQ__03qVvPNYQ";
        }else{
            strToken = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0d190cmFja2VyIiwiYXVkaWVuY2UiOiJ3ZWIiLCJjcmVhdGVkIjoxNTIxNjAwMjIxMzc5fQ.fQYha8zo4g8i2xDhF6wpDYqawl-BQF-RcTQZ8vCl3iA";
        }

        Map<String, Object> map = new HashMap<>();
        map.put("level", 4);
        map.put("token", strToken);
        map.put("code", code);
        map.put("webOrderId", webOrderId);

        if(reqDate.length() > 0){
            map.put("reqDate", reqDate);
        }else{
            map.put("reqDate", new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
        }

        String strJson = new Gson().toJson(map);

        try {
            String encKey = tKey;
            AES256Util aesUtil = new AES256Util(encKey);

            String encParam = aesUtil.aesEncode(strJson);
            String encBase = CustomEncryptUtil.encodeBase64(encParam);

            String decBase = CustomEncryptUtil.decodeBase64(encBase);
            String decAes = aesUtil.aesDecode(CustomEncryptUtil.decodeBase64(encBase));

            model.addAttribute("strParam", strJson);
            model.addAttribute("encBase", encBase);
            model.addAttribute("decBase", decBase);
            model.addAttribute("decAes", decAes);


            if (!StringUtils.isNullOrEmpty(aseValue)){
                String decBase1 = CustomEncryptUtil.decodeBase64(aseValue);
                String decAes1 = aesUtil.aesDecode(CustomEncryptUtil.decodeBase64(aseValue));

                System.out.println("#####################################################");
                System.out.println("decBase1 = " + decBase1);
                System.out.println("decAes1 = " + decAes1);
                System.out.println("#####################################################");
            }

//            if (!StringUtils.isNullOrEmpty(aseValue)){
//                String decBase1 = CustomEncryptUtil.decodeBase64(aseValue);
//                String decAes1 = aesUtil.aesDecode(CustomEncryptUtil.decodeBase64(aseValue));
//
//                System.out.println("#####################################################");
//                System.out.println("decBase1 = " + decBase1);
//                System.out.println("decAes1 = " + decAes1);
//                System.out.println("#####################################################");
//            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        }

        return "/tracker";
    }

    @GetMapping("/tracker")
    public String getTracker(@RequestParam(required = false) String encParam, Model model) throws Exception {
        if (encParam != null) {
            Tracker trackerResult = trackerService.getTracker(encParam);
            if (trackerResult == null) {
                model.addAttribute("encParam", encParam);
                model.addAttribute("ErrorValue", messageSource.getMessage("tracker.error.infomation",null, locale));
                return "/tracker/null";
            }
            TimeZone timeZone = TimeZone.getTimeZone("Asia/Taipei");
            DateTimeFormatter DATEFORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

            long rqDate = LocalDateTime.parse(trackerResult.getRequestDate(), DATEFORMATTER).atZone(timeZone.toZoneId()).toInstant().toEpochMilli();
            long localNow = LocalDateTime.now(timeZone.toZoneId()).atZone(timeZone.toZoneId()).toInstant().toEpochMilli();
            long abs = Math.abs(rqDate - localNow) / 60000;

//            System.out.println("LocalDateTime.now(timeZone.toZoneId()) = "+LocalDateTime.now(timeZone.toZoneId()));
//            System.out.println("getRequestDate = "+trackerResult.getRequestDate());
//            System.out.println("abs = "+abs);

            if (trackerResult.getRiderLatitude() != null) {
                Misc misc = new Misc();
                trackerResult.setDistance(Double.toString(misc.getHaversine(trackerResult.getStoreLatitude(), trackerResult.getStoreLongitude(), trackerResult.getRiderLatitude(), trackerResult.getRiderLongitude()) / (double) 1000));
            }
            trackerResult.setRequestDate(null);
            if (abs < 60) {
                model.addAttribute("encParam", encParam);
                model.addAttribute("tracker", trackerResult);
                return "/tracker/tracker4";
            } else {
                model.addAttribute("ErrorValue", messageSource.getMessage("tracker.error.timeout",null, locale));
                return "/tracker/null";
            }
        } else {
            return "/error/error-tracker";
        }
    }

    @ResponseBody
    @GetMapping("/trackerInfo")
    @CnttMethodDescription("tracker 정보 조회")
    public Tracker getTrackerInfo(@RequestParam(required = false) String encParam) throws Exception {
        Tracker trackerResult = trackerService.getTracker(encParam);
        if (trackerResult.getRiderLatitude() != null) {
            Misc misc = new Misc();
            trackerResult.setDistance(Double.toString(misc.getHaversine(trackerResult.getStoreLatitude(), trackerResult.getStoreLongitude(), trackerResult.getRiderLatitude(), trackerResult.getRiderLongitude()) / (double) 1000));
        }
        trackerResult.setRequestDate(null);
        return trackerResult;
    }

   /* @GetMapping("/trackerTest")
    public String getTestTracker(){
        return "/tracker/tracker_test";
    }*/
}
