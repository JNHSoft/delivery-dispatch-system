package kr.co.cntt.deliverydispatchadmin.controller;


import kr.co.cntt.core.annotation.CnttMethodDescription;
import kr.co.cntt.core.model.common.SearchInfo;
import kr.co.cntt.core.model.dashboard.ChartInfo;
import kr.co.cntt.core.model.dashboard.DashboardInfo;
import kr.co.cntt.core.model.dashboard.RankInfo;
import kr.co.cntt.core.model.dashboard.TimeSectionInfo;
import kr.co.cntt.core.service.admin.DashboardAdminService;
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
        searchInfo.setBrandCode(adminInfo.getAdminBrandCode());

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


        } /*else if (searchInfo.getDays().equals("1")){
            log.debug("하루 데이터 조회");
            startCal.add(Calendar.DAY_OF_MONTH, -1);
            compareSearchInfo.setSDate(formatter.format(startCal.getTime()));
            compareSearchInfo.setEDate(formatter.format(startCal.getTime()));
        }*/ else{
            log.debug("조건이 없는 데이터 조회");
            int sevenValue = Math.floorDiv(Integer.parseInt(searchInfo.getDays()), 7) + 1;
            int modValue = Math.floorMod(Integer.parseInt(searchInfo.getDays()), 7);

            if (modValue == 0){
                sevenValue--;
            }

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
        if (!adminInfo.getAdminBrandCode().equals("1")){
            // PizzaHut인 경우
            currentDetail.removeIf(x -> x.getDashBoardType().equals("D7"));
        }else{
            // 피자헛 외적인 경우
            currentDetail.removeIf(x -> x.getDashBoardType().equals("D14"));
            currentDetail.removeIf(x -> x.getDashBoardType().equals("D16"));
        }

        /**
         * 메인 데이터와 이전 데이터의 비교 
         * */
        currentDetail.forEach(x -> {
            DashboardInfo info = compareDetail.stream().filter(y -> y.getDashBoardType().equals(x.getDashBoardType())).findFirst().get();

            float fVariation = ((x.getMainValue() / info.getMainValue()) - 1) * 100;
            x.setVariation(fVariation);
        });

        return currentDetail;
    }

    @GetMapping( "/dashboardDetail")
    @CnttMethodDescription("대시보드 상세페이지")
    public String dashboardDetail(Model model,
                                  @RequestParam("dashBoardType") String type){

        model.addAttribute("dashboardType", type);

        return "/dashboard/dashboardDetail";
    }
    
    /**
     * 대시 보드 상세 페이지 세부 내용 출력
     * */
    @ResponseBody
    @PostMapping("/dashboardDetail")
    @CnttMethodDescription("대시 보드 상세 내용 호출")
    public Map<String, Object> dashboardDetailInfo(Model model,
                                         @RequestParam("dashBoardType") String type,
                                         SearchInfo searchInfo){
        Map<String, Object> resultMapt = new HashMap<>();
        ChartInfo info = new ChartInfo();
        List<RankInfo> storeRank = new ArrayList<>();

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
                resultMapt.put("status", "Failed");
                log.debug("조회일이 31일을 초과했습니다.");
                return resultMapt;
            }

            searchInfo.setDays((Integer.toString(((int) (long) diffDays + 1))));
        } catch (ParseException ex) {
            ex.printStackTrace();
            resultMapt.put("status", "Failed");
            log.debug("조회일이 산정 중 오류 발생");
            return resultMapt;
        }

        // ADMIN 정보
        SecurityUser adminInfo = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getDetails();

        searchInfo.setToken(adminInfo.getAdminAccessToken());
        searchInfo.setRole("ROLE_ADMIN");
        searchInfo.setBrandCode(adminInfo.getAdminBrandCode());
        // platform 대시보드에서는 대시보드 종류로 이용한다.
        searchInfo.setPlatform(type);

        log.debug("type에 맞게 데이터를 조회합니다." + type);
        switch (type){
            case "D30":
                info = dashboardAdminService.selectD30Detail(searchInfo);
                storeRank = dashboardAdminService.selectD30Rank(searchInfo);
                break;
            case "D30T":
                info = dashboardAdminService.selectD30TDetail(searchInfo);
                storeRank = dashboardAdminService.selectD30TRank(searchInfo);
                break;
            case "D7":
                info = dashboardAdminService.selectD7Detail(searchInfo);
                storeRank = dashboardAdminService.selectD7Rank(searchInfo);
                break;
            case "D14":
                info = dashboardAdminService.selectD14Detail(searchInfo);
                storeRank = dashboardAdminService.selectD14Rank(searchInfo);
                break;
            case "D16":
                info = dashboardAdminService.selectD16Detail(searchInfo);
                storeRank = dashboardAdminService.selectD16Rank(searchInfo);
                break;
            case "TPLH":
                info = dashboardAdminService.selectTPLHDetail(searchInfo);
                storeRank = dashboardAdminService.selectTPLHRank(searchInfo);
                break;
            case "QT":
                info = dashboardAdminService.selectQTDetail(searchInfo);
                storeRank = dashboardAdminService.selectQTRank(searchInfo);
                break;
            case "TC":
                info = dashboardAdminService.selectTCDetail(searchInfo);
                storeRank = dashboardAdminService.selectTCRank(searchInfo);
                break;
            case "Service Point":
                info = dashboardAdminService.selectRiderServicePointDetail(searchInfo);
                storeRank = dashboardAdminService.selectRiderServicePointRank(searchInfo);
                break;
            case "Speed Point":
                info = dashboardAdminService.selectRiderSpeedPointDetail(searchInfo);
                storeRank = dashboardAdminService.selectRiderSpeedPointRank(searchInfo);
                break;
            default:
                break;
        }

        log.debug("데이터 조회가 완료 되었습니다.");

        resultMapt.put("status", "OK");
        resultMapt.put("chartInfo", info);
        resultMapt.put("rankInfo", storeRank);
        return resultMapt;
    }

    @PostMapping("/dashboardDetailExcel")
    public ModelAndView dashboardDetailExcelDownload(HttpServletResponse response,
                                                     @RequestParam("dashBoardType") String type,
                                                     SearchInfo searchInfo){
        response.setHeader("Set-Cookie", "fileDownload=true; path=/");
        List<RankInfo> storeRank = new ArrayList<>();

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
                return null;
            }

            searchInfo.setDays((Integer.toString(((int) (long) diffDays + 1))));
        } catch (ParseException ex) {
            ex.printStackTrace();
            return null;
        }

        // ADMIN 정보
        SecurityUser adminInfo = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getDetails();

        searchInfo.setToken(adminInfo.getAdminAccessToken());
        searchInfo.setRole("ROLE_ADMIN");
        searchInfo.setBrandCode(adminInfo.getAdminBrandCode());
        // platform 대시보드에서는 대시보드 종류로 이용한다.
        searchInfo.setPlatform(type);


        switch (type){
            case "D30":
                storeRank = dashboardAdminService.selectD30Rank(searchInfo);
                break;
            case "D30T":
                storeRank = dashboardAdminService.selectD30TRank(searchInfo);
                break;
            case "D7":
                storeRank = dashboardAdminService.selectD7Rank(searchInfo);
                break;
            case "D14":
                storeRank = dashboardAdminService.selectD14Rank(searchInfo);
                break;
            case "D16":
                storeRank = dashboardAdminService.selectD16Rank(searchInfo);
                break;
            case "TPLH":
                storeRank = dashboardAdminService.selectTPLHRank(searchInfo);
                break;
            case "QT":
                storeRank = dashboardAdminService.selectQTRank(searchInfo);
                break;
            case "TC":
                storeRank = dashboardAdminService.selectTCRank(searchInfo);
                break;
            case "Service Point":
                storeRank = dashboardAdminService.selectRiderServicePointRank(searchInfo);
                break;
            case "Speed Point":
                storeRank = dashboardAdminService.selectRiderSpeedPointRank(searchInfo);
                break;
            default:
                break;
        }

        // 데이터가 없으면 return
        if (storeRank.isEmpty()){
            return null;
        }

        ModelAndView modelAndView = new ModelAndView("DashBoardDetailExcelBuilderServiceImpl");
        modelAndView.addObject("getDashboardDetailInfo", storeRank);
        modelAndView.addObject("typeName", type);

        return modelAndView;
    }

    @PostMapping("/downloadTimeSectionExcel")
    public ModelAndView timeSectionExcelDownload(HttpServletResponse response,
                                                 SearchInfo searchInfo){
        response.setHeader("Set-Cookie", "fileDownload=true; path=/");

        List<TimeSectionInfo> timeSecionInfos = new ArrayList<>();

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
                return null;
            }

            searchInfo.setDays((Integer.toString(((int) (long) diffDays + 1))));
        } catch (ParseException ex) {
            ex.printStackTrace();
            return null;
        }

        // ADMIN 정보
        SecurityUser adminInfo = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getDetails();

        searchInfo.setToken(adminInfo.getAdminAccessToken());
        searchInfo.setRole("ROLE_ADMIN");
        searchInfo.setBrandCode(adminInfo.getAdminBrandCode());

        timeSecionInfos = dashboardAdminService.selectTimeSection(searchInfo);

        // 데이터가 없으면 return
        if (timeSecionInfos.isEmpty()){
            return null;
        }

        ModelAndView modelAndView = new ModelAndView("DashBoardTimeSectionExcelBuilderServiceImpl");
        modelAndView.addObject("getTimeSectionInfo", timeSecionInfos);

        return modelAndView;
    }
}
