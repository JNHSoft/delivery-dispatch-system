package kr.co.deliverydispatch.controller;

import kr.co.cntt.core.model.tracker.Tracker;
import kr.co.cntt.core.util.AES256Util;
import kr.co.cntt.core.util.CustomEncryptUtil;
import kr.co.cntt.core.util.Misc;
import kr.co.deliverydispatch.service.TrackerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.TimeZone;

@Slf4j
@Controller
public class TrackerController {

    @Value("${api.tracker.key}")
    private String tKey;

    private TrackerService trackerService;

    @Autowired
    public TrackerController(TrackerService trackerService) { this.trackerService = trackerService; }

    /*@GetMapping("/tracker")
    public String tracker(Model model) {
        // String param = "token=eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0d190cmFja2VyIiwiYXVkaWVuY2UiOiJ3ZWIiLCJjcmVhdGVkIjoxNTIxNjAwMjIxMzc5fQ.fQYha8zo4g8i2xDhF6wpDYqawl-BQF-RcTQZ8vCl3iA&level=4&code=016&regOrderId=15";

        String param = "{\"level\":\"4\",\"token\":\"eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0d190cmFja2VyIiwiYXVkaWVuY2UiOiJ3ZWIiLCJjcmVhdGVkIjoxNTIxNjAwMjIxMzc5fQ.fQYha8zo4g8i2xDhF6wpDYqawl-BQF-RcTQZ8vCl3iA\",\"code\":\"103\",\"webOrderId\":\"s-20180509-cnt-aa120\",\"reqDate\":\"20180515130000\"}";

        try {
            String encKey = tKey;
            AES256Util aesUtil = new AES256Util(encKey);

            String encParam = aesUtil.aesEncode(param);
            String encBase = CustomEncryptUtil.encodeBase64(encParam);

            String decBase = CustomEncryptUtil.decodeBase64(encBase);
            String decAes = aesUtil.aesDecode(CustomEncryptUtil.decodeBase64(encBase));

            model.addAttribute("strParam", param);
            model.addAttribute("encBase", encBase);
            model.addAttribute("decBase", decBase);
            model.addAttribute("decAes", decAes);

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
    }*/

    @GetMapping("/tracker")
    public String getTracker(@RequestParam(required=false) String encParam, Model model) throws Exception {
        if (encParam != null) {
            Tracker trackerResult = trackerService.getTracker(encParam);
            if(trackerResult == null){
                return "/error/error-tracker";
            }
            TimeZone timeZone = TimeZone.getTimeZone("Asia/Taipei");
            DateTimeFormatter DATEFORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

            long rqDate = LocalDateTime.parse(trackerResult.getRequestDate(), DATEFORMATTER).atZone(timeZone.toZoneId()).toInstant().toEpochMilli();
            long localNow = LocalDateTime.now(timeZone.toZoneId()).atZone(timeZone.toZoneId()).toInstant().toEpochMilli();
            long abs = Math.abs(rqDate-localNow)/60000;

            /*System.out.println("LocalDateTime.now(timeZone.toZoneId()) = "+LocalDateTime.now(timeZone.toZoneId()));
            System.out.println("getRequestDate = "+trackerResult.getRequestDate());
            System.out.println("abs = "+abs);*/

            if(trackerResult.getRiderLatitude() != null){
                Misc misc = new Misc();
                trackerResult.setDistance(Double.toString(misc.getHaversine(trackerResult.getLatitude(), trackerResult.getLongitude(), trackerResult.getRiderLatitude(), trackerResult.getRiderLongitude())/(double) 1000));
            }

            if (trackerResult != null) {
                if (abs < 1){
                    model.addAttribute("tracker", trackerResult);
                    return "/tracker/tracker";
                }else {
                    return "/error/error-tracker";
                }
            } else {
                return "/error/error-tracker";
            }
        } else {
            return "/error/error-tracker";
        }
    }

}
