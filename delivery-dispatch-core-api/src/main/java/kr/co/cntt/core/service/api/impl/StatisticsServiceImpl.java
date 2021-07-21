package kr.co.cntt.core.service.api.impl;

import kr.co.cntt.core.enums.ErrorCodeEnum;
import kr.co.cntt.core.exception.AppTrException;
import kr.co.cntt.core.mapper.StatisticsMapper;
import kr.co.cntt.core.model.group.SubGroupStoreRel;
import kr.co.cntt.core.model.store.Store;
import kr.co.cntt.core.service.ServiceSupport;
import kr.co.cntt.core.service.api.StatisticsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service("statisticsService")
public class StatisticsServiceImpl extends ServiceSupport implements StatisticsService {

    StatisticsMapper statisticsMapper;

    @Autowired
    public StatisticsServiceImpl(StatisticsMapper statisticsMapper) {
        this.statisticsMapper = statisticsMapper;
    }

    /**
     * 쉐어 스토어
     * */
    @Secured({"ROLE_ADMIN", "ROLE_STORE"})
    @Override
    public Map getSubGroupStore(Store store) throws AppTrException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        switch (authentication.getAuthorities().toString()) {
            case "[ROLE_STORE]":
                store.setRole("ROLE_STORE");
                break;
            case "[ROLE_ADMIN]":
                store.setRole("ROLE_ADMIN");
                // 필수 필드가 없는 경우 필드 오류 전송
                if (store.getCode() == null || store.getCode().isEmpty()){
                    throw new AppTrException(getMessage(ErrorCodeEnum.E00040), ErrorCodeEnum.E00040.name());
                }
                break;
        }

        Map resultMap = new HashMap();
        List<Map> subgroupStoreList = statisticsMapper.selectSubgroupStoreList(store);

        resultMap.put("storeCount", subgroupStoreList.size());
        if (subgroupStoreList.size() > 0){
            resultMap.put("storeLists", subgroupStoreList);
        }

        return resultMap;
    }
}
