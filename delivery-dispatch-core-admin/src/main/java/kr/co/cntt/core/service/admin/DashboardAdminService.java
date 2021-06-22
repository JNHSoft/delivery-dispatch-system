package kr.co.cntt.core.service.admin;

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
    DashboardInfo selectD30Detail(SearchInfo search);
    
    /**
     * TPLH 데이터 조회
     * */
    DashboardInfo selectTPLHDetail(SearchInfo search);

    /**
     * QT 데이터 조회
     * */
    DashboardInfo selectQTDetail(SearchInfo search);

    /**
     * TC 데이터 조회
     * */
    DashboardInfo selectTCDetail(SearchInfo search);
    
    /**
     * Order Stack Rate 조회
     * */
    DashboardInfo selectOrderStackRateDetail(SearchInfo search);
    
    /**
     * D7 데이터 조회
     * */
    DashboardInfo selectD7Detail(SearchInfo search);
}
