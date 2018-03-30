package kr.co.cntt.deliverydispatchadmin.controller;

import kr.co.cntt.core.annotation.CnttMethodDescription;
import kr.co.cntt.core.model.admin.Admin;
import kr.co.cntt.core.model.alarm.Alarm;
import kr.co.cntt.core.model.group.Group;
import kr.co.cntt.core.model.group.SubGroup;
import kr.co.cntt.core.model.group.SubGroupStoreRel;
import kr.co.cntt.core.model.notice.Notice;
import kr.co.cntt.core.model.reason.Reason;
import kr.co.cntt.core.model.thirdParty.ThirdParty;
import kr.co.cntt.core.service.admin.AccountAdminService;
import kr.co.cntt.core.service.admin.AssignAdminService;
import kr.co.cntt.core.service.admin.FileUploadAdminService;
import kr.co.cntt.core.service.admin.NoticeAdminService;
import kr.co.cntt.core.util.FileUtil;
import kr.co.cntt.core.util.MediaTypeUtils;
import kr.co.cntt.deliverydispatchadmin.security.SecurityUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Controller
public class SettingController {

    @Value("${api.upload.path.alarm}")
    private String alarmFileUploadPath;

    @Value("${api.upload.path.notice}")
    private String noticeFileUploadPath;

    private AccountAdminService accountAdminService;
    private AssignAdminService assignAdminService;
    private FileUploadAdminService fileUploadAdminService;
    private NoticeAdminService noticeAdminService;

    @Autowired
    public SettingController(AccountAdminService accountAdminService, AssignAdminService assignAdminService, FileUploadAdminService fileUploadAdminService, NoticeAdminService noticeAdminService) {
        this.accountAdminService = accountAdminService;
        this.assignAdminService = assignAdminService;
        this.fileUploadAdminService = fileUploadAdminService;
        this.noticeAdminService = noticeAdminService;
    }

    @Autowired
    private ServletContext servletContext;

    /**
     * 설정 - 계정관리 페이지
     *
     * @return
     */
    @GetMapping("/setting-account")
    @CnttMethodDescription("계정관리 페이지")
    public String settingAccount(Admin admin, Model model) {
        SecurityUser adminInfo = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getDetails();
        admin.setToken(adminInfo.getAdminAccessToken());

        Admin retAdmin = accountAdminService.getAdminAccount(admin);

        model.addAttribute("adminInfo", retAdmin);

        return "/setting/setting_account";
    }


    /**
     * 설정 - 계정관리 관리자 정보 수정
     *
     * @return
     */
    @PostMapping("/putAdminAccount")
    @CnttMethodDescription("관리자 계정 정보 수정")
    public String putAdminAccount(Admin admin) {
        SecurityUser adminInfo = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getDetails();
        admin.setToken(adminInfo.getAdminAccessToken());

        accountAdminService.putAdminAccount(admin);

        return "redirect:/setting-account";
    }


    /**
     * 설정 - 배정관리 페이지
     *
     * @return
     */
    @GetMapping("/setting-assign")
    public String settingAssign(Admin admin, Model model) {

        SecurityUser adminInfo = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getDetails();
        admin.setToken(adminInfo.getAdminAccessToken());

        Admin retAdmin = accountAdminService.getAdminAccount(admin);

        ThirdParty thirdParty = new ThirdParty();
        thirdParty.setToken(admin.getToken());
        thirdParty.setLevel("1");

        Reason reason = new Reason();
        reason.setToken(admin.getToken());

        List<ThirdParty> thirdPartyList = assignAdminService.getThirdParty(thirdParty);
        List<Reason> assignedAdvanceList = assignAdminService.getAssignedAdvance(reason);
        List<Reason> assignedRejectList = assignAdminService.getassignedReject(reason);

        model.addAttribute("adminInfo", retAdmin);
        model.addAttribute("thirdPartyList", thirdPartyList);
        model.addAttribute("assignedAdvanceList", assignedAdvanceList);
        model.addAttribute("assignedRejectList", assignedRejectList);

        return "/setting/setting_assign";
    }


    /**
     * 설정 - 배정관리 관리자 배정모드, 최대오더 수정
     *
     * @return
     */
    @PostMapping("/putAdminAssign")
    @CnttMethodDescription("관리자 배정모드, 최대오더 수정")
    public String putAdminAssign(Admin admin
            , @RequestParam(value = "assignAuto", required = false) String assignAuto
            , @RequestParam(value = "assignStore", required = false) String assignStore
            , @RequestParam(value = "assignRider", required = false) String assignRider) {
        SecurityUser adminInfo = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getDetails();
        admin.setToken(adminInfo.getAdminAccessToken());

        List<String > tmpAssignMod = new ArrayList<>();

        if (assignAuto != null) {
            tmpAssignMod.add(assignAuto);
        }
        if (assignStore != null) {
            tmpAssignMod.add(assignStore);
        }
        if (assignRider != null) {
            tmpAssignMod.add(assignRider);
        }

        if (assignAuto == null && assignStore == null && assignRider == null) {
            admin.setAssignmentStatus("none");
        } else {
            admin.setAssignmentStatus(String.join("|", tmpAssignMod));
        }

        assignAdminService.putAdminAssignmentStatus(admin);

        return "redirect:/setting-assign";
    }


    @ResponseBody
    @GetMapping("/postThirdParty")
    @CnttMethodDescription("배정관리 - 서드파티 추가")
    public ThirdParty postThirdParty(ThirdParty thirdParty) {
        SecurityUser adminInfo = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getDetails();
        thirdParty.setToken(adminInfo.getAdminAccessToken());
        thirdParty.setLevel("1");

        assignAdminService.postThirdParty(thirdParty);

        return thirdParty;
    }


    @ResponseBody
    @PutMapping("/putThirdParty")
    @CnttMethodDescription("배정관리 - 서드파티 수정")
    public ThirdParty putThirdParty(ThirdParty thirdParty) {
        SecurityUser adminInfo = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getDetails();
        thirdParty.setToken(adminInfo.getAdminAccessToken());

        assignAdminService.updateThirdParty(thirdParty);

        return thirdParty;
    }


    @ResponseBody
    @PutMapping("/deleteThirdParty")
    @CnttMethodDescription("배정관리 - 서드파티 삭제")
    public ThirdParty deleteThirdParty(ThirdParty thirdParty) {
        SecurityUser adminInfo = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getDetails();
        thirdParty.setToken(adminInfo.getAdminAccessToken());

        assignAdminService.deleteThirdParty(thirdParty);

        return thirdParty;
    }



    @ResponseBody
    @GetMapping("/postAssignedAdvance")
    @CnttMethodDescription("배정관리 - 우선배정 사유 추가")
    public Reason postAssignedAdvance(Reason reason) {
        SecurityUser adminInfo = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getDetails();
        reason.setToken(adminInfo.getAdminAccessToken());

        assignAdminService.postAssignedAdvance(reason);

        return reason;
    }

    @ResponseBody
    @PutMapping("/putAssignedAdvance")
    @CnttMethodDescription("배정관리 - 우선배정 사유 수정")
    public Reason putAssignedAdvance(Reason reason) {
        SecurityUser adminInfo = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getDetails();
        reason.setToken(adminInfo.getAdminAccessToken());

        assignAdminService.putAssignedAdvance(reason);

        return reason;
    }

    @ResponseBody
    @PutMapping("/deleteAssignedAdvance")
    @CnttMethodDescription("배정관리 - 우선배정 사유 삭제")
    public Reason deleteAssignedAdvance(Reason reason) {
        SecurityUser adminInfo = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getDetails();
        reason.setToken(adminInfo.getAdminAccessToken());

        assignAdminService.deleteAssignedAdvance(reason);

        return reason;
    }

    @ResponseBody
    @GetMapping("/postAssignedReject")
    @CnttMethodDescription("배정관리 - 배정거절 사유 추가")
    public Reason postAssignedReject(Reason reason) {
        SecurityUser adminInfo = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getDetails();
        reason.setToken(adminInfo.getAdminAccessToken());

        assignAdminService.postAssignedReject(reason);

        return reason;
    }


    @ResponseBody
    @PutMapping("/putAssignedReject")
    @CnttMethodDescription("배정관리 - 배정거절 사유 수정")
    public Reason putAssignedReject(Reason reason) {
        SecurityUser adminInfo = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getDetails();
        reason.setToken(adminInfo.getAdminAccessToken());

        assignAdminService.putAssignedReject(reason);

        return reason;
    }


    @ResponseBody
    @PutMapping("/deleteAssignedReject")
    @CnttMethodDescription("배정관리 - 배정거절 사유 삭제")
    public Reason deleteAssignedReject(Reason reason) {
        SecurityUser adminInfo = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getDetails();
        reason.setToken(adminInfo.getAdminAccessToken());

        assignAdminService.deleteRejectReason(reason);

        return reason;
    }


    /**
     * 설정 - 알림음 설정 페이지
     *
     * @return
     */
    @GetMapping("/setting-alarm")
    public String settingAlarm(Alarm alarm, Model model) {
        SecurityUser adminInfo = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getDetails();
        alarm.setToken(adminInfo.getAdminAccessToken());

        List<Alarm> retAlarm = fileUploadAdminService.getAlarmList(alarm);

        Alarm newAlarm = new Alarm();
        Alarm assignAlarm = new Alarm();
        Alarm assignedCancelAlarm = new Alarm();
        Alarm completeAlarm = new Alarm();
        Alarm cancelAlarm = new Alarm();

        for (Alarm a : retAlarm) {
            if (a.getAlarmType().equals("0")) {
                newAlarm = a;
            } else if (a.getAlarmType().equals("1")) {
                assignAlarm = a;
            } else if (a.getAlarmType().equals("2")) {
                assignedCancelAlarm = a;
            } else if (a.getAlarmType().equals("3")) {
                completeAlarm = a;
            } else if (a.getAlarmType().equals("4")) {
                cancelAlarm = a;
            }

        }

        log.info(newAlarm.getOriFileName());
        log.info(newAlarm.getAlarmType());
        model.addAttribute("newAlarm", newAlarm);
        model.addAttribute("assignAlarm", assignAlarm);
        model.addAttribute("assignedCancelAlarm", assignedCancelAlarm);
        model.addAttribute("completeAlarm", completeAlarm);
        model.addAttribute("cancelAlarm", cancelAlarm);

        return "/setting/setting_alarm";
    }


    @PostMapping("/alarmFileUpload")
    public String alarmFileUpload(HttpServletRequest request, HttpServletResponse response) {
        SecurityUser adminInfo = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getDetails();

        MultipartHttpServletRequest multipartRequest =  (MultipartHttpServletRequest) request;

        List<MultipartFile> reqFiles = multipartRequest.getFiles("alarmFile");
        List<MultipartFile> files = new ArrayList<>();

        DateTimeFormatter dateformatter = DateTimeFormatter.ofPattern("yyyyMMdd");

        Alarm alarm = new Alarm();
        alarm.setToken(adminInfo.getAdminAccessToken());

        for (int j = 0; j < reqFiles.size(); j++) {
            if (reqFiles.get(j).getSize() > 0) {
                files.add(reqFiles.get(j));

                alarm.setAlarmType(Integer.toString(j));
                alarm.setOriFileName(reqFiles.get(j).getOriginalFilename());
//                String[] tmp = reqFiles.get(j).getOriginalFilename().split("\\.");
//                alarm.setFileName(RandomStringUtils.randomAlphanumeric(16) + "_" + LocalDateTime.now().format(dateformatter) + "." + tmp[1]);
                alarm.setFileName(LocalDateTime.now().format(dateformatter) + "_" + reqFiles.get(j).getOriginalFilename());
                alarm.setFileSize(Long.toString(reqFiles.get(j).getSize()));

                fileUploadAdminService.alarmFileUpload(alarm);
            }
        }

//        for (MultipartFile f : reqFiles) {
//            if (f.getSize() > 0) {
//                files.add(f);
//            }
//        }

        MultipartFile[] fileArray = new MultipartFile[files.size()];
        for (int i = 0; i < files.size(); i++) {
            if (files.get(i).getSize() > 0) {
                fileArray[i] = files.get(i);
            }
        }

        FileUtil fileUtil = new FileUtil();
        fileUtil.fileUpload(fileArray, alarmFileUploadPath+"/");
//        fileUtil.fileUpload(fileArray, "c:\\");

        return "redirect:/setting-alarm";
    }


    /**
     * 설정 - 알람 삭제
     *
     * @return
     */
    @ResponseBody
    @GetMapping("/deleteAlarm")
    @CnttMethodDescription("알람 삭제")
    public int deleteAlarm(Alarm alarm) {
        SecurityUser adminInfo = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getDetails();
        alarm.setToken(adminInfo.getAdminAccessToken());

        return fileUploadAdminService.deleteAlarm(alarm);
    }


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


    @ResponseBody
    @PutMapping("/putNotice")
    @CnttMethodDescription("공지사항 수정")
    public int putNotice(Notice notice){
        SecurityUser adminInfo = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getDetails();
        notice.setToken(adminInfo.getAdminAccessToken());
        return noticeAdminService.putNotice(notice);
    }


    @ResponseBody
    @PutMapping("/deleteNotice")
    @CnttMethodDescription("공지사항 삭제")
    public int deleteNotice(Notice notice){
        SecurityUser adminInfo = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getDetails();
        notice.setToken(adminInfo.getAdminAccessToken());
        return noticeAdminService.deleteNotice(notice);
    }

    @ResponseBody
    @PutMapping("/getGroupList")
    @CnttMethodDescription("공지사항 그룹 목록 조회")
    public List<Group> getGroupList(Notice notice){
        SecurityUser adminInfo = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getDetails();
        notice.setToken(adminInfo.getAdminAccessToken());
        return noticeAdminService.getGroupList(notice);
    }

//    @ResponseBody
    @PostMapping("/postNotice")
    @CnttMethodDescription("공지사항 등록")
    public String postNotice(Notice notice, @RequestParam("nNewFile") MultipartFile file){
        SecurityUser adminInfo = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getDetails();
        notice.setToken(adminInfo.getAdminAccessToken());

        FileUtil fileUtil = new FileUtil();
        fileUtil.fileUpload(file, noticeFileUploadPath+"/");

        DateTimeFormatter dateformatter = DateTimeFormatter.ofPattern("yyyyMMdd");

        notice.setOriFileName(file.getOriginalFilename());
        notice.setFileName(LocalDateTime.now().format(dateformatter) + "_" + file.getOriginalFilename());
        notice.setFileSize(Long.toString(file.getSize()));

        noticeAdminService.postNotice(notice);

        return "redirect:/setting-notice";
    }


    @ResponseBody
    @PutMapping("/deleteNoticeFile")
    @CnttMethodDescription("공지사항 수정")
    public int deleteNoticeFile(Notice notice){
        SecurityUser adminInfo = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getDetails();
        notice.setToken(adminInfo.getAdminAccessToken());
        notice.setOriFileName("none");
        notice.setFileName("none");
        notice.setFileSize("none");

        return noticeAdminService.putNotice(notice);
    }


    @GetMapping("/noticeFileDownload")
    @CnttMethodDescription("공지사항 첨부파일 다운로드")
    public void noticeFileDownload(HttpServletResponse response,/*Notice notice*/@RequestParam(value = "fileName", required = false) String fileName)  throws IOException {
        SecurityUser adminInfo = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getDetails();
        Notice notice = new Notice();
        notice.setFileName(fileName);
        notice.setToken(adminInfo.getAdminAccessToken());

        MediaType mediaType = MediaTypeUtils.getMediaTypeForFileName(this.servletContext,  notice.getFileName());

        File file = new File(noticeFileUploadPath + "/" + notice.getFileName());
//        InputStreamResource resource = new InputStreamResource(new FileInputStream(file));
        if(file.exists()){
            response.setContentType(mediaType.getType());

        /* "Content-Disposition : inline" will show viewable types [like images/text/pdf/anything viewable by browser] right on browser
            while others(zip e.g) will be directly downloaded [may provide save as popup, based on your browser setting.]*/
            response.setHeader("Content-Disposition", String.format("inline; filename=\"" + file.getName() +"\""));


            /* "Content-Disposition : attachment" will be directly download, may provide save as popup, based on your browser setting*/
            //response.setHeader("Content-Disposition", String.format("attachment; filename=\"%s\"", file.getName()));

            response.setContentLength((int)file.length());


            InputStream inputStream = new BufferedInputStream(new FileInputStream(file));

            //Copy bytes from source to destination(outputstream in this example), closes both streams.
            FileCopyUtils.copy(inputStream, response.getOutputStream());
        }else{
            response.sendError(404, "잘못된 접근입니다.");
        }



//        String path = String.format("%s%s", fsResource.getPath(), filename);

        /*FileSystemResource resource = new FileSystemResource(noticeFileUploadPath + "/" + notice.getFileName());
        InputStreamResource isResource = new InputStreamResource(resource.getInputStream());






        HttpHeaders responseHeaders = new HttpHeaders();

        responseHeaders.setContentType(MediaType.APPLICATION_OCTET_STREAM);

        responseHeaders.set("Content-Disposition", "attachment;filename=\"" + notice.getFileName() + "\";");

        responseHeaders.set("Content-Transfer-Encoding", "binary");

        return new ResponseEntity<InputStreamResource>(isResource, responseHeaders, HttpStatus.OK);*/




        /*return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + file.getName())
                .contentType(mediaType)
                .contentLength(file.length())
                .body(resource);*/

//        return "redirect:/setting-account";
    }

}
