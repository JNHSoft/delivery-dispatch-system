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
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Slf4j
@Controller
public class StatisticsController {

    /**
     * 객체 주입
     */
    private StoreStatementService storeStatementService;

    @Autowired
    public StatisticsController(StoreStatementService storeStatementService) { this.storeStatementService = storeStatementService; }

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
    public List<Order> getStoreStatistics(@RequestParam(value = "startDate") String startDate
            ,@RequestParam(value = "endDate") String endDate){
        SecurityUser storeInfo = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getDetails();
        Order order = new Order();
        order.setCurrentDatetime(startDate);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date sdfStartDate = formatter.parse(startDate);
            Date sdfEndDate = formatter.parse(endDate);
            long diff = sdfEndDate.getTime() - sdfStartDate.getTime();
            long diffDays = diff / (24 * 60 * 60 * 1000);

            order.setDays(Integer.toString((int) (long) diffDays + 1));
        } catch (ParseException e) {
            e.printStackTrace();
        }
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

    // excel 다운로드
//    @ResponseBody
    @GetMapping("/excelDownload")
    public ModelAndView statisticsExcelDownload(HttpServletResponse response, @RequestParam(value = "startDate") String startDate, @RequestParam(value = "endDate") String endDate) {
        response.setHeader("Set-Cookie", "fileDownload=true; path=/");
        SecurityUser storeInfo = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getDetails();
        Order order = new Order();
        order.setToken(storeInfo.getStoreAccessToken());
        order.setCurrentDatetime(startDate);

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date sdfStartDate = formatter.parse(startDate);
            Date sdfEndDate = formatter.parse(endDate);
            long diff = sdfEndDate.getTime() - sdfStartDate.getTime();
            long diffDays = diff / (24 * 60 * 60 * 1000);

            order.setDays(Integer.toString((int) (long) diffDays + 1));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        ModelAndView modelAndView = new ModelAndView("StatisticsStoreExcelBuilderServiceImpl");
        List<Order> orderStatisticsByStoreList = storeStatementService.getStoreStatisticsExcel(order);
        modelAndView.addObject("getStoreStatisticsExcel", orderStatisticsByStoreList);

        return modelAndView;
    }

}
