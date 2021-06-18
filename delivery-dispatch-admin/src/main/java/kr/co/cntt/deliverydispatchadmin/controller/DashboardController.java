package kr.co.cntt.deliverydispatchadmin.controller;


import kr.co.cntt.core.annotation.CnttMethodDescription;
import kr.co.cntt.core.model.Search;
import kr.co.cntt.core.model.common.Common;
import kr.co.cntt.core.model.dashboard.DashboardInfo;
import kr.co.cntt.core.model.dashboard.SearchInfo;
import kr.co.cntt.core.model.order.Order;
import kr.co.cntt.core.service.admin.DashboardAdminService;
import kr.co.cntt.core.service.admin.StatisticsAdminService;
import kr.co.cntt.core.service.admin.StoreAdminService;
import kr.co.cntt.deliverydispatchadmin.security.SecurityUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Slf4j
@Controller
public class DashboardController {
    @Value("${spring.mvc.locale}")
    private Locale regionLocale;
    /**
     * 객체 주입
     */
    DashboardAdminService dashboardAdminService;

    @Autowired
    public  DashboardController(DashboardAdminService dashboardAdminService){
        this.dashboardAdminService = dashboardAdminService;
    }

    /**
     * 대시보드 페이지
     */
    @GetMapping("/dashboard")
    @CnttMethodDescription("대시보드 메인페이지")
    public String dashboard() {
        return "/dashboard/dashboard";
    }

    /**
     * 대시보드 메인 페이지 통계 추출
     * */
    @ResponseBody
    @PostMapping(value = "/totalStatistsc")
    @CnttMethodDescription("대시보드 메인 통계 자료")
    public List<DashboardInfo> totalStatistsc(SearchInfo searchInfo){
        List<DashboardInfo> resultMap = new ArrayList<>();

        // ADMIN 정보
        SecurityUser adminInfo = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getDetails();
        adminInfo.getAdminBrandCode();

        searchInfo.setToken(adminInfo.getAdminAccessToken());

        // 관리자 토큰까지 저장 후, 데이터를 Copy한다.
        SearchInfo compareSearchInfo = searchInfo.deapCopy();
        // 데이터일자를 체크하여,

        /**
         * Common 통계
         * */
        // D30
        DashboardInfo currentD30 = dashboardAdminService.selectD30Detail(searchInfo);


        // TPLH
        DashboardInfo currentTPLH = dashboardAdminService.selectTPLHDetail(searchInfo);

        // QT
        DashboardInfo currentQT = dashboardAdminService.selectQTDetail(searchInfo);

        // TC
        DashboardInfo currentTC = dashboardAdminService.selectTCDetail(searchInfo);

        // Order Stack Rate
        DashboardInfo currentOSR = dashboardAdminService.selectOrderStackRateDetail(searchInfo);

        /**
         * 브랜드별 통계
         * */
        
        // D7
        DashboardInfo currentD7 = null;
        if (adminInfo.getAdminBrandCode().equals("1")){
            // KFC만 작동하도록 설정
            currentD7 = dashboardAdminService.selectD7Detail(searchInfo);
        }



        /**
         * 대시보드 메인 페이지에 대한 내용을 리턴한다.
         * */
        /// Return할 데이터를 추가한다.
        if (currentD30 != null){
            resultMap.add(currentD30);
        }

        if (currentD7 != null){
            resultMap.add(currentD7);
        }

        if (currentTPLH != null){
            resultMap.add(currentTPLH);
        }

        if (currentQT != null){
            resultMap.add(currentQT);
        }

        if (currentTC != null){
            resultMap.add(currentTC);
        }

        if (currentOSR != null){
            resultMap.add(currentOSR);
        }

        return resultMap;
    }

    /**
     * 대시 보드 상세 페이지
     * */
    @GetMapping("/dashboardDetail")
    @CnttMethodDescription("대시보드 상세페이지")
    public String dashboardDetail(){

        return "/dashboard/dashboardDetail";
    }
}
