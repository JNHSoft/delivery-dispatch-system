package kr.co.cntt.core.service.api;


import kr.co.cntt.core.exception.AppTrException;
import kr.co.cntt.core.model.store.Store;

import java.util.List;

/**
 * <per>
 * kr.co.cntt.core.service.api
 *    └─ StoreService.java
 * </per>
 * service impl 를 호출하기 위한 interface 임
 * @author Nick
 * @since  2018-01-26
 *
 */
public interface StoreService {
  /**
   * login_id 확인
   * @author Nick
   * @since  2018-01-26
   *
   */
    public String selectLoginStore(Store store);

   /**
    * token 값 확인
    * @author Nick
    * @since  2018-01-26
    *
    */
    public int selectStoreTokenCheck(Store store);

    /**
     * token 값 insert
     * @author Nick
     * @since  2018-01-26
     *
     */
    public int insertStoreSession(Store store);

  /**
   * store 정보 조회
   * @author Nick
   * @since  2018-01-26
   *
   */
    public List<Store> getStoreInfo(Store store) throws AppTrException;

    /**
     * store 정보 수정
     * @author Nick
     * @param store
     * @return
     */
    public int updateStoreInfo(Store store);

    /**
     *  <p> deleteStore
     *
     * @param store
     * @return
     */
    public int putStoreAssignmentStatus(Store store);

    /**
     *  <p> putThirdParty
     *
     * @param thirdParty
     * @return
     */
    public int putStoreThirdParty(Store store);
}
