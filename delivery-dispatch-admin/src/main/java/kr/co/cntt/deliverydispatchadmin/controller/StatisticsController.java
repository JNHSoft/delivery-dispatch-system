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
import kr.co.cntt.core.model.store.Store;
import kr.co.cntt.core.service.admin.GroupAdminService;
import kr.co.cntt.core.service.admin.NoticeAdminService;
import kr.co.cntt.core.service.admin.StatisticsAdminService;
import kr.co.cntt.core.service.admin.StoreAdminService;
import kr.co.cntt.deliverydispatchadmin.security.SecurityUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.terracotta.statistics.Statistic;

import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
//        log.info("===============> adminInfo.getAdminAccessToken()    : {}", adminInfo.getAdminAccessToken());

        order.setToken(adminInfo.getAdminAccessToken());

//        List<Order> statisticsList = statisticsAdminService.selectAdminStatistics(order);

//        model.addAttribute("statisticsList", statisticsList);
//        model.addAttribute("jsonList", new Gson().toJson(statisticsList));

//        log.info("json : {}", new Gson().toJson(statisticsList));


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

        List<Order> statisticsList = statisticsAdminService.selectAdminStatistics(order);


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
    public Order getStatisticsInfo(@RequestParam(required=false) String frag,@RequestParam("regOrderId") String regOrderId

    ) {
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
    @ResponseBody
    @GetMapping("/excelDownload")
    public ModelAndView statisticsExcelDownload(ModelMap model,
                                                HttpServletRequest request,
                                                @RequestParam(value = "startDate", required = false, defaultValue = "") String startDate,
                                                @RequestParam(value = "endDate", required = false, defaultValue = "") String endDate

    ) {
        log.info("statisticsExcelDownload");
        log.info(startDate);
        log.info(endDate);

//        if (StringUtils.isBlank(startDate)) startDate = StringHelper.getDate("yyyyMMdd", -1);
//        if (StringUtils.isBlank(endDate)) endDate = StringHelper.getDate("yyyyMMdd", -1);
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

            order.setDays(Integer.toString((int) (long) diffDays + 1));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        order.setToken(adminInfo.getAdminAccessToken());


        ModelAndView modelAndView = new ModelAndView("StatisticsAdminExcelBuilderServiceImpl");

        // List 불러오기                                               Nick
        List<Order> orderStatisticsByAdminList = statisticsAdminService.selectAdminStatisticsExcel(order);

        modelAndView.addObject("selectAdminStatisticsExcel", orderStatisticsByAdminList);


        return modelAndView;
    }

}
