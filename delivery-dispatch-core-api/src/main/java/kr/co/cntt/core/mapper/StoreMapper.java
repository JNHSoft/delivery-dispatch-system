package kr.co.cntt.core.mapper;

import kr.co.cntt.core.annotation.DeliveryDispatchMapper;
import kr.co.cntt.core.model.store.Store;
import kr.co.cntt.core.model.thirdParty.ThirdParty;

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
     *
     * @param store
     * @return
     */
    public String selectLoginStore(Store store);

    /**
     * token 값 확인
     *
     * @param store
     * @return
     */
    public int selectStoreTokenCheck(Store store);

    /**
     * insert token
     *
     * @param store
     * @return
     */
    public int insertStoreSession(Store store);


    /**
     * store 정보 조회
     *
     * @param store
     * @return
     */
    public List<Store> selectStoreInfo(Store store);

    /**
     * Store 정보 수정
     *
     * @param store
     * @return
     */
    public int updateStoreInfo(Store store);

    /**
     * <p> 상점 목록 조회
     *
     * @param store
     * @return
     */
    public List<Store> selectStores(Store store);

    /**
     * <p> 소그룹 상점 목록 조회
     *
     * @param store
     * @return
     */
    public List<Store> selectSubGroupStores(Store store);

    /**
     * <p> 배정 모드 설정
     *
     * @param store
     * @return
     */
    public int updateStoreAssignmentStatus(Store store);

    /**
     * <p> 배정 서드파티 설정
     *
     * @param store
     * @return
     */
    public int updateStoreThirdParty(Store store);

    /**
     * <p> 배정 서드파티 목록
     *
     * @param thirdParty
     * @return
     */
    public List<ThirdParty> selectThirdParty(ThirdParty thirdParty);

    /**
     * <p> 상점 위치 정보 조회
     *
     * @param storeId
     * @return
     */
    public Store selectStoreLocation(String storeId);

    /**
     * <p> 알림음 설정
     *
     * @param store
     * @return
     */
    public int updateStoreAlarm(Store store);
}
