package kr.co.deliverydispatch.service;

import kr.co.cntt.core.model.common.Common;
import kr.co.cntt.core.model.order.Order;
import kr.co.cntt.core.model.rider.Rider;
import kr.co.cntt.core.model.store.Store;

import java.util.List;

public interface StoreOrderService {

    /**
     * <p> getStoreInfo
     *
     * @param store
     * @return Store
     */
    public Store getStoreInfo(Store store);

    /**
     * <p> getOrders
     *
     * @param order
     * @return List<Order>
     */
    public List<Order> getOrders(Order order);

    /**
     * <p> getOrderInfo
     *
     * @param common
     * @return Order
     */
    public Order getOrderInfo(Common common);

    /**
     * <p> putOrderAssignedFirst
     *
     * @param order
     * @return int
     */
    public int putOrderAssignedFirst(Order order);

    /**
     * <p> putOrderAssigned
     *
     * @param order
     * @return int
     */
    public int putOrderAssigned(Order order);

    /**
     * <p> getSubgroupRiderRels
     *
     * @param common
     * @return List<Rider>
     */
    public List<Rider> getSubgroupRiderRels(Common common);

    /**
     * <p> putOrderInfo
     *
     * @param order
     * @return int
     */
    public int putOrderInfo(Order order);


    /**
     * <p> updateThirdParty 추가
     *
     * @param order
     * @return int
     */
    public int putOrderThirdParty(Order order);


    /**
     * <p> putOrderCanceled
     *
     * @param order
     * @return int
     */
    public int putOrderCanceled(Order order);

    /**
     * <p> putOrderAssignCanceled
     *
     * @param order
     * @return int
     */
    public int putOrderAssignCanceled(Order order);

    /**
     * <p> getFooterOrders
     *
     * @param order
     * @return List<Order>
     */
    public List<Order> getFooterOrders(Order order);

    /**
     * <p> getCountOderAdmit
     *
     * @param order
     * @return int
     */
    public int getCountOderAdmit(Order order);
}
