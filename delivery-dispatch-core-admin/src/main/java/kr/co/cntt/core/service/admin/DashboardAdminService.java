package kr.co.cntt.core.service.admin;

import kr.co.cntt.core.model.dashboard.ChartInfo;
import kr.co.cntt.core.model.dashboard.DashboardInfo;
import kr.co.cntt.core.model.dashboard.SearchInfo;

import java.util.List;

public interface DashboardAdminService {

    /**
     * 메인에 표시된 모든 항목에 대한 정보를 출력하여 전달한다.
     * */
    List<DashboardInfo> selectAllDetail(SearchInfo search);

    /**
     * D30 데이터 조회
     * @param search 검색 조건
     * */
    ChartInfo selectD30Detail(SearchInfo search);
    
    /**
     * TPLH 데이터 조회
     * */
    ChartInfo selectTPLHDetail(SearchInfo search);

    /**
     * QT 데이터 조회
     * */
    ChartInfo selectQTDetail(SearchInfo search);

    /**
     * TC 데이터 조회
     * */
    ChartInfo selectTCDetail(SearchInfo search);
    
    /**
     * Order Stack Rate 조회
     * */
    ChartInfo selectOrderStackRateDetail(SearchInfo search);
    
    /**
     * D7 데이터 조회
     * */
    ChartInfo selectD7Detail(SearchInfo search);
}
