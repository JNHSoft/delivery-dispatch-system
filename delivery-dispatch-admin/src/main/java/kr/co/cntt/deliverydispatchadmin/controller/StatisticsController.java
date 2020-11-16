package kr.co.cntt.deliverydispatchadmin.controller;

import com.google.gson.Gson;
import kr.co.cntt.core.annotation.CnttMethodDescription;
import kr.co.cntt.core.model.admin.Admin;
import kr.co.cntt.core.model.group.Group;
import kr.co.cntt.core.model.group.SubGroup;
import kr.co.cntt.core.model.group.SubGroupStoreRel;
import kr.co.cntt.core.model.notice.Notice;
import kr.co.cntt.core.model.order.Order;
import kr.co.cntt.core.model.rider.Rider;
import kr.co.cntt.core.model.statistic.AdminByDate;
import kr.co.cntt.core.model.statistic.ByDate;
import kr.co.cntt.core.model.statistic.Interval;
import kr.co.cntt.core.model.statistic.IntervalAtTWKFC;
import kr.co.cntt.core.model.store.Store;
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
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.terracotta.statistics.Statistic;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
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
            SimpleDateFormat format = new SimpleDateFormat("yyyy-mm-dd");
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

//        log.info("===============> adminInfo.getAdminAccessToken()    : {}", adminInfo.getAdminAccessToken());


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


//        log.info("@@@@@@@@@@@@@@@@@@@@@@"+statisticsList);

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

//        log.info("===============> adminInfo.getAdminAccessToken()    : {}", adminInfo.getAdminAccessToken());

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

//        log.info("===============> adminInfo.getAdminAccessToken()    : {}", adminInfo.getAdminAccessToken());

        // Group list
        List<Group> groupList = statisticsAdminService.getGroupList(order);

        // 리스트 확인
        /*if (groupList.size() == 0) {
            log.info("0000000000000000000000");
        } else {
            for (Group s : groupList) {
                log.info("@@" + s.getName());
            }
        }*/
        return groupList;

    }

    // subgroup list 불러오기
    @ResponseBody
    @GetMapping("/getStatisticsSubGroupList")
    @CnttMethodDescription("서브 그룹 리스트 불러오기")
    public List<SubGroup> getStatisticsSubGroupList(@RequestParam(value ="groupId", required=false) String groupId) {
        log.info("getStatisticsSubGroupList");

        Order order = new Order();

        // ADMIN 정보
        SecurityUser adminInfo = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getDetails();
        // token 부여
        order.setToken(adminInfo.getAdminAccessToken());

//        log.info("===============> adminInfo.getAdminAccessToken()    : {}", adminInfo.getAdminAccessToken());
        // param storeId
        order.setId(groupId);

        // subGroup list
        List<SubGroup> subGroupList = statisticsAdminService.getSubGroupList(order);

        // 리스트 확인
        /*if (subGroupList.size() == 0) {
            log.info("0000000000000000000000");
        } else {
            for (SubGroup s : subGroupList) {
                log.info("@@" + s.getName());
            }
        }*/
        return subGroupList;
    }


    // store list 불러오기
    @ResponseBody
    @GetMapping("/getStatisticsStoreList")
    @CnttMethodDescription("상점 리스트 불러오기")
    public List<SubGroupStoreRel> getStatisticsStoreList(
            @RequestParam(value ="groupId", required = false) String groupId,
            @RequestParam(value ="subGroupId", required = false) String subGroupId
    ) {
        log.info("getStatisticsStoreList");

//        log.info("=>>>>>>>>>>>>>>>="+groupId);
//        log.info("=>>>>>>>>>>>>="+subGroupId);
        SubGroupStoreRel subGroupStoreRel = new SubGroupStoreRel();
        Admin admin = new Admin();

        // ADMIN 정보
        SecurityUser adminInfo = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getDetails();
        // token 부여
        subGroupStoreRel.setToken(adminInfo.getAdminAccessToken());

//        log.info("===============> adminInfo.getAdminAccessToken()    : {}", adminInfo.getAdminAccessToken());

        // param storeId
        subGroupStoreRel.setGroupId(groupId);
        subGroupStoreRel.setSubGroupId(subGroupId);
        subGroupStoreRel.setToken(adminInfo.getAdminAccessToken());

        admin.setToken(adminInfo.getAdminAccessToken());
//        log.info("!!!!!!!!!!!!!!!!!!!!!!!!!!"+subGroupStoreRel);

        // store list
        List<SubGroupStoreRel> storeList = statisticsAdminService.selectSubgroupStoreRels(subGroupStoreRel);
//        log.info("@@@@@@@@@@@!@#!@#!@#!@#!@#!@#"+storeList);


//        // subGroup list
//        List<SubGroup> subGroupList = groupAdminService.selectSubGroupsList(order);

//        model.addAttribute("subGroupList",subGroupList);
//        model.addAttribute("storeList",storeList);


        return storeList;
    }


    // excel 다운로드
//    @ResponseBody
    @GetMapping("/excelDownload")
    public ModelAndView statisticsExcelDownload(HttpServletResponse response,
                                                @RequestParam(value = "startDate", required = false, defaultValue = "") String startDate,
                                                @RequestParam(value = "endDate", required = false, defaultValue = "") String endDate

    ) {
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

            // 31일까지만 조회 가능
            if (diffDays > 31){
                return  null;
            }

            order.setDays(Integer.toString((int) (long) diffDays + 1));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        order.setToken(adminInfo.getAdminAccessToken());


        ModelAndView modelAndView = new ModelAndView("StatisticsAdminExcelBuilderServiceImpl");

        // List 불러오기                                               Nick
        List<Order> orderStatisticsByAdminList = statisticsAdminService.selectAdminStatisticsExcel(order);

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
                                                ,@RequestParam(value = "subGroupID", required = false) String subGroupId
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

        if (subGroupId.trim() != "" && !subGroupId.toLowerCase().equals("reset")){
            order.setSubGroup(new SubGroup());
            order.getSubGroup().setId(subGroupId);
        }

        if (storeId.trim() != "" && !storeId.toLowerCase().equals("reset")){
            order.setStoreId(storeId);
        }
        
        List<Order> statistByOrder = statisticsAdminService.selectStoreStatisticsByOrderForAdmin(order);

        return statistByOrder.stream().filter(a -> {
            if (a.getAssignedDatetime() != null && a.getPickedUpDatetime() != null && a.getCompletedDatetime() != null && a.getReturnDatetime() != null) {
                if (a.getReservationStatus().equals("1")) {
                    LocalDateTime reserveToCreated = LocalDateTime.parse((a.getReservationDatetime()).replace(" ", "T"));
                    a.setCreatedDatetime(reserveToCreated.minusMinutes(30).format(DateTimeFormatter.ofPattern("YYYY-MM-dd HH:mm:ss.S")));
                }

                LocalDateTime pickupTime = LocalDateTime.parse((a.getPickedUpDatetime()).replace(" ", "T"));
                LocalDateTime completeTime = LocalDateTime.parse((a.getCompletedDatetime()).replace(" ", "T"));
                LocalDateTime returnTime = LocalDateTime.parse((a.getReturnDatetime()).replace(" ", "T"));
                LocalDateTime createdTime = LocalDateTime.parse((a.getCreatedDatetime()).replace(" ", "T"));

                if (completeTime.until(returnTime, ChronoUnit.SECONDS) >= 60 && !(createdTime.until(completeTime, ChronoUnit.SECONDS) < 0 || createdTime.until(pickupTime, ChronoUnit.SECONDS) < 0 || createdTime.until(returnTime, ChronoUnit.SECONDS) < 0)) {
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
                                                ,@RequestParam(value = "subGroupID", required = false) String subGroupId
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

        if (subGroupId.trim() != "" && !subGroupId.toLowerCase().equals("reset")){
            order.setSubGroup(new SubGroup());
            order.getSubGroup().setId(subGroupId);
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
