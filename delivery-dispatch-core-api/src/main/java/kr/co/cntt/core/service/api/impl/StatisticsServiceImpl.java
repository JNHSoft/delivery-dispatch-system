package kr.co.cntt.core.service.api.impl;

import kr.co.cntt.core.enums.ErrorCodeEnum;
import kr.co.cntt.core.exception.AppTrException;
import kr.co.cntt.core.mapper.StatisticsMapper;
import kr.co.cntt.core.model.common.SearchInfo;
import kr.co.cntt.core.model.store.Store;
import kr.co.cntt.core.service.ServiceSupport;
import kr.co.cntt.core.service.api.StatisticsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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

    /**
     * 21-07-22 라이더의 쉐어 정보
     * */
    @Secured({"ROLE_ADMIN", "ROLE_STORE"})
    @Override
    public Map getRiderStatsInfos(SearchInfo searchInfo) throws AppTrException {

        System.out.println("###############################");
        System.out.println(searchInfo);

        // 필수 필드의 값이 들어왔는지 체크한다.
        if (searchInfo.getSDate() == null || searchInfo.getSDate().isEmpty() ||
            searchInfo.getEDate() == null || searchInfo.getEDate().isEmpty() ||
            searchInfo.getRiderIds() == null || searchInfo.getRiderIds().size() < 1){
            throw new AppTrException(getMessage(ErrorCodeEnum.E00040), ErrorCodeEnum.E00040.name());
        }

        // 본 프로세스 진행 시작
        Map resultMap = new HashMap();
        List<Map> listMap = new ArrayList<>();

        Map<String, Object> searchMap = new HashMap<>();

        searchMap.put("sDate", searchInfo.getSDate());
        searchMap.put("eDate", searchInfo.getEDate());

        // 조회 요청 라이더 수만큼 데이터 전달 시작
        for (String info : searchInfo.getRiderIds()
             ) {

            searchMap.put("loginId", info);

            // 라이더의 기본 정보 가져오기
            Map<String, Object> riderInfo = statisticsMapper.selectRiderInfoByLoginId(searchMap);

            // 기본 정보가 없다면 정보를 구할 수 없으므로, 제외한다.
            if (riderInfo != null){
                // 라이더가 소속된 스토어 정보를 가져온다.
                searchMap.put("storeId", riderInfo.get("storeId"));
                searchMap.put("adminId", riderInfo.get("adminId"));
                searchMap.put("id", riderInfo.get("id"));

                Map<String, Object> orgStoreInfo = statisticsMapper.selectOrgStoreInfoByRider(searchMap);

                // 라이더의 출퇴근 시간 정보를 가져온다.
                List<Map> workingTimes = statisticsMapper.selectRiderWorkingInfo(searchMap);

                // 라이더의 상태 변화 정보
                List<Map> changeSharedStatusInfos = statisticsMapper.selectRiderSharedInfoList(searchMap);

                // 라이더가 쉐어로 받은 주문에 대한 정보
                List<Map> sharedStoreOrderInfos = statisticsMapper.selectSharedOrderInfos(searchMap);

                riderInfo.remove("adminId");
                riderInfo.remove("storeId");
                riderInfo.remove("id");

                if (orgStoreInfo != null){
                    riderInfo.put("orgStoreInfo", orgStoreInfo);
                }

                if (workingTimes != null && workingTimes.size() > 0){
                    riderInfo.put("workingTimes", workingTimes);
                }

                if (changeSharedStatusInfos != null && changeSharedStatusInfos.size() > 0){
                    riderInfo.put("changeSharedStatusInfos", changeSharedStatusInfos);
                }

                if (sharedStoreOrderInfos != null && sharedStoreOrderInfos.size() > 0){
                    riderInfo.put("sharedStoreOrderInfos", sharedStoreOrderInfos);
                }

                listMap.add(riderInfo);
            }
        }


        resultMap.put("riderInfos", listMap);

        return resultMap;
    }
}
