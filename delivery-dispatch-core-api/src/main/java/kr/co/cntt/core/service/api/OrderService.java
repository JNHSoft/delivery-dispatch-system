package kr.co.cntt.core.service.api;

import kr.co.cntt.core.exception.AppTrException;
import kr.co.cntt.core.model.order.Order;

public interface OrderService {

    /**
     * <p> insertOrder
     *
     * @param order
     * @return
     * @throws AppTrException
     */
    public int postOrder(Order order) throws AppTrException;

}
