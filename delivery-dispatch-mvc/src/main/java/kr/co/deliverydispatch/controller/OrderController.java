package kr.co.deliverydispatch.controller;

import com.google.gson.Gson;
import kr.co.cntt.core.annotation.CnttMethodDescription;
import kr.co.cntt.core.exception.AppTrException;
import kr.co.cntt.core.model.order.Order;
import kr.co.cntt.core.model.store.Store;
import kr.co.deliverydispatch.security.SecurityUser;
import kr.co.deliverydispatch.service.StoreOrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Slf4j
@Controller
public class OrderController {

    /**
     * 객체 주입
     */
    StoreOrderService storeOrderService;

    @Autowired
    public OrderController(StoreOrderService storeOrderService) { this.storeOrderService = storeOrderService;}

    /**
     * 주문현황 페이지
     *
     * @return
     */
    @GetMapping("/order")
    @CnttMethodDescription("오더 리스트 조회")
    public String order(Order order, @RequestParam(required = false) String frag, Model model){
        // Store 정보
        SecurityUser storeInfo = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getDetails();
        log.info("===============> storeInfo.getStoreAccessToken()    : {}", storeInfo.getStoreAccessToken());

        order.setToken(storeInfo.getStoreAccessToken());

        List<Order> orderList = storeOrderService.getOrders(order);

        model.addAttribute("orderList", orderList);
        model.addAttribute("jsonList", new Gson().toJson(orderList));

        log.info("json : {}", new Gson().toJson(orderList));

        return "/order/order";
    }

}
