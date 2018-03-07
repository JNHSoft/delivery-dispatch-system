package kr.co.cntt.core.service.admin;

import kr.co.cntt.core.model.store.Store;

import java.util.List;

public interface StoreAdminService {
    /**
     * store 리스트 조회
     * @author Nick
     * @since  2018-03-07
     *
     */
    public List<Store> selectStoreList(Store store);
}
