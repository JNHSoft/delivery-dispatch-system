package kr.co.cntt.core.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 마이바티스에서 Mapper Interface를 자동으로 생성하기 위해 스캔하는 기준으로 사용
 * 패키지 단위 스캔뿐아니라 인터페이스에 정의된 어노테이션 기준으로 스캔시킨다.
 * 콜센터용이다
 * @author su
 *
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DeliveryDispatchMapper {
}