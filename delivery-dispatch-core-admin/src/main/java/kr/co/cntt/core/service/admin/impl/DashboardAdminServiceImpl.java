package kr.co.cntt.core.service.admin.impl;

import kr.co.cntt.core.mapper.DashboardMapper;
import kr.co.cntt.core.model.common.SearchInfo;
import kr.co.cntt.core.model.dashboard.ChartInfo;
import kr.co.cntt.core.model.dashboard.DashboardInfo;
import kr.co.cntt.core.model.dashboard.RankInfo;
import kr.co.cntt.core.model.dashboard.TimeSectionInfo;
import kr.co.cntt.core.service.admin.DashboardAdminService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.*;

@Slf4j
@Service("DashboardAdminService")
public class DashboardAdminServiceImpl implements DashboardAdminService {


    DashboardMapper dashboardMapper;

    @Autowired
    public DashboardAdminServiceImpl(DashboardMapper dashboardMapper){
        this.dashboardMapper = dashboardMapper;
    }


    @Override
    public List<DashboardInfo> selectAllDetail(SearchInfo search) {

        List<DashboardInfo> resultList = new ArrayList<>();
        Map resultMap = dashboardMapper.selectAllDetail(search);

        // 파싱할 데이터가 NULL 이면 오류가 발생하므로, 예외처리를 진행한다.
        if (resultMap != null){
            // D30 등록
            DashboardInfo infoD30 = new DashboardInfo();
            infoD30.setDashBoardType("D30");
            infoD30.setUnit("%");
            infoD30.setMainValueType("percent");

            // D30 메인 Value 입력
            if (resultMap.containsKey("avgD30")){
                infoD30.setMainValue(Float.parseFloat(changeValueType(Float.class, resultMap.get("avgD30").toString())));
            }else{
                infoD30.setMainValue(0f);
            }
            
//            // D30 평균 Value 입력
//            if (resultMap.containsKey("detailArrived")){
//                infoD30.setAvgValue(Long.parseLong(changeValueType(Long.class, resultMap.get("detailArrived").toString())));
//            }else{
//                infoD30.setAvgValue(0l);
//            }
            
            // D30 평균 시간 카드로 변경할 것
            // TPLH 등록
            DashboardInfo infoD30T = new DashboardInfo();
            infoD30T.setDashBoardType("D30T");
            infoD30T.setUnit("");
            infoD30T.setMainValueType("times");

            if (resultMap.containsKey("detailArrived")){
                infoD30T.setMainValue(Float.parseFloat(changeValueType(Float.class, resultMap.get("detailArrived").toString())));
            }else{
                infoD30T.setMainValue(0f);
            }


            // D7 등록
            DashboardInfo infoD7 = new DashboardInfo();
            infoD7.setDashBoardType("D7");
            infoD7.setUnit("%");
            infoD7.setMainValueType("percent");

            // D7의 메인 Value 입력
            if (resultMap.containsKey("avgD7")){
                infoD7.setMainValue(Float.parseFloat(changeValueType(Float.class, resultMap.get("avgD7").toString())));
            }else{
                infoD7.setMainValue(0f);
            }

            // D7 의 평균 Value
            if (resultMap.containsKey("detailPickedUp")){
                infoD7.setAvgValue(Long.parseLong(changeValueType(Long.class, resultMap.get("detailPickedUp").toString())));
            }else{
                infoD7.setAvgValue(0l);
            }

            // D14 등록
            DashboardInfo infoD14 = new DashboardInfo();
            infoD14.setDashBoardType("D14");
            infoD14.setUnit("%");
            infoD14.setMainValueType("percent");

            // D14의 메인 Value 입력
            if (resultMap.containsKey("avgD14")){
                infoD14.setMainValue(Float.parseFloat(changeValueType(Float.class, resultMap.get("avgD14").toString())));
            }else{
                infoD14.setMainValue(0f);
            }

            // D14 의 평균 Value
            if (resultMap.containsKey("detailPickedUp")){
                infoD14.setAvgValue(Long.parseLong(changeValueType(Long.class, resultMap.get("detailPickedUp").toString())));
            }else{
                infoD14.setAvgValue(0l);
            }

            // D16 등록 (16분 20초)
            DashboardInfo infoD16 = new DashboardInfo();
            infoD16.setDashBoardType("D16");
            infoD16.setUnit("%");
            infoD16.setMainValueType("percent");

            // D16의 메인 Value 입력
            if (resultMap.containsKey("avgD16")){
                infoD16.setMainValue(Float.parseFloat(changeValueType(Float.class, resultMap.get("avgD16").toString())));
            }else{
                infoD16.setMainValue(0f);
            }

            // D16 의 평균 Value
            if (resultMap.containsKey("detailPickedUp")){
                infoD16.setAvgValue(Long.parseLong(changeValueType(Long.class, resultMap.get("detailPickedUp").toString())));
            }else{
                infoD16.setAvgValue(0l);
            }

            // TPLH 등록
            DashboardInfo infoTPLH = new DashboardInfo();
            infoTPLH.setDashBoardType("TPLH");
            infoTPLH.setUnit("");
            infoTPLH.setMainValueType("number");

            if (resultMap.containsKey("avgTPLH")){
                infoTPLH.setMainValue(Float.parseFloat(changeValueType(Float.class, resultMap.get("avgTPLH").toString())));
            }else{
                infoTPLH.setMainValue(0f);
            }

            // QT 등록
            DashboardInfo infoQT = new DashboardInfo();
            infoQT.setDashBoardType("QT");
            infoQT.setUnit("mins");
            infoQT.setMainValueType("number");

            if (resultMap.containsKey("avgQT")){
                infoQT.setMainValue(Float.parseFloat(changeValueType(Float.class, resultMap.get("avgQT").toString())));
            }else{
                infoQT.setMainValue(0f);
            }

            // TC 등록
            DashboardInfo infoTC = new DashboardInfo();
            infoTC.setDashBoardType("TC");
            infoTC.setUnit("");
            infoTC.setMainValueType("number");

            if (resultMap.containsKey("sumTC")){
                infoTC.setMainValue(Float.parseFloat(changeValueType(Float.class, resultMap.get("sumTC").toString())));
            }else{
                infoTC.setMainValue(0f);
            }

            // 2022-01-23 배달원의 서비스 만족도
            DashboardInfo infoService = new DashboardInfo();
            infoService.setDashBoardType("Service Point");
            infoService.setUnit("");
            infoService.setMainValueType("number");

            if (resultMap.containsKey("avgRiderService")) {
                infoService.setMainValue(Float.parseFloat(changeValueType(Float.class, resultMap.get("avgRiderService").toString())));
            } else {
                infoService.setMainValue(0f);
            }

            // 2022-01-23 배달 속도 만족도
            DashboardInfo infoSpeed = new DashboardInfo();
            infoSpeed.setDashBoardType("Speed Point");
            infoSpeed.setUnit("");
            infoSpeed.setMainValueType("number");

            if (resultMap.containsKey("avgRiderSpeed")) {
                infoSpeed.setMainValue(Float.parseFloat(changeValueType(Float.class, resultMap.get("avgRiderSpeed").toString())));
            } else {
                infoSpeed.setMainValue(0f);
            }

            // 정렬에 맞게 데이터를 넣어준다.
            resultList.add(infoD30);
            resultList.add(infoD30T);
            resultList.add(infoD7);
            resultList.add(infoD14);
            resultList.add(infoD16);
            resultList.add(infoTPLH);
            resultList.add(infoQT);
            resultList.add(infoTC);
            resultList.add(infoService);
            resultList.add(infoSpeed);
        }

        return resultList;
    }

    @Override
    public ChartInfo selectD30Detail(SearchInfo search) {
        List<DashboardInfo> detail = dashboardMapper.selectD30Detail(search);

        if (detail == null || detail.isEmpty()){
            return null;
        }

        return makeBarChartInfo(detail, Integer.parseInt(search.getDays()), 5);
    }

    @Override
    public ChartInfo selectD30TDetail(SearchInfo search) {
        List<DashboardInfo> detail = dashboardMapper.selectD30TDetail(search);

        if (detail == null || detail.isEmpty()){
            return null;
        }

        return makeLineChartInfo(detail, Integer.parseInt(search.getDays()), 5);
    }

    @Override
    public ChartInfo selectTPLHDetail(SearchInfo search) {
        List<DashboardInfo> detail = dashboardMapper.selectTPLHDetail(search);

        if (detail == null || detail.isEmpty()){
            return null;
        }

        return makeLineChartInfo(detail, Integer.parseInt(search.getDays()), 5);
    }

    @Override
    public ChartInfo selectQTDetail(SearchInfo search) {
        List<DashboardInfo> detail = dashboardMapper.selectQTDetail(search);

        if (detail == null || detail.isEmpty()){
            return null;
        }

        return makeLineChartInfo(detail, Integer.parseInt(search.getDays()), 5);
    }

    @Override
    public ChartInfo selectTCDetail(SearchInfo search) {
        List<DashboardInfo> detail = dashboardMapper.selectTCDetail(search);

        if (detail == null || detail.isEmpty()){
            return null;
        }

        return makeLineChartInfo(detail, Integer.parseInt(search.getDays()), 5);
    }

    @Override
    public ChartInfo selectOrderStackRateDetail(SearchInfo search) {
        List<DashboardInfo> detail = dashboardMapper.selectOrderStackRateDetail(search);

        if (detail == null || detail.isEmpty()){
            return null;
        }

        return makeLineChartInfo(detail, Integer.parseInt(search.getDays()), 5);
    }

    @Override
    public ChartInfo selectD7Detail(SearchInfo search) {
        List<DashboardInfo> detail = dashboardMapper.selectD7Detail(search);

        if (detail == null || detail.isEmpty()){
            return null;
        }

        return makeBarChartInfo(detail, Integer.parseInt(search.getDays()), 5);
    }

    @Override
    public ChartInfo selectD14Detail(SearchInfo search) {
        List<DashboardInfo> detail = dashboardMapper.selectD14Detail(search);

        if (detail == null || detail.isEmpty()){
            return null;
        }

        return makeBarChartInfo(detail, Integer.parseInt(search.getDays()), 5);
    }

    @Override
    public ChartInfo selectD16Detail(SearchInfo search) {
        List<DashboardInfo> detail = dashboardMapper.selectD16Detail(search);

        if (detail == null || detail.isEmpty()){
            return null;
        }

        return makeBarChartInfo(detail, Integer.parseInt(search.getDays()), 5);
    }

    @Override
    public ChartInfo selectRiderServicePointDetail(SearchInfo search) {
        List<DashboardInfo> detail = dashboardMapper.selectRiderServicePointDetail(search);

        if (detail == null || detail.isEmpty()){
            return null;
        }

        return makeDoughnutChartInfo(detail, Integer.parseInt(search.getDays()), 5);
    }

    @Override
    public ChartInfo selectRiderSpeedPointDetail(SearchInfo search) {
        List<DashboardInfo> detail = dashboardMapper.selectRiderSpeedPointDetail(search);

        if (detail == null || detail.isEmpty()){
            return null;
        }

        return makeDoughnutChartInfo(detail, Integer.parseInt(search.getDays()), 5);
    }

    @Override
    public List<RankInfo> selectD30Rank(SearchInfo search) {
        return dashboardMapper.selectD30Rank(search);
    }

    @Override
    public List<RankInfo> selectD30TRank(SearchInfo search) {
        return dashboardMapper.selectD30TRank(search);
    }

    @Override
    public List<RankInfo> selectTPLHRank(SearchInfo search) {
        return dashboardMapper.selectTPLHRank(search);
    }

    @Override
    public List<RankInfo> selectQTRank(SearchInfo search) {
        return dashboardMapper.selectQTRank(search);
    }

    @Override
    public List<RankInfo> selectTCRank(SearchInfo search) {
        return dashboardMapper.selectTCRank(search);
    }

    @Override
    public List<RankInfo> selectOrderStackRateRank(SearchInfo search) {
        return dashboardMapper.selectOrderStackRateRank(search);
    }

    @Override
    public List<RankInfo> selectD7Rank(SearchInfo search) {
        return dashboardMapper.selectD7Rank(search);
    }

    @Override
    public List<RankInfo> selectD14Rank(SearchInfo search) {
        return dashboardMapper.selectD14Rank(search);
    }

    @Override
    public List<RankInfo> selectD16Rank(SearchInfo search) {
        return dashboardMapper.selectD16Rank(search);
    }

    /**
     * 22-01-23
     * 배달원 서비스 만족도 랭킹 (별점 평균)
     * */
    @Override
    public List<RankInfo> selectRiderServicePointRank(SearchInfo search) {
        return dashboardMapper.selectRiderServicePointRank(search);
    }

    /**
     * 22-01-23
     * 배달 속도 만족도 랭킹 (별점 평균)
     * */
    @Override
    public List<RankInfo> selectRiderSpeedPointRank(SearchInfo search) {
        return dashboardMapper.selectRiderSpeedPointRank(search);
    }

    @Override
    public List<TimeSectionInfo> selectTimeSection(SearchInfo search) {
        return dashboardMapper.selectTimeSectionList(search);
    }

    private String changeValueType(Type type, String value){
        String strReturn = "";

        try{
            if (type.equals(Integer.class)){
                Integer intValue = 0;

                try{
                    intValue = Integer.parseInt(value);
                }catch (NumberFormatException e){
                    Double doubleValue = 0d;

                    doubleValue = Double.parseDouble(changeValueType(Double.class, value));
                    intValue = doubleValue.intValue();

                }catch (NullPointerException e){
                    log.debug("changeType Data NullPoint = " + value);
                }catch (Exception e){
                    log.error(e.getMessage());
                }

                strReturn = intValue.toString();
            }else if (type.equals(Float.class)){
                Float floatValue = 0f;

                try{
                    floatValue = Float.parseFloat(value);
                }catch (NullPointerException e){
                    log.debug("changeType Data NullPoint = " + value);
                }catch (Exception e){
                    log.error(e.getMessage());
                }

                strReturn = floatValue.toString();
            }else if (type.equals(Double.class)){
                Double doubleValue = 0d;

                try{
                    doubleValue = Double.parseDouble(value);
                }catch (NullPointerException e){
                    log.debug("changeType Data NullPoint = " + value);
                }catch (Exception e){
                    log.error(e.getMessage());
                }
                strReturn = doubleValue.toString();
            }else if (type.equals(String.class)){
                if (value != null){
                    strReturn = value.toString();
                }
            }else if (type.equals(Long.class)){
                Long longValue = 0l;

                try{
                    longValue = Long.parseLong(value);
                }catch (NumberFormatException e){
                    Double doubleValue = 0d;

                    doubleValue = Double.parseDouble(changeValueType(Double.class, value));       // 반올림 적용

                    longValue = doubleValue.longValue();
                }catch (NullPointerException e){
                    log.debug("changeType Data NullPoint = " + value);
                }catch (Exception e){
                    log.error(e.getMessage());
                }

                strReturn = longValue.toString();

            }
        }catch (Exception e){
            log.error(e.getMessage());
        }

        return strReturn;
    }

    private ChartInfo makeBarChartInfo(List<DashboardInfo> dashboardInfo, int xValue, int yValue){
        ChartInfo result = new ChartInfo();

        result.setDetail(dashboardInfo);

        result.setChartType(0);                         // 막대그래프(퍼센트)

        // Y 좌표 정보
        result.setMinY(0);
        result.setMaxY(100);
        result.setIntervalY(11);

        // X 좌표 정보
        result.setMinX(dashboardInfo.stream().min((o1, o2) -> o1.getCreatedDatetime().compareToIgnoreCase(o2.getCreatedDatetime())).get().getCreatedDatetime());
        result.setMaxX(dashboardInfo.stream().max((o1, o2) -> o1.getCreatedDatetime().compareToIgnoreCase(o2.getCreatedDatetime())).get().getCreatedDatetime());
        result.setIntervalX(Math.floorDiv(xValue, 10) + 1);

        return  result;
    }

    private ChartInfo makeLineChartInfo(List<DashboardInfo> dashboardInfo, int xValue, int yValue){
        ChartInfo result = new ChartInfo();

        result.setDetail(dashboardInfo);

        result.setChartType(1);                         // 라인그래프

        // Y 좌표 정보
        float minY = 0;
        float maxY = (dashboardInfo.stream().max(Comparator.comparing(DashboardInfo::getMainValue)).get().getMainValue());

        maxY = (int)maxY;

        double dLen = String.valueOf((int)maxY).length();
        double dTen = Math.pow(10, (dLen - 1));
        double dPV = Math.floorDiv((int)maxY, (int)dTen);


        maxY = (float)((dPV + 2) * dTen);

        result.setMinY(minY);
        result.setMaxY(maxY);
        result.setIntervalY((float) (dPV + 3));


        // X 좌표 정보
        result.setMinX(dashboardInfo.stream().min((o1, o2) -> o1.getCreatedDatetime().compareToIgnoreCase(o2.getCreatedDatetime())).get().getCreatedDatetime());
        result.setMaxX(dashboardInfo.stream().max((o1, o2) -> o1.getCreatedDatetime().compareToIgnoreCase(o2.getCreatedDatetime())).get().getCreatedDatetime());
        result.setIntervalX(Math.floorDiv(xValue, 10) + 1);

        return  result;
    }

    // 도넛 모양의 Chart
    // x좌표는 라벨, y좌표는 결과값
    private ChartInfo makeDoughnutChartInfo(List<DashboardInfo> dashboardInfo, int xValue, int yValue) {
        ChartInfo result = new ChartInfo();

        result.setDetail(dashboardInfo);

        result.setChartType(2);                         // 도넛 그래프

        // Y 좌표 정보
        float minY = 0;
        float maxY = (dashboardInfo.stream().max(Comparator.comparing(DashboardInfo::getMainValue)).get().getMainValue());

        maxY = (int)maxY;

        double dLen = String.valueOf((int)maxY).length();
        double dTen = Math.pow(10, (dLen - 1));
        double dPV = Math.floorDiv((int)maxY, (int)dTen);


        maxY = (float)((dPV + 2) * dTen);

        result.setMinY(minY);
        result.setMaxY(maxY);
        result.setIntervalY((float) (dPV + 3));

        result.setMinX(dashboardInfo.stream().min((o1, o2) -> o1.getCreatedDatetime().compareToIgnoreCase(o2.getCreatedDatetime())).get().getCreatedDatetime());
        result.setMaxX(dashboardInfo.stream().max((o1, o2) -> o1.getCreatedDatetime().compareToIgnoreCase(o2.getCreatedDatetime())).get().getCreatedDatetime());
        result.setIntervalX(Math.floorDiv(xValue, 10) + 1);

        return result;
    }
}
