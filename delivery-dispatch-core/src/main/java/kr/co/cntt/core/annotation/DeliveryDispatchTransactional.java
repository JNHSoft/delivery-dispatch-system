package kr.co.cntt.core.annotation;

import org.springframework.transaction.annotation.Transactional;

import java.lang.annotation.*;

/**
 * <p>kr.co.cntt.core.annotation
 * <p>DeliveryDispatchTransactional.java
 * <p>Delivery Dispatch
 * <p>νΈλμ­μ
 * @author Merlin
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Transactional(value="deliveryDispatchTransactionManager", rollbackFor=Exception.class)
public @interface DeliveryDispatchTransactional {
}
