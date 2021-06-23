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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
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

        // 일자를 막도록 합시다.
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date sdfStartDate;
        Date sdfEndDate;
        try {
            sdfStartDate = formatter.parse(searchInfo.getSDate());
            sdfEndDate = formatter.parse(searchInfo.getEDate());

            long diff = sdfEndDate.getTime() - sdfStartDate.getTime();
            long diffDays = diff / (24 * 60 * 60 * 1000);

            if (diffDays > 31) {
                return new ArrayList<>();
            }

            searchInfo.setDays((Integer.toString(((int) (long) diffDays + 1))));
        } catch (ParseException ex) {
            ex.printStackTrace();
            return new ArrayList<>();
        }

        // ADMIN 정보
        SecurityUser adminInfo = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getDetails();

        searchInfo.setToken(adminInfo.getAdminAccessToken());
        searchInfo.setRole("ROLE_ADMIN");

        // 관리자 토큰까지 저장 후, 데이터를 Copy한다.
        SearchInfo compareSearchInfo = searchInfo.deapCopy();

        // 날짜를 체크하여, 비교할 날짜를 추출한다.
        Calendar startCal = Calendar.getInstance();
        startCal.setTime(sdfStartDate);

        Calendar endCal = Calendar.getInstance();
        endCal.setTime(sdfEndDate);

        if (startCal.get(Calendar.MONTH) == endCal.get(Calendar.MONTH) && startCal.get(Calendar.DAY_OF_MONTH) == 1 && endCal.get(Calendar.DAY_OF_MONTH) == endCal.getActualMaximum(Calendar.DAY_OF_MONTH)){
            log.debug("월초/월말 데이터 조회");
            startCal.add(Calendar.MONTH, -1);

            Calendar firstDate = Calendar.getInstance();
            Calendar lastDate = Calendar.getInstance();

            firstDate.set(startCal.get(Calendar.YEAR), startCal.get(Calendar.MONTH), startCal.getActualMinimum(Calendar.DAY_OF_MONTH));
            lastDate.set(startCal.get(Calendar.YEAR), startCal.get(Calendar.MONTH), startCal.getActualMaximum(Calendar.DAY_OF_MONTH));

            compareSearchInfo.setSDate(formatter.format(firstDate.getTime()));
            compareSearchInfo.setEDate(formatter.format(lastDate.getTime()));


        }else if (searchInfo.getDays().equals("1")){
            log.debug("하루 데이터 조회");
            startCal.add(Calendar.DAY_OF_MONTH, -1);
            compareSearchInfo.setSDate(formatter.format(startCal.getTime()));
            compareSearchInfo.setEDate(formatter.format(startCal.getTime()));
        }else{
            log.debug("조건이 없는 데이터 조회");
            int sevenValue = Math.floorDiv(Integer.parseInt(searchInfo.getDays()), 7) + 1;
            int modValue = Math.floorMod(Integer.parseInt(searchInfo.getDays()), 7);

            if (modValue == 0){
                sevenValue--;
            }

            System.out.println("몇 주에 대한 데이터인가요? => " + sevenValue);

            startCal.add(Calendar.DAY_OF_MONTH, -1 * (sevenValue * 7));
            endCal.add(Calendar.DAY_OF_MONTH, -1 * (sevenValue * 7));

            compareSearchInfo.setSDate(formatter.format(startCal.getTime()));
            compareSearchInfo.setEDate(formatter.format(endCal.getTime()));

        }

        /**
         * 메인 데이터를 가져온다.
         * */
        List<DashboardInfo> currentDetail = dashboardAdminService.selectAllDetail(searchInfo);

        /**
         * 비교할 이전 데이터를 가져온다.
         * */
        List<DashboardInfo> compareDetail = dashboardAdminService.selectAllDetail(compareSearchInfo);



        /**
         * 브랜드별로 제외할 데이터는 제외한다.
         * */
        if (adminInfo.getAdminBrandCode().equals(0)){
            // PizzaHut인 경우
            currentDetail.removeIf(x -> x.getDashBoardType().equals("D7"));
        }

        /**
         * 메인 데이터와 이전 데이터의 비교 
         * */
        currentDetail.forEach(x -> {
            DashboardInfo info = compareDetail.stream().filter(y -> y.getDashBoardType().equals(x.getDashBoardType())).findFirst().get();

            float fVariation = ((x.getMainValue() / info.getMainValue()) - 1) * 100;
            x.setVariation(fVariation);
        });


        System.out.println(currentDetail);
        System.out.println(compareDetail);

        System.out.println(searchInfo);
        System.out.println(compareSearchInfo);

        return currentDetail;
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
