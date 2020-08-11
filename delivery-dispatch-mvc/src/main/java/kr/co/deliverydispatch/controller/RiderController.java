package kr.co.deliverydispatch.controller;

import com.google.gson.Gson;
import com.sun.org.apache.xpath.internal.operations.Bool;
import kr.co.cntt.core.annotation.CnttMethodDescription;
import kr.co.cntt.core.model.chat.Chat;
import kr.co.cntt.core.model.common.Common;
import kr.co.cntt.core.model.notice.Notice;
import kr.co.cntt.core.model.order.Order;
import kr.co.cntt.core.model.rider.Rider;
import kr.co.cntt.core.model.rider.RiderApprovalInfo;
import kr.co.cntt.core.model.store.Store;
import kr.co.deliverydispatch.security.SecurityUser;
import kr.co.deliverydispatch.service.StoreNoticeService;
import kr.co.deliverydispatch.service.StoreOrderService;
import kr.co.deliverydispatch.service.StoreRiderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.Locale;
import java.util.Map;

@Slf4j
@Controller
public class RiderController {

    /**
     * 객체 주입
     */
    private StoreRiderService storeRiderService;

    @Value("${spring.mvc.locale}")
    private Locale regionLocale;

    @Autowired
    public RiderController(StoreRiderService storeRiderService) { this.storeRiderService = storeRiderService; }

    /**
     * 기사현황 페이지
     *
     * @return
     */
    @GetMapping("/rider")
    public String rider(Store store, @RequestParam(required = false) String frag, Model model) {
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
        List<Rider> riderList = storeRiderService.getRiderNow(common);
        return riderList;
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
        List<Chat> chatList = storeRiderService.getChat(chat);
        return chatList;
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
    public String riderApprovalView(Store store, @RequestParam(required = false) String frag, Model model){

        SecurityUser storeInfo = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getDetails();
        store.setToken(storeInfo.getStoreAccessToken());
        Store myStore = storeRiderService.getStoreInfo(store);
        model.addAttribute("store", myStore);
        model.addAttribute("regionLocale", regionLocale);

        System.out.println("@!#@!$#%@#$^$#%$#!$@#$#!@$");

        return "/rider/rider_approval";
    }

    // 라이더 승인 요청에 대한 리스트 가져오기
    @GetMapping("/getApprovalRiderList")
    @ResponseBody
    @CnttMethodDescription("라이더 승인 리스트")
    public List<RiderApprovalInfo> getApprovalRiderList(Model model){
        SecurityUser storeInfo = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getDetails();
        //store.setToken(storeInfo.getStoreAccessToken());
        Store store = new Store();
        store.setToken(storeInfo.getStoreAccessToken());

        // 스토어 정보
        List<RiderApprovalInfo> approvalRider = storeRiderService.getRiderApprovalList(store);

        return approvalRider;
    }

    // 라이더 승인 상태 변경 요청
    @ResponseBody
    @PostMapping("/changeApprovalStatus")
    @CnttMethodDescription("라이더 승인 상태 변경")
    public Boolean changeApprovalStatus(RiderApprovalInfo riderInfo){
        SecurityUser storeInfo = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getDetails();
        //store.setToken(storeInfo.getStoreAccessToken());
        riderInfo.setToken(storeInfo.getStoreAccessToken());

        // 라이더의 상태값 체크를 위해 다시 한번 정보를 가져온다.
        RiderApprovalInfo chkRiderInfo = storeRiderService.getRiderApprovalInfo(riderInfo);

        // 값이 없는 경우
        if (chkRiderInfo == null){
            return false;
        }


        // 승인을 요청하는 경우
        if (!(riderInfo.getApprovalStatus().trim().equals("1") && chkRiderInfo.getApprovalStatus().equals("0"))){
            return false;
        }

        // 승인을 거부하는 경우
        if (!(riderInfo.getApprovalStatus().trim().equals("2") &&
                (chkRiderInfo.getApprovalStatus().trim().equals("0") || chkRiderInfo.getApprovalStatus().trim().equals("1")))){
            return false;
        }

        // 승인이 된 상태에서 취소하는 경우
        if (!(riderInfo.getApprovalStatus().trim().equals("3") &&
                (chkRiderInfo.getApprovalStatus().trim().equals("2")))){
            return false;
        }

        // 상태 변경 관련 UPDATE 문 실행

        System.out.println("############################# Change Approval Staus #############################");
        System.out.println(riderInfo.getId());
        System.out.println(chkRiderInfo.getId());
        System.out.println("############################# Change Approval Staus #############################");

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
    public Boolean setRiderExpDate(RiderApprovalInfo riderInfo){
        SecurityUser storeInfo = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getDetails();
        //store.setToken(storeInfo.getStoreAccessToken());
        riderInfo.setToken(storeInfo.getStoreAccessToken());

        System.out.println("################### 라이더 유효기간 설정 ###################");
        System.out.println(riderInfo.getId());
        System.out.println(riderInfo.getSession().getExpiryDatetime());
        System.out.println("################### 라이더 유효기간 설정 ###################");

        return true;
    }

    // 라이더 상세 정보 변경
    @ResponseBody
    @PostMapping("/changeRiderInfo")
    @CnttMethodDescription("라이더 상세 정보 변경")
    public Boolean changeRiderInfo(RiderApprovalInfo riderInfo){
        SecurityUser storeInfo = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getDetails();
        //store.setToken(storeInfo.getStoreAccessToken());
        riderInfo.setToken(storeInfo.getStoreAccessToken());

        System.out.println("################### 라이더 상세 정보 변경 ###################");
        System.out.println(riderInfo);
        System.out.println("################### 라이더 상세 정보 변경 ###################");

        return true;
    }
}
