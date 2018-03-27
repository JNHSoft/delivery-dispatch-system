package kr.co.deliverydispatch.controller;

import com.google.gson.Gson;
import kr.co.cntt.core.annotation.CnttMethodDescription;
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

import java.util.Arrays;
import java.util.List;

@Slf4j
@Controller
public class OrderController {

    /**
     * 객체 주입
     */
    private StoreOrderService storeOrderService;
    private StoreNoticeService storeNoticeService;
    private StoreRiderService storeRiderService;
    @Autowired
    public OrderController(StoreOrderService storeOrderService, StoreNoticeService storeNoticeService, StoreRiderService storeRiderService) {
        this.storeOrderService = storeOrderService;
        this.storeNoticeService = storeNoticeService;
        this.storeRiderService = storeRiderService;
    }

    /**
     * 주문현황 페이지
     *
     * @return
     */
    @GetMapping("/order")
    @CnttMethodDescription("오더 페이지")
    public String order(Store store, @RequestParam(required = false) String frag, Model model){
        SecurityUser storeInfo = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getDetails();
        // Store 정보
        log.info("===============> storeInfo.getStoreAccessToken()    : {}", storeInfo.getStoreAccessToken());

        Notice notice = new Notice();
        Rider rider = new Rider();
        Order order = new Order();
        store.setToken(storeInfo.getStoreAccessToken());
        notice.setToken(storeInfo.getStoreAccessToken());
        rider.setToken(storeInfo.getStoreAccessToken());
        order.setToken(storeInfo.getStoreAccessToken());
        List<Notice> noticeList = storeNoticeService.getNoticeList(notice);
        Store myStore = storeOrderService.getStoreInfo(store);
        List<Rider> footerRiderList = storeRiderService.getRiderFooter(rider);
        List<Order> orderList = storeOrderService.getOrders(order);
        List<Order> footerOrderList = storeOrderService.getFooterOrders(order);

        model.addAttribute("store", myStore);
        model.addAttribute("noticeList", noticeList);
        model.addAttribute("footerRiderList", footerRiderList);
        model.addAttribute("orderList", orderList);
        model.addAttribute("footerOrderList", footerOrderList);


        return "/order/order";
    }

    @ResponseBody
    @GetMapping("/getOrderList")
    @CnttMethodDescription("오더 리스트 조회")
    public List<Order> getOrderList(Order order){
        System.out.println(Arrays.toString(order.getStatusArray()));
        SecurityUser storeInfo = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getDetails();
        order.setToken(storeInfo.getStoreAccessToken());
        List<Order> orderList = storeOrderService.getOrders(order);
        return orderList;
    }

    @ResponseBody
    @GetMapping("/getOrderDetail")
    @CnttMethodDescription("오더 상세 조회")
    public Order getOrderDetail(Common common){
        System.out.println("!!!!!!!!!!!!!!!!!!!common : "+common.getId());
        SecurityUser storeInfo = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getDetails();
        common.setToken(storeInfo.getStoreAccessToken());
        Order order = storeOrderService.getOrderInfo(common);
        return order;
    }

    @ResponseBody
    @PutMapping("/putAssignedAdvanceFirst")
    @CnttMethodDescription("우선배정")
    public boolean putAssignedAdvanceFirst(Order order){
        SecurityUser storeInfo = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getDetails();
        order.setToken(storeInfo.getStoreAccessToken());
        storeOrderService.putOrderAssignedFirst(order);
        return true;
    }

    @ResponseBody
    @PutMapping("/putAssignedAdvance")
    @CnttMethodDescription("강제배정")
    public boolean putAssignedAdvance(Order order){
        SecurityUser storeInfo = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getDetails();
        order.setToken(storeInfo.getStoreAccessToken());
        storeOrderService.putOrderAssigned(order);
        return true;
    }

    @ResponseBody
    @PutMapping("/putOrder")
    @CnttMethodDescription("주문수정")
    public boolean putOrder(Order order){
        SecurityUser storeInfo = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getDetails();
        order.setToken(storeInfo.getStoreAccessToken());
        storeOrderService.putOrderInfo(order);
        return true;
    }

    @ResponseBody
    @PutMapping("/putOrderCancle")
    @CnttMethodDescription("주문취소")
    public boolean putOrderCancle(Order order){
        SecurityUser storeInfo = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getDetails();
        order.setToken(storeInfo.getStoreAccessToken());
        storeOrderService.putOrderCanceled(order);
        return true;
    }

    @ResponseBody
    @PutMapping("/putOrderAssignCancle")
    @CnttMethodDescription("주문 배정 취소")
    public boolean putOrderAssignCancle(Order order){
        SecurityUser storeInfo = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getDetails();
        order.setToken(storeInfo.getStoreAccessToken());
        storeOrderService.putOrderAssignCanceled(order);
        return true;
    }

    @ResponseBody
    @GetMapping("/getMyRiderList")
    @CnttMethodDescription("그룹소속 기사목록")
    public List<Rider> getMyRiderList(Common common){
        SecurityUser storeInfo = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getDetails();
        common.setToken(storeInfo.getStoreAccessToken());
        List<Rider> riderList = storeOrderService.getSubgroupRiderRels(common);
        return riderList;
    }
}
