package kr.co.cntt.deliverydispatchadmin.controller;

import com.google.gson.Gson;
import kr.co.cntt.core.annotation.CnttMethodDescription;
import kr.co.cntt.core.model.common.Common;
import kr.co.cntt.core.model.group.Group;
import kr.co.cntt.core.model.group.SubGroupRiderRel;
import kr.co.cntt.core.model.group.SubGroupStoreRel;
import kr.co.cntt.core.model.rider.Rider;
import kr.co.cntt.core.model.store.Store;
import kr.co.cntt.core.service.admin.StaffAdminService;
import kr.co.cntt.deliverydispatchadmin.security.SecurityUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Controller
public class StaffController {



    /**
     * 객체 주입
     */
    StaffAdminService staffAdminService;


    @Autowired
    public StaffController(StaffAdminService staffAdminService){
        this.staffAdminService = staffAdminService;
    }


    /**
     * 매장관리 페이지
     *
     * @return
     */
    @GetMapping("/staff")
    @CnttMethodDescription("직원 페이지 조회")
    public String staff(Rider rider, @RequestParam(required=false) String frag, Model model) {
        // ADMIN 정보
        SecurityUser adminInfo = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getDetails();
        log.info("===============> adminInfo.getAdminAccessToken()    : {}", adminInfo.getAdminAccessToken());

        rider.setToken(adminInfo.getAdminAccessToken());

        List<Rider> riderList = staffAdminService.selectRiderList(rider);

        model.addAttribute("riderList", riderList);
        model.addAttribute("jsonList", new Gson().toJson(riderList));

        log.info("json : {}", new Gson().toJson(riderList));

        return "/staff/staff";
    }

    /**
     * 라이더 리스트 조회
     *
     * @return
     */
    @ResponseBody
    @GetMapping("/getRiderList")
    @CnttMethodDescription("직원 페이지 조회")
    public List<Rider> getRiderList(@RequestParam(required=false) String frag) {
        Rider rider = new Rider();

        // ADMIN 정보
        SecurityUser adminInfo = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getDetails();

        log.info("===============> adminInfo.getAdminAccessToken()    : {}", adminInfo.getAdminAccessToken());

        rider.setToken(adminInfo.getAdminAccessToken());

        List<Rider> riderList = staffAdminService.selectRiderList(rider);

        return riderList;
    }


    // map 적용!!!
    @ResponseBody
    @GetMapping("/getRiderDetail")
    @CnttMethodDescription("기사 상세 보기")
    public Map<String, Object> getRiderDetail(
            @RequestParam("storeId") String storeId,
            @RequestParam("riderId") String riderId,
            @RequestParam("groupId") String groupId,
            @RequestParam("subGroupId") String subGroupId) {

        Rider rider = new Rider();
        SubGroupRiderRel subGroupRiderRel = new SubGroupRiderRel();
        Store store = new Store();

        // ADMIN 정보
        SecurityUser adminInfo = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getDetails();
        // token 부여
        rider.setToken(adminInfo.getAdminAccessToken());

        store.setToken(adminInfo.getAdminAccessToken());

        log.info("===============> adminInfo.getAdminAccessToken()    : {}", adminInfo.getAdminAccessToken());
        // param storeId , IsAdmin
        rider.setId(riderId);


        subGroupRiderRel.setSubGroupId(subGroupId);
        subGroupRiderRel.setGroupId(groupId);
        subGroupRiderRel.setRiderId(riderId);
        rider.setSubGroupRiderRel(subGroupRiderRel);

        // store 정보 조회
        Rider A_Rider = staffAdminService.getRiderInfo(rider);

        // storeList
        List<Store> storeList = staffAdminService.selectStoreList(store);

//        List<Rider> riderGroup = staffAdminService.selectSubgroupRiderRels(rider);



        // map 으로 값 전달
        Map<String, Object> map = new HashMap<>();
        map.put("A_Rider", A_Rider);
        map.put("storeList", storeList);
//        map.put("riderGroup", riderGroup);

        return map;
    }


    @ResponseBody
    @PutMapping("/putRiderDetail")
    @CnttMethodDescription("기사 정보 수정")
    public String putStoreDetail(
            @RequestParam("riderId") String riderId,
            @RequestParam("storeId") String storeId,
            @RequestParam("code") String code,
            @RequestParam("name") String name,
            @RequestParam("gender") String gender,
            @RequestParam("groupId") String groupId,
            @RequestParam("subGroupId") String subGroupId,
            @RequestParam("phone") String phone,
            @RequestParam("emergencyPhone") String emergencyPhone,
            @RequestParam("address") String address,
            @RequestParam("teenager") String teenager,
            @RequestParam("workingHours") String workingHours,
            @RequestParam("restHours") String restHours,
            @RequestParam("vehicleNumber") String vehicleNumber
    ) {
        Rider rider = new Rider();

        // ADMIN 정보
        SecurityUser adminInfo = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getDetails();
        // token 부여
        rider.setToken(adminInfo.getAdminAccessToken());

        log.info("===============> adminInfo.getAdminAccessToken()    : {}", adminInfo.getAdminAccessToken());

        // param storeId
        rider.setId(riderId);
        rider.setIsAdmin("1");
        rider.setCode(code);
        rider.setName(name);
        rider.setGender(gender);
        rider.setEmergencyPhone(emergencyPhone);
        rider.setPhone(phone);
        rider.setAddress(address);
        rider.setTeenager(teenager);
        rider.setWorkingHours(workingHours);
        rider.setRestHours(restHours);
        rider.setVehicleNumber(vehicleNumber);

        // group model 생성
        Group group = new Group();
        group.setId(groupId);

        rider.setGroup(group);

        // subgroup model 생성
        SubGroupRiderRel subGroupRiderRel = new SubGroupRiderRel();
        subGroupRiderRel.setSubGroupId(subGroupId);
        subGroupRiderRel.setGroupId(groupId);
        subGroupRiderRel.setStoreId(storeId);
        subGroupRiderRel.setRiderId(riderId);
        rider.setSubGroupRiderRel(subGroupRiderRel);

        int A_Rider = staffAdminService.updateRiderInfo(rider);
        int A_Store = 0;

        A_Store = staffAdminService.updateRiderStore(rider);

        if (A_Rider == 0) {
            return "err";
        } else {
            return "ok";
        }
    }

    @ResponseBody
    @PostMapping("/postRider")
    @CnttMethodDescription("기사 등록")
    public String postRider(
                             @RequestParam("loginId") String loginId,
                             @RequestParam("loginPw") String loginPw,
                             @RequestParam("storeId") String storeId,
                             @RequestParam("code") String code,
                             @RequestParam("name") String name,
                             @RequestParam("gender") String gender,
                             @RequestParam("groupId") String groupId,
                             @RequestParam("subGroupId") String subGroupId,
                             @RequestParam("phone") String phone,
                             @RequestParam("emergencyPhone") String emergencyPhone,
                             @RequestParam("address") String address,
                             @RequestParam("teenager") String teenager,
                             @RequestParam("workingHours") String workingHours,
                             @RequestParam("restHours") String restHours,
                             @RequestParam("vehicleNumber") String vehicleNumber)
    {
        Rider rider = new Rider();

        SecurityUser adminInfo = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getDetails();
        // token 부여
        rider.setToken(adminInfo.getAdminAccessToken());

        log.info("===============> adminInfo.getAdminAccessToken()    : {}", adminInfo.getAdminAccessToken());

        rider.setLoginId(loginId);
        rider.setLoginPw(loginPw);
        rider.setCode(code);
        rider.setName(name);
        rider.setGender(gender);

        rider.setEmergencyPhone(emergencyPhone);
        rider.setPhone(phone);
        rider.setAddress(address);
        rider.setTeenager(teenager);
        rider.setWorkingHours(workingHours);
        rider.setRestHours(restHours);
        rider.setVehicleNumber(vehicleNumber);
        // group model 생성
        Group group = new Group();
        group.setId(groupId);
        rider.setGroup(group);

        rider.setType("2");

        // subgroup model 생성
        SubGroupRiderRel subGroupRiderRel = new SubGroupRiderRel();
        subGroupRiderRel.setSubGroupId(subGroupId);
        subGroupRiderRel.setGroupId(groupId);
        subGroupRiderRel.setStoreId(storeId);
        rider.setSubGroupRiderRel(subGroupRiderRel);


        staffAdminService.insertChatUser(rider);

        int A_Rider = staffAdminService.insertRider(rider);
        int A_Group = 0 ;

        log.info("@@@@@@@@@@@@@@@@@groupinsert@@@@@@@@@@@@@@@@@@@@");
        A_Group = staffAdminService.insertSubGroupRiderRel(rider);

        if (A_Rider == 0 && A_Group == 0 ) {
            return "err";
        } else {
            return "ok";
        }
    }
    // 기사 등록시 매장 리스트 불러오기
    @ResponseBody
    @GetMapping("/getRiderStoreList")
    @CnttMethodDescription("상점 리스트 불러오기")
    public List<Store> getStoreList(@RequestParam(required=false) String frag) {
        Store store = new Store();

        // ADMIN 정보
        SecurityUser adminInfo = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getDetails();
        // token 부여
        store.setToken(adminInfo.getAdminAccessToken());

        log.info("===============> adminInfo.getAdminAccessToken()    : {}", adminInfo.getAdminAccessToken());

        // storeList
        List<Store> storeList = staffAdminService.selectStoreList(store);


        // 리스트 확인
        if (storeList.size() == 0) {
            log.info("0000000000000000000000");
        } else {
            for (Store s : storeList) {
                log.info("@@" + s.getName());
            }
        }
        return storeList;

    }

    // 매장 선택시 매장 정보 불러오기
    @ResponseBody
    @GetMapping("/getRiderStoreInfo")
    @CnttMethodDescription("상점 리스트 불러오기")
    public Store getRiderStoreInfo(
            @RequestParam("storeId") String storeId,

            @RequestParam(required=false) String frag) {
        Store store = new Store();
        // ADMIN 정보
        SecurityUser adminInfo = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getDetails();
        // token 부여
        store.setToken(adminInfo.getAdminAccessToken());

        log.info("===============> adminInfo.getAdminAccessToken()    : {}", adminInfo.getAdminAccessToken());


        store.setId(storeId);
        store.setIsAdmin("1");


        // storeList
        Store A_Store = staffAdminService.selectStoreInfo(store);

        log.info("상점 리스트 아이디 그룹 불러와"+A_Store);

        return A_Store;

    }

    @ResponseBody
    @PutMapping("/deleteRider")
    @CnttMethodDescription("기사 삭제")
    public boolean deleteRider( @RequestParam("riderId") String riderId
                                  ){
        // group model 생성
        Rider rider = new Rider();

        // ADMIN 정보
        SecurityUser adminInfo = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getDetails();
        // token 부여
        rider.setToken(adminInfo.getAdminAccessToken());

        log.info("===============> adminInfo.getAdminAccessToken()    : {}", adminInfo.getAdminAccessToken());
        rider.setId(riderId);


        if(staffAdminService.deleteRider(rider) == 0) {
            return false;
        }
        return true;

    }






}
