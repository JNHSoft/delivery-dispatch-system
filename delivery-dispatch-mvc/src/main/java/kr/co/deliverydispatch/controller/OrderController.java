package kr.co.deliverydispatch.controller;

import kr.co.cntt.core.annotation.CnttMethodDescription;
import kr.co.cntt.core.model.common.Common;
import kr.co.cntt.core.model.order.Order;
import kr.co.cntt.core.model.rider.Rider;
import kr.co.cntt.core.model.store.Store;
import kr.co.deliverydispatch.security.SecurityUser;
import kr.co.deliverydispatch.service.StoreOrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Locale;

@Slf4j
@Controller
public class OrderController {

    /**
     * 객체 주입
     */
    private StoreOrderService storeOrderService;

    @Value("${spring.mvc.locale}")
    private Locale regionLocale;

    @Value("${api.cors.origin}")
    private String origin;

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
        //List<Order> orderList = storeOrderService.getOrders(order);
        model.addAttribute("store", myStore);
        //model.addAttribute("orderList", orderList);
        model.addAttribute("regionLocale", regionLocale);
        return "/order/order";
    }

    @ResponseBody
    @GetMapping("/getOrderList")
    @CnttMethodDescription("오더 리스트 조회")
    public List<Order> getOrderList(Order order){
        SecurityUser storeInfo = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getDetails();
        order.setToken(storeInfo.getStoreAccessToken());

        // Store ID를 입력한다.
        order.setStoreId(String.valueOf(storeInfo.getStoreSeq()));

        List<Order> orderList = storeOrderService.getOrders(order);
        return orderList;
    }

    @ResponseBody
    @GetMapping("/getOrderDetail")
    @CnttMethodDescription("오더 상세 조회")
    public Order getOrderDetail(Common common){
        SecurityUser storeInfo = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getDetails();
        common.setToken(storeInfo.getStoreAccessToken());

        // 21-09-11 스토어의 ID를 넣는다.
//        common.setId(String.valueOf(storeInfo.getStoreSeq()));


        Order order = storeOrderService.getOrderInfo(common);
        return order;
    }

    @ResponseBody
    @PutMapping("/putOrderThirdParty")
    @CnttMethodDescription("서드파티 배정 및 주문완료")
    public boolean putOrderThirdParty(@RequestBody Order order){
        SecurityUser storeInfo = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getDetails();
        order.setToken(storeInfo.getStoreAccessToken());
        // 서드파티 배정 된 상태에서도 배정할수있도록 변경 Nick
        if(order.getStatus().equals("0") || order.getStatus().equals("1") || order.getStatus().equals("5")){
            storeOrderService.putOrderThirdParty(order);
            return true;
        } else {
            return false;
        }
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


        System.out.println(storeInfo.getStoreSeq() + " ### brand Code = " + storeInfo.getStoreBrandCode() + " #### store id => " + order.getStoreId());

        // 2021-11-17 KFC 매장 쉐어인 경우에도 라이더 및 정보 변경이 가능하도록 적용
        order.setStoreId(String.valueOf(storeInfo.getStoreSeq()));
        order.setStore(new Store());
        order.getStore().setBrandCode(storeInfo.getStoreBrandCode());

        int oderAdmitCount = storeOrderService.getCountOderAdmit(order);
        if(oderAdmitCount>0){
            storeOrderService.putOrderAssigned(order);
            return true;
        }else{
            return false;
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
    @PutMapping("/putOrderCancel")
    @CnttMethodDescription("주문취소")
    public boolean putOrderCancel(Order order){
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

        System.out.println(storeInfo.getStoreSeq() + " ### brand Code = " + storeInfo.getStoreBrandCode() + " #### store id => " + order.getStoreId());

        // 2021-11-17 KFC 매장 쉐어인 경우에도 라이더 및 정보 변경이 가능하도록 적용
        order.setStoreId(String.valueOf(storeInfo.getStoreSeq()));
        order.setStore(new Store());
        order.getStore().setBrandCode(storeInfo.getStoreBrandCode());


        storeOrderService.putOrderAssignCanceled(order);
        return true;
    }

    @ResponseBody
    @GetMapping("/getMyRiderList")
    @CnttMethodDescription("그룹소속 기사목록")
    public List<Rider> getMyRiderList(Common common){
        SecurityUser storeInfo = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getDetails();
        common.setToken(storeInfo.getStoreAccessToken());

        // 21-07-28 스토어의 ID를 넣는다.
        common.setId(String.valueOf(storeInfo.getStoreSeq()));

        List<Rider> riderList = storeOrderService.getSubgroupRiderRels(common);
        return riderList;
    }
}
