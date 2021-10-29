package kr.co.cntt.deliverydispatchadmin.controller;

import kr.co.cntt.core.annotation.CnttMethodDescription;
import kr.co.cntt.core.model.common.SearchInfo;
import kr.co.cntt.core.model.order.Order;
import kr.co.cntt.core.model.statistic.AdminByDate;
import kr.co.cntt.core.model.statistic.IntervalAtTWKFC;
import kr.co.cntt.core.service.admin.GroupAdminService;
import kr.co.cntt.core.service.admin.NoticeAdminService;
import kr.co.cntt.core.service.admin.StatisticsAdminService;
import kr.co.cntt.core.service.admin.StoreAdminService;
import kr.co.cntt.deliverydispatchadmin.security.SecurityUser;
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

@Slf4j
@Controller
public class StatisticsKFCController {
    /**
     * 객체 주입
     */
    StatisticsAdminService statisticsAdminService;

    /**
     * 객체 주입
     */
    StoreAdminService storeAdminService;

    /**
     * 객체 주입
     */
    GroupAdminService groupAdminService;

    /**
     * 객체 주입
     */
    NoticeAdminService noticeAdminService;

    @Value("${spring.mvc.locale}")
    private Locale regionLocale;

    @Autowired
    public StatisticsKFCController(StatisticsAdminService statisticsAdminService,StoreAdminService storeAdminService,NoticeAdminService noticeAdminService,GroupAdminService groupAdminService){
        this.statisticsAdminService = statisticsAdminService;
        this.storeAdminService = storeAdminService;
        this.groupAdminService = groupAdminService;
        this.noticeAdminService = noticeAdminService;
    }


    /**
     * orderStatement_tw_kfc.html
     * */

    /**
     * 주문별 통계 페이지
     * Main 통계 페이지와는 다소 다름
     * */
    @ResponseBody
    @GetMapping("/getStoreStatisticsByOrderAtTWKFC")
    @CnttMethodDescription("관리자 주문별 통계 리스트 조회 TW KFC")
    public List<Order> getStoreStatisticsByOrderAtTWKFC(SearchInfo searchInfo) {
        // ADMIN 정보
        SecurityUser adminInfo = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getDetails();
        searchInfo.setCurrentDatetime(searchInfo.getSDate());

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

        try {
            Date sdfStartDate = formatter.parse(searchInfo.getSDate());
            Date sdfEndDate = formatter.parse(searchInfo.getEDate());

            long diff = sdfEndDate.getTime() - sdfStartDate.getTime();
            long diffDays = diff / (24 * 60 * 60 * 1000);

            if (diffDays > 31) {
                return new ArrayList<>();
            }

            searchInfo.setDays((Integer.toString(((int) (long) diffDays + 1))));

        } catch (ParseException ex) {
            ex.printStackTrace();
        }

        searchInfo.setToken(adminInfo.getAdminAccessToken());

        List<Order> statistByOrder = statisticsAdminService.selectStoreStatisticsByOrderForAdmin(searchInfo);

        return statistByOrder.stream().filter(a -> {
            if (a.getAssignedDatetime() != null && a.getPickedUpDatetime() != null && a.getCompletedDatetime() != null && a.getReturnDatetime() != null) {
                LocalDateTime reserveDatetime = LocalDateTime.parse((a.getReservationDatetime()).replace(" ", "T"));
                LocalDateTime pickupTime = LocalDateTime.parse((a.getPickedUpDatetime()).replace(" ", "T"));
                LocalDateTime arrivedTime = LocalDateTime.parse((a.getArrivedDatetime()).replace(" ", "T"));
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

                // 초 단위로 비교하여, 예약 시간 - QT가 될 수 있도록 적용한다
                if (ChronoUnit.SECONDS.between(assignTime, qtAssignTime) < 0){
                    assignTime = qtAssignTime;
                    a.setAssignedDatetime(assignTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S")));
                }

                // 19.08.26 페이지에서 음수가 나오는 오류 사항 변경
                if (arrivedTime.until(returnTime, ChronoUnit.SECONDS) >= 60 && !(assignTime.until(arrivedTime, ChronoUnit.SECONDS) < 0 || assignTime.until(pickupTime, ChronoUnit.SECONDS) < 0 || assignTime.until(returnTime, ChronoUnit.SECONDS) < 0)) {
                    return true;
                } else {
                    return false;
                }
            } else {
                return false;
            }
        }).collect(Collectors.toList());//서비스로 빼면 안됨(해당 스트림 필터는 해당 컨트롤러에서만 필요)
    }

    @GetMapping("/excelDownloadByOrderAtTWKFC")
    @CnttMethodDescription("관리자 주문별 통계 리스트 엑셀 출력 TW KFC")
    public ModelAndView statisticsByOrderExcelDownloadAtTWKFC(HttpServletResponse response,
                                                              SearchInfo searchInfo){
        response.setHeader("Set-Cookie", "fileDownload=true; path=/");

        // ADMIN 정보
        SecurityUser adminInfo = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getDetails();
        searchInfo.setCurrentDatetime(searchInfo.getSDate());

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

        try {
            Date sdfStartDate = formatter.parse(searchInfo.getSDate());
            Date sdfEndDate = formatter.parse(searchInfo.getEDate());
            long diff = sdfEndDate.getTime() - sdfStartDate.getTime();
            long diffDays = diff / (24 * 60 * 60 * 1000);

            if (diffDays > 31){
                return null;
            }

            searchInfo.setDays(Integer.toString((int)(long) diffDays + 1));

        }catch (ParseException e){
            e.printStackTrace();
        }

        searchInfo.setToken(adminInfo.getAdminAccessToken());
        ModelAndView modelAndView = new ModelAndView("StatisticsAdminOrderAtTWKFCBuilderServiceImpl");
        List<Order> storeOrderListByAdmin = statisticsAdminService.selectStoreStatisticsByOrderForAdmin(searchInfo);

        List<Order> filterStoreOrderListByAdmin =
                storeOrderListByAdmin.stream().filter(a -> {
                    // 다음 4가지의 모든 시간이 NULL 이 아닌 경우만 가져온다
                    if (a.getAssignedDatetime() != null && a.getPickedUpDatetime() != null && a.getCompletedDatetime() != null && a.getReturnDatetime() != null){
                        LocalDateTime reserveDatetime = LocalDateTime.parse((a.getReservationDatetime()).replace(" ", "T"));
                        // 픽업 시간
                        LocalDateTime pickupTime = LocalDateTime.parse((a.getPickedUpDatetime()).replace(" ", "T"));
                        // 도착 완료 시간
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

                        // 21.05.27 배정 시간의 규칙 변경
                        // 배정 시간이 예약 시간 - QT 시간보다 늦어진 경우에 예약 - QT 시간으로 계산한다.
                        LocalDateTime assignTime = LocalDateTime.parse((a.getAssignedDatetime()).replace(" ", "T"));
                        LocalDateTime qtAssignTime = reserveDatetime.minusMinutes(qtTime);

                        // 초 단위로 비교하여, 예약 시간 - QT가 될 수 있도록 적용한다
                        if (ChronoUnit.SECONDS.between(assignTime, qtAssignTime) < 0){
                            assignTime = qtAssignTime;
                            a.setAssignedDatetime(assignTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S")));
                        }

                        // 다음 조건에 부합한 경우만 표기되도록 적용
                        //if (completeTime.until(returnTime, ChronoUnit.SECONDS) >= 60 && !(createdTime.until(completeTime, ChronoUnit.SECONDS) < 0 || createdTime.until(pickupTime, ChronoUnit.SECONDS) < 0 || createdTime.until(returnTime, ChronoUnit.SECONDS) < 0)){
                        if (arrivedTime.until(returnTime, ChronoUnit.SECONDS) >= 60 && !(assignTime.until(arrivedTime, ChronoUnit.SECONDS) < 0 || assignTime.until(pickupTime, ChronoUnit.SECONDS) < 0 || assignTime.until(returnTime, ChronoUnit.SECONDS) < 0)){
                            return  true;
                        }else {
                            return  false;
                        }
                    }else{
                        return  false;
                    }
                }).collect(Collectors.toList());

        int groupNumber = 0;
        // 그룹핑 정보도 보내기
        if (searchInfo.getGroupId().equals("reset")) {
            groupNumber = 1;
        } else if (!searchInfo.getGroupId().equals("reset") && searchInfo.getSubgroupId().equals("reset")){
            groupNumber = 2;
        }

        modelAndView.addObject("selectStoreStatisticsByOrderForAdminAtTWKFC", filterStoreOrderListByAdmin);
        modelAndView.addObject("groupNumber", groupNumber);

        return modelAndView;
    }

    ///////////////////////////////////////
    // *   주문별 통계 자료 종료 구간    * //
    ///////////////////////////////////////

    /**
     * 기간별 통계 페이지
     * */
    @ResponseBody
    @GetMapping("/getStoreStatisticsByDateAtTWKFC")
    @CnttMethodDescription("날짜별 통계 리스트 조회 TW KFC")
    public List<AdminByDate> getStoreStatisticsByDate(SearchInfo searchInfo){
        // ADMIN 정보
        SecurityUser adminInfo = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getDetails();
        searchInfo.setCurrentDatetime(searchInfo.getSDate());

        /**
         * 20.12.26 데이터 구하는 방식 변경
         * */
        SimpleDateFormat formatter;

        if (searchInfo.getChkTime() && !searchInfo.getChkPeakTime()){
            formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        }else{
            formatter = new SimpleDateFormat("yyyy-MM-dd");
        }

        try {
            Date sdfStartDate = formatter.parse(searchInfo.getSDate());
            Date sdfEndDate = formatter.parse(searchInfo.getEDate());

            long diff = sdfEndDate.getTime() - sdfStartDate.getTime();
            long diffDays = diff / (24* 60 * 60 * 1000);

            if (diffDays > 31) {
                return new ArrayList<>();
            }

            if (sdfStartDate.getTime() > sdfEndDate.getTime()){
                return new ArrayList<>();
            }

            searchInfo.setDays(Integer.toString((int) (long) diffDays + 1));
        }catch (ParseException e){
            e.printStackTrace();
        }

        searchInfo.setToken(adminInfo.getAdminAccessToken());

        List<AdminByDate> byDateList = statisticsAdminService.selectStoreStatisticsByDateForAdminAtTWKFC(searchInfo);

        return byDateList;
    }

    @GetMapping("/excelDownloadByDateAtTWKFC")
    @CnttMethodDescription("관리자 기간별 통계 리스트 엑셀 출력 TW KFC")
    public ModelAndView statisticsByDateExcelDownload(HttpServletResponse response,
                                                      SearchInfo searchInfo){
        response.setHeader("Set-Cookie", "fileDownload=true; path=/");

        // ADMIN 정보
        SecurityUser adminInfo = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getDetails();
        searchInfo.setCurrentDatetime(searchInfo.getSDate());

        /**
         * 20.12.24 데이터 구하는 방식 변경
         * */
        SimpleDateFormat formatter;

        if (searchInfo.getChkTime() && !searchInfo.getChkPeakTime()){
            formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        }else{
            formatter = new SimpleDateFormat("yyyy-MM-dd");
        }

        try {
            Date sdfStartDate = formatter.parse(searchInfo.getSDate());
            Date sdfEndDate = formatter.parse(searchInfo.getEDate());
            long diff = sdfEndDate.getTime() - sdfStartDate.getTime();
            long diffDays = diff / (24 * 60 * 60 * 1000);

            if (diffDays > 31){
                return null;
            }

            if (sdfStartDate.getTime() > sdfEndDate.getTime()){
                return null;
            }

            searchInfo.setDays(Integer.toString((int)(long) diffDays + 1));

        }catch (ParseException e){
            e.printStackTrace();
        }

        searchInfo.setToken(adminInfo.getAdminAccessToken());
        ModelAndView modelAndView = new ModelAndView("StatisticsAdminByDateAtTWKFCBuilderServiceImpl");
        List<AdminByDate> storeOrderListByAdmin = statisticsAdminService.selectStoreStatisticsByDateForAdminAtTWKFC(searchInfo);

        int groupNumber = 0;
        // 그룹핑 정보도 보내기
        if (searchInfo.getGroupId().equals("reset")) {
            groupNumber = 1;
        } else if (!searchInfo.getGroupId().equals("reset") && searchInfo.getSubgroupId().equals("reset")){
            groupNumber = 2;
        }

        modelAndView.addObject("selectStoreStatisticsByDateForAdminAtTWKFC", storeOrderListByAdmin);
        modelAndView.addObject("groupNumber", groupNumber);

        return modelAndView;
    }

    ///////////////////////////////////////
    // *   기간별 통계 자료 종료 구간    * //
    ///////////////////////////////////////

    /**
     * 누적 완료 통계 페이지
     * */
    @ResponseBody
    @GetMapping("/getStoreStatisticsByIntervalAtTWKFC")
    @CnttMethodDescription("구간별 통계 리스트 조회 TW KFC")
    public Map getStoreStatisticsByInterval(@RequestParam(value = "startDate") String startDate,
                                            @RequestParam(value = "endDate") String endDate,
                                            @RequestParam(value = "timeCheck") Boolean chkTime,
                                            @RequestParam(value = "peakCheck") Boolean peakTime,
                                            @RequestParam(value = "peakType") String peakType){
        // ADMIN 정보
        SecurityUser adminInfo = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getDetails();
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
            long diffDays = diff / (24* 60 * 60 * 1000);

            if (diffDays > 31) {
                return new HashMap();
            }

            if (sdfStartDate.getTime() > sdfEndDate.getTime()){
                return new HashMap();
            }

            order.setDays(Integer.toString((int) (long) diffDays + 1));
        }catch (ParseException e){
            e.printStackTrace();
        }

        order.setToken(adminInfo.getAdminAccessToken());


        IntervalAtTWKFC statisticsInterval = statisticsAdminService.selectAdminStatisticsByIntervalAtTWKFC(order);
        //구간별 통계 리스트 조회페이지에 추가된 그래프 정보
        List<Map> statisticsMin30Below = statisticsAdminService.selectAdminStatisticsMin30BelowByDateAtTWKFC(order);

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

        // ADMIN 정보
        SecurityUser adminInfo = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getDetails();
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
            long diffDays = diff / (24* 60 * 60 * 1000);

            if (diffDays > 31) {
                return null;
            }

            if (sdfStartDate.getTime() > sdfEndDate.getTime()){
                return null;
            }

            order.setDays(Integer.toString((int) (long) diffDays + 1));
        }catch (ParseException e){
            e.printStackTrace();
        }

        order.setToken(adminInfo.getAdminAccessToken());

        ModelAndView modelAndView = new ModelAndView("StatisticsAdminByIntervalAtTWKFCExcelBuilderServiceImpl");
        IntervalAtTWKFC storeStatisticsByInterval = statisticsAdminService.selectAdminStatisticsByIntervalAtTWKFC(order);
        modelAndView.addObject("getAdminStatisticsByIntervalExcelAtTWKFC", storeStatisticsByInterval);

        return modelAndView;
    }
    ///////////////////////////////////////
    // *  누적 완료 통계 자료 종료 구간  * //
    ///////////////////////////////////////

}
