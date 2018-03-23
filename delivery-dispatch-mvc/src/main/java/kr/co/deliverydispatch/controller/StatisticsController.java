package kr.co.deliverydispatch.controller;

import com.google.gson.Gson;
import kr.co.cntt.core.annotation.CnttMethodDescription;
import kr.co.cntt.core.model.common.Common;
import kr.co.cntt.core.model.order.Order;
import kr.co.cntt.core.model.store.Store;
import kr.co.deliverydispatch.security.SecurityUser;
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
    StoreStatementService storeStatementService;

    @Autowired
    public StatisticsController(StoreStatementService storeStatementService) { this.storeStatementService = storeStatementService;}

    /**
     * 통계 페이지
     *
     * @return
     */
    @GetMapping("/statistics")
    public String statistics(Store store, @RequestParam(required = false) String frag, Model model) {

        SecurityUser storeInfo = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getDetails();
        // Store 정보
        log.info("===============> storeInfo.getStoreAccessToken()    : {}", storeInfo.getStoreAccessToken());

        store.setToken(storeInfo.getStoreAccessToken());
        System.out.println("!!!!토큰"+store.getToken());
        Store myStore = storeStatementService.getStoreInfo(store);
        model.addAttribute("store", myStore);
        model.addAttribute("json", new Gson().toJson(store));

        log.info("json : {}", new Gson().toJson(store));

        return "/statistics/statement";
    }


    @ResponseBody
    @GetMapping("/getStoreStatistics")
    @CnttMethodDescription("통계 리스트 조회")
    public List<Order> getStoreStatistics(Order order){
        System.out.println(Arrays.toString(order.getStatusArray()));
        SecurityUser storeInfo = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getDetails();
        order.setToken(storeInfo.getStoreAccessToken());
        List<Order> statisticsList = storeStatementService.getStoreStatistics(order);
        return statisticsList;
    }

    @ResponseBody
    @GetMapping("/getStatisticsInfo")
    @CnttMethodDescription("통계 상세 조회")
    public Order getStatisticsInfoDetail(Order order){
        System.out.println("!!!!!!!!!!!!!!!!!!!common : "+order.getId());
        SecurityUser storeInfo = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getDetails();
        order.setToken(storeInfo.getStoreAccessToken());
        Order statisticsInfo = storeStatementService.getStoreStatisticsInfo(order);
        return statisticsInfo;
    }
}
