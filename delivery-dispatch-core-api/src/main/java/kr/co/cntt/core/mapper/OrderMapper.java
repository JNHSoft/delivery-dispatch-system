package kr.co.cntt.core.mapper;

import kr.co.cntt.core.annotation.DeliveryDispatchMapper;
import kr.co.cntt.core.model.common.Common;
import kr.co.cntt.core.model.group.SubGroup;
import kr.co.cntt.core.model.order.Order;
import kr.co.cntt.core.model.order.OrderCheckAssignment;
import kr.co.cntt.core.model.reason.Reason;
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
     * @param order
     * @return
     */
    public List<Order> selectOrders(Order order);

    /**
     * <p> Order 정보 조회
     *
     * @param common
     * @return
     */
    public Order selectOrderInfo(Common common);

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

    /**
     * <p> 자동 배정 관련 주문 목록
     * @return
     */
    public List<Order> selectForAssignOrders();

    /**
     * 주문 위치 정보 조회
     * @param id
     * @return
     */
    public Order selectOrderLocation(String id);

    /**
     * <p> 우선 배정 사유 목록 조회
     *
     * @param common
     * @return
     */
    public List<Reason> selectOrderFirstAssignmentReason(Common common);

    public List<String> selectPushToken(SubGroup subGroup);

    public List<Order> selectFooterOrders(Order order);

    /**
     * 예약 배정 푸시 관련
     * @return
     */
    public List<Order> selectReservationOrders();

    /**
     * Reg 오더 중복 체크
     * @param order
     * @return
     */
    public int selectRegOrderIdCheck(Order order);

    /**
     * 라이더가 받을 수 있는 주문 개수
     * @param order
     * @return
     */
    public int selectCountOderAdmit(Order order);


}
