package kr.co.deliverydispatch.service;

import kr.co.cntt.core.model.chat.Chat;
import kr.co.cntt.core.model.common.Common;
import kr.co.cntt.core.model.order.Order;
import kr.co.cntt.core.model.rider.Rider;
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
     * Store 통계 조회
     * @param order
     * @return
     * @throws
     */
    public Order getStoreStatisticsInfo(Order order);
}
