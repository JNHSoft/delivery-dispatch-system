package kr.co.cntt.core.service.api;


import kr.co.cntt.core.exception.AppTrException;
import kr.co.cntt.core.model.store.Store;

import java.util.List;

public interface StoreService {
    /**
     * <p> selectLoginStore
     *
     * @return
     */
    public Store selectLoginStore(Store store);

    /**
     * <p> selectStoreTokenCheck
     *
     * @return
     */
    public int selectStoreTokenCheck(Store store);

    /**
     * <p> insertStoreSession
     *
     * @return
     */
    public int insertStoreSession(Store store);

    /**
     * <p> selectStoreInfo
     *
     * @return
     */
    public List<Store> getStoreInfo(Store store) throws AppTrException;
}
