package kr.co.cntt.deliverydispatchadmin.controller;

import com.google.gson.Gson;
import kr.co.cntt.core.annotation.CnttMethodDescription;
import kr.co.cntt.core.model.admin.Admin;
import kr.co.cntt.core.model.group.Group;
import kr.co.cntt.core.model.group.SubGroup;
import kr.co.cntt.core.model.group.SubGroupStoreRel;
import kr.co.cntt.core.model.store.Store;
import kr.co.cntt.core.service.admin.GroupAdminService;
import kr.co.cntt.deliverydispatchadmin.security.SecurityUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@Controller
public class GroupController {



    /**
     * 객체 주입
     */
    GroupAdminService groupAdminService;


    @Autowired
    public GroupController(GroupAdminService groupAdminService){
        this.groupAdminService = groupAdminService;
    }



    /**
     * 그룹관리 페이지
     *
     * @return
     */
    @GetMapping("/group")
    @CnttMethodDescription("그룹 리스트 조회")
    public String group(Store store, @RequestParam(required=false) String frag, Model model) {
        // ADMIN 정보
        SecurityUser adminInfo = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getDetails();
        log.info("===============> adminInfo.getAdminAccessToken()    : {}", adminInfo.getAdminAccessToken());

        SubGroupStoreRel subGroupStoreRel = new SubGroupStoreRel();
        Admin admin = new Admin();

        store.setToken(adminInfo.getAdminAccessToken());

        // 그룹 리스트
        List<Group> groupList = groupAdminService.selectGroupsList(store);

        subGroupStoreRel.setToken(adminInfo.getAdminAccessToken());

        // storeRel list
        List<SubGroupStoreRel> noneGroupList = groupAdminService.selectNoneSubgroupStoreRels(subGroupStoreRel);

        model.addAttribute("groupList", groupList);
        model.addAttribute("noneGroupList", noneGroupList);
        model.addAttribute("jsonList", new Gson().toJson(groupList));

        // 리스트 확인
        /*if (noneGroupList.size() == 0) {
            log.info("0000000000000000000000");
        } else {
            for (SubGroupStoreRel s : noneGroupList) {
                log.info("@@" + s.getStoreName());
                log.info("@@" + s.getId());

            }
        }*/
        log.info("json : {}", new Gson().toJson(groupList));

        return "/group/group";
    }


    // subgroup list 불러오기
    @ResponseBody
    @GetMapping("/getNoneStoreSubGroupList")
    @CnttMethodDescription("서브 그룹 리스트 불러오기")
    public List<SubGroup> getNoneStoreSubGroupList(@RequestParam(value ="groupId", required=false) String groupId) {


        Admin admin = new Admin();
        // ADMIN 정보
        SecurityUser adminInfo = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getDetails();
        // token 부여
        admin.setToken(adminInfo.getAdminAccessToken());

        log.info("===============> adminInfo.getAdminAccessToken()    : {}", adminInfo.getAdminAccessToken());
        // param storeId
        admin.setId(groupId);

        // subGroup list
        List<SubGroup> subGroupList = groupAdminService.selectSubGroupsList(admin);

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

    @ResponseBody
    @PutMapping("/putGroupName")
    @CnttMethodDescription("그룹 이름 수정")
    public boolean putGroupName(
            @RequestParam("groupId") String groupId,
            @RequestParam("groupName") String groupName

    ) {
//        Admin admin = new Admin();

        // ADMIN 정보
        SecurityUser adminInfo = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getDetails();
        // token 부여

        // group model 생성
        Group group = new Group();

        group.setToken(adminInfo.getAdminAccessToken());
        log.info("===============> adminInfo.getAdminAccessToken()    : {}", adminInfo.getAdminAccessToken());

        group.setId(groupId);
        group.setName(groupName);
//        group.setAdminId(adminInfo.getAdminAccessToken());
        log.info("=========@@@@@@@@@@@@@@@@@@======>"+adminInfo.getAdminAccessToken());
        log.info("=========@@@@@@@@@@@@@@@@@@======>");

        if(groupAdminService.updateGroup(group) == 0) {
            return false;
        }
        return true;
    }


    @ResponseBody
    @PutMapping("/putSubGroupName")
    @CnttMethodDescription("서브 그룹 이름 수정")
    public boolean putSubGroupName(
            @RequestParam("groupId") String groupId,
            @RequestParam("groupName") String groupName,
            @RequestParam("subGroupId") String subGroupId

    ) {
//        Admin admin = new Admin();

        // ADMIN 정보
        SecurityUser adminInfo = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getDetails();
        // token 부여

        // group model 생성
        SubGroup subGroup = new SubGroup();

        subGroup.setToken(adminInfo.getAdminAccessToken());
        log.info("===============> adminInfo.getAdminAccessToken()    : {}", adminInfo.getAdminAccessToken());

        subGroup.setGroupId(groupId);
        subGroup.setName(groupName);
        subGroup.setId(subGroupId);

//        group.setAdminId(adminInfo.getAdminAccessToken());
        log.info("=========@@@@@@@@@@@@@@@@@@======>"+adminInfo.getAdminAccessToken());
        log.info("=========@@@@@@@@@@@@@@@@@@======>");

        if(groupAdminService.updateSubGroup(subGroup) == 0) {
            return false;
        }
        return true;
    }

    @ResponseBody
    @PostMapping("/postGroup")
    @CnttMethodDescription("그룹 등록")
    public boolean postGroup(
                             @RequestParam("groupName") String groupName
                           )
    {
        // group model 생성
        Group group = new Group();

        // ADMIN 정보
        SecurityUser adminInfo = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getDetails();
        // token 부여
        group.setToken(adminInfo.getAdminAccessToken());

        log.info("===============> adminInfo.getAdminAccessToken()    : {}", adminInfo.getAdminAccessToken());

        // param
        group.setName(groupName);


        if(groupAdminService.insertGroup(group) == 0) {
            return false;
        }
        return true;
    }


    @ResponseBody
    @PutMapping("/deleteGroup")
    @CnttMethodDescription("그룹 삭제")
    public boolean deleteGroup( @RequestParam("groupId") String groupId){
        // group model 생성
        Group group = new Group();

        // ADMIN 정보
        SecurityUser adminInfo = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getDetails();
        // token 부여
        group.setToken(adminInfo.getAdminAccessToken());

        log.info("===============> adminInfo.getAdminAccessToken()    : {}", adminInfo.getAdminAccessToken());
        group.setId(groupId);


        if(groupAdminService.deleteGroup(group) == 0) {
            return false;
        }
        return true;

    }

    @ResponseBody
    @PutMapping("/deleteSubGroup")
    @CnttMethodDescription("서브 그룹 삭제")
    public boolean deleteSubGroup( @RequestParam("groupId") String groupId,
                                   @RequestParam(value ="subGroupId") String subGroupId){
        // group model 생성
        SubGroup subGroup = new SubGroup();

        // ADMIN 정보
        SecurityUser adminInfo = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getDetails();
        // token 부여
        subGroup.setToken(adminInfo.getAdminAccessToken());

        log.info("===============> adminInfo.getAdminAccessToken()    : {}", adminInfo.getAdminAccessToken());
        subGroup.setId(subGroupId);
        subGroup.setGroupId(groupId);


        if(groupAdminService.deleteSubGroup(subGroup) == 0) {
            return false;
        }
        return true;

    }


    // subgroup list 불러오기
    @GetMapping("/subGroup")
    @CnttMethodDescription("서브 그룹 리스트 불러오기")
    public String getSubGroupList(@RequestParam(value ="groupId", required=false) String groupId,
                @RequestParam(value ="frag") String frag, Model model) {

        Admin admin = new Admin();

        // ADMIN 정보
        SecurityUser adminInfo = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getDetails();
        // token 부여
        admin.setToken(adminInfo.getAdminAccessToken());

        log.info("===============> adminInfo.getAdminAccessToken()    : {}", adminInfo.getAdminAccessToken());
        // param storeId
        admin.setId(groupId);

        // subGroup list
        List<SubGroup> subGroupList = groupAdminService.selectSubGroupsList(admin);

        model.addAttribute("subGroupList",subGroupList);

        // 리스트 확인
        /*if (subGroupList.size() == 0) {
            log.info("0000000000000000000000");
        } else {
            for (SubGroup s : subGroupList) {
                log.info("@@" + s.getName());
            }
        }*/
        return "/group/group ::" + frag;
    }


    // store list 불러오기
    @GetMapping("/storeList")
    @CnttMethodDescription("상점 리스트 불러오기")
    public String getStoreList(
            @RequestParam(value ="groupId", required=false) String groupId,
            @RequestParam(value ="subGroupId") String subGroupId,
            Model model,
            @RequestParam(value ="frag") String frag) {

        SubGroupStoreRel subGroupStoreRel = new SubGroupStoreRel();
        Admin admin = new Admin();
        // ADMIN 정보
        SecurityUser adminInfo = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getDetails();
        // token 부여
        subGroupStoreRel.setToken(adminInfo.getAdminAccessToken());

        log.info("===============> adminInfo.getAdminAccessToken()    : {}", adminInfo.getAdminAccessToken());
        // param storeId
        subGroupStoreRel.setGroupId(groupId);

        subGroupStoreRel.setSubGroupId(subGroupId);
        admin.setId(groupId);
        admin.setToken(adminInfo.getAdminAccessToken());

        // store list
        List<SubGroupStoreRel> storeList = groupAdminService.selectSubgroupStoreRels(subGroupStoreRel);
        // subGroup list
        List<SubGroup> subGroupList = groupAdminService.selectSubGroupsList(admin);

        model.addAttribute("subGroupList",subGroupList);
        model.addAttribute("storeList",storeList);

        // 리스트 확인
        /*if (storeList.size() == 0) {
            log.info("0000000000000000000000");
        } else {
            for (SubGroup s : subGroupList) {
                log.info("@stosetstostoser@" + s.getName());
            }
        }*/
        return "/group/group ::" + frag;
    }



    @ResponseBody
    @PutMapping("/putStoreSubGroup")
    @CnttMethodDescription("상점 서브 그룹만 수정")
    public boolean putStoreSubGroup(
            @RequestParam("groupId") String groupId,
            @RequestParam("storeId") String storeId,
            @RequestParam("subGroupId") String subGroupId

    ) {
//        Admin admin = new Admin();

        // ADMIN 정보
        SecurityUser adminInfo = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getDetails();
        // token 부여

        // group model 생성


        Store store = new Store();
        store.setToken(adminInfo.getAdminAccessToken());
        log.info("===============> adminInfo.getAdminAccessToken()    : {}", adminInfo.getAdminAccessToken());
        store.setId(storeId);


        Group group = new Group();
        group.setId(groupId);
        store.setGroup(group);


        SubGroupStoreRel subGroupStoreRel = new SubGroupStoreRel();
        subGroupStoreRel.setSubGroupId(subGroupId);
        subGroupStoreRel.setGroupId(groupId);
        store.setSubGroupStoreRel(subGroupStoreRel);


        log.info("=========@@@@@@@@@@@@@@@@@@======>"+adminInfo.getAdminAccessToken());
        int updateStoreSub = groupAdminService.updateStoreSubGroup(store);
        groupAdminService.updateRiderSubGroup(store);
        if(updateStoreSub == 0) {
            return false;
        }
        return true;
    }


    @ResponseBody
    @PostMapping("/postSubGroup")
    @CnttMethodDescription("서브그룹 등록")
    public boolean postSubGroup(
            @RequestParam("subGroupName") String subGroupName,
            @RequestParam("groupId") String groupId

    )
    {
        // group model 생성
        SubGroup subGroup = new SubGroup();

        // ADMIN 정보
        SecurityUser adminInfo = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getDetails();
        // token 부여
        subGroup.setToken(adminInfo.getAdminAccessToken());

        log.info("===============> adminInfo.getAdminAccessToken()    : {}", adminInfo.getAdminAccessToken());

        // param
        subGroup.setName(subGroupName);
        subGroup.setGroupId(groupId);


        if(groupAdminService.insertSubGroup(subGroup) == 0) {
            return false;
        }
        return true;
    }

    @ResponseBody
    @PutMapping("/deleteStoreGroup")
    @CnttMethodDescription("상점 그룹 소그룹 삭제")
    public boolean deleteSubGroup( @RequestParam("groupId") String groupId,
                                   @RequestParam(value ="subGroupId") String subGroupId,
                                   @RequestParam(value ="storeId") String storeId){

        SubGroupStoreRel subGroupStoreRel = new SubGroupStoreRel();


        // ADMIN 정보
        SecurityUser adminInfo = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getDetails();
        // token 부여
        subGroupStoreRel.setToken(adminInfo.getAdminAccessToken());

        log.info("===============> adminInfo.getAdminAccessToken()    : {}", adminInfo.getAdminAccessToken());

        subGroupStoreRel.setSubGroupId(subGroupId);
        subGroupStoreRel.setGroupId(groupId);
        subGroupStoreRel.setStoreId(storeId);


        if(groupAdminService.deleteSubGroupStoreRel(subGroupStoreRel) == 0) {
            return false;
        }
        return true;

    }



    @ResponseBody
    @PostMapping("/postStoreGroupSubGroup")
    @CnttMethodDescription("상점 그룹 서브그룹 등록")
    public boolean postStore(
                             @RequestParam("groupId") String groupId,
                             @RequestParam("subGroupId") String subGroupId,
                             @RequestParam("storeId") String storeId
                             )
    {
        // store Model 생성
        Store store = new Store();
        // ADMIN 정보
        SecurityUser adminInfo = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getDetails();
        // token 부여
        store.setToken(adminInfo.getAdminAccessToken());

        log.info("===============> adminInfo.getAdminAccessToken()    : {}", adminInfo.getAdminAccessToken());

        // group model 생성
        Group group = new Group();
        group.setId(groupId);

        store.setGroup(group);
        store.setId(storeId);

        // subgroup model 생성
        SubGroupStoreRel subGroupRel = new SubGroupStoreRel();
        subGroupRel.setSubGroupId(subGroupId);
        subGroupRel.setGroupId(groupId);
        store.setSubGroupStoreRel(subGroupRel);


        if(groupAdminService.insertSubGroupStoreRel(store) == 0) {
            return false;
        }
        return true;
    }
}
