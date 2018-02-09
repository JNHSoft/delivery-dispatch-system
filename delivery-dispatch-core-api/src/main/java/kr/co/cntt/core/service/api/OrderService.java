package kr.co.cntt.core.service.api;

import kr.co.cntt.core.exception.AppTrException;
import kr.co.cntt.core.model.common.Common;
import kr.co.cntt.core.model.order.Order;

import java.util.List;

public interface OrderService {

    /**
     * <p> insertOrder
     *
     * @param order
     * @return
     * @throws AppTrException
     */
    public int postOrder(Order order) throws AppTrException;

    /**
     * <p> getOrders
     *
     * @param common
     * @return
     * @throws AppTrException
     */
    public List<Order> getOrders(Common common) throws AppTrException;

    /**
     * <p> getOrderInfo
     *
     * @param common
     * @return
     * @throws AppTrException
     */
    public List<Order> getOrderInfo(Common common) throws AppTrException;

    /**
     * <p> putOrderInfo
     *
     * @param order
     * @return
     * @throws AppTrException
     */
    public int putOrderInfo(Order order) throws AppTrException;

    /**
     * <p> putOrderAssigned
     *
     * @param order
     * @return
     * @throws AppTrException
     */
    public int putOrderAssigned(Order order) throws AppTrException;

    /**
     * <p> putOrderPickedUp
     *
     * @param order
     * @return
     * @throws AppTrException
     */
    public int putOrderPickedUp(Order order) throws AppTrException;

    /**
     * <p> putOrderCompleted
     *
     * @param order
     * @return
     * @throws AppTrException
     */
    public int putOrderCompleted(Order order) throws AppTrException;
}
