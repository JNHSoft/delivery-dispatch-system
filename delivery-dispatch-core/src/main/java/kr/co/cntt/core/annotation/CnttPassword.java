package kr.co.cntt.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

import kr.co.cntt.core.validator.CnttPasswordValidator;

/**
 * <p>kr.co.cntt.core.annotation
 * <p>CnttPassword.java
 * <p>패스워드 규칙
 * <p>1. 최소 8자 ~ 최대 20자 이내로 입력합니다.
 * <p>2. 반드시 영문, 숫자, 특수문자가 각 1자리 이상 포함되어야 합니다.
 * <p>3. 3자리 이상 연속되는 숫자 또는 문자열은 사용할 수 없습니다. (예:123, 321, 012, abc, cba)
 * <p>4. 3자리 이상 동일한 숫자 또는 문자열은 사용할 수 없습니다. (예:000, 111, 222, ,aaa, bbb)
 * <p>5. 아이디와 연속한 3자리 이상 일치하는 비밀번호는 사용할 수 없습니다.
 * @author JIN
 */
@Constraint(validatedBy=CnttPasswordValidator.class)
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface CnttPassword {
    int min() default 8;
    int max() default 20;
    String message() default "비밀번호 길이 {min} ~ {max}, 영문, 숫자, 특수문자 의 조합으로 입력 해주세요.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
    String idField();
    String passwordField();
}
