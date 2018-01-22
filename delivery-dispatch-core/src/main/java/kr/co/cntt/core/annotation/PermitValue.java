package kr.co.cntt.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

import kr.co.cntt.core.validator.PermitValueValidator;

/**
 * <p>kr.co.cntt.core.annotation
 * <p>PermitValue.java
 * <p>필드 허용 값 검증
 * @author JIN
 */
@Constraint(validatedBy=PermitValueValidator.class)
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface PermitValue {
	
	String message() default "허용된 값이 아닙니다.";
	
	Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
    
    String[] value() default {};
	
}
