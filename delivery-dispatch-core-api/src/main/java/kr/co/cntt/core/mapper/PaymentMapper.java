package kr.co.cntt.core.mapper;


import kr.co.cntt.core.annotation.DeliveryDispatchMapper;
import kr.co.cntt.core.model.common.Common;
import kr.co.cntt.core.model.payment.Payment;

import java.util.List;

/**
 * <per>
 * kr.co.cntt.core.mapper
 *    └─ PaymentMapper.java
 * </per>
 *  Payment 관련
 * @author Nick
 * @since  2018-02-09
 *
 */
@DeliveryDispatchMapper
public interface PaymentMapper {

    /**
     * Payment 결제 정보 조회
     * @param common
     * @return
     */
    public List<Payment> selectPaymentInfo(Common common);

    /**
     * Payment 정보 등록
     * @param payment
     * @return
     */
    public int insertPaymentInfo(Payment payment);

    /**
     * Payment 정보 수정
     * @param payment
     * @return
     */
    public int updatePaymentInfo(Payment payment);

}
