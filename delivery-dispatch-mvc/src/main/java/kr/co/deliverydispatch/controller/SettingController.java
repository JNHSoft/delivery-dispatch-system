package kr.co.deliverydispatch.controller;

import com.google.gson.Gson;
import kr.co.cntt.core.annotation.CnttMethodDescription;
import kr.co.cntt.core.model.notice.Notice;
import kr.co.cntt.core.model.store.Store;
import kr.co.deliverydispatch.security.SecurityUser;
import kr.co.deliverydispatch.service.StoreNoticeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Slf4j
@Controller
public class SettingController {

    private StoreNoticeService storeNoticeService;

    @Autowired
    public SettingController(StoreNoticeService storeNoticeService) { this.storeNoticeService = storeNoticeService; }

    /**
     * 설정 - 계정관리 페이지
     *
     * @return
     */
    @GetMapping("/setting-account")
    public String settingAccount() { return "/setting/setting_account"; }


    /**
     * 설정 - 배정관리 페이지
     *
     * @return
     */
    @GetMapping("/setting-assign")
    public String settingAssign() { return "/setting/setting_assign"; }


    /**
     * 설정 - 기사관리 페이지
     *
     * @return
     */
    @GetMapping("/setting-rider")
    public String settingRider() { return "/setting/setting_rider"; }


    /**
     * 설정 - 알림음 설정 페이지
     *
     * @return
     */
    @GetMapping("/setting-alarm")
    public String settingAlarm() { return "/setting/setting_alarm"; }


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

        log.info("json : {}", new Gson().toJson(store));

        return "/setting/setting_notice";
    }


    @ResponseBody
    @GetMapping("/getNoticeList")
    @CnttMethodDescription("공지사항 리스트 조회")
    public List<Notice> getNoticeList(Notice notice){
        SecurityUser storeInfo = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getDetails();
        notice.setToken(storeInfo.getStoreAccessToken());
        List<Notice> noticeList = storeNoticeService.getNoticeList(notice);
        return noticeList;
    }

}
