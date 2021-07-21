/**
 * 2021-07-19 통계 API 개발 의뢰로 통계 API 추가
 *
 * */
package kr.co.cntt.core.service.api;

import kr.co.cntt.core.exception.AppTrException;
import kr.co.cntt.core.model.store.Store;

import java.util.Map;

public interface StatisticsService {
    Map getSubGroupStore(Store subGroupStoreRel) throws AppTrException;
}
