package kr.co.cntt.core.mapper;

import kr.co.cntt.core.annotation.DeliveryDispatchMapper;
import kr.co.cntt.core.model.admin.Admin;
import kr.co.cntt.core.model.alarm.Alarm;
import kr.co.cntt.core.model.common.Common;
import kr.co.cntt.core.model.login.User;
import kr.co.cntt.core.model.order.Order;
import kr.co.cntt.core.model.statistic.ByDate;
import kr.co.cntt.core.model.store.Store;
import kr.co.cntt.core.model.thirdParty.ThirdParty;

import java.util.List;
import java.util.Map;

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
     * <p> loginStoreInfo
     *
     * @return loginId String
     */
    public Store loginStoreInfo(Store admin);
    /**
     * login id 확인
     *
     * @param store
     * @return
     */
    public Map selectLoginStore(Store store);

    /**
     * token 값 확인
     *
     * @param store
     * @return
     */
    public int selectStoreTokenCheck(Store store);

    /**
     * token 값 확인
     *
     * @param store
     * @return
     */
    public User selectStoreTokenLoginCheck(Store store);

    /**
     * insert token
     *
     * @param store
     * @return
     */
    public int insertStoreSession(Store store);

    /**
     * insert Admin token
     *
     * @param store
     * @return
     */
    public int insertAdminStoreSession(Store store);



    /**
     * store 토큰 만료일, last_access update
     *
     * @param token
     * @return
     */
    public int updateStoreSession(String token);

    /**
     * store 정보 조회
     *
     * @param common
     * @return
     */
    public Store selectStoreInfo(Common common);

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
     * @param common
     * @return
     */
    public List<ThirdParty> selectThirdParty(Common common);

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

    /**
     * <p> 알림음 목록
     *
     * @param store
     * @return
     */
    public List<Alarm> selectAlarm(Store store);

    /**
     * Store 통계 목록
     * @param order
     * @return
     */
    public List<Order> selectStoreStatistics(Order order);

    /**
     * Store 통계 목록
     * @param order
     * @return
     */
    public List<Order> selectStoreStatisticsByOrder(Order order);

    /**
     * 통계 조회
     * @param order
     * @return
     */
    public Order selectStoreStatisticsInfo(Order order);

    /**
     * 통계 목록 Excel
     * @param order
     * @return
     */
    public List<Order> selectStoreStatisticsExcel(Order order);

    /**
     * Store 비밀번호 초기화
     *
     * @param store
     * @return
     */
    public int resetStorePassword(Store store);

    /**
     * Store 구간별 통계 목록
     * @param order
     * @return
     */
    public List<Integer> selectStoreStatisticsByInterval(Order order);

    /**
     * Store 구간별 통계 목록 At TW KFC
     * @param order
     * @return
     */
    public List<Map> selectStoreStatisticsByIntervalAtTWKFC(Order order);


    /**
     * Store 일별 통계 목록
     * @param order
     * @return
     */
    public List<ByDate> selectStoreStatisticsByDate(Order order);

    /**
     * Store 일별 통계 목록 At TW KFC
     * @param order
     * @return
     */
    public List<ByDate> selectStoreStatisticsByDateAtTWKFC(Order order);

    /**
     * Store 일별 배달완료율 30분 미만 목록
     * @param order
     * @return
     */
    public List<Map> selectStoreStatisticsMin30BelowByDate(Order order);


    /**
     * Store 일별 배달완료율 30분 미만 목록 At TW KFC
     * @param order
     * @return
     */
    public List<Map> selectStoreStatisticsMin30BelowByDateAtTWKFC(Order order);


    /**
     * <p> Admin 정보 조회
     *
     * @param common
     * @return Admin Info 조회 결과값
     */
    public Admin selectAdminInfo(Common common);

    List<Order> selectOrderListForStore(Order order);
}
