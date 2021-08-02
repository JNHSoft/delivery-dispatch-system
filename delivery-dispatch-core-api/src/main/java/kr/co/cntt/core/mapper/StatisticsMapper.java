package kr.co.cntt.core.mapper;

/**
 * 2021-07-19 통계 API를 위한 SQL Mapper
 * */

import kr.co.cntt.core.annotation.DeliveryDispatchMapper;
import kr.co.cntt.core.model.store.Store;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@DeliveryDispatchMapper
public interface StatisticsMapper {
    /**
     * Store를 이용하여 해당 스토어의 AC에 포함된 리스트 구하기
     */
    @Transactional(readOnly=true)
    List<Map> selectSubgroupStoreList(Store subGroupStoreRel);

    /**
     * 21.07.26 라이더 login ID를 이용하여 라이더 기본 정보 가져오기
     * */
    Map selectRiderInfoByLoginId(Map riderInfo);

    /**
     * 21.07.26 라이더 정보를 이용하여 라이더의 매장 정보를 가져온다.
     * */
    Map selectOrgStoreInfoByRider(Map riderInfo);

    /**
     * 21.07.26 라이더 정보를 이용하여 라이더의 출근 정보를 가져온다.
     * */
    List<Map> selectRiderWorkingInfo(Map riderInfo);

    /**
     * 21.07.26 라이더 정보를 이용하여 공유 변화 상태를 가져온다. (조회일 기준의 데이터들 모두)
     * */
    List<Map> selectRiderSharedInfoList(Map riderInfo);

    /**
     * 21.07.26 라이더 정보를 이용하여 공유 받은 매장의 주문들을 가져온다.
     * */
    List<Map> selectSharedOrderInfos(Map riderInfo);
}
