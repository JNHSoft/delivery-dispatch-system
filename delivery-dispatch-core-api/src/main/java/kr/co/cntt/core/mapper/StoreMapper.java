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
     * login id 확인
     * @param store
     * @return
     */
    public String selectLoginStore(Store store);

    /**
     * token 값 확인
     * @param store
     * @return
     */
    public int selectStoreTokenCheck(Store store);

    /**
     * insert token
     * @param store
     * @return
     */
    public int insertStoreSession(Store store);


    /**
     * store 정보 조회
     * @param store
     * @return
     */
    public List<Store> getStoreInfo(Store store);


}
