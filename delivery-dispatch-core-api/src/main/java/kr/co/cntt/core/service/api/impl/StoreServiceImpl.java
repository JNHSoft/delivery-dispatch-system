package kr.co.cntt.core.service.api.impl;

import kr.co.cntt.core.enums.ErrorCodeEnum;
import kr.co.cntt.core.exception.AppTrException;
import kr.co.cntt.core.mapper.StoreMapper;
import kr.co.cntt.core.model.store.Store;
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
        // 이 조회
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
            throw new AppTrException(getMessage(ErrorCodeEnum.A0011), ErrorCodeEnum.A0011.name());
        }

        return S_Store;
    }

    // store 정보 수정
    @Override
    public int updateStoreInfo(Store store) {
        // token 값 선언
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

        return storeMapper.updateStoreInfo(store);
    }

}
