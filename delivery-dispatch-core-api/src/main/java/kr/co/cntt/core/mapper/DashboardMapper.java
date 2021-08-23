package kr.co.cntt.core.mapper;

import kr.co.cntt.core.annotation.DeliveryDispatchMapper;
import kr.co.cntt.core.model.common.SearchInfo;
import kr.co.cntt.core.model.dashboard.DashboardInfo;
import kr.co.cntt.core.model.dashboard.RankInfo;
import kr.co.cntt.core.model.dashboard.TimeSectionInfo;
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

    /**
     * D14 데이터 조회
     * */
    @Transactional(readOnly=true)
    List<DashboardInfo> selectD14Detail(SearchInfo search);

    /**
     * D16 데이터 조회 (16분 20초)
     * */
    @Transactional(readOnly=true)
    List<DashboardInfo> selectD16Detail(SearchInfo search);

    /**
     * D30 스토어 랭크 조회
     * */
    @Transactional(readOnly = true)
    List<RankInfo> selectD30Rank(SearchInfo search);

    /**
     * TPLH 스토어 랭크 조회
     * */
    @Transactional(readOnly = true)
    List<RankInfo> selectTPLHRank(SearchInfo search);

    /**
     * QT 스토어 랭크 조회
     * */
    @Transactional(readOnly = true)
    List<RankInfo> selectQTRank(SearchInfo search);

    /**
     * TC 스토어 랭크 조회
     * */
    @Transactional(readOnly = true)
    List<RankInfo> selectTCRank(SearchInfo search);

    /**
     * OrderStackRate 스토어 랭크 조회
     * */
    @Transactional(readOnly = true)
    List<RankInfo> selectOrderStackRateRank(SearchInfo search);

    /**
     * D7 스토어 랭크 조회
     * */
    @Transactional(readOnly = true)
    List<RankInfo> selectD7Rank(SearchInfo search);

    /**
     * D14 스토어 랭크 조회
     * */
    @Transactional(readOnly = true)
    List<RankInfo> selectD14Rank(SearchInfo search);

    /**
     * D16 스토어 랭크 조회 (16분 20초)
     * */
    @Transactional(readOnly = true)
    List<RankInfo> selectD16Rank(SearchInfo search);

    /**
     * 배정 ~ 도착 시간의 구간 개수 구하기
     * */
    @Transactional(readOnly = true)
    List<TimeSectionInfo> selectTimeSectionList(SearchInfo search);

}
