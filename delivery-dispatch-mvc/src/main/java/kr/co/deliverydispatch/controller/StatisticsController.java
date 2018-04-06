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
import kr.co.deliverydispatch.service.StoreStatementService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Arrays;
import java.util.List;

@Slf4j
@Controller
public class StatisticsController {

    /**
     * 객체 주입
     */
    private StoreStatementService storeStatementService;
    private StoreNoticeService storeNoticeService;
    private StoreRiderService storeRiderService;
    private StoreOrderService storeOrderService;

    @Autowired
    public StatisticsController(StoreStatementService storeStatementService, StoreNoticeService storeNoticeService, StoreRiderService storeRiderService, StoreOrderService storeOrderService) {
        this.storeStatementService = storeStatementService;
        this.storeNoticeService = storeNoticeService;
        this.storeRiderService = storeRiderService;
        this.storeOrderService = storeOrderService;
    }

    /**
     * 통계 페이지
     *
     * @return
     */
    @GetMapping("/statistics")
    public String statistics(Store store, @RequestParam(required = false) String frag, Model model) {
        SecurityUser storeInfo = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getDetails();
        store.setToken(storeInfo.getStoreAccessToken());
        Store myStore = storeStatementService.getStoreInfo(store);
        model.addAttribute("store", myStore);
        return "/statistics/statement";
    }


    @ResponseBody
    @GetMapping("/getStoreStatistics")
    @CnttMethodDescription("통계 리스트 조회")
    public List<Order> getStoreStatistics(Order order){
        SecurityUser storeInfo = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getDetails();
        order.setToken(storeInfo.getStoreAccessToken());
        List<Order> statisticsList = storeStatementService.getStoreStatistics(order);
        return statisticsList;
    }

    @ResponseBody
    @GetMapping("/getStatisticsInfo")
    @CnttMethodDescription("통계 상세 조회")
    public Order getStatisticsInfoDetail(Order order){
        SecurityUser storeInfo = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getDetails();
        order.setToken(storeInfo.getStoreAccessToken());
        Order statisticsInfo = storeStatementService.getStoreStatisticsInfo(order);
        return statisticsInfo;
    }

}
