package kr.co.cntt.deliverydispatchadmin.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonParser;
import kr.co.cntt.core.annotation.CnttMethodDescription;
import kr.co.cntt.core.model.admin.Admin;
import kr.co.cntt.core.model.alarm.Alarm;
import kr.co.cntt.core.model.group.Group;
import kr.co.cntt.core.model.group.SubGroup;
import kr.co.cntt.core.model.group.SubGroupStoreRel;
import kr.co.cntt.core.model.login.User;
import kr.co.cntt.core.model.notice.Notice;
import kr.co.cntt.core.model.reason.Reason;
import kr.co.cntt.core.model.shared.SharedRiderInfo;
import kr.co.cntt.core.model.store.Store;
import kr.co.cntt.core.model.thirdParty.ThirdParty;
import kr.co.cntt.core.service.admin.*;
import kr.co.cntt.core.util.FileUtil;
import kr.co.cntt.core.util.MediaTypeUtils;
import kr.co.cntt.deliverydispatchadmin.security.SecurityUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
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
import java.util.*;

@Slf4j
@Controller
public class SettingController {

    @Value("${api.upload.path.alarm}")
    private String alarmFileUploadPath;

    @Value("${api.upload.path.notice}")
    private String noticeFileUploadPath;

    @Value("${spring.mvc.locale}")
    private Locale regionLocale;

    @Value("${api.default.alarms}")
    private String defaultAlarms;

    private AccountAdminService accountAdminService;
    private AssignAdminService assignAdminService;
    private FileUploadAdminService fileUploadAdminService;
    private NoticeAdminService noticeAdminService;
    private SharedInfoService sharedInfoService;

    @Autowired
    public SettingController(AccountAdminService accountAdminService, AssignAdminService assignAdminService, FileUploadAdminService fileUploadAdminService, NoticeAdminService noticeAdminService, SharedInfoService sharedInfoService) {
        this.accountAdminService = accountAdminService;
        this.assignAdminService = assignAdminService;
        this.fileUploadAdminService = fileUploadAdminService;
        this.noticeAdminService = noticeAdminService;
        this.sharedInfoService = sharedInfoService;
    }

    @Autowired
    private ServletContext servletContext;

    /**
     * ?????? - ???????????? ?????????
     *
     * @return
     */
    @GetMapping("/setting-account")
    @CnttMethodDescription("???????????? ?????????")
    public String settingAccount(Admin admin, Model model) {
        SecurityUser adminInfo = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getDetails();
        admin.setToken(adminInfo.getAdminAccessToken());

        Admin retAdmin = accountAdminService.getAdminAccount(admin);

        model.addAttribute("adminInfo", retAdmin);

        return "/setting/setting_account";
    }


    /**
     * ?????? - ???????????? ????????? ?????? ??????
     *
     * @return
     */
    @PostMapping("/putAdminAccount")
    @CnttMethodDescription("????????? ?????? ?????? ??????")
    public String putAdminAccount(Admin admin) {
        SecurityUser adminInfo = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getDetails();
        admin.setToken(adminInfo.getAdminAccessToken());

        accountAdminService.putAdminAccount(admin);

        return "redirect:/setting-account";
    }


    /**
     * ?????? - ???????????? ?????????
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
     * ?????? - ???????????? ????????? ????????????, ???????????? ??????
     *
     * @return
     */
    @PostMapping("/putAdminAssign")
    @CnttMethodDescription("????????? ????????????, ???????????? ??????")
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
    @CnttMethodDescription("???????????? - ???????????? ??????")
    public ThirdParty postThirdParty(ThirdParty thirdParty) {
        SecurityUser adminInfo = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getDetails();
        thirdParty.setToken(adminInfo.getAdminAccessToken());
        thirdParty.setLevel("1");

        assignAdminService.postThirdParty(thirdParty);

        return thirdParty;
    }


    @ResponseBody
    @PutMapping("/putThirdParty")
    @CnttMethodDescription("???????????? - ???????????? ??????")
    public ThirdParty putThirdParty(ThirdParty thirdParty) {
        SecurityUser adminInfo = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getDetails();
        thirdParty.setToken(adminInfo.getAdminAccessToken());

        assignAdminService.updateThirdParty(thirdParty);

        return thirdParty;
    }


    @ResponseBody
    @PutMapping("/deleteThirdParty")
    @CnttMethodDescription("???????????? - ???????????? ??????")
    public ThirdParty deleteThirdParty(ThirdParty thirdParty) {
        SecurityUser adminInfo = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getDetails();
        thirdParty.setToken(adminInfo.getAdminAccessToken());

        assignAdminService.deleteThirdParty(thirdParty);

        return thirdParty;
    }



    @ResponseBody
    @GetMapping("/postAssignedAdvance")
    @CnttMethodDescription("???????????? - ???????????? ?????? ??????")
    public Reason postAssignedAdvance(Reason reason) {
        SecurityUser adminInfo = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getDetails();
        reason.setToken(adminInfo.getAdminAccessToken());

        assignAdminService.postAssignedAdvance(reason);

        return reason;
    }

    @ResponseBody
    @PutMapping("/putAssignedAdvance")
    @CnttMethodDescription("???????????? - ???????????? ?????? ??????")
    public Reason putAssignedAdvance(Reason reason) {
        SecurityUser adminInfo = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getDetails();
        reason.setToken(adminInfo.getAdminAccessToken());

        assignAdminService.putAssignedAdvance(reason);

        return reason;
    }

    @ResponseBody
    @PutMapping("/deleteAssignedAdvance")
    @CnttMethodDescription("???????????? - ???????????? ?????? ??????")
    public Reason deleteAssignedAdvance(Reason reason) {
        SecurityUser adminInfo = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getDetails();
        reason.setToken(adminInfo.getAdminAccessToken());

        assignAdminService.deleteAssignedAdvance(reason);

        return reason;
    }

    @ResponseBody
    @GetMapping("/postAssignedReject")
    @CnttMethodDescription("???????????? - ???????????? ?????? ??????")
    public Reason postAssignedReject(Reason reason) {
        SecurityUser adminInfo = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getDetails();
        reason.setToken(adminInfo.getAdminAccessToken());

        assignAdminService.postAssignedReject(reason);

        return reason;
    }


    @ResponseBody
    @PutMapping("/putAssignedReject")
    @CnttMethodDescription("???????????? - ???????????? ?????? ??????")
    public Reason putAssignedReject(Reason reason) {
        SecurityUser adminInfo = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getDetails();
        reason.setToken(adminInfo.getAdminAccessToken());

        assignAdminService.putAssignedReject(reason);

        return reason;
    }


    @ResponseBody
    @PutMapping("/deleteAssignedReject")
    @CnttMethodDescription("???????????? - ???????????? ?????? ??????")
    public Reason deleteAssignedReject(Reason reason) {
        SecurityUser adminInfo = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getDetails();
        reason.setToken(adminInfo.getAdminAccessToken());

        assignAdminService.deleteRejectReason(reason);

        return reason;
    }


    /**
     * ?????? - ????????? ?????? ?????????
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

        model.addAttribute("newAlarm", newAlarm);
        model.addAttribute("assignAlarm", assignAlarm);
        model.addAttribute("assignedCancelAlarm", assignedCancelAlarm);
        model.addAttribute("completeAlarm", completeAlarm);
        model.addAttribute("cancelAlarm", cancelAlarm);
        model.addAttribute("regionLocale", regionLocale);
        model.addAttribute("selectedLang", alarm.getLang());
        model.addAttribute("defaultAlarms", defaultAlarms);

        return "/setting/setting_alarm";
    }


    @PostMapping("/alarmFileUpload")
    public String alarmFileUpload(HttpServletRequest request, HttpServletResponse response) {
        SecurityUser adminInfo = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getDetails();

        Admin admin = new Admin();
        admin.setToken(adminInfo.getAdminAccessToken());

        if (request.getParameter("defaultSoundChk") == null) {
            admin.setDefaultSoundStatus(Boolean.FALSE);
            fileUploadAdminService.putDefaultSoundStatus(admin);


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

            if (!files.isEmpty()) {
                MultipartFile[] fileArray = new MultipartFile[files.size()];
                for (int i = 0; i < files.size(); i++) {
                    if (files.get(i).getSize() > 0) {
                        fileArray[i] = files.get(i);
                    }
                }

                if (fileArray.length > 0) {
                    FileUtil fileUtil = new FileUtil();
                    fileUtil.fileUpload(fileArray, alarmFileUploadPath+"/");
//                fileUtil.fileUpload(fileArray, "c:\\");
                }
            }
        } else {
            admin.setDefaultSoundStatus(Boolean.TRUE);
            fileUploadAdminService.putDefaultSoundStatus(admin);
        }

        return "redirect:/setting-alarm";
    }


    /**
     * ?????? - ?????? ??????
     *
     * @return
     */
    @ResponseBody
    @GetMapping("/deleteAlarm")
    @CnttMethodDescription("?????? ??????")
    public int deleteAlarm(Alarm alarm) {
        SecurityUser adminInfo = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getDetails();
        alarm.setToken(adminInfo.getAdminAccessToken());

        return fileUploadAdminService.deleteAlarm(alarm);
    }


    /**
     * ?????? - ???????????? ?????????
     *
     * @return
     */
    @GetMapping("/setting-notice")
    @CnttMethodDescription("???????????? ?????????")
    public String settingNotice() { return "/setting/setting_notice"; }


    @ResponseBody
    @GetMapping("/getNoticeList")
    @CnttMethodDescription("???????????? ????????? ??????")
    public List<Notice> getNoticeList(Notice notice) {
        SecurityUser adminInfo = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getDetails();
        notice.setToken(adminInfo.getAdminAccessToken());

        List<Notice> noticeList = noticeAdminService.getNoticeList(notice);

        return noticeList;
    }


    @ResponseBody
    @GetMapping("/getNotice")
    @CnttMethodDescription("???????????? ?????? ??????")
    public Map getNotice(Notice notice) {
        SecurityUser adminInfo = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getDetails();
        notice.setToken(adminInfo.getAdminAccessToken());

        Map noticeDetail = noticeAdminService.getNotice(notice);

        return noticeDetail;
    }


    @ResponseBody
    @GetMapping("/getAdminSubGroupList")
    @CnttMethodDescription("????????? ????????? ????????? ?????? ??????")
    public List<SubGroup> getAdminSubGroupList(Notice notice) {
        SecurityUser adminInfo = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getDetails();
        notice.setToken(adminInfo.getAdminAccessToken());

        List<SubGroup> subGroupList = noticeAdminService.getSubGroupList(notice);

        return subGroupList;
    }


    @ResponseBody
    @GetMapping("/getAdminSubGroupStoreList")
    @CnttMethodDescription("????????? ???????????? ?????? ?????? ??????")
    public List<SubGroupStoreRel> getAdminSubGroupStoreList(Notice notice) {
        SecurityUser adminInfo = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getDetails();
        notice.setToken(adminInfo.getAdminAccessToken());

        List<SubGroupStoreRel> storeList = noticeAdminService.getSubGroupStoreRelList(notice);

        return storeList;
    }


    @ResponseBody
    @PutMapping("/putNotice")
    @CnttMethodDescription("???????????? ??????")
    public int putNotice(Notice notice){
        SecurityUser adminInfo = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getDetails();
        notice.setToken(adminInfo.getAdminAccessToken());
        return noticeAdminService.putNotice(notice);
    }


    @ResponseBody
    @PutMapping("/deleteNotice")
    @CnttMethodDescription("???????????? ??????")
    public int deleteNotice(Notice notice){
        SecurityUser adminInfo = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getDetails();
        notice.setToken(adminInfo.getAdminAccessToken());
        return noticeAdminService.deleteNotice(notice);
    }

    @ResponseBody
    @PutMapping("/getGroupList")
    @CnttMethodDescription("???????????? ?????? ?????? ??????")
    public List<Group> getGroupList(Notice notice){
        SecurityUser adminInfo = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getDetails();
        notice.setToken(adminInfo.getAdminAccessToken());
        return noticeAdminService.getGroupList(notice);
    }

//    @ResponseBody
    @PostMapping("/postNotice")
    @CnttMethodDescription("???????????? ??????")
    public String postNotice(Notice notice, @RequestParam("nNewFile") MultipartFile file){
        SecurityUser adminInfo = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getDetails();
        notice.setToken(adminInfo.getAdminAccessToken());

        FileUtil fileUtil = new FileUtil();
        fileUtil.fileUpload(file, noticeFileUploadPath+"/");
        // ?????? ????????? ??? ???????????? ?????? ?????? ??????                                   2018. 10. 01    Nick
        if (!file.getOriginalFilename().isEmpty()) {
            DateTimeFormatter dateformatter = DateTimeFormatter.ofPattern("yyyyMMdd");
            notice.setOriFileName(file.getOriginalFilename());
            notice.setFileName(LocalDateTime.now().format(dateformatter) + "_" + file.getOriginalFilename());
            notice.setFileSize(Long.toString(file.getSize()));
        }
        noticeAdminService.postNotice(notice);

        return "redirect:/setting-notice";
    }


    @ResponseBody
    @PutMapping("/deleteNoticeFile")
    @CnttMethodDescription("???????????? ??????")
    public int deleteNoticeFile(Notice notice){
        SecurityUser adminInfo = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getDetails();
        notice.setToken(adminInfo.getAdminAccessToken());
        notice.setOriFileName("none");
        notice.setFileName("none");
        notice.setFileSize("none");

        return noticeAdminService.putNotice(notice);
    }


    @GetMapping("/noticeFileDownload")
    @CnttMethodDescription("???????????? ???????????? ????????????")
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
            response.sendError(404, "????????? ???????????????.");
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

    /**
     * ?????? - ?????? ???????????? ????????? ?????? ??????
     * */
    @ResponseBody
    @GetMapping("/getSharedAdminInfo")
    @CnttMethodDescription("?????? ????????? ????????? ??????")
    public List<User> getSharedAdminInfo(){
        List<User> allowAdmin = new ArrayList<>();
        SecurityUser adminInfo = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getDetails();

        allowAdmin = sharedInfoService.getAllowAdminInfo(adminInfo.getAdminSeq());

        return allowAdmin;
    }

    @ResponseBody
    @GetMapping("/getSharedGroupInfo")
    @CnttMethodDescription("????????? ???????????? ?????? ??????")
    public List<Group> getSharedGroupInfo(@RequestParam(value ="sharedAdminID") Integer sharedAdminID){
        if (sharedAdminID == null){
            return new ArrayList<>();
        }

        //SecurityUser adminInfo = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getDetails();
        List<Group> groupList = sharedInfoService.getSharedGroupListForAdmin(sharedAdminID);

        return groupList;
    }

    @ResponseBody
    @GetMapping("/getSharedSubGroupInfo")
    @CnttMethodDescription("????????? ???????????? ?????? ?????? ??????")
    public List<SubGroup> getSharedSubGroupInfo(@RequestParam(value="sharedAdminID") Integer sharedAdminID,
                                                @RequestParam(value="sharedGroupID") Integer sharedGroupID){
        if (sharedAdminID == null || sharedGroupID == null){
            return new ArrayList<>();
        }

        HashMap<String, Object> map = new HashMap<>();
        map.put("sharedAdminID", sharedAdminID);
        map.put("sharedGroupID", sharedGroupID);

        List<SubGroup> subGroupList = sharedInfoService.getSharedSubGroupListForAdmin(map);

        return subGroupList;
    }

    @ResponseBody
    @GetMapping("/getSharedStoreInfo")
    @CnttMethodDescription("????????? ???????????? ?????? ??????")
    public List<Store> getSharedSubGroupInfo(@RequestParam(value="sharedAdminID") Integer sharedAdminID,
                                             @RequestParam(value="sharedGroupID") Integer sharedGroupID,
                                             @RequestParam(value = "sharedSubGroupID") Integer sharedSubGroupID){
        if (sharedAdminID == null || sharedGroupID == null || sharedSubGroupID == null){
            return new ArrayList<>();
        }

        HashMap<String, Object> map = new HashMap<>();
        map.put("sharedAdminID", sharedAdminID);
        map.put("sharedGroupID", sharedGroupID);
        map.put("sharedSubGroupID", sharedSubGroupID);

        List<Store> subGroupList = sharedInfoService.getSharedStoreListForAdmin(map);

        return subGroupList;
    }

    @ResponseBody
    @GetMapping("/getSharedInfoList")
    @CnttMethodDescription("?????? ????????? ?????? ?????????")
    public List<SharedRiderInfo> getSharedInfoList(){
        SecurityUser adminInfo = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getDetails();

        List<SharedRiderInfo> riderInfos = sharedInfoService.getSharedInfoListForAdmin(adminInfo.getAdminSeq());

        return riderInfos;
    }

    /**
     * 19.12.19 Dev Start
     * ?????? - ????????? ????????? ??????
     * ?????? ????????? ????????? ??????
     * */
    @GetMapping("/setting-shared-admin")
    @CnttMethodDescription("????????? ????????? ?????????")
    public String sharedAdminSetting(){
        return "/setting/setting_shared_admin";
    }

    /**
     * 19.12.19 Dev Start
     * ?????? - ????????? ????????? ??????
     * ?????? ????????? ????????? ??????
     * */
    @GetMapping("/setting-shared-rider")
    @CnttMethodDescription("????????? ????????? ?????????")
    public String sharedRiderSetting(){
        return "/setting/setting_shared_rider";
    }

    @ResponseBody
    @PostMapping("/save-shared-rider")
    @CnttMethodDescription("????????? ????????? ?????? ??????")
    public String postSaveSharedRider(@RequestParam(value = "sharedRiders")String strRider){
        int adminID = ((SecurityUser) SecurityContextHolder.getContext().getAuthentication().getDetails()).getAdminSeq();

        ObjectMapper mapper = new ObjectMapper();
        List<SharedRiderInfo> riderInfos = new ArrayList<>();

        try {
            riderInfos = Arrays.asList(mapper.readValue(strRider, SharedRiderInfo[].class));
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (riderInfos.stream()
                .filter(x-> x.getShared_adminid() == null || x.getShared_adminid().toString() == "" || x.getGroupid() == null || x.getGroupid().toString() == "" )
                .count() > 0){
            return "failed";
        }

        for (SharedRiderInfo rider:riderInfos
             ) {
            rider.setAdminid(adminID);
            sharedInfoService.setSharedInfoUpdate(rider);
        }

        return "success";
    }

    @ResponseBody
    @PostMapping("/update-shared-info")
    @CnttMethodDescription("????????? ?????? ??????")
    public String postSaveSharedRider(SharedRiderInfo riderInfo){
        int adminID = ((SecurityUser) SecurityContextHolder.getContext().getAuthentication().getDetails()).getAdminSeq();

        riderInfo.setAdminid(adminID);
        sharedInfoService.setSharedInfoUpdate(riderInfo);

        return "OK";
    }
}
