package kr.co.deliverydispatch.controller;

import com.google.gson.Gson;
import kr.co.cntt.core.annotation.CnttMethodDescription;
import kr.co.cntt.core.model.chat.Chat;
import kr.co.cntt.core.model.common.Common;
import kr.co.cntt.core.model.group.Group;
import kr.co.cntt.core.model.group.SubGroup;
import kr.co.cntt.core.model.group.SubGroupRiderRel;
import kr.co.cntt.core.model.group.SubGroupStoreRel;
import kr.co.cntt.core.model.rider.Rider;
import kr.co.cntt.core.model.rider.RiderApprovalInfo;
import kr.co.cntt.core.model.rider.RiderSession;
import kr.co.cntt.core.model.store.Store;
import kr.co.cntt.core.util.ShaEncoder;
import kr.co.deliverydispatch.security.SecurityUser;
import kr.co.deliverydispatch.service.CommInfoService;
import kr.co.deliverydispatch.service.CommunityService;
import kr.co.deliverydispatch.service.StoreRiderService;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;
import java.util.*;

@Slf4j
@Controller
public class RiderController {

    /**
     * 객체 주입
     */
    private StoreRiderService storeRiderService;
    private CommInfoService commInfoService;
    private CommunityService communityService;

    @Value("${spring.mvc.locale}")
    private Locale regionLocale;

    @Value("${api.cors.origin}")
    private String strApiRoot;

    @Autowired
    public RiderController(StoreRiderService storeRiderService, CommInfoService commInfoService, CommunityService communityService) {
        this.storeRiderService = storeRiderService;
        this.commInfoService = commInfoService;
        this.communityService = communityService;
    }

    /**
     * 기사현황 페이지
     */
    @GetMapping("/rider")
    public String rider(Store store, Model model) {
        SecurityUser storeInfo = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getDetails();
        store.setToken(storeInfo.getStoreAccessToken());
        Store myStore = storeRiderService.getStoreInfo(store);
        model.addAttribute("store", myStore);
        model.addAttribute("regionLocale", regionLocale);
        return "/rider/rider";
    }

    @ResponseBody
    @GetMapping("/getRiderList")
    @CnttMethodDescription("그룹소속 기사목록")
    public List<Rider> getMyRiderList(Common common){
        SecurityUser storeInfo = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getDetails();
        common.setToken(storeInfo.getStoreAccessToken());
        return storeRiderService.getRiderNow(common);
    }

    @ResponseBody
    @PutMapping("/putRiderReturnTime")
    @CnttMethodDescription("라이더 재배치")
    public Boolean putRiderReturnTime(Rider rider){
        SecurityUser storeInfo = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getDetails();
        rider.setToken(storeInfo.getStoreAccessToken());
        storeRiderService.putRiderReturnTime(rider);
        return true;
    }

    @ResponseBody
    @GetMapping("/getChatList")
    @CnttMethodDescription("채팅목록")
    public List<Chat> getChatList(Chat chat){
        SecurityUser storeInfo = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getDetails();
        chat.setToken(storeInfo.getStoreAccessToken());
        return storeRiderService.getChat(chat);
    }

    @ResponseBody
    @PostMapping("/postChat")
    @CnttMethodDescription("채팅보내기")
    public Boolean postChat(Chat chat){
        SecurityUser storeInfo = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getDetails();
        chat.setToken(storeInfo.getStoreAccessToken());
        storeRiderService.postChat(chat);
        return true;
    }

    /**
     * 20.08.07 대만 개발 요청 사항
     * # 라이더 앱에서 회원가입 프로세스
     * */
    @GetMapping("/riderApproval")
    public String riderApprovalView(Store store, Model model){

        SecurityUser storeInfo = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getDetails();
        store.setToken(storeInfo.getStoreAccessToken());
        Store myStore = storeRiderService.getStoreInfo(store);
        model.addAttribute("store", myStore);
        model.addAttribute("regionLocale", regionLocale);

        return "/rider/rider_approval";
    }

    // 라이더 승인 요청에 대한 리스트 가져오기
    @GetMapping("/getApprovalRiderList")
    @ResponseBody
    @CnttMethodDescription("라이더 승인 리스트")
    public List<RiderApprovalInfo> getApprovalRiderList(){
        SecurityUser storeInfo = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getDetails();

        Store store = new Store();
        store.setToken(storeInfo.getStoreAccessToken());
        store.setRole("ROLE_STORE");

        // 스토어 정보
        return storeRiderService.getRiderApprovalList(store);
    }

    // 승인 리스트 항목 Excel Download
    @GetMapping("/excelDownloadApprovalRiderList")
    public ModelAndView approvalRiderListforExcel(HttpServletResponse response){
        response.setHeader("Set-Cookie", "fileDownload=true; path=/");
        SecurityUser storeInfo = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getDetails();
        Store store = new Store();
        store.setToken(storeInfo.getStoreAccessToken());
        store.setRole("ROLE_STORE");

        ModelAndView modelAndView = new ModelAndView("ApprovalRiderListforExcelServiceImpl");
        List<RiderApprovalInfo> approvalInfos = storeRiderService.getRiderApprovalList(store);
        modelAndView.addObject("getApprovalRiderList", approvalInfos);

        return modelAndView;
    }

    // 라이더 승인 상태 변경 요청
    @ResponseBody
    @PostMapping("/changeApprovalStatus")
    @CnttMethodDescription("라이더 승인 상태 변경")
    public Boolean changeApprovalStatus(RiderApprovalInfo riderInfo){

        // 승인 요청인 경우는 거절한다
        if (riderInfo.getApprovalStatus().equals("1")){
            return false;
        }

        SecurityUser storeInfo = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getDetails();
        riderInfo.setToken(storeInfo.getStoreAccessToken());

        // 라이더의 상태값 체크를 위해 다시 한번 정보를 가져온다.
        RiderApprovalInfo chkRiderInfo = storeRiderService.getRiderApprovalInfo(riderInfo);

        // 값이 없는 경우
        if (chkRiderInfo == null){
            return false;
        }

        // 승인을 거부하는 경우
        if (riderInfo.getApprovalStatus().trim().equals("2") &&
                !((chkRiderInfo.getApprovalStatus().trim().equals("0") || chkRiderInfo.getApprovalStatus().trim().equals("1")))){
            log.info("changeApprovalStatus return false # New Reject");
            return false;
        }

        // 승인이 된 상태에서 취소하는 경우
        if (riderInfo.getApprovalStatus().trim().equals("3") &&
                !(chkRiderInfo.getApprovalStatus().trim().equals("1"))){
            log.info("changeApprovalStatus return false # Approval");
            return false;
        }

        // 라이더 승인 상태인 경우, Row 및 Rider Info에 삭제 처리를 적용한다.
        if (chkRiderInfo.getApprovalStatus().equals("1")){
            Rider rider = new Rider();
            rider.setId(chkRiderInfo.getRiderId());
            commInfoService.deleteRiderInfo(rider);     // 라이더 및 라이더 소속 그룹에 대한 정보를 삭제
        }

        riderInfo.setName(chkRiderInfo.getName());
        // 상태 변경 관련 UPDATE 문 실행
        storeRiderService.setRiderInfo(riderInfo);

        return true;
    }

    // 라이더 승인 상태 적용
    @ResponseBody
    @PostMapping("/approvalAccept")
    @CnttMethodDescription("라이더 승인 허용")
    public Boolean approvalAccept(RiderApprovalInfo riderInfo){
        SecurityUser storeInfo = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getDetails();
        Store myStore;

        riderInfo.setToken(storeInfo.getStoreAccessToken());

        // 라이더의 상태값 체크를 위해 다시 한번 정보를 가져온다.
        RiderApprovalInfo chkRiderInfo = storeRiderService.getRiderApprovalInfo(riderInfo);

        // 값이 없는 경우
        if (chkRiderInfo == null){
            return false;
        }

        // 승인을 요청하는 경우
        if (riderInfo.getApprovalStatus().trim().equals("1") && !chkRiderInfo.getApprovalStatus().equals("0")){
            log.info("approvalAccept return false # request Accept but Not Waiting mode");
            return false;
        }

        // 관련 라이더 정보가 있는지 확인 LOGIN ID는 중복되면 안되므로
        chkRiderInfo.setRole("ROLE_SEARCH");
        List<RiderApprovalInfo> approvalInfos = storeRiderService.getRiderApprovalList(chkRiderInfo);

        // 필터링 시작
        if (!checkExpDate(approvalInfos)){
            return false;
        }

        // 승인 실패로 인하여 등록된 라이더 정보가 있는 경우
        List<Rider> getRegistRiderInfo = storeRiderService.getRegistRiderInfoList(chkRiderInfo);

        Rider rider = new Rider();
        if (getRegistRiderInfo.size() < 1) {
            // 승인 허용 유무 절차 완료 # 승인 정보 등록

            rider.setAdminId(chkRiderInfo.getAdminId());
            rider.setType("3");
            rider.setPhone(chkRiderInfo.getPhone());
            rider.setGender(chkRiderInfo.getGender());
            rider.setAddress(chkRiderInfo.getAddress());
            rider.setWorking("0");
            rider.setStatus("0");
            rider.setLatitude(chkRiderInfo.getLatitude());
            rider.setLongitude(chkRiderInfo.getLongitude());
            rider.setVehicleNumber(chkRiderInfo.getVehicleNumber());
            rider.setWorkingHours("0|0");
            rider.setName(chkRiderInfo.getName());
            rider.setLoginId(chkRiderInfo.getLoginId());
            rider.setLoginPw(commInfoService.selectApprovalRiderPw(chkRiderInfo.getId()));
            rider.setAppType("1");

            /** #### 그룹 정보 #### */
            if (chkRiderInfo.getRiderDetail().getRiderStore() != null) {
                myStore = storeRiderService.getStoreInfo(chkRiderInfo.getRiderDetail().getRiderStore());

                Group group = new Group();
                group.setId(myStore.getGroup().getId());

                SubGroup subGroup = new SubGroup();
                subGroup.setGroupId(myStore.getSubGroup().getGroupId());
                subGroup.setId(myStore.getSubGroup().getId());

                rider.setGroup(group);
                rider.setSubGroup(subGroup);
                rider.setRiderStore(myStore);
            }

            // 채팅 User ID 등록
            commInfoService.insertChatUser(rider);
            rider.setRole("ROLE_ADD");
            commInfoService.insertRiderInfo(rider);

            riderInfo.setRiderId(rider.getId());

            // 라이더 소속 그룹 저장
            SubGroupRiderRel subGroupRiderRel = new SubGroupRiderRel();
            if (rider.getSubGroup() != null) {
                subGroupRiderRel.setSubGroupId(rider.getSubGroup().getId());
                subGroupRiderRel.setGroupId(rider.getSubGroup().getGroupId());
                subGroupRiderRel.setRiderId(rider.getId());
                subGroupRiderRel.setStoreId(chkRiderInfo.getRiderDetail().getRiderStore().getId());

                rider.setSubGroupRiderRel(subGroupRiderRel);

                commInfoService.insertSubGroupRiderRel(rider);
            }
        }else{
            Rider regRiderInfo = getRegistRiderInfo.stream()
                    .sorted(Comparator.comparing(Rider::getLocationUpdated, Comparator.nullsFirst(Comparator.naturalOrder())).reversed())
                    .findFirst().get();

            riderInfo.setRiderId(regRiderInfo.getId());
            rider.setLoginId(chkRiderInfo.getLoginId());
            rider.setLoginPw(commInfoService.selectApprovalRiderPw(chkRiderInfo.getId()));
        }

        // Token 발급을 위한 메소드 호출
        try {
            Map<String, String> resultMap = new HashMap<>();
            Map<String, String> result;
            int iCount = 0;
            boolean bToken = false;

            resultMap.put("level", "3");
            resultMap.put("loginId", rider.getLoginId());
            resultMap.put("loginPw", rider.getLoginPw());

            while (iCount < 3){
                log.info("TOKEN 발급 API 호출 시작 횟수 : [" + iCount + "]");
                try{
                    String resultJson = communityService.sendPostApiServer(strApiRoot + "/API/getToken.do", resultMap);
                    result = new Gson().fromJson(resultJson, Map.class);

                    log.info("resultJson = [" + resultJson + "]");

                    if (result.get("result").equals("1")){
                        if (result.containsKey("result")){
                            bToken = true;
                            log.info("TOKEN 발급 API 호출 완료 : TOKEN VALUE [" + result.get("token") + "] # iCount = [" + iCount + "]");
                        }else{
                            bToken = false;
                            log.info("TOKEN 발급 API 호출 완료 : TOKEN 없음 # iCount = [" + iCount + "]");
                        }

                        break;
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }

                log.info("TOKEN 발급 API 호출 완료 : 비정상");
                iCount++;
            }

            if (!bToken){
                log.info("Approval Failed # Not Found Token");
                return false;
            }


            // 상태 변경 관련 UPDATE 문 실행
            storeRiderService.setRiderInfo(riderInfo);

            // 만료 기간이 없는 경우 강제로 현재 일자롤부터 180일을 추가한다.
            if (chkRiderInfo.getSession() == null || chkRiderInfo.getSession().getExpiryDatetime().equals("")){
                log.info("유효기간 입력");
                if (chkRiderInfo.getSession() == null){
                    chkRiderInfo.setSession(new RiderSession());
                }

                try {
                    SimpleDateFormat defaultFormat = new SimpleDateFormat("yyyy-MM-dd");
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(new Date());
                    calendar.add(Calendar.YEAR, 1);

                    log.info("############ 유효기간 설정 #################");
                    log.info(defaultFormat.format(calendar.getTime()));
                    log.info("############ 유효기간 설정 #################");

                    chkRiderInfo.getSession().setExpiryDatetime(defaultFormat.format(calendar.getTime()));

                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            // Token 발행이 성공적이고, 유효기간이 있는 경우, 유효기간을 업데이트한다.
            if (chkRiderInfo.getSession() != null && chkRiderInfo.getSession().getExpiryDatetime() != null){
                // 라이더 Session 유효기간 변경
                RiderSession session = new RiderSession();
                session.setRider_id(rider.getId());
                session.setExpiryDatetime(chkRiderInfo.getSession().getExpiryDatetime());

                storeRiderService.updateRiderSession(session);
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        return true;
    }

    // 라이더 승인 상태 변경
    @ResponseBody
    @PostMapping("/changeStatus")
    @CnttMethodDescription("라이더 상태 변경 전용")
    public Boolean onlyChangeStatus(RiderApprovalInfo riderInfo){
        SecurityUser storeInfo = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getDetails();
        riderInfo.setToken(storeInfo.getStoreAccessToken());
        riderInfo.setRole("ROLE_STORE");

        // 라이더의 상태값 체크를 위해 다시 한번 정보를 가져온다.
        RiderApprovalInfo chkRiderInfo = storeRiderService.getRiderApprovalInfo(riderInfo);

        // 값이 없는 경우
        if (chkRiderInfo == null){
            return false;
        }

        switch (riderInfo.getApprovalStatus()){
            case "1":
                if (!chkRiderInfo.getApprovalStatus().equals("5")){
                    return false;
                }

                break;
            case "5":
                if (!chkRiderInfo.getApprovalStatus().equals("1")){
                    return false;
                }

                break;
            default:
                return false;
        }

        // 일시정지 상태가 된 경우 라이더 강제 OFF 시키기
        if (riderInfo.getApprovalStatus().equals("5")){
            riderInfo.setWorking("0");
        }

        // 상태 변경 관련 UPDATE 문 실행
        storeRiderService.setRiderInfo(riderInfo);

        return true;
    }

    // 라이더 상세 정보 가져오기
    @ResponseBody
    @PostMapping("/getRiderApprovalInfo")
    @CnttMethodDescription("라이더 상세 정보")
    public RiderApprovalInfo getRiderApprovalInfo(RiderApprovalInfo riderInfo){
        SecurityUser storeInfo = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getDetails();
        //store.setToken(storeInfo.getStoreAccessToken());
        riderInfo.setToken(storeInfo.getStoreAccessToken());

        // 라이더의 상태값 체크를 위해 다시 한번 정보를 가져온다.
        return storeRiderService.getRiderApprovalInfo(riderInfo);
    }

    // 라이더 유효기간 설정
    @ResponseBody
    @PostMapping("/setRiderExpDate")
    @CnttMethodDescription("라이더 유효기간 설정")
    public Boolean setRiderExpDate(RiderApprovalInfo riderInfo, String expiryDate){
        SecurityUser storeInfo = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getDetails();
        riderInfo.setToken(storeInfo.getStoreAccessToken());

        // 선택한 일자가 금일보다 작을 수 없도록 적용
        SimpleDateFormat defaultFormat = new SimpleDateFormat("yyyy-MM-dd");
        // 라이더의 상태값 체크를 위해 다시 한번 정보를 가져온다.
        RiderApprovalInfo chkRiderInfo = storeRiderService.getRiderApprovalInfo(riderInfo);

        // 비교를 위한 날짜들을 가져온다.
        Date nowDate;            // 현재 날짜
        Date regDate;            // 변경 전 날짜
        Date changeDate;          // 변경 요청 날짜

        try{
            nowDate = defaultFormat.parse(DateTime.now().toString());
            regDate = chkRiderInfo.getSession() != null && chkRiderInfo.getSession().getExpiryDatetime() != null ? defaultFormat.parse(chkRiderInfo.getSession().getExpiryDatetime()) : null;
            changeDate = defaultFormat.parse(expiryDate);

            long changeDiff = changeDate.getTime() - nowDate.getTime();
            long orgDiff = 0;

            if (regDate != null){
                orgDiff = regDate.getTime() - nowDate.getTime();
            }

            if (!(changeDiff >= 0 && orgDiff >= 0)){
                return false;
            }
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }

        // 날짜에 대한 예외처리가 완료된 후 적용
        riderInfo.setSession(new RiderSession());
        riderInfo.getSession().setExpiryDatetime(defaultFormat.format(changeDate));

        if (chkRiderInfo.getApprovalStatus().equals("0")){                       // 상태 값이 신규 요청이 경우, 임시 테이블에서 정보가 변경이 되어야함.
            storeRiderService.setRiderInfo(riderInfo);
        }else if (chkRiderInfo.getApprovalStatus().equals("1")){                 // 상태 값이 1인 경우, 라이더 정보에서 값이 변경 되어야 한다.
            riderInfo.getSession().setRider_id(chkRiderInfo.getRiderId());
            storeRiderService.updateRiderSession(riderInfo.getSession());
        }

        return true;
    }

    // 라이더 상세 정보 변경
    @ResponseBody
    @PostMapping("/changeRiderInfo")
    @CnttMethodDescription("라이더 상세 정보 변경")
    public Boolean changeRiderInfo(RiderApprovalInfo riderInfo){
        SecurityUser storeInfo = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getDetails();
        riderInfo.setToken(storeInfo.getStoreAccessToken());

        // 라이더의 원본 데이터를 가져온다.
        RiderApprovalInfo chkRiderInfo = storeRiderService.getRiderApprovalInfo(riderInfo);

        if (chkRiderInfo.getSession() == null){
            chkRiderInfo.setSession(new RiderSession());
        }

        // 직원코드가 다른 경우
        if (riderInfo.getCode() != null && !riderInfo.getCode().equals(chkRiderInfo.getCode())){
            chkRiderInfo.setCode(riderInfo.getCode());
        }

        // 번호판이 다른 경우 변경한다.
        if (riderInfo.getVehicleNumber() != null && !riderInfo.getVehicleNumber().equals(chkRiderInfo.getVehicleNumber())){
            chkRiderInfo.setVehicleNumber(riderInfo.getVehicleNumber());
        }

        if (riderInfo.getName() != null && !riderInfo.getName().equals(chkRiderInfo.getName())){
            chkRiderInfo.setName(riderInfo.getName());
        }

        // 공유 상태가 다른 경우
        if (riderInfo.getSharedStatus() != null && !riderInfo.getSharedStatus().equals(chkRiderInfo.getSharedStatus())){
            chkRiderInfo.setSharedStatus(riderInfo.getSharedStatus());
        }

        if (chkRiderInfo.getApprovalStatus().equals("1")){
            // 라이더가 승인이 된 경우 TB_RIDER에서 정보를 변경한다.
            Rider changeRider = new Rider();

            changeRider.setId(chkRiderInfo.getRiderId());
            changeRider.setVehicleNumber(riderInfo.getVehicleNumber());
            changeRider.setCode(riderInfo.getCode());
            changeRider.setName(chkRiderInfo.getName());
            changeRider.setSharedStatus(chkRiderInfo.getSharedStatus());

            commInfoService.updateRiderInfo(changeRider);
        }else{
            // 승인 이외의 정보는 Rider Approval Info에서 적용한다.
            storeRiderService.setRiderInfo(chkRiderInfo);
        }

       return true;
    }

    // 라이더 Approval Row 삭제
    @ResponseBody
    @PostMapping("/deleteApprovalRiderRowData")
    @CnttMethodDescription("라이더 Approval Row 데이터 삭제")
    public Boolean deleteApprovalRiderRowData(RiderApprovalInfo riderInfo){
        SecurityUser storeInfo = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getDetails();
        riderInfo.setToken(storeInfo.getStoreAccessToken());

        int iCount = storeRiderService.deleteApprovalRiderRowData(riderInfo);
        log.info("라이더 Approval Row 데이터 삭제 완료 # id = [" + riderInfo.getId() + "] # [" + iCount + "]");

        return true;
    }

    // 라이더 비밀번호 초기화
    @ResponseBody
    @PutMapping("putRiderPwReset")
    @CnttMethodDescription("라이더 비밀번호 초기화")
    public String resetRiderPwd(Rider rider){
        SecurityUser storeInfo = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getDetails();
        rider.setToken(storeInfo.getStoreAccessToken());

        ShaEncoder sha = new ShaEncoder(512);
        rider.setLoginPw(sha.encode("1111"));

        int iResetPwd = storeRiderService.resetRiderPassword(rider);

        if (iResetPwd == 0) {
            return "err";
        } else {
            return "ok";
        }

    }

    @ResponseBody
    @PostMapping("getSharedStoreList")
    @CnttMethodDescription("공유 가능한 매장 정보 가져오기")
    public List<Store> sharedStoreList(Rider rider){
        SecurityUser storeInfo = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getDetails();
        rider.setToken(storeInfo.getStoreAccessToken());
        rider.setRole("ROLE_STORE");

        List<Store> sharedStoreList = storeRiderService.getSharedStoreList(rider);

        return  sharedStoreList;
    }

    @ResponseBody
    @PostMapping("regSharedStore")
    @CnttMethodDescription("타 매장에 라이더 공유")
    public boolean postSharedStore(Rider rider){
        SecurityUser storeInfo = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getDetails();
        rider.setToken(storeInfo.getStoreAccessToken());

        // 현재 라이더의 상태 값 가져오기
        Rider riderInfo = commInfoService.selectRiderInfo(rider);
        // 공유될 매장의 정보 가져오기
        Store searchStore = new Store();
        searchStore.setRole("ROLE_SYSTEM");
        searchStore.setId(rider.getSharedStoreId());

        Store store = storeRiderService.getStoreInfo(searchStore);

        // 라이더의 정보를 저장
        rider.setAdminId(riderInfo.getAdminId());
        rider.setType("2");


        // 라이더에게 배정할 타 매장의 신규 정보를 저장해 놓는다.
        rider.setSubGroupStoreRel(new SubGroupStoreRel());
        rider.getSubGroupStoreRel().setGroupId(store.getSubGroup().getGroupId());
        rider.getSubGroupStoreRel().setSubGroupId(store.getSubGroup().getId());

        rider.setSharedStore("1");
        rider.setAdminId(riderInfo.getAdminId());

        commInfoService.updateRiderInfo(rider);

        if (riderInfo.getSharedStore() != null && riderInfo.getSharedStore().equals("Y")){
            // 라이더에 대한 타 매장 정보를 모두 삭제
            storeRiderService.deleteSharedStoreInfo(rider);
        }

        // 라이더 관련 정보 생성 후 전달
        storeRiderService.regSharedStoreInfo(rider);

        return true;
    }

    @ResponseBody
    @PostMapping("unsharedStore")
    @CnttMethodDescription("타 매장에 공유된 라이더의 공유 취소")
    public boolean unsharedStore(Rider rider){
        rider.setSharedStore("0");

        commInfoService.updateRiderInfo(rider);

        // 등록되어 있던 라이더 정보 삭제
        storeRiderService.deleteSharedStoreInfo(rider);

        return  true;
    }


    // 라이더 상태 및 유효기간을 체크한다
    // true = 가입 가능
    private Boolean checkExpDate(List<RiderApprovalInfo> approvalInfos){
        if (approvalInfos == null){
            return false;
        }

        // 유효기간이 안 지난 데이터를 가져온다.
        long underExpDate = approvalInfos.stream().filter( x->{
            SimpleDateFormat fullFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

            if (x.getSession() != null){
                Date nowDate;
                Date expDate;
                try {
                    nowDate = dateFormat.parse(dateFormat.format(new Date()));
                    expDate = dateFormat.parse(dateFormat.format(fullFormat.parse(x.getSession().getExpiryDatetime())));

                    if (expDate.getTime() >= nowDate.getTime()){
                        return x.getApprovalStatus().equals("1");
                    }else{
                        return false;
                    }
                }catch (Exception e){
                    return true;
                }

            }else{      // 유효기간이 없는 경우는 유효기간이 오버되지 않았으므로,
                return x.getApprovalStatus().equals("1");
            }
        }).count();

        return underExpDate <= 0;
    }
}
