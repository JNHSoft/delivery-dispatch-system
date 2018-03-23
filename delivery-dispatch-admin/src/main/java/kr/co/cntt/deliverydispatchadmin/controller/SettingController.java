package kr.co.cntt.deliverydispatchadmin.controller;

import kr.co.cntt.core.annotation.CnttMethodDescription;
import kr.co.cntt.core.model.group.SubGroup;
import kr.co.cntt.core.model.group.SubGroupStoreRel;
import kr.co.cntt.core.model.notice.Notice;
import kr.co.cntt.core.service.admin.NoticeAdminService;
import kr.co.cntt.deliverydispatchadmin.security.SecurityUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

@Slf4j
@Controller
public class SettingController {

    private NoticeAdminService noticeAdminService;

    @Autowired
    public SettingController(NoticeAdminService noticeAdminService) { this.noticeAdminService = noticeAdminService; }

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
    public String settingNotice() { return "/setting/setting_notice"; }


    @ResponseBody
    @GetMapping("/getNoticeList")
    @CnttMethodDescription("공지사항 리스트 조회")
    public List<Notice> getNoticeList(Notice notice) {
        SecurityUser adminInfo = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getDetails();
        notice.setToken(adminInfo.getAdminAccessToken());

        List<Notice> noticeList = noticeAdminService.getNoticeList(notice);

        return noticeList;
    }


    @ResponseBody
    @GetMapping("/getNotice")
    @CnttMethodDescription("공지사항 상세 조회")
    public Map getNotice(Notice notice) {
        SecurityUser adminInfo = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getDetails();
        notice.setToken(adminInfo.getAdminAccessToken());

        Map noticeDetail = noticeAdminService.getNotice(notice);

        return noticeDetail;
    }


    @ResponseBody
    @GetMapping("/getAdminSubGroupList")
    @CnttMethodDescription("선택한 그룹의 소그룹 목록 조회")
    public List<SubGroup> getAdminSubGroupList(Notice notice) {
        SecurityUser adminInfo = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getDetails();
        notice.setToken(adminInfo.getAdminAccessToken());

        List<SubGroup> subGroupList = noticeAdminService.getSubGroupList(notice);

        return subGroupList;
    }


    @ResponseBody
    @GetMapping("/getAdminSubGroupStoreList")
    @CnttMethodDescription("선택한 소그룹의 매장 목록 조회")
    public List<SubGroupStoreRel> getAdminSubGroupStoreList(Notice notice) {
        SecurityUser adminInfo = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getDetails();
        notice.setToken(adminInfo.getAdminAccessToken());

        List<SubGroupStoreRel> storeList = noticeAdminService.getSubGroupStoreRelList(notice);

        return storeList;
    }

}
