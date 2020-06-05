package kr.co.deliverydispatch.controller;

import kr.co.cntt.core.annotation.CnttMethodDescription;
import kr.co.cntt.core.model.order.Order;
import kr.co.cntt.core.model.statistic.ByDate;
import kr.co.cntt.core.model.statistic.Interval;
import kr.co.cntt.core.model.statistic.IntervalAtTWKFC;
import kr.co.deliverydispatch.security.SecurityUser;
import kr.co.deliverydispatch.service.StoreStatementService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
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

/**
 * 2020.06.04 통계 페이지 브랜드별 분리를 위한 내용
 * 단, 기본적인 URL은 동일하게 가되, 실제 필요로 하는 데이터는 브랜드별로 분기 처리로 진행
 * */

@Slf4j
@Controller
public class StatisticsKFCController {
    /**
     * 객체 주입
     */
    private StoreStatementService storeStatementService;

    @Autowired
    public StatisticsKFCController(StoreStatementService storeStatementService) { this.storeStatementService = storeStatementService; }

    @Value("${spring.mvc.locale}")
    private Locale regionLocale;

    /**
     * orderStatement_tw_kfc.html
     * */
    @ResponseBody
    @GetMapping("/getStoreStatisticsByOrderAtTWKFC")
    @CnttMethodDescription("주문별 통계 리스트 조회 TW KFC")
    public List<Order> getStoreStatisticsByOrderAtTWKFC(@RequestParam(value = "startDate") String startDate
            , @RequestParam(value = "endDate") String endDate){
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
                    // 2020.05.18 예약시간 - 30분 시간이 실제 주문 시간보다 큰 경우에만 적용
                    LocalDateTime createDatetime = LocalDateTime.parse((a.getCreatedDatetime()).replace(" ", "T"));
                    LocalDateTime bookingDatetime = LocalDateTime.parse((a.getReservationDatetime()).replace(" ", "T"));

                    if (createDatetime.isBefore(bookingDatetime.minusMinutes(30))){
                        LocalDateTime reserveToCreated = LocalDateTime.parse((a.getReservationDatetime()).replace(" ", "T"));
                        a.setCreatedDatetime(reserveToCreated.minusMinutes(30).format(DateTimeFormatter.ofPattern("YYYY-MM-dd HH:mm:ss.S")));
                    }
                }
                LocalDateTime pickupTime = LocalDateTime.parse((a.getPickedUpDatetime()).replace(" ", "T"));
                LocalDateTime completeTime = LocalDateTime.parse((a.getCompletedDatetime()).replace(" ", "T"));
                LocalDateTime returnTime = LocalDateTime.parse((a.getReturnDatetime()).replace(" ", "T"));
                // 19.08.26 데이터 격차가 음수로 나오는지 여부 체크 / 날짜 기준 변경
                LocalDateTime assignTime = LocalDateTime.parse((a.getAssignedDatetime()).replace(" ", "T"));

                // 19.08.26 페이지에서 음수가 나오는 오류 사항 변경
                // 20.05.22 기준 시간 변경
                if(completeTime.until(returnTime, ChronoUnit.SECONDS)>=60 && !(assignTime.until(completeTime, ChronoUnit.SECONDS) < 0 || assignTime.until(pickupTime, ChronoUnit.SECONDS) < 0 || assignTime.until(returnTime, ChronoUnit.SECONDS) < 0)){
                    return true;
                }else{
                    return false;
                }
            }else{
                return false;
            }
        }).collect(Collectors.toList());//서비스로 빼면 안됨(해당 스트림 필터는 해당 컨트롤러에서만 필요)
    }

    @GetMapping("/excelDownloadByOrderAtTWKFC")
    public ModelAndView statisticsByOrderAtTWKFCExcelDownload(HttpServletResponse response, @RequestParam(value = "startDate") String startDate, @RequestParam(value = "endDate") String endDate) {
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

        ModelAndView modelAndView = new ModelAndView("StoreStatisticsByOrderAtTWKFCExcelBuilderServiceImpl");
        List<Order> storeStatisticsByOrderList = storeStatementService.getStoreStatisticsByOrder(order);

        /*
         * 2020.04.17 통계 View와 동일하게 표시될 수 있도록 적용
         * */
        List<Order> filerStoreStatisticsByOrderList =
                storeStatisticsByOrderList.stream().filter(a -> {
                    // 다음 4가지의 모든 시간이 NULL 이 아닌 경우만 가져온다
                    if (a.getAssignedDatetime() != null && a.getPickedUpDatetime() != null && a.getCompletedDatetime() != null && a.getReturnDatetime() != null){
                        // 예약 주문인 경우 30분을 제외한다.
                        if (a.getReservationStatus().equals("1")){
                            // 2020.05.18 예약시간 - 30분 시간이 실제 주문 시간보다 큰 경우에만 적용
                            LocalDateTime createDatetime = LocalDateTime.parse((a.getCreatedDatetime()).replace(" ", "T"));
                            LocalDateTime bookingDatetime = LocalDateTime.parse((a.getReservationDatetime()).replace(" ", "T"));

                            if (createDatetime.isBefore(bookingDatetime.minusMinutes(30))){
                                LocalDateTime reserveToCreated = LocalDateTime.parse((a.getReservationDatetime()).replace(" ", "T"));
                                a.setCreatedDatetime((reserveToCreated.minusMinutes(30).format(DateTimeFormatter.ofPattern("YYYY-MM-dd HH:mm:ss.S"))));
                            }
                        }

                        // 픽업 시간
                        LocalDateTime pickupTime = LocalDateTime.parse((a.getPickedUpDatetime()).replace(" ", "T"));
                        // 배달 완료 시간
                        LocalDateTime completeTime = LocalDateTime.parse((a.getCompletedDatetime()).replace(" ", "T"));
                        // 기사 복귀 시간
                        LocalDateTime returnTime = LocalDateTime.parse((a.getReturnDatetime()).replace(" ", "T"));

                        // 주문 등록 시간
                        //LocalDateTime createdTime = LocalDateTime.parse((a.getCreatedDatetime()).replace(" ", "T"));
                        LocalDateTime assignTime = LocalDateTime.parse((a.getAssignedDatetime()).replace(" ", "T"));

                        // 다음 조건에 부합한 경우만 표기되도록 적용
                        //if (completeTime.until(returnTime, ChronoUnit.SECONDS) >= 60 && !(createdTime.until(completeTime, ChronoUnit.SECONDS) < 0 || createdTime.until(pickupTime, ChronoUnit.SECONDS) < 0 || createdTime.until(returnTime, ChronoUnit.SECONDS) < 0)){
                        if (completeTime.until(returnTime, ChronoUnit.SECONDS) >= 60 && !(assignTime.until(completeTime, ChronoUnit.SECONDS) < 0 || assignTime.until(pickupTime, ChronoUnit.SECONDS) < 0 || assignTime.until(returnTime, ChronoUnit.SECONDS) < 0)){
                            return  true;
                        }else {
                            return  false;
                        }
                    }else{
                        return  false;
                    }
                }).collect(Collectors.toList());

        modelAndView.addObject("getStoreStatisticsByOrderAtTWKFCExcel", filerStoreStatisticsByOrderList);


        return modelAndView;
    }


    /**
     * dateStatement_tw_kfc.html
     * */
    @ResponseBody
    @GetMapping("/getStoreStatisticsByDateAtTWKFC")
    @CnttMethodDescription("날짜별 통계 리스트 조회 TW KFC")
    public List<ByDate> getStoreStatisticsByDate(@RequestParam(value = "startDate") String startDate
            , @RequestParam(value = "endDate") String endDate){
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

        List<ByDate> statisticsDate = storeStatementService.getStoreStatisticsByDateAtTWKFC(order);

        return statisticsDate;
    }

    // 날짜별 통계 리스트 엑셀 다운
    @GetMapping("/excelDownloadByDateAtTWKFC")
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

        ModelAndView modelAndView = new ModelAndView("StoreStatisticsByDateAtTWKFCExcelBuilderServiceImpl");
        List<ByDate> storeStatisticsByDate = storeStatementService.getStoreStatisticsByDateAtTWKFC(order);
        modelAndView.addObject("getStoreStatisticsByDateAtTWKFCExcel", storeStatisticsByDate);

        return modelAndView;
    }

    @ResponseBody
    @GetMapping("/getStoreStatisticsByIntervalAtTWKFC")
    @CnttMethodDescription("구간별 통계 리스트 조회 TW KFC")
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

        IntervalAtTWKFC statisticsInterval = storeStatementService.getStoreStatisticsByIntervalAtTWKFC(order);
        //구간별 통계 리스트 조회페이지에 추가된 그래프 정보
        List<Map> statisticsMin30Below = storeStatementService.getStoreStatisticsMin30BelowByDateAtTWKFC(order);

        Map result = new HashMap();
        result.put("intervalData", statisticsInterval);
        result.put("intervalMin30Below", statisticsMin30Below);

        return result;
    }

    @GetMapping("/excelDownloadByIntervalAtTWKFC")
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

        ModelAndView modelAndView = new ModelAndView("StoreStatisticsByIntervalAtTWKFCExcelBuilderServiceImpl");
        IntervalAtTWKFC storeStatisticsByInterval = storeStatementService.getStoreStatisticsByIntervalAtTWKFC(order);
        modelAndView.addObject("getStoreStatisticsByIntervalAtTWKFCExcel", storeStatisticsByInterval);

        return modelAndView;
    }
}
