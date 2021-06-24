package kr.co.cntt.core.service.admin.impl;

import kr.co.cntt.core.mapper.DashboardMapper;
import kr.co.cntt.core.model.dashboard.ChartInfo;
import kr.co.cntt.core.model.dashboard.DashboardInfo;
import kr.co.cntt.core.model.dashboard.SearchInfo;
import kr.co.cntt.core.service.admin.DashboardAdminService;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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

        // 데이터에 대하여 파싱을 진행한다.
        System.out.println(resultMap);

        // 파싱할 데이터가 NULL 이면 오류가 발생하므로, 예외처리를 진행한다.
        if (resultMap != null){
            // D30 등록
            DashboardInfo infoD30 = new DashboardInfo();
            infoD30.setDashBoardType("D30");
            infoD30.setUnit("%");

            // D30 메인 Value 입력
            if (resultMap.containsKey("avgD30")){
                infoD30.setMainValue(Float.parseFloat(changeValueType(Float.class, resultMap.get("avgD30").toString())));
            }else{
                infoD30.setMainValue(0f);
            }
            
            // D30 평균 Value 입력
            if (resultMap.containsKey("detailD30")){
                infoD30.setAvgValue(Long.parseLong(changeValueType(Long.class, resultMap.get("detailD30").toString())));
            }else{
                infoD30.setAvgValue(0l);
            }


            // D7 등록
            DashboardInfo infoD7 = new DashboardInfo();
            infoD7.setDashBoardType("D7");
            infoD7.setUnit("%");

            // D7의 메인 Value 입력
            if (resultMap.containsKey("avgD7")){
                infoD7.setMainValue(Float.parseFloat(changeValueType(Float.class, resultMap.get("avgD7").toString())));
            }else{
                infoD7.setMainValue(0f);
            }

            // D7 의 평균 Value
            if (resultMap.containsKey("detailD7")){
                infoD7.setAvgValue(Long.parseLong(changeValueType(Long.class, resultMap.get("detailD7").toString())));
            }else{
                infoD7.setAvgValue(0l);
            }

            // TPLH 등록
            DashboardInfo infoTPLH = new DashboardInfo();
            infoTPLH.setDashBoardType("TPLH");
            infoTPLH.setUnit("");

            if (resultMap.containsKey("avgTPLH")){
                infoTPLH.setMainValue(Float.parseFloat(changeValueType(Float.class, resultMap.get("avgTPLH").toString())));
            }else{
                infoTPLH.setMainValue(0f);
            }

            // QT 등록
            DashboardInfo infoQT = new DashboardInfo();
            infoQT.setDashBoardType("QT");
            infoQT.setUnit("mins");

            if (resultMap.containsKey("avgQT")){
                infoQT.setMainValue(Float.parseFloat(changeValueType(Float.class, resultMap.get("avgQT").toString())));
            }else{
                infoQT.setMainValue(0f);
            }

            // TC 등록
            DashboardInfo infoTC = new DashboardInfo();
            infoTC.setDashBoardType("TC");
            infoTC.setUnit("");

            if (resultMap.containsKey("sumTC")){
                infoTC.setMainValue(Float.parseFloat(changeValueType(Float.class, resultMap.get("sumTC").toString())));
            }else{
                infoTC.setMainValue(0f);
            }

            // 정렬에 맞게 데이터를 넣어준다.
            resultList.add(infoD30);
            resultList.add(infoD7);
            resultList.add(infoTPLH);
            resultList.add(infoQT);
            resultList.add(infoTC);
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
        ChartInfo result = new ChartInfo();
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
        result.setIntervalY(yValue);

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
        result.setMinY(dashboardInfo.stream().min(Comparator.comparing(DashboardInfo::getMainValue)).get().getMainValue() - 2);
        result.setMaxY(dashboardInfo.stream().max(Comparator.comparing(DashboardInfo::getMainValue)).get().getMainValue() + 2);
        result.setIntervalY(yValue);

        // X 좌표 정보
        result.setMinX(dashboardInfo.stream().min((o1, o2) -> o1.getCreatedDatetime().compareToIgnoreCase(o2.getCreatedDatetime())).get().getCreatedDatetime());
        result.setMaxX(dashboardInfo.stream().max((o1, o2) -> o1.getCreatedDatetime().compareToIgnoreCase(o2.getCreatedDatetime())).get().getCreatedDatetime());
        result.setIntervalX(Math.floorDiv(xValue, 10) + 1);

        return  result;
    }

}
