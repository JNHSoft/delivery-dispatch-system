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
     *
     */
    @Transactional(readOnly=true)
    List<Map> selectSubgroupStoreList(Store subGroupStoreRel);

}
