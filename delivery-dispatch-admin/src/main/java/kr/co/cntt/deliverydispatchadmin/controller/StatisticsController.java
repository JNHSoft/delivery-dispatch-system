package kr.co.cntt.deliverydispatchadmin.controller;

import kr.co.cntt.core.annotation.CnttMethodDescription;
import kr.co.cntt.core.model.admin.Admin;
import kr.co.cntt.core.model.common.SearchInfo;
import kr.co.cntt.core.model.group.Group;
import kr.co.cntt.core.model.group.SubGroup;
import kr.co.cntt.core.model.group.SubGroupStoreRel;
import kr.co.cntt.core.model.order.Order;
import kr.co.cntt.core.model.statistic.AdminByDate;
import kr.co.cntt.core.model.statistic.Interval;
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
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
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
    public StatisticsController(StatisticsAdminService statisticsAdminService,StoreAdminService storeAdminService,NoticeAdminService noticeAdminService,GroupAdminService groupAdminService){
        this.statisticsAdminService = statisticsAdminService;
        this.storeAdminService = storeAdminService;
        this.groupAdminService = groupAdminService;
        this.noticeAdminService = noticeAdminService;
    }


    /**
     * 통계 페이지
     *
     * @return
     */
    @GetMapping("/statistics")
    public String statistics(Order order,@RequestParam(required=false) String frag, Model model) {
        log.info("statistics");

        // ADMIN 정보
        SecurityUser adminInfo = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getDetails();
        order.setToken(adminInfo.getAdminAccessToken());

        return "/statistics/statement"; }


    /**
     * 통계 리스트 조회
     *
     * @return
     */
    @ResponseBody
    @GetMapping("/getStatisticsList")
    @CnttMethodDescription("통계 페이지 조회")
    public List<Order> getStatisticsList(@RequestParam(required=false) String frag
            ,@RequestParam(value = "startDate", required=false) String startDate
            ,@RequestParam(value = "endDate", required=false) String endDate

    ) {
        log.info("getStatisticsList");

        // 날짜 차이가 31일 이상인 경우 프로세스 종료
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            Date dateStart = format.parse(startDate);
            Date dateEnd = format.parse(endDate);

            long dateDiff = ((dateEnd.getTime() - dateStart.getTime()) / (1000*3600*24));

            if (dateDiff > 31){
                return new ArrayList<>();
            }

        }catch (ParseException ex){
            ex.printStackTrace();
        }

        Order order = new Order();

        // ADMIN 정보
        SecurityUser adminInfo = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getDetails();


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

        order.setToken(adminInfo.getAdminAccessToken());

        List<Order> statisticsList;

        try{
            statisticsList = statisticsAdminService.selectAdminStatistics(order);
        }catch (Exception e){
            e.printStackTrace();
            statisticsList = new ArrayList<>();
        }

        return statisticsList;
    }


    /**
     * 통계 상세보기
     *
     * @return
     */
    @ResponseBody
    @GetMapping("/getStatisticsInfo")
    @CnttMethodDescription("통계 상세 보기")
    public Order getStatisticsInfo(@RequestParam(required=false) String frag,@RequestParam("regOrderId") String regOrderId) {
        log.info("getStatisticsInfo");

        Order order = new Order();

        // ADMIN 정보
        SecurityUser adminInfo = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getDetails();

        order.setToken(adminInfo.getAdminAccessToken());

        order.setRegOrderId(regOrderId);

        Order statisticsInfo = statisticsAdminService.selectAdminStatisticsInfo(order);

        return statisticsInfo;
    }


    // group list 불러오기
    @ResponseBody
    @GetMapping("/getStatisticsGroupList")
    @CnttMethodDescription("그룹 리스트 불러오기")
    public List<Group> getStatisticsGroupList(@RequestParam(required=false) String frag) {
        log.info("getStatisticsGroupList");

        Order order = new Order();

        // ADMIN 정보
        SecurityUser adminInfo = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getDetails();
        // token 부여
        order.setToken(adminInfo.getAdminAccessToken());

        // Group list
        List<Group> groupList = statisticsAdminService.getGroupList(order);

        return groupList;

    }

    // subgroup list 불러오기
    @ResponseBody
    @GetMapping("/getStatisticsSubGroupList")
    @CnttMethodDescription("서브 그룹 리스트 불러오기")
    public List<SubGroup> getStatisticsSubGroupList(@RequestParam(value ="groupId", required=false) String groupId) {
        Order order = new Order();

        // ADMIN 정보
        SecurityUser adminInfo = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getDetails();
        // token 부여
        order.setToken(adminInfo.getAdminAccessToken());
        order.setId(groupId);

        // subGroup list
        List<SubGroup> subGroupList = statisticsAdminService.getSubGroupList(order);

        return subGroupList;
    }


    // store list 불러오기
    @ResponseBody
    @GetMapping("/getStatisticsStoreList")
    @CnttMethodDescription("상점 리스트 불러오기")
    public List<SubGroupStoreRel> getStatisticsStoreList(
            @RequestParam(value ="groupId", required = false) String groupId,
            //@RequestParam(value ="subGroupId", required = false) String subGroupId
            @RequestParam(value ="subGroupName", required = false) String subGroupName
    ) {
        log.info("getStatisticsStoreList");

        SubGroupStoreRel subGroupStoreRel = new SubGroupStoreRel();
        Admin admin = new Admin();

        // ADMIN 정보
        SecurityUser adminInfo = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getDetails();
        // token 부여
        subGroupStoreRel.setToken(adminInfo.getAdminAccessToken());

        // param storeId
        subGroupStoreRel.setGroupId(groupId);
        //subGroupStoreRel.setSubGroupId(subGroupId);
        subGroupStoreRel.setGroupingName(subGroupName);
        subGroupStoreRel.setToken(adminInfo.getAdminAccessToken());

        admin.setToken(adminInfo.getAdminAccessToken());

        // store list
        List<SubGroupStoreRel> storeList = statisticsAdminService.selectSubgroupStoreRels(subGroupStoreRel);
        return storeList;
    }


    // excel 다운로드
//    @ResponseBody
    @GetMapping("/excelDownload")
    public ModelAndView statisticsExcelDownload(HttpServletResponse response,
                                                SearchInfo searchInfo
    ) {
        response.setHeader("Set-Cookie", "fileDownload=true; path=/");
        // ADMIN 정보
        SecurityUser adminInfo = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getDetails();

        System.out.println("SearchInfo => " + searchInfo);

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

        try {
            Date sdfStartDate = formatter.parse(searchInfo.getSDate());
            Date sdfEndDate = formatter.parse(searchInfo.getEDate());
            long diff = sdfEndDate.getTime() - sdfStartDate.getTime();
            long diffDays = diff / (24 * 60 * 60 * 1000);

            // 31일까지만 조회 가능
            if (diffDays > 31){
                return  null;
            }

            searchInfo.setDays(Integer.toString((int) (long) diffDays + 1));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        searchInfo.setToken(adminInfo.getAdminAccessToken());


        ModelAndView modelAndView = new ModelAndView("StatisticsAdminExcelBuilderServiceImpl");

        // List 불러오기                                               Nick
        List<Order> orderStatisticsByAdminList = statisticsAdminService.selectAdminStatisticsExcel(searchInfo);

        modelAndView.addObject("selectAdminStatisticsExcel", orderStatisticsByAdminList);
        modelAndView.addObject("brandCode", adminInfo.getAdminBrandCode());

        return modelAndView;
    }


    /**
     * 2020-04-23 Store 통계 페이지 추가
     * */

    /**
     * 주문별 통계 페이지
     * Main 통계 페이지와는 다소 다름
     * */
    @GetMapping("/statisticsByOrder")
    public String statisticsByOrder(Order order,@RequestParam(required=false) String frag, Model model) {
        log.info("adminStatisticsByOrder");
        String viewPath = "/statistics";
        // ADMIN 정보
        SecurityUser adminInfo = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getDetails();
        order.setToken(adminInfo.getAdminAccessToken());

        if (adminInfo.getAdminBrandCode().trim().equals("1")){
            viewPath = viewPath.concat("/orderStatement_tw_kfc");
        }else{
            viewPath = viewPath.concat("/orderStatement");
        }

        return viewPath;
    }

    @ResponseBody
    @GetMapping("/getStoreStatisticsByOrder")
    @CnttMethodDescription("관리자 주문별 통계 리스트 조회")
    public List<Order> getStoreStatisticsByOrder(@RequestParam(value = "startDate") String startDate
                                                ,@RequestParam(value = "endDate") String endDate
                                                ,@RequestParam(value = "groupID", required = false) String groupId
                                                //,@RequestParam(value = "subGroupID", required = false) String subGroupId
                                                ,@RequestParam(value = "subGroupName", required = false) String subGroupName
                                                ,@RequestParam(value = "storeID", required = false) String storeId) {
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

        // 2020-08-24 검색조건
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

                if (ChronoUnit.MINUTES.between(assignTime, qtAssignTime) < 0){
                    assignTime = qtAssignTime;
                    a.setAssignedDatetime(assignTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S")));
                }

                // KFC와 동일한 조건으로 만들기 위해 사용
                if (arrivedTime.until(returnTime, ChronoUnit.SECONDS) >= 60 && !(assignTime.until(arrivedTime, ChronoUnit.SECONDS) < 0 || assignTime.until(pickupTime, ChronoUnit.SECONDS) < 0 || assignTime.until(returnTime, ChronoUnit.SECONDS) < 0)){
                    return true;
                } else {
                    return false;
                }
            } else {
                return false;
            }
        }).collect(Collectors.toList());//서비스로 빼면 안됨(해당 스트림 필터는 해당 컨트롤러에서만 필요)
    }

    @GetMapping("/excelDownloadByOrder")
    @CnttMethodDescription("관리자 주문별 통계 리스트 엑셀 출력")
    public ModelAndView statisticsByOrderExcelDownload(HttpServletResponse response
                                                      ,@RequestParam(value = "startDate") String startDate
                                                      ,@RequestParam(value = "endDate") String endDate){
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
        ModelAndView modelAndView = new ModelAndView("StatisticsAdminOrderBuilderServiceImpl");
        List<Order> storeOrderListByAdmin = statisticsAdminService.selectStoreStatisticsByOrderForAdmin(order);
        modelAndView.addObject("selectStoreStatisticsByOrderForAdmin", storeOrderListByAdmin);

        return modelAndView;
    }

    ///////////////////////////////////////
    // *   주문별 통계 자료 종료 구간    * //
    ///////////////////////////////////////

    /**
     * 기간별 통계 페이지
     * */
    @GetMapping("/statisticsByDate")
    public String statisticsByDate(Order order, @RequestParam(required = false) String frag, Model model){
        log.info("adminstatisticsByDate");

        String viewPath = "/statistics";
        // ADMIN 정보
        SecurityUser adminInfo = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getDetails();
        order.setToken(adminInfo.getAdminAccessToken());

        if (adminInfo.getAdminBrandCode().trim().equals("1")){
            viewPath = viewPath.concat("/dateStatement_tw_kfc");
        }else{
            viewPath = viewPath.concat("/dateStatement");
        }

        model.addAttribute("regionLocale", regionLocale);


        return viewPath;
    }

    @ResponseBody
    @GetMapping("/getStoreStatisticsByDate")
    @CnttMethodDescription("날짜별 통계 리스트 조회")
    public List<AdminByDate> getStoreStatisticsByDate(@RequestParam("startDate") String startDate
                                                ,@RequestParam("endDate") String endDate
                                                ,@RequestParam(value = "groupID", required = false) String groupId
                                                ,@RequestParam(value = "subGroupName", required = false) String subGroupName
                                                ,@RequestParam(value = "storeID", required = false) String storeId){
        // ADMIN 정보
        SecurityUser adminInfo = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getDetails();
        Order order = new Order();
        order.setCurrentDatetime(startDate);

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

        try {
            Date sdfStartDate = formatter.parse(startDate);
            Date sdfEndDate = formatter.parse(endDate);

            long diff = sdfEndDate.getTime() - sdfStartDate.getTime();
            long diffDays = diff / (24* 60 * 60 * 1000);

            if (diffDays > 31) {
                return new ArrayList<>();
            }

            order.setDays(Integer.toString((int) (long) diffDays + 1));
        }catch (ParseException e){
            e.printStackTrace();
        }

        order.setToken(adminInfo.getAdminAccessToken());

        // 2020-08-24 검색조건
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


        List<AdminByDate> byDateList = statisticsAdminService.selectStoreStatisticsByDateForAdmin(order);

        return byDateList;
    }

    @GetMapping("/excelDownloadByDate")
    @CnttMethodDescription("관리자 기간별 통계 리스트 엑셀 출력")
    public ModelAndView statisticsByDateExcelDownload(HttpServletResponse response,
                                                      @RequestParam(value = "startDate") String startDate,
                                                      @RequestParam(value = "endDate") String endDate){
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
        ModelAndView modelAndView = new ModelAndView("StatisticsAdminByDateBuilderServiceImpl");
        List<AdminByDate> storeOrderListByAdmin = statisticsAdminService.selectStoreStatisticsByDateForAdmin(order);

        modelAndView.addObject("selectStoreStatisticsByDateForAdmin", storeOrderListByAdmin);

        return modelAndView;
    }

    ///////////////////////////////////////
    // *   기간별 통계 자료 종료 구간    * //
    ///////////////////////////////////////


    /**
     * 누적 완료 통계 페이지
     * */
    @GetMapping("/statisticsByInterval")
    public String statisticsByInterval(Order order, @RequestParam(required = false) String frag, Model model){
        log.info("adminstatisticsByDate");

        String viewPath = "/statistics";
        // ADMIN 정보
        SecurityUser adminInfo = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getDetails();
        order.setToken(adminInfo.getAdminAccessToken());

        if (adminInfo.getAdminBrandCode().trim().equals("1")){
            viewPath = viewPath.concat("/interval_tw_kfc");
        }else{
            viewPath = viewPath.concat("/interval");
        }

//        model.addAttribute("regionLocale", regionLocale);


        return viewPath;
    }

    @ResponseBody
    @GetMapping("/getStoreStatisticsByInterval")
    @CnttMethodDescription("구간별 통계 리스트 조회")
    public Map getStoreStatisticsByInterval(@RequestParam(value = "startDate") String startDate,
                                            @RequestParam(value = "endDate") String endDate){
        // ADMIN 정보
        SecurityUser adminInfo = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getDetails();
        Order order = new Order();
        order.setCurrentDatetime(startDate);
        order.setEndDate(endDate);

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

        try {
            Date sdfStartDate = formatter.parse(startDate);
            Date sdfEndDate = formatter.parse(endDate);

            long diff = sdfEndDate.getTime() - sdfStartDate.getTime();
            long diffDays = diff / (24* 60 * 60 * 1000);

            if (diffDays > 31) {
                return new HashMap();
            }

            order.setDays(Integer.toString((int) (long) diffDays + 1));
        }catch (ParseException e){
            e.printStackTrace();
        }

        order.setToken(adminInfo.getAdminAccessToken());


        Interval statisticsInterval = statisticsAdminService.selectAdminStatisticsByInterval(order);
        //구간별 통계 리스트 조회페이지에 추가된 그래프 정보
        List<Map> statisticsMin30Below = statisticsAdminService.selectAdminStatisticsMin30BelowByDate(order);

        Map result = new HashMap();
        result.put("intervalData", statisticsInterval);
        result.put("intervalMin30Below", statisticsMin30Below);

        return result;
    }

    @GetMapping("/excelDownloadByInterval")
    public ModelAndView statisticsByIntervalExcelDownload(HttpServletResponse response, @RequestParam(value = "startDate") String startDate, @RequestParam(value = "endDate") String endDate) {
        response.setHeader("Set-Cookie", "fileDownload=true; path=/");

        // ADMIN 정보
        SecurityUser adminInfo = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getDetails();
        Order order = new Order();
        order.setCurrentDatetime(startDate);
        order.setEndDate(endDate);

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

        try {
            Date sdfStartDate = formatter.parse(startDate);
            Date sdfEndDate = formatter.parse(endDate);

            long diff = sdfEndDate.getTime() - sdfStartDate.getTime();
            long diffDays = diff / (24* 60 * 60 * 1000);

            if (diffDays > 31) {
                return null;
            }

            order.setDays(Integer.toString((int) (long) diffDays + 1));
        }catch (ParseException e){
            e.printStackTrace();
        }

        order.setToken(adminInfo.getAdminAccessToken());

        ModelAndView modelAndView = new ModelAndView("StatisticsAdminByIntervalExcelBuilderServiceImpl");
        Interval storeStatisticsByInterval = statisticsAdminService.selectAdminStatisticsByInterval(order);
        modelAndView.addObject("getAdminStatisticsByIntervalExcel", storeStatisticsByInterval);

        return modelAndView;
    }
    ///////////////////////////////////////
    // *  누적 완료 통계 자료 종료 구간  * //
    ///////////////////////////////////////

}
