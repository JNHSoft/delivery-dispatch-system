package kr.co.cntt.core.service.api.impl;

import kr.co.cntt.core.enums.ErrorCodeEnum;
import kr.co.cntt.core.exception.AppTrException;
import kr.co.cntt.core.mapper.PaymentMapper;
import kr.co.cntt.core.model.common.Common;
import kr.co.cntt.core.model.payment.Payment;
import kr.co.cntt.core.service.ServiceSupport;
import kr.co.cntt.core.service.api.PaymentService;
import kr.co.cntt.core.service.api.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service("paymentService")
public class PaymentServiceImpl  extends ServiceSupport implements PaymentService {

    /**
     * RedisService
     */
    @Autowired
    private RedisService redisService;

    /**
     *  Payment DAO
     */
    private PaymentMapper paymentMapper;

    @Autowired
    public PaymentServiceImpl(PaymentMapper paymentMapper){
        this.paymentMapper = paymentMapper;
    }
    // 결제 정보 조회
    @Override
    public List<Payment> getPaymentInfo(Common common) throws AppTrException{
        // Role 확인
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication.getAuthorities().toString().equals("[ROLE_ADMIN]")) {

            common.setRole("ROLE_ADMIN");
        } else if (authentication.getAuthorities().toString().equals("[ROLE_STORE]")) {
            common.setRole("ROLE_STORE");
        } else if (authentication.getAuthorities().toString().equals("[ROLE_RIDER]")) {
            common.setRole("ROLE_RIDER");
        }

        List<Payment> P_Payment = paymentMapper.selectPaymentInfo(common);

        if (P_Payment.size() == 0) {
            throw new AppTrException(getMessage(ErrorCodeEnum.A0011), ErrorCodeEnum.A0011.name());
        }



        return P_Payment;
    }

    // 결제 정보 등록
    @Secured("ROLE_RIDER")
    @Override
    public int postPaymentInfo(Payment payment) throws AppTrException{
        int postPayment = paymentMapper.insertPaymentInfo(payment);

        List<Payment> S_Payment = paymentMapper.selectPaymentInfo(payment);

        if (postPayment != 0) {
            redisService.setPublisher("order_updated", "order_id:"+S_Payment.get(0).getOrderId()+", store_id:"+S_Payment.get(0).getStoreId());
        }

        return postPayment;
    }

    // 결제 정보 수정
    @Secured("ROLE_RIDER")
    @Override
    public int updatePaymentInfo(Payment payment) throws AppTrException{
        int updatePayment = paymentMapper.updatePaymentInfo(payment);

        List<Payment> S_Payment = paymentMapper.selectPaymentInfo(payment);

        if (updatePayment != 0) {
            redisService.setPublisher("order_updated", "order_id:"+S_Payment.get(0).getOrderId()+", store_id:"+S_Payment.get(0).getStoreId());
        }

        return updatePayment;
    }


}
