package kr.co.deliverydispatch.controller;

import kr.co.cntt.core.annotation.CnttMethodDescription;
import kr.co.cntt.core.model.alarm.Alarm;
import kr.co.cntt.core.model.common.Common;
import kr.co.cntt.core.model.notice.Notice;
import kr.co.cntt.core.model.rider.Rider;
import kr.co.cntt.core.model.store.Store;
import kr.co.cntt.core.model.thirdParty.ThirdParty;
import kr.co.cntt.core.util.Geocoder;
import kr.co.cntt.core.util.MD5Encoder;
import kr.co.cntt.core.util.MediaTypeUtils;
import kr.co.cntt.core.util.ShaEncoder;
import kr.co.deliverydispatch.security.SecurityUser;
import kr.co.deliverydispatch.service.StoreNoticeService;
import kr.co.deliverydispatch.service.StoreOrderService;
import kr.co.deliverydispatch.service.StoreRiderService;
import kr.co.deliverydispatch.service.StoreSettingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Controller
public class SettingController {

    @Value("${api.upload.path.notice}")
    private String noticeFileUploadPath;

    private StoreSettingService storeSettingService;
    private StoreNoticeService storeNoticeService;
    @Autowired
    public SettingController(StoreSettingService storeSettingService, StoreNoticeService storeNoticeService) {
        this.storeSettingService = storeSettingService;
        this.storeNoticeService = storeNoticeService;
    }
    @Autowired
    private ServletContext servletContext;

    /**
     * 설정 - 계정관리 페이지
     *
     * @return
     */
    @GetMapping("/setting-account")
    public String settingAccount(Store store, @RequestParam(required = false) String frag, Model model) {
        SecurityUser storeInfo = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getDetails();
        store.setToken(storeInfo.getStoreAccessToken());
        Store myStore = storeSettingService.getStoreInfo(store);
        model.addAttribute("store", myStore);
        return "/setting/setting_account";
    }

    @ResponseBody
    @PutMapping("/putStoreInfo")
    public String  putStoreInfo(Store store) {
        SecurityUser storeInfo = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getDetails();
        store.setToken(storeInfo.getStoreAccessToken());
        store.setAccessToken(storeInfo.getStoreAccessToken());

        Store tmpStore = storeSettingService.getStoreInfo(store);

        MD5Encoder md5 = new MD5Encoder();
        ShaEncoder sha = new ShaEncoder(512);

        if (!store.getLoginPw().equals("")) {
            store.setLoginPw(sha.encode(store.getLoginPw()));
        }

        // 위도 경도
        if (store.getAddress() != null && store.getAddress() != "") {
            Geocoder geocoder = new Geocoder();
            try {
                Map<String, String> geo = geocoder.getLatLng(store.getAddress());
                store.setLatitude(geo.get("lat"));
                store.setLongitude(geo.get("lng"));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (store.getLatitude() == null || store.getLongitude() == null) {
            return "geo_err";
        }

        storeSettingService.updateStoreInfo(store);
        return "ok";
    }


    /**
     * 설정 - 배정관리 페이지
     *
     * @return
     */
    @GetMapping("/setting-assign")
    public String settingAssign(Store store, @RequestParam(required = false) String frag, Model model) {
        SecurityUser storeInfo = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getDetails();
        store.setToken(storeInfo.getStoreAccessToken());
        Store myStore = storeSettingService.getStoreInfo(store);
        ThirdParty thirdParty = new ThirdParty();
        thirdParty.setToken(storeInfo.getStoreAccessToken());
        List<ThirdParty> allThirdParty= storeSettingService.getThirdParty(thirdParty);
        model.addAttribute("store", myStore);
        model.addAttribute("thirdParty", allThirdParty);
        return "/setting/setting_assign";
    }

    // 서드파티 추가
    @ResponseBody
    @GetMapping("/getThirdPartyList")
    public List<ThirdParty> getThirdPartyList(Common common){
        SecurityUser storeInfo = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getDetails();
        ThirdParty thirdParty = new ThirdParty();
        thirdParty.setToken(storeInfo.getStoreAccessToken());
        List<ThirdParty> allThirdParty = storeSettingService.getThirdParty(thirdParty);

        return allThirdParty;
    }




    @ResponseBody
    @PutMapping("/putAssign")
    public Boolean putAssign(Store store) {
        SecurityUser storeInfo = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getDetails();
        store.setToken(storeInfo.getStoreAccessToken());
        storeSettingService.putStoreThirdParty(store);
        storeSettingService.updateStoreInfo(store);
        return true;
    }

    /**
     * 설정 - 기사관리 페이지
     *
     * @return
     */
    @GetMapping("/setting-rider")
    public String settingRider(Store store, @RequestParam(required = false) String frag, Model model) {
        SecurityUser storeInfo = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getDetails();
        store.setToken(storeInfo.getStoreAccessToken());
        Store myStore = storeSettingService.getStoreInfo(store);
        List<Rider> myRiderList = storeSettingService.getMyStoreRiderRels(store);
        model.addAttribute("riderList", myRiderList);
        model.addAttribute("store", myStore);
        return "/setting/setting_rider";
    }

    @ResponseBody
    @GetMapping("/getRiderInfo")
    public Rider getRiderInfo(Rider rider){
        SecurityUser storeInfo = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getDetails();
        rider.setToken(storeInfo.getStoreAccessToken());
        Rider riderInfo = storeSettingService.getRiderInfo(rider);

        return riderInfo;
    }

    @ResponseBody
    @PutMapping("/putRiderInfo")
    public Boolean putRiderInfo(Rider rider) {
        SecurityUser storeInfo = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getDetails();
        rider.setToken(storeInfo.getStoreAccessToken());
        storeSettingService.updateRiderInfo(rider);
        return true;
    }

    /**
     * 설정 - 알림음 설정 페이지
     *
     * @return
     */
    @GetMapping("/setting-alarm")
    public String settingAlarm(Store store, @RequestParam(required = false) String frag, Model model) {
        SecurityUser storeInfo = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getDetails();
        store.setToken(storeInfo.getStoreAccessToken());

        Store myStore = storeSettingService.getStoreInfo(store);
        ArrayList<Alarm> alarmList = (ArrayList)storeSettingService.getAlarm(store);

        Alarm newAlarm = null;
        Alarm assignAlarm = null;
        Alarm assignCancelAlarm = null;
        Alarm completeAlarm = null;
        Alarm cancelAlarm = null;

        for(Alarm alarm : alarmList){
            if(alarm.getAlarmType().equals("0")){
                newAlarm = alarm;
            }else if(alarm.getAlarmType().equals("1")){
                assignAlarm = alarm;
            }else if(alarm.getAlarmType().equals("2")){
                assignCancelAlarm = alarm;
            }else if(alarm.getAlarmType().equals("3")){
                completeAlarm = alarm;
            }else if(alarm.getAlarmType().equals("4")){
                cancelAlarm = alarm;
            }
        }

        model.addAttribute("store", myStore);
        model.addAttribute("newAlarm", newAlarm);
        model.addAttribute("assignAlarm", assignAlarm);
        model.addAttribute("assignCancelAlarm", assignCancelAlarm);
        model.addAttribute("completeAlarm", completeAlarm);
        model.addAttribute("cancelAlarm", cancelAlarm);
        return "/setting/setting_alarm";
    }
    @ResponseBody
    @PutMapping("/putStoreAlarm")
    public Boolean putStoreAlarm(Store store){
        SecurityUser storeInfo = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getDetails();
        store.setToken(storeInfo.getStoreAccessToken());
        storeSettingService.putStoreAlarm(store);

        return true;
    }
    /**
     * 설정 - 공지사항 페이지
     *
     * @return
     */
    @GetMapping("/setting-notice")
    @CnttMethodDescription("공지사항 페이지")
    public String settingNotice(Store store, @RequestParam(required = false) String frag, Model model) {
        SecurityUser storeInfo = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getDetails();
        store.setToken(storeInfo.getStoreAccessToken());
        Store myStore = storeNoticeService.getStoreInfo(store);
        model.addAttribute("store", myStore);
        return "/setting/setting_notice";
    }


    @ResponseBody
    @GetMapping("/getNoticeList")
    @CnttMethodDescription("공지사항 리스트 조회")
    public List<Notice> getNoticeList(Notice notice) {
        SecurityUser storeInfo = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getDetails();
        notice.setToken(storeInfo.getStoreAccessToken());
        List<Notice> noticeList = storeNoticeService.getNoticeList(notice);
        return noticeList;
    }


    @ResponseBody
    @GetMapping("/getNotice")
    @CnttMethodDescription("공지사항 상세 조회")
    public Notice getNotice(Notice notice) {
        SecurityUser storeInfo = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getDetails();
        notice.setToken(storeInfo.getStoreAccessToken());
        Notice noticeDetail = storeNoticeService.getNotice(notice);
        log.info(noticeDetail.getTitle());
        return noticeDetail;
    }

    @ResponseBody
    @PutMapping("/putNoticeConfirm")
    public Boolean putNoticeConfirm(Notice notice) {
        SecurityUser storeInfo = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getDetails();
        notice.setToken(storeInfo.getStoreAccessToken());
        storeSettingService.putNoticeConfirm(notice);
        return true;
    }

   @GetMapping("/noticeFileDownload")
   @CnttMethodDescription("공지사항 첨부파일 다운로드")
   public void noticeFileDownload(HttpServletResponse response, @RequestParam(value = "fileName", required = false) String fileName)  throws IOException {
       SecurityUser storeInfo = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getDetails();
       Notice notice = new Notice();
       notice.setFileName(fileName);
       notice.setToken(storeInfo.getStoreAccessToken());

       MediaType mediaType = MediaTypeUtils.getMediaTypeForFileName(this.servletContext,  notice.getFileName());

       File file = new File(noticeFileUploadPath + "/" + notice.getFileName());

       if(file.exists()){
           response.setContentType(mediaType.getType());

           response.setHeader("Content-Disposition", String.format("inline; filename=\"" + file.getName() +"\""));

           response.setContentLength((int)file.length());

           InputStream inputStream = new BufferedInputStream(new FileInputStream(file));

           FileCopyUtils.copy(inputStream, response.getOutputStream());

       }else{
           response.sendError(404, "잘못된 접근입니다.");
       }

   }
}
