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
                LocalDateTime reserveDatetime = LocalDateTime.parse((a.getReservationDatetime()).replace(" ", "T"));
                LocalDateTime pickupTime = LocalDateTime.parse((a.getPickedUpDatetime()).replace(" ", "T"));
                // 배달 도착 시간
                LocalDateTime arrivedTime = LocalDateTime.parse((a.getArrivedDatetime()).replace(" ", "T"));
                //LocalDateTime completeTime = LocalDateTime.parse((a.getCompletedDatetime()).replace(" ", "T"));
                LocalDateTime returnTime = LocalDateTime.parse((a.getReturnDatetime()).replace(" ", "T"));
                int qtTime = 0;

                try {
                    qtTime = Integer.parseInt(a.getCookingTime());
                }catch (Exception e){
                    log.error("QT Time 파싱 중 오류 발생", e);
                    qtTime = 30;
                }

                if (qtTime > 30 || qtTime < 1) qtTime = 30;

                // 21.05.27 배정 시간의 규칙 변경
                // 배정 시간이 예약 시간 - QT 시간보다 늦어진 경우에 예약 - QT 시간으로 계산한다.
                LocalDateTime assignTime = LocalDateTime.parse((a.getAssignedDatetime()).replace(" ", "T"));
                LocalDateTime qtAssignTime = reserveDatetime.minusMinutes(qtTime);

                if (ChronoUnit.MINUTES.between(assignTime, qtAssignTime) < 0){
                    assignTime = qtAssignTime;
                    a.setAssignedDatetime(assignTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S")));
                }

                // 19.08.26 페이지에서 음수가 나오는 오류 사항 변경
                // 20.05.22 기준 시간 변경
                if(arrivedTime.until(returnTime, ChronoUnit.SECONDS)>=60 && !(assignTime.until(arrivedTime, ChronoUnit.SECONDS) < 0 || assignTime.until(pickupTime, ChronoUnit.SECONDS) < 0 || assignTime.until(returnTime, ChronoUnit.SECONDS) < 0)){
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
                        LocalDateTime reserveDatetime = LocalDateTime.parse((a.getReservationDatetime()).replace(" ", "T"));
                        // 픽업 시간
                        LocalDateTime pickupTime = LocalDateTime.parse((a.getPickedUpDatetime()).replace(" ", "T"));
                        // 배달 도착 시간
                        LocalDateTime arrivedTime = LocalDateTime.parse((a.getArrivedDatetime()).replace(" ", "T"));
                        // 배달 완료 시간
                        //LocalDateTime completeTime = LocalDateTime.parse((a.getCompletedDatetime()).replace(" ", "T"));
                        // 기사 복귀 시간
                        LocalDateTime returnTime = LocalDateTime.parse((a.getReturnDatetime()).replace(" ", "T"));
                        int qtTime = 0;

                        try {
                            qtTime = Integer.parseInt(a.getCookingTime());
                        }catch (Exception e){
                            log.error("QT Time 파싱 중 오류 발생", e);
                            qtTime = 30;
                        }

                        if (qtTime > 30 || qtTime < 1) qtTime = 30;

                        // 주문 등록 시간
                        LocalDateTime assignTime = LocalDateTime.parse((a.getAssignedDatetime()).replace(" ", "T"));
                        LocalDateTime qtAssignTime = reserveDatetime.minusMinutes(qtTime);

                        if (ChronoUnit.MINUTES.between(assignTime, qtAssignTime) < 0){
                            assignTime = qtAssignTime;
                            a.setAssignedDatetime(assignTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S")));
                        }

                        if (arrivedTime.until(returnTime, ChronoUnit.SECONDS) >= 60 && !(assignTime.until(arrivedTime, ChronoUnit.SECONDS) < 0 || assignTime.until(pickupTime, ChronoUnit.SECONDS) < 0 || assignTime.until(returnTime, ChronoUnit.SECONDS) < 0)){
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
    public List<ByDate> getStoreStatisticsByDate(@RequestParam(value = "startDate") String startDate,
                                                 @RequestParam(value = "endDate") String endDate,
                                                 @RequestParam(value = "timeCheck") Boolean chkTime,
                                                 @RequestParam(value = "peakCheck") Boolean peakTime,
                                                 @RequestParam(value = "peakType") String peakType){
        // 날짜
        SecurityUser storeInfo = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getDetails();
        Order order = new Order();
        order.setCurrentDatetime(startDate);
        order.setEndDate(endDate);
        order.setChkTime(chkTime);
        order.setChkPeakTime(peakTime);
        order.setPeakType(peakType);

        /**
         * 20.12.26 데이터 구하는 방식 변경
         * */
        SimpleDateFormat formatter;

        if (chkTime && !peakTime){
            formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        }else{
            formatter = new SimpleDateFormat("yyyy-MM-dd");
        }

        try {
            Date sdfStartDate = formatter.parse(startDate);
            Date sdfEndDate = formatter.parse(endDate);
            long diff = sdfEndDate.getTime() - sdfStartDate.getTime();
            long diffDays = diff / (24 * 60 * 60 * 1000);

            //31일까지만 조회가능
            if (diffDays > 31){
                return new ArrayList<>();
            }

            if (sdfStartDate.getTime() > sdfEndDate.getTime()){
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
    public ModelAndView statisticsByDateExcelDownload(HttpServletResponse response,
                                                      @RequestParam(value = "startDate") String startDate,
                                                      @RequestParam(value = "endDate") String endDate,
                                                      @RequestParam(value = "timeCheck") Boolean chkTime,
                                                      @RequestParam(value = "peakCheck") Boolean peakTime,
                                                      @RequestParam(value = "peakType") String peakType) {
        response.setHeader("Set-Cookie", "fileDownload=true; path=/");
        SecurityUser storeInfo = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getDetails();
        Order order = new Order();
        order.setToken(storeInfo.getStoreAccessToken());
        order.setCurrentDatetime(startDate);
        order.setEndDate(endDate);
        order.setChkTime(chkTime);
        order.setChkPeakTime(peakTime);
        order.setPeakType(peakType);

        /**
         * 20.12.24 데이터 구하는 방식 변경
         * */
        SimpleDateFormat formatter;

        if (chkTime && !peakTime){
            formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        }else{
            formatter = new SimpleDateFormat("yyyy-MM-dd");
        }

        try {
            Date sdfStartDate = formatter.parse(startDate);
            Date sdfEndDate = formatter.parse(endDate);
            long diff = sdfEndDate.getTime() - sdfStartDate.getTime();
            long diffDays = diff / (24 * 60 * 60 * 1000);

            //31일까지만 가능
            if (diffDays>31){
                return null;
            }

            if (sdfStartDate.getTime() > sdfEndDate.getTime()){
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
    public Map getStoreStatisticsByInterval(@RequestParam(value = "startDate") String startDate,
                                            @RequestParam(value = "endDate") String endDate,
                                            @RequestParam(value = "timeCheck") Boolean chkTime,
                                            @RequestParam(value = "peakCheck") Boolean peakTime,
                                            @RequestParam(value = "peakType") String peakType){
        SecurityUser storeInfo = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getDetails();
        Order order = new Order();
        order.setCurrentDatetime(startDate);
        order.setEndDate(endDate);
        order.setChkTime(chkTime);
        order.setChkPeakTime(peakTime);
        order.setPeakType(peakType);

        /**
         * 20.12.24 데이터 구하는 방식 변경
         * */
        SimpleDateFormat formatter;

        if (chkTime && !peakTime){
            formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        }else{
            formatter = new SimpleDateFormat("yyyy-MM-dd");
        }

        try {
            Date sdfStartDate = formatter.parse(startDate);
            Date sdfEndDate = formatter.parse(endDate);
            long diff = sdfEndDate.getTime() - sdfStartDate.getTime();
            long diffDays = diff / (24 * 60 * 60 * 1000);

            //31일까지만 조회가능
            if (diffDays > 31){
                return new HashMap();
            }

            if (sdfStartDate.getTime() > sdfEndDate.getTime()){
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
    public ModelAndView statisticsByIntervalExcelDownload(HttpServletResponse response,
                                                          @RequestParam(value = "startDate") String startDate,
                                                          @RequestParam(value = "endDate") String endDate,
                                                          @RequestParam(value = "timeCheck") Boolean chkTime,
                                                          @RequestParam(value = "peakCheck") Boolean peakTime,
                                                          @RequestParam(value = "peakType") String peakType) {
        response.setHeader("Set-Cookie", "fileDownload=true; path=/");
        SecurityUser storeInfo = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getDetails();
        Order order = new Order();
        order.setToken(storeInfo.getStoreAccessToken());
        order.setCurrentDatetime(startDate);
        order.setEndDate(endDate);
        order.setChkTime(chkTime);
        order.setChkPeakTime(peakTime);
        order.setPeakType(peakType);

        /**
         * 20.12.24 데이터 구하는 방식 변경
         * */
        SimpleDateFormat formatter;

        if (chkTime && !peakTime){
            formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        }else{
            formatter = new SimpleDateFormat("yyyy-MM-dd");
        }

        try {
            Date sdfStartDate = formatter.parse(startDate);
            Date sdfEndDate = formatter.parse(endDate);
            long diff = sdfEndDate.getTime() - sdfStartDate.getTime();
            long diffDays = diff / (24 * 60 * 60 * 1000);

            //31일까지만 가능
            if (diffDays > 31){
                return null;
            }

            if (sdfStartDate.getTime() > sdfEndDate.getTime()){
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

    /**
     * 2020-06-16 orderdetail.html
     * */
    @ResponseBody
    @GetMapping("/getStoreOrderList")
    @CnttMethodDescription("날짜별 주문 리스트 조회 통합")
    public List<Order> getStoreOrderList(@RequestParam(value = "startDate") String startDate, @RequestParam(value = "endDate") String endDate){
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

        List<Order> orderList = storeStatementService.getStoreOrderList(order);


        return orderList;
    }

    @GetMapping("/excelDownloadByOrderList")
    public ModelAndView statisticsByOrderListExcelDownload(HttpServletResponse response, @RequestParam(value = "startDate") String startDate, @RequestParam(value = "endDate") String endDate){
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

        ModelAndView modelAndView = new ModelAndView("StoreStatisticsByOrderDetailExcelBuilderServiceImpl");
        List<Order> storeStatisticsByOrderList = storeStatementService.getStoreOrderList(order);
        modelAndView.addObject("getStoreStatisticsByOrderListExcel", storeStatisticsByOrderList);

        return modelAndView;
    }

}
