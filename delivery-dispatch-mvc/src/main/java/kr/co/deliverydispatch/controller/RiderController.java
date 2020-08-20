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
import kr.co.cntt.core.model.rider.RiderSession;
import kr.co.cntt.core.model.store.Store;
import kr.co.deliverydispatch.security.SecurityUser;
import kr.co.deliverydispatch.service.StoreNoticeService;
import kr.co.deliverydispatch.service.StoreOrderService;
import kr.co.deliverydispatch.service.StoreRiderService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
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
        if (riderInfo.getApprovalStatus().trim().equals("1") && !chkRiderInfo.getApprovalStatus().equals("0")){
            System.out.println("return false one #################");
            return false;
        }

        // 승인을 거부하는 경우
        if (riderInfo.getApprovalStatus().trim().equals("2") &&
                !((chkRiderInfo.getApprovalStatus().trim().equals("0") || chkRiderInfo.getApprovalStatus().trim().equals("1")))){
            System.out.println("return false two #################");
            return false;
        }

        // 승인이 된 상태에서 취소하는 경우
        if (riderInfo.getApprovalStatus().trim().equals("3") &&
                !(chkRiderInfo.getApprovalStatus().trim().equals("2"))){
            System.out.println("return false three #################");
            return false;
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
    public Boolean setRiderExpDate(RiderApprovalInfo riderInfo, String expiryDatetime, String datePattern){
        SecurityUser storeInfo = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getDetails();
        riderInfo.setToken(storeInfo.getStoreAccessToken());

        // 선택한 일자가 금일보다 작을 수 없도록 적용
        SimpleDateFormat format = new SimpleDateFormat(datePattern);
        try{
            Date expDate = format.parse(expiryDatetime);
            Date nowDate = format.parse(DateTime.now().toString());
            long diffDate = expDate.getTime() - nowDate.getTime();

            if (diffDate < 0){
                System.out.println();
            }

        }catch (Exception e){
            e.printStackTrace();
        }

        // 라이더의 상태값 체크를 위해 다시 한번 정보를 가져온다.
        RiderApprovalInfo chkRiderInfo = storeRiderService.getRiderApprovalInfo(riderInfo);

        // 만료날짜가 초과된 경우, 변경되지 않도록 작업
        if (chkRiderInfo.getSession() != null && chkRiderInfo.getSession().getExpiryDatetime() != null){
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            try {
                Date sdfStartDate = formatter.parse(chkRiderInfo.getSession().getExpiryDatetime());
                Date sdfEndDate = formatter.parse(DateTime.now().toString());
                long diff = sdfStartDate.getTime() - sdfEndDate.getTime();

                if (diff < 0){
                    System.out.println("diff = " + diff);
                    return false;
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        // 날짜에 대한 예외처리가 완료된 후 적용
        riderInfo.setSession(new RiderSession());
        riderInfo.getSession().setExpiryDatetime(expiryDatetime);

        if (chkRiderInfo.getApprovalStatus() == "0"){                       // 상태 값이 신규 요청이 경우, 임시 테이블에서 정보가 변경이 되어야함.

        }else if (chkRiderInfo.getApprovalStatus() == "1"){                 // 상태 값이 1인 경우, 라이더 정보에서 값이 변경 되어야 한다.

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

        System.out.println("#############################################");
        System.out.println(riderInfo.getId());
        System.out.println(riderInfo.getSession().getExpiryDatetime());
        System.out.println("#############################################");

        return true;
    }

}
