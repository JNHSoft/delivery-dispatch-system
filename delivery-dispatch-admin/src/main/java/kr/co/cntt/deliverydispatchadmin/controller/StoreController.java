package kr.co.cntt.deliverydispatchadmin.controller;

import com.google.gson.Gson;
import kr.co.cntt.core.annotation.CnttMethodDescription;
import kr.co.cntt.core.model.admin.Admin;
import kr.co.cntt.core.model.group.Group;
import kr.co.cntt.core.model.group.SubGroup;
import kr.co.cntt.core.model.group.SubGroupStoreRel;
import kr.co.cntt.core.model.login.User;
import kr.co.cntt.core.model.store.Store;
import kr.co.cntt.core.service.admin.StoreAdminService;
import kr.co.cntt.core.util.Geocoder;
import kr.co.cntt.core.util.MD5Encoder;
import kr.co.cntt.core.util.ShaEncoder;
import kr.co.cntt.deliverydispatchadmin.enums.SessionEnum;
import kr.co.cntt.deliverydispatchadmin.security.SecurityUser;
import kr.co.cntt.deliverydispatchadmin.security.TokenManager;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Controller
public class StoreController {


    /**
     * 객체 주입
     */
    StoreAdminService storeAdminService;

    @Autowired
    private TokenManager tokenManager;

    @Autowired
    public StoreController(StoreAdminService storeAdminService){
        this.storeAdminService = storeAdminService;
    }


    /**
     * 매장관리 페이지
     *
     * @return
     */
    @GetMapping("/store")
    @CnttMethodDescription("매장 페이지 조회")
    public String store(Store store, @RequestParam(required=false) String frag, Model model) {
        // ADMIN 정보
        SecurityUser adminInfo = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getDetails();
        log.info("===============> adminInfo.getAdminAccessToken()    : {}", adminInfo.getAdminAccessToken());

        store.setToken(adminInfo.getAdminAccessToken());

//        List<Store> storeList = storeAdminService.selectStoreList(store);
//
//        model.addAttribute("storeList", storeList);
//        model.addAttribute("jsonList", new Gson().toJson(storeList));
//
//        log.info("json : {}", new Gson().toJson(storeList));

        return "/store/store";
    }

    /**
     * 매장 리스트 조회
     *
     * @return
     */
    @ResponseBody
    @GetMapping("/getStoreList")
    @CnttMethodDescription("매장 리스트 조회")
    public List<Store> getStoreList(@RequestParam(required=false) String frag) {
        Store store = new Store();

        // ADMIN 정보
        SecurityUser adminInfo = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getDetails();

        log.info("===============> adminInfo.getAdminAccessToken()    : {}", adminInfo.getAdminAccessToken());

        store.setToken(adminInfo.getAdminAccessToken());

        List<Store> storeList = storeAdminService.selectStoreList(store);

        return storeList;
    }


    // map 적용!!!
    @ResponseBody
    @GetMapping("/getStoreDetail")
    @CnttMethodDescription("매장 상세 보기")
    public Map<String, Object> getStoreDetail(@RequestParam("storeId") String storeId,
                                              @RequestParam("groupId") String groupId,
                                              @RequestParam("subGroupId") String subGroupId) {

        Store store = new Store();
        SubGroupStoreRel subGroupStoreRel = new SubGroupStoreRel();

        // ADMIN 정보
        SecurityUser adminInfo = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getDetails();
        // token 부여
        store.setToken(adminInfo.getAdminAccessToken());

        log.info("===============> adminInfo.getAdminAccessToken()    : {}", adminInfo.getAdminAccessToken());
        // param storeId , IsAdmin
        store.setId(storeId);
        store.setIsAdmin("1");

        subGroupStoreRel.setSubGroupId(subGroupId);
        subGroupStoreRel.setGroupId(groupId);
        store.setSubGroupStoreRel(subGroupStoreRel);

        // store 정보 조회
        Store A_Store = storeAdminService.selectStoreInfo(store);
        // group list
        List<Group> groupList = storeAdminService.selectGroupsList(store);

        // subGroup list
        List<SubGroupStoreRel> subGroupList = storeAdminService.selectSubgroupStoreRels(subGroupStoreRel);

        // map 으로 값 전달
        Map<String, Object> map = new HashMap<>();
        map.put("A_Store", A_Store);
        map.put("groupList", groupList);
        map.put("subGroupList", subGroupList);

        return map;
    }

    // subgroup list 불러오기
    @ResponseBody
    @GetMapping("/getSubGroupList")
    @CnttMethodDescription("서브 그룹 리스트 불러오기")
    public List<SubGroup> getSubGroupList(@RequestParam(value ="groupId", required=false) String groupId) {

        Store store = new Store();

        // ADMIN 정보
        SecurityUser adminInfo = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getDetails();
        // token 부여
        store.setToken(adminInfo.getAdminAccessToken());

        log.info("===============> adminInfo.getAdminAccessToken()    : {}", adminInfo.getAdminAccessToken());
        // param storeId
        store.setId(groupId);

        // subGroup list
        List<SubGroup> subGroupList = storeAdminService.selectSubGroupsList(store);

        // 리스트 확인
        /*if (subGroupList.size() == 0) {
            log.info("0000000000000000000000");
        } else {
            for (SubGroup s : subGroupList) {
                log.info("@@" + s.getName());
            }
        }*/
        return subGroupList;
    }

    // group list 불러오기
    @ResponseBody
    @GetMapping("/getGroupList")
    @CnttMethodDescription("그룹 리스트 불러오기")
    public List<Group> getGroupList(@RequestParam(required=false) String frag) {
        Store store = new Store();

        // ADMIN 정보
        SecurityUser adminInfo = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getDetails();
        // token 부여
        store.setToken(adminInfo.getAdminAccessToken());

        log.info("===============> adminInfo.getAdminAccessToken()    : {}", adminInfo.getAdminAccessToken());

        // Group list
        List<Group> groupList = storeAdminService.selectGroupsList(store);

        // 리스트 확인
        /*if (groupList.size() == 0) {
            log.info("0000000000000000000000");
        } else {
            for (Group s : groupList) {
                log.info("@@" + s.getName());
            }
        }*/
        return groupList;

    }


    @ResponseBody
    @PutMapping("/putStoreDetail")
    @CnttMethodDescription("매장 정보 수정")
    public String putStoreDetail(
            @RequestParam("storeId") String storeId,
            @RequestParam("code") String code,
            @RequestParam("storeName") String storeName,
            @RequestParam("storePhone") String storePhone,
            @RequestParam("assignmentStatus") String assignmentStatus,
            @RequestParam("groupId") String groupId,
            @RequestParam("subGroupId") String subGroupId,
            //@RequestParam("name") String name,
            //@RequestParam("phone") String phone,
            @RequestParam("address") String address,
            @RequestParam("detailAddress") String detailAddress,
            @RequestParam("hasGroup") String hasGroup
    ) {
        // store Model 생성
        Store store = new Store();

        // ADMIN 정보
        SecurityUser adminInfo = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getDetails();
        // token 부여
        store.setToken(adminInfo.getAdminAccessToken());

        log.info("===============> adminInfo.getAdminAccessToken()    : {}", adminInfo.getAdminAccessToken());

        // param storeId
        store.setId(storeId);
        store.setIsAdmin("1");
        store.setCode(code);
        store.setStoreName(storeName);
        store.setStorePhone(storePhone);
//        store.setName(name);
//        store.setPhone(phone);
        store.setAddress(address);
        store.setDetailAddress(detailAddress);
        System.out.println("!!!!"+groupId);
        // 위도 경도
        if (store.getAddress() != null && store.getAddress() != "") {
            log.info("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@"+store.getAddress());
            Geocoder geocoder = new Geocoder();
            try {
                Map<String, String> geo = geocoder.getLatLng(store.getAddress());
                store.setLatitude(geo.get("lat"));
                store.setLongitude(geo.get("lng"));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        log.info("@@@@@@@@@@@@@@@@@@@@@@@@" + store.getLatitude());
        if (store.getLatitude() == null || store.getLongitude() == null) {
            return "geo_err";
        }

        store.setAssignmentStatus(assignmentStatus);

        // group model 생성
        Group group = new Group();
        group.setId(groupId);

        store.setGroup(group);

        // subgroup model 생성
        SubGroupStoreRel subGroupRel = new SubGroupStoreRel();
        subGroupRel.setSubGroupId(subGroupId);
        subGroupRel.setGroupId(groupId);
        store.setSubGroupStoreRel(subGroupRel);

        // store 정보 조회
        int A_Store = storeAdminService.updateStoreInfo(store);
        int A_Assign_Status = 0;
        int A_Group = 0;
        if (hasGroup.equals("T")) {
            log.info("update group.................................");
               A_Group = storeAdminService.updateSubGroupStoreRel(store);
        } else {
            if(!groupId.equals("")){
                log.info("insert group..................................");
                A_Group = storeAdminService.insertSubGroupStoreRel(store);
            }
        }

        int B_Group = storeAdminService.putSubGroupRiderRelByStoreId(store);

        if (assignmentStatus != null && assignmentStatus != "") {
            A_Assign_Status = storeAdminService.updateStoreAssignmentStatus(store);
        }

        if (A_Store == 0 && A_Group == 0 && A_Assign_Status == 0 && B_Group ==0) {
            return "err";
        } else {
            return "ok";
        }
    }



    @ResponseBody
    @PostMapping("/postStore")
    @CnttMethodDescription("매장 등록")
    public String postStore( @RequestParam("loginId") String loginId,
                             @RequestParam("loginPw") String loginPw,
                             @RequestParam("code") String code,
                             @RequestParam("storeName") String storeName,
                             @RequestParam("storePhone") String storePhone,
                             @RequestParam("assignmentStatus") String assignmentStatus,
                             @RequestParam("groupId") String groupId,
                             @RequestParam("subGroupId") String subGroupId,
                             // @RequestParam("name") String name,
                             // @RequestParam("phone") String phone,
                             @RequestParam("address") String address,
                             @RequestParam("detailAddress") String detailAddress

    )
    {

    // store Model 생성
    Store store = new Store();
    // ADMIN 정보
    SecurityUser adminInfo = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getDetails();
    // token 부여
    store.setToken(adminInfo.getAdminAccessToken());

    log.info("===============> adminInfo.getAdminAccessToken()    : {}", adminInfo.getAdminAccessToken());


    Store storeSession = new Store();


    MD5Encoder md5 = new MD5Encoder();
    ShaEncoder sha = new ShaEncoder(512);

    // param storeId
    store.setLoginId(loginId);
    store.setLoginPw(sha.encode(loginPw));
    store.setCode(code);
    store.setStoreName(storeName);
    store.setStorePhone(storePhone);
//    store.setName(name);
//    store.setPhone(phone);
    store.setAddress(address);
    store.setDetailAddress(detailAddress);
    store.setAssignmentStatus(assignmentStatus);

    // group model 생성
    Group group = new Group();
    group.setId(groupId);

    store.setGroup(group);

    // subgroup model 생성
    SubGroupStoreRel subGroupRel = new SubGroupStoreRel();
    subGroupRel.setSubGroupId(subGroupId);
    subGroupRel.setGroupId(groupId);
    store.setSubGroupStoreRel(subGroupRel);


    // 위도 경도
    if ((store.getLatitude() == null || store.getLatitude() == "") || (store.getLongitude() == null || store.getLongitude() == "")) {
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

    // chatuser , room 등록
    store.setType("2");
    store.setIsAdmin("1");
    storeAdminService.insertChatUser(store);
    storeAdminService.insertChatRoom(store);
    int A_Store = storeAdminService.insertStore(store);
    log.info("@@@@@@@insertStore@@@@@@@@@@@"+store);


    int A_Group = 0;
    int A_Assign_Status = 0;

    String storeSessionToken = tokenManager.getToken("2",loginId , loginPw);
    storeSession.setAccessToken(storeSessionToken);
    storeSession.setId(store.getId());
    storeSession.setLoginId(loginId);



    storeAdminService.insertAdminStoreSession(storeSession);
    log.info("@@@@@@@insertStoreSession@@@@@@@@@@@"+storeSession);

    if(subGroupId !=""){
        A_Group = storeAdminService.insertSubGroupStoreRel(store);
        log.info("@@@@@@@insertSubGroupRel@@@@@@@@@@@"+store);
    }






    if (assignmentStatus != null) {
        log.info("@@@@@@@@@@@@@@@@@배정상태 insert@@@@@@@@@@@@@@@@@@@@");
        A_Assign_Status = storeAdminService.updateStoreAssignmentStatus(store);
    }
    if (A_Store == 0 && A_Group == 0 && A_Assign_Status == 0) {
        return "err";
    } else {
        return "ok";
    }
}

    @ResponseBody
    @PutMapping("/deleteStore")
    @CnttMethodDescription("상점 삭제")
    public boolean deleteStore( @RequestParam("storeId") String storeId
                                   ){
        // group model 생성
        Store store = new Store();

        // ADMIN 정보
        SecurityUser adminInfo = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getDetails();
        // token 부여
        store.setToken(adminInfo.getAdminAccessToken());

        log.info("===============> adminInfo.getAdminAccessToken()    : {}", adminInfo.getAdminAccessToken());
        store.setId(storeId);

        if(storeAdminService.deleteStore(store) == 0) {
            return false;
        }
        return true;

    }


    /**
     * 매장 아이디 중복 조회
     *
     * @return
     */
    @ResponseBody
    @GetMapping("/selectStoreLoginIdCheck")
    @CnttMethodDescription("상점 아이디 중복 조회")
    public int selectStoreLoginIdCheck(@RequestParam("loginId") String loginId) {

        Store store = new Store();

        // ADMIN 정보
        SecurityUser adminInfo = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getDetails();

        log.info("===============> adminInfo.getAdminAccessToken()    : {}", adminInfo.getAdminAccessToken());

        store.setToken(adminInfo.getAdminAccessToken());
        store.setLoginId(loginId);

        int S_Id = storeAdminService.selectStoreLoginIdCheck(store);

        log.info("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@"+S_Id);


        return S_Id;
    }

    /**
     * 매장 비밀번호 초기화
     *
     * @return
     */
    @ResponseBody
    @PutMapping("putStorePwReset")
    @CnttMethodDescription("매장 비밀번호 초기화")
    public String resetStorePw(Store store){
        SecurityUser adminInfo = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getDetails();
        store.setToken(adminInfo.getAdminAccessToken());

        ShaEncoder sha = new ShaEncoder(512);
        store.setLoginPw(sha.encode("1111"));

        int A_Store = storeAdminService.resetStorePassword(store);

        if (A_Store == 0) {
            return "err";
        } else {
            return "ok";
        }
    }


}
