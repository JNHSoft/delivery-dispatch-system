package kr.co.cntt.core.service.api;

import kr.co.cntt.core.exception.AppTrException;
import kr.co.cntt.core.model.common.Common;
import kr.co.cntt.core.model.order.Order;
import kr.co.cntt.core.model.reason.Reason;

import java.util.List;

public interface OrderService {

    /**
     * <p> autoAssignOrder
     *
     */
    public void autoAssignOrder() throws AppTrException;

    /**
     * <p> reservationOrders
     */
    public void reservationOrders() throws AppTrException;

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
     * @param order
     * @return
     * @throws AppTrException
     */
    public List<Order> getOrders(Order order) throws AppTrException;

    /**
     * <p> getOrderInfo
     *
     * @param common
     * @return
     * @throws AppTrException
     */
    public Order getOrderInfo(Common common) throws AppTrException;

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
     * <p> putOrderArrived </p>
     *
     * @param order
     * @return
     * @throws AppTrException
     * */
    int putOrderArrived(Order order) throws AppTrException;

    /**
     * <p> putOrderCompleted
     *
     * @param order
     * @return
     * @throws AppTrException
     */
    public int putOrderCompleted(Order order) throws AppTrException;

    /**
     * <p> putOrderCanceled
     *
     * @param order
     * @return
     * @throws AppTrException
     */
    public int putOrderCanceled(Order order) throws AppTrException;

    /**
     * <p> putOrderAssignCanceled
     *
     * @param order
     * @return
     * @throws AppTrException
     */
    public int putOrderAssignCanceled(Order order) throws AppTrException;

    /**
     * <p> postOrderConfirm
     *
     * @param order
     * @return
     * @throws AppTrException
     */
    public int postOrderConfirm(Order order) throws AppTrException;

    /**
     * <p> postOrderDeny
     *
     * @param order
     * @return
     * @throws AppTrException
     */
    public int postOrderDeny(Order order) throws AppTrException;

    /**
     * <p> putOrderAssignedFirst
     *
     * @param order
     * @return
     * @throws AppTrException
     */
    public int putOrderAssignedFirst(Order order) throws AppTrException;

    /**
     * <p> getOrderFirstAssignmentReason
     *
     * @param common
     * @return
     * @throws AppTrException
     */
    public List<Reason> getOrderFirstAssignmentReason(Common common) throws AppTrException;

    /**
     * <p> putOrderReturn
     *
     * @param order
     * @return
     * @throws AppTrException
     */
    public int putOrderReturn(Order order) throws AppTrException;

}
