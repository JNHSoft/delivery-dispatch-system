package kr.co.cntt.core.service.api.impl;

import kr.co.cntt.core.enums.ErrorCodeEnum;
import kr.co.cntt.core.exception.AppTrException;
import kr.co.cntt.core.mapper.StoreMapper;
import kr.co.cntt.core.model.store.Store;
import kr.co.cntt.core.model.thirdParty.ThirdParty;
import kr.co.cntt.core.service.ServiceSupport;
import kr.co.cntt.core.service.api.StoreService;
import kr.co.cntt.core.util.Misc;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service("storeService")
public class StoreServiceImpl extends ServiceSupport implements StoreService {

    /**
     * Store DAO
     */
    private StoreMapper storeMapper;

    /**
     * @param storeMapper USER D A O
     * @author Nick
     */
    @Autowired
    public StoreServiceImpl(StoreMapper storeMapper) {
        this.storeMapper = storeMapper;
    }

    // login_id 체크하는 함수
    @Override
    public String selectLoginStore(Store store) {
        return storeMapper.selectLoginStore(store);
    }

    // login_id 에 맞는 token 값 체크 하는 함수
    @Override
    public int selectStoreTokenCheck(Store store) {
        return storeMapper.selectStoreTokenCheck(store);
    }

    // token 값 insert 해주는 함수
    @Override
    public int insertStoreSession(Store store) {
        return storeMapper.insertStoreSession(store);
    }

    // store 정보 조회
    @Secured({"ROLE_ADMIN", "ROLE_STORE" , "ROLE_RIDER"})
    @Override
    public List<Store> getStoreInfo(Store store) throws AppTrException {
        // 권한
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        // store 가 조회
        if (authentication.getAuthorities().toString().equals("[ROLE_STORE]")) {
            // token 값 선언
            store.setAccessToken(store.getToken());
            store.setId("");
            store.setIsAdmin("");
        }
        // 권한이 없는 다른 user 가 조회
        else if (authentication.getAuthorities().toString().equals("[ROLE_USER]")) {
            store.setAccessToken(null);
            store.setId("");
            store.setIsAdmin("");
        }

        // log 확인
        log.info(">>> token: " + store.getAccessToken());
        log.info(">>> token: " + store.getToken());

        // 리스트
        List<Store> S_Store = storeMapper.getStoreInfo(store);

        if (S_Store.size() == 0) {
            throw new AppTrException(getMessage(ErrorCodeEnum.E00007), ErrorCodeEnum.E00007.name());
        }

        return S_Store;
    }

    // store 정보 수정
    @Secured({"ROLE_ADMIN", "ROLE_STORE"})
    @Override
    public int updateStoreInfo(Store store) {

        store.setAccessToken(store.getToken());

        Misc misc = new Misc();
        Map<String, Integer> storeHaversineMap = new HashMap<>();
        List<Store> C_Store = storeMapper.getStoreInfo(store);
        List<Store> S_Store = storeMapper.selectStores(store);

        for (Store cs : C_Store) {
            for (Store s : S_Store) {
                if (cs.getId().equals(s.getId())) {
                    continue;
                }

                try {
                    storeHaversineMap.put(s.getId(), misc.getHaversine(cs.getLatitude(), cs.getLongitude(), s.getLatitude(), s.getLongitude()));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        store.setStoreDistanceSort(StringUtils.arrayToDelimitedString(misc.sortByValue(storeHaversineMap).toArray(), ","));

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication.getAuthorities().toString().equals("[ROLE_STORE]")) {
            store.setId(null);
            store.setCode(null);
            store.setSubGroupStoreRel(null);
        }

        return storeMapper.updateStoreInfo(store);
    }

    // 배정 모드 설정
    @Secured({"ROLE_ADMIN", "ROLE_STORE"})
    @Override
    public int putStoreAssignmentStatus(Store store){return storeMapper.updateStoreAssignmentStatus(store); }

    //배정 서드파티 설정
    @Secured({"ROLE_ADMIN", "ROLE_STORE"})
    @Override
    public int putStoreThirdParty(Store store){return storeMapper.updateStoreThirdParty(store); }

    //배정 서드파티 목록
    @Secured({"ROLE_ADMIN", "ROLE_STORE"})
    @Override
    public List<ThirdParty> getThirdParty(ThirdParty thirdParty){
        return storeMapper.selectThirdParty(thirdParty);
    }
}
