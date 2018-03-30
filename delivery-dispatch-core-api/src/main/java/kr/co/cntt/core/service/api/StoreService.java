package kr.co.cntt.core.service.api;


import kr.co.cntt.core.exception.AppTrException;
import kr.co.cntt.core.model.alarm.Alarm;
import kr.co.cntt.core.model.order.Order;
import kr.co.cntt.core.model.store.Store;
import kr.co.cntt.core.model.thirdParty.ThirdParty;

import java.util.List;
import java.util.Map;

import kr.co.cntt.core.model.login.User;

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
    public Map selectLoginStore(Store store);

   /**
    * token 값 확인
    * @author Nick
    * @since  2018-01-26
    *
    */
    public int selectStoreTokenCheck(Store store);

    /**
     * token 값 확인
     * @author Nick
     * @since  2018-01-26
     *
     */
    public User selectStoreTokenLoginCheck(Store store);


    /**
     * token 값 insert
     * @author Nick
     * @since  2018-01-26
     *
     */
    public int insertStoreSession(Store store);

    /**
     * <p> updateStoreSession
     *
     * @param token
     * @return
     */
    public int updateStoreSession(String token);

    /**
     * store 정보 조회
     * @author Nick
     * @since  2018-01-26
     *
     */
    public Store getStoreInfo(Store store) throws AppTrException;

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
     * @param store
     * @return
     */
    public int putStoreThirdParty(Store store);

    /**
     *  <p> getThirdParty
     *
     * @param thirdParty
     * @return
     */
    public List<ThirdParty> getThirdParty(ThirdParty thirdParty);

    /**
     *  <p> putStoreAlarm
     *
     * @param store
     * @return
     */
    public int putStoreAlarm(Store store);

    /**
     *  <p> getAlarm
     *
     * @param store
     * @return
     */
    public List<Alarm> getAlarm(Store store);

    /**
     * Store 통계 목록
     * @param order
     * @return
     * @throws AppTrException
     */
    public List<Order> getStoreStatistics(Order order) throws AppTrException;

    /**
     * Store 통계 조회
     * @param order
     * @return
     * @throws AppTrException
     */
    public Order getStoreStatisticsInfo(Order order) throws AppTrException;
}
