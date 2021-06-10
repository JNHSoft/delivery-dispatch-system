package kr.co.cntt.core.mapper;

import kr.co.cntt.core.annotation.DeliveryDispatchMapper;
import kr.co.cntt.core.model.common.Common;
import kr.co.cntt.core.model.group.SubGroup;
import kr.co.cntt.core.model.order.Order;
import kr.co.cntt.core.model.order.OrderCheckAssignment;
import kr.co.cntt.core.model.reason.Reason;
import kr.co.cntt.core.model.rider.Rider;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

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
    int insertOrder(Order order);

    /**
     * <p> Orders 목록 조회
     *
     * @param order
     * @return
     */
    @Transactional(readOnly=true)
    List<Order> selectOrders(Order order);

    /**
     * <p> Order 정보 조회
     * 혹시라도 백업 서버에 적용이 안된 경우 중복 유무를 체크할 방법이 없으므로, readOnly는 제외한다.
     * @param common
     * @return
     */
    Order selectOrderInfo(Common common);

    /**
     * <p> Order 수정
     *
     * @param order
     * @return
     */
    int updateOrder(Order order);

    /**
     * <p> 카드 결제 승인 완료 체크
     *
     * @param order
     * @return
     */
    @Transactional(readOnly=true)
    int selectOrderIsApprovalCompleted(Order order);

    /**
     * <p> 주문 완료, 취소 체크
     *
     * @param order
     * @return
     */
    @Transactional(readOnly=true)
    int selectOrderIsCompletedIsCanceled(Order order);

    /**
     * <p> Order 배정 확인
     *
     * @param order
     * @return
     */
    int insertOrderConfirm(Order order);

    /**
     * <p> Order 배정 거부
     *
     * @param order
     * @return
     */
    int insertOrderDeny(Order order);

    /**
     * <p> Order 배정 확인 조회
     *
     * @param order
     * @return
     */
    @Transactional(readOnly=true)
    List<OrderCheckAssignment> selectOrderConfirm(Order order);

    /**
     * <p> Order 배정 거부 조회
     *
     * @param order
     * @return
     */
    @Transactional(readOnly=true)
    List<OrderCheckAssignment> selectOrderDeny(Order order);

    /**
     * <p> 무반응 거부 count 조회
     *
     * @param rider
     * @return
     */
    @Transactional(readOnly=true)
    int selectOrderDenyCount(Rider rider);

    /**
     * <p> 자동 배정 관련 주문 목록
     * @return
     */
    @Transactional(readOnly=true)
    List<Order> selectForAssignOrders(Map locale);

    /**
     * 주문 위치 정보 조회
     * @param id
     * @return
     */
    @Transactional(readOnly=true)
    Order selectOrderLocation(String id);

    /**
     * <p> 우선 배정 사유 목록 조회
     *
     * @param common
     * @return
     */
    @Transactional(readOnly=true)
    List<Reason> selectOrderFirstAssignmentReason(Common common);

    @Transactional(readOnly=true)
    List<Map> selectPushToken(SubGroup subGroup);

    @Transactional(readOnly=true)
    List<Order> selectFooterOrders(Order order);

    /**
     * 예약 배정 푸시 관련
     * @return
     */
    @Transactional(readOnly=true)
    List<Order> selectReservationOrders();

    /**
     * Reg 오더 중복 체크
     * @param order
     * @return
     */
    int selectRegOrderIdCheck(Order order);

    /**
     * 라이더가 받을 수 있는 주문 개수
     * @param order
     * @return
     */
    @Transactional(readOnly=true)
    int selectCountOderAdmit(Order order);


    /**
     * <p> 배정 서드파티 업데이트
     *
     * @param order
     * @return
     */
    int updateOrderThirdParty(Order order);


    /**
     * <p> 서드파티  오더 상태확인
     *
     * @param order
     * @return
     */
    @Transactional(readOnly=true)
    int selectOrderIsThirdPartyStatus(Order order);

    /**
     * <p> 배정할 주문의 목적지와 가까운 주문을 가진 Rider 정보를 가져온다
     * */
    @Transactional(readOnly=true)
    List<Order> selectNearOrderRider(Map searchMap);

}
