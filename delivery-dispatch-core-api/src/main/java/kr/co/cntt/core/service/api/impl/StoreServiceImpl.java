package kr.co.cntt.core.service.api.impl;

import kr.co.cntt.core.enums.ErrorCodeEnum;
import kr.co.cntt.core.exception.AppTrException;
import kr.co.cntt.core.mapper.StoreMapper;
import kr.co.cntt.core.model.store.Store;
import kr.co.cntt.core.service.ServiceSupport;
import kr.co.cntt.core.service.api.StoreService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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
    @Override
    public List<Store> getStoreInfo(Store store) throws AppTrException {
        // token 값 선언
        store.setAccessToken(store.getToken());

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
    public int updateStoreInfo(Store store){
        // token 값 선언
        store.setAccessToken(store.getToken());


        return storeMapper.updateStoreInfo(store);
    }




}
