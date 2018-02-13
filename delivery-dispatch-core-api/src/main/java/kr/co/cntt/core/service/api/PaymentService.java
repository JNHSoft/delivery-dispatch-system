package kr.co.cntt.core.service.api;

import kr.co.cntt.core.exception.AppTrException;
import kr.co.cntt.core.model.common.Common;
import kr.co.cntt.core.model.payment.Payment;

import java.util.List;

public interface PaymentService {

    /**
     * Payment 조회
     * @param common
     * @return
     */
    public List<Payment> getPaymentInfo(Common common) throws AppTrException;

    /**
     * Payment 등록
     * @param payment
     * @return
     * @throws AppTrException
     */
    public int postPaymentInfo(Payment payment) throws AppTrException;

    /**
     * Payment 수정
     * @param payment
     * @return
     * @throws AppTrException
     */
    public int updatePaymentInfo(Payment payment) throws AppTrException;

}
