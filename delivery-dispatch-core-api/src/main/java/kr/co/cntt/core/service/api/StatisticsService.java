/**
 * 2021-07-19 통계 API 개발 의뢰로 통계 API 추가
 *
 * */
package kr.co.cntt.core.service.api;

import kr.co.cntt.core.exception.AppTrException;
import kr.co.cntt.core.model.common.SearchInfo;
import kr.co.cntt.core.model.store.Store;

import java.util.Map;

public interface StatisticsService {
    /**
     * 21-07-20 하위 그룹의 매장 리스트 가져오기
     * */
    Map getSubGroupStore(Store subGroupStoreRel) throws AppTrException;

    /**
     * 21-07-22 라이더 쉐어 관련 정보를 가져오기
     * */
    Map getRiderStatsInfos(SearchInfo searchInfo) throws AppTrException;

}
