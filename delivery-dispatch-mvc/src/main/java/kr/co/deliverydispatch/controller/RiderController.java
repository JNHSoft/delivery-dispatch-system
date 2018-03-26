package kr.co.deliverydispatch.controller;

import com.google.gson.Gson;
import kr.co.cntt.core.annotation.CnttMethodDescription;
import kr.co.cntt.core.model.chat.Chat;
import kr.co.cntt.core.model.common.Common;
import kr.co.cntt.core.model.notice.Notice;
import kr.co.cntt.core.model.order.Order;
import kr.co.cntt.core.model.rider.Rider;
import kr.co.cntt.core.model.store.Store;
import kr.co.deliverydispatch.security.SecurityUser;
import kr.co.deliverydispatch.service.StoreNoticeService;
import kr.co.deliverydispatch.service.StoreOrderService;
import kr.co.deliverydispatch.service.StoreRiderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@Controller
public class RiderController {

    /**
     * 객체 주입
     */
    private StoreRiderService storeRiderService;
    private StoreNoticeService storeNoticeService;
    private StoreOrderService storeOrderService;


    @Autowired
    public RiderController(StoreRiderService storeRiderService, StoreNoticeService storeNoticeService, StoreOrderService storeOrderService) {
        this.storeRiderService = storeRiderService;
        this.storeNoticeService = storeNoticeService;
        this.storeOrderService = storeOrderService;
    }

    /**
     * 기사현황 페이지
     *
     * @return
     */
    @GetMapping("/rider")
    public String rider(Store store, @RequestParam(required = false) String frag, Model model) {
        SecurityUser storeInfo = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getDetails();
        // Store 정보
        log.info("===============> storeInfo.getStoreAccessToken()    : {}", storeInfo.getStoreAccessToken());

        store.setToken(storeInfo.getStoreAccessToken());
        System.out.println("!!!!토큰"+store.getToken());
        Store myStore = storeRiderService.getStoreInfo(store);
        Notice notice = new Notice();
        List<Notice> noticeList = storeNoticeService.getNoticeList(notice);
        model.addAttribute("noticeList", noticeList);
        model.addAttribute("store", myStore);
        Rider rider = new Rider();
        rider.setToken(storeInfo.getStoreAccessToken());
        List<Rider> footerRiderList = storeRiderService.getRiderFooter(rider);
        model.addAttribute("footerRiderList", footerRiderList);
        Order order = new Order();
        order.setToken(storeInfo.getStoreAccessToken());
        List<Order> footerOrderList = storeOrderService.getFooterOrders(order);
        model.addAttribute("footerOrderList", footerOrderList);
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
}
