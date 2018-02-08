package kr.co.cntt.core.mapper;

import kr.co.cntt.core.annotation.DeliveryDispatchMapper;
import kr.co.cntt.core.model.common.Common;
import kr.co.cntt.core.model.order.Order;

import java.util.List;

/**
 * <p> kr.co.cntt.core.mapper </p>
 * <p> OrderMapper.java </p>
 * <p> Order 관련 </p>
 *
 * @author Aiden
 * @see DeliveryDispatchMapper
 */
@DeliveryDispatchMapper
public interface OrderMapper {

    /**
     * <p> Order Insert
     *
     * @param order
     * @return Insert 결과값
     */
    public int insertOrder(Order order);

    /**
     * <p> Orders 목록 조회
     *
     * @param common
     * @return
     */
    public List<Order> selectOrders(Common common);

}
