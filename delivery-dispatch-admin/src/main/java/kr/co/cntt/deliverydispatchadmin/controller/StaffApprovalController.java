package kr.co.cntt.deliverydispatchadmin.controller;

import kr.co.cntt.core.annotation.CnttMethodDescription;
import kr.co.cntt.core.model.admin.Admin;
import kr.co.cntt.core.model.group.Group;
import kr.co.cntt.core.model.group.SubGroup;
import kr.co.cntt.core.model.group.SubGroupRiderRel;
import kr.co.cntt.core.model.group.SubGroupStoreRel;
import kr.co.cntt.core.model.rider.Rider;
import kr.co.cntt.core.model.rider.RiderApprovalInfo;
import kr.co.cntt.core.model.rider.RiderSession;
import kr.co.cntt.core.model.rider.RiderSharedInfo;
import kr.co.cntt.core.model.store.Store;
import kr.co.cntt.core.service.admin.StaffApprovalAdminService;
import kr.co.cntt.core.util.ShaEncoder;
import kr.co.cntt.deliverydispatchadmin.security.SecurityUser;
import kr.co.cntt.deliverydispatchadmin.security.TokenManager;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;
import java.util.*;

@Slf4j
@Controller
public class StaffApprovalController {
    @Autowired
    private TokenManager tokenManager;

    /**
     * 객체 주입
     */
    StaffApprovalAdminService staffApprovalAdminService;

    @Autowired
    public StaffApprovalController(StaffApprovalAdminService staffApprovalAdminService){
        this.staffApprovalAdminService = staffApprovalAdminService;
    }

    /**
     * Staff Approval 목록 호출
     * */
    @GetMapping("/staffApproval")
    @CnttMethodDescription("직원 승인 목록")
    public String staffApproval(RiderApprovalInfo approvalInfo, Model model){

        return "/staff/staff_approval";
    }

    // 라이더 승인 요청에 대한 리스트 가져오기
    @GetMapping("/getApprovalRiderList")
    @ResponseBody
    @CnttMethodDescription("라이더 승인 리스트")
    public List<RiderApprovalInfo> getApprovalRiderList(Model model){
        SecurityUser adminInfo = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getDetails();

        Admin admin = new Admin();
        admin.setToken(adminInfo.getAdminAccessToken());
        admin.setRole("ROLE_ADMIN");

        // 스토어 정보
        List<RiderApprovalInfo> approvalRider = staffApprovalAdminService.getRiderApprovalList(admin);

        return approvalRider;
    }

    // 승인 리스트 항목 Excel Download
    @GetMapping("/excelDownloadApprovalRiderListforAdmin")
    public ModelAndView approvalRiderListforExcel(HttpServletResponse response){
        response.setHeader("Set-Cookie", "fileDownload=true; path=/");
        SecurityUser adminInfo = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getDetails();
        Admin admin = new Admin();
        admin.setToken(adminInfo.getAdminAccessToken());
        admin.setRole("ROLE_ADMIN");

        ModelAndView modelAndView = new ModelAndView("ApprovalRiderListAtAdminforExcelServiceImpl");
        List<RiderApprovalInfo> approvalInfos = staffApprovalAdminService.getRiderApprovalList(admin);
        modelAndView.addObject("getApprovalRiderList", approvalInfos);

        return modelAndView;
    }

    // 라이더 승인 상태 변경 요청
    @ResponseBody
    @PostMapping("/changeApprovalStatus")
    @CnttMethodDescription("라이더 승인 거절 상태 변경")
    public Boolean changeApprovalStatus(RiderApprovalInfo riderInfo){

        // 승인 요청인 경우는 거절한다
        if (riderInfo.getApprovalStatus().equals("1")){
            return false;
        }

        SecurityUser adminInfo = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getDetails();
        riderInfo.setToken(adminInfo.getAdminAccessToken());

        // 라이더의 상태값 체크를 위해 다시 한번 정보를 가져온다.
        RiderApprovalInfo chkRiderInfo = staffApprovalAdminService.getRiderApprovalInfo(riderInfo);

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
            log.info("changeApprovalStatus return false # Reject after Approval");
            return false;
        }

        // 라이더 승인 상태인 경우, Row 및 Rider Info에 삭제 처리를 적용한다.
        if (chkRiderInfo.getApprovalStatus().equals("1")){
            Rider rider = new Rider();
            rider.setId(chkRiderInfo.getRiderId());
            staffApprovalAdminService.deleteRiderInfo(rider);     // 라이더 및 라이더 소속 그룹에 대한 정보를 삭제
        }

        // 상태 변경 관련 UPDATE 문 실행
        staffApprovalAdminService.setRiderInfo(riderInfo);

        return true;
    }

    // 라이더 승인 상태 적용
    @ResponseBody
    @PostMapping("/approvalAccept")
    @CnttMethodDescription("라이더 승인 허용")
    public Boolean approvalAccept(RiderApprovalInfo riderInfo){
        SecurityUser adminInfo = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getDetails();
        Store myStore = null;
        //store.setToken(storeInfo.getStoreAccessToken());
        riderInfo.setToken(adminInfo.getAdminAccessToken());
        riderInfo.setRole("ROLE_ADMIN");

        // 라이더의 상태값 체크를 위해 다시 한번 정보를 가져온다.
        RiderApprovalInfo chkRiderInfo = staffApprovalAdminService.getRiderApprovalInfo(riderInfo);

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
        List<RiderApprovalInfo> approvalInfos = staffApprovalAdminService.getRiderApprovalList(chkRiderInfo);

        // 필터링 시작
        if (!checkExpDate(approvalInfos)){
            return false;
        }

        // 승인 허용 유무 절차 완료 # 승인 정보 등록
        Rider rider = new Rider();

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
        rider.setLoginPw(staffApprovalAdminService.selectApprovalRiderPw(chkRiderInfo.getId()));
        rider.setAppType("1");

        /** #### 그룹 정보 #### */
        if (chkRiderInfo.getRiderDetail().getRiderStore() != null){
            myStore = staffApprovalAdminService.selectStoreInfo(chkRiderInfo.getRiderDetail().getRiderStore());

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
        staffApprovalAdminService.insertChatUser(rider);
        rider.setRole("ROLE_ADD");
        staffApprovalAdminService.insertRider(rider);

        riderInfo.setRiderId(rider.getId());

        // 라이더 소속 그룹 저장
        SubGroupRiderRel subGroupRiderRel = new SubGroupRiderRel();
        if (rider.getSubGroup() != null){
            subGroupRiderRel.setSubGroupId(rider.getSubGroup().getId());
            subGroupRiderRel.setGroupId(rider.getSubGroup().getGroupId());
            subGroupRiderRel.setRiderId(rider.getId());
            subGroupRiderRel.setStoreId(chkRiderInfo.getRiderDetail().getRiderStore().getId());

            rider.setSubGroupRiderRel(subGroupRiderRel);

            staffApprovalAdminService.insertSubGroupRiderRel(rider);
        }

        // Token 발급을 위한 메소드 호출
        try {
            String riderSessionToken = tokenManager.getToken("3", rider.getLoginId(), rider.getLoginPw());
            rider.setAccessToken(riderSessionToken);

            staffApprovalAdminService.insertAdminRiderSession(rider);

            // 상태 변경 관련 UPDATE 문 실행
            staffApprovalAdminService.setRiderInfo(riderInfo);

            // 만료 기간이 없는 경우 강제로 현재 일자롤부터 180일을 추가한다.
            if (chkRiderInfo.getSession() == null || chkRiderInfo.getSession().getExpiryDatetime() == ""){
                if (chkRiderInfo.getSession() == null){
                    chkRiderInfo.setSession(new RiderSession());
                }

                try {
                    SimpleDateFormat defaultFormat = new SimpleDateFormat("yyyy-MM-dd");
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(new Date());
                    calendar.add(Calendar.YEAR, 1);

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

                staffApprovalAdminService.updateRiderSession(session);
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
        SecurityUser adminInfo = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getDetails();
        riderInfo.setToken(adminInfo.getAdminAccessToken());
        riderInfo.setRole("ROLE_ADMIN");

        // 라이더의 상태값 체크를 위해 다시 한번 정보를 가져온다.
        RiderApprovalInfo chkRiderInfo = staffApprovalAdminService.getRiderApprovalInfo(riderInfo);

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
        staffApprovalAdminService.setRiderInfo(riderInfo);

        return true;
    }

    // 라이더 상세 정보 가져오기
    @ResponseBody
    @PostMapping("/getRiderApprovalInfo")
    @CnttMethodDescription("라이더 상세 정보")
    public RiderApprovalInfo getRiderApprovalInfo(RiderApprovalInfo riderInfo){
        SecurityUser adminInfo = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getDetails();
        //store.setToken(storeInfo.getStoreAccessToken());
        riderInfo.setToken(adminInfo.getAdminAccessToken());

        // 라이더의 상태값 체크를 위해 다시 한번 정보를 가져온다.
        return staffApprovalAdminService.getRiderApprovalInfo(riderInfo);
    }

    // 라이더 유효기간 설정
    @ResponseBody
    @PostMapping("/setRiderExpDate")
    @CnttMethodDescription("라이더 유효기간 설정")
    public Boolean setRiderExpDate(RiderApprovalInfo riderInfo, String expiryDate){
        SecurityUser adminInfo = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getDetails();
        riderInfo.setToken(adminInfo.getAdminAccessToken());

        // 선택한 일자가 금일보다 작을 수 없도록 적용
        SimpleDateFormat defaultFormat = new SimpleDateFormat("yyyy-MM-dd");
        // 라이더의 상태값 체크를 위해 다시 한번 정보를 가져온다.
        RiderApprovalInfo chkRiderInfo = staffApprovalAdminService.getRiderApprovalInfo(riderInfo);

        // 비교를 위한 날짜들을 가져온다.
        Date nowDate = null;            // 현재 날짜
        Date regDate = null;            // 변경 전 날짜
        Date changeDate = null;          // 변경 요청 날짜

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
            staffApprovalAdminService.setRiderInfo(riderInfo);
        }else if (chkRiderInfo.getApprovalStatus().equals("1")){                 // 상태 값이 1인 경우, 라이더 정보에서 값이 변경 되어야 한다.
            riderInfo.getSession().setRider_id(chkRiderInfo.getRiderId());
            staffApprovalAdminService.updateRiderSession(riderInfo.getSession());
        }

        return true;
    }

    // 라이더 상세 정보 변경
    @ResponseBody
    @PostMapping("/changeRiderInfo")
    @CnttMethodDescription("라이더 상세 정보 변경")
    public Boolean changeRiderInfo(RiderApprovalInfo riderInfo){
        SecurityUser adminInfo = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getDetails();
        riderInfo.setToken(adminInfo.getAdminAccessToken());

        boolean bSharedStatus = false;

        // 라이더의 원본 데이터를 가져온다.
        RiderApprovalInfo chkRiderInfo = staffApprovalAdminService.getRiderApprovalInfo(riderInfo);

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
            bSharedStatus = true;
        }

        if (chkRiderInfo.getApprovalStatus().equals("1")){
            // 라이더가 승인이 된 경우 TB_RIDER에서 정보를 변경한다.
            Rider changeRider = new Rider();

            changeRider.setId(chkRiderInfo.getRiderId());
            changeRider.setVehicleNumber(riderInfo.getVehicleNumber());
            changeRider.setCode(riderInfo.getCode());
            changeRider.setName(riderInfo.getName());
            changeRider.setSharedStatus(chkRiderInfo.getSharedStatus());

            staffApprovalAdminService.updateRiderInfo(changeRider);
        }else{
            // 승인 이외의 정보는 Rider Approval Info에서 적용한다.
            staffApprovalAdminService.setRiderInfo(chkRiderInfo);
        }

        // 라이더 정보가 변경이 완료 된 후에 shared History를 반영한다.
        if (bSharedStatus){
            RiderSharedInfo sharedStatus = new RiderSharedInfo();
            sharedStatus.setRiderId(chkRiderInfo.getRiderId());
            sharedStatus.setReqUser("1");
            sharedStatus.setSharedType(1);
            if (chkRiderInfo.getSharedStatus().equals("0")){
                // 반전 값을 넣기 위함임
                sharedStatus.setBeforeStatus(1);
                sharedStatus.setAfterStatus(0);
            }else {
                // 반전 값을 넣기 위함임
                sharedStatus.setBeforeStatus(0);
                sharedStatus.setAfterStatus(1);
            }

            staffApprovalAdminService.insertSharedHistory(sharedStatus);
        }

        return true;
    }

    // 라이더 Approval Row 삭제
    @ResponseBody
    @PostMapping("/deleteApprovalRiderRowData")
    @CnttMethodDescription("라이더 Approval Row 데이터 삭제")
    public Boolean deleteApprovalRiderRowData(RiderApprovalInfo riderInfo){
        SecurityUser adminInfo = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getDetails();
        riderInfo.setToken(adminInfo.getAdminAccessToken());

        int iCount = staffApprovalAdminService.deleteApprovalRiderRowData(riderInfo);
        log.info("라이더 Approval Row 데이터 삭제 완료 # id = [" + riderInfo.getId() + "] # [" + iCount + "]");

        return true;
    }


    /**
     * 라이더 비밀번호 초기화
     *
     * @return
     */
    @ResponseBody
    @PutMapping("putRiderPwReset")
    @CnttMethodDescription("라이더 비밀번호 초기화")
    public String resetRiderPw(Rider rider){
        SecurityUser adminInfo = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getDetails();
        rider.setToken(adminInfo.getAdminAccessToken());

        ShaEncoder sha = new ShaEncoder(512);
        rider.setLoginPw(sha.encode("1111"));

        int iResetPwd = staffApprovalAdminService.resetRiderPassword(rider);

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
        SecurityUser adminInfo = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getDetails();
        rider.setToken(adminInfo.getAdminAccessToken());
        rider.setRole("ROLE_ADMIN");

        List<Store> sharedStoreList = staffApprovalAdminService.getSharedStoreList(rider);

        return  sharedStoreList;
    }

    @ResponseBody
    @PostMapping("regSharedStore")
    @CnttMethodDescription("타 매장에 라이더 공유")
    public boolean postSharedStore(Rider rider){
        SecurityUser adminInfo = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getDetails();
        rider.setToken(adminInfo.getAdminAccessToken());

        // 현재 라이더의 상태 값 가져오기
        Rider riderInfo = staffApprovalAdminService.getRiderInfo(rider);
        // 공유될 매장의 정보 가져오기
        Store searchStore = new Store();
        searchStore.setRole("ROLE_SYSTEM");
        searchStore.setId(rider.getSharedStoreId());

        Store store = staffApprovalAdminService.selectStoreInfo(searchStore);

        // 라이더의 정보를 저장
        rider.setAdminId(riderInfo.getAdminId());
        rider.setType("1");


        // 라이더에게 배정할 타 매장의 신규 정보를 저장해 놓는다.
        rider.setSubGroupStoreRel(new SubGroupStoreRel());
        rider.getSubGroupStoreRel().setGroupId(store.getSubGroup().getGroupId());
        rider.getSubGroupStoreRel().setSubGroupId(store.getSubGroup().getId());

        rider.setSharedStore("1");
        rider.setAdminId(riderInfo.getAdminId());

        staffApprovalAdminService.updateRiderInfo(rider);

        if (riderInfo.getSharedStore() != null && riderInfo.getSharedStore().equals("Y")){
            // 라이더에 대한 타 매장 정보를 모두 삭제
            staffApprovalAdminService.deleteSharedStoreInfo(rider);
        }

        // 라이더 관련 정보 생성 후 전달
        staffApprovalAdminService.regSharedStoreInfo(rider);

        return true;
    }

    @ResponseBody
    @PostMapping("unsharedStore")
    @CnttMethodDescription("타 매장에 공유된 라이더의 공유 취소")
    public boolean unsharedStore(Rider rider){
        rider.setSharedStore("0");

        staffApprovalAdminService.updateRiderInfo(rider);

        // 등록되어 있던 라이더 정보 삭제
        staffApprovalAdminService.deleteSharedStoreInfo(rider);

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
                        if (x.getApprovalStatus().equals("1")){
                            return true;
                        }else{
                            return false;
                        }
                    }else{
                        return false;
                    }
                }catch (Exception e){
                    return true;
                }

            }else{      // 유효기간이 없는 경우는 유효기간이 오버되지 않았으므로,
                if (x.getApprovalStatus().equals("1")){
                    return true;
                }else{
                    return false;
                }
            }
        }).count();

        if (underExpDate > 0){
            return false;
        }else{
            return true;
        }
    }
}
