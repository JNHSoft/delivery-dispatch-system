package kr.co.deliverydispatch.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Slf4j
@Controller
public class SettingController {

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
     * 설정 - 계정관리 페이지
     *
     * @return
     */
    @GetMapping("/setting-notice")
    public String settingNotice() { return "/setting/setting_notice"; }

}
