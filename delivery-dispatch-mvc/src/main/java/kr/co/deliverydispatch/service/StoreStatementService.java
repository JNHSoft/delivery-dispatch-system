package kr.co.deliverydispatch.service;

import kr.co.cntt.core.model.order.Order;
import kr.co.cntt.core.model.statistic.ByDate;
import kr.co.cntt.core.model.statistic.Interval;
import kr.co.cntt.core.model.store.Store;

import java.util.List;

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

}
