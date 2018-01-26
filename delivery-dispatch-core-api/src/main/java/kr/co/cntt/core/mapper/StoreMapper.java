package kr.co.cntt.core.mapper;

import kr.co.cntt.core.annotation.DeliveryDispatchMapper;
import kr.co.cntt.core.model.store.Store;

import java.util.List;

/**
 * <p> kr.co.cntt.core.mapper </p>
 * <p> StoreMapper.java </p>
 * <p> Store 관련 </p>
 *
 * @author Aiden
 * @see DeliveryDispatchMapper
 */
@DeliveryDispatchMapper
public interface StoreMapper {

    /**
     * <p> selectLoginStore
     *
     * @return loginId String
     */
    public Store selectLoginStore(Store store);

    /**
     * <p> selectStoreTokenCheck
     *
     * @return Count Int
     */
    public int selectStoreTokenCheck(Store store);

    /**
     * <p> Store Session Insert
     *
     * @return Insert 결과값
     */
    public int insertStoreSession(Store store);


    /**
     * <p> Store 정보 조회
     *
     * @return Store Info 조회 결과값
     */
    public List<Store> getStoreInfo(Store store);


}
