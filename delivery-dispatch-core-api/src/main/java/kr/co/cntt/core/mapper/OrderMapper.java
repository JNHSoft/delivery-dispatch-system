package kr.co.cntt.core.mapper;

import kr.co.cntt.core.annotation.DeliveryDispatchMapper;
import kr.co.cntt.core.model.common.Common;
import kr.co.cntt.core.model.order.Order;
import kr.co.cntt.core.model.order.OrderCheckAssignment;
import kr.co.cntt.core.model.rider.Rider;

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

    /**
     * <p> Order 정보 조회
     *
     * @param common
     * @return
     */
    public List<Order> selectOrderInfo(Common common);

    /**
     * <p> Order 수정
     *
     * @param order
     * @return
     */
    public int updateOrder(Order order);

    /**
     * <p> 카드 결제 승인 완료 체크
     *
     * @param order
     * @return
     */
    public int selectOrderIsApprovalCompleted(Order order);

    /**
     * <p> 주문 완료, 취소 체크
     *
     * @param order
     * @return
     */
    public int selectOrderIsCompletedIsCanceled(Order order);

    /**
     * <p> Order 배정 확인
     *
     * @param order
     * @return
     */
    public int insertOrderConfirm(Order order);

    /**
     * <p> Order 배정 거부
     *
     * @param order
     * @return
     */
    public int insertOrderDeny(Order order);

    /**
     * <p> Order 배정 확인 조회
     *
     * @param order
     * @return
     */
    public List<OrderCheckAssignment> selectOrderConfirm(Order order);

    /**
     * <p> Order 배정 거부 조회
     *
     * @param order
     * @return
     */
    public List<OrderCheckAssignment> selectOrderDeny(Order order);

    /**
     * <p> 무반응 거부 count 조회
     *
     * @param rider
     * @return
     */
    public int selectOrderDenyCount(Rider rider);

}
