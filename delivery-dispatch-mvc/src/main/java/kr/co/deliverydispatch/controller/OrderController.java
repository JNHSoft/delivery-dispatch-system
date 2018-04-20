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

    @Autowired
    public OrderController(StoreOrderService storeOrderService) { this.storeOrderService = storeOrderService; }

    /**
     * 주문현황 페이지
     *
     * @return
     */
    @GetMapping("/order")
    @CnttMethodDescription("오더 페이지")
    public String order(Store store, @RequestParam(required = false) String frag, Model model){
        SecurityUser storeInfo = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getDetails();
        Order order = new Order();
        store.setToken(storeInfo.getStoreAccessToken());
        order.setToken(storeInfo.getStoreAccessToken());
        Store myStore = storeOrderService.getStoreInfo(store);
        List<Order> orderList = storeOrderService.getOrders(order);
        model.addAttribute("store", myStore);
        model.addAttribute("orderList", orderList);
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

        Store store = new Store();
        store.setToken(storeInfo.getStoreAccessToken());
        store.setAccessToken(storeInfo.getStoreAccessToken());
        Store tmpStore = storeOrderService.getStoreInfo(store);

        List<Order> tmpOrders = storeOrderService.getOrders(order);

        int tmpAssignCount = 1;
        for (Order o : tmpOrders) {
            log.info(o.getStatus());
            log.info(o.getRiderId());
            log.info(order.getRiderId());
            if (o.getRiderId() != null && o.getRiderId().equals(order.getRiderId()) && (o.getStatus().equals("1") || o.getStatus().equals("2"))) {
                ++tmpAssignCount;
            }
        }

        if (tmpAssignCount > Integer.parseInt(tmpStore.getAssignmentLimit())) {
            return false;
        } else {
            storeOrderService.putOrderAssigned(order);
            return true;
        }
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
