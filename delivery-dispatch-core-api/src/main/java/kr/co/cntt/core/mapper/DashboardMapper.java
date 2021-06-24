package kr.co.cntt.core.mapper;

import kr.co.cntt.core.annotation.DeliveryDispatchMapper;
import kr.co.cntt.core.model.dashboard.DashboardInfo;
import kr.co.cntt.core.model.dashboard.SearchInfo;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * 대시보드 관련 내용 추가
 * */
@DeliveryDispatchMapper
public interface DashboardMapper {
    
    /**
     * DashBoard 메인 데이터
     * 모든 정보를 가져온 뒤에, 서비스 또는 컨트롤러 단에서 데이터를 커스터마이징을 진행한다.
     * */
    @Transactional(readOnly=true)
    Map selectAllDetail(SearchInfo search);

    /**
     * D30 데이터 조회
     * */
    @Transactional(readOnly=true)
    List<DashboardInfo> selectD30Detail(SearchInfo search);

    /**
     * TPLH 데이터 조회
     * */
    @Transactional(readOnly=true)
    List<DashboardInfo> selectTPLHDetail(SearchInfo search);
    
    /**
     * QT 데이터 조회
     * */
    @Transactional(readOnly=true)
    List<DashboardInfo> selectQTDetail(SearchInfo search);

    /**
     * TC 데이터 조회
     * */
    @Transactional(readOnly=true)
    List<DashboardInfo> selectTCDetail(SearchInfo search);
    
    /**
     * OrderStackRate 데이터 조회
     * */
    @Transactional(readOnly=true)
    List<DashboardInfo> selectOrderStackRateDetail(SearchInfo search);
    
    /**
     * D7 데이터 조회
     * */
    @Transactional(readOnly=true)
    List<DashboardInfo> selectD7Detail(SearchInfo search);
}
