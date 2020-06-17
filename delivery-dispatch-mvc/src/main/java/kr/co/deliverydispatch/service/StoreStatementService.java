package kr.co.deliverydispatch.service;

import kr.co.cntt.core.model.order.Order;
import kr.co.cntt.core.model.statistic.ByDate;
import kr.co.cntt.core.model.statistic.Interval;
import kr.co.cntt.core.model.statistic.IntervalAtTWKFC;
import kr.co.cntt.core.model.store.Store;

import java.util.List;
import java.util.Map;

public interface StoreStatementService {
    /**
     * <p> getStoreInfo
     *
     * @param store
     * @return
     */
    public Store getStoreInfo(Store store);

    /**
     * Store 통계 목록
     * @param order
     * @return
     * @throws
     */
    public List<Order> getStoreStatistics(Order order);

    /**
     * Store 주문별 통계 목록
     * @param order
     * @return
     * @throws
     */
    public List<Order> getStoreStatisticsByOrder(Order order);

    /**
     * Store 일자별 통계 목록
     * @param order
     * @return
     * @throws
     */
    public List<ByDate> getStoreStatisticsByDate(Order order);

    /**
     * Store 일자별 통계 목록 KFC용
     * @param order
     * @return
     * @throws
     */
    public List<ByDate> getStoreStatisticsByDateAtTWKFC(Order order);

    /**
     * Store 구간별 배달완료율 30분 미만 목록
     * @param order
     * @return
     */
    public List<Map> getStoreStatisticsMin30BelowByDate(Order order);

    /**
     * Store 구간별 배달완료율 30분 미만 목록 At TW KFC
     * @param order
     * @return
     */
    public List<Map> getStoreStatisticsMin30BelowByDateAtTWKFC(Order order);

    /**
     * Store 통계 조회
     * @param order
     * @return
     * @throws
     */
    public Order getStoreStatisticsInfo(Order order);

    /**
     * 통계 목록 Excel
     * @param order
     * @return
     */
    public List<Order> getStoreStatisticsExcel(Order order);

    /**
     * Store 구간별 통계 목록
     * @param order
     * @return
     */
    public Interval getStoreStatisticsByInterval(Order order);

    /**
     * Store 구간별 통계 목록 At TW KFC
     * @param order
     * @return
     */
    public IntervalAtTWKFC getStoreStatisticsByIntervalAtTWKFC(Order order);

    /**
     * 2020-06-16 주문 리스트 데이터 출력
     * */
    List<Order> getStoreOrderList(Order order);

}
