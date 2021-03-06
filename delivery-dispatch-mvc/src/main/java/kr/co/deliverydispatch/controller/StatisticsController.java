package kr.co.deliverydispatch.controller;

import kr.co.cntt.core.annotation.CnttMethodDescription;
import kr.co.cntt.core.model.order.Order;
import kr.co.cntt.core.model.statistic.ByDate;
import kr.co.cntt.core.model.statistic.Interval;
import kr.co.cntt.core.model.store.Store;
import kr.co.deliverydispatch.security.SecurityUser;
import kr.co.deliverydispatch.service.StoreStatementService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletResponse;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Controller
public class StatisticsController {

    /**
     * 객체 주입
     */
    private StoreStatementService storeStatementService;

    @Autowired
    public StatisticsController(StoreStatementService storeStatementService) { this.storeStatementService = storeStatementService; }

    @Value("${spring.mvc.locale}")
    private Locale regionLocale;

    /**
     * 통계 페이지
     *
     * @return
     */
    @GetMapping("/statisticsByOrder")
    public String statisticsByOrder(Store store, @RequestParam(required = false) String frag, Model model) {
        String viewPath = "/statistics";

        SecurityUser storeInfo = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getDetails();
        store.setToken(storeInfo.getStoreAccessToken());
        Store myStore = storeStatementService.getStoreInfo(store);
        model.addAttribute("store", myStore);
        model.addAttribute("regionLocale", regionLocale);

        /**
         * 2020.06.04 대만 요청으로 인하여 통계 View 화면은 KFC 및 PIZZAHUT이 다르게 보여져야함
         * */
        if (myStore.getBrandCode().trim().equals("1"))       /// KFC
        {
            viewPath = viewPath.concat("/orderStatement_tw_kfc");
        }else{
            viewPath = viewPath.concat("/orderStatement");
        }

        return viewPath;
    }

    @GetMapping("/statisticsByDate")
    public String statisticsByDate(Store store, @RequestParam(required = false) String frag, Model model) {
        String viewPath = "/statistics";
        SecurityUser storeInfo = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getDetails();
        store.setToken(storeInfo.getStoreAccessToken());
        Store myStore = storeStatementService.getStoreInfo(store);
        model.addAttribute("store", myStore);
        model.addAttribute("regionLocale", regionLocale);

        /**
         * 2020.06.04 대만 요청으로 인하여 통계 View 화면은 KFC 및 PIZZAHUT이 다르게 보여져야함
         * */
        if (myStore.getBrandCode().trim().equals("1"))       /// KFC
        {
            viewPath = viewPath.concat("/dateStatement_tw_kfc");
        }else{
            viewPath = viewPath.concat("/dateStatement");
        }

        return viewPath;
    }

    @GetMapping("/statisticsByInterval")
    public String statisticsByInterval(Store store, @RequestParam(required = false) String frag, Model model) {
        String viewPath = "/statistics";
        SecurityUser storeInfo = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getDetails();
        store.setToken(storeInfo.getStoreAccessToken());
        Store myStore = storeStatementService.getStoreInfo(store);
        model.addAttribute("store", myStore);
        model.addAttribute("regionLocale", regionLocale);

        /**
         * 2020.06.04 대만 요청으로 인하여 통계 View 화면은 KFC 및 PIZZAHUT이 다르게 보여져야함
         * */
        if (myStore.getBrandCode().trim().equals("1"))       /// KFC
        {
            viewPath = viewPath.concat("/interval_tw_kfc");
        }else{
            viewPath = viewPath.concat("/interval");
        }

        return viewPath;
    }

    /**
     * 2020-06-16
     * */
    @GetMapping("/statisticsByOrderDetail")
    public String statisticsByOrderDetail(Store store, @RequestParam(required = false) String frag, Model model){
        String viewPath = "/statistics";
        SecurityUser storeInfo = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getDetails();
        store.setToken(storeInfo.getStoreAccessToken());
        Store myStore = storeStatementService.getStoreInfo(store);
        model.addAttribute("store", myStore);
        model.addAttribute("regionLocale", regionLocale);

        if (myStore.getBrandCode().trim().equals("1"))       /// KFC
        {
            viewPath = viewPath.concat("/orderdetail_tw_kfc");
        }else{
            viewPath = "redirect:/statisticsByOrder";
        }

        return viewPath;
    }

    /*@ResponseBody
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
    }*/

    @ResponseBody
    @GetMapping("/getStoreStatisticsByOrder")
    @CnttMethodDescription("주문별 통계 리스트 조회")
    public List<Order> getStoreStatisticsByOrder(@RequestParam(value = "startDate") String startDate
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

            // 31일까지만 가능
            if (diffDays > 31){
                return new ArrayList<>();
            }

            order.setDays(Integer.toString((int) (long) diffDays + 1));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        order.setToken(storeInfo.getStoreAccessToken());
        List<Order> statisticsList = storeStatementService.getStoreStatisticsByOrder(order);
        return statisticsList.stream().filter(a->{
            if (a.getAssignedDatetime() != null && a.getPickedUpDatetime() != null && a.getCompletedDatetime() != null  && a.getReturnDatetime() != null){
                if (a.getReservationStatus().equals("1")) {
                    LocalDateTime reserveToCreated = LocalDateTime.parse((a.getReservationDatetime()).replace(" ", "T"));
                    a.setCreatedDatetime(reserveToCreated.minusMinutes(30).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S")));
                }
                /* LocalDateTime assignTime = LocalDateTime.parse((a.getAssignedDatetime()).replace(" ", "T"));*/
                LocalDateTime pickupTime = LocalDateTime.parse((a.getPickedUpDatetime()).replace(" ", "T"));
                LocalDateTime completeTime = LocalDateTime.parse((a.getCompletedDatetime()).replace(" ", "T"));
                LocalDateTime returnTime = LocalDateTime.parse((a.getReturnDatetime()).replace(" ", "T"));
                // 19.08.26 데이터 격차가 음수로 나오는지 여부 체크
                LocalDateTime createdTime = LocalDateTime.parse((a.getCreatedDatetime()).replace(" ", "T"));
//                if(assignTime.until(pickupTime, ChronoUnit.SECONDS)>=120 && pickupTime.until(completeTime, ChronoUnit.SECONDS)>=120 && completeTime.until(returnTime, ChronoUnit.SECONDS)>=120){

                // 19.08.26 페이지에서 음수가 나오는 오류 사항 변경
                if(completeTime.until(returnTime, ChronoUnit.SECONDS)>=60 && !(createdTime.until(completeTime, ChronoUnit.SECONDS) < 0 || createdTime.until(pickupTime, ChronoUnit.SECONDS) < 0 || createdTime.until(returnTime, ChronoUnit.SECONDS) < 0)){
                    return true;
                }else{
                    /* 19.08.26 디버그 시 내용 확인*/

//                    System.out.println("###################################################");
//                    System.out.println(a.getId() + " % " + a.getRegOrderId());
//                    System.out.println(completeTime.until(returnTime, ChronoUnit.SECONDS));
//                    System.out.println(createdTime.until(completeTime, ChronoUnit.SECONDS));
//                    System.out.println(createdTime.until(pickupTime, ChronoUnit.SECONDS));
//                    System.out.println(createdTime.until(returnTime, ChronoUnit.SECONDS));
//                    System.out.println("###################################################");

                    return false;
                }
            }else{
                return false;
            }
        }).collect(Collectors.toList());//서비스로 빼면 안됨(해당 스트림 필터는 해당 컨트롤러에서만 필요)
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
    /*@GetMapping("/excelDownload")
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
    }*/

    @GetMapping("/excelDownloadByOrder")
    public ModelAndView statisticsByOrderExcelDownload(HttpServletResponse response, @RequestParam(value = "startDate") String startDate, @RequestParam(value = "endDate") String endDate) {
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

            // 31일까지만 가능
            if (diffDays > 31){
                return null;
            }

            order.setDays(Integer.toString((int) (long) diffDays + 1));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        ModelAndView modelAndView = new ModelAndView("StoreStatisticsByOrderExcelBuilderServiceImpl");
        List<Order> storeStatisticsByOrderList = storeStatementService.getStoreStatisticsByOrder(order);
        modelAndView.addObject("getStoreStatisticsByOrderExcel", storeStatisticsByOrderList);

        return modelAndView;
    }


    // 2번째 페이지
    @ResponseBody
    @GetMapping("/getStoreStatisticsByDate")
    @CnttMethodDescription("날짜별 통계 리스트 조회")
    public List<ByDate> getStoreStatisticsByDate(@RequestParam(value = "startDate") String startDate
            ,@RequestParam(value = "endDate") String endDate){
        // 날짜
        SecurityUser storeInfo = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getDetails();
        Order order = new Order();
        order.setCurrentDatetime(startDate);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date sdfStartDate = formatter.parse(startDate);
            Date sdfEndDate = formatter.parse(endDate);
            long diff = sdfEndDate.getTime() - sdfStartDate.getTime();
            long diffDays = diff / (24 * 60 * 60 * 1000);

            //31일까지만 조회가능
            if (diffDays > 31){
                return new ArrayList<>();
            }

            order.setDays(Integer.toString((int) (long) diffDays + 1));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        order.setToken(storeInfo.getStoreAccessToken());

        List<ByDate> statisticsDate = storeStatementService.getStoreStatisticsByDate(order);

        return statisticsDate;
    }

    // 날짜별 통계 리스트 엑셀 다운
    @GetMapping("/excelDownloadByDate")
    public ModelAndView statisticsByDateExcelDownload(HttpServletResponse response, @RequestParam(value = "startDate") String startDate, @RequestParam(value = "endDate") String endDate) {
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

            //31일까지만 가능
            if (diffDays>31){
                return null;
            }

            order.setDays(Integer.toString((int) (long) diffDays + 1));
        } catch (ParseException e) {
            e.printStackTrace();
        }

//        order.getStore().setStoreName(myStore.getStoreName());

        ModelAndView modelAndView = new ModelAndView("StoreStatisticsByDateExcelBuilderServiceImpl");
        List<ByDate> storeStatisticsByDate = storeStatementService.getStoreStatisticsByDate(order);
        modelAndView.addObject("getStoreStatisticsByDateExcel", storeStatisticsByDate);

        return modelAndView;
    }

    @ResponseBody
    @GetMapping("/getStoreStatisticsByInterval")
    @CnttMethodDescription("구간별 통계 리스트 조회")
    public Map getStoreStatisticsByInterval(@RequestParam(value = "startDate") String startDate
            , @RequestParam(value = "endDate") String endDate){
        SecurityUser storeInfo = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getDetails();
        Order order = new Order();
        order.setCurrentDatetime(startDate);
        order.setEndDate(endDate);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date sdfStartDate = formatter.parse(startDate);
            Date sdfEndDate = formatter.parse(endDate);
            long diff = sdfEndDate.getTime() - sdfStartDate.getTime();
            long diffDays = diff / (24 * 60 * 60 * 1000);

            //31일까지만 조회가능
            if (diffDays > 31){
                return new HashMap();
            }

            order.setDays(Integer.toString((int) (long) diffDays + 1));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        order.setToken(storeInfo.getStoreAccessToken());

        Interval statisticsInterval = storeStatementService.getStoreStatisticsByInterval(order);
        //구간별 통계 리스트 조회페이지에 추가된 그래프 정보
        List<Map> statisticsMin30Below = storeStatementService.getStoreStatisticsMin30BelowByDate(order);

        Map result = new HashMap();
        result.put("intervalData", statisticsInterval);
        result.put("intervalMin30Below", statisticsMin30Below);

        return result;
    }

    @GetMapping("/excelDownloadByInterval")
    public ModelAndView statisticsByIntervalExcelDownload(HttpServletResponse response, @RequestParam(value = "startDate") String startDate, @RequestParam(value = "endDate") String endDate) {
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

            //31일까지만 가능
            if (diffDays > 31){
                return null;
            }

            order.setDays(Integer.toString((int) (long) diffDays + 1));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        ModelAndView modelAndView = new ModelAndView("StoreStatisticsByIntervalExcelBuilderServiceImpl");
        Interval storeStatisticsByInterval = storeStatementService.getStoreStatisticsByInterval(order);
        modelAndView.addObject("getStoreStatisticsByIntervalExcel", storeStatisticsByInterval);

        return modelAndView;
    }

}
