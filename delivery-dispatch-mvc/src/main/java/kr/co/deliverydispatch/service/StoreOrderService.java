package kr.co.deliverydispatch.service;

import kr.co.cntt.core.exception.AppTrException;
import kr.co.cntt.core.model.order.Order;

import java.util.List;

public interface StoreOrderService {

    /**
     * <p> getOrders
     *
     * @param order
     * @return
     * @throws AppTrException
     */
    public List<Order> getOrders(Order order);

}
