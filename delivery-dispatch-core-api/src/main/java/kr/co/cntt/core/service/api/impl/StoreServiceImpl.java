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
     */
    @Autowired
    public StoreServiceImpl(StoreMapper storeMapper) {
        this.storeMapper = storeMapper;
    }

    @Override
    public String selectLoginStore(Store store) {
        return storeMapper.selectLoginStore(store);
    }

    @Override
    public int selectStoreTokenCheck(Store store) {
        return storeMapper.selectStoreTokenCheck(store);
    }

    @Override
    public int insertStoreSession(Store store) {
        return storeMapper.insertStoreSession(store);
    }

    @Override
    public List<Store> getStoreInfo(Store store) throws AppTrException {

        List<Store> S_Store = storeMapper.getStoreInfo(store);

        if (S_Store.size() == 0) {
            throw new AppTrException(getMessage(ErrorCodeEnum.A0011), ErrorCodeEnum.A0011.name());
        }

        return S_Store;
    }

}
