package kr.co.deliverydispatch.controller;

import com.google.gson.Gson;
import kr.co.cntt.core.annotation.CnttMethodDescription;
import kr.co.cntt.core.model.alarm.Alarm;
import kr.co.cntt.core.model.notice.Notice;
import kr.co.cntt.core.model.order.Order;
import kr.co.cntt.core.model.rider.Rider;
import kr.co.cntt.core.model.store.Store;
import kr.co.cntt.core.model.thirdParty.ThirdParty;
import kr.co.cntt.core.util.MD5Encoder;
import kr.co.cntt.core.util.ShaEncoder;
import kr.co.deliverydispatch.security.SecurityUser;
import kr.co.deliverydispatch.service.StoreNoticeService;
import kr.co.deliverydispatch.service.StoreOrderService;
import kr.co.deliverydispatch.service.StoreRiderService;
import kr.co.deliverydispatch.service.StoreSettingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Controller
public class SettingController {

    private StoreSettingService storeSettingService;
    private StoreNoticeService storeNoticeService;
    private StoreRiderService storeRiderService;
    private StoreOrderService storeOrderService;
    @Autowired
    public SettingController(StoreSettingService storeSettingService, StoreNoticeService storeNoticeService, StoreRiderService storeRiderService, StoreOrderService storeOrderService) {
        this.storeSettingService = storeSettingService;
        this.storeNoticeService = storeNoticeService;
        this.storeRiderService = storeRiderService;
        this.storeOrderService = storeOrderService;
    }

    @RequestMapping("/getNotice")
    @ResponseBody
    public Notice getNotice() {
        // TODO : Notice 불러오기

        return new Notice();
    }

    /**
     * 설정 - 계정관리 페이지
     *
     * @return
     */
    @GetMapping("/setting-account")
    public String settingAccount(Store store, @RequestParam(required = false) String frag, Model model) {
        SecurityUser storeInfo = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getDetails();
        log.info("===============> storeInfo.getStoreAccessToken()    : {}", storeInfo.getStoreAccessToken());

        store.setToken(storeInfo.getStoreAccessToken());
        Store myStore = storeSettingService.getStoreInfo(store);
        model.addAttribute("store", myStore);
        model.addAttribute("json", new Gson().toJson(store));
        Rider rider = new Rider();
        rider.setToken(storeInfo.getStoreAccessToken());
        List<Rider> footerRiderList = storeRiderService.getRiderFooter(rider);
        model.addAttribute("footerRiderList", footerRiderList);
        Order order = new Order();
        order.setToken(storeInfo.getStoreAccessToken());
        List<Order> footerOrderList = storeOrderService.getFooterOrders(order);
        model.addAttribute("footerOrderList", footerOrderList);
        log.info("json : {}", new Gson().toJson(store));
        return "/setting/setting_account";
    }

    @ResponseBody
    @PutMapping("/putStoreInfo")
    public Boolean putStoreInfo(Store store) {
        SecurityUser storeInfo = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getDetails();
        store.setToken(storeInfo.getStoreAccessToken());
        MD5Encoder md5 = new MD5Encoder();
        ShaEncoder sha = new ShaEncoder(512);
        store.setLoginPw(sha.encode(store.getLoginPw()));
        storeSettingService.updateStoreInfo(store);
        return true;
    }


    /**
     * 설정 - 배정관리 페이지
     *
     * @return
     */
    @GetMapping("/setting-assign")
    public String settingAssign(Store store, @RequestParam(required = false) String frag, Model model) {
        SecurityUser storeInfo = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getDetails();
        log.info("===============> storeInfo.getStoreAccessToken()    : {}", storeInfo.getStoreAccessToken());

        store.setToken(storeInfo.getStoreAccessToken());
        System.out.println("!!!!토큰"+store.getToken());
        Store myStore = storeSettingService.getStoreInfo(store);
        ThirdParty thirdParty = new ThirdParty();
        thirdParty.setToken(storeInfo.getStoreAccessToken());
        List<ThirdParty> allThirdParty= storeSettingService.getThirdParty(thirdParty);
        model.addAttribute("store", myStore);
        model.addAttribute("thirdParty", allThirdParty);
        model.addAttribute("json", new Gson().toJson(store));
        Rider rider = new Rider();
        rider.setToken(storeInfo.getStoreAccessToken());
        List<Rider> footerRiderList = storeRiderService.getRiderFooter(rider);
        model.addAttribute("footerRiderList", footerRiderList);
        Order order = new Order();
        order.setToken(storeInfo.getStoreAccessToken());
        List<Order> footerOrderList = storeOrderService.getFooterOrders(order);
        model.addAttribute("footerOrderList", footerOrderList);
        log.info("json : {}", new Gson().toJson(store));
        return "/setting/setting_assign";
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
        log.info("===============> storeInfo.getStoreAccessToken()    : {}", storeInfo.getStoreAccessToken());

        store.setToken(storeInfo.getStoreAccessToken());
        System.out.println("!!!!토큰"+store.getToken());
        Store myStore = storeSettingService.getStoreInfo(store);
        List<Rider> myRiderList = storeSettingService.getMyStoreRiderRels(store);
        model.addAttribute("riderList", myRiderList);
        model.addAttribute("store", myStore);
        model.addAttribute("json", new Gson().toJson(store));
        Rider rider = new Rider();
        rider.setToken(storeInfo.getStoreAccessToken());
        List<Rider> footerRiderList = storeRiderService.getRiderFooter(rider);
        model.addAttribute("footerRiderList", footerRiderList);
        Order order = new Order();
        order.setToken(storeInfo.getStoreAccessToken());
        List<Order> footerOrderList = storeOrderService.getFooterOrders(order);
        model.addAttribute("footerOrderList", footerOrderList);
        log.info("json : {}", new Gson().toJson(model));
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
    @PutMapping("/putRIderInfo")
    public Boolean putRIderInfo(Rider rider) {
        SecurityUser storeInfo = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getDetails();
        rider.setToken(storeInfo.getStoreAccessToken());
        ShaEncoder sha = new ShaEncoder(512);
        rider.setLoginPw(sha.encode(rider.getLoginPw()));
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
        log.info("===============> storeInfo.getStoreAccessToken()    : {}", storeInfo.getStoreAccessToken());

        store.setToken(storeInfo.getStoreAccessToken());
        System.out.println("!!!!토큰"+store.getToken());

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
        model.addAttribute("json", new Gson().toJson(store));
        Rider rider = new Rider();
        rider.setToken(storeInfo.getStoreAccessToken());
        List<Rider> footerRiderList = storeRiderService.getRiderFooter(rider);
        model.addAttribute("footerRiderList", footerRiderList);
        Order order = new Order();
        order.setToken(storeInfo.getStoreAccessToken());
        List<Order> footerOrderList = storeOrderService.getFooterOrders(order);
        model.addAttribute("footerOrderList", footerOrderList);
        log.info("json : {}", new Gson().toJson(model));
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
        log.info("===============> storeInfo.getStoreAccessToken()    : {}", storeInfo.getStoreAccessToken());

        store.setToken(storeInfo.getStoreAccessToken());
        System.out.println("!!!!토큰"+store.getToken());
        Store myStore = storeNoticeService.getStoreInfo(store);
        model.addAttribute("store", myStore);
        model.addAttribute("json", new Gson().toJson(store));
        Rider rider = new Rider();
        rider.setToken(storeInfo.getStoreAccessToken());
        List<Rider> footerRiderList = storeRiderService.getRiderFooter(rider);
        model.addAttribute("footerRiderList", footerRiderList);
        Order order = new Order();
        order.setToken(storeInfo.getStoreAccessToken());
        List<Order> footerOrderList = storeOrderService.getFooterOrders(order);
        model.addAttribute("footerOrderList", footerOrderList);
        log.info("json : {}", new Gson().toJson(store));

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
        log.info("!!!!!!!!!!!!putNoticeConfirm");
        SecurityUser storeInfo = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getDetails();
        notice.setToken(storeInfo.getStoreAccessToken());
        storeSettingService.putNoticeConfirm(notice);
        return true;
    }
}
