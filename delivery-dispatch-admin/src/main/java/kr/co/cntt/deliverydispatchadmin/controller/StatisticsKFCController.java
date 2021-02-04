package kr.co.cntt.deliverydispatchadmin.controller;

import kr.co.cntt.core.annotation.CnttMethodDescription;
import kr.co.cntt.core.model.group.Group;
import kr.co.cntt.core.model.group.SubGroup;
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
    public List<Order> getStoreStatisticsByOrderAtTWKFC(@RequestParam(value = "startDate") String startDate
            , @RequestParam(value = "endDate") String endDate
            , @RequestParam(value = "groupID", required = false) String groupId
            //, @RequestParam(value = "subGroupID", required = false) String subGroupId
            , @RequestParam(value = "subGroupName", required = false) String subGroupName
            , @RequestParam(value = "storeID", required = false) String storeId) {
        // ADMIN 정보
        SecurityUser adminInfo = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getDetails();
        Order order = new Order();
        order.setCurrentDatetime(startDate);

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

        try {
            Date sdfStartDate = formatter.parse(startDate);
            Date sdfEndDate = formatter.parse(endDate);

            long diff = sdfEndDate.getTime() - sdfStartDate.getTime();
            long diffDays = diff / (24 * 60 * 60 * 1000);

            if (diffDays > 31) {
                return new ArrayList<>();
            }

            order.setDays((Integer.toString(((int) (long) diffDays + 1))));

        } catch (ParseException ex) {
            ex.printStackTrace();
        }

        order.setToken(adminInfo.getAdminAccessToken());

        if (groupId.trim() != "" && !groupId.toLowerCase().equals("reset")){
            order.setGroup(new Group());
            order.getGroup().setId(groupId);
        }

        // 21-01-21 서브그룹 그룹화
        if (subGroupName.trim() != "" && !subGroupName.toLowerCase().equals("reset")){
            order.setSubGroup(new SubGroup());
            order.getSubGroup().setGroupingName(subGroupName);
        }

        if (storeId.trim() != "" && !storeId.toLowerCase().equals("reset")){
            order.setStoreId(storeId);
        }


        List<Order> statistByOrder = statisticsAdminService.selectStoreStatisticsByOrderForAdmin(order);

        return statistByOrder.stream().filter(a -> {
            if (a.getAssignedDatetime() != null && a.getPickedUpDatetime() != null && a.getCompletedDatetime() != null && a.getReturnDatetime() != null) {
                if (a.getReservationStatus().equals("1")) {
                    // 2020.05.18 예약시간 - 30분 시간이 실제 주문 시간보다 큰 경우에만 적용
                    LocalDateTime createDatetime = LocalDateTime.parse((a.getCreatedDatetime()).replace(" ", "T"));
                    LocalDateTime bookingDatetime = LocalDateTime.parse((a.getReservationDatetime()).replace(" ", "T"));

                    if (createDatetime.isBefore(bookingDatetime.minusMinutes(30))){
                        LocalDateTime reserveToCreated = LocalDateTime.parse((a.getReservationDatetime()).replace(" ", "T"));
                        a.setCreatedDatetime(reserveToCreated.minusMinutes(30).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S")));
                    }
                }

                LocalDateTime pickupTime = LocalDateTime.parse((a.getPickedUpDatetime()).replace(" ", "T"));
                LocalDateTime completeTime = LocalDateTime.parse((a.getCompletedDatetime()).replace(" ", "T"));
                LocalDateTime returnTime = LocalDateTime.parse((a.getReturnDatetime()).replace(" ", "T"));
                // 19.08.26 데이터 격차가 음수로 나오는지 여부 체크
                //LocalDateTime createdTime = LocalDateTime.parse((a.getCreatedDatetime()).replace(" ", "T"));
                LocalDateTime assignTime = LocalDateTime.parse((a.getAssignedDatetime()).replace(" ", "T"));

                // 19.08.26 페이지에서 음수가 나오는 오류 사항 변경
                //if (completeTime.until(returnTime, ChronoUnit.SECONDS) >= 60 && !(createdTime.until(completeTime, ChronoUnit.SECONDS) < 0 || createdTime.until(pickupTime, ChronoUnit.SECONDS) < 0 || createdTime.until(returnTime, ChronoUnit.SECONDS) < 0)) {
                if (completeTime.until(returnTime, ChronoUnit.SECONDS) >= 60 && !(assignTime.until(completeTime, ChronoUnit.SECONDS) < 0 || assignTime.until(pickupTime, ChronoUnit.SECONDS) < 0 || assignTime.until(returnTime, ChronoUnit.SECONDS) < 0)) {
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
    public ModelAndView statisticsByOrderExcelDownloadAtTWKFC(HttpServletResponse response
            , @RequestParam(value = "startDate") String startDate
            , @RequestParam(value = "endDate") String endDate){
        response.setHeader("Set-Cookie", "fileDownload=true; path=/");

        // ADMIN 정보
        SecurityUser adminInfo = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getDetails();
        Order order = new Order();
        order.setCurrentDatetime(startDate);

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

        try {
            Date sdfStartDate = formatter.parse(startDate);
            Date sdfEndDate = formatter.parse(endDate);
            long diff = sdfEndDate.getTime() - sdfStartDate.getTime();
            long diffDays = diff / (24 * 60 * 60 * 1000);

            if (diffDays > 31){
                return null;
            }

            order.setDays(Integer.toString((int)(long) diffDays + 1));

        }catch (ParseException e){
            e.printStackTrace();
        }

        order.setToken(adminInfo.getAdminAccessToken());
        ModelAndView modelAndView = new ModelAndView("StatisticsAdminOrderAtTWKFCBuilderServiceImpl");
        List<Order> storeOrderListByAdmin = statisticsAdminService.selectStoreStatisticsByOrderForAdmin(order);

        List<Order> filterStoreOrderListByAdmin =
                storeOrderListByAdmin.stream().filter(a -> {
                    // 다음 4가지의 모든 시간이 NULL 이 아닌 경우만 가져온다
                    if (a.getAssignedDatetime() != null && a.getPickedUpDatetime() != null && a.getCompletedDatetime() != null && a.getReturnDatetime() != null){
                        // 예약 주문인 경우 30분을 제외한다.
                        if (a.getReservationStatus().equals("1")){
                            // 2020.05.18 예약시간 - 30분 시간이 실제 주문 시간보다 큰 경우에만 적용
                            LocalDateTime createDatetime = LocalDateTime.parse((a.getCreatedDatetime()).replace(" ", "T"));
                            LocalDateTime bookingDatetime = LocalDateTime.parse((a.getReservationDatetime()).replace(" ", "T"));

                            if (createDatetime.isBefore(bookingDatetime.minusMinutes(30))){
                                LocalDateTime reserveToCreated = LocalDateTime.parse((a.getReservationDatetime()).replace(" ", "T"));
                                a.setCreatedDatetime((reserveToCreated.minusMinutes(30).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S"))));
                            }
                        }

                        // 픽업 시간
                        LocalDateTime pickupTime = LocalDateTime.parse((a.getPickedUpDatetime()).replace(" ", "T"));
                        // 배달 완료 시간
                        LocalDateTime completeTime = LocalDateTime.parse((a.getCompletedDatetime()).replace(" ", "T"));
                        // 기사 복귀 시간
                        LocalDateTime returnTime = LocalDateTime.parse((a.getReturnDatetime()).replace(" ", "T"));

                        // 주문 등록 시간
                        // LocalDateTime createdTime = LocalDateTime.parse((a.getCreatedDatetime()).replace(" ", "T"));
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

        modelAndView.addObject("selectStoreStatisticsByOrderForAdminAtTWKFC", filterStoreOrderListByAdmin);

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
    public List<AdminByDate> getStoreStatisticsByDate(@RequestParam("startDate") String startDate
            , @RequestParam("endDate") String endDate
            , @RequestParam(value = "timeCheck") Boolean chkTime
            , @RequestParam(value = "peakCheck") Boolean peakTime
            , @RequestParam(value = "peakType") String peakType
            , @RequestParam(value = "groupID", required = false) String groupId
            //, @RequestParam(value = "subGroupID", required = false) String subGroupId
            , @RequestParam(value = "subGroupName", required = false) String subGroupName
            , @RequestParam(value = "storeID", required = false) String storeId){
        // ADMIN 정보
        SecurityUser adminInfo = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getDetails();
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
            long diffDays = diff / (24* 60 * 60 * 1000);

            if (diffDays > 31) {
                return new ArrayList<>();
            }

            if (sdfStartDate.getTime() > sdfEndDate.getTime()){
                return new ArrayList<>();
            }

            order.setDays(Integer.toString((int) (long) diffDays + 1));
        }catch (ParseException e){
            e.printStackTrace();
        }

        order.setToken(adminInfo.getAdminAccessToken());

        if (groupId.trim() != "" && !groupId.toLowerCase().equals("reset")){
            order.setGroup(new Group());
            order.getGroup().setId(groupId);
        }

        if (subGroupName.trim() != "" && !subGroupName.toLowerCase().equals("reset")){
            order.setSubGroup(new SubGroup());
            order.getSubGroup().setGroupingName(subGroupName);
        }

        if (storeId.trim() != "" && !storeId.toLowerCase().equals("reset")){
            order.setStoreId(storeId);
        }

        List<AdminByDate> byDateList = statisticsAdminService.selectStoreStatisticsByDateForAdminAtTWKFC(order);

        return byDateList;
    }

    @GetMapping("/excelDownloadByDateAtTWKFC")
    @CnttMethodDescription("관리자 기간별 통계 리스트 엑셀 출력 TW KFC")
    public ModelAndView statisticsByDateExcelDownload(HttpServletResponse response,
                                                      @RequestParam(value = "startDate") String startDate,
                                                      @RequestParam(value = "endDate") String endDate,
                                                      @RequestParam(value = "timeCheck") Boolean chkTime,
                                                      @RequestParam(value = "peakCheck") Boolean peakTime,
                                                      @RequestParam(value = "peakType") String peakType){
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
            long diffDays = diff / (24 * 60 * 60 * 1000);

            if (diffDays > 31){
                return null;
            }

            if (sdfStartDate.getTime() > sdfEndDate.getTime()){
                return null;
            }

            order.setDays(Integer.toString((int)(long) diffDays + 1));

        }catch (ParseException e){
            e.printStackTrace();
        }

        order.setToken(adminInfo.getAdminAccessToken());
        ModelAndView modelAndView = new ModelAndView("StatisticsAdminByDateAtTWKFCBuilderServiceImpl");
        List<AdminByDate> storeOrderListByAdmin = statisticsAdminService.selectStoreStatisticsByDateForAdminAtTWKFC(order);

        modelAndView.addObject("selectStoreStatisticsByDateForAdminAtTWKFC", storeOrderListByAdmin);

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
